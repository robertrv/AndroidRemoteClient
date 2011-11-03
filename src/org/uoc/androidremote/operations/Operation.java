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
 * The Class Operation.
 * 
 * @author angel
 */
public class Operation implements Serializable {

    /** The Constant OP_OPEN. */
    public static final int OP_OPEN = 0;
    
    /** The Constant OP_SERVICES_RUNNING. */
    public static final int OP_SERVICES_RUNNING = 1;
    
    /** The Constant OP_APPLICATIONS_RUNNING. */
    public static final int OP_APPLICATIONS_RUNNING = 2;
    
    /** The Constant OP_LOCATION_GPS. */
    public static final int OP_LOCATION_GPS = 3;
    
    /** The Constant OP_CLOSE. */
    public static final int OP_CLOSE = 4;
    
    /** The Constant OP_APPLICATIONS_INSTALLED. */
    public static final int OP_APPLICATIONS_INSTALLED = 5;
    
    /** The Constant OP_BATTERY_LEVEL. */
    public static final int OP_BATTERY_LEVEL = 6;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The id. */
    private int id;
    
    /** The message. */
    private String message;

    /**
	 * Instantiates a new operation.
	 * 
	 * @param id
	 *            the id
	 * @param msg
	 *            the msg
	 */
    public Operation(int id, String msg) {
        this.id = id;
        message = msg;
    }

    /**
	 * Gets the message.
	 * 
	 * @return the message
	 */
    public final String getMessage() {
        return message;
    }

    /**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
    public final void setMessage(String message) {
        this.message = message;
    }

    /**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    public final int getId() {
        return id;
    }

    /**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
    public final void setId(int id) {
        this.id = id;
    }

}
