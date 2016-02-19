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

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * A TimeProjection which defines the relation between times and axis positions.
 * 
 * @author  Jon Brumfitt
 */
public class BasicTimeProjection implements TimeProjection {
    private EventListenerList _listenerList = new EventListenerList();
    private transient ChangeEvent _changeEvent = null;
    
    private JComponent _component;
    private int _inset;
    private TimeInterval _timeRange;    // Displayed time range

    /**
     * Create a new BasicTimeProjection.
     * 
     * @param component JComponent defining the width of the view.
     */
    public BasicTimeProjection(JComponent component, TimeInterval range, int inset) {
	_component = component;
	_timeRange = range;
	_inset = inset;
    }
    
    /**
     * Set the component if a null value was passed to the constructor.
     */
    public void setComponent(JComponent component) {
	_component = component;
    }
    
    /**
     * Return the width including inset at each end.
     */
    public int getWidth() {
	return _component.getWidth();
    }

    /**
     * Return the time range.
     */
    public TimeInterval getRange() {
	return _timeRange;
    }

    /**
     * Set the time range.
     */
    public void setRange(TimeInterval range) {
	if(range.duration() <= 0) {
	    throw new IllegalArgumentException("Period must be greater than zero");
	}
	_timeRange = range;
	fireChange();
    }

    /**
     * Transform time to plotting units.
     */
    public float timeToScreen(TaiTime time) {
	double x = time.subtract(getOrigin()) / getScale();
	return (float)(x) + _inset;
    }

    /**
     * Returns the time corresponding to a certain screen position.
     */
    public TaiTime screenToTime(float x) {
	return getOrigin().addMicroseconds((long)((x - _inset) * getScale()));
    }

    /**
     * Returns the time scale in microseconds per pixel;
     */
    public double getScale() {
	return getRange().duration() / (double)(getWidth() - 2 * _inset);
    }

    /**
     * Returns the earliest time represented in the timeline.
     */
    private TaiTime getOrigin() {
	return getRange().start();
    }
    
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
