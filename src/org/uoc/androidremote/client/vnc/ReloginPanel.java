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
//  Copyright (C) 2002 Cendio Systems.  All Rights Reserved.
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
// ReloginPanel class implements panel with a button for logging in again,
// after fatal errors or disconnect
//

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO: Auto-generated Javadoc
//
// The panel which implements the Relogin button
//

/**
 * The Class ReloginPanel.
 */
public class ReloginPanel extends Panel implements ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The relogin button. */
	Button reloginButton;
	
	/** The close button. */
	Button closeButton;
	
	/** The viewer. */
	VncViewer viewer;

	//
	// Constructor.
	//
	/**
	 * Instantiates a new relogin panel.
	 * 
	 * @param v
	 *            the v
	 */
	public ReloginPanel(VncViewer v) {
		viewer = v;
		setLayout(new FlowLayout(FlowLayout.CENTER));
		reloginButton = new Button("Login again");
		add(reloginButton);
		reloginButton.addActionListener(this);
		if (viewer.inSeparateFrame) {
			closeButton = new Button("Close window");
			add(closeButton);
			closeButton.addActionListener(this);
		}
	}

	//
	// This method is called when a button is pressed.
	//
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public synchronized void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == reloginButton)
			viewer.getAppletContext().showDocument(viewer.getDocumentBase());
	}
}
