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

import java.io.File;



// TODO: Auto-generated Javadoc
/**
 * The Class FileInfo.
 */
public class FileInfo {

	/** The device. */
	public AndroidDevice device;
	
	/** The path. */
	public String path;
	
	/** The attribs. */
	public String attribs;
	
	/** The directory. */
	public boolean directory;
	
	/** The name. */
	public String name;
	
	/**
	 * Download temporary.
	 * 
	 * @return the file
	 */
	public File downloadTemporary() {
		try {
			File tempFile = File.createTempFile("android", name);
			device.pullFile(path + name, tempFile);
			tempFile.deleteOnExit();
			return tempFile;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
	
}
