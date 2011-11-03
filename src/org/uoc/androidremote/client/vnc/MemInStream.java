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

/**
 * The Class MemInStream.
 */
public class MemInStream extends InStream {

	/**
	 * Instantiates a new mem in stream.
	 * 
	 * @param data
	 *            the data
	 * @param offset
	 *            the offset
	 * @param len
	 *            the len
	 */
	public MemInStream(byte[] data, int offset, int len) {
		b = data;
		ptr = offset;
		end = offset + len;
	}

	/* (non-Javadoc)
	 * @see org.uoc.androidremote.client.vnc.InStream#pos()
	 */
	public int pos() {
		return ptr;
	}

	/* (non-Javadoc)
	 * @see org.uoc.androidremote.client.vnc.InStream#overrun(int, int)
	 */
	protected int overrun(int itemSize, int nItems) throws Exception {
		throw new Exception("MemInStream overrun: end of stream");
	}
}
