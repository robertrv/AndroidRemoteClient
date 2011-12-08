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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.uoc.androidremote.client.main.Client;
import org.uoc.androidremote.operations.AndroidApplication;
import org.uoc.androidremote.operations.LocationOperation;
import org.uoc.androidremote.operations.AndroidRunningApplication;
import org.uoc.androidremote.operations.AndroidService;
import org.uoc.androidremote.operations.ApplicationsInstalled;
import org.uoc.androidremote.operations.ApplicationsRunning;
import org.uoc.androidremote.operations.Operation;
import org.uoc.androidremote.operations.Reboot;
import org.uoc.androidremote.operations.ServicesRunning;

// TODO: Auto-generated Javadoc
/**
 * The Class NetworkPanel.
 */
public class NetworkPanel extends JPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private final Client client;

	private final JTextArea resultAreaAppsInst = new JTextArea(30, 20);
	private final JTextArea resultAreaApps = new JTextArea(30, 20);
	private final JTextArea resultAreaServices = new JTextArea(30, 20);
	private final JLabel resultLocation = new JLabel();
	private final JLabel resultReboot = new JLabel();
	private final JLabel resultBattery = new JLabel();
	
	private JButton queryAppsInstalledButton;
	
	private JButton runningAppsButton;
	
	private JButton executeServicesButton;
	
	private JButton batteryButton;
	
	private JButton locationButton;
	
	private JButton rebootButton;

	private static final int IMPORTANCE_BACKGROUND = 400;
	private static final int IMPORTANCE_EMPTY = 500;
	private static final int IMPORTANCE_FOREGROUND = 100;
	private static final int IMPORTANCE_SERVICE = 300;
	private static final int IMPORTANCE_VISIBLE = 200;
	
	/**
	 * Instantiates a new network panel.
	 * 
	 * @param c
	 *            the c
	 */
	public NetworkPanel(Client c) {
		this.client = c;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		batteryButton = new JButton("Bateria");
		batteryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (client.initSockets()) {

						Operation o = new Operation(Operation.OP_BATTERY_LEVEL,
								"");
						Integer level = (Integer) client.request(o);
						if (level != null) {
							resultBattery.setText(level + "%");
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.add(batteryButton);
		resultBattery.setText("");
		this.add(resultBattery);

		locationButton = new JButton("Posicion");
		locationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (client.initSockets()) {

						Operation o = new Operation(Operation.OP_LOCATION_GPS,
								"");

						LocationOperation loc = (LocationOperation) client
								.request(o);
						if (loc != null && loc.getMessage() == null) {
							resultLocation.setText("Lat: " + loc.getLatitude()
									+ " Long: " + loc.getLongitude());
						} else {
							String responseText = (loc != null && loc
									.getMessage() != null) ? loc.getMessage()
									: "Error trying to get location. ";
							resultLocation.setToolTipText(responseText);
							resultLocation.setText(responseText);
						}

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.add(locationButton);
		resultLocation.setText("");
		this.add(resultLocation);
		
		
		rebootButton = new JButton("Reiniciar");
		rebootButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (client.initSockets()) {

						System.out.println("Servicios");

						Operation o2 = new Operation(Operation.OP_REBOOT, "");

						Reboot result = (Reboot) client
								.request(o2);
						
						if (result != null) {
							String msg = "Result: ";
							if (result.getResult()) {
								msg += "Reboot performed";
							} else {
								msg += "Problem while rebooting: "
										+ result.getProblemMessage();
							}
							resultLocation.setText(msg);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
		});
		add(rebootButton);
		resultReboot.setText("");
		add(resultReboot);

		queryAppsInstalledButton = new JButton("Aplicaciones instaladas");
		queryAppsInstalledButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					if (client.initSockets()) {

						Operation o = new Operation(
								Operation.OP_APPLICATIONS_INSTALLED, "");
						ApplicationsInstalled apps = (ApplicationsInstalled) client
								.request(o);
						if (apps != null) {
							List<AndroidApplication> appsList = apps.getList();
							Collections.sort(appsList);
							if (!appsList.isEmpty()) {
								resultAreaAppsInst.setText("");
								for (Iterator<AndroidApplication> appIt = apps
										.getList().iterator(); appIt.hasNext();) {
									AndroidApplication app = appIt.next();
									resultAreaAppsInst.append(app
											.getApplicationName()
											+ " - "
											+ app.getPackageName() + "\n");
								}

							}
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.add(queryAppsInstalledButton);
		JScrollPane resultPaneAppsInst = new JScrollPane(resultAreaAppsInst);
		this.add(resultPaneAppsInst);

		runningAppsButton = new JButton("Aplicaciones en ejecución");
		runningAppsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					if (client.initSockets()) {

						System.out.println("Aplicaciones");
						Operation o = new Operation(
								Operation.OP_APPLICATIONS_RUNNING, "");

						ApplicationsRunning apps = (ApplicationsRunning) client
								.request(o);
						if (apps != null) {
							List<AndroidRunningApplication> appsList = apps
									.getList();
							Collections.sort(appsList);
							if (!appsList.isEmpty()) {
								resultAreaApps.setText("");
							}
							for (Iterator<AndroidRunningApplication> appIt = apps
									.getList().iterator(); appIt.hasNext();) {
								AndroidRunningApplication app = appIt.next();
								System.out.println(app.getName() + " - "
										+ app.getImportance());
								resultAreaApps.append(app.getName()
										+ " - "
										+ getImportanceText(app.getImportance())
										+ "\n");
							}

						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.add(runningAppsButton);
		JScrollPane resultPaneApps = new JScrollPane(resultAreaApps);
		this.add(resultPaneApps);

		executeServicesButton = new JButton("Servicios");
		executeServicesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					if (client.initSockets()) {

						System.out.println("Servicios");

						Operation o2 = new Operation(
								Operation.OP_SERVICES_RUNNING, "");

						ServicesRunning services = (ServicesRunning) client
								.request(o2);
						if (services != null) {
							if (!services.getServices().isEmpty()) {
								resultAreaServices.setText("");
							}
							for (Iterator<AndroidService> servIt = services
									.getServices().iterator(); servIt.hasNext();) {
								AndroidService s = servIt.next();
								System.out.println(s.getPid() + " "
										+ s.getProcess());
								resultAreaServices.append(s.getPid() + " - "
										+ s.getProcess() + "\n");
							}

						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		add(executeServicesButton);
		JScrollPane resultPaneServices = new JScrollPane(resultAreaServices);
		add(resultPaneServices);
	}
	
	private String getImportanceText(int imp) {
		switch (imp) {
		case IMPORTANCE_BACKGROUND:
			return "Background";
		case IMPORTANCE_EMPTY:
			return "Empty";
		case IMPORTANCE_FOREGROUND:
			return "Foreground";
		case IMPORTANCE_SERVICE:
			return "Service";
		case IMPORTANCE_VISIBLE:
			return "Visible";
		default:
			return "";
		}
	}
	
	public void manageNetworkFunctions(boolean enabled) {
		runningAppsButton.setEnabled(enabled);
		executeServicesButton.setEnabled(enabled);
		locationButton.setEnabled(enabled);
		queryAppsInstalledButton.setEnabled(enabled);
		batteryButton.setEnabled(enabled);
		rebootButton.setEnabled(enabled);
	}
}
