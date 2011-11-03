/*
 *  This file is part of Android Remote.
 *
 *  Android Remote is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Leeser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Android Remote is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Leeser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.uoc.androidremote.client.vnc;

//
//  Copyright (C) 2002 Constantin Kaplinsky.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

//
// SessionRecorder is a class to write FBS (FrameBuffer Stream) files.
// FBS files are used to save RFB sessions for later playback.
//

import java.io.*;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionRecorder.
 */
public class SessionRecorder {

	/** The f. */
	protected FileOutputStream f;
	
	/** The df. */
	protected DataOutputStream df;
	
	/** The last time offset. */
	protected long startTime, lastTimeOffset;

	/** The buffer. */
	protected byte[] buffer;
	
	/** The buffer size. */
	protected int bufferSize;
	
	/** The buffer bytes. */
	protected int bufferBytes;

	/**
	 * Instantiates a new session recorder.
	 * 
	 * @param name
	 *            the name
	 * @param bufsize
	 *            the bufsize
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SessionRecorder(String name, int bufsize) throws IOException {
		f = new FileOutputStream(name);
		df = new DataOutputStream(f);
		startTime = System.currentTimeMillis();
		lastTimeOffset = 0;

		bufferSize = bufsize;
		bufferBytes = 0;
		buffer = new byte[bufferSize];
	}

	/**
	 * Instantiates a new session recorder.
	 * 
	 * @param name
	 *            the name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SessionRecorder(String name) throws IOException {
		this(name, 65536);
	}

	//
	// Close the file, free resources.
	//

	/**
	 * Close.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException {
		try {
			flush();
		} catch (IOException e) {
		}

		df = null;
		f.close();
		f = null;
		buffer = null;
	}

	//
	// Write the FBS file header as defined in the rfbproxy utility.
	//

	/**
	 * Write header.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeHeader() throws IOException {
		df.write("FBS 001.000\n".getBytes());
	}

	//
	// Write one byte.
	//

	/**
	 * Write byte.
	 * 
	 * @param b
	 *            the b
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeByte(int b) throws IOException {
		prepareWriting();
		buffer[bufferBytes++] = (byte) b;
	}

	//
	// Write 16-bit value, big-endian.
	//

	/**
	 * Write short be.
	 * 
	 * @param v
	 *            the v
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeShortBE(int v) throws IOException {
		prepareWriting();
		buffer[bufferBytes++] = (byte) (v >> 8);
		buffer[bufferBytes++] = (byte) v;
	}

	//
	// Write 32-bit value, big-endian.
	//

	/**
	 * Write int be.
	 * 
	 * @param v
	 *            the v
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeIntBE(int v) throws IOException {
		prepareWriting();
		buffer[bufferBytes] = (byte) (v >> 24);
		buffer[bufferBytes + 1] = (byte) (v >> 16);
		buffer[bufferBytes + 2] = (byte) (v >> 8);
		buffer[bufferBytes + 3] = (byte) v;
		bufferBytes += 4;
	}

	//
	// Write 16-bit value, little-endian.
	//

	/**
	 * Write short le.
	 * 
	 * @param v
	 *            the v
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeShortLE(int v) throws IOException {
		prepareWriting();
		buffer[bufferBytes++] = (byte) v;
		buffer[bufferBytes++] = (byte) (v >> 8);
	}

	//
	// Write 32-bit value, little-endian.
	//

	/**
	 * Write int le.
	 * 
	 * @param v
	 *            the v
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeIntLE(int v) throws IOException {
		prepareWriting();
		buffer[bufferBytes] = (byte) v;
		buffer[bufferBytes + 1] = (byte) (v >> 8);
		buffer[bufferBytes + 2] = (byte) (v >> 16);
		buffer[bufferBytes + 3] = (byte) (v >> 24);
		bufferBytes += 4;
	}

	//
	// Write byte arrays.
	//

	/**
	 * Write.
	 * 
	 * @param b
	 *            the b
	 * @param off
	 *            the off
	 * @param len
	 *            the len
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void write(byte b[], int off, int len) throws IOException {
		prepareWriting();
		while (len > 0) {
			if (bufferBytes > bufferSize - 4)
				flush(false);

			int partLen;
			if (bufferBytes + len > bufferSize) {
				partLen = bufferSize - bufferBytes;
			} else {
				partLen = len;
			}
			System.arraycopy(b, off, buffer, bufferBytes, partLen);
			bufferBytes += partLen;
			off += partLen;
			len -= partLen;
		}
	}

	/**
	 * Write.
	 * 
	 * @param b
	 *            the b
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	//
	// Flush the output. This method saves buffered data in the
	// underlying file object adding data sizes and timestamps. If the
	// updateTimeOffset is set to false, then the current time offset
	// will not be changed for next write operation.
	//

	/**
	 * Flush.
	 * 
	 * @param updateTimeOffset
	 *            the update time offset
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void flush(boolean updateTimeOffset) throws IOException {
		if (bufferBytes > 0) {
			df.writeInt(bufferBytes);
			df.write(buffer, 0, (bufferBytes + 3) & 0x7FFFFFFC);
			df.writeInt((int) lastTimeOffset);
			bufferBytes = 0;
			if (updateTimeOffset)
				lastTimeOffset = -1;
		}
	}

	/**
	 * Flush.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void flush() throws IOException {
		flush(true);
	}

	//
	// Before writing any data, remember time offset and flush the
	// buffer before it becomes full.
	//

	/**
	 * Prepare writing.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void prepareWriting() throws IOException {
		if (lastTimeOffset == -1)
			lastTimeOffset = System.currentTimeMillis() - startTime;
		if (bufferBytes > bufferSize - 4)
			flush(false);
	}

}
