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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format for parsing/printing time using the Gregorian Calendar.<p>
 *
 * This is based on the CCSDS ASCII Time Format A. For example:
 * "1986-12-31T00:23:59.123456Z". However, it supports other time
 * scales such as TAI and TT.
 *
 * @author  Jon Brumfitt
 */
public class SimpleTimeFormat implements TimeFormat {

    private GregorianTimeCalendar _calendar;
    private TimeScale _timeScale;           // UTC, TAI, TT, etc
    private Pattern   _pattern;             // Regular expression
    private boolean   _strictFraction;      // Enforce fraction format
    private boolean   _round;               // Round least significant digit
    private int       _decimals;            // Decimal places [0,6]

    /**
     * Create a SimpleTimeFormat for the TAI time scale.
     */
    public SimpleTimeFormat() {
	this(new TaiTimeScale());
    }

    /**
     * Create a SimpleTimeFormat for a specified time scale.
     *
     * Example:
     * <pre>
     *     TimeFormat format = new SimpleTimeFormat(TimeScale.UTC);
     * </pre>
     *
     * @param scale The TimeScale to be used by this TimeFormat
     */
   public SimpleTimeFormat(TimeScale scale) {
	_timeScale = scale;

	_calendar = new GregorianTimeCalendar();
	if(_timeScale.getName().equals("UTC")) {
	    _calendar.enableLeapSeconds(true);
	}
	initPattern();
    }

    /**
     * Specify whether parsing should enforce exact number of decinal places.<p>
     *
     * If enabled, the string to be parsed must have the number of decimal places
     * specified by <tt>setDecimals</tt>. If disabled, any number of decimal places
     * in the range [0,6] will be accepted.
     *
     * @param enable True to enforce exact number of decimal places
     */
    public void setStrictFraction(boolean enable) {
	_strictFraction = enable;
	initPattern();
    }

    /**
     * Enable rounding to least significant digit.<p>
     *
     * Rounds the fractional seconds to the number of decimal places
     * set by <tt>setDecimals</tt>. If decimals is set to 6 (the maximum),
     * no rounding takes place. If enable is false, additional digits
     * are simply truncated.
     *
     * @param enable True to enable rounding
     */
    public void setRounding(boolean enable) {
        _round = enable;
    }

    /**
     * Set the number of decimal places for seconds.<p>
     *
     * If <tt>setRounding</tt> is enabled, the value is rounded to the
     * specified number of decimal places, otherwise it is truncated.<p>
     *
     * If decimals==0, no decimal point is inserted on output or expected
     * on input.
     *
     * @param decimals Number of decimal places (0 to 6)
     */
    public void setDecimals(int decimals) {
	if((decimals < 0) || (decimals > 6)) {
	    throw new IllegalArgumentException("Decimal places must be in range 0 to 6");
	}
	_decimals = decimals;
	initPattern();
    }

    /**
     * Initialise the regular expression pattern for parsing.
     */
    private void initPattern() {
	String s = "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})";

	if(_strictFraction && _decimals==0) {
	    _pattern = Pattern.compile(s);
	} else if(_strictFraction) {
	    _pattern = Pattern.compile(s + "\\.(\\d{" + _decimals + "})");
	} else {
	    _pattern = Pattern.compile(s + "(?:\\.(\\d{1,6}))??");
	}
    }

    /**
     * Format a TaiTime.<p>
     *
     * @param ft The time to be formatted
     * @return Formatted String representation of time
     */
    public String format(TaiTime ft) {
	long tai = ft.microsecondsSince1958();
	long scale = _timeScale.taiToScale(tai);
	String suffix = _timeScale.getSuffix();

	// Prepend a space to suffix except for "Z" (UTC).
	if(!_timeScale.getSuffix().equals("Z")) {
	    suffix = " " + suffix;
	}

	// Round seconds to required number of decimal places.
	if(_round) {
	    scale = roundDecimals(scale, _decimals);
	}

	// This could be a static field of the class.
        NumberFormat nf = new DecimalFormat();
        nf.setMinimumIntegerDigits(2);
        nf.setGroupingUsed(false);

	// Use a GregorianTimeCalendar to calculate field values.
	_calendar.setTime(new TaiTime(scale));
	int[] fields = _calendar.getFields();
	String s = "" + fields[0]  + "-" // Field width should be 4
	              + nf.format(fields[1]) + "-"   
	              + nf.format(fields[2]) + "T"
	              + nf.format(fields[3]) + ":"
	              + nf.format(fields[4]) + ":"
	              + nf.format(fields[5]);

	return s + formatDecimals(scale, _decimals) + suffix;
    }

    /**
     * Parse a time String as a TaiTime.q
     *
     * @param s The String to be parsed
     * @return the TaiTime value
     */
    public TaiTime parse(String s) {
	String suffix = _timeScale.getSuffix();

	// Check that expected suffix is present.
	if(!s.endsWith(suffix)) {
	    throw new IllegalArgumentException("Suffix \"" 
					       + suffix + "\" expected");
	}
	// Trim off suffix and white space.
	s = s.substring(0, s.length() - suffix.length()).trim();

	Matcher m = _pattern.matcher(s);
	if(!m.matches()) {
	    throw new IllegalArgumentException("Invalid time: " + s);
	}
	int yr = Integer.parseInt(m.group(1));
	int mo = Integer.parseInt(m.group(2));
	int dy = Integer.parseInt(m.group(3));
	int hr = Integer.parseInt(m.group(4));
	int mn = Integer.parseInt(m.group(5));
	int sc = Integer.parseInt(m.group(6));
	int us = 0;
	if(m.groupCount()==7 && m.group(7) != null) {
	    us = Integer.parseInt(padRight(m.group(7),6,'0'));
	}		      

	_calendar.setFields(new int[]{yr,mo,dy,hr,mn,sc,us});
	long scale = _calendar.getTime().microsecondsSince1958();

	return new TaiTime(_timeScale.scaleToTai(scale));
    }

    /**
     * Pad a String with padding character to the right to specified field width.     
     */
    private static String padRight(String s, int fieldWidth, char padding) {
	int n = fieldWidth - s.length();
	for(int i=0; i<n; i++) {
	    s = s + padding;
	}
	return s;
    }

    /**
     * Format the fractional seconds.
     *
     * @param tai Microseconds since TAI epoch
     * @param decimals The number of decimal places
     * @return String representation of fractional seconds
     */
    protected String formatDecimals(long tai, int decimals) {
        if(decimals <= 0) {
	    return "";
	}
	long microseconds = tai % 1000000;
	for(int i=0; i<6 - decimals; i++) {
	    microseconds /= 10;
	}
	NumberFormat nf = new DecimalFormat();
	nf.setMaximumIntegerDigits(decimals);
	nf.setMinimumIntegerDigits(decimals);
	nf.setGroupingUsed(false);

	return "." + nf.format(microseconds);
    }

    /**
     * Round number in microseconds to given number of decimal places 
     * of seconds.<p>
     *
     * e.g. roundDecimals(12345678, 4) = 12345700
     *
     * @param value The value in microseconds
     * @param digits The number of decimal places of seconds required
     * @return the rounded value
     */
    protected long roundDecimals(long value, int digits) {
	// Do nothing if all 6 digits are required.
	if(digits >= 6) {
	    return value;
	}
	int factor = 1;
	for(int j=digits; j<5; j++) {
	    value /= 10;
	    factor *= 10;
	}
	return ((value + 5) / 10) * 10 * factor;
    }
}

