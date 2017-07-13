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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling leap-seconds for the UTC time scale.<p>
 *
 * Leap seconds were introduced on 1 January 1972, at which time the
 * difference between UTC and TAI was: TAI = UTC + 10. Leap seconds are
 * introduced from time-to-time, on the first of January or the first of
 * July.<p>
 *
 * The table of leap-seconds is read from a file. This must be updated
 * when new leap seconds are announced (see reference below for details).
 * A later verssion of this library might update the table automatically,
 * using a suitable server.<p>
 *
 * Limitations:
 *<br>
 * <ul>
 * <li> Leap-seconds are only valid for dates from Jan 1, 1972 up to
 *      the end of the period covered by the leap-second table. New
 *      leap seconds are only known up to six months in advance.
 *
 * <li> This class does not currently support negative leap-seconds (i.e.
 *      a second is skipped, rather than added). However, this is unlikely
 *      to occur in the near future.
 *
 * <li> The leap-second table is not re-read automatically when it is updated.
 * </ul>
 *
 * @author  Jon Brumfitt
 * @see <a href="http://hpiers.obspm.fr/eop-pc/earthor/utc/TAI-UTC_tab.html">
 * Internation Earth Rotation Service - Leap-second data</a>
 */
public class LeapSeconds {

    private static final String FILE_NAME = "leapSeconds";

    /** TAI-UTC seconds at 1 Jan 1972. */
    private static final int LEAP_1972 = 10;

    /** Java Date representation of TAI epoch 1 Jan 1958. */
    private static final long D58 = -((1970 - 1958) * 365 + 3) * 86400L * 1000;

    /** Java Date representation of 1 Jan 1972 UTC. */
    private static final long D72 = (1972 - 1970) * 365 * 86400L * 1000;

    /** Microseconds (epoch 1958) of 1 Jan 1972 UTC. */
    private static final long UTC_72 = (D72 - D58 + LEAP_1972 * 1000) * 1000;

    /** Number of seconds from 1 Jan 1958 to 1 Jan 1972. */
    private static final long D72_58 = (D72 - D58) * 1000;


    /**
     * Table of leap seconds in microseconds since the epoch 1958.
     * An entry is the end of the leap second (e.g. 00:00:00 and not 23:59:60).
     */
    private static long[] _leap;

    static {
	loadLeapSecondData();
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private LeapSeconds() {
    }

    /**
     * Reload the table of leap-second data.
     *
     * @throws RuntimeException if the table cannot be read succesfully.
     */
    public static void loadLeapSecondData() {

	// Regular expression for parsing imput lines
	String regexp = "^(\\d{4}-\\d{2})[ \t]*(?:#.*)$";
	Pattern pattern = Pattern.compile(regexp);

	// Format used by leap-second table
	final SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM");
	formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	formatter.setLenient(false);

	// First read into a vector as the length is unknown.
	Vector<Long> times = new Vector<Long>();
	try {
	    InputStream is = LeapSeconds.class.getResourceAsStream(FILE_NAME);

	    if(is == null) {
		throw new RuntimeException("Cannot read leap-second table");
	    }

	    DataInputStream in = new DataInputStream(is);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

	    while(reader.ready()) {
		String line = reader.readLine().trim();
		// Skip blank lines and comment lines.
		if(line.equals("") || line.startsWith("#")) {
		    continue;
		}

		Matcher m = pattern.matcher(line);
		if(!m.matches()) {
		    throw new RuntimeException("Invalid entry in leap-second table: \""
			    + line + "\"");
		}
		long t = formatter.parse(m.group(1), new ParsePosition(0)).getTime();
		times.add(new Long(t));
	    }
	    reader.close();
	    in.close();
	} catch(IOException e) {
	    throw new RuntimeException("Cannot read leap-second table", e);
	}

	// Convert the Vector of dates into an array of TAI times.
	Iterator<Long> it = times.iterator();
	int i = 0;
	long[] leap = new long[times.size()];
	while(it.hasNext()) {
	    long utc = it.next().longValue();
	    leap[i] = (utc + (i + 1 + LEAP_1972) * 1000 - D58) * 1000;
	    i++;
	}
	_leap = leap;
    }

    /**
     * Check that time is in valid range for leap-second calculation.
     *
     * @param tai Microseconds since TAI epoch
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    private static void checkTai(long tai) {
	if(tai < UTC_72) {
	    throw new IllegalArgumentException("Cannot handle time before 1972 UTC");
	}	
    }

    /**
     * Return true if the specified time is within a leap second.<p>
     *
     * The result is true if the UTC second value is in the range [60, 60.999999].
     *
     * @param tai Microseconds since TAI epoch
     * @return true if the time is within a leap-second
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    public static boolean isLeapSecond(long tai) {
	checkTai(tai);
	long taiSeconds = tai / 1000000 + 1; // End of the second

	// Scan table backwards as times near the present are more likely.
	for(int i = _leap.length-1; i>=0; i--) {
	    if(taiSeconds == _leap[i] / 1000000) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Return the number of leap seconds (TAI - UTC) at a specified time.
     * This includes the difference TAI-UTC = 10 that existed when leap-seconds
     * were introduced in 1972.<p>
     *
     * The result is the number of complete leap-seconds and consequently
     * increments at the end of the leap second (i.e. midnight UTC).
     *
     * @param tai Microseconds since TAI epoch
     * @return TAI - UTC seconds
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    public static int leapSeconds(long tai) {
	checkTai(tai);

	// Scan table backwards as times near the present are more likely.
	int i = _leap.length;
	while((i >= 1) && (tai < _leap[i - 1])) {
	    i--;
	}
	return LEAP_1972 + i;
    }

    /**
     * Convert TAI microseconds to UNIX time in microseconds.<p>
     * 
     * UNIX time has the epoch 1970-01-01:00:00:00 UTC and omits leap seconds.
     * 
     * If the parameter <tt>freeze</tt> is false, the UNIX time jumps backwards
     * by a second at the end of a leap second. Hence, two TAI times can map onto
     * the same UNIX time. This follows the POSIX time / UNIX time convention.<p>
     * 
     * If the parameter <tt>freeze</tt> is true, all TAI times within the leap 
     * second are mapped onto the start of the next second. Consequently, the
     * UNIX time is frozen for the duration of the leap second. This can be useful
     * if it is important that there are no backward jumps but repeated times
     * are allowed.<p>
     * 
     * Does not support times before 1 Jan 1972 UTC.
     *
     * @param tai Microseconds since TAI epoch
     * @param freeze False for POSIX convention, true to freeze time in leap-second
     * @return Microseconds since 1970 omitting leap-seconds
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
    static long taiToUnixTime(long tai, boolean freeze) {
	checkTai(tai);

	long taiSeconds = tai / 1000000; // Truncate to start of second
	boolean isLeap = false;

	int i = _leap.length;
	// Scan table backwards as times near the present are more likely.
	while((i >= 1) && (tai < _leap[i - 1])) {
	    if(taiSeconds + 1 == _leap[i - 1] / 1000000) {
		isLeap = true;
	    }
	    i--;
	}
	if(isLeap && freeze) {
	    tai = taiSeconds * 1000000;
	}
	return tai + D58 * 1000 - (LEAP_1972 + i) * 1000000L;
    }

    /**
     * Convert UNIX time to TAI microseconds.<p>
     *
     * Leap seconds are aliased onto the next second. Consequently, conversions
     * never result in a TAI value falling within a leap second.<p>
     *
     * Does not support times before 1 Jan 1972 UTC.
     *
     * @param unixTime  Microseconds since 1970 omitting leap seconds
     * @return TAI microseconds since 1958
     * @throws IllegalArgumentException if time is before 1 Jan 1972 UTC
     */
     static long unixTimeToTai(long unixTime) {
	if(unixTime < D72 * 1000) {
	    throw new IllegalArgumentException("Cannot handle time before 1972 UTC");
	}

	// Scan table backwards as times near the present are more likely.
	long tai = (unixTime - D58 * 1000) + 1000000 * (LEAP_1972 + _leap.length);
	for(int i=_leap.length; i>0; i--) {
	    if(tai < _leap[i-1]) {
		tai -= 1000000;
	    }
	}
	return tai;
    }
}

