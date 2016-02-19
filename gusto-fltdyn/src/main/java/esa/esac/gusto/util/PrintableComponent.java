/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2016 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package esa.esac.gusto.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;

/**
 * An adapter to make any Component printable.
 */
public class PrintableComponent implements Printable {

    public static final int UNSCALED = 0;     // Print screen size
    public static final int SCALE_TO_FIT = 1; // Scale X and Y to fit paper
    public static final int FIX_ASPECT = 2;   // Scale to fit, preserving aspect ratio

    private Component _component;
    private int _scalingMode;

    /**
     * Create a new PrintableComponent.
     */
    public PrintableComponent(Component c) {
	_component = c;
    }

    /**
     * Set the scaling mode.
     */
    public void setScalingMode(int mode) {
	_scalingMode = mode;
    }

    /**
     * Print the pane.
     */
    public int print(Graphics g, PageFormat pf, int page)
	    throws PrinterException {

	if (page >= 1) {
	    return Printable.NO_SUCH_PAGE;
	}
	Graphics2D g2 = (Graphics2D)g;

	g2.translate(pf.getImageableX(),pf.getImageableY());

	if(_scalingMode == SCALE_TO_FIT) {
	    g2.scale(pf.getImageableWidth() / _component.getWidth(),
		     pf.getImageableHeight() / _component.getHeight());

	} else if(_scalingMode == FIX_ASPECT) {
	    double rw = pf.getImageableWidth() / _component.getWidth();
	    double rh = pf.getImageableHeight() / _component.getHeight();
	    double scale = Math.min(rw, rh);
	    g2.scale(scale, scale);
	    
	    g2.setClip(0, 0, _component.getWidth(), _component.getHeight());
	}

	// Turn off double buffering, as it reduces resolution to 72 dpi.
	boolean wasBuffered = disableDoubleBuffering(_component);
	_component.paint(g2);
	restoreDoubleBuffering(_component, wasBuffered);

	return Printable.PAGE_EXISTS;
    }
    
    /**
     * Disable double buffering.
     */
    private boolean disableDoubleBuffering(Component c) {
	if(c instanceof JComponent == false) {
	    return false;
	}
	JComponent jc = (JComponent)c;
	boolean wasBuffered = jc.isDoubleBuffered();
	jc.setDoubleBuffered(false);
	return wasBuffered;
    }

    /**
     * Restore double buffering.
     */
    private void restoreDoubleBuffering(Component c, boolean wasBuffered) {
	if(c instanceof JComponent) {
	    ((JComponent)c).setDoubleBuffered(wasBuffered);
	}
    }
}


