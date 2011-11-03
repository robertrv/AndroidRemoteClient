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

// TODO: Auto-generated Javadoc
/**
 * The Class ConnectionPanel.
 */
public class ConnectionPanel extends JPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The host entry. */
	private final JTextField hostEntry = new JTextField(20);
	
	/** The port entry. */
	private final JTextField portEntry = new JTextField(5);
	
	/** The gestion port entry. */
	private final JTextField gestionPortEntry = new JTextField(5);
	
	/** The client. */
	private Client client;
	
	/** The usb conn. */
	private JRadioButton usbConn;
	
	/** The net conn. */
	private JRadioButton netConn;
	
	/** The button connect. */
	private JButton buttonConnect;
	
	/** The connected. */
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
				hostEntry.setText("127.0.0.1");
				// portEntry.setText("5801");
				hostEntry.setEnabled(false);
			}
		});
		netConn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hostEntry.setText("192.168.1.199");
				// portEntry.setText("5901");
				hostEntry.setEnabled(true);
			}
		});
		netConn.setSelected(true);
		this.add(usbConn);
		this.add(netConn);
		this.add(new JLabel("Host:"));
		hostEntry.setText("192.168.1.199");
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
					client.setHost(hostEntry.getText());
					String strVNCPort = portEntry.getText();
					int port = 0;
					try {
						port = Integer.valueOf(strVNCPort);
					} catch (NumberFormatException e) {
						port = 0;
					}
					client.setPort(port);
					String strGestionPort = gestionPortEntry.getText();
					int gestionPort = 0;
					try {
						gestionPort = Integer.valueOf(strGestionPort);
					} catch (NumberFormatException e) {
						gestionPort = 0;
					}
					client.setGestionPort(gestionPort);
					client.getVncViewer().configure(client.getHost(), port);
					client.getVncViewer().start();
					client.manageNetworkFunctions(true);

					if (usbConn.isSelected()) {
						try {
							AndroidDebugBridge bridge = AndroidDebugBridge
									.createBridge();
							waitDeviceList(bridge);
							IDevice devices[] = bridge.getDevices();
							AndroidDevice device = new AndroidDevice(devices[0]);
							device.getDevice().createForward(port, port);
							device.getDevice().createForward(gestionPort,
									gestionPort);
							client.setDevice(device);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					try {
						client.initSockets();
						client.openConnection();
					} catch (Exception e) {
						// TODO: handle exception
					}
					connected = true;
					buttonConnect.setText("Desconectar");
				} else {
					client.getVncViewer().stop();
					client.manageNetworkFunctions(false);
					client.manageUSBFunctions(false);
					buttonConnect.setText("Conectar");
				}
			}
		});
		this.add(buttonConnect);
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
}
