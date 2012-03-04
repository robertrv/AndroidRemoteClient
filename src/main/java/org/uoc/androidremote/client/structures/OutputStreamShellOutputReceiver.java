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
package org.uoc.androidremote.client.structures;

import java.io.IOException;
import java.io.OutputStream;

import com.android.ddmlib.IShellOutputReceiver;

// TODO: Auto-generated Javadoc
/**
 * The Class OutputStreamShellOutputReceiver.
 */
public class OutputStreamShellOutputReceiver implements IShellOutputReceiver {

	/** The os. */
	OutputStream os;
	
	/**
	 * Instantiates a new output stream shell output receiver.
	 * 
	 * @param os
	 *            the os
	 */
	public OutputStreamShellOutputReceiver(OutputStream os) {
		this.os = os;
	}
	
	/* (non-Javadoc)
	 * @see com.android.ddmlib.IShellOutputReceiver#isCancelled()
	 */
	public boolean isCancelled() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.android.ddmlib.IShellOutputReceiver#flush()
	 */
	public void flush() {
	}
	
	/* (non-Javadoc)
	 * @see com.android.ddmlib.IShellOutputReceiver#addOutput(byte[], int, int)
	 */
	public void addOutput(byte[] buf, int off, int len) {
		try {
			os.write(buf,off,len);
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
