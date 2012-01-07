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
package org.uoc.androidremote.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.uoc.androidremote.client.main.Client;
import org.uoc.androidremote.client.structures.AndroidDevice;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.glavsoft.viewer.ARViewer;

/**
 * Swing panel to manage the connection stuff, like connect or disconnect to 
 * the remote device server.
 * 
 * Managing also the ip's and port in case of a network connection.
 */
public class ConnectionPanel extends JPanel {

	private static final String DEFAULT_IP = "127.0.0.1";

	private static final long serialVersionUID = 1L;
	
	private final JTextField hostEntry = new JTextField(20);
	
	private final JTextField portEntry = new JTextField(5);
	
	private final JTextField gestionPortEntry = new JTextField(5);
	
	private Client client;
	
	private JRadioButton usbConn;
	
	private JRadioButton netConn;
	
	private JButton buttonConnect;
	private static final String TO_CONNECT = "Conectar";
	private static final String TO_DISCONNECT = "Desconectar";
	
	private boolean connected = false;

	/**
	 * Instantiates a new connection panel.
	 * 
	 * @param c
	 *            the c
	 */
	public ConnectionPanel(Client c) {
		this.client = c;
		ButtonGroup group = new ButtonGroup();
		usbConn = new JRadioButton("USB");
		netConn = new JRadioButton("Network");
		group.add(usbConn);
		group.add(netConn);
		usbConn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hostEntry.setText(DEFAULT_IP);
				hostEntry.setEnabled(false);
			}
		});
		netConn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hostEntry.setText(DEFAULT_IP);
				hostEntry.setEnabled(true);
			}
		});
		netConn.setSelected(true);
		this.add(usbConn);
		this.add(netConn);
		this.add(new JLabel("Host:"));
		hostEntry.setText(DEFAULT_IP);
		this.add(hostEntry);
		this.add(new JLabel("VNC Port:"));
		portEntry.setText("5901");
		this.add(portEntry);
		this.add(new JLabel("Gestion Port:"));
		gestionPortEntry.setText("5000");
		this.add(gestionPortEntry);
		buttonConnect = new JButton("Conectar");
		buttonConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!connected) {
					connect();
				} else {
					disconnect();
				}
			}
		});
		this.add(buttonConnect);
	}
	
	private void disconnect() {
		client.closeConnection();
		client.getVncViewer().dontTryAgain();
		client.manageNetworkFunctions(false);
		client.manageUSBFunctions(false);
		
		setConnected(false);
		client.showConnectionClosed();
	}
	
	private void connect() {
		client.setHost(hostEntry.getText());
		String strVNCPort = portEntry.getText();
		int vncPort = 0;
		try {
			vncPort = Integer.valueOf(strVNCPort);
		} catch (NumberFormatException e) {
			vncPort = 0;
		}
		client.setPort(vncPort);
		String strGestionPort = gestionPortEntry.getText();
		int gestionPort = 0;
		try {
			gestionPort = Integer.valueOf(strGestionPort);
		} catch (NumberFormatException e) {
			gestionPort = 0;
		}
		client.setGestionPort(gestionPort);
		configureConnection(client.getVncViewer(), client.getHost(), vncPort);
		
		client.manageNetworkFunctions(true);

		if (usbConn.isSelected()) {
			try {
				AndroidDebugBridge bridge = AndroidDebugBridge
						.createBridge();
				waitDeviceList(bridge);
				IDevice devices[] = bridge.getDevices();
				AndroidDevice device = new AndroidDevice(devices[0]);
				device.getDevice().createForward(vncPort, vncPort);
				device.getDevice().createForward(gestionPort,
						gestionPort);
				client.setDevice(device);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			client.initSockets();
			client.openConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		setConnected(true);
	}

	/**
	 * Will prepare the vncViewer to connect to the host and port with
	 * the default parameters, like scaling factor, color depth and so.
	 * 
	 * @param vncViewer
	 *            The component which finally will perform the
	 *            connection to the remote host
	 * @param host
	 * @param port
	 */
	private void configureConnection(ARViewer vncViewer, String host,
			int port) {
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ARViewer.ARG_HOST, host);
		settings.put(ARViewer.ARG_PORT, String.valueOf(port));

		settings.put(ARViewer.ARG_SHOW_CONTROLS, String.valueOf(false));
		settings.put(ARViewer.ARG_OPEN_NEW_WINDOW, String.valueOf(false));
		settings.put(ARViewer.ARG_ALLOW_COPY_RECT, String.valueOf(false));
		settings.put(ARViewer.ARG_COMPRESSION_LEVEL, String.valueOf(9));
		settings.put(ARViewer.ARG_JPEG_IMAGE_QUALITY, String.valueOf(0));

		vncViewer.configure(settings);
		vncViewer.init();				
	}


	/**
	 * Wait device list.
	 * 
	 * @param bridge
	 *            the bridge
	 */
	private void waitDeviceList(AndroidDebugBridge bridge) {
		int count = 0;
		while (bridge.hasInitialDeviceList() == false) {
			try {
				Thread.sleep(100);
				count++;
			} catch (InterruptedException e) {
				// pass
			}
			// let's not wait > 10 sec.
			if (count > 10) {
				throw new RuntimeException("Timeout getting device list!");
			}
		}
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
		buttonConnect.setText(connected ?TO_DISCONNECT:TO_CONNECT);
	}
}
