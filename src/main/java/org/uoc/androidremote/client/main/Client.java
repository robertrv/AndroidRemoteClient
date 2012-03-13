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
package org.uoc.androidremote.client.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.uoc.androidremote.client.structures.AndroidDevice;
import org.uoc.androidremote.client.ui.ConnectionPanel;
import org.uoc.androidremote.client.ui.NetworkPanel;
import org.uoc.androidremote.client.ui.USBPanel;
import org.uoc.androidremote.client.ui.USBScreenPanel;
import org.uoc.androidremote.operations.Operation;

import com.glavsoft.viewer.ARViewer;

/**
 * Main AndroidRemoteClient class to hold the main window
 */
public class Client extends JFrame {

	/**
	 * Simple class to handle all the logger from the vnc viewer and show it 
	 * inside our swing layout.
	 * 
	 * @author roberrv[at]gmail.com
	 *
	 */
	private final class StatusHandler extends Handler {
		private final JLabel statusLabel;

		private StatusHandler(JLabel statusLabel) {
			this.statusLabel = statusLabel;
		}

		@Override
		public void publish(final LogRecord record) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (record.getLevel().intValue() >= Level.WARNING
							.intValue()) {
						statusLabel.setForeground(Color.RED);
					} else {
						statusLabel.setForeground(Color.BLACK);
					}

					statusLabel.setText(record.getMessage());

					// remove after 15 seconds, if it holds my message yet
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
							@Override public void run() {
								statusLabel.setText(" ");
							}
						},
						15 * 1000); // wait 15 seconds						
				}
				
			});
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	}

	private static final long serialVersionUID = 1L;
	
	private final ARViewer vncViewer = new ARViewer();
	
	private NetworkPanel networkPanel;
	
	private USBPanel usbPanel;
	
	private USBScreenPanel usbScreenPanel;

	private JLabel gestionConnStatus;	/** The device. */
	private AndroidDevice device;
	
	private int gestionPort = 5000;
	
	private String host = "localhost";
	
	private int port = 0;
	
	private Socket envia;
	
	private ObjectOutputStream salida;
	
	private ObjectInputStream ins;

	private ConnectionPanel connectionPanel;

	private JLabel statusLabel;

	private static final String MNG_STATUS = "Management - State: ";
	
	private static final Logger logger = Logger.getLogger(Client.class.getName());

	/**
	 * Instantiates a new client.
	 */
	public Client() {
		this.setTitle("Android Remote Client");
	}

	/**
	 * Start.
	 */
	public void start() {
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(new Dimension(1000, 1000));
		JPanel mainPanel = new JPanel(new BorderLayout());

		connectionPanel = new ConnectionPanel(this, getHost(),
				String.valueOf(getPort()), String.valueOf(getGestionPort()));

		mainPanel.add(connectionPanel, BorderLayout.NORTH);

		//JPanel centerPanel = new JPanel(new GridLayout(1, 2));
		JPanel centerPanel = new JPanel(new BorderLayout());

		JPanel vncContainer = new JPanel(new BorderLayout());
		statusLabel = new JLabel(" ");
		StatusHandler logHandler = new StatusHandler(statusLabel);
		vncViewer.addLoggerHandler(logHandler);
		logger.addHandler(logHandler);
		
		vncContainer.add(statusLabel, BorderLayout.NORTH);
		
		JApplet vncPanel = vncViewer;
		vncContainer.add(vncPanel, BorderLayout.CENTER);
		centerPanel.add(vncContainer, BorderLayout.CENTER);

		JPanel gestionContainer = new JPanel(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		networkPanel = new NetworkPanel(this);
		usbPanel = new USBPanel(this);
		gestionConnStatus = new JLabel(MNG_STATUS + "disconnected");
		gestionContainer.add(gestionConnStatus, BorderLayout.NORTH);
		usbScreenPanel = new USBScreenPanel(this);

		tabbedPane.addTab("Funcionalidades en red", networkPanel);
		tabbedPane.addTab("Funcionalidades en USB", usbPanel);
		tabbedPane.addTab("Vista ADB", usbScreenPanel);
		gestionContainer.add(tabbedPane, BorderLayout.CENTER);
		gestionContainer.setMaximumSize(new Dimension(300,300));
		gestionContainer.setPreferredSize(new Dimension(300,300));
		centerPanel.add(gestionContainer, BorderLayout.EAST);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		this.add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		manageNetworkFunctions(false);
		manageUSBFunctions(false);
		
		// This handler manages all those exceptions which are not explicitly handled
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				String msg = "GUI produced an unexpected exception (thread: "
						+ t.getName() + ")";
				logger.log(Level.SEVERE, msg, e);
				JOptionPane.showMessageDialog(Client.this, e.getMessage());
				boolean isStopped = Client.this.vncViewer.getIsAppletStopped();
				Client.this.connectionPanel.setConnected(!isStopped);
			}
		});
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Client c = new Client();
		if (args.length>0) {
			int idx = 0;
			if (args.length>idx) {
				c.setHost(args[idx++]);
			}
			if (args.length>idx) {
				c.setPort(Integer.parseInt(args[idx++]));
			}
			if (args.length>idx) {
				c.setGestionPort(Integer.parseInt(args[idx++]));
			}
		}
		c.start();

	}

	/**
	 * inits management sockets
	 * 
	 * @return true, if successful
	 */
	public boolean initSockets() {
		boolean available = false;
		try {
			envia = new Socket(InetAddress.getByName(host), gestionPort);
			ins = new ObjectInputStream(envia.getInputStream());
			salida = new ObjectOutputStream(envia.getOutputStream());
			salida.flush();

			String status = MNG_STATUS + "connected";
			manageUSBFunctions(device != null);
			if (device != null) {
				status += " - Conectado a USB";
			}
			gestionConnStatus.setText(status);
			available = true;
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			gestionConnStatus.setText(MNG_STATUS + "connection error");
		} catch (ConnectException e) {
			gestionConnStatus
					.setText(MNG_STATUS + "remote server stopped");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			gestionConnStatus.setText(MNG_STATUS + "connection error");
		}
		return available;
	}

	/**
	 * Manage network functions.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	public void manageNetworkFunctions(boolean enabled) {
		networkPanel.manageNetworkFunctions(enabled);
	}

	/**
	 * Manage usb functions.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	public void manageUSBFunctions(boolean enabled) {
		usbPanel.manageUSBFunctions(enabled);
		usbScreenPanel.manageUSBFunctions(enabled);
	}

	/**
	 * Gets the vnc viewer.
	 * 
	 * @return the vnc viewer
	 */
	public ARViewer getVncViewer() {
		return vncViewer;
	}

	/**
	 * Gets the device.
	 * 
	 * @return the device
	 */
	public AndroidDevice getDevice() {
		return device;
	}

	/**
	 * Sets the device.
	 * 
	 * @param device
	 *            the new device
	 */
	public void setDevice(AndroidDevice device) {
		this.device = device;
	}

	/**
	 * Open connection.
	 */
	public void openConnection() {
		if (salida == null) {
			throw new IllegalStateException(
					"Network error, manangment server down!");
		}
		Operation o = new Operation(Operation.OP_OPEN,
				"Cliente: AndroidRemoteClient");

		request(o);
}
	
	public void closeConnection() {
		if (salida == null) {
			throw new IllegalStateException(
					"Network error, manangment server down!");
		}
		if (initSockets()) {
			request(new Operation(Operation.OP_CLOSE, 
					"disconnecting from client AndroidRemoteClient"));			
		}
		gestionConnStatus.setText(MNG_STATUS + "disconnected");
	}


	/**
	 * Gets the gestion port.
	 * 
	 * @return the gestion port
	 */
	public int getGestionPort() {
		return gestionPort;
	}

	/**
	 * Sets the gestion port.
	 * 
	 * @param gestionPort
	 *            the new gestion port
	 */
	public void setGestionPort(int gestionPort) {
		this.gestionPort = gestionPort;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the new host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Request.
	 * 
	 * @param o
	 *            the o
	 * @return the object
	 */
	public Object request(Operation o) {
		try {
			salida.writeObject(o);
			salida.flush();
			return ins.readObject();
		} catch (IOException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void showConnectionClosed() {
		statusLabel.setText("Disconnected from server");
	}
	
}
