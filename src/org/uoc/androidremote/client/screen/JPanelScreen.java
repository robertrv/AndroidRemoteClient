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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class JPanelScreen.
 */
public class JPanelScreen extends JPanel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The coef. */
    public float coef = 1;
    
    /** The orig x. */
    double origX;
    
    /** The orig y. */
    double origY;
    
    /** The landscape. */
    boolean landscape;
    
    /** The size. */
    Dimension size = null;
    
    /** The image. */
    Image image = null;
    
    /**
	 * Instantiates a new j panel screen.
	 */
    public JPanelScreen() {
        this.setFocusable(true);
    }
    
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
    public void handleNewImage(Dimension size, Image image, boolean landscape) {
        this.landscape = landscape;
        this.size = size;
        this.image = image;
        repaint();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        if(size == null)
            return;
        if(size.height == 0)
            return;
        g.clearRect(0, 0, getWidth(), getHeight());
        double width = Math.min(getWidth(), size.width*getHeight()/size.height);
        coef = (float)width / (float)size.width;
        double height = width*size.height/size.width;
        origX = (getWidth() - width) / 2;
        origY = (getHeight() - height) / 2;
        g.drawImage(image, (int)origX, (int)origY, (int)width, (int)height, this);
    }
    
    
    /**
	 * Gets the raw point.
	 * 
	 * @param p1
	 *            the p1
	 * @return the raw point
	 */
    public Point getRawPoint(Point p1) {
        Point p2 = new Point();
        p2.x = (int)((p1.x - origX)/coef);
        p2.y = (int)((p1.y - origY)/coef);
        return p2;
    }
    
}
