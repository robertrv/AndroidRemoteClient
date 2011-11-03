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
//  Copyright (C) 2002 HorizonLive.com, Inc.  All Rights Reserved.
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
// SocketFactory.java describes an interface used to substitute the
// standard Socket class by its alternative implementations.
//

import java.applet.*;
import java.net.*;
import java.io.*;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Socket objects.
 */
public interface SocketFactory {

	/**
	 * Creates a new Socket object.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param applet
	 *            the applet
	 * @return the socket
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Socket createSocket(String host, int port, Applet applet) throws IOException;

	/**
	 * Creates a new Socket object.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param args
	 *            the args
	 * @return the socket
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Socket createSocket(String host, int port, String[] args) throws IOException;
}
