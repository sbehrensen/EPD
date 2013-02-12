/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.shore.gui.utils;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import dk.dma.epd.shore.EPDShore;

public class StaticImages {

    Cursor dragCursorMouseClicked;
    Cursor dragCursor;
    Cursor navCursorMouseClicked;
    Cursor navCursor;
    ImageIcon highlightIcon;
    ImageIcon vesselWhite;
    ImageIcon vesselBlue;
    ImageIcon vesselLightgreen;
    ImageIcon vesselCyan;
    ImageIcon vesselRed;
    ImageIcon vesselWhite0;
    ImageIcon vesselBrown;
    ImageIcon vesselMagenta;
    ImageIcon vesselLightgray;
    public StaticImages(){


//      //Get the default toolkit
      Toolkit toolkit = Toolkit.getDefaultToolkit();

      //Load an image for the cursor

      Image image = toolkit.getImage(EPDShore.class.getClassLoader().getResource("images/toolbar/drag_mouse.png"));
      dragCursor = toolkit.createCustomCursor(image, new Point(0,0), "Drag");
      Image image2 = toolkit.getImage(EPDShore.class.getClassLoader().getResource("images/toolbar/drag_on_mouse.png"));
      dragCursorMouseClicked = toolkit.createCustomCursor(image2, new Point(0,0), "Drag_on_mouse");


      Image image3 = toolkit.getImage(EPDShore.class.getClassLoader().getResource("images/toolbar/zoom_mouse.png"));
      navCursor = toolkit.createCustomCursor(image3, new Point(0,0), "Zoom");


      Image image4 = toolkit.getImage(EPDShore.class.getClassLoader().getResource("images/toolbar/zoom_on_mouse.png"));
      navCursorMouseClicked = toolkit.createCustomCursor(image4, new Point(0,0), "Zoom_on_mouse");

      highlightIcon = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/ais/highlight.png"));

      vesselWhite = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/white1_90.png"));
      vesselBlue = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/blue1_90.png"));
      vesselLightgreen = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/lightgreen1_90.png"));
      vesselCyan = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/cyan1_90.png"));
      vesselRed = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/red1_90.png"));
      vesselWhite0 = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/white0.png"));
      vesselBrown = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/brown1_90.png"));
      vesselMagenta = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/magenta1_90.png"));
      vesselLightgray = new ImageIcon(EPDShore.class.getClassLoader().getResource("images/vesselIcons/lightgray1_90.png"));

    }

    public Cursor getDragCursorMouseClicked() {
        return dragCursorMouseClicked;
    }

    public Cursor getDragCursor() {
        return dragCursor;
    }

    public Cursor getNavCursorMouseClicked() {
        return navCursorMouseClicked;
    }

    public Cursor getNavCursor() {
        return navCursor;
    }

    public ImageIcon getHighlightIcon(){
        return highlightIcon;
    }

    public ImageIcon getVesselWhite() {
        return vesselWhite;
    }

    public ImageIcon getVesselBlue() {
        return vesselBlue;
    }

    public ImageIcon getVesselLightgreen() {
        return vesselLightgreen;
    }

    public ImageIcon getVesselCyan() {
        return vesselCyan;
    }

    public ImageIcon getVesselRed() {
        return vesselRed;
    }

    public ImageIcon getVesselWhite0() {
        return vesselWhite0;
    }

    public ImageIcon getVesselBrown() {
        return vesselBrown;
    }

    public ImageIcon getVesselMagenta() {
        return vesselMagenta;
    }

    public ImageIcon getVesselLightgray() {
        return vesselLightgray;
    }


}