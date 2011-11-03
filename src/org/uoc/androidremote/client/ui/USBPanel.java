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
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.uoc.androidremote.client.main.Client;
import org.uoc.androidremote.client.usb.visualization.JFrameExplorer;

// TODO: Auto-generated Javadoc
/**
 * The Class USBPanel.
 */
public class USBPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The filesystem button. */
	private JButton filesystemButton;
	
	/** The install app button. */
	private JButton installAppButton;
	
	/** The client. */
	private Client client;
	
	/** The app file entry. */
	private JTextField appFileEntry;
	
	/**
	 * Instantiates a new uSB panel.
	 * 
	 * @param c
	 *            the c
	 */
	public USBPanel(Client c) {
		this.client = c;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.add(new JLabel("Sistema de ficheros"));
		filesystemButton = new JButton("Explorar");
		filesystemButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					if (client.getDevice() != null) {
						JFrameExplorer jf = new JFrameExplorer(client.getDevice()
								.getDevice());
						jf.setIconImage(client.getIconImage());
						jf.setVisible(true);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.add(filesystemButton);

		this.add(new JLabel("Aplicaciones"));
		installAppButton = new JButton("Instalar");
		installAppButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				if (client.getDevice() != null) {
					try {
						String filePath = appFileEntry.getText();
						if ((!filePath.isEmpty())
								&& (filePath.endsWith(".apk"))) {
							File app = new File(filePath);
							if (app.exists()) {
								client.getDevice().instalarApp(app.getAbsolutePath());
							}
						}

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
		appFileEntry = new JTextField(100);
		appFileEntry.setMaximumSize(appFileEntry.getPreferredSize());
		this.add(appFileEntry);
		this.add(installAppButton);
	}
	
	/**
	 * Manage usb functions.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	public void manageUSBFunctions(boolean enabled) {
		filesystemButton.setEnabled(enabled);
		installAppButton.setEnabled(enabled);
	}
}
