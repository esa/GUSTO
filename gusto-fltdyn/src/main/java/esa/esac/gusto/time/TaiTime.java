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

package esa.esac.gusto.time;

import java.io.Serializable;
import java.util.Date;

/**
 * Atomic time (SI seconds) elapsed since the TAI epoch of 1 January 
 * 1958 UT2. The resolution is one microsecond and the allowable range 
 * is: epoch +/-290,000 years approximately.<p>
 *
 * This has the following advantages, compared with the standard Java
 * <tt>Date</tt> class:<br>
 * <ul>
 * <li> It has better resolution (microseconds)
 * <li> Time differences are correct across leap seconds
 * <li> It is immutable
 * </ul>
 *
 * <b>Note:</b> The Java <tt>Date</tt> class represents the approximate
 * elapsed time since the epoch 1 January 1970, ignoring leap-seconds.
 * Hence, time differences, obtained by subtracting the <tt>getTime</tt>
 * values of two Dates are not correct if there is an intervening 
 * leap-second. This approach is adequate for business applications, 
 * but not for many scientific applications.
 *
 * @author  Jon Brumfitt
 */
public final class TaiTime implements Serializable, Cloneable, Comparable<TaiTime> {

    private static final long serialVersionUID = -4725763097853111754L;
    
    private static final SimpleTimeFormat fmt;
    static {
	fmt = new SimpleTimeFormat(TimeScale.TAI);
	fmt.setDecimals(6);
    }

    private final long _time; // Microseconds since epoch 1958

    /**
     * Create a TaiTime object representing the specified number of
     * microseconds since the epoch.<p>
     *
     * The epoch is January 1, 1958, 00:00:00 TAI. This is defined to
     * be the same as January 1, 1958, 00:00:00 UT2.
     *
     * @param microseconds Number of microseconds since the epoch
     */
    public TaiTime(long microseconds) {
	_time = microseconds;
    }

    /**
     * Create a TaiTime from a Java Date.<p>
     *
     * This is a convenience method which is equivalent to using the
     * UnixTime class.<p>
     *
     * @param date  The Date to be converted
     * @throws IllegalArgumentException if 'date' is before 1 Jan 1972 UTC.
     */
    public TaiTime(Date date) {
	_time = UnixTime.dateToTaiTime(date)._time;
    }

    /**
     * Return this time as a Java Date.<p>
     *
     * This is a convenience method which is equivalent to using the
     * UnixTime class.<p>
     *
     * Any time within a leap-second is aliased onto the start of the next second
     * (i.e. midnight). Consequently, any time within a leap second will treated
     * as the start of the next day (midnight).<p>
     *
     * Any fraction of a millisecond in the TaiTime is simply truncated, rather
     * than rounded.<p>
     */
    public Date toDate() {
	return UnixTime.TaiTimeToDate(this);
    }

    /**
     * Return the number of microseconds since the epoch: 1 Jan 1958.
     *
     * @return microseconds since 1958 TAI epoch
     */
    public long microsecondsSince1958() {
	return _time;
    }

    /**
     * Return the earlier of this time and the specified time.
     *
     * @param t Time to be compared with this one
     * @return The earlier of the two times
     */
    public TaiTime earliest(TaiTime t) {
	if(_time < t._time) {
	    return this;
	} else {
	    return t;
	}
    }

    /**
     * Return the later of this time and the specified time.
     *
     * @param t Time to be compared with this one
     * @return The later of the two times
     */
    public TaiTime latest(TaiTime t) {
	if(_time > t._time) {
	    return this;
	} else {
	    return t;
	}
    }

    /**
     * Return true if this time is before the specified time.
     *
     * @param t Time to be compared with this one
     * @return true if and only if this time is < t
     */
    public boolean before(TaiTime t) {
	return _time < t._time;
    }

    /**
     * Return true if this time is after the specified time.
     *
     * @param t Time to be compared with this one
     * @return true if and only if this time is > t
     */
    public boolean after(TaiTime t) {
	return _time > t._time;
    }

    /**
     * Return true if this time is before, or the same as, the specified
     * time.
     *
     * @param t Time to be compared with this one
     * @return true if  and only if this time is <= t
     */
    public boolean atOrBefore(TaiTime t) {
	return _time <= t._time;
    }

    /**
     * Return true if this time is after, or the same as, the specified
     * time.
     *
     * @param t Time to be compared with this one
     * @return true if  and only if this time is >= t
     */
    public boolean atOrAfter(TaiTime t) {
	return _time >= t._time;
    }

    /**
     * Compare this time to another object.<p>
     *
     * @param t The object to be compared
     * @return -1 if earlier than obj, +1 if later than obj and 0 if equal to obj
     * @throws ClassCastException if the other object is not a time
     */
    public int compareTo(TaiTime t) {
	if(_time < t._time) {
	    return -1;
	} else if(_time > t._time) {
	    return 1;
	} else {
	    return 0;
	}
    }

    /**
     * Compare this time for equality with another object.
     *
     * @param obj The object to be compared
     * @return true if and only if equal to obj
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof TaiTime)) {
	    return false;
	}
	TaiTime t = (TaiTime)obj;
	return _time == t._time;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return the hash code of this object
     */
    public int hashCode() {
	return (int)(_time ^ (_time>>>32));
    }

    /**
     * Returns a String representation of this object.<p> 
     *
     * The exact details of the representation are
     * unspecified and subject to change.
     */
    public String toString() {
	if((_time > 0) && (_time < 100000000000000000L)) {
	    return fmt.format(this) + " (" + _time + ")";
	} else {
	    return "" + _time;
	}
    }

    /**
     * Return a new TaiTime, 'offset' SI microseconds later than
     * this object.<p>
     *
     * When using a non-linear time scale, such as TDB, calendar 
     * seconds may be added using the <tt>addSeconds</tt> method of 
     * <tt>GregorianTimeCalendar</tt>.<p>
     *
     * This method could give an incorrect result if the answer wraps 
     * round at +/-290,000 years. Ideally, an exception should be thrown.
     *
     * @param offset The number of microseconds to be added
     * @return The new TaiTime
     */
    public TaiTime addMicroseconds(long offset) {
	return new TaiTime(_time + offset);
    }

    /**
     * Return a new TaiTime, 'offset' SI seconds later than
     * this object.<p>
     *
     * When using a non-linear time scale, such as TDB, calendar 
     * seconds may be added using the <tt>addSeconds</tt> method of 
     * <tt>GregorianTimeCalendar</tt>.<p>
     *
     * This method could give an incorrect result if the answer wraps 
     * round at +/-290,000 years. Ideally, an exception should be thrown.
     *
     * @param offset The number of microseconds to be added
     * @return The new TaiTime
     */
    public TaiTime addSeconds(long offset) {
	return new TaiTime(_time + offset * 1000000);
    }

    /**
     * Subract the specified time and return the difference in microseconds.<p>
     *
     * The correct result will not be returned if the time difference exceeds
     * approximately 290,000 years. Ideally, an exception should be thrown.
     *
     * @param time The time to be subtracted from this time
     * @return Number of microseconds difference: this - time
     */
    public long subtract(TaiTime time) {
	return _time - time._time;
    }
    
    /**
     * Create and return a clone of this object.
     * 
     * @return a clone of this object
     */
    public TaiTime clone() {
        try {
            return (TaiTime)super.clone();
        } catch(CloneNotSupportedException e) {
            throw new Error("Assertion failed");
        }
    }
}



