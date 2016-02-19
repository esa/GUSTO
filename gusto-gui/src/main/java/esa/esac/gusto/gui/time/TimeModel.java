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

import esa.esac.gusto.time.TaiTime;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Model for time.
 * 
 * @author Jon Brumfitt
 */
public class TimeModel {
    private EventListenerList _listenerList = new EventListenerList();
    private transient ChangeEvent _changeEvent = null;
    private TaiTime _time;
    private int _timeStep = 1; // Time quantization in microseconds.

    /**
     * Crate a new TimeModel.
     */
    public TimeModel() {
    }
    
    /**
     * Return the time.
     */
    public TaiTime getTime() {
	return _time;
    }
    
    /**
     * Set the time quantization level in microseconds.<p>
     * 
     * For example, <tt>setTimeStep(100000)</tt> for 0.1s steps.
     * Note that the quantization is performed on TAI times, so 10 second
     * steps might not correspond to multiples of 10 seconds in UTC.
     * 
     * @param microseconds
     */
    public void setTimeStep(int microseconds) {
	if(microseconds <= 0) {
	    _timeStep = 0;
	}
	_timeStep = microseconds;
    }

    /**
     * Set the time.<p>
     * 
     * The time is rounded to the nearest multiple of the time step.
     */
    public void setTime(TaiTime time) {
	_time = new TaiTime((time.microsecondsSince1958() + _timeStep/2) / _timeStep * _timeStep);
	fireChange();
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

