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
package esa.esac.gusto.demo.combined.schedule;

import java.util.ArrayList;
import java.util.List;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

public class Schedule {
    private static final SimpleTimeFormat utc = new SimpleTimeFormat(TimeScale.UTC);
    private TaiTime _tStart;
    private TaiTime _tEnd;
    private List<ScheduledItem> _observations;
    private int _timeStep = 1000000;
    
    public Schedule() {
	_tStart = utc.parse("2010-01-01T02:00:00Z");
	_tEnd = utc.parse("2010-01-02T02:00:01Z");
	_observations = new ArrayList<ScheduledItem>();
	TaiTime t1 = utc.parse("2010-01-01T10:00:00Z");
	TaiTime t2 = utc.parse("2010-01-01T12:00:00Z");
	TaiTime t3 = utc.parse("2010-01-01T13:00:00Z");
	_observations.add(new ScheduledItem(new Observation("one", 3600), t1));
	_observations.add(new ScheduledItem(new Observation("two", 1800), t2));
	_observations.add(new ScheduledItem(new Observation("three", 5400), t3));
    }
	
    public TimeInterval planningPeriod() {
	return new TimeInterval(_tStart, _tEnd);
    }
    
    public List<ScheduledItem> getObservations() {
	return _observations;
    }
    
    public void moveObservation(ScheduledItem item, TaiTime time) {
	time = snapToGrid(time);
	int duration = item.getItem().getDuration();
	if(time.before(_tStart)) {
	    time = _tStart;
	} else if(time.addSeconds(duration).after(_tEnd)) {
	    time = _tEnd.addSeconds(-duration);
	}
	item.setStartTime(time);
    }
    
    /**
     * Round a  time to the nearest multiple of the step size.
     */
    private TaiTime snapToGrid(TaiTime time) {
	return new TaiTime((time.microsecondsSince1958() + _timeStep/2) / _timeStep * _timeStep);
    }
}
