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
//  Copyright (C) 2006 Constantin Kaplinsky.  All Rights Reserved.
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

import java.awt.*;
import java.io.*;

//
// VncCanvas2 is a special version of VncCanvas which may use Java 2 API.
//

/**
 * The Class VncCanvas2.
 */
public class VncCanvas2 extends VncCanvas {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new vnc canvas2.
	 * 
	 * @param v
	 *            the v
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public VncCanvas2(VncViewer v) throws IOException {
		super(v);
		disableFocusTraversalKeys();
	}

	/**
	 * Instantiates a new vnc canvas2.
	 * 
	 * @param v
	 *            the v
	 * @param maxWidth_
	 *            the max width_
	 * @param maxHeight_
	 *            the max height_
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public VncCanvas2(VncViewer v, int maxWidth_, int maxHeight_) throws IOException {

		super(v, maxWidth_, maxHeight_);
		disableFocusTraversalKeys();
	}

	/* (non-Javadoc)
	 * @see org.uoc.androidremote.client.vnc.VncCanvas#paintScaledFrameBuffer(java.awt.Graphics)
	 */
	public void paintScaledFrameBuffer(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.drawImage(memImage, 0, 0, scaledWidth, scaledHeight, null);
	}

	//
	// Try to disable focus traversal keys (JVMs 1.4 and higher).
	//

	/**
	 * Disable focus traversal keys.
	 */
	private void disableFocusTraversalKeys() {
		try {
			Class[] argClasses = { Boolean.TYPE };
			java.lang.reflect.Method method = getClass().getMethod("setFocusTraversalKeysEnabled", argClasses);
			Object[] argObjects = { new Boolean(false) };
			method.invoke(this, argObjects);
		} catch (Exception e) {
		}
	}

}
