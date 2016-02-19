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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * A TimeConstraint is a set of valid times, which provide a constraint,
 * represented as a sequence of valid time TimeIntervals.
 *
 * Each TimeInterval is assumed to be half-open, of the form [t1,t2).
 * Consequently, it is not possible to create a constraint representing
 * a singleton set, such as {t1}.
 *
 * @author  Jon Brumfitt
 */
public class TimeConstraint implements Serializable, Iterable<TimeInterval> {

    private static final long serialVersionUID = 1L;
    
    private List<TimeInterval> _intervals = new Vector<TimeInterval>();
    
    /**
     * Create a new empty TimeConstraint.
     */
    public TimeConstraint() {
    }

    /**
     * Create a new TimeConstraint representing a single TimeInterval.
     */
    public TimeConstraint(TimeInterval interval) {
	_intervals.add(interval);
    }

    /**
     * Create a new TimeConstraint representing a single half-open
     * TimeInterval [start,finish).
     */
    public TimeConstraint(TaiTime start, TaiTime finish) {
	_intervals.add(new TimeInterval(start,finish));
    }

    /**
     * Create a new TimeConstraint representing a single half-open
     * TimeInterval [start,start+duration).
     */
    public TimeConstraint(TaiTime start, long duration) {
	_intervals.add(new TimeInterval(start,duration));
    }

    /**
     * Test two TimeConstraints for equality.
     */
    public boolean equals(Object obj) {
	if (this == obj)   {                // Just an optimisation
	    return true;
	}
	if(obj instanceof TimeConstraint) {
	    Iterator<TimeInterval> it1 = iterator();
	    Iterator<TimeInterval> it2 = ((TimeConstraint)obj).iterator();
	    while(it1.hasNext() && it2.hasNext()) {
		TimeInterval i1 = it1.next();
		TimeInterval i2 = it2.next();
		if(! i1.equals(i2)) {
		    return false;
		}
	    }
	    if(it1.hasNext() || it2.hasNext()) {
		return false;
	    }
	    return true;
	}
	return false;
    }

    /**
     * Return the HashCode of this object;
     */
    public int hashCode() {
	return _intervals.hashCode();
    }

    /**
     * Return iterator over the intervals within the TimeConstraint.
     */
    public Iterator<TimeInterval> iterator() {
	return _intervals.iterator();
    }
    

    /**
     * Test whether time is contained in a valid interval.
     *
     * Each interval is treated as half open: [t1,t2).
     * Hence, [t1,t2) contains(t2) is false.
     */
    public boolean contains(TaiTime time) {
	Iterator<TimeInterval> it = _intervals.iterator();
	boolean ok = false;
	while(it.hasNext() && !ok) {
	    ok = it.next().contains(time);
	}
        return ok;
    }

    /**
     * Test whether interval is contained in a valid TimeInterval.
     *
     * This allows intervals which fit a valid TimeInterval exactly.
     */
    public boolean contains(TimeInterval interval) {
	Iterator<TimeInterval> it = _intervals.iterator();
	boolean ok = false;
	while(it.hasNext() && !ok) {
	    ok = it.next().contains(interval);
	}
        return ok;
    }

    /**
     * Test whether the interval [start,start+duration) is contained
     * in a valid interval.
     */
    public boolean contains(TaiTime start, long duration) {
	return contains(new TimeInterval(start,duration));
    }

    /**
     * Test whether the interval [start,finish) is contained in a valid
     * interval.
     */
    public boolean contains(TaiTime start, TaiTime finish) {
	return contains(new TimeInterval(start,finish));
    }

    /**
     * Find earliest Time which satisfies the TimeConstraint.
     *
     * Returns null if constraint is never satisfied.
     */
    public TaiTime earliest() {   // Optimised version of earliestFor(0);
	if(_intervals.isEmpty()) {
	    return null;
	} else {
	    return ((TimeInterval)_intervals.get(0)).start();
	}
    }

    /**
     * Return the (open) end-point of the last interval. Note that this time 
     * does NOT satisfy the constraint, as the intervals are half-open.
     *
     * An alternative way to view the result is as the latest time which
     * satisfies the constraint, rounded to the nearest time that can be
     * represented by the finite representation of TaiTime.
     *
     * i.e: last [t1,t2) == t2, but [t1,t2) contains(t2) is false.
     *
     * Returns null if constraint is never satisfied.
     */
    public TaiTime latest() {     // Optimised version of latestFor(0)
	if(_intervals.isEmpty()) {
	    return null;
	} else {
	    return ((TimeInterval)_intervals.get(_intervals.size() - 1)).finish();	
	}
    }
 
    /**
     * Find the earliest time which can contain the duration.
     *
     * Returns null if not possible.
     */
    public TaiTime earliestFor(long duration) {
	TaiTime earliest = null;
	for(TimeInterval interval : _intervals) {
	    if(interval.duration() >= duration) {
		return interval.start();
	    }
        }
	return earliest;
    }
    
   /**
     * Find the latest time which can contain the duration.
     *
     *   [t1,t2) latestFor(d) = t2-d, if d <= t2-t1
     *   [t1,t2) latestFor(d) = null, if d > t2-t1
     *   [t1,t2) latestFor(t2-t1) = t1
     *
     * Note that the time given by 'result+duration' does not satisfy the
     * constraint, because the intervals are half-open. The duration may be
     * thought of as representing a half-open interval [t,t+d). Hence, the
     * valid start times for the duration form a closed interval. The method
     * returns the latest time that satisfies this closed interval.
     *
     * Returns null if the constraint cannot be satisfied for the duration.
     */
    public TaiTime latestFor(long duration) {
	TaiTime latest = null;
	for(TimeInterval interval : _intervals) {
	    if(interval.duration() >= duration) {
		latest = interval.finish().addMicroseconds(-duration);
	    }
	}
	return latest;
    }
    
   /**
     * Return the time, closest to 'time', which is the start of
     * a valid interval of length 'duration'. Return null if there
     * is no such interval.
     *
     * This implementation assumes that times are within the range
     * +/- Long.MAX/VALUE/2.
     */
    public TaiTime nearestFor(long duration, TaiTime time) {
       	TaiTime nearest = null;
	long dist = Long.MAX_VALUE/ 2;  // Half max. to avoid overflow

	// FIXME: Could be simplified because intervals are ordered
	for(TimeInterval interval : _intervals) { 
	    if(interval.duration() >= duration) {
		TaiTime start  = interval.start();
		TaiTime finish = interval.finish().addMicroseconds(-duration);
		if(time.atOrAfter(start) && time.atOrBefore(finish)) {
		    nearest = time;
		    break;
		}
		if(start.atOrAfter(time) && 
		   start.addMicroseconds(-dist).atOrBefore(time)) {
		    nearest = start;
		    dist = start.subtract(time);
		}
		if(finish.atOrBefore(time) &&
		   finish.addMicroseconds(dist).atOrAfter(time)) {
		    nearest = finish;
		    dist = time.subtract(finish);
		}
	    }
	}
 	return nearest;
    }

   /**
     * Return the start of the earliest valid interval of length 'duration'
     * which is not before 'time'. Return null if there is no such interval.
     *
     * This implementation assumes that times are within the range
     * +/- Long.MAX/VALUE/2.
     */
    public TaiTime earliestNotBefore(long duration, TaiTime time) {
	for(TimeInterval interval : _intervals) { 
	    if(interval.duration() >= duration) {
		TaiTime start  = interval.start();
		TaiTime finish = interval.finish().addMicroseconds(-duration);

		if(time.atOrBefore(finish)) {
		    if(time.atOrAfter(start)) {
			return time;
		    } else {
			return start;
		    }
		}
	    }
	}
 	return null;
    }

    /**
     * Return the start of the latest valid interval of length 'duration'
     * which is not after 'time'. Return null if there is no such interval.
     *
     * This implementation assumes that times are within the range
     * +/- Long.MAX/VALUE/2.
     */
    public TaiTime latestNotAfter(long duration, TaiTime time) {
	ListIterator<TimeInterval> it = _intervals.listIterator(_intervals.size());
	while(it.hasPrevious()) {
	    TimeInterval interval = it.previous();
	    if(interval.duration() >= duration) {
		TaiTime start  = interval.start();
		TaiTime finish = interval.finish().addMicroseconds(-duration);

		if(time.atOrAfter(start)) {
		    if(time.atOrBefore(finish)) {
			return time;
		    } else {
			return finish;
		    }
		}
	    }
	}
	return null;
    }

    /**
     * Interface for a boolean operator.
     */
    private interface BooleanOp {
	public boolean apply(boolean a, boolean b);
    }

    /**
     * Return union with another TimeConstraint. That is, those Times that
     * satisfy either this TimeConstraint or the specified TimeConstraint.
     */
    public TimeConstraint union(TimeConstraint c) {
        return merge(c, new BooleanOp() {
                public boolean apply(boolean a,boolean b) {
                    return a || b;
                }
            });
    }

    /**
     * Return intersection with another TimeConstraint. That is, those times
     * that satisfy both this TimeConstraint and the specified TimeConstraint.<p>
     *
     * Since intervals are half-open:
     *   [t1,t2) union [t2,t3) gives an empty constraint
     */
    public TimeConstraint intersection(TimeConstraint c) {
        return merge(c, new BooleanOp() {
                public boolean apply(boolean a,boolean b) {
                    return a && b;
                }
            });
    }

    /**
     * Return a constraint which contains those times that satisfy
     * this TimeConstraint but do not satisfy the specified TimeConstraint.
     */
    public TimeConstraint exclude(TimeConstraint c) {
        return merge(c, new BooleanOp() {
                public boolean apply(boolean a,boolean b) {
                    return a && !b;
                }
            });
    }

    /**
     * Return a constraint which contains those times that satisfy
     * neither this TimeConstraint nor the specified TimeConstraint.
     */
    public TimeConstraint neither(TimeConstraint c) {
        return merge(c, new BooleanOp() {
                public boolean apply(boolean a,boolean b) {
                    return (!a) && (!b);
                }
            });
    }

    /**
     * Apply a boolean operator to merge this interval with the specified
     * interval. The merging algorithm treats the intervals as half-open:
     *    [t1,t2) and [t2,t3) gives an empty constraint.
     *
     * c1.contains(t) op c2.contains(t) <=> result.contains(t)
     */
    private TimeConstraint merge(TimeConstraint c, BooleanOp op) {
	TimeConstraint result = new TimeConstraint();
	
	Iterator<TimeInterval> it1 = _intervals.iterator();
	Iterator<TimeInterval> it2 = c._intervals.iterator();
	boolean s1 = false;
	boolean s2 = false;
	boolean out = false;
	TimeInterval i1 = null;
	TimeInterval i2 = null;
	TaiTime t1 = null;
	TaiTime t2 = null;
	TaiTime t = null;
	TaiTime start = null;

	// Read first pair of events
	if(it1.hasNext()) {
	    i1 = it1.next();
	    t1 = i1.start();
	}
	if(it2.hasNext()) {
	    i2 = it2.next();
	    t2 = i2.start();
	}
	boolean lastOut = op.apply(s1,s2);

	while((t1 != null) || (t2 != null)) {

	    if((t1 != null) && ((t2 == null) || (t1.before(t2)))) {
		// t1 earlier than t2
		s1 = !s1;
		t = t1;
		if(s1) {
		    t1 = i1.finish();
		} else {
		    if(it1.hasNext()) {
			i1 = (TimeInterval)it1.next();
			t1 = i1.start();
		    } else {
			t1 = null;
		    }
		}
	    } else if((t2 != null) && ((t1 == null) || (t1.after(t2)))) {
		// t2 is earlier or same as t1
		s2 = !s2;
		t = t2;
		if(s2) {
		    t2 = i2.finish();
		} else {
		    if(it2.hasNext()) {
			i2 = (TimeInterval)it2.next();
			t2 = i2.start();
		    } else {
			t2 = null;
		    }
		}
	    } else { // t1 == t2
		s1 = !s1;
		s2 = !s2;
		t = t2;
		if(s1) {
		    t1 = i1.finish();
		} else {
		    if(it1.hasNext()) {
			i1 = (TimeInterval)it1.next();
			t1 = i1.start();
		    } else {
			t1 = null;
		    }
		}
		if(s2) {
		    t2 = i2.finish();
		} else {
		    if(it2.hasNext()) {
			i2 = (TimeInterval)it2.next();
			t2 = i2.start();
		    } else {
			t2 = null;
		    }
		}
	    }

	    // Generate output when 'out' changes state
	    lastOut = out;
	    out = op.apply(s1,s2);
	    if(out && !lastOut) {
		start = t;
	    } else {
		if((!out && lastOut) && (t.after(start))) {
		    result._intervals.add(new TimeInterval(start, t));
		}
	    }
	}
	return result;
    }

    /**
     * Return intersection with a TimeInterval.
     */
    public TimeConstraint intersection(TimeInterval interval) {
	return intersection(new TimeConstraint(interval));
    }

    /**
     * Return union with a TimeInterval.
     */
    public TimeConstraint union(TimeInterval interval) {
	return union(new TimeConstraint(interval));
    }

    /**
     * Return constraint excluding a fiven interval.
     */
    public TimeConstraint exclude(TimeInterval interval) {
	return exclude(new TimeConstraint(interval));
    }

    /**
     * Test whether the constraint is empty (never satisfied).
     */
    public boolean isEmpty() {
	return _intervals.isEmpty();
    }

    /**
     * Return a String representation of the Constraint.
     * This is intended mainly for testing and debugging purposes.
     */
    public String toString() {
	StringBuffer s = new StringBuffer();
	s.append("Constraint:\n");
	for(TimeInterval ti : _intervals) {
	    s.append(" ");
	    s.append(ti);
	    s.append("\n");
	}
        return s.toString();
    }
}

