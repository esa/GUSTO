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

/**
 * Format for parsing and converting MJD2000 Modified Julian Dates.<p>
 *
 * Modified Julian Dates are normally measured using the Universal Time (UT)
 *  time scale. This can be approximated using MJD2000(UTC), to an accuracy
 * of better than one second, although the presence of leap-seconds leads to
 * ambiguities.<p>
 *
 * To determine time intervals in a uniform time system, it is better to use
 * MJD2000(TAI) or MJD2000(TT), which are based on the TAI and Terrestrial 
 * Time (TT), respectively.
 *
 * @author  Jon Brumfitt
 */
public class Mjd2000TimeFormat implements TimeFormat {

    /**
     * 1-Jan-1958 TAI in microseconds from 1-Jan-1970 TAI.
     */
    private static final long T1970_1958 = ((1970-1958) * 365 + 3) * 86400L * 1000000;

    private TimeScale _timeScale;  // UTC, TAI, TT, etc

    /**
     * Create an MjdTimeFormat for the TAI time scale.
     */
    public Mjd2000TimeFormat() {
	_timeScale = new TaiTimeScale();
    }

    /**
     * Create an MjdTimeFormat for a specified time scale.
     *
     * Example:
     * <pre>
     *     TimeFormat format = new MjdTimeFormat(TimeScale.UTC);
     * </pre>
     */
   public Mjd2000TimeFormat(TimeScale scale) {
	_timeScale = scale;
    }

    /**
     * Format a TaiTime.<p>
     *
     * @param ft The time to be formatted
     * @return Formatted String representation of time
     */
    public String format(TaiTime ft) {
	return "" + TaiTimeToMjd2000(ft);
    }

    /**
     * Parse a time String as a TaiTime.
     *
     * @param s The String to be parsed
     * @return the TaiTime value
     */
    public TaiTime parse(String s) {
	double mjd2000 = Double.parseDouble(s);
	return mjd2000ToTaiTime(mjd2000);
    }

    /**
     * Convert a TaiTime to a MJD2000 date.
     *
     * @param ft The TaiTime to be converted
     * @return The equivalent Modified Julian Date
     */
    public double TaiTimeToMjd2000(TaiTime ft) {
	long tai = ft.microsecondsSince1958();

	// Freeze time during leap seconds for UTC
	if(_timeScale.getName().equals("UTC")) {
	    int leapSecs = LeapSeconds.leapSeconds(tai);
	    if(LeapSeconds.isLeapSecond(tai / 1000000 * 1000000)) {
		tai = tai / 1000000 * 1000000;
	    }
	    tai -= leapSecs * 1000000; 
	}

	long scale = _timeScale.taiToScale(tai);
	return (scale - 15340 * 86400000000L) / 86400000000d;
    }

   /**
     * Convert an MJD2000 Date to a TaiTime.
     *
     * @param mjd2000 The Modified Julian Date to be converted
     * @return The equivalent TaiTime
     */
    public TaiTime mjd2000ToTaiTime(double mjd2000) {
	long tai = _timeScale.scaleToTai(((long)(mjd2000 * 86400000000d)) 
					 + 15340 * 86400000000L);
	
	// Insert leap seconds for UTC
	if(_timeScale.getName().equals("UTC")) {
	    tai = LeapSeconds.unixTimeToTai(tai - T1970_1958);
	}
	return new TaiTime(tai);
    }
}

