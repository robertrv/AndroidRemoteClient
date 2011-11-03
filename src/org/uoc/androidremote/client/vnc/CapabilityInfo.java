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
//
//  Copyright (C) 2003 Constantin Kaplinsky.  All Rights Reserved.
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
// CapabilityInfo.java - A class to hold information about a
// particular capability as used in the RFB protocol 3.130.
//

/**
 * The Class CapabilityInfo.
 */
public class CapabilityInfo {

	// Public methods

	/**
	 * Instantiates a new capability info.
	 * 
	 * @param code
	 *            the code
	 * @param vendorSignature
	 *            the vendor signature
	 * @param nameSignature
	 *            the name signature
	 * @param description
	 *            the description
	 */
	public CapabilityInfo(int code, String vendorSignature, String nameSignature, String description) {
		this.code = code;
		this.vendorSignature = vendorSignature;
		this.nameSignature = nameSignature;
		this.description = description;
		enabled = false;
	}

	/**
	 * Instantiates a new capability info.
	 * 
	 * @param code
	 *            the code
	 * @param vendorSignature
	 *            the vendor signature
	 * @param nameSignature
	 *            the name signature
	 */
	public CapabilityInfo(int code, byte[] vendorSignature, byte[] nameSignature) {
		this.code = code;
		this.vendorSignature = new String(vendorSignature);
		this.nameSignature = new String(nameSignature);
		this.description = null;
		enabled = false;
	}

	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enable.
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Equals.
	 * 
	 * @param other
	 *            the other
	 * @return true, if successful
	 */
	public boolean equals(CapabilityInfo other) {
		return (other != null && this.code == other.code && this.vendorSignature.equals(other.vendorSignature) && this.nameSignature.equals(other.nameSignature));
	}

	/**
	 * Enable if equals.
	 * 
	 * @param other
	 *            the other
	 * @return true, if successful
	 */
	public boolean enableIfEquals(CapabilityInfo other) {
		if (this.equals(other))
			enable();

		return isEnabled();
	}

	// Protected data

	/** The code. */
	protected int code;
	
	/** The vendor signature. */
	protected String vendorSignature;
	
	/** The name signature. */
	protected String nameSignature;

	/** The description. */
	protected String description;
	
	/** The enabled. */
	protected boolean enabled;
}
