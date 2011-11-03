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
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;

// TODO: Auto-generated Javadoc
/**
 * The Class ThreadGetScreen.
 */
public class ThreadGetScreen extends Thread {

    /** The image. */
    private BufferedImage image;
    
    /** The size. */
    private Dimension size;
    
    /** The device. */
    private IDevice device;
    
    /** The landscape. */
    private boolean landscape = false;
    
    /** The listener. */
    private GetScreenListener listener = null;

    /**
	 * Instantiates a new thread get screen.
	 * 
	 * @param device
	 *            the device
	 */
    public ThreadGetScreen(IDevice device) {
        super("Screen capture");
        this.device = device;
        image = null;
        size = new Dimension();
    }

    /**
	 * Gets the preferred size.
	 * 
	 * @return the preferred size
	 */
    public Dimension getPreferredSize() {
        return size;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
        do {
            try {
                boolean ok = fetchImage();
                if(!ok)
                    break;
            } catch (java.nio.channels.ClosedByInterruptException ciex) {
                break;
            } catch (IOException e) {
                System.err.println((new StringBuilder()).append(
                        "Exception fetching image: ").append(e.toString())
                        .toString());
            }

        } while (true);
    }

    /**
	 * Fetch image.
	 * 
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
    private boolean fetchImage() throws IOException {
        if (device == null) {
            // device not ready
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        // System.out.println("Getting initial screenshot through ADB");
        RawImage rawImage = null;
        synchronized (device) {
            rawImage = device.getScreenshot();
        }
        if (rawImage != null) {
            // System.out.println("screenshot through ADB ok");
            display(rawImage);
        } else {
            System.out.println("failed getting screenshot through ADB ok");
        }
        /*try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            return false;
        }*/

        return true;
    }

    /**
	 * Toogle orientation.
	 */
    public void toogleOrientation() {
        landscape = !landscape;
    }

    /**
	 * Display.
	 * 
	 * @param rawImage
	 *            the raw image
	 */
    public void display(RawImage rawImage) {
        int width2 = landscape ? rawImage.height : rawImage.width;
        int height2 = landscape ? rawImage.width : rawImage.height;
        if (image == null) {
            image = new BufferedImage(width2, height2,
                    BufferedImage.TYPE_INT_RGB);
            size.setSize(image.getWidth(), image.getHeight());
        } else {
            if (image.getHeight() != height2 || image.getWidth() != width2) {
                image = new BufferedImage(width2, height2,
                        BufferedImage.TYPE_INT_RGB);
                size.setSize(image.getWidth(), image.getHeight());
            }
        }
        int index = 0;
        int indexInc = rawImage.bpp >> 3;
        for (int y = 0; y < rawImage.height; y++) {
            for (int x = 0; x < rawImage.width; x++, index += indexInc) {
                int value = rawImage.getARGB(index);
                if (landscape)
                    image.setRGB(y, rawImage.width - x - 1, value);
                else
                    image.setRGB(x, y, value);
            }
        }
        
        if (listener != null) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    listener.handleNewImage(size, image, landscape);
                    // jp.handleNewImage(size, image, landscape);
                }
            });
        }
    }
    
    

    /**
	 * Gets the listener.
	 * 
	 * @return the listener
	 */
    public GetScreenListener getListener() {
        return listener;
    }

    /**
	 * Sets the listener.
	 * 
	 * @param listener
	 *            the new listener
	 */
    public void setListener(GetScreenListener listener) {
        this.listener = listener;
    }

}
