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
package org.uoc.androidremote.client.remote;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.uoc.androidremote.client.screen.ThreadGetScreen;
import org.uoc.androidremote.client.structures.AndroidDevice;
import org.uoc.androidremote.client.structures.OutputStreamShellOutputReceiver;
import org.uoc.androidremote.client.vnc.Utilities;

import com.android.ddmlib.IDevice;

// TODO: Auto-generated Javadoc
/**
 * The Class Conector.
 */
public class Conector {

    /** The Constant PORT. */
    private static final int PORT = 1324;
    
    /** The Constant FICHERO_SERVIDOR. */
    private static final String FICHERO_SERVIDOR = "/Server.jar";
    
    /** The Constant DESTINO_SERVIDOR. */
    private static final String DESTINO_SERVIDOR = "/data/local/tmp/Server.jar";
    
    /** The Constant LANZADOR. */
    private static final String LANZADOR = "org.uoc.androidremote.server.Launcher";
    
    /** The Constant DIRECCION_DISPOSITIVO. */
    private static final String DIRECCION_DISPOSITIVO = "127.0.0.1";
    
    /** The Constant COMANDO_SALIR. */
    private static final String COMANDO_SALIR = "quit\n";
    
    /** The dispositivo. */
    IDevice dispositivo;
    
    /** The s. */
    public static Socket s;
    
    /** The os. */
    OutputStream os;
    
    /** The t. */
    Thread t = new Thread("Agent Init") {
        public void run() {
            try {
                init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    /** The screencapture. */
    public ThreadGetScreen screencapture;

    /**
	 * Instantiates a new conector.
	 * 
	 * @param d
	 *            the d
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
    public Conector(IDevice d) throws IOException {
        this.dispositivo = d;
        this.screencapture = new ThreadGetScreen(d);
    }

    /**
	 * Start.
	 */
    public void start() {
        t.start();
    }

    /**
	 * Upload agent.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
    private void uploadAgent() throws IOException {
        try {
            File tempFile = File.createTempFile("agent", ".jar");
            Utilities.transfertResource(getClass(), FICHERO_SERVIDOR,
                    tempFile);
            new AndroidDevice(dispositivo).pushFile(tempFile,
                    DESTINO_SERVIDOR);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
	 * Kill running agent.
	 * 
	 * @return true if there was a client running
	 */
    private static boolean killRunningAgent() {
        try {
            Socket s = new Socket(DIRECCION_DISPOSITIVO, PORT);
            OutputStream os = s.getOutputStream();
            os.write(COMANDO_SALIR.getBytes());
            os.flush();
            os.close();
            s.close();
            return true;
        } catch (Exception ex) {
            // ignor�
        }
        return false;
    }

    /**
	 * Close.
	 */
    public void close() {
        try {
            if (os != null) {
                os.write(COMANDO_SALIR.getBytes());
                os.flush();
                os.close();
            }
            s.close();
        } catch (Exception ex) {
            // ignored
        }
        screencapture.interrupt();
        try {
            s.close();
        } catch (Exception ex) {
            // ignored
        }
        try {
            synchronized (dispositivo) {
                /*
                 * if(device != null) device.removeForward(PORT, PORT);
                 */
            }
        } catch (Exception ex) {
            // ignored
        }
    }

    /**
	 * Inits the.
	 * 
	 * @throws UnknownHostException
	 *             the unknown host exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
    private void init() throws UnknownHostException, IOException,
            InterruptedException {
        dispositivo.createForward(PORT, PORT);

        if (killRunningAgent()){
            System.out.println("Old client closed");
        }

        uploadAgent();

        Thread threadRunningAgent = new Thread("Running Agent") {
            public void run() {
                try {
                    launchProg("" + PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        threadRunningAgent.start();
        Thread.sleep(4000);
        connectToAgent();
        System.out.println("succes !");
    }

    /**
	 * Connect to agent.
	 */
    private void connectToAgent() {
        for (int i = 0; i < 10; i++) {
            try {
                s = new Socket("127.0.0.1", PORT);
                break;
            } catch (Exception s) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
        System.out.println("Desktop => device socket connected");
        screencapture.start();
        try {
            os = s.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
	 * Launch prog.
	 * 
	 * @param cmdList
	 *            the cmd list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
    private void launchProg(String cmdList) throws IOException {
        String fullCmd = "export CLASSPATH=" + FICHERO_SERVIDOR;
        fullCmd += "; exec app_process /system/bin " + LANZADOR + " "
                + cmdList;
        System.out.println(fullCmd);
        dispositivo.executeShellCommand(fullCmd,
                new OutputStreamShellOutputReceiver(System.out));
        System.out.println("Prog ended");
        dispositivo.executeShellCommand("rm " + DESTINO_SERVIDOR,
                new OutputStreamShellOutputReceiver(System.out));
    }
}
