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
package org.uoc.androidremote.client.usb.visualization;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.uoc.androidremote.client.structures.AndroidDevice;
import org.uoc.androidremote.client.structures.FileInfo;

import com.android.ddmlib.IDevice;

// TODO: Auto-generated Javadoc
/**
 * The Class JFrameExplorer.
 */
public class JFrameExplorer extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The jt. */
	JTree jt;
	
	/** The j split pane. */
	JSplitPane jSplitPane;
	
	/** The device. */
	IDevice device;
	
	/** The j list fichiers. */
	JList jListFichiers;
	
	/** The cache. */
	Map<String, List<FileInfo>> cache = new LinkedHashMap<String, List<FileInfo>>();

	/**
	 * The Class FileTreeNode.
	 */
	private class FileTreeNode extends DefaultMutableTreeNode {
		
		/** The fi. */
		FileInfo fi;

		/**
		 * Instantiates a new file tree node.
		 * 
		 * @param fi
		 *            the fi
		 */
		public FileTreeNode(FileInfo fi) {
			super(fi.name);
			this.fi = fi;
		}

	}

	/**
	 * The Class FolderTreeNode.
	 */
	private class FolderTreeNode extends LazyMutableTreeNode {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		
		/** The name. */
		String name;
		
		/** The path. */
		String path;

		/**
		 * Instantiates a new folder tree node.
		 * 
		 * @param name
		 *            the name
		 * @param path
		 *            the path
		 */
		public FolderTreeNode(String name, String path) {
			this.name = name;
			this.path = path;
		}

		/* (non-Javadoc)
		 * @see org.uoc.androidremote.client.usb.visualization.LazyMutableTreeNode#initChildren()
		 */
		@Override
		public void initChildren() {
			List<FileInfo> fileInfos = cache.get(path);
			if (fileInfos == null)
				fileInfos = new AndroidDevice(device).list(path);
			for (FileInfo fi : fileInfos) {
				if (fi.directory)
					add(new FolderTreeNode(fi.name, path + fi.name + "/"));
				// else
				// add(new FileTreeNode(fi));
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultMutableTreeNode#toString()
		 */
		public String toString() {
			return name;
		}

	}

	/**
	 * Instantiates a new j frame explorer.
	 * 
	 * @param device
	 *            the device
	 */
	public JFrameExplorer(IDevice device) {
		this.device = device;

		setTitle("Explorer");
		setLayout(new BorderLayout());

		jt = new JTree(new DefaultMutableTreeNode("Test"));
		jt.setModel(new DefaultTreeModel(new FolderTreeNode("Device", "/")));
		jt.setRootVisible(true);
		jt.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				if (tp == null)
					return;
				if (!(tp.getLastPathComponent() instanceof FolderTreeNode))
					return;
				FolderTreeNode node = (FolderTreeNode) tp.getLastPathComponent();
				displayFolder(node.path);
			}
		});

		JScrollPane jsp = new JScrollPane(jt);

		jListFichiers = new JList();
		jListFichiers.setListData(new Object[] {});

		jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp, new JScrollPane(jListFichiers));

		add(jSplitPane, BorderLayout.CENTER);
		setSize(640, 480);
		setLocationRelativeTo(null);

		jListFichiers.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = jListFichiers.locationToIndex(e.getPoint());
					ListModel dlm = jListFichiers.getModel();
					FileInfo item = (FileInfo) dlm.getElementAt(index);
					;
					launchFile(item);
				}
			}

		});
	}

	/**
	 * Display folder.
	 * 
	 * @param path
	 *            the path
	 */
	private void displayFolder(String path) {
		List<FileInfo> fileInfos = cache.get(path);
		if (fileInfos == null)
			fileInfos = new AndroidDevice(device).list(path);

		List<FileInfo> files = new Vector<FileInfo>();
		for (FileInfo fi2 : fileInfos) {
			if (fi2.directory)
				continue;
			files.add(fi2);
		}
		jListFichiers.setListData(files.toArray());

	}

	/**
	 * Launch file.
	 * 
	 * @param node
	 *            the node
	 */
	private void launchFile(FileInfo node) {
		try {
			File tempFile = node.downloadTemporary();
			Desktop.getDesktop().open(tempFile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
