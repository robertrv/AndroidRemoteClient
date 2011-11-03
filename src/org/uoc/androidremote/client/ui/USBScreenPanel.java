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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.uoc.androidremote.client.main.Client;
import org.uoc.androidremote.client.screen.GetScreenListener;
import org.uoc.androidremote.client.screen.JPanelScreen;
import org.uoc.androidremote.client.screen.ThreadGetScreen;

// TODO: Auto-generated Javadoc
/**
 * The Class USBScreenPanel.
 */
public class USBScreenPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The client. */
	private final Client client;
	
	/** The show usb screen button. */
	private JButton showUSBScreenButton;;
	
	/** The old image dimension. */
	private Dimension oldImageDimension = null;
	
	/** The panel screen. */
	private JPanelScreen panelScreen;
	
	/**
	 * Instantiates a new uSB screen panel.
	 * 
	 * @param c
	 *            the c
	 */
	public USBScreenPanel(Client c) {
		this.client = c;
		showUSBScreenButton = new JButton("Mostrar vista");
		showUSBScreenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showScreen();
			}
		});
		this.add(showUSBScreenButton);
	}
	
	/**
	 * Show screen.
	 */
	private void showScreen() {
		if (client.getDevice() != null) {
			panelScreen = new JPanelScreen();
			panelScreen.setPreferredSize(new Dimension(500, 800));
			panelScreen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			ThreadGetScreen screencapture = new ThreadGetScreen(
					client.getDevice().getDevice());
			screencapture.setListener(new GetScreenListener() {

				public void handleNewImage(Dimension size, BufferedImage image,
						boolean landscape) {
					if (oldImageDimension == null
							|| !size.equals(oldImageDimension)) {
						oldImageDimension = size;
					}
					panelScreen.handleNewImage(size, image, landscape);
				}
			});
			screencapture.start();
			this.add(panelScreen);
			this.repaint();
		}
	}
	
	/**
	 * Manage usb functions.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	public void manageUSBFunctions(boolean enabled) {
		showUSBScreenButton.setEnabled(enabled);
	}
}
