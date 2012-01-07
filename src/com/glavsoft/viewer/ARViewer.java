// Copyright (C) 2010, 2011 GlavSoft LLC.
// All rights reserved.
//
//-------------------------------------------------------------------------
// This file is part of the TightVNC software.  Please visit our Web site:
//
//                       http://www.tightvnc.com/
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//-------------------------------------------------------------------------
//

package com.glavsoft.viewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import com.glavsoft.exceptions.AuthenticationFailedException;
import com.glavsoft.exceptions.FatalException;
import com.glavsoft.exceptions.TransportException;
import com.glavsoft.exceptions.UnsupportedProtocolVersionException;
import com.glavsoft.exceptions.UnsupportedSecurityTypeException;
import com.glavsoft.rfb.IPasswordRetriever;
import com.glavsoft.rfb.client.FramebufferUpdateRequestMessage;
import com.glavsoft.rfb.client.KeyEventMessage;
import com.glavsoft.rfb.client.SetEncodingsMessage;
import com.glavsoft.rfb.encoding.decoder.DecodersContainer;
import com.glavsoft.rfb.protocol.MessageQueue;
import com.glavsoft.rfb.protocol.Protocol;
import com.glavsoft.rfb.protocol.ProtocolSettings;
import com.glavsoft.rfb.protocol.ReceiverTask;
import com.glavsoft.rfb.protocol.SenderTask;
import com.glavsoft.transport.Transport;
import com.glavsoft.utils.Strings;
import com.glavsoft.viewer.cli.Parser;
import com.glavsoft.viewer.swing.ClipboardControllerImpl;
import com.glavsoft.viewer.swing.KeyEventListener;
import com.glavsoft.viewer.swing.ModifierButtonEventListener;
import com.glavsoft.viewer.swing.ParametersHandler;
import com.glavsoft.viewer.swing.ParametersHandler.ConnectionParams;
import com.glavsoft.viewer.swing.Surface;
import com.glavsoft.viewer.swing.Utils;
import com.glavsoft.viewer.swing.gui.ConnectionDialog;
import com.glavsoft.viewer.swing.gui.OptionsDialog;
import com.glavsoft.viewer.swing.gui.PasswordDialog;

/**
 * This is a forced copy of the Viewer class of tightvnc java code due the lack
 * of simple way to use this class from another application, like we need here!
 * 
 * We will try to change the minimum part so we can update on future releases
 * 
 * @author robertrv [at] gmail.com
 * 
 */
@SuppressWarnings("serial")
public class ARViewer extends JApplet implements Runnable, IViewerSessionManager,
		WindowListener {
	
	public static final String ARG_LOCAL_POINTER = "LocalPointer";
	public static final String ARG_SCALING_FACTOR = "ScalingFactor";
	public static final String ARG_COLOR_DEPTH = "ColorDepth";
	public static final String ARG_JPEG_IMAGE_QUALITY = "JpegImageQuality";
	public static final String ARG_COMPRESSION_LEVEL = "CompressionLevel";
	public static final String ARG_ENCODING = "Encoding";
	public static final String ARG_SHARE_DESKTOP = "ShareDesktop";
	public static final String ARG_ALLOW_COPY_RECT = "AllowCopyRect";
	public static final String ARG_VIEW_ONLY = "ViewOnly";
	public static final String ARG_SHOW_CONTROLS = "ShowControls";
	public static final String ARG_OPEN_NEW_WINDOW = "OpenNewWindow";
	public static final String ARG_PASSWORD = "password";
	public static final String ARG_PORT = "port";
	public static final String ARG_HOST = "host";
	public static final String ARG_HELP = "help";
	public static final int DEFAULT_PORT = 5900;

	public static Logger logger = Logger.getLogger("com.glavsoft");

	/**
	 * Ask user for password if needed
	 */
	private static class PasswordChooser implements IPasswordRetriever {
		private final String passwordPredefined;
		private final ParametersHandler.ConnectionParams connectionParams;
		PasswordDialog passwordDialog;
		private final JFrame owner;
		private final WindowListener onClose;

		/**
		 * @param passwordPredefined
		 * @param connectionParams
		 * @param onClose
		 *            TODO
		 */
		private PasswordChooser(String passwordPredefined,
				ParametersHandler.ConnectionParams connectionParams,
				JFrame owner, WindowListener onClose) {
			this.passwordPredefined = passwordPredefined;
			this.connectionParams = connectionParams;
			this.owner = owner;
			this.onClose = onClose;
		}

		@Override
		public String getPassword() {
			return Strings.isTrimmedEmpty(passwordPredefined) ? getPasswordFromGUI()
					: passwordPredefined;
		}

		private String getPasswordFromGUI() {
			if (null == passwordDialog) {
				passwordDialog = new PasswordDialog(owner, onClose);
			}
			passwordDialog.setServerHostName(connectionParams.hostName);
			passwordDialog.setVisible(true);
			return passwordDialog.getPassword();
		}
	}

	public static void main(String[] args) {
		Parser parser = new Parser();
		ParametersHandler.completeParserOptions(parser);

		parser.parse(args);
		if (parser.isSet(ARG_HELP)) {
			printUsage(parser.optionsUsage());
			System.exit(0);
		}
		ARViewer viewer = new ARViewer(parser);
		SwingUtilities.invokeLater(viewer);
	}

	public static void printUsage(String additional) {
		System.out
				.println("Usage: java -jar (progfilename) [hostname [port_number]] [Options]\n"
						+ "    or\n"
						+ " java -jar (progfilename) [Options]\n"
						+ "    or\n java -jar (progfilename) -help\n    to view this help\n\n"
						+ "Where Options are:\n"
						+ additional
						+ "\nOptions format: -optionName=optionValue. Ex. -host=localhost -port=5900 -viewonly=yes\n"
						+ "Both option name and option value are case insensitive.");
	}

	private final ParametersHandler.ConnectionParams connectionParams;
	private String passwordFromParams;
	private Socket workingSocket;
	private Protocol workingProtocol;
	private JFrame containerFrame;
	private SenderTask senderTask;
	private ReceiverTask receiverTask;
	private volatile boolean forceConnectionDialog;
	boolean isSeparateFrame = true;
	boolean isApplet = true;
	boolean showControls = true;
	private Surface surface;
	public final ProtocolSettings settings = ProtocolSettings
			.getDefaultSettings();
	private OptionsDialog optionsDialog;
	private final DecodersContainer decoders = new DecodersContainer();
	private ClipboardControllerImpl clipboardController;
	private boolean tryAgain;
	private boolean isAppletStopped = false;
	private Map<String,String> unstructuredSettings;

	public ARViewer() {
		connectionParams = new ParametersHandler.ConnectionParams();
	}

	/* XXX AndroidRemote Changes !*/
	private ARViewer(Parser parser) {
		this();
		initializeWithParser(parser);
	}

	private void initializeWithParser(Parser parser) {
		ParametersHandler.completeSettingsFromCLI(parser, connectionParams,
				settings);
		showControls = ParametersHandler.showControls;
		passwordFromParams = parser.getValueFor(ARG_PASSWORD);
		logger.info("TightVNC Viewer");
		isApplet = false;
	}
	/**
	 * Useful to receive notifications or information when there is any action 
	 * inside the viewer
	 * @param logHandler
	 */
	public void addLoggerHandler(Handler logHandler) {
		logger.addHandler(logHandler);
	}
	
	/**
	 * a method which configures current viewer with new settings. 
	 * @param args
	 */
	public void configure(Map<String,String> settings) {
		unstructuredSettings = settings;
		 
		String[] cliGeneratedArgs = new String[settings.size()];
		
		Parser parser = new Parser();
		int position = 0;
		for (Map.Entry<String, String> setting : settings.entrySet()) {
			cliGeneratedArgs[position++] = '-' + setting.getKey() + '='
					+ setting.getValue();
		}
		
		ParametersHandler.completeParserOptions(parser);

		parser.parse(cliGeneratedArgs);
		
		initializeWithParser(parser);
		isApplet = true;
		ParametersHandler.isSeparateFrame = false;
	}
	
	@Override
	public String getParameter(String name) {
		return unstructuredSettings.get(name);
	}

	/* XXX End of AndroidRemote Changes !*/

	@Override
	public synchronized void stopTasksAndRunNewSession(String message) {
		if (null == senderTask)
			return;
		stopTasks();
		// start new session
		forceConnectionDialog = connectionParams.isHostNameEmpty();
		SwingUtilities.invokeLater(this);
	}

	private synchronized void stopTasks() {
		if (senderTask != null) {
			senderTask.stop();
		}
		if (receiverTask != null) {
			receiverTask.stop();
		}
		senderTask = null;
		receiverTask = null;
		if (workingSocket != null && workingSocket.isConnected()) {
			try {
				workingSocket.close();
			} catch (IOException e) { /* nop */
			}
		}
		if (containerFrame != null) {
			containerFrame.dispose();
			containerFrame = null;
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (e != null && e.getComponent() != null) {
			e.getWindow().setVisible(false);
		}
		closeApp();
	}

	/**
	 * Closes App(lication) or stops App(let).
	 */
	private void closeApp() {
		stopTasks();
		tryAgain = false;
		if (isApplet) {
			logger.severe("Applet is stopped.");
			isAppletStopped = true;
			repaint();
		} else {
			System.exit(0);
		}
	}

	@Override
	public void paint(Graphics g) {
		if (!isAppletStopped) {
			super.paint(g);
		} else {
			getContentPane().removeAll();
			g.clearRect(0, 0, getWidth(), getHeight());
			g.drawString("Disconnected", 10, 20);
		}
	}

	@Override
	public void destroy() {
		stopTasks();
		super.destroy();
	}

	@Override
	public void init() {
		ParametersHandler.completeSettingsFromApplet(this, connectionParams,
				settings);
		showControls = ParametersHandler.showControls;
		isSeparateFrame = ParametersHandler.isSeparateFrame;
		passwordFromParams = getParameter(ARG_PASSWORD);
		isApplet = true;

		repaint();
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void start() {
		setSurfaceToHandleKbdFocus();
		super.start();
	}

	@Override
	public void run() {
		tryAgain = true;
		while (tryAgain) {
			workingSocket = connectToHost(connectionParams);
			if (null == workingSocket) {
				closeApp();
				break;
			}
			decoders.initDecodersWhenNeeded(settings.encodings);
			logger.info("Connected");

			try {
				Transport.Reader reader = new Transport.Reader(
						workingSocket.getInputStream());
				Transport.Writer writer = new Transport.Writer(
						workingSocket.getOutputStream());

				workingProtocol = new Protocol(reader, writer,
						new PasswordChooser(passwordFromParams,
								connectionParams, containerFrame, this),
						settings);
				workingProtocol.handshake();
				workingProtocol.negotiateAboutSecurityType();
				workingProtocol.authenticate();
				workingProtocol.clientAndServerInit();

				workingProtocol.set32bppPixelFormat();
				settings.setPixelFormat(workingProtocol.getPixelFormat());

				MessageQueue senderQueue = new MessageQueue();
				workingProtocol.startNormalHandling(senderQueue);
				decoders.resetDecoders();
				surface = new Surface(workingProtocol.getFbWidth(),
						workingProtocol.getFbHeight(), senderQueue, settings);
				containerFrame = createContainer(workingProtocol.getFbWidth(),
						workingProtocol.getFbHeight(), senderQueue,
						workingProtocol.getRemoteDesktopName());

				senderTask = new SenderTask(senderQueue, writer, this);
				new Thread(senderTask).start();

				clipboardController = new ClipboardControllerImpl(senderQueue);
				clipboardController.setEnabled(settings
						.isAllowClipboardTransfer());
				receiverTask = new ReceiverTask(reader,
						workingProtocol.getFbWidth(),
						workingProtocol.getFbHeight(),
						workingProtocol.getPixelFormat(), surface,
						clipboardController, this, decoders, senderQueue);
				new Thread(receiverTask).start();
				tryAgain = false;
			} catch (UnsupportedProtocolVersionException e) {
				showReconnectDialog("Unsupported Protocol Version",
						e.getMessage());
				logger.severe(e.getMessage());
			} catch (UnsupportedSecurityTypeException e) {
				showReconnectDialog("Unsupported Security Type", e.getMessage());
				logger.severe(e.getMessage());
			} catch (AuthenticationFailedException e) {
				passwordFromParams = null;
				showReconnectDialog("Authentication Failed", e.getMessage());
				logger.severe(e.getMessage());
			} catch (TransportException e) {
				showReconnectDialog("Connection Error", "Connection Error"
						+ ": " + e.getMessage());
				logger.severe(e.getMessage());
			} catch (IOException e) {
				showReconnectDialog("Connection Error", "Connection Error"
						+ ": " + e.getMessage());
				logger.severe(e.getMessage());
			} catch (FatalException e) {
				showReconnectDialog("Connection Error", "Connection Error"
						+ ": " + e.getMessage());
				logger.severe(e.getMessage());
			}
		}
	}

	private JFrame createContainer(final int width, final int height,
			final MessageQueue messageQueue, final String title) {
		JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		outerPanel.add(surface);

		Container container;
		JFrame frame = null;
		JScrollPane scroller = new JScrollPane(outerPanel);
		surface.registerScroller(scroller);
		if (isSeparateFrame) {
			frame = new JFrame();
			// frame.add(this);
			if (!isApplet) {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
			frame.setTitle(title);
			frame.setLayout(new BorderLayout());
			frame.getContentPane().add(scroller, BorderLayout.CENTER);
			List<Image> icons = Utils.getIcons();
			if (icons.size() != 0) {
				frame.setIconImages(icons);
			}
			container = frame;
		} else {
			setLayout(new BorderLayout());
			getContentPane().add(scroller, BorderLayout.CENTER);
			container = this;
		}

		if (showControls) {
			createButtonsPanel(messageQueue, title, container);
		}

		if (isSeparateFrame) {
			frame.pack();
			container.setVisible(true);
		}
		container.validate();

		setSurfaceToHandleKbdFocus();
		return isSeparateFrame ? frame : null;
	}

	protected void createButtonsPanel(final MessageQueue messageQueue,
			final String title, Container container) {
		JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 1));

		Insets buttonsMargin = new Insets(2, 2, 2, 2);

		JButton optionsButton = new JButton(Utils.getButtonIcon("options"));
		optionsButton.setToolTipText("Set Options");
		optionsButton.setMargin(buttonsMargin);
		buttonBar.add(optionsButton);
		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showOptionsDialog(messageQueue);
				setSurfaceToHandleKbdFocus();
			}
		});

		JButton infoButton = new JButton(Utils.getButtonIcon("info"));
		infoButton.setToolTipText("Show connection info");
		infoButton.setMargin(buttonsMargin);
		buttonBar.add(infoButton);
		infoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showConnectionInfoMessage(title);
				setSurfaceToHandleKbdFocus();
			}
		});

		buttonBar.add(Box.createHorizontalStrut(10));
		JButton refreshButton = new JButton(Utils.getButtonIcon("refresh"));
		refreshButton.setToolTipText("Refresh screen");
		refreshButton.setMargin(buttonsMargin);
		buttonBar.add(refreshButton);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messageQueue.put(new FramebufferUpdateRequestMessage(0, 0,
						surface.getWidth(), surface.getHeight(), false));
				setSurfaceToHandleKbdFocus();
			}
		});

		buttonBar.add(Box.createHorizontalStrut(10));
		JButton ctrlAltDelButton = new JButton(
				Utils.getButtonIcon("ctrl-alt-del"));
		ctrlAltDelButton.setToolTipText("Send 'Ctrl-Alt-Del'");
		ctrlAltDelButton.setMargin(buttonsMargin);
		buttonBar.add(ctrlAltDelButton);
		ctrlAltDelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendCtrlAltDel(messageQueue);
				setSurfaceToHandleKbdFocus();
			}
		});
		JButton winButton = new JButton(Utils.getButtonIcon("win"));
		winButton.setToolTipText("Send 'Win' key as 'Ctrl-Esc'");
		winButton.setMargin(buttonsMargin);
		buttonBar.add(winButton);
		winButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendWinKey(messageQueue);
				setSurfaceToHandleKbdFocus();
			}
		});
		JToggleButton ctrlButton = new JToggleButton(
				Utils.getButtonIcon("ctrl"));
		ctrlButton.setMargin(buttonsMargin);
		buttonBar.add(ctrlButton);
		ctrlButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSurfaceToHandleKbdFocus();
			}
		});
		ctrlButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					messageQueue.put(new KeyEventMessage(
							KeyEventListener.K_CTRL_LEFT, true));
				} else {
					messageQueue.put(new KeyEventMessage(
							KeyEventListener.K_CTRL_LEFT, false));
				}
			}
		});
		JToggleButton altButton = new JToggleButton(Utils.getButtonIcon("alt"));
		altButton.setMargin(buttonsMargin);
		buttonBar.add(altButton);
		altButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					messageQueue.put(new KeyEventMessage(
							KeyEventListener.K_ALT_LEFT, true));
				} else {
					messageQueue.put(new KeyEventMessage(
							KeyEventListener.K_ALT_LEFT, false));
				}
			}
		});
		altButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSurfaceToHandleKbdFocus();
			}
		});
		ModifierButtonEventListener modifierButtonListener = new ModifierButtonEventListener();
		modifierButtonListener.addButton(KeyEvent.VK_CONTROL, ctrlButton);
		modifierButtonListener.addButton(KeyEvent.VK_ALT, altButton);
		surface.addModifierListener(modifierButtonListener);

		// JButton fileTransferButton = new
		// JButton(Utils.getButtonIcon("file-transfer"));
		// fileTransferButton.setMargin(buttonsMargin);
		// buttonBar.add(fileTransferButton);

		buttonBar.add(Box.createHorizontalStrut(10));

		JButton closeButton = new JButton(Utils.getButtonIcon("close"));
		closeButton.setToolTipText(isApplet ? "Disconnect" : "Close");
		closeButton.setMargin(buttonsMargin);
		closeButton.setAlignmentX(RIGHT_ALIGNMENT);
		buttonBar.add(closeButton);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeApp();
			}
		});

		container.add(buttonBar, BorderLayout.NORTH);
	}

	protected void setSurfaceToHandleKbdFocus() {
		if (surface != null && !surface.requestFocusInWindow()) {
			surface.requestFocus();
		}
	}

	protected void showReconnectDialog(String title, String message) {
		if (tryAgain) {
			if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
					containerFrame, message + "\nTry another connection?",
					title, JOptionPane.YES_NO_OPTION)) {
				closeApp();
			} else {
				forceConnectionDialog = !isApplet
						|| connectionParams.isHostNameEmpty();
			}
		}
	}

	private Socket connectToHost(final ConnectionParams connectionParams) {
		Socket socket = null;
		ConnectionDialog connectionDialog = null;
		boolean wasError = false;
		do {
			if (forceConnectionDialog || wasError
					|| connectionParams.isHostNameEmpty()
					|| -1 == connectionParams.portNumber) {
				forceConnectionDialog = false;
				if (null == connectionDialog) {
					connectionDialog = new ConnectionDialog(containerFrame,
							this, connectionParams.hostName,
							connectionParams.portNumber, optionsDialog,
							settings);
				}
				connectionDialog.setVisible(true);
				connectionParams.hostName = connectionDialog
						.getServerNameString();
				connectionParams.portNumber = connectionDialog.getPort();
			}
			logger.info("Connecting to host " + connectionParams.hostName + ":"
					+ connectionParams.portNumber);
			logger.info(String.valueOf(isApplet));
			try {
				socket = new Socket(connectionParams.hostName,
						connectionParams.portNumber);
				wasError = false;
			} catch (UnknownHostException e) {
				logger.severe("Unknown host: " + connectionParams.hostName);
				JOptionPane.showMessageDialog(null, "Unknown host: '"
						+ connectionParams.hostName + "'",
						"Couldn't connect to host", JOptionPane.ERROR_MESSAGE);
				wasError = true;
			} catch (IOException e) {
				logger.severe("Couldn't connect to: "
						+ connectionParams.hostName + ":"
						+ connectionParams.portNumber + ": " + e.getMessage());
				JOptionPane.showMessageDialog(null, "Couldn't connect to: '"
						+ connectionParams.hostName + "'\n" + e.getMessage(),
						"Couldn't connect to host", JOptionPane.ERROR_MESSAGE);
				wasError = true;
			}
			if (null == socket && !wasError) {
				logger.severe("Couldn't connect to: "
						+ connectionParams.hostName + ":"
						+ connectionParams.portNumber + ", socket is null");
				JOptionPane.showMessageDialog(null, "Couldn't connect to: '"
						+ connectionParams.hostName + "'",
						"Couldn't connect to host", JOptionPane.ERROR_MESSAGE);
				wasError = true;
			}
		} while (!isApplet && (connectionParams.isHostNameEmpty() || wasError));
		if (connectionDialog != null) {
			connectionDialog.dispose();
		}
		connectionDialog = null;
		return socket;
	}

	private void applySettings(MessageQueue messageQueue) {
		// TODO recreate Renderer on scale changed
		clipboardController.setEnabled(settings.isAllowClipboardTransfer());
		surface.enableUserInput(!settings.isViewOnly());
		surface.showCursor(settings.isShowRemoteCursor());
		decoders.initDecodersWhenNeeded(settings.encodings);
		SetEncodingsMessage setEncodingsMessage = new SetEncodingsMessage(
				settings.encodings);
		messageQueue.put(setEncodingsMessage);
		logger.fine("sent: " + setEncodingsMessage.toString());
	}

	private void showOptionsDialog(final MessageQueue messageQueue) {
		if (null == optionsDialog) {
			optionsDialog = new OptionsDialog(containerFrame);
		}
		optionsDialog.initControlsFromSettings(settings, false);
		optionsDialog.setVisible(true);
		if (optionsDialog.isOkPressed()) {
			applySettings(messageQueue);
		}
	}

	private void showConnectionInfoMessage(final String title) {
		StringBuilder message = new StringBuilder();
		message.append("Connected to: ").append(title).append("\n");
		message.append("Host: ").append(connectionParams.hostName)
				.append(" Port: ").append(connectionParams.portNumber)
				.append("\n\n");

		message.append("Desktop geometry: ")
				.append(String.valueOf(surface.getWidth())).append(" \u00D7 ") // multiplication
																				// sign
				.append(String.valueOf(surface.getHeight())).append("\n");
		message.append("Using depth: ")
				.append(String.valueOf(settings.getPixelFormat().depth))
				.append("\n");
		message.append("Current protocol version: ").append(
				settings.getProtocolVersion());
		if (settings.isTight()) {
			message.append("tight");
		}
		message.append("\n");

		JOptionPane.showMessageDialog(containerFrame, message.toString(),
				"VNC connection info", JOptionPane.INFORMATION_MESSAGE);
	}

	private void sendCtrlAltDel(final MessageQueue messageQueue) {
		messageQueue
				.put(new KeyEventMessage(KeyEventListener.K_CTRL_LEFT, true));
		messageQueue
				.put(new KeyEventMessage(KeyEventListener.K_ALT_LEFT, true));
		messageQueue.put(new KeyEventMessage(KeyEventListener.K_DELETE, true));
		messageQueue.put(new KeyEventMessage(KeyEventListener.K_DELETE, false));
		messageQueue
				.put(new KeyEventMessage(KeyEventListener.K_ALT_LEFT, false));
		messageQueue.put(new KeyEventMessage(KeyEventListener.K_CTRL_LEFT,
				false));
	}

	private void sendWinKey(final MessageQueue messageQueue) {
		messageQueue
				.put(new KeyEventMessage(KeyEventListener.K_CTRL_LEFT, true));
		messageQueue.put(new KeyEventMessage(KeyEventListener.K_ESCAPE, true));
		messageQueue.put(new KeyEventMessage(KeyEventListener.K_ESCAPE, false));
		messageQueue.put(new KeyEventMessage(KeyEventListener.K_CTRL_LEFT,
				false));
	}

	@Override
	public void windowOpened(WindowEvent e) { /* nop */
	}

	@Override
	public void windowClosed(WindowEvent e) { /* nop */
	}

	@Override
	public void windowIconified(WindowEvent e) { /* nop */
	}

	@Override
	public void windowDeiconified(WindowEvent e) { /* nop */
	}

	@Override
	public void windowActivated(WindowEvent e) { /* nop */
	}

	@Override
	public void windowDeactivated(WindowEvent e) { /* nop */
	}

}