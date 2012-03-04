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

import javax.swing.tree.DefaultMutableTreeNode;

// TODO: Auto-generated Javadoc
/**
 * The Class LazyMutableTreeNode.
 */
public abstract class LazyMutableTreeNode extends DefaultMutableTreeNode {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The _loaded. */
	protected boolean _loaded = false;

	/**
	 * Instantiates a new lazy mutable tree node.
	 */
	public LazyMutableTreeNode() {
		super();
	}

	/**
	 * Instantiates a new lazy mutable tree node.
	 * 
	 * @param userObject
	 *            the user object
	 */
	public LazyMutableTreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * Instantiates a new lazy mutable tree node.
	 * 
	 * @param userObject
	 *            the user object
	 * @param allowsChildren
	 *            the allows children
	 */
	public LazyMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.DefaultMutableTreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		synchronized (this) {
			if (!_loaded) {
				_loaded = true;
				initChildren();
			}
		}
		return super.getChildCount();
	}

	/**
	 * Clear.
	 */
	public void clear() {
		removeAllChildren();
		_loaded = false;
	}

	/**
	 * Checks if is loaded.
	 * 
	 * @return true, if is loaded
	 */
	public boolean isLoaded() {
		return _loaded;
	}

	/**
	 * Inits the children.
	 */
	protected abstract void initChildren();

}