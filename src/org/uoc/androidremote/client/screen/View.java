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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import org.uoc.androidremote.client.remote.Conector;

import com.android.ddmlib.IDevice;


// TODO: Auto-generated Javadoc
/**
 * The Class View.
 */
public class View extends JFrame {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
        
    /** The old image dimension. */
    private Dimension oldImageDimension = null;
    
    /** The panel. */
    private JPanelScreen panel;
    
    /**
	 * Instantiates a new view.
	 * 
	 * @param dispositivo
	 *            the dispositivo
	 */
    public View(IDevice dispositivo){
        this.setLayout(new BorderLayout());
        panel = new JPanelScreen();
        this.add(panel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(300,600));
        setDefaultCloseOperation(3);
        try {
            Conector conector = new Conector(dispositivo);
            conector.start();
            setConector(conector);
        } catch (IOException e) {
            System.err.println("Error al crear el conector");
        }
    }
    
    /**
	 * Sets the conector.
	 * 
	 * @param con
	 *            the new conector
	 */
    public void setConector(Conector con) {
        con.screencapture.setListener(new GetScreenListener() {

            public void handleNewImage(Dimension size, BufferedImage image,
                    boolean landscape) {
                if(oldImageDimension == null ||
                        !size.equals(oldImageDimension)) {
                    View.this.pack();
                    oldImageDimension = size;
                }
                panel.handleNewImage(size, image, landscape);
            }
        });
    }

}
