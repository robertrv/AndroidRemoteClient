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

package org.uoc.androidremote.operations;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class AndroidRunningApplication.
 * 
 * @author angel
 */
public class AndroidRunningApplication implements Serializable, Comparable<AndroidRunningApplication> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The name. */
	private String name;

	/** The importance. */
	private int importance;

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the importance.
	 * 
	 * @return the importance
	 */
	public final int getImportance() {
		return importance;
	}

	/**
	 * Sets the importance.
	 * 
	 * @param importance
	 *            the new importance
	 */
	public final void setImportance(int importance) {
		this.importance = importance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AndroidRunningApplication o) {
		AndroidRunningApplication app = o;
		if (this.importance != app.importance) {
			return this.importance - app.importance;
		} else {
			return this.name.compareTo(app.getName());
		}

	}
}
