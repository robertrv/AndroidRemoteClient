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
// A ZlibInStream reads from a zlib.io.InputStream
//

/**
 * The Class ZlibInStream.
 */
public class ZlibInStream extends InStream {

  /** The Constant defaultBufSize. */
  static final int defaultBufSize = 16384;

  /**
	 * Instantiates a new zlib in stream.
	 * 
	 * @param bufSize_
	 *            the buf size_
	 */
  public ZlibInStream(int bufSize_) {
    bufSize = bufSize_;
    b = new byte[bufSize];
    ptr = end = ptrOffset = 0;
    inflater = new java.util.zip.Inflater();
  }

  /**
	 * Instantiates a new zlib in stream.
	 */
  public ZlibInStream() { this(defaultBufSize); }

  /**
	 * Sets the underlying.
	 * 
	 * @param is
	 *            the is
	 * @param bytesIn_
	 *            the bytes in_
	 */
  public void setUnderlying(InStream is, int bytesIn_) {
    underlying = is;
    bytesIn = bytesIn_;
    ptr = end = 0;
  }

  /**
	 * Reset.
	 * 
	 * @throws Exception
	 *             the exception
	 */
  public void reset() throws Exception {
    ptr = end = 0;
    if (underlying == null) return;

    while (bytesIn > 0) {
      decompress();
      end = 0; // throw away any data
    }
    underlying = null;
  }

  /* (non-Javadoc)
   * @see org.uoc.androidremote.client.vnc.InStream#pos()
   */
  public int pos() { return ptrOffset + ptr; }

  /* (non-Javadoc)
   * @see org.uoc.androidremote.client.vnc.InStream#overrun(int, int)
   */
  protected int overrun(int itemSize, int nItems) throws Exception {
    if (itemSize > bufSize)
      throw new Exception("ZlibInStream overrun: max itemSize exceeded");
    if (underlying == null)
      throw new Exception("ZlibInStream overrun: no underlying stream");

    if (end - ptr != 0)
      System.arraycopy(b, ptr, b, 0, end - ptr);

    ptrOffset += ptr;
    end -= ptr;
    ptr = 0;

    while (end < itemSize) {
      decompress();
    }

    if (itemSize * nItems > end)
      nItems = end / itemSize;

    return nItems;
  }

  // decompress() calls the decompressor once.  Note that this won't
  // necessarily generate any output data - it may just consume some input
  // data.  Returns false if wait is false and we would block on the underlying
  // stream.

  /**
	 * Decompress.
	 * 
	 * @throws Exception
	 *             the exception
	 */
  private void decompress() throws Exception {
    try {
      underlying.check(1);
      int avail_in = underlying.getend() - underlying.getptr();
      if (avail_in > bytesIn)
        avail_in = bytesIn;

      if (inflater.needsInput()) {
        inflater.setInput(underlying.getbuf(), underlying.getptr(), avail_in);
      }

      int n = inflater.inflate(b, end, bufSize - end); 

      end += n;
      if (inflater.needsInput()) {
        bytesIn -= avail_in;
        underlying.setptr(underlying.getptr() + avail_in);
      }
    } catch (java.util.zip.DataFormatException e) {
      throw new Exception("ZlibInStream: inflate failed");
    }
  }

  /** The underlying. */
  private InStream underlying;
  
  /** The buf size. */
  private int bufSize;
  
  /** The ptr offset. */
  private int ptrOffset;
  
  /** The inflater. */
  private java.util.zip.Inflater inflater;
  
  /** The bytes in. */
  private int bytesIn;
}
