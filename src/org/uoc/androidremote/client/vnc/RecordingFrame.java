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
//  Copyright (C) 2002 Constantin Kaplinsky.  All Rights Reserved.
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
// Recording frame. It allows to control recording RFB sessions into
// FBS (FrameBuffer Stream) files.
//

import java.io.*;
import java.awt.*;
import java.awt.event.*;

// TODO: Auto-generated Javadoc
/**
 * The Class RecordingFrame.
 */
public class RecordingFrame extends Frame implements WindowListener, ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The recording. */
	boolean recording;

	/** The fname field. */
	TextField fnameField;
	
	/** The browse button. */
	Button browseButton;

	/** The status label. */
	Label statusLabel;

	/** The close button. */
	Button recordButton, nextButton, closeButton;
	
	/** The viewer. */
	VncViewer viewer;

	//
	// Check if current security manager allows to create a
	// RecordingFrame object.
	//

	/**
	 * Check security.
	 * 
	 * @return true, if successful
	 */
	public static boolean checkSecurity() {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			try {
				security.checkPropertyAccess("user.dir");
				security.checkPropertyAccess("file.separator");
				// Work around (rare) checkPropertyAccess bug
				System.getProperty("user.dir");
			} catch (SecurityException e) {
				System.out.println("SecurityManager restricts session recording.");
				return false;
			}
		}
		return true;
	}

	//
	// Constructor.
	//

	/**
	 * Instantiates a new recording frame.
	 * 
	 * @param v
	 *            the v
	 */
	RecordingFrame(VncViewer v) {
		super("TightVNC Session Recording");

		viewer = v;

		// Determine initial filename for next saved session.
		// FIXME: Check SecurityManager.

		String fname = nextNewFilename(System.getProperty("user.dir") + System.getProperty("file.separator") + "vncsession.fbs");

		// Construct new panel with file name field and "Browse" button.

		Panel fnamePanel = new Panel();
		GridBagLayout fnameGridbag = new GridBagLayout();
		fnamePanel.setLayout(fnameGridbag);

		GridBagConstraints fnameConstraints = new GridBagConstraints();
		fnameConstraints.gridwidth = GridBagConstraints.RELATIVE;
		fnameConstraints.fill = GridBagConstraints.BOTH;
		fnameConstraints.weightx = 4.0;

		fnameField = new TextField(fname, 64);
		fnameGridbag.setConstraints(fnameField, fnameConstraints);
		fnamePanel.add(fnameField);
		fnameField.addActionListener(this);

		fnameConstraints.gridwidth = GridBagConstraints.REMAINDER;
		fnameConstraints.weightx = 1.0;

		browseButton = new Button("Browse");
		fnameGridbag.setConstraints(browseButton, fnameConstraints);
		fnamePanel.add(browseButton);
		browseButton.addActionListener(this);

		// Construct the frame.

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(10, 0, 0, 0);

		Label helpLabel = new Label("File name to save next recorded session in:", Label.CENTER);
		gridbag.setConstraints(helpLabel, gbc);
		add(helpLabel);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 0, 0, 0);

		gridbag.setConstraints(fnamePanel, gbc);
		add(fnamePanel);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(10, 0, 10, 0);

		statusLabel = new Label("", Label.CENTER);
		gridbag.setConstraints(statusLabel, gbc);
		add(statusLabel);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 0);

		recordButton = new Button("Record");
		gridbag.setConstraints(recordButton, gbc);
		add(recordButton);
		recordButton.addActionListener(this);

		nextButton = new Button("Next file");
		gridbag.setConstraints(nextButton, gbc);
		add(nextButton);
		nextButton.addActionListener(this);

		closeButton = new Button("Close");
		gridbag.setConstraints(closeButton, gbc);
		add(closeButton);
		closeButton.addActionListener(this);

		// Set correct text, font and color for the statusLabel.
		stopRecording();

		pack();

		addWindowListener(this);
	}

	//
	// If the given string ends with ".NNN" where NNN is a decimal
	// number, increase this number by one. Otherwise, append ".001"
	// to the given string.
	//

	/**
	 * Next filename.
	 * 
	 * @param fname
	 *            the fname
	 * @return the string
	 */
	protected String nextFilename(String fname) {
		int len = fname.length();
		int suffixPos = len;
		int suffixNum = 1;

		if (len > 4 && fname.charAt(len - 4) == '.') {
			try {
				suffixNum = Integer.parseInt(fname.substring(len - 3, len)) + 1;
				suffixPos = len - 4;
			} catch (NumberFormatException e) {
			}
		}

		char[] zeroes = { '0', '0', '0' };
		String suffix = String.valueOf(suffixNum);
		if (suffix.length() < 3) {
			suffix = new String(zeroes, 0, 3 - suffix.length()) + suffix;
		}

		return fname.substring(0, suffixPos) + '.' + suffix;
	}

	//
	// Find next name of a file which does not exist yet.
	//

	/**
	 * Next new filename.
	 * 
	 * @param fname
	 *            the fname
	 * @return the string
	 */
	protected String nextNewFilename(String fname) {
		String newName = fname;
		File f;
		try {
			do {
				newName = nextFilename(newName);
				f = new File(newName);
			} while (f.exists());
		} catch (SecurityException e) {
		}

		return newName;
	}

	//
	// Let the user choose a file name showing a FileDialog.
	//

	/**
	 * Browse file.
	 * 
	 * @return true, if successful
	 */
	protected boolean browseFile() {
		File currentFile = new File(fnameField.getText());

		FileDialog fd = new FileDialog(this, "Save next session as...", FileDialog.SAVE);
		fd.setDirectory(currentFile.getParent());
		fd.setVisible(true);
		if (fd.getFile() != null) {
			String newDir = fd.getDirectory();
			String sep = System.getProperty("file.separator");
			if (newDir.length() > 0) {
				if (!sep.equals(newDir.substring(newDir.length() - sep.length())))
					newDir += sep;
			}
			String newFname = newDir + fd.getFile();
			if (newFname.equals(fnameField.getText())) {
				fnameField.setText(newFname);
				return true;
			}
		}
		return false;
	}

	//
	// Start recording.
	//

	/**
	 * Start recording.
	 */
	public void startRecording() {
		statusLabel.setText("Status: Recording...");
		statusLabel.setFont(new Font("Helvetica", Font.BOLD, 12));
		statusLabel.setForeground(Color.red);
		recordButton.setLabel("Stop recording");

		recording = true;

		viewer.setRecordingStatus(fnameField.getText());
	}

	//
	// Stop recording.
	//

	/**
	 * Stop recording.
	 */
	public void stopRecording() {
		statusLabel.setText("Status: Not recording.");
		statusLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
		statusLabel.setForeground(Color.black);
		recordButton.setLabel("Record");

		recording = false;

		viewer.setRecordingStatus(null);
	}

	//
	// Close our window properly.
	//

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent evt) {
		setVisible(false);
	}

	//
	// Ignore window events we're not interested in.
	//

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

	//
	// Respond to button presses
	//

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == browseButton) {
			if (browseFile() && recording)
				startRecording();

		} else if (evt.getSource() == recordButton) {
			if (!recording) {
				startRecording();
			} else {
				stopRecording();
				fnameField.setText(nextNewFilename(fnameField.getText()));
			}

		} else if (evt.getSource() == nextButton) {
			fnameField.setText(nextNewFilename(fnameField.getText()));
			if (recording)
				startRecording();

		} else if (evt.getSource() == closeButton) {
			setVisible(false);

		}
	}
}
