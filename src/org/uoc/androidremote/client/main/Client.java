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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.uoc.androidremote.client.structures.AndroidDevice;
import org.uoc.androidremote.client.ui.ConnectionPanel;
import org.uoc.androidremote.client.ui.NetworkPanel;
import org.uoc.androidremote.client.ui.USBPanel;
import org.uoc.androidremote.client.ui.USBScreenPanel;
import org.uoc.androidremote.client.vnc.VncViewer;
import org.uoc.androidremote.operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class Client.
 */
public class Client extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The vnc viewer. */
	private final VncViewer vncViewer = new VncViewer();;
	
	/** The network panel. */
	private NetworkPanel networkPanel;
	
	/** The usb panel. */
	private USBPanel usbPanel;
	
	/** The usb screen panel. */
	private USBScreenPanel usbScreenPanel;

	/** The gestion conn status. */
	private JLabel gestionConnStatus;	/** The device. */
	private AndroidDevice device;
	
	/** The gestion port. */
	private int gestionPort = 5000;
	
	/** The host. */
	private String host = "localhost";
	
	/** The port. */
	private int port = 0;
	
	/** The envia. */
	private Socket envia;
	
	/** The salida. */
	private ObjectOutputStream salida;
	
	/** The ins. */
	private ObjectInputStream ins;

	/** The Constant GESTION_STATUS. */
	private static final String GESTION_STATUS = "Gestión - Estado: ";
	
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
		// this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(new Dimension(1000, 1000));
		vncViewer.init();
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel topPanel = new ConnectionPanel(this);

		mainPanel.add(topPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(1, 2));

		JPanel vncContainer = new JPanel(new BorderLayout());
		vncContainer.add(vncViewer.connStatusLabel, BorderLayout.NORTH);
		JPanel vncPanel = vncViewer.vncFrame;
		vncPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		vncContainer.add(vncPanel, BorderLayout.CENTER);
		centerPanel.add(vncContainer);

		JPanel gestionContainer = new JPanel(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		networkPanel = new NetworkPanel(this);
		usbPanel = new USBPanel(this);
		gestionConnStatus = new JLabel(GESTION_STATUS + "desconectado");
		gestionContainer.add(gestionConnStatus, BorderLayout.NORTH);
		usbScreenPanel = new USBScreenPanel(this);

		tabbedPane.addTab("Funcionalidades en red", networkPanel);
		tabbedPane.addTab("Funcionalidades en USB", usbPanel);
		tabbedPane.addTab("Vista ADB", usbScreenPanel);
		gestionContainer.add(tabbedPane, BorderLayout.CENTER);
		centerPanel.add(gestionContainer);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		this.add(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		manageNetworkFunctions(false);
		manageUSBFunctions(false);
		
		// This handler manages all those exceptions which are not explicitly handled
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(){
			@Override public void uncaughtException(Thread t, Throwable e){
				String msg="GUI produced an unexpected exception (thread: " + t.getName() + ")";
				logger.log(Level.WARNING, msg, e);
				JOptionPane.showMessageDialog(Client.this, e.getMessage());
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
		
		c.start();

	}

	/**
	 * Inits the sockets.
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

			String status = GESTION_STATUS + "conectado";
			manageUSBFunctions(device != null);
			if (device != null) {
				status += " - Conectado a USB";
			}
			gestionConnStatus.setText(status);
			available = true;
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			gestionConnStatus.setText(GESTION_STATUS + "error de conexión");
		} catch (ConnectException e) {
			gestionConnStatus
					.setText(GESTION_STATUS + "servidor remoto parado");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			gestionConnStatus.setText(GESTION_STATUS + "error de conexión");
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
	public VncViewer getVncViewer() {
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
		Operation o = new Operation(Operation.OP_OPEN, "Cliente: Angel");

		try {
			salida.writeObject(o);
			salida.flush();
			String response = (String) ins.readObject();
			System.out.println(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Clean vnc.
	 */
	public void cleanVNC(){
		
	}
}
