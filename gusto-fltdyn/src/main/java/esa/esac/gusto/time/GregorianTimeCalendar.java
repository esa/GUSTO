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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Gregorian Calendar for use with TaiTime.<p>
 *
 * This calendar supports the microsecond time resolution of TaiTime
 * and also handles leap-seconds when required.<p>
 *
 * Thye calendar divides time into the following integer fields:
 * <pre>
 *   year:        -29227 to 29227
 *   month:       1 to 12
 *   day:         1 to 31
 *   hour:        0 to 23
 *   minute:      0 to 59
 *   second:      0 to 60
 *   microsecond: 0 to 999999
 * </pre>
 *
 * @author  Jon Brumfitt
 */
public class GregorianTimeCalendar {

    /** Java Date representation of TAI epoch 1 Jan 1958. */
    private static final long D58 = -((1970 - 1958) * 365 + 3) * 86400L * 1000;

    private GregorianCalendar _calendar;
    private boolean _enableLeapSeconds;
    private long _time;                   // Microseconds since TAI epoch

    private int _year;
    private int _month;       // 1 to 12
    private int _day;         // 1 to 31
    private int _hour;        // 0 to 59
    private int _minute;      // 0 to 59
    private int _second;      // 0 to 60
    private int _microsecond; // 0 to 999999

    /**
     * Create a new GregorianTimeCalendar.
     */
    public GregorianTimeCalendar() {
	_calendar = new GregorianCalendar();
	_calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
	_calendar.setGregorianChange(new Date(Long.MIN_VALUE));
    }

    /**
     * Enable the handling of leap seconds.<p>
     *
     * When enabled, the calendar represents leap seconds as second number 60.
     *
     * @param enable True to enable handling of leap-seconds
     */
    public void enableLeapSeconds(boolean enable) {
	_enableLeapSeconds = enable;
    }

    /**
     * Return the calendar date and time as a TaiTime.
     *
     * @return TaiTime representation of calendar date and time
     */
    public TaiTime getTime() {
	return new TaiTime(_time);
    }

    /**
     * Set the Calendar date and time from a TaiTime.
     *
     * @param ft TaiTime used to set the calendar date and time
     */
    public void setTime(TaiTime ft) {
	_time = ft.microsecondsSince1958();
	convertTimeToFields();
    }

    /**
     * Return the calendar fields as an array.<p>
     *
     * They are returned in the order: [year,month,day,hour,minute,second,microsecond].
     *
     * @return array of field values
     */
    public int[] getFields() {
	return new int[]{_year, _month, _day, _hour, _minute, _second, _microsecond};
    }

    /**
     * Set the calendar date and time from a vector of field values.<p>
     *
     * The fields are in the order: [year,month,day,hour,minute,second,microsecond].     *
     * Note: No check is currently made that DAY field <= days in month.
     *
     * @param fields Array of field values
     */
    public void setFields(int[] fields) {
	if(    (fields.length != 7)
	    || (fields[1] < 1) || (fields[1] > 12)           // month
	    || (fields[2] < 1) || (fields[2] > 31)           // day
	    || (fields[3] < 0) || (fields[3] > 23)           // hour
	    || (fields[4] < 0) || (fields[4] > 59)           // minute
	    || (fields[5] < 0) || (fields[5] > 60)           // second
	    || ((!_enableLeapSeconds) && ((fields[5] > 59))) // second
	    || (fields[6] < 0) || (fields[6] > 999999)) {    // microsecond
	    throw new IllegalArgumentException("Invalid calendar fields");
	}
	_year        = fields[0];
	_month       = fields[1];
	_day         = fields[2];
	_hour        = fields[3];
	_minute      = fields[4];
	_second      = fields[5];
	_microsecond = fields[6];
	
	convertFieldsToTime();
    }
    
    /**
     * Set the field values from the time.
     */
    private void convertTimeToFields() {
	long scale = _time;
	scale = lfloor(scale, 1000000);  // Whole seconds

	// Adjust for leap seconds.
	boolean leap = false;
	if(_enableLeapSeconds) {
	    leap = LeapSeconds.isLeapSecond(scale * 1000000);
	    if(leap) {
		scale -= 1;  // Convert second 60 to 59
	    }
	    scale -= LeapSeconds.leapSeconds(scale * 1000000);
	}

	// Convert to fields using Java GregorianCalendar.
	_calendar.setTimeInMillis(scale * 1000 + D58);
	_year   = _calendar.get(Calendar.YEAR);
	_month  = _calendar.get(Calendar.MONTH) + 1;    // Calendar uses January=0
	_day    = _calendar.get(Calendar.DAY_OF_MONTH);
	_hour   = _calendar.get(Calendar.HOUR_OF_DAY);
	_minute = _calendar.get(Calendar.MINUTE);
	_second = _calendar.get(Calendar.SECOND);
	_microsecond = (int)Math.abs(_time % 1000000);  // Should use scale time

	// Adjust the result if it is a leap second.
	if(leap) {
	    _second = 60;
	}
    }

    /**
     * Set the time from the field values.
     */
    private void convertFieldsToTime() {
	boolean leapSecond = false;
	if(_second == 60) {
	    _calendar.set(_year, _month-1, _day, _hour, _minute, 59);
	    leapSecond = true;
	} else {
	    _calendar.set(_year, _month-1, _day, _hour, _minute, _second);
	}
	_calendar.set(Calendar.MILLISECOND,0);

	long whole = lfloor(_calendar.getTimeInMillis(), 1000);

	long scale;
	if(_enableLeapSeconds) {
	    scale = LeapSeconds.unixTimeToTai(whole * 1000000 + _microsecond);
	} else {
	    scale = (whole * 1000 - D58) * 1000 + _microsecond;
	}

	if(leapSecond) {
	    scale += 1000000;
	}

// 	_time = _timeScale.scaleToTai(scale);
	_time = scale;
    }

    /**
     * Add specified number of calendar seconds to the current time.<p>
     *
     * For TimeScales such as TAI, UTC and TT, which have seconds equal to the
     * SI second, this has the same effect as <tt>TaiTime.addSeconds</tt>.
     * For non-linear time scales, such as TDB, this method adds calendar
     * seconds, whereas <tt>TaiTime.addSeconds</tt> adds SI seconds.
     *
     * @param seconds Number of calendar seconds to be added
     */
    void addSeconds(long seconds) {
        _time += seconds * 1000000;
	convertTimeToFields();
    }

    /**
     * Add specified number of calendar microseconds to the current time.<p>
     *
     * For TimeScales such as TAI, UTC and TT, which have seconds equal to the SI
     * second, this has the same effect as <tt>TaiTime.addMicrosecondSeconds</tt>.
     *
     * @param seconds Number of calendar microseconds to be added
     */
    void addMicroseconds(long microseconds) {
        _time += microseconds;
	convertTimeToFields();
    }

    /**
     * Return the largest long value less than or equal to a/b.<p>
     *
     * For example:
     * <pre>
     *     lfloor(-60, 10) == -6
     *     lfloor(-57,10) == -6.
     *     lfloor(57,10)  == 5.
     * </pre><p>
     *
     * Note that simple integer division truncates towards zero (e.g. -57/10 == -5).
     *
     * @param a numerator
     * @param b divisor
     * @return largest long value <= a/b
     */
    private static long lfloor(long a, long b) {
	if((a < 0) && (a % b != 0)) {
	    return a / b - 1;
	} else {
	    return a / b;
	}
    }

    /**
     * Returns a String representation of this object.<p>
     *
     * The exact details of the representation are unspecified and subject
     * to change.
     *
     * @return String representation of this object
     */
    public String toString() {
	if(_calendar.get(Calendar.MILLISECOND) != 0) {
	    throw new RuntimeException("State invariant error");
	};
	return "[" + _year + "," + _month + "," + _day + "," + _hour
	       + "," + _minute + "," + _second + "," + _microsecond + "]";
    }
}

