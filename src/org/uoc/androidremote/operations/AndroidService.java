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
 * The Class AndroidService.
 */
public class AndroidService implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The pid. */
    private int pid;
    
    /** The process. */
    private String process;

    /**
	 * Gets the pid.
	 * 
	 * @return the pid
	 */
    public final int getPid() {
        return pid;
    }

    /**
	 * Sets the pid.
	 * 
	 * @param pid
	 *            the new pid
	 */
    public final void setPid(int pid) {
        this.pid = pid;
    }

    /**
	 * Gets the process.
	 * 
	 * @return the process
	 */
    public final String getProcess() {
        return process;
    }

    /**
	 * Sets the process.
	 * 
	 * @param process
	 *            the new process
	 */
    public final void setProcess(String process) {
        this.process = process;
    }

    /**
	 * Instantiates a new android service.
	 * 
	 * @param pid
	 *            the pid
	 * @param process
	 *            the process
	 */
    public AndroidService(int pid, String process) {
        super();
        this.pid = pid;
        this.process = process;
    }
    
    
}
