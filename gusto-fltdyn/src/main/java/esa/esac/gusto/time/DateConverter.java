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

import java.util.Date;

/**
 * Converts between <tt>TaiTime</tt> and <tt>Date</tt> representations
 * of time.<p>
 * 
 * <tt>TaiTime</tt> represents the elapsed time since the epoch 1958.
 * The Java <tt>Date</tt> class represents the approximate elapsed time
 * since the epoch 1970, but skips leap-seconds. Hence conversion between
 * these two representations is not exact at leap seconds. For most purposes
 * it is best to use a TimeFormat to format and parse TaiTimes directly,
 * avoiding the conversion to and from Java Dates.<p>
 *
 * The Java Date 1970 epoch is taken to be: 1972-01-01 UTC - (2 * 365 * 86400)
 * seconds (SI) and not 1970-01-01 UTC. This is consistent with the behaviour
 * of the Java <tt>Calendar</tt> and <tt>DateFormat</tt> classes. For example,
 * <tt>Date(2*365*86400L*1000)</tt> is interpreted as 1 January 1972 UTC.<p>
 *
 * Limitations:<br>
 * <ul>
 * <li> Dates before 1 January 1972 are not supported.
 *
 * <li> Leap-seconds are only valid for dates up to the end of the period
 *      covered by the leap-second table.
 * </ul>
 *
 * Example:<pre>
 *   Date d = new Date();
 *   TaiTime ft = DateConverter.dateToTaiTime(d);
 *   // Convert it back to a Date
 *   Date date2  = DateConverter.TaiTimeToDate(ft); 
 * </pre>
 *
 * @author  Jon Brumfitt
 * @see <a href="http://hpiers.obspm.fr/eop-pc/earthor/utc/TAI-UTC_tab.html">
 * Internation Earth Rotation Service - Leap-second data</a>
 */
public final class DateConverter {

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private DateConverter() {
    }

    /**
     * Convert a TaiTime (microsecond TAI) to a Date.<p>
     *
     * Any time within a leap-second is aliased onto the start of the next second
     * (i.e. midnight). Consequently, any time within a leap second will treated
     * as the start of the next day (midnight).<p>
     *
     * Any fraction of a millisecond in the TaiTime is simply truncated, rather
     * than rounded.
     *
     * @param tai  The TaiTime to be converted
     * @throws IllegalArgumentException if 'fine' is before 1 Jan 1972 UTC.
     */
    public static Date TaiTimeToDate(TaiTime tai) {
	return new Date(LeapSeconds.compressLeapSeconds(tai.microsecondsSince1958()) / 1000);
    }

    /**
     * Convert a Date to a TaiTime (microsecond TAI).<p>
     *
     * At a leap-second, the TaiTime value jumps by one second, from
     * 23:59:59.999999 UTC to 00:00:00.000000 UTC.
     *
     * @param date  The Date to be converted
     * @throws IllegalArgumentException if 'date' is before 1 Jan 1972 UTC.
     */
    public static TaiTime dateToTaiTime(Date date) {
 	return new TaiTime(LeapSeconds.insertLeapSeconds(date.getTime() * 1000));
    }
}

