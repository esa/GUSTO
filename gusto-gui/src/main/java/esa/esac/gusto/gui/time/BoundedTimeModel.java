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

/**
 * Model for a time with a bounded range.<p>
 * 
 * The range provides a hard limit on the allowed value of the time.
 * 
 * @author Jon Brumfitt
 */
public class BoundedTimeModel extends TimeModel {
    private TimeInterval _interval;

    /**
     * Crate a new BoundedTimeModel.
     */
    public BoundedTimeModel(TimeInterval interval) {
	_interval = interval;
	setTime(interval.start().addMicroseconds(interval.duration() / 2));
    }
    
    /**
     * Crate a new BoundedTimeModel.
     */
    public BoundedTimeModel(TaiTime start, TaiTime finish) {
	this(new TimeInterval(start, finish));
    }
    
    /**
     * Return the time range.<p>
     * 
     * This is a hard limit on the allowed time range and should not be
     * confused with the time range that is displayed by the view.
     */
    public TimeInterval getRange() {
	return _interval;
    }

    /**
     * Set the time range.
     */
    public void setRange(TimeInterval interval) {
	_interval = interval;
	fireChange();
    }
}

