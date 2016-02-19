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

import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * Handler for mouse events in a SkyPane.<p>
 * 
 * This class provides basic handling of mouse events for panning and zooming the SkyPane.
 * The mouse button and modifier combinations have been chosen to work well across platforms
 * (Linux, Mac OS X and Windows).
 * 
 * <table border="1">
 * <tr><td>Left drag</td><td>Pan the view</td></tr>
 * <tr><td>Right drag</td><td>Select region and zoom</td></tr>
 * <tr><td>Right double-click</td><td>Zoom out</td></tr>
 * <tr><td>Mouse wheel</td><td>Zoom</td></tr>
 * </table><br>
 * 
 * In addition, it is possible to provide a SkyMouseHandler to handle the following
 * additional events:<p>
 * 
 * <table border="1">
 * <tr><td>Right click</td><td>Popup menu</td></tr>
 * <tr><td>Left click</td><td>Typically used for selection</td></tr>
 * </table><br>
 * 
 * SHIFT + left drag is reserved for future use for selecting a region.<p>
 * 
 * For use on Mac OS X when the right mouse button is not enabled, the left button
 * can be used with the Command key instead:<p>
 * 
 * <table border="1">
 * <tr><td>CMD + left drag</td><td>Select region and zoom</td></tr>
 * <tr><td>CMD + left double-click</td><td>Zoom out</td></tr>
 * <tr><td>CMD + left click</td><td>Popup menu</td></tr>
 * </table>
 */
public class SkyMouseListener extends MouseInputAdapter {
    
   private static final double DEFAULT_ZOOM_RATE = 1.0;
   private static final double DEFAULT_SCALE = 301;
   
    private SkyPane _pane;                         // The pane to be controlled
    private Vector3 _fromVector;                   // Mouse down position as a Vector3
    private SkyMouseHandler _handler;              // Plug-in handler for extra events
    private RectangularRegionSelector _dragger;    // Drag selector for rectangular region
    private double _zoomRate = DEFAULT_ZOOM_RATE;  // 1 to 100

    /**
     * Create a SkyMouseListener.
     * 
     * @param pane The pane to be controlled by the mouse events
     */
    public SkyMouseListener(SkyPane pane) {
	_pane = pane;
	_dragger = new RectangularRegionSelector(_pane);
    }
    
    /**
     * Set the scroll wheel sensitivity for zooming.
     * 
     * @param rate Zoom rate (0.01 to 100) default=1
     */
    public void setZoomRate(double rate) {
	_zoomRate = Math.min(Math.max(rate, 0.01), 100);
    }
    
    
    //================================================================
    // MouseHandler
    //================================================================
    
    /**
     * Set a handler for additional mouse events.
     */
    public void setMouseHandler(SkyMouseHandler handler) {
	_handler = handler;
    }
    
    /**
     * Handle menu pop-up event.<p>
     * 
     * This may be implemented either by overridding this method or setting a MouseHandler.
     * 
     * @param ev The MouseEvent
     */
    protected void showPopupMenu(MouseEvent ev, Vector3 vector) {
	if(_handler != null) {
	    _handler.showPopupMenu(ev, vector);
	}
    }
    
    /**
     * Override this method to handle a left-button click.
     * 
     * @param ev MouseEvent
     * @param vector Sky coordinates (or null)
     */
    public void clicked(MouseEvent ev, Vector3 vector) {
	if(_handler != null) {
	    _handler.clicked(ev, vector);
	}
    }

    //================================================================
    // MouseEvent handing
    //================================================================
    
    /**
     * Test whether the button/modifier combination is to trigger dragging.
     */
    private boolean isDragEvent(MouseEvent ev) {
	return (ev.isShiftDown() && SwingUtilities.isLeftMouseButton(ev))
	    || (ev.isMetaDown() && SwingUtilities.isRightMouseButton(ev));
    }
    
    /**
     * Handle mousePressed event.
     */
    public void mousePressed(MouseEvent ev) {
	// Mac OS X & Linux use right mouse PRESSED for pop-up trigger
	if(ev.isPopupTrigger()) {
	    Vector3 v = _pane.getMouseVector(ev);
	    showPopupMenu(ev, v);

	} else if(SwingUtilities.isLeftMouseButton(ev)) {
	    // Initialize current position for panning
	    _fromVector = _pane.getMouseVector(ev);
	    
	}
	if(isDragEvent(ev)) {
	    _dragger.startDragging(ev);
	}
    }

    /**
     * Handle mouseReleased event.
     */
    public void mouseReleased(MouseEvent ev) {
	// Windows uses right mouse RELEASED for pop-up trigger
	if(ev.isPopupTrigger()) {
	    Vector3 v = _pane.getMouseVector(ev);
	    showPopupMenu(ev, v);
	} 
	if(isDragEvent(ev)) {
	    Rectangle rect = _dragger.stopDragging(ev, 2);
	    
	    // Select an area
	    if(rect != null) {
		boolean isSelect = ev.isMetaDown();
		if(isSelect) {
		    _pane.selectArea(rect);
		}
	    }
	    
	    // Double-click zooms out
	    if(ev.isMetaDown() && ev.getClickCount() == 2) {
		SkyViewPortController adjuster = _pane.getViewPortController();
		if(adjuster != null) {
		    adjuster.setScale(DEFAULT_SCALE);
		}
	    }
	}
    }

    /**
     * Handle mouse dragged event.
     */
    public void mouseDragged(MouseEvent ev) {
	// Handle drag if META is down
	if(isDragEvent(ev)) {
	    _dragger.keepDragging(ev);

        // Else, pan the view
	} else if(SwingUtilities.isLeftMouseButton(ev)) {
	    SkyViewPortController adjuster = _pane.getViewPortController();
	    if(adjuster != null) {
		Vector3 vector3AtClick = _pane.getMouseVector(ev);
		if (vector3AtClick != null && _fromVector != null) {
		    Vector3 axis = _fromVector.cross(vector3AtClick);
		    double angle = _fromVector.angle(vector3AtClick);
		    if(angle > 1E-10) {
			Quaternion q = new Quaternion(axis, angle);
			adjuster.setViewPort(q.conjugate().mMultiply(_pane.getProjection().getViewPort()));
		    }
		}
		_pane.repaint();
	    }
	}
    }
 
    /**
     * Handle mouseClicked event.
     * 
     * @param ev The MouseEvent
     */
    public void mouseClicked(MouseEvent ev) {
	if(SwingUtilities.isLeftMouseButton(ev) && !(ev.isShiftDown() || ev.isMetaDown())) {
	    Vector3 v = _pane.getMouseVector(ev);
	    clicked(ev, v);
	}
    }
    
    /**
     * Handle mouseWheelMoved event.
     */
    public void mouseWheelMoved(MouseWheelEvent ev) {
	if(!ev.isShiftDown()) {  // Ignore left/right events
	    SkyViewPortController adjuster = _pane.getViewPortController();
	    if(adjuster != null) {
		double scale = adjuster.getScale();
//		int rot = ev.getWheelRotation();
		int rot = ev.getUnitsToScroll();
		adjuster.setScale(scale * (1 - rot * _zoomRate * 0.005));
	    }
	}
    }
}
