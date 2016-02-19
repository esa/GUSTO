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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Drag selection of a circular region.<p>
 * 
 * The region is a circle in display coordinates.
 * 
 * @author  Jon Brumfitt
 */
public class CircularRegionSelector {

    private SkyPane _pane;            // The pane to be controlled
    private int _xDown;               // Mouse down X value
    private int _yDown;               // Mouse down Y value
    private Ellipse2D.Double _circle; // Circle used for drag selection
    
    public CircularRegionSelector(SkyPane pane) {
	_pane = pane;
    }
    
    /**
     * Start a rectangle selection.
     */
    public void startDragging(MouseEvent ev) {
	_xDown = ev.getX();
	_yDown = ev.getY();
	_circle = new Ellipse2D.Double(_xDown, _yDown, 0, 0);
    }

    /**
     * Continue dragging during a rectangle selection.
     */
    public void keepDragging(MouseEvent ev) {
	Graphics2D g2 = (Graphics2D)_pane.getGraphics();
	g2.setXORMode(java.awt.Color.gray);
	g2.draw(_circle);
	updateDragRectangle(ev);
	g2.draw(_circle);
    }

    /**
     * Stop a rectangle selection and return the bounding rectangle.
     *
     * @param ev MouseEvent from mouseReleased call
     * @param minSize minumum selection size in pixels
     * @return Bounding rectangle of the circle
     */
    public Rectangle stopDragging(MouseEvent ev, int minSize) {
	updateDragRectangle(ev);
	_pane.repaint();

	if((_circle.width > minSize) && (_circle.height > minSize)) {
	    Rectangle2D r = _circle.getFrame();
	    return new Rectangle((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
	}
	return null;
    }

    /**
     * Set rectangle using mouse down position and current mouse position.
     */
    private void updateDragRectangle(MouseEvent ev) {
	int ex = ev.getX();
	int ey = ev.getY();
	
	int dx = ex - _xDown;
	int dy = ey - _yDown;
	double radius = Math.sqrt(dx * dx + dy * dy);
	_circle.setFrameFromCenter(_xDown, _yDown, _xDown + radius, _yDown + radius);
    }
}
