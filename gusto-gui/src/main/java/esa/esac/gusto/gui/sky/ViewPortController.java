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

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Controller for the view-port of a SkyView.
 *
 * @author  Jon Brumfitt
 */
public class ViewPortController implements SkyViewPortController {
    private static final double DEFAULT_SCALE = 300;    // Pixels/radian
    
    private static final double MIN_SCALE = 50.0;       // Pixels per radian
    private static final double MAX_SCALE = 1000000.0;  // Pixels per radian
    private static final double ZOOM_STEP = 1.5;        // Zoom factor
    private static final double TURN_STEP = 10.0;       // Degrees
    private static final double PAN_STEP  = 50.0;       // Pixels

    private EventListenerList _listenerList = new EventListenerList();
    private transient ChangeEvent _changeEvent = null;

    private Quaternion _qCenter = new Quaternion();
    private Quaternion _selection;
    private double     _scale;
    private CoordinateFrame _coordFrame = CoordinateFrame.EQUATORIAL;
    private CoordinateFrame _graticuleFrame = CoordinateFrame.EQUATORIAL;
    
    private Action _centerAction;
    private Action _panUpAction;
    private Action _panDownAction;
    private Action _panLeftAction;
    private Action _panRightAction;
    private Action _rotateLeftAction;
    private Action _rotateRightAction;
    private Action _zoomInAction;
    private Action _zoomOutAction;
    private Map<String,Action> _actions;

    /**
     * Create a new ViewPortController.
     */
    @SuppressWarnings("serial")
    public ViewPortController() {
	_scale = DEFAULT_SCALE;

	_centerAction = new AbstractAction("Center") {
	    public void actionPerformed(ActionEvent e) {
		resetViewPort(); 
	    }
	};
	_panUpAction = new AbstractAction("Pan up") {
	    public void actionPerformed(ActionEvent e) {
		pan(0.0);
	    }
	};
	_panDownAction = new AbstractAction("Pan down") {
	    public void actionPerformed(ActionEvent e) {
		pan(180);
	    }
	};
	_panLeftAction = new AbstractAction("Pan left") {
	    public void actionPerformed(ActionEvent e) {
		pan(90.0); 
	    }
	};
	_panRightAction = new AbstractAction("Pan right") {
	    public void actionPerformed(ActionEvent e) {
		pan(270.0); 
	    }
	};
	_rotateLeftAction = new AbstractAction("Rotate left") {
	    public void actionPerformed(ActionEvent e) {
		turn(TURN_STEP);
	    }
	};
	_rotateRightAction = new AbstractAction("Rotate right") {
	    public void actionPerformed(ActionEvent e) {
		turn(-TURN_STEP);
	    }
	};
	_zoomInAction = new AbstractAction("Zoom in") {
	    public void actionPerformed(ActionEvent e) {
		zoom(ZOOM_STEP); 
	    }
	};
	_zoomOutAction = new AbstractAction("Zoom out") {
	    public void actionPerformed(ActionEvent e) {
		zoom(1.0/ZOOM_STEP); 
	    }
	};
	
	_actions = new HashMap<String,Action>();
	_actions.put("Center", _centerAction);
	_actions.put("Pan up", _panUpAction);
	_actions.put("Pan down", _panDownAction);
	_actions.put("Pan left", _panLeftAction);
	_actions.put("Pan right", _panRightAction);
	_actions.put("Rotate left", _rotateLeftAction);
	_actions.put("Rotate right", _rotateRightAction);
	_actions.put("Zoom in", _zoomInAction);
	_actions.put("Zoom out", _zoomOutAction);
    }

    /**
     * Return a map of the available actions.
     */
    public Map<String,Action> getActions() {
	return _actions;
    }
    
    /**
     * Tell the view to pan by the current PAN_STEP, in the direction
     * indicated (in degrees).
     */
    private void pan(double x) {
	Quaternion qy = Quaternion.yRotation(-PAN_STEP/_scale);
	Quaternion qx = Quaternion.xRotation(-Math.toRadians(x));
	_qCenter.mMultiply(qx.rotate(qy));
	
	fireChange();
    }
    
    /**
     * Tell the view to rotate through the given angle (in degrees).
     */
    private void turn(double angle) {
	Quaternion q = Quaternion.xRotation(Math.toRadians(-angle));
	_qCenter.mMultiply(q);
	
	fireChange();
    }

    /**
     * Tell the view to zoom by the given factor.<p>
     *
     * The view will zoom in if factor>1 or zoom out if factor<1.
     */
    private void zoom(double factor) {
	double scale = _scale * factor;
	
	if(scale >= MIN_SCALE && scale <= MAX_SCALE) {
	    _scale = scale;
	    updateButtons();
            fireChange();
	}
    }

    /**
     * Update enabling of the buttons.
     */
    private void updateButtons() {
	_zoomInAction.setEnabled(_scale * ZOOM_STEP <= MAX_SCALE);
	_zoomOutAction.setEnabled(_scale / ZOOM_STEP >= MIN_SCALE);
    }

    /**
     * Set the scale (pixels per radian) and notify views.
     */
    public void setScale(double scale) {
	if(scale < MIN_SCALE) {
	    scale = MIN_SCALE;
	} else if(scale > MAX_SCALE) {
	    scale = MAX_SCALE;
	}

	if(scale != _scale) {
	    _scale = scale;
	    updateButtons();
	    fireChange();
	}
    }
    
    
    /**
     * Reset the ViewPort.<p>
     * 
     * Uses the selection, if set, otherwise resets to default alignment for the frame.
     */
    public void resetViewPort() {
	if(_selection == null) {
	    _qCenter = CoordinateFrame.transformation(_coordFrame, _graticuleFrame);
	} else {
	    _qCenter = _selection;
	}
	fireChange();
    }
    
    /**
     * Set a ViewPort for the resetViewPort method.
     */
    public void setSelection(Quaternion q) {
	_selection = q;
    }
    
    /**
     * Set the Frame for the cursor readout.
     */
    public void setFrames(CoordinateFrame coord, CoordinateFrame graticule) {
	_coordFrame = coord;
	_graticuleFrame = graticule;
    }

    /**
     * Set the view port.
     */ 
    public void setViewPort(Quaternion q) {
	_qCenter = q;
	fireChange();
    }

    /**
     * Set viewport and scale and notify views.
     */
    public void setViewPort(Quaternion q, double scale) {
	_qCenter = q;
	_scale = scale;

	if(scale < MIN_SCALE) {
	    _scale = MIN_SCALE;

	} else if(scale > MAX_SCALE) {
	    _scale = MAX_SCALE;
	}

	updateButtons();
	fireChange();
    }
    

    //--------------------------
    //  Model properties
    //--------------------------

    /**
     * Return the current viewport.
     */
    public Quaternion getViewPort() {
	return _qCenter;
    }

    /**
     * Return the current scale of the view;
     */
    public double getScale() {
	return _scale;
    }

    
    //--------------------------
    //  MVC interaction
    //--------------------------

    /**
     * Add a listener to receive notification when the model state changes.
     */
    public void addChangeListener(ChangeListener l) {
	_listenerList.add(ChangeListener.class, l);
    }

    /**
     * Remove a listener so that it no longer receives notifications.
     */
    public void removeChangeListener(ChangeListener l) {
	_listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Notify all listeners that the model state has changed.
     */
    protected void fireChange() {
	Object[] listeners = _listenerList.getListenerList();
	for(int i = listeners.length - 2; i >= 0; i -= 2) {
	    if(listeners[i] == ChangeListener.class) {
		if(_changeEvent == null) {
		    _changeEvent = new ChangeEvent(this);
		}
		((ChangeListener)listeners[i+1]).stateChanged(_changeEvent);
	    }
	}
    }
}

