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
 * Utility class providing conversions between TaiTime, UNIX time and Java Date.<p>
 * 
 * UNIX time (also known as POSIX time) is a format used in operating
 * systems to represent time when leap-seconds are not relevant.
 * It is the number of (micro-)seconds since the epoch 1970-01-01T00:00:00 
 * UTC excluding leap seconds. Each day in UNIX time has exactly 86400
 * seconds. The UNIX time does not increase monotonically as it jumps 
 * backwards by one second at the end of a leap second.<p>
 * 
 * Conversions between TAI and UNIX time are approximate during the
 * one-second duration of a leap-second. Consequently, this representation
 * should only used for interfacing with other systems that use UNIX time.<p>
 * 
 * This class represents UNIX times as long integers expressed in microseconds
 * since the 1970 epoch. The Java Date and GregorianCalendar classes also
 * use UNIX time but with the value in milliseconds.<p>
 * 
 * Leap-seconds were not introduced until 1972-01-01 UTC, at which time
 * the number of leap-seconds was defined to be exactly 10. Between 1970
 * and 1972, the length of the second was varied to keep UTC in
 * approximate agreement with Solar time. Conversions are only supported
 * for times after 1972-01-01T00:00:00 UTC.<p>
 *
 * Limitations:<br>
 * <ul>
 * <li> Dates before 1 January 1972 are not supported.
 *
 * <li> Leap-seconds are only valid for dates up to the end of the period
 *      covered by the leap-second table.
 * </ul>
 *
 * @author  Jon Brumfitt
 * @see <a href="http://hpiers.obspm.fr/eop-pc/earthor/utc/TAI-UTC_tab.html">
 * Internation Earth Rotation Service - Leap-second data</a>
 */
public final class UnixTime {

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private UnixTime() {
    }

    /**
     * Convert a TaiTime to a Java Date.<p>
     *
     * Leap seconds are aliased onto the next second. Consequently, two
     * TAI times differing by one second may result in the same Java Date.<p>
     *
     * Any fraction of a millisecond in the TaiTime is simply truncated, 
     * rather than being rounded.
     *
     * @param tai  The TaiTime to be converted
     * @throws IllegalArgumentException if 'fine' is before 1 Jan 1972 UTC.
     */
    public static Date TaiTimeToDate(TaiTime tai) {
	return new Date(taiToUnixTime(tai) / 1000);
    }

    /**
     * Convert a Java Date to a TaiTime.<p>
     *
     * Leap seconds are aliased onto the next second. Consequently, conversions
     * never result in a TAI value falling within a leap second.<p>
     * 
     * @param date  The Date to be converted
     * @throws IllegalArgumentException if 'date' is before 1 Jan 1972 UTC.
     */
    public static TaiTime dateToTaiTime(Date date) {
 	return unixToTaiTime(date.getTime() * 1000);
    }
    
    /**
     * Convert TaiTime to UNIX time in microseconds.<p>
     * 
     * The parameter <tt>freeze</tt> should be <tt>false</tt> for the normal
     * POSIX / UNIX time conventions. In this case, the UNIX time jumps backwards
     * by a second at the end of a leap second. Consequently, two TAI times can
     * map onto the same UNIX time.<p>
     * 
     * If it is important that there are no backward jumps, but repeated times
     * are allowed, <tt>freeze</tt> may be set to <tt>true</tt>. In this case,
     * all TAI times within the leap second are mapped onto the start of the 
     * next second and the UNIX time is effectivelly frozen for the duration 
     * of the leap second.<p>
     *
     * Does not support times before 1 Jan 1972 UTC.
     *
     * @param tai TaiTime
     * @return Microseconds since 1970 omitting leap-seconds
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    public static long taiToUnixTime(TaiTime tai, boolean freeze) {
	return LeapSeconds.taiToUnixTime(tai.microsecondsSince1958(), freeze);
    }
    
    /**
     * Convert a TaiTime to UNIX time assuming normal POSIX time conventions.<p>
     * 
     * Equivalent to <tt>taiToUnixTime(tai, false)</tt>.
     * 
     * @param tai The TaiTime
     * @return UNIX time in microseconds since 1970 epoch (excluding leap seconds)
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    public static long taiToUnixTime(TaiTime tai) {
	return LeapSeconds.taiToUnixTime(tai.microsecondsSince1958(), false);
    }
    
    /**
     * Convert UNIX time to TaiTime.<p>
     *
     * Leap seconds are aliased onto the next second. Consequently, conversions
     * never result in a TAI value falling within a leap second.<p>
     *
     * Does not support times before 1 Jan 1972 UTC.
     *
     * @param unixTime  Microseconds since 1970 omitting leap seconds
     * @return TaiTime
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    public static TaiTime unixToTaiTime(long unixTime) {
	return new TaiTime(LeapSeconds.unixTimeToTai(unixTime));
    }
}

