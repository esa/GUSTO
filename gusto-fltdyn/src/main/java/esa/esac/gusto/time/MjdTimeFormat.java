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
 * Format for parsing and converting Modified Julian Dates.<p>
 *
 * Modified Julian Date (MJD) is normally measured using the Universal Time
 * (UT) time scale. This can be approximated using MJD(UTC), to an accuracy
 * of better than one second, although the presence of leap-seconds leads to
 * ambiguities.<p>
 *
 * To determine time intervals in a uniform time system, it is better to use
 * MJD(TAI) or MJD(TT), which are based on the TAI and Terrestrial Time (TT),
 * respectively.
 *
 * @author  Jon Brumfitt
 */
public class MjdTimeFormat implements TimeFormat {

    private static final int MJD_1958 = 36204;  // MJD(TAI) of 1958 TAI epoch

    /**
     * 1-Jan-1958 TAI in microseconds from 1-Jan-1970 TAI.
     */
    private static final long T1970_1958 = ((1970-1958) * 365 + 3) * 86400L * 1000000;

    private TimeScale _timeScale;  // UTC, TAI, TT, etc

    /**
     * Create an MjdTimeFormat for the TAI time scale.
     */
    public MjdTimeFormat() {
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
   public MjdTimeFormat(TimeScale scale) {
	_timeScale = scale;
    }

    /**
     * Format a TaiTime.<p>
     *
     * @param ft The time to be formatted
     * @return Formatted String representation of time
     */
    public String format(TaiTime ft) {
	return "" + TaiTimeToMjd(ft);
    }

    /**
     * Parse a time String as a TaiTime.
     *
     * @param s The String to be parsed
     * @return the TaiTime value
     */
    public TaiTime parse(String s) {
	double mjd = Double.parseDouble(s);
	return mjdToTaiTime(mjd);
    }

    /**
     * Convert a TaiTime to a Modified Julian Date.
     *
     * @param ft The TaiTime to be converted
     * @return The equivalent Modified Julian Date
     */
    public double TaiTimeToMjd(TaiTime ft) {
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
	return scale / 86400000000d + MJD_1958;
    }

   /**
     * Convert a Modified Julian Date to a TaiTime.
     *
     * @param mjd The Modified Julian Date to be converted
     * @return The equivalent TaiTime
     */
    public TaiTime mjdToTaiTime(double mjd) {
	long tai = _timeScale.scaleToTai((long)((mjd - MJD_1958) * 86400000000d));
	
	// Insert leap seconds for UTC
	if(_timeScale.getName().equals("UTC")) {
	    tai = LeapSeconds.unixTimeToTai(tai - T1970_1958);
	}
	return new TaiTime(tai);
    }
}

