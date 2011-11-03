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
package org.uoc.androidremote.client.vnc;

//
//  Copyright (C) 2001 HorizonLive.com, Inc.  All Rights Reserved.
//  Copyright (C) 2001 Constantin Kaplinsky.  All Rights Reserved.
//  Copyright (C) 2000 Tridia Corporation.  All Rights Reserved.
//  Copyright (C) 1999 AT&T Laboratories Cambridge.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

//
// Options frame.
//
// This deals with all the options the user can play with.
// It sets the encodings array and some booleans.
//

import java.awt.*;
import java.awt.event.*;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionsFrame.
 */
public class OptionsFrame extends Frame implements WindowListener, ActionListener,
		ItemListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The names. */
	static String[] names = { "Encoding", "Compression level",
			"JPEG image quality", "Cursor shape updates", "Use CopyRect",
			"Restricted colors", "Mouse buttons 2 and 3", "View only",
			"Scale remote cursor", "Share desktop", };

	/** The values. */
	static String[][] values = {
			{ "Auto", "Raw", "RRE", "CoRRE", "Hextile", "Zlib", "Tight", "ZRLE" },
			{ "Default", "1", "2", "3", "4", "5", "6", "7", "8", "9" },
			{ "JPEG off", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" },
			{ "Enable", "Ignore", "Disable" }, { "Yes", "No" },
			{ "Yes", "No" }, { "Normal", "Reversed" }, { "Yes", "No" },
			{ "No", "50%", "75%", "125%", "150%" }, { "Yes", "No" }, };

	/** The share desktop index. */
	final int encodingIndex = 0, compressLevelIndex = 1, jpegQualityIndex = 2,
			cursorUpdatesIndex = 3, useCopyRectIndex = 4,
			eightBitColorsIndex = 5, mouseButtonIndex = 6, viewOnlyIndex = 7,
			scaleCursorIndex = 8, shareDesktopIndex = 9;

	/** The labels. */
	Label[] labels = new Label[names.length];
	
	/** The choices. */
	Choice[] choices = new Choice[names.length];
	
	/** The close button. */
	Button closeButton;
	
	/** The viewer. */
	VncViewer viewer;

	//
	// The actual data which other classes look at:
	//

	/** The preferred encoding. */
	int preferredEncoding;
	
	/** The compress level. */
	int compressLevel;
	
	/** The jpeg quality. */
	int jpegQuality;
	
	/** The use copy rect. */
	boolean useCopyRect;
	
	/** The request cursor updates. */
	boolean requestCursorUpdates;
	
	/** The ignore cursor updates. */
	boolean ignoreCursorUpdates;

	/** The eight bit colors. */
	boolean eightBitColors;

	/** The reverse mouse buttons2 and3. */
	boolean reverseMouseButtons2And3;
	
	/** The share desktop. */
	boolean shareDesktop;
	
	/** The view only. */
	boolean viewOnly;
	
	/** The scale cursor. */
	int scaleCursor;

	/** The auto scale. */
	boolean autoScale;
	
	/** The scaling factor. */
	int scalingFactor;

	//
	// Constructor. Set up the labels and choices from the names and values
	// arrays.
	//

	/**
	 * Instantiates a new options frame.
	 * 
	 * @param v
	 *            the v
	 */
	OptionsFrame(VncViewer v) {
		super("TightVNC Options");

		viewer = v;

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;

		for (int i = 0; i < names.length; i++) {
			labels[i] = new Label(names[i]);
			gbc.gridwidth = 1;
			gridbag.setConstraints(labels[i], gbc);
			add(labels[i]);

			choices[i] = new Choice();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(choices[i], gbc);
			add(choices[i]);
			choices[i].addItemListener(this);

			for (int j = 0; j < values[i].length; j++) {
				choices[i].addItem(values[i][j]);
			}
		}

		closeButton = new Button("Close");
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(closeButton, gbc);
		add(closeButton);
		closeButton.addActionListener(this);

		pack();

		addWindowListener(this);

		// Set up defaults

		choices[encodingIndex].select("Auto");
		choices[compressLevelIndex].select("Default");
		choices[jpegQualityIndex].select("6");
		choices[cursorUpdatesIndex].select("Enable");
		choices[useCopyRectIndex].select("Yes");
		choices[eightBitColorsIndex].select("No");
		choices[mouseButtonIndex].select("Normal");
		choices[viewOnlyIndex].select("No");
		choices[scaleCursorIndex].select("No");
		choices[shareDesktopIndex].select("Yes");

		// But let them be overridden by parameters

		/*for (int i = 0; i < names.length; i++) {
			String s = viewer.readParameter(names[i], false);
			if (s != null) {
				for (int j = 0; j < values[i].length; j++) {
					if (s.equalsIgnoreCase(values[i][j])) {
						choices[i].select(j);
					}
				}
			}
		}*/
		choices[0].select(6);

		// FIXME: Provide some sort of GUI for "Scaling Factor".

		autoScale = false;
		scalingFactor = 100;
		/*String s = viewer.readParameter("Scaling Factor", false);
		if (s != null) {
			if (s.equalsIgnoreCase("Auto")) {
				autoScale = true;
			} else {
				// Remove the '%' char at the end of string if present.
				if (s.charAt(s.length() - 1) == '%') {
					s = s.substring(0, s.length() - 1);
				}
				// Convert to an integer.
				try {
					scalingFactor = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					scalingFactor = 100;
				}
				// Make sure scalingFactor is in the range of [1..1000].
				if (scalingFactor < 1) {
					scalingFactor = 1;
				} else if (scalingFactor > 1000) {
					scalingFactor = 1000;
				}
			}
		}*/

		// Make the booleans and encodings array correspond to the state of the
		// GUI

		setEncodings();
		setColorFormat();
		setOtherOptions();
	}

	//
	// Disable the shareDesktop option
	//

	/**
	 * Disable share desktop.
	 */
	void disableShareDesktop() {
		labels[shareDesktopIndex].setEnabled(false);
		choices[shareDesktopIndex].setEnabled(false);
	}

	//
	// setEncodings looks at the encoding, compression level, JPEG
	// quality level, cursor shape updates and copyRect choices and sets
	// corresponding variables properly. Then it calls the VncViewer's
	// setEncodings method to send a SetEncodings message to the RFB
	// server.
	//

	/**
	 * Sets the encodings.
	 */
	void setEncodings() {
		useCopyRect = choices[useCopyRectIndex].getSelectedItem().equals("Yes");

		preferredEncoding = RfbProto.EncodingRaw;
		boolean enableCompressLevel = false;

		if (choices[encodingIndex].getSelectedItem().equals("RRE")) {
			preferredEncoding = RfbProto.EncodingRRE;
		} else if (choices[encodingIndex].getSelectedItem().equals("CoRRE")) {
			preferredEncoding = RfbProto.EncodingCoRRE;
		} else if (choices[encodingIndex].getSelectedItem().equals("Hextile")) {
			preferredEncoding = RfbProto.EncodingHextile;
		} else if (choices[encodingIndex].getSelectedItem().equals("ZRLE")) {
			preferredEncoding = RfbProto.EncodingZRLE;
		} else if (choices[encodingIndex].getSelectedItem().equals("Zlib")) {
			preferredEncoding = RfbProto.EncodingZlib;
			enableCompressLevel = true;
		} else if (choices[encodingIndex].getSelectedItem().equals("Tight")) {
			preferredEncoding = RfbProto.EncodingTight;
			enableCompressLevel = true;
		} else if (choices[encodingIndex].getSelectedItem().equals("Auto")) {
			preferredEncoding = -1;
		}

		// Handle compression level setting.

		try {
			compressLevel = Integer.parseInt(choices[compressLevelIndex]
					.getSelectedItem());
		} catch (NumberFormatException e) {
			compressLevel = -1;
		}
		if (compressLevel < 1 || compressLevel > 9) {
			compressLevel = -1;
		}
		labels[compressLevelIndex].setEnabled(enableCompressLevel);
		choices[compressLevelIndex].setEnabled(enableCompressLevel);

		// Handle JPEG quality setting.

		try {
			jpegQuality = Integer.parseInt(choices[jpegQualityIndex]
					.getSelectedItem());
		} catch (NumberFormatException e) {
			jpegQuality = -1;
		}
		if (jpegQuality < 0 || jpegQuality > 9) {
			jpegQuality = -1;
		}

		// Request cursor shape updates if necessary.

		requestCursorUpdates = !choices[cursorUpdatesIndex].getSelectedItem()
				.equals("Disable");

		if (requestCursorUpdates) {
			ignoreCursorUpdates = choices[cursorUpdatesIndex].getSelectedItem()
					.equals("Ignore");
		}

		viewer.setEncodings();
	}

	//
	// setColorFormat sets eightBitColors variable depending on the GUI
	// setting, causing switches between 8-bit and 24-bit colors mode if
	// necessary.
	//

	/**
	 * Sets the color format.
	 */
	void setColorFormat() {

		eightBitColors = choices[eightBitColorsIndex].getSelectedItem().equals(
				"Yes");

		boolean enableJPEG = !eightBitColors;

		labels[jpegQualityIndex].setEnabled(enableJPEG);
		choices[jpegQualityIndex].setEnabled(enableJPEG);
	}

	//
	// setOtherOptions looks at the "other" choices (ones that do not
	// cause sending any protocol messages) and sets the boolean flags
	// appropriately.
	//

	/**
	 * Sets the other options.
	 */
	void setOtherOptions() {

		reverseMouseButtons2And3 = choices[mouseButtonIndex].getSelectedItem()
				.equals("Reversed");

		viewOnly = choices[viewOnlyIndex].getSelectedItem().equals("Yes");
		if (viewer.vc != null)
			viewer.vc.enableInput(!viewOnly);

		shareDesktop = choices[shareDesktopIndex].getSelectedItem().equals(
				"Yes");

		String scaleString = choices[scaleCursorIndex].getSelectedItem();
		if (scaleString.endsWith("%"))
			scaleString = scaleString.substring(0, scaleString.length() - 1);
		try {
			scaleCursor = Integer.parseInt(scaleString);
		} catch (NumberFormatException e) {
			scaleCursor = 0;
		}
		if (scaleCursor < 10 || scaleCursor > 500) {
			scaleCursor = 0;
		}
		if (requestCursorUpdates && !ignoreCursorUpdates && !viewOnly) {
			labels[scaleCursorIndex].setEnabled(true);
			choices[scaleCursorIndex].setEnabled(true);
		} else {
			labels[scaleCursorIndex].setEnabled(false);
			choices[scaleCursorIndex].setEnabled(false);
		}
		if (viewer.vc != null)
			viewer.vc.createSoftCursor(); // update cursor scaling
	}

	//
	// Respond to actions on Choice controls
	//

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent evt) {
		Object source = evt.getSource();

		if (source == choices[encodingIndex]
				|| source == choices[compressLevelIndex]
				|| source == choices[jpegQualityIndex]
				|| source == choices[cursorUpdatesIndex]
				|| source == choices[useCopyRectIndex]) {

			setEncodings();

			if (source == choices[cursorUpdatesIndex]) {
				setOtherOptions(); // update scaleCursor state
			}

		} else if (source == choices[eightBitColorsIndex]) {

			setColorFormat();

		} else if (source == choices[mouseButtonIndex]
				|| source == choices[shareDesktopIndex]
				|| source == choices[viewOnlyIndex]
				|| source == choices[scaleCursorIndex]) {

			setOtherOptions();

		}
	}

	//
	// Respond to button press
	//

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == closeButton)
			setVisible(false);
	}

	//
	// Respond to window events
	//

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent evt) {
		setVisible(false);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent evt) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent evt) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent evt) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent evt) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent evt) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent evt) {
	}
}
