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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;
import com.android.ddmlib.SyncService.SyncResult;

/**
 * The Class AndroidDevice.
 */
public class AndroidDevice {

    /** The device. */
    IDevice device;

    /**
	 * Instantiates a new android device.
	 * 
	 * @param device
	 *            the device
	 */
    public AndroidDevice(IDevice device) {
        this.device = device;
    }

    /**
	 * Open url.
	 * 
	 * @param url
	 *            the url
	 */
    public void openUrl(String url) {
        executeCommand("am start " + url);
    }

    /**
	 * Execute command.
	 * 
	 * @param cmd
	 *            the cmd
	 * @return the string
	 */
    public String executeCommand(String cmd) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            device.executeShellCommand(cmd,
                    new OutputStreamShellOutputReceiver(bos));
            return new String(bos.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
	 * Push file.
	 * 
	 * @param localFrom
	 *            the local from
	 * @param remoteTo
	 *            the remote to
	 */
    public void pushFile(File localFrom, String remoteTo) {
        try {
            if (device.getSyncService() == null)
                throw new RuntimeException("SyncService is null, ADB crashed ?");

            SyncResult result = device.getSyncService().pushFile(
                    localFrom.getAbsolutePath(), remoteTo,
                    new NullSyncProgressMonitor());
            if (result.getCode() != 0)
                throw new RuntimeException("code = " + result.getCode()
                        + " message= " + result.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
	 * Pull file.
	 * 
	 * @param removeFrom
	 *            the remove from
	 * @param localTo
	 *            the local to
	 */
    public void pullFile(String removeFrom, File localTo) {
        // ugly hack to call the method without FileEntry
        try {
            if (device.getSyncService() == null)
                throw new RuntimeException("SyncService is null, ADB crashed ?");

            Method m = device
                    .getSyncService()
                    .getClass()
                    .getDeclaredMethod("doPullFile", String.class,
                            String.class, ISyncProgressMonitor.class);
            m.setAccessible(true);
            m.invoke(device.getSyncService(), removeFrom,
                    localTo.getAbsolutePath(),
                    SyncService.getNullProgressMonitor());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
	 * List.
	 * 
	 * @param path
	 *            the path
	 * @return the list
	 */
    public List<FileInfo> list(String path) {
        try {
            String s = executeCommand("ls -l " + path);
            String[] entries = s.split("\r\n");
            Vector<FileInfo> liste = new Vector<FileInfo>();
            for (int i = 0; i < entries.length; i++) {
                String[] data = entries[i].split(" ");
                if (data.length < 4)
                    continue;
                /*
                 * for(int j=0; j<data.length; j++) {
                 * System.out.println(j+" = "+data[j]); }
                 */
                String attribs = data[0];
                boolean directory = attribs.startsWith("d");
                String name = data[data.length - 1];

                FileInfo fi = new FileInfo();
                fi.attribs = attribs;
                fi.directory = directory;
                fi.name = name;
                fi.path = path;
                fi.device = this;

                liste.add(fi);
            }

            return liste;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
	 * Checks if is emulator.
	 * 
	 * @return true, if is emulator
	 */
    public boolean isEmulator() {
        return device.isEmulator();
    }

    /**
	 * Execute shell command.
	 * 
	 * @param cmd
	 *            the cmd
	 * @param bos
	 *            the bos
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
    public void executeShellCommand(String cmd, ByteArrayOutputStream bos)
            throws IOException {
        device.executeShellCommand(cmd,
                new OutputStreamShellOutputReceiver(bos));
    }

    /**
	 * Instalar app.
	 * 
	 * @param ruta
	 *            the ruta
	 */
    public void instalarApp(String ruta) {
        try {
            device.installPackage(ruta, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
	 * Gets the device.
	 * 
	 * @return the device
	 */
    public IDevice getDevice() {
        return device;
    }

    /**
	 * Sets the device.
	 * 
	 * @param device
	 *            the new device
	 */
    public void setDevice(IDevice device) {
        this.device = device;
    }

    /**
	 * Show properties.
	 */
    public void showProperties() {
        Map<String, String> properties = device.getProperties();
        Set<String> keys = properties.keySet();
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
            String key = iterator.next();
            System.out.println(key + ": " + properties.get(key));
        }
    }
}
