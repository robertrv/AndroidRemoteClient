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
 * The Class AndroidApplication.
 */
public class AndroidApplication implements Serializable, Comparable<AndroidApplication> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Nombre del paquete. */
	private String packageName;

	/** Nombre de la aplicación. */
	private String applicationName;

	/**
	 * Constructor.
	 * 
	 * @param packageName
	 *            paquete
	 * @param activityName
	 *            aplicacioin
	 */
	public AndroidApplication(String packageName, String activityName) {
		super();
		this.packageName = packageName;
		this.applicationName = activityName;
	}

	/**
	 * Gets the package name.
	 * 
	 * @return paquete
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Sets the package name.
	 * 
	 * @param packageName
	 *            paqute
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Gets the application name.
	 * 
	 * @return aplicacion
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Sets the application name.
	 * 
	 * @param applicationName
	 *            aplicacion
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Compara dos objetos.
	 * 
	 * @param o
	 *            the o
	 * @return the int
	 */
	public int compareTo(AndroidApplication o) {
		AndroidApplication app = o;
		return this.applicationName.compareTo(app.applicationName);

	}

}
