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
import org.uoc.androidremote.operations.AndroidLocation;
import org.uoc.androidremote.operations.AndroidRunningApplication;
import org.uoc.androidremote.operations.AndroidService;
import org.uoc.androidremote.operations.ApplicationsInstalled;
import org.uoc.androidremote.operations.ApplicationsRunning;
import org.uoc.androidremote.operations.Operation;
import org.uoc.androidremote.operations.ServicesRunning;

// TODO: Auto-generated Javadoc
/**
 * The Class NetworkPanel.
 */
public class NetworkPanel extends JPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The client. */
	private final Client client;
	
	/** The result area apps inst. */
	private final JTextArea resultAreaAppsInst = new JTextArea(30, 20);
	
	/** The result area apps. */
	private final JTextArea resultAreaApps = new JTextArea(30, 20);
	
	/** The result area services. */
	private final JTextArea resultAreaServices = new JTextArea(30, 20);
	
	/** The result location. */
	private final JLabel resultLocation = new JLabel();
	
	/** The result battery. */
	private final JLabel resultBattery = new JLabel();
	
	/** The query apps installed button. */
	private JButton queryAppsInstalledButton;
	
	/** The execute button. */
	private JButton executeButton;
	
	/** The execute services button. */
	private JButton executeServicesButton;
	
	/** The battery button. */
	private JButton batteryButton;
	
	/** The location button. */
	private JButton locationButton;

	/** The Constant IMPORTANCE_BACKGROUND. */
	private static final int IMPORTANCE_BACKGROUND = 400;
	
	/** The Constant IMPORTANCE_EMPTY. */
	private static final int IMPORTANCE_EMPTY = 500;
	
	/** The Constant IMPORTANCE_FOREGROUND. */
	private static final int IMPORTANCE_FOREGROUND = 100;
	
	/** The Constant IMPORTANCE_SERVICE. */
	private static final int IMPORTANCE_SERVICE = 300;
	
	/** The Constant IMPORTANCE_VISIBLE. */
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

		this.add(new JLabel("Bateria"));
		batteryButton = new JButton("Consultar");
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

		this.add(new JLabel("Posicion"));
		locationButton = new JButton("Consultar");
		locationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (client.initSockets()) {

						Operation o = new Operation(Operation.OP_LOCATION_GPS,
								"");

						AndroidLocation loc = (AndroidLocation) client
								.request(o);
						if (loc != null) {
							resultLocation.setText("Lat: " + loc.getLatitude()
									+ " Long: " + loc.getLongitude());
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

		this.add(new JLabel("Aplicaciones instaladas"));
		queryAppsInstalledButton = new JButton("Ejecutar");
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

		this.add(new JLabel("Aplicaciones en ejecución"));
		executeButton = new JButton("Ejecutar");
		executeButton.addActionListener(new ActionListener() {

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
		this.add(executeButton);
		JScrollPane resultPaneApps = new JScrollPane(resultAreaApps);
		this.add(resultPaneApps);

		this.add(new JLabel("Servicios"));

		executeServicesButton = new JButton("Ejecutar");
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
		this.add(executeServicesButton);
		JScrollPane resultPaneServices = new JScrollPane(resultAreaServices);
		this.add(resultPaneServices);
	}
	
	/**
	 * Gets the importance text.
	 * 
	 * @param imp
	 *            the imp
	 * @return the importance text
	 */
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
	
	/**
	 * Manage network functions.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	public void manageNetworkFunctions(boolean enabled) {
		executeButton.setEnabled(enabled);
		executeServicesButton.setEnabled(enabled);
		locationButton.setEnabled(enabled);
		queryAppsInstalledButton.setEnabled(enabled);
		batteryButton.setEnabled(enabled);
	}
}
