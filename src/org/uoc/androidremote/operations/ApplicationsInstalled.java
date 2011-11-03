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
 * The Class ApplicationsInstalled.
 * 
 * @author angel
 */
public class ApplicationsInstalled implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The list. */
    private List<AndroidApplication> list = new ArrayList<AndroidApplication>();

    /**
	 * Gets the list.
	 * 
	 * @return the list
	 */
    public final List<AndroidApplication> getList() {
        return list;
    }

    /**
	 * Sets the list.
	 * 
	 * @param list
	 *            the new list
	 */
    public final void setList(List<AndroidApplication> list) {
        this.list = list;
    }

    /**
	 * Adds the app.
	 * 
	 * @param app
	 *            the app
	 */
    public final void addApp(AndroidApplication app) {
        list.add(app);
    }

}
