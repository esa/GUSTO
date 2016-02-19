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

package esa.esac.gusto.gui.sky;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Drag selection of a rectangular region.<p>
 * 
 * The region is a rectangle in projected screen coordinates.
 * 
 * @author  Jon Brumfitt
 */
public class RectangularRegionSelector {

    private SkyPane _pane;        // The pane to be controlled
    private int _xDown;           // Mouse down X value
    private int _yDown;           // Mouse down Y value
    private Rectangle _rectangle; // Rectangle used for drag selection
    
    public RectangularRegionSelector(SkyPane pane) {
	_pane = pane;
    }
    
    /**
     * Start a rectangle selection.
     */
    public void startDragging(MouseEvent ev) {
	_rectangle = new Rectangle(ev.getX(), ev.getY(), 0, 0);
	_xDown = ev.getX();
	_yDown = ev.getY();
    }

    /**
     * Continue dragging during a rectangle selection.
     */
    public void keepDragging(MouseEvent ev) {
	Graphics2D g2 = (Graphics2D)_pane.getGraphics();
	g2.setXORMode(java.awt.Color.gray);
	g2.draw(_rectangle);
	updateDragRectangle(ev);
	g2.draw(_rectangle);
    }

    /**
     * Stop a rectangle selection and return the rectangle.
     *
     * @param ev MouseEvent from mouseReleased call
     * @param minSize minumum selection size in pixels
     */
    public Rectangle stopDragging(MouseEvent ev, int minSize) {
	updateDragRectangle(ev);
	_pane.repaint();

	if((_rectangle.width > minSize) && (_rectangle.height > minSize)) {
	    return _rectangle;
	}
	return null;
    }

    /**
     * Set rectangle using mouse down position and current mouse position.
     */
    private void updateDragRectangle(MouseEvent ev) {
	int ex = ev.getX();
	int ey = ev.getY();

	// Four cases to consider as Rectangle cannot have negative width or height
	if(ex > _xDown) {
	    if(ey > _yDown) {
		_rectangle.setRect(_xDown, _yDown, ex - _xDown, ey - _yDown);
	    } else {
		_rectangle.setRect(_xDown, ey, ex - _xDown, _yDown - ey);
	    }
	} else {
	    if(ey > _yDown) {
		_rectangle.setRect(ex, _yDown, _xDown - ex, ey - _yDown);
	    } else {
		_rectangle.setRect(ex, ey, _xDown - ex, _yDown - ey);
	    }
	}
    }
}
