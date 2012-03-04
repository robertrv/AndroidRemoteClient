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
package org.uoc.androidremote.client.screen;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving getScreen events. The class that is
 * interested in processing a getScreen event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addGetScreenListener<code> method. When
 * the getScreen event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see GetScreenEvent
 */
public interface GetScreenListener {
    
    /**
	 * Handle new image.
	 * 
	 * @param size
	 *            the size
	 * @param image
	 *            the image
	 * @param landscape
	 *            the landscape
	 */
    public void handleNewImage(Dimension size, BufferedImage image,
            boolean landscape);
}