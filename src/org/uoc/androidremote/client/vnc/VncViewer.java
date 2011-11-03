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
//  Copyright (C) 2001-2004 HorizonLive.com, Inc.  All Rights Reserved.
//  Copyright (C) 2002 Constantin Kaplinsky.  All Rights Reserved.
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
// VncViewer.java - the VNC viewer applet.  This class mainly just sets up the
// user interface, leaving it to the VncCanvas to do the actual rendering of
// a VNC desktop.
//

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Class VncViewer.
 * 
 * This is responsible to display the result of the Vnc client, as well all the
 *  button and options to manage it.
 */
public class VncViewer extends java.applet.Applet implements java.lang.Runnable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The in an applet. */
	boolean inAnApplet = true;
	
	/** The in separate frame. */
	boolean inSeparateFrame = false;

	/**
	 * Instantiates a new vnc viewer.
	 */
	public VncViewer() {
		this.inAnApplet = false;
		this.inSeparateFrame = true;
	}

	//
	// main() is called when run as a java program from the command line.
	// It simply runs the applet inside a newly-created frame.
	//

	/**
	 * The main method.
	 * 
	 * @param argv
	 *            the arguments
	 */
	public static void main(String[] argv) {
		VncViewer v = new VncViewer();

		v.init();
		v.start();
	}

	/** The main args. */
	String[] mainArgs;

	/** The rfb. */
	RfbProto rfb;
	
	/** The rfb thread. */
	Thread rfbThread;

	/** The vnc frame. */
	public JPanel vncFrame;
	
	/** The vnc container. */
	Container vncContainer;
	
	/** The desktop scroll pane. */
	ScrollPane desktopScrollPane;
	
	/** The gridbag. */
	GridBagLayout gridbag;
	
	/** The button panel. */
	ButtonPanel buttonPanel;
	
	/** The conn status label. */
	public JLabel connStatusLabel;
	
	/** The vc. */
	VncCanvas vc;
	
	/** The options. */
	OptionsFrame options;
	
	/** The clipboard. */
	ClipboardFrame clipboard;
	
	/** The rec. */
	RecordingFrame rec;

	// Control session recording.
	/** The recording sync. */
	Object recordingSync;
	
	/** The session file name. */
	String sessionFileName;
	
	/** The recording active. */
	boolean recordingActive;
	
	/** The recording status changed. */
	boolean recordingStatusChanged;
	
	/** The cursor updates def. */
	String cursorUpdatesDef;
	
	/** The eight bit colors def. */
	String eightBitColorsDef;

	// Variables read from parameter values.
	/** The socket factory. */
	String socketFactory;
	
	/** The host. */
	String host;
	
	/** The port. */
	int port;
	
	/** The password param. */
	String passwordParam;
	
	/** The show controls. */
	boolean showControls;
	
	/** The offer relogin. */
	boolean offerRelogin;
	
	/** The show offline desktop. */
	boolean showOfflineDesktop;
	
	/** The defer screen updates. */
	int deferScreenUpdates;
	
	/** The defer cursor updates. */
	int deferCursorUpdates;
	
	/** The defer update requests. */
	int deferUpdateRequests;
	
	/** The debug stats exclude updates. */
	int debugStatsExcludeUpdates;
	
	/** The debug stats measure updates. */
	int debugStatsMeasureUpdates;

	/** The Constant VNC_STATUS. */
	private static final String VNC_STATUS = "Visualización VNC - Estado: ";

	// Reference to this applet for inter-applet communication.
	/** The ref applet. */
	public static java.applet.Applet refApplet;

	//
	// init()
	//

	/* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {

		// readParameters();

		refApplet = this;

		if (inSeparateFrame) {
			vncFrame = new JPanel();
			if (!inAnApplet) {
				vncFrame.add("Center", this);
			}
			vncContainer = vncFrame;
		} else {
			vncContainer = this;
		}

		recordingSync = new Object();

		options = new OptionsFrame(this);
		clipboard = new ClipboardFrame(this);
		if (RecordingFrame.checkSecurity())
			rec = new RecordingFrame(this);

		sessionFileName = null;
		recordingActive = false;
		recordingStatusChanged = false;
		cursorUpdatesDef = null;
		eightBitColorsDef = null;

		connStatusLabel = new JLabel("Visualización VNC - Estado: desconectado");
		// rfbThread = new Thread(this);
		// rfbThread.start();
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
	}

	//
	// run() - executed by the rfbThread to deal with the RFB socket.
	//

	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
	public void start() {
		rfbThread = new Thread(this);
		rfbThread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		gridbag = new GridBagLayout();
		vncContainer.setLayout(gridbag);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		// if (showControls) {
		buttonPanel = new ButtonPanel(this);
		gridbag.setConstraints(buttonPanel, gbc);
		vncContainer.add(buttonPanel);
		// }

		try {
			connectAndAuthenticate();
			doProtocolInitialisation();

			// FIXME: Use auto-scaling not only in a separate frame.
			if (options.autoScale && inSeparateFrame) {
				Dimension screenSize;
				try {
					screenSize = vncContainer.getToolkit().getScreenSize();
				} catch (Exception e) {
					screenSize = new Dimension(0, 0);
				}
				createCanvas(screenSize.width - 32, screenSize.height - 32);
			} else {
				createCanvas(0, 0);
			}

			gbc.weightx = 1.0;
			gbc.weighty = 1.0;

			if (inSeparateFrame) {

				// Create a panel which itself is resizeable and can hold
				// non-resizeable VncCanvas component at the top left corner.
				Panel canvasPanel = new Panel();
				canvasPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
				canvasPanel.add(vc);

				// Create a ScrollPane which will hold a panel with VncCanvas
				// inside.
				desktopScrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
				gbc.fill = GridBagConstraints.BOTH;

				gridbag.setConstraints(desktopScrollPane, gbc);
				/*
				 * gbc.gridy++; gridbag.setConstraints(desktopScrollPane, gbc);
				 */
				desktopScrollPane.add(canvasPanel);

				// Finally, add our ScrollPane to the Frame window.
				vncFrame.add(desktopScrollPane);
				vc.resizeDesktopFrame();

			} else {

				// Just add the VncCanvas component to the Applet.
				gridbag.setConstraints(vc, gbc);
				add(vc);
				validate();

			}

			if (showControls)
				buttonPanel.enableButtons();

			moveFocusToDesktop();
			processNormalProtocol();

		} catch (NoRouteToHostException e) {
			System.err.println("Network error: no route to server: " + host + " - " + e.getMessage());
			showConnectionStatus("Network error: no route to server: " + host, true);
		} catch (UnknownHostException e) {
			System.err.println("Network error: server name unknown: " + host + " - " + e.getMessage());
			showConnectionStatus("Network error: server name unknown: " + host, true);
		} catch (ConnectException e) {
			System.err.println("Network error: could not connect to server: " + host + ":" + port + " - " + e.getMessage());
			showConnectionStatus("Network error: could not connect to server: " + host + ":" + port, true);
		} catch (EOFException e) {
			if (showOfflineDesktop) {
				e.printStackTrace();
				System.out.println("Network error: remote side closed connection");
				if (vc != null) {
					vc.enableInput(false);
				}
				if (rfb != null && !rfb.closed())
					rfb.close();
				if (showControls && buttonPanel != null) {
					buttonPanel.disableButtonsOnDisconnect();
					validate();
				}
			} else {
				fatalError("Network error: remote side closed connection", e);
			}
			showConnectionStatus("Network error");
		} catch (IOException e) {
			String str = e.getMessage();
			if (str != null && str.length() != 0) {
				System.err.println("Network Error: " + str + " - " + e.getMessage());
			} else {
				System.err.println(e.toString() + " - " + e.getMessage());
			}
			showConnectionStatus("Network error");
		} catch (Exception e) {
			String str = e.getMessage();
			if (str != null && str.length() != 0) {
				System.err.println("Error: " + str + " -" + e.getMessage());
			} else {
				System.err.println(e.toString() + "-" + e.getMessage());
			}
			showConnectionStatus("Network error");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.vncFrame.repaint();
		this.vncFrame.validate();
	}

	/*
	 * private JPanel createGestionPanel(){ JPanel gestionPanel = new JPanel(new GridLayout(4, 1)); JLabel applicationsListName = new JLabel("Aplicaciones"); JTextPane applicationsList = new
	 * JTextPane(); JTextArea applications = new JTextArea("app1\napp2\n"); applicationsList.add(applications); JLabel tasksListName = new JLabel("Tareas"); JTextPane tasksList = new JTextPane();
	 * JTextArea tasks = new JTextArea("t1\nt2\nt3\n"); tasksList.add(tasks); gestionPanel.add(applicationsListName); gestionPanel.add(applicationsList); gestionPanel.add(tasksListName);
	 * gestionPanel.add(tasks); return gestionPanel; }
	 */
	//
	// Create a VncCanvas instance.
	//

	/**
	 * Creates the canvas.
	 * 
	 * @param maxWidth
	 *            the max width
	 * @param maxHeight
	 *            the max height
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void createCanvas(int maxWidth, int maxHeight) throws IOException {
		// Determine if Java 2D API is available and use a special
		// version of VncCanvas if it is present.
		vc = null;
		try {
			// This throws ClassNotFoundException if there is no Java 2D API.
			Class cl = Class.forName("java.awt.Graphics2D");
			// If we could load Graphics2D class, then we can use VncCanvas2D.
			cl = Class.forName("VncCanvas2");
			Class[] argClasses = { this.getClass(), Integer.TYPE, Integer.TYPE };
			Constructor cstr = cl.getConstructor(argClasses);
			Object[] argObjects = { this, new Integer(maxWidth), new Integer(maxHeight) };
			vc = (VncCanvas) cstr.newInstance(argObjects);
		} catch (Exception e) {
			System.out.println("Warning: Java 2D API is not available");
		}

		// If we failed to create VncCanvas2D, use old VncCanvas.
		if (vc == null)
			vc = new VncCanvas(this, maxWidth, maxHeight);
	}

	//
	// Process RFB socket messages.
	// If the rfbThread is being stopped, ignore any exceptions,
	// otherwise rethrow the exception so it can be handled.
	//

	/**
	 * Process normal protocol.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	void processNormalProtocol() throws Exception {
		try {
			vc.processNormalProtocol();
		} catch (Exception e) {
			if (rfbThread == null) {
				System.out.println("Ignoring RFB socket exceptions" + " because applet is stopping");
			} else {
				throw e;
			}
		}
	}

	//
	// Connect to the RFB server and authenticate the user.
	//

	/**
	 * Connect and authenticate.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	void connectAndAuthenticate() throws Exception {
		showConnectionStatus("Inicializando...");
		if (inSeparateFrame) {
			vncFrame.show();
		} else {
			validate();
		}

		showConnectionStatus("Conectando a " + host + ", port " + port + "...");

		rfb = new RfbProto(host, port, this);
		showConnectionStatus("Conectando al servidor");

		rfb.readVersionMsg();
		// showConnectionStatus("RFB server supports protocol version "
		// + rfb.serverMajor + "." + rfb.serverMinor);

		rfb.writeVersionMsg();
		// showConnectionStatus("Using RFB protocol version " + rfb.clientMajor
		// + "." + rfb.clientMinor);

		int secType = rfb.negotiateSecurity();
		int authType;
		if (secType == RfbProto.SecTypeTight) {
			// showConnectionStatus("Enabling TightVNC protocol extensions");
			rfb.setupTunneling();
			authType = rfb.negotiateAuthenticationTight();
		} else {
			authType = secType;
		}

		switch (authType) {
		case RfbProto.AuthNone:
			showConnectionStatus("No authentication needed");
			rfb.authenticateNone();
			break;
		case RfbProto.AuthVNC:
			showConnectionStatus("Performing standard VNC authentication");
			if (passwordParam != null) {
				rfb.authenticateVNC(passwordParam);
			} else {
				String pw = askPassword();
				rfb.authenticateVNC(pw);
			}
			break;
		default:
			throw new Exception("Unknown authentication scheme " + authType);
		}
		showConnectionStatus("Conectado");
	}

	//
	// Show a message describing the connection status.
	// To hide the connection status label, use (msg == null).
	//

	/**
	 * Show connection status.
	 * 
	 * @param msg
	 *            the msg
	 */
	void showConnectionStatus(String msg, boolean... error) {
		/*
		 * if (msg == null) { if (vncContainer.isAncestorOf(connStatusLabel)) { vncContainer.remove(connStatusLabel); } connStatusLabel.setText(VNC_STATUS+"desconectado"); return; }
		 */

		/*
		 * if (connStatusLabel == null) { connStatusLabel = new Label("Status: " + msg); connStatusLabel.setFont(new Font("Helvetica", Font.PLAIN, 12)); } else {
		 */
		if (msg != null) {
			connStatusLabel.setText(VNC_STATUS + msg);
			if (error != null && error.length > 0 && error[0]) {
				connStatusLabel.setForeground(Color.RED);				
			}
		}
		// }

		/*
		 * if (!vncContainer.isAncestorOf(connStatusLabel)) { GridBagConstraints gbc = new GridBagConstraints(); gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.fill = GridBagConstraints.HORIZONTAL;
		 * gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.insets = new Insets(20, 30, 20, 30); gridbag.setConstraints(connStatusLabel, gbc);
		 * vncContainer.add(connStatusLabel); }
		 */

		validate();
	}

	//
	// Show an authentication panel.
	//

	/**
	 * Ask password.
	 * 
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	String askPassword() throws Exception {
		showConnectionStatus(null);

		AuthPanel authPanel = new AuthPanel(this);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.ipadx = 100;
		gbc.ipady = 50;
		gridbag.setConstraints(authPanel, gbc);
		vncContainer.add(authPanel);

		validate();

		authPanel.moveFocusToDefaultField();
		String pw = authPanel.getPassword();
		vncContainer.remove(authPanel);

		return pw;
	}

	//
	// Do the rest of the protocol initialisation.
	//

	/**
	 * Do protocol initialisation.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void doProtocolInitialisation() throws IOException {
		rfb.writeClientInit();
		rfb.readServerInit();

		System.out.println("Desktop name is " + rfb.desktopName);
		System.out.println("Desktop size is " + rfb.framebufferWidth + " x " + rfb.framebufferHeight);

		setEncodings();

		showConnectionStatus(null);
	}

	//
	// Send current encoding list to the RFB server.
	//

	/** The encodings saved. */
	int[] encodingsSaved;
	
	/** The n encodings saved. */
	int nEncodingsSaved;

	/**
	 * Sets the encodings.
	 */
	void setEncodings() {
		setEncodings(false);
	}

	/**
	 * Auto select encodings.
	 */
	void autoSelectEncodings() {
		setEncodings(true);
	}

	/**
	 * Sets the encodings.
	 * 
	 * @param autoSelectOnly
	 *            the new encodings
	 */
	void setEncodings(boolean autoSelectOnly) {
		if (options == null || rfb == null || !rfb.inNormalProtocol)
			return;

		int preferredEncoding = options.preferredEncoding;
		if (preferredEncoding == -1) {
			long kbitsPerSecond = rfb.kbitsPerSecond();
			if (nEncodingsSaved < 1) {
				// Choose Tight or ZRLE encoding for the very first update.
				System.out.println("Using Tight/ZRLE encodings");
				preferredEncoding = RfbProto.EncodingTight;
			} else if (kbitsPerSecond > 2000 && encodingsSaved[0] != RfbProto.EncodingHextile) {
				// Switch to Hextile if the connection speed is above 2Mbps.
				System.out.println("Throughput " + kbitsPerSecond + " kbit/s - changing to Hextile encoding");
				preferredEncoding = RfbProto.EncodingHextile;
			} else if (kbitsPerSecond < 1000 && encodingsSaved[0] != RfbProto.EncodingTight) {
				// Switch to Tight/ZRLE if the connection speed is below 1Mbps.
				System.out.println("Throughput " + kbitsPerSecond + " kbit/s - changing to Tight/ZRLE encodings");
				preferredEncoding = RfbProto.EncodingTight;
			} else {
				// Don't change the encoder.
				if (autoSelectOnly)
					return;
				preferredEncoding = encodingsSaved[0];
			}
		} else {
			// Auto encoder selection is not enabled.
			if (autoSelectOnly)
				return;
		}

		int[] encodings = new int[20];
		int nEncodings = 0;

		encodings[nEncodings++] = preferredEncoding;
		if (options.useCopyRect) {
			encodings[nEncodings++] = RfbProto.EncodingCopyRect;
		}

		if (preferredEncoding != RfbProto.EncodingTight) {
			encodings[nEncodings++] = RfbProto.EncodingTight;
		}
		if (preferredEncoding != RfbProto.EncodingZRLE) {
			encodings[nEncodings++] = RfbProto.EncodingZRLE;
		}
		if (preferredEncoding != RfbProto.EncodingHextile) {
			encodings[nEncodings++] = RfbProto.EncodingHextile;
		}
		if (preferredEncoding != RfbProto.EncodingZlib) {
			encodings[nEncodings++] = RfbProto.EncodingZlib;
		}
		if (preferredEncoding != RfbProto.EncodingCoRRE) {
			encodings[nEncodings++] = RfbProto.EncodingCoRRE;
		}
		if (preferredEncoding != RfbProto.EncodingRRE) {
			encodings[nEncodings++] = RfbProto.EncodingRRE;
		}

		if (options.compressLevel >= 0 && options.compressLevel <= 9) {
			encodings[nEncodings++] = RfbProto.EncodingCompressLevel0 + options.compressLevel;
		}
		if (options.jpegQuality >= 0 && options.jpegQuality <= 9) {
			encodings[nEncodings++] = RfbProto.EncodingQualityLevel0 + options.jpegQuality;
		}

		if (options.requestCursorUpdates) {
			encodings[nEncodings++] = RfbProto.EncodingXCursor;
			encodings[nEncodings++] = RfbProto.EncodingRichCursor;
			if (!options.ignoreCursorUpdates)
				encodings[nEncodings++] = RfbProto.EncodingPointerPos;
		}

		encodings[nEncodings++] = RfbProto.EncodingLastRect;
		encodings[nEncodings++] = RfbProto.EncodingNewFBSize;

		boolean encodingsWereChanged = false;
		if (nEncodings != nEncodingsSaved) {
			encodingsWereChanged = true;
		} else {
			for (int i = 0; i < nEncodings; i++) {
				if (encodings[i] != encodingsSaved[i]) {
					encodingsWereChanged = true;
					break;
				}
			}
		}

		if (encodingsWereChanged) {
			try {
				rfb.writeSetEncodings(encodings, nEncodings);
				if (vc != null) {
					vc.softCursorFree();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			encodingsSaved = encodings;
			nEncodingsSaved = nEncodings;
		}
	}

	//
	// setCutText() - send the given cut text to the RFB server.
	//

	/**
	 * Sets the cut text.
	 * 
	 * @param text
	 *            the new cut text
	 */
	void setCutText(String text) {
		try {
			if (rfb != null && rfb.inNormalProtocol) {
				rfb.writeClientCutText(text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// Order change in session recording status. To stop recording, pass
	// null in place of the fname argument.
	//

	/**
	 * Sets the recording status.
	 * 
	 * @param fname
	 *            the new recording status
	 */
	void setRecordingStatus(String fname) {
		synchronized (recordingSync) {
			sessionFileName = fname;
			recordingStatusChanged = true;
		}
	}

	//
	// Start or stop session recording. Returns true if this method call
	// causes recording of a new session.
	//

	/**
	 * Check recording status.
	 * 
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	boolean checkRecordingStatus() throws IOException {
		synchronized (recordingSync) {
			if (recordingStatusChanged) {
				recordingStatusChanged = false;
				if (sessionFileName != null) {
					startRecording();
					return true;
				} else {
					stopRecording();
				}
			}
		}
		return false;
	}

	//
	// Start session recording.
	//

	/**
	 * Start recording.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void startRecording() throws IOException {
		synchronized (recordingSync) {
			if (!recordingActive) {
				// Save settings to restore them after recording the session.
				cursorUpdatesDef = options.choices[options.cursorUpdatesIndex].getSelectedItem();
				eightBitColorsDef = options.choices[options.eightBitColorsIndex].getSelectedItem();
				// Set options to values suitable for recording.
				options.choices[options.cursorUpdatesIndex].select("Disable");
				options.choices[options.cursorUpdatesIndex].setEnabled(false);
				options.setEncodings();
				options.choices[options.eightBitColorsIndex].select("No");
				options.choices[options.eightBitColorsIndex].setEnabled(false);
				options.setColorFormat();
			} else {
				rfb.closeSession();
			}

			System.out.println("Recording the session in " + sessionFileName);
			rfb.startSession(sessionFileName);
			recordingActive = true;
		}
	}

	//
	// Stop session recording.
	//

	/**
	 * Stop recording.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void stopRecording() throws IOException {
		synchronized (recordingSync) {
			if (recordingActive) {
				// Restore options.
				options.choices[options.cursorUpdatesIndex].select(cursorUpdatesDef);
				options.choices[options.cursorUpdatesIndex].setEnabled(true);
				options.setEncodings();
				options.choices[options.eightBitColorsIndex].select(eightBitColorsDef);
				options.choices[options.eightBitColorsIndex].setEnabled(true);
				options.setColorFormat();

				rfb.closeSession();
				System.out.println("Session recording stopped.");
			}
			sessionFileName = null;
			recordingActive = false;
		}
	}

	//
	// readParameters() - read parameters from the html source or from the
	// command line. On the command line, the arguments are just a sequence of
	// param_name/param_value pairs where the names and values correspond to
	// those expected in the html applet tag source.
	//

	/**
	 * Configure.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public void configure(String host, int port) {
		this.host = host;
		if (host == null) {
			host = getCodeBase().getHost();
			if (host.equals("")) {
				fatalError("HOST parameter not specified");
			}
		}

		this.port = port;
		if (this.port == 0) {
			this.port = 5900;
		}

		// Read "ENCPASSWORD" or "PASSWORD" parameter if specified.
		// readPasswordParameters();

		String str;
		if (inAnApplet) {
			str = readParameter("Open New Window", false);
			if (str != null && str.equalsIgnoreCase("Yes"))
				inSeparateFrame = true;
		}

		// "Show Controls" set to "No" disables button panel.
		showControls = true;
		// str = readParameter("Show Controls", false);
		// if (str != null && str.equalsIgnoreCase("No"))
		// showControls = false;

		// "Offer Relogin" set to "No" disables "Login again" and "Close
		// window" buttons under error messages in applet mode.
		offerRelogin = true;
		/*
		 * str = readParameter("Offer Relogin", false); if (str != null && str.equalsIgnoreCase("No")) offerRelogin = false;
		 */

		// Do we continue showing desktop on remote disconnect?
		showOfflineDesktop = false;
		/*
		 * str = readParameter("Show Offline Desktop", false); if (str != null && str.equalsIgnoreCase("Yes")) showOfflineDesktop = true;
		 */

		// Fine tuning options.
		/*
		 * deferScreenUpdates = readIntParameter("Defer screen updates", 20); deferCursorUpdates = readIntParameter("Defer cursor updates", 10); deferUpdateRequests =
		 * readIntParameter("Defer update requests", 0);
		 * 
		 * // Debugging options. debugStatsExcludeUpdates = readIntParameter("DEBUG_XU", 0); debugStatsMeasureUpdates = readIntParameter("DEBUG_CU", 0);
		 * 
		 * // SocketFactory. socketFactory = readParameter("SocketFactory", false);
		 */

		deferScreenUpdates = 20;
		deferCursorUpdates = 10;
		deferUpdateRequests = 0;

		// Debugging options.
		debugStatsExcludeUpdates = 0;
		debugStatsMeasureUpdates = 0;

		// SocketFactory.
		socketFactory = null;
	}

	//
	// Read password parameters. If an "ENCPASSWORD" parameter is set,
	// then decrypt the password into the passwordParam string. Otherwise,
	// try to read the "PASSWORD" parameter directly to passwordParam.
	//

	/**
	 * Read password parameters.
	 */
	private void readPasswordParameters() {
		String encPasswordParam = readParameter("ENCPASSWORD", false);
		if (encPasswordParam == null) {
			passwordParam = readParameter("PASSWORD", false);
		} else {
			// ENCPASSWORD is hexascii-encoded. Decode.
			byte[] pw = { 0, 0, 0, 0, 0, 0, 0, 0 };
			int len = encPasswordParam.length() / 2;
			if (len > 8)
				len = 8;
			for (int i = 0; i < len; i++) {
				String hex = encPasswordParam.substring(i * 2, i * 2 + 2);
				Integer x = new Integer(Integer.parseInt(hex, 16));
				pw[i] = x.byteValue();
			}
			// Decrypt the password.
			byte[] key = { 23, 82, 107, 6, 35, 78, 88, 7 };
			DesCipher des = new DesCipher(key);
			des.decrypt(pw, 0, pw, 0);
			passwordParam = new String(pw);
		}
	}

	/**
	 * Read parameter.
	 * 
	 * @param name
	 *            the name
	 * @param required
	 *            the required
	 * @return the string
	 */
	public String readParameter(String name, boolean required) {
		if (inAnApplet) {
			String s = getParameter(name);
			if ((s == null) && required) {
				fatalError(name + " parameter not specified");
			}
			return s;
		}

		for (int i = 0; i < mainArgs.length; i += 2) {
			if (mainArgs[i].equalsIgnoreCase(name)) {
				try {
					return mainArgs[i + 1];
				} catch (Exception e) {
					if (required) {
						fatalError(name + " parameter not specified");
					}
					return null;
				}
			}
		}
		if (required) {
			fatalError(name + " parameter not specified");
		}
		return null;
	}

	/**
	 * Read int parameter.
	 * 
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * @return the int
	 */
	int readIntParameter(String name, int defaultValue) {
		String str = readParameter(name, false);
		int result = defaultValue;
		if (str != null) {
			try {
				result = Integer.parseInt(str);
			} catch (NumberFormatException e) {
			}
		}
		return result;
	}

	//
	// moveFocusToDesktop() - move keyboard focus either to VncCanvas.
	//

	/**
	 * Move focus to desktop.
	 */
	void moveFocusToDesktop() {
		if (vncContainer != null) {
			if (vc != null && vncContainer.isAncestorOf(vc))
				vc.requestFocus();
		}
	}

	//
	// disconnect() - close connection to server.
	//

	/**
	 * Disconnect.
	 */
	synchronized public void disconnect() {
		System.out.println("Disconnecting");

		if (vc != null) {
			double sec = (System.currentTimeMillis() - vc.statStartTime) / 1000.0;
			double rate = Math.round(vc.statNumUpdates / sec * 100) / 100.0;
			int nRealRects = vc.statNumPixelRects;
			int nPseudoRects = vc.statNumTotalRects - vc.statNumPixelRects;
			System.out.println("Updates received: " + vc.statNumUpdates + " (" + nRealRects + " rectangles + " + nPseudoRects + " pseudo), " + rate + " updates/sec");
			int numRectsOther = nRealRects - vc.statNumRectsTight - vc.statNumRectsZRLE - vc.statNumRectsHextile - vc.statNumRectsRaw - vc.statNumRectsCopy;
			System.out.println("Rectangles:" + " Tight=" + vc.statNumRectsTight + "(JPEG=" + vc.statNumRectsTightJPEG + ") ZRLE=" + vc.statNumRectsZRLE + " Hextile=" + vc.statNumRectsHextile
					+ " Raw=" + vc.statNumRectsRaw + " CopyRect=" + vc.statNumRectsCopy + " other=" + numRectsOther);

			int raw = vc.statNumBytesDecoded;
			int compressed = vc.statNumBytesEncoded;
			if (compressed > 0) {
				double ratio = Math.round((double) raw / compressed * 1000) / 1000.0;
				System.out.println("Pixel data: " + vc.statNumBytesDecoded + " bytes, " + vc.statNumBytesEncoded + " compressed, ratio " + ratio);
			}
		}

		if (rfb != null && !rfb.closed())
			rfb.close();
		options.dispose();
		clipboard.dispose();
		if (rec != null)
			rec.dispose();

		if (inAnApplet) {
			showMessage("Disconnected");
		} else {
			System.exit(0);
		}
	}

	//
	// fatalError() - print out a fatal error message.
	// FIXME: Do we really need two versions of the fatalError() method?
	//

	/**
	 * Fatal error.
	 * 
	 * @param str
	 *            the str
	 */
	synchronized public void fatalError(String str) {
		System.out.println(str);

		if (inAnApplet) {
			// vncContainer null, applet not inited,
			// can not present the error to the user.
			Thread.currentThread().stop();
		} else {
			System.exit(1);
		}
	}

	/**
	 * Fatal error.
	 * 
	 * @param str
	 *            the str
	 * @param e
	 *            the e
	 */
	synchronized public void fatalError(String str, Exception e) {

		if (rfb != null && rfb.closed()) {
			// Not necessary to show error message if the error was caused
			// by I/O problems after the rfb.close() method call.
			System.out.println("RFB thread finished");
			return;
		}

		System.out.println(str);
		e.printStackTrace();

		if (rfb != null)
			rfb.close();

		if (inAnApplet) {
			showMessage(str);
		} else {
			System.exit(1);
		}
	}

	//
	// Show message text and optionally "Relogin" and "Close" buttons.
	//

	/**
	 * Show message.
	 * 
	 * @param msg
	 *            the msg
	 */
	void showMessage(String msg) {
		vncContainer.removeAll();

		Label errLabel = new Label(msg, Label.CENTER);
		errLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));

		if (offerRelogin) {

			Panel gridPanel = new Panel(new GridLayout(0, 1));
			Panel outerPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			outerPanel.add(gridPanel);
			vncContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 16));
			vncContainer.add(outerPanel);
			Panel textPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
			textPanel.add(errLabel);
			gridPanel.add(textPanel);
			gridPanel.add(new ReloginPanel(this));

		} else {

			vncContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 30));
			vncContainer.add(errLabel);

		}

		validate();
	}

	//
	// Stop the applet.
	// Main applet thread will terminate on first exception
	// after seeing that rfbThread has been set to null.
	//

	/* (non-Javadoc)
	 * @see java.applet.Applet#stop()
	 */
	public void stop() {
		System.out.println("Stopping applet");
		rfbThread = null;
		vncFrame = new JPanel();
		vncFrame.repaint();
		vncFrame.validate();
	}

	//
	// This method is called before the applet is destroyed.
	//

	/* (non-Javadoc)
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		System.out.println("Destroying applet");

		vncContainer.removeAll();
		options.dispose();
		clipboard.dispose();
		if (rec != null)
			rec.dispose();
		if (rfb != null && !rfb.closed())
			rfb.close();
	}

	//
	// Start/stop receiving mouse events.
	//

	/**
	 * Enable input.
	 * 
	 * @param enable
	 *            the enable
	 */
	public void enableInput(boolean enable) {
		vc.enableInput(enable);
	}

}
