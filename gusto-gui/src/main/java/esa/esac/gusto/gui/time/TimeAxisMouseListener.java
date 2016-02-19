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
package esa.esac.gusto.gui.time;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.time.TaiTime;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * MouseListener for time axis panes.<p>
 * 
 * The listener responds to the following mouse events:<br>
 * 
 * <table border="1">
 * <tr><td>Click-and-drag scale</td><td>Shift time range</td></tr>
 * <tr><td>Click-and-drag thumb</td><td>Adjust time</td></tr>
 * <tr><td>Double-click</td><td>Jump to time</td></tr>
 * <tr><td>Mouse wheel</td><td>Zoom time range</td></tr>
 * <tr><td>Right double-click</td><td>Zoom out fully</td></tr>
 * </table><br>
 *
 * @author  Jon Brumfitt
 */
public class TimeAxisMouseListener extends MouseAdapter {

    private TimeView _timeView;
    private BoundedTimeModel _model;
    private TimeProjection _projection;
    double _zoomRate = 1;
    int _minimumPeriod = 1;
    
    private TaiTime _tStart;
    private int _xDown;
    private boolean _dragging;
    private boolean _zooming;
    private int _yMin = Integer.MIN_VALUE;
    private int _yMax = Integer.MAX_VALUE;
    
    /**
     * Create a new TimeAxisMouseListener.
     */
    public TimeAxisMouseListener(BoundedTimeModel model, TimeView view) {
	_model = model;
	_timeView = view;
	_projection = view.getProjection();
    }

    public void setMinimumPeriod(int seconds) {
	_minimumPeriod = seconds;
    }

    //================================================================================
    // Mouse handling
    //================================================================================

    /**
     * Set the range of Y values for which mouse events are handled.
     */
    public void setYRange(int min, int max) {
	_yMin = min;
	_yMax = max;
    }

    /**
     * Handle mousePressed event.
     */
    public void mousePressed(MouseEvent ev) {
	int y = ev.getY();
	if(y < _yMin || y > _yMax) return;
	_tStart = _projection.getRange().start();
	_xDown = ev.getX();

	float xOld = _projection.timeToScreen(_model.getTime());
	if(Math.abs(_xDown - xOld) < 7) {   // Thumb selection radius
	    _dragging = true;
	}
	if(!_dragging) {
	    _zooming = true;
	}
	_timeView.repaint();
    }

    /**
     * Handle mouseReleased event.
     */
    public void mouseReleased(MouseEvent ev) {
	_dragging = false;
	_zooming = false;
	_timeView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	if(ev.getY() > _yMin && ev.getY() < _yMax) {
	    if(ev.getClickCount() == 2) { 
		if(ev.isMetaDown()) {
		    _projection.setRange(_model.getRange()); // Zoom out maximum
		} else {
		    TaiTime time = _projection.screenToTime(ev.getX()); // Double-click to select time
		    setTime(time);
		}
	    }
	}
	_timeView.repaint();
    }

    /**
     * Handle mouse dragged event.
     */
    public void mouseDragged(MouseEvent ev) {
	int x = ev.getX();
	// Drag thumb to change time
	if(_dragging) {
	    setTime(_projection.screenToTime(x));
	    _timeView.repaint();

	} else if(_zooming) {  // else drag the scale
	    int dx = x - _xDown;
	    long dt = -(long)(_projection.getScale() * dx);
	    TaiTime t1 = _tStart.addMicroseconds(dt);
	    pan(t1);
	    _timeView.repaint();
	}
    }

    private void setTime(TaiTime time) {
	TimeInterval range = _projection.getRange();
	if(time.before(range.start())) {
	    _model.setTime(range.start());
	} else if(time.after(range.finish())) {
	    _model.setTime(range.finish());
	} else {
	    _model.setTime(time);
	}
    }

    public void mouseMoved(MouseEvent ev) { 
	_xDown = ev.getX();
	int y = ev.getY();
	float xOld = _projection.timeToScreen(_model.getTime());
	if((Math.abs(_xDown - xOld) < 7) && (y > _yMin) && (y < _yMax)) {
	    _timeView.setCursor(new Cursor(Cursor.HAND_CURSOR));
	} else {
	    _timeView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
    }

    /**
     * Handle mouseWheelMoved event.
     */
    public void mouseWheelMoved(MouseWheelEvent ev) {
	if(!ev.isShiftDown()) {  // Ignore left/right events
	    // int rot = ev.getWheelRotation();
	    int rot = ev.getUnitsToScroll();
	    zoomAbout(1 + rot * _zoomRate * 0.01, ev.getX());
	    _timeView.repaint();
	}
    }


    //================================================================================
    // Zooming and panning
    //================================================================================

    /**
     * Zoom-out the axis by the specified factor about mouse position.
     */
    private void zoomAbout(double factor, int point) {
	TimeInterval limit = _model.getRange();
	long tMin = limit.start().microsecondsSince1958();
	long tMax = limit.finish().microsecondsSince1958();
	long t1 = _projection.getRange().start().microsecondsSince1958();
	long t2 = _projection.getRange().finish().microsecondsSince1958();

	// Zoom about point
	long tp = _projection.screenToTime(point).microsecondsSince1958();
	if(tp < t1) tp = t1;
	if(tp > t2) tp = t2;
	long t3 = (long)(tp + (t1 - tp) * factor);
	long t4 = (long)(tp + (t2 - tp) * factor);
	
	/*
	 * If the zoom-out causes one end hits the limit, it is no longer possible to zoom
	 * about the point, so zoom about the end. If both ends hit the limit, then stop zooming.
	 */
	if(t4 - t3 >= _minimumPeriod * 1000000L) {
	    if(t3 < tMin) {
		t3 = tMin;
		t4 = (long)(t1 + (t2 - t1) * factor);
	    }
	    if(t4 > tMax){
		t4 = tMax;
		t3 = (long)(t2 - (t2 - t1) * factor);
		if(t3 < tMin) {
		    t3 = tMin;
		}
	    }
	    _projection.setRange(new TimeInterval(new TaiTime(t3), new TaiTime(t4)));   
	}
    }

    private void pan(TaiTime t1) {
	long duration = _projection.getRange().duration();
	TaiTime min = _model.getRange().start();
	TaiTime max = _model.getRange().finish();

	if(t1.before(min)) {
	    t1 = min;
	}
	TaiTime t2 = t1.addMicroseconds(duration);
	if(t2.after(max)) {
	    t2 = max;
	    t1 = max.addMicroseconds(-duration);
	}
	_projection.setRange(new TimeInterval(t1, t2));
    }
}
