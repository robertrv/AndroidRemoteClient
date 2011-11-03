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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class Utilities.
 */
public class Utilities {

	/**
	 * Transfert.
	 * 
	 * @param is
	 *            the is
	 * @param os
	 *            the os
	 */
	public static void transfert(InputStream is, OutputStream os) {
		try {
			while (true) {
				int val = is.read();
				if (val <= -1)
					break;
				os.write(val);
			}
		} catch (IOException io) {
			throw new RuntimeException(io);
		}
	}

	/**
	 * Transfert resource.
	 * 
	 * @param c
	 *            the c
	 * @param resourceName
	 *            the resource name
	 * @param output
	 *            the output
	 */
	public static void transfertResource(Class c, String resourceName, File output) {
		InputStream resStream = c.getResourceAsStream(resourceName);
		if (resStream == null)
			throw new RuntimeException("Cannot find resource " + resourceName);
		try {
			FileOutputStream fos = new FileOutputStream(output);
			transfert(resStream, fos);
			fos.close();
			resStream.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
