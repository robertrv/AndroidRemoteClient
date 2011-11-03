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
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ServicesRunning.
 */
public class ServicesRunning implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The services. */
    List<AndroidService> services = new ArrayList<AndroidService>();

    /**
	 * Gets the services.
	 * 
	 * @return the services
	 */
    public final List<AndroidService> getServices() {
        return services;
    }

    /**
	 * Sets the services.
	 * 
	 * @param services
	 *            the new services
	 */
    public final void setServices(List<AndroidService> services) {
        this.services = services;
    }

    /**
	 * Adds the service.
	 * 
	 * @param s
	 *            the s
	 */
    public final void addService(AndroidService s) {
        services.add(s);
    }
}
