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

import com.android.ddmlib.SyncService.ISyncProgressMonitor;

// TODO: Auto-generated Javadoc
/**
 * The Class NullSyncProgressMonitor.
 */
public class NullSyncProgressMonitor implements ISyncProgressMonitor {

	/* (non-Javadoc)
	 * @see com.android.ddmlib.SyncService.ISyncProgressMonitor#advance(int)
	 */
	public void advance(int arg0) {
	}

	/* (non-Javadoc)
	 * @see com.android.ddmlib.SyncService.ISyncProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.android.ddmlib.SyncService.ISyncProgressMonitor#start(int)
	 */
	public void start(int arg0) {
	}

	/* (non-Javadoc)
	 * @see com.android.ddmlib.SyncService.ISyncProgressMonitor#startSubTask(java.lang.String)
	 */
	public void startSubTask(String arg0) {
		
	}

	/* (non-Javadoc)
	 * @see com.android.ddmlib.SyncService.ISyncProgressMonitor#stop()
	 */
	public void stop() {
	}

}
