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
//  Copyright (C) 2002 Constantin Kaplinsky, Inc.  All Rights Reserved.
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
// HTTPConnectSocketFactory.java together with HTTPConnectSocket.java
// implement an alternate way to connect to VNC servers via one or two
// HTTP proxies supporting the HTTP CONNECT method.
//

import java.applet.*;
import java.net.*;
import java.io.*;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating HTTPConnectSocket objects.
 */
public class HTTPConnectSocketFactory implements SocketFactory {

	/* (non-Javadoc)
	 * @see org.uoc.androidremote.client.vnc.SocketFactory#createSocket(java.lang.String, int, java.applet.Applet)
	 */
	public Socket createSocket(String host, int port, Applet applet) throws IOException {

		return createSocket(host, port, applet.getParameter("PROXYHOST1"), applet.getParameter("PROXYPORT1"));
	}

	/* (non-Javadoc)
	 * @see org.uoc.androidremote.client.vnc.SocketFactory#createSocket(java.lang.String, int, java.lang.String[])
	 */
	public Socket createSocket(String host, int port, String[] args) throws IOException {

		return createSocket(host, port, readArg(args, "PROXYHOST1"), readArg(args, "PROXYPORT1"));
	}

	/**
	 * Creates a new HTTPConnectSocket object.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param proxyHost
	 *            the proxy host
	 * @param proxyPortStr
	 *            the proxy port str
	 * @return the socket
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Socket createSocket(String host, int port, String proxyHost, String proxyPortStr) throws IOException {

		int proxyPort = 0;
		if (proxyPortStr != null) {
			try {
				proxyPort = Integer.parseInt(proxyPortStr);
			} catch (NumberFormatException e) {
			}
		}

		if (proxyHost == null || proxyPort == 0) {
			System.out.println("Incomplete parameter list for HTTPConnectSocket");
			return new Socket(host, port);
		}

		System.out.println("HTTP CONNECT via proxy " + proxyHost + " port " + proxyPort);
		HTTPConnectSocket s = new HTTPConnectSocket(host, port, proxyHost, proxyPort);

		return (Socket) s;
	}

	/**
	 * Read arg.
	 * 
	 * @param args
	 *            the args
	 * @param name
	 *            the name
	 * @return the string
	 */
	private String readArg(String[] args, String name) {

		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equalsIgnoreCase(name)) {
				try {
					return args[i + 1];
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}
}
