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

package esa.esac.gusto.constraint;

import esa.esac.gusto.time.TaiTime;

/**
 * A TimeInterval is a period of time between a pair of absolute start 
 * and end times.<p>
 *
 * TimeIntervals are half-closed, including their start time but not
 * their end time.<p>
 * <pre>
 *   TimeInterval(start,end) = {t:Time | start<=t & t<end}
 *
 *   TimeInterval(start,end).duration() = end - start, end >=start
 *                                  = 0,           otherwise
 *
 *   Note that: TimeInterval(start,end) = {}, when end<=start
 * </pre>
 *
 * For the purposes of definition, Time should be thought of
 * as real, although the constructors of Time may only allow
 * a resolution of one second.<p>
 *
 * TimeIntervals are immutable if the setStart and setDuration methods
 * are not used.
 *
 * @author  Jon Brumfitt
 */
public class TimeInterval {

    private static final boolean CHECK_PRE = true;

    private TaiTime _start;
    private long  _duration;  // Microseconds

    /**
     * Create a new interval with the specified start time and duration.
     *
     * @param duration Duration in microseconds
     */
    public TimeInterval(TaiTime start, long duration) {
	if(CHECK_PRE) {
	    if(duration < 0) {
		throw new IllegalArgumentException("Negative duration");
	    }
	}

	_start = start;            // TaiTime is immutable
	if(_duration >= 0) {
	    _duration = duration;
	}
    }

    /**
     * Create a new interval with the specified start and stop times.     
     */
    public TimeInterval(TaiTime start, TaiTime stop) {
	if(CHECK_PRE) {
	    if(stop.before(start)) {
		throw new IllegalArgumentException("Negative duration");
	    }
	}

	_start = start;          // TaiTime is immutable
	long duration = stop.subtract(start);
	if(duration >= 0) {
	    _duration = duration;
	}
    }

    /**
     * Return the start time.
     */
    public TaiTime start() {
	return _start;
    }

    /**
     * Return the duration in microseconds.
     */
    public long duration() {
	return _duration;
    }

    /**
     * Return the end time.
     */
    public TaiTime finish() {
	return _start.addMicroseconds(_duration);
    }

    /**
     * Return true if the duration is zero.
     */
    public boolean isNull() {
	return (_duration <= 0);
    }

    /**
     * Returns true if the TimeInterval contains the specified Time.
     */
    // WHAT IF duration = 0?
    public boolean contains(TaiTime time) {
	return (time.atOrAfter(_start)) 
	    && (time.before(finish()));
    }

    /**
     * Returns true if the specified TimeInterval is contained within
     * the TimeInterval represented by this object.
     */
    public boolean contains(TimeInterval i) {
	return contains(i._start)
	    && finish().atOrAfter(i.finish())
	    && (i.duration() >= 0);         // 'duration>=0' might be a state invariant
    }

    /**
     * Returns true if this TimeInterval starts before the specified
     * interval.
     */
    public boolean startsBefore(TimeInterval i) {
	return _start.before(i._start);
    }

    /**
     * Returns true if this TimeInterval starts before the specified
     * interval or at the same time.
     */
    public boolean startsAtOrBefore(TimeInterval i) {
	return _start.atOrBefore(i._start);
    }

    /**
     * Returns true if this TimeInterval starts after the specified
     * interval.
     */
    public boolean startsAfter(TimeInterval i) {
	return _start.after(i._start);
    }

    /**
     * Returns true if this TimeInterval starts after the specified
     * interval or at the same time.
     */
    public boolean startsAtOrAfter(TimeInterval i) {
	return _start.atOrAfter(i._start);
    }

    /**
     * Return the intersection of this interval with another.<p>
     *
     * Returns null is the intervals do not overlap.
     */
    public TimeInterval intersection(TimeInterval i) {
	TaiTime start  = start().latest(i.start());
	TaiTime finish = finish().earliest(i.finish());

	if(start.after(finish)) {
	    return null;
	} else {
	    return new TimeInterval(start, finish);
	}
    }

    /**
     * Returns true if this TimeInterval is equal to the specified
     * TimeInterval.
     */
    public boolean equals(TimeInterval i) {
        return _start.equals(i._start) 
	    && (_duration == i._duration);
    }

    /**
     * Return the HashCode of this object.
     */
    public int hashCode() {
        int result = 17;
	result = 37 * result + _start.hashCode();
	result = 37 * result + (int)(_duration ^ (_duration >>> 32));
	return result;
    }

    /**
     * Compares the start time of this interval with that of another
     * TimeInterval. Returns -1 if this interval starts first, 0 if they
     * start at the same time and +1 if this interval starts last.
     */
    public int compareStartTo(TimeInterval i) {
	return (_start.before(i._start) ? -1 : (_start.equals(i._start) ? 0 : 1));
    }

    /**
     * Return a String representation of the TimeInterval.
     */
    public String toString() {
	return start() + " to " + finish();
    }
}

