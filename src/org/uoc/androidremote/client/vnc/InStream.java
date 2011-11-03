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

// TODO: Auto-generated Javadoc
/* Copyright (C) 2002-2005 RealVNC Ltd.  All Rights Reserved.
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */

//
// rdr::InStream marshalls data from a buffer stored in RDR (RFB Data
// Representation).
//

/**
 * The Class InStream.
 */
abstract public class InStream {

	// check() ensures there is buffer data for at least one item of size
	// itemSize bytes. Returns the number of items in the buffer (up to a
	// maximum of nItems).

	/**
	 * Check.
	 * 
	 * @param itemSize
	 *            the item size
	 * @param nItems
	 *            the n items
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int check(int itemSize, int nItems) throws Exception {
		if (ptr + itemSize * nItems > end) {
			if (ptr + itemSize > end)
				return overrun(itemSize, nItems);

			nItems = (end - ptr) / itemSize;
		}
		return nItems;
	}

	/**
	 * Check.
	 * 
	 * @param itemSize
	 *            the item size
	 * @throws Exception
	 *             the exception
	 */
	public final void check(int itemSize) throws Exception {
		if (ptr + itemSize > end)
			overrun(itemSize, 1);
	}

	// readU/SN() methods read unsigned and signed N-bit integers.

	/**
	 * Read s8.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readS8() throws Exception {
		check(1);
		return b[ptr++];
	}

	/**
	 * Read s16.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readS16() throws Exception {
		check(2);
		int b0 = b[ptr++];
		int b1 = b[ptr++] & 0xff;
		return b0 << 8 | b1;
	}

	/**
	 * Read s32.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readS32() throws Exception {
		check(4);
		int b0 = b[ptr++];
		int b1 = b[ptr++] & 0xff;
		int b2 = b[ptr++] & 0xff;
		int b3 = b[ptr++] & 0xff;
		return b0 << 24 | b1 << 16 | b2 << 8 | b3;
	}

	/**
	 * Read u8.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readU8() throws Exception {
		return readS8() & 0xff;
	}

	/**
	 * Read u16.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readU16() throws Exception {
		return readS16() & 0xffff;
	}

	/**
	 * Read u32.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readU32() throws Exception {
		return readS32() & 0xffffffff;
	}

	// readString() reads a string - a U32 length followed by the data.

	/**
	 * Read string.
	 * 
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public final String readString() throws Exception {
		int len = readU32();
		if (len > maxStringLength)
			throw new Exception("InStream max string length exceeded");

		char[] str = new char[len];
		int i = 0;
		while (i < len) {
			int j = i + check(1, len - i);
			while (i < j) {
				str[i++] = (char) b[ptr++];
			}
		}

		return new String(str);
	}

	// maxStringLength protects against allocating a huge buffer. Set it
	// higher if you need longer strings.

	/** The max string length. */
	public static int maxStringLength = 65535;

	/**
	 * Skip.
	 * 
	 * @param bytes
	 *            the bytes
	 * @throws Exception
	 *             the exception
	 */
	public final void skip(int bytes) throws Exception {
		while (bytes > 0) {
			int n = check(1, bytes);
			ptr += n;
			bytes -= n;
		}
	}

	// readBytes() reads an exact number of bytes into an array at an offset.

	/**
	 * Read bytes.
	 * 
	 * @param data
	 *            the data
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @throws Exception
	 *             the exception
	 */
	public void readBytes(byte[] data, int offset, int length) throws Exception {
		int offsetEnd = offset + length;
		while (offset < offsetEnd) {
			int n = check(1, offsetEnd - offset);
			System.arraycopy(b, ptr, data, offset, n);
			ptr += n;
			offset += n;
		}
	}

	// readOpaqueN() reads a quantity "without byte-swapping". Because java has
	// no byte-ordering, we just use big-endian.

	/**
	 * Read opaque8.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readOpaque8() throws Exception {
		return readU8();
	}

	/**
	 * Read opaque16.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readOpaque16() throws Exception {
		return readU16();
	}

	/**
	 * Read opaque32.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readOpaque32() throws Exception {
		return readU32();
	}

	/**
	 * Read opaque24 a.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readOpaque24A() throws Exception {
		check(3);
		int b0 = b[ptr++];
		int b1 = b[ptr++];
		int b2 = b[ptr++];
		return b0 << 24 | b1 << 16 | b2 << 8;
	}

	/**
	 * Read opaque24 b.
	 * 
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public final int readOpaque24B() throws Exception {
		check(3);
		int b0 = b[ptr++];
		int b1 = b[ptr++];
		int b2 = b[ptr++];
		return b0 << 16 | b1 << 8 | b2;
	}

	// pos() returns the position in the stream.

	/**
	 * Pos.
	 * 
	 * @return the int
	 */
	abstract public int pos();

	// bytesAvailable() returns true if at least one byte can be read from the
	// stream without blocking. i.e. if false is returned then readU8() would
	// block.

	/**
	 * Bytes available.
	 * 
	 * @return true, if successful
	 */
	public boolean bytesAvailable() {
		return end != ptr;
	}

	// getbuf(), getptr(), getend() and setptr() are "dirty" methods which allow
	// you to manipulate the buffer directly. This is useful for a stream which
	// is a wrapper around an underlying stream.

	/**
	 * Gets the buf.
	 * 
	 * @return the buf
	 */
	public final byte[] getbuf() {
		return b;
	}

	/**
	 * Gets the ptr.
	 * 
	 * @return the ptr
	 */
	public final int getptr() {
		return ptr;
	}

	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public final int getend() {
		return end;
	}

	/**
	 * Sets the ptr.
	 * 
	 * @param p
	 *            the new ptr
	 */
	public final void setptr(int p) {
		ptr = p;
	}

	// overrun() is implemented by a derived class to cope with buffer overrun.
	// It ensures there are at least itemSize bytes of buffer data. Returns
	// the number of items in the buffer (up to a maximum of nItems). itemSize
	// is supposed to be "small" (a few bytes).

	/**
	 * Overrun.
	 * 
	 * @param itemSize
	 *            the item size
	 * @param nItems
	 *            the n items
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	abstract protected int overrun(int itemSize, int nItems) throws Exception;

	/**
	 * Instantiates a new in stream.
	 */
	protected InStream() {
	}

	/** The b. */
	protected byte[] b;
	
	/** The ptr. */
	protected int ptr;
	
	/** The end. */
	protected int end;
}
