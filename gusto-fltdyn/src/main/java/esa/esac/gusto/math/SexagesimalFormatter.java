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

package esa.esac.gusto.math;

import esa.esac.gusto.util.FormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formatter to parse and format sexagesimal (base-60) values.<p>
 *
 * This is intended for angles expressed in Hours-Minutes-Seconds 
 * or Degrees-Minutes-Seconds, but can also be used for times.
 *
 * @author Jon Brumfitt
 */
public class SexagesimalFormatter {
	
	private static final int DEFAULT_DECIMALS = 3;
	private static final boolean DEFAULT_SEPARATOR_BEFORE_POINT = false;
	private static final boolean DEFAULT_OMIT_DECIMAL_POINT = false;
	private static final boolean DEFAULT_SHOW_PLUS_SIGN = false;
	private static final boolean DEFAULT_SHOW_LEADING_ZEROS = false;
	private static final int MAX_DECIMALS = 9;
	
	/**
	 * Common modes for right ascensions and declinations.
	 */
    public static enum Mode {
        RA_HMS_LOWER("hms",    true,  true),   // 12h34m56.789s
        RA_HMS_UPPER("HMS",    true,  true),   // 12H34M56.789S
        RA_DMS_LOWER("dms",    false, true),   // 012d34m56.789s
        RA_DMS_UPPER("DMS",    false, true),   // 012D34M56.789S
        DEC_DMS_LOWER("dms",   false, false),  // +12d34m56.789s
        DEC_DMS_UPPER("DMS",   false, false),  // +12D34M56.789S
        DEC_DMS_SYMBOL("\u00b0'\"", false, false);  // +12˚34'56.789"
        
        private boolean _modeHourAngle;
        private String _modeSeparators;
        private boolean _nonNegative;
        
        /**
         * Create a new Mode constant.
         * 
         * @param separators String containing HMS or DMS separators
         * @param hours True if using hours (HMS) rather than degrees (DMS)
         * @param nonNegative True if value is expected to be non-negative
         */
        private Mode(String separators, boolean hours, boolean nonNegative) {
        	_modeHourAngle = hours;
        	_modeSeparators = separators;
        	_nonNegative = nonNegative;
        }
        
        /**
         * Return true if the mode represents an hour angle.
         * 
         * @return true for hour angles
         */
        boolean isHourAngle() {
        	return _modeHourAngle;
        }
        
        /**
         * Return String containing separators.
         * 
         * @return Separator String
         */
        String getSeparators() {
        	return _modeSeparators;
        }
        
        /**
         * Return whether value is always non-negative.
         * 
         * @return true if non-negative.
         */
        boolean isNonNegative() {
        	return _nonNegative;
        }
    }
    
    private Pattern _pattern;
    private boolean _hourAngle;
    private String _separators;
    private int _decimals;
    private boolean _separatorBeforePoint;
    private boolean _omitDecimalPoint;
    private boolean _showPlusSign;
    private boolean _showLeadingZeros;
    private boolean _isNonNegative;

    /**
     * Create a SexagesimalFormatter.
     *
     * @param mode The formatting Mode to be used.
     */
    public SexagesimalFormatter(Mode mode) {
    	this(mode.getSeparators(), mode.isHourAngle(), mode.isNonNegative());
    }
    
    /** 
     * Create a custom SexagesimalFormatter.
     * 
     * e.g. "::Z"     -> 12:34:56.789Z
     *      "::"      -> 12:34:56.789   // Seconds separator is missing
     *      "xyz"     -> 12x34y56z.789  // With separatorBeforePoint enabled 
     *      "   "     -> 12 34 56 .789  // With separatorBeforePoint enabled 
     *      
     * If the length of the separator String <3, trailing characters will be omitted.
     * Other characters can be omitted using the Unicode NULL character "\u0000" for
     * formatting, but are needed for parsing.<p>
     * 
     * If nonNegative is true, no space will be left for the sign, unless the
     * setShowPlusSign option is enabled.
     *
     * @param separators String containing HMS or DMS separators
     * @param hours True if using hours (HMS) rather than degrees (DMS)
     * @param nonNegative True is value should be non-negative
     */
    public SexagesimalFormatter(String separators, boolean hours, boolean nonNegative) {
    	_separators = separators;
    	_hourAngle = hours;
    	_isNonNegative = nonNegative;
    	
    	_decimals = DEFAULT_DECIMALS;
    	_separatorBeforePoint = DEFAULT_SEPARATOR_BEFORE_POINT;
    	_omitDecimalPoint = DEFAULT_OMIT_DECIMAL_POINT;
    	_showPlusSign = DEFAULT_SHOW_PLUS_SIGN;
    	_showLeadingZeros = DEFAULT_SHOW_LEADING_ZEROS;
    	
    	createPattern();
    }

    /**
     * Return the separator at given index or "" if not specified.
     * 
     * @param index Position in range [0,3]
     * @return Separator character as a String, or empty String
     */
    private String getSeparator(int index) {
    	if(_separators.length() > index) {
    		return "" + _separators.charAt(index);
    	} else {
    		return "";
    	}
    }
    
    /**
     * Set the number of decimal places of seconds for formatting, with rounding.
     * When decimals=0, the decimal point is omitted for formatting.
     *
     * @param decimals Number of decimal places
     */
    public void setDecimals(int decimals) {
    	if(decimals<0 || decimals>MAX_DECIMALS) {
    		throw new IllegalArgumentException("Decimal places must be in range [0," + MAX_DECIMALS + "]");
    	}
    	_decimals = decimals;
    }
    
    /**
     * Specify that the seconds separator is before the decimal point.
     * e.g. true ->  12˚34'56".789 
     *      false -> 12˚34'56.789" 
     * The default is false.
     *
     * @param enable True to place seconds separator before point.
     */
    public void setSeparatorBeforePoint(boolean enable) {
    	_separatorBeforePoint = enable;
    	createPattern();
    }
    
    /**
     * Specific that decimal point should be omitted when formatting.
     * This is useful with the option setSeparatorBeforePoint.
     * The default is false.
     * 
     * @param enable True to omit the decimal point
     */
    public void setOmitDecimalPoint(boolean enable) {
    	_omitDecimalPoint = enable;
    }
    
    /**
     * Use explicit "+" sign for positive angles for formatting.
     * e.g. +12d34m67.789s
     * When not enabled, positive numbers are formatted with a space 
     * for the sign, so that values displayed in columns align.
     *
     * @param enable True to force explicit "+" sign
     */
    public void setShowPlusSign(boolean enable) {
    	_showPlusSign = enable;
    }
    
    /**
     * Format using leading zeros.
     * e.g. 01˚02'03.456"
     * 
     * @param enable Enable leading zeros
     */
    public void setShowLeadingZeros(boolean enable) {
    	_showLeadingZeros = enable;
    }
    
    /**
     * Format a value in degrees as a sexagesimal String.
     *
     * @param degrees Angle in degrees
     * @return Sexagesimal string
     */
    public String formatDegrees(double degrees) {
    	boolean sign = (degrees >= 0);
    	double angle = degrees = sign ? degrees : -degrees;
    	if(_hourAngle) {
    		angle /= 15.0d;
    	}
    	double seconds = angle * 3600d;
    	
    	long k = pow10(_decimals);
    	long sk = (long)(seconds * k + 0.5);
    	int frac = (int)(sk % k);
    	int fr = (int)(sk / k);
    	int sec = fr % 60;
    	int sr = fr / 60;
    	int min = sr % 60;
    	int deg = sr / 60;
    	
        return format(sign, deg, min, sec, frac, _decimals);
    }
 
    /**
     * Format a value in seconds as a sexagesimal String.
     *
     * @param seconds Angle or time in seconds
     * @return Sexagesimal string
     */
    public String formatSeconds(double seconds) {
    	return formatDegrees(seconds / 3600.0d);
    }
    
    /**
     * Format a value in radians as a sexagesimal String.
     *
     * @param radians Angle in radians
     * @return Sexagesimal string
     */
     public String formatRadians(double radians) {
     	return formatDegrees(Math.toDegrees(radians));
     }
    
    /**
     * Parse an sexagesimal String as a value in degrees.
     *
     * @param string Sexagesimal value to be parsed
     * @return Angle in degrees
     * @throws FormatException
     */
    public double parseDegrees(String string) {
    	int[] fs = parseFields(string);
    	
    	double scale = pow10(fs[5]);
    	double value = fs[1] + (fs[2] + (fs[3] + fs[4] / scale) / 60.0d) / 60.0d;
    	
    	if(_hourAngle) {
    		value *= 15.0d;
    	}
    	return fs[0]>0 ? value : -value;
    }
    
    /**
     * Parse an sexagesimal String as a value in seconds.
     *
     * @param string Sexagesimal value to be parsed
     * @return Angle or time in seconds
     * @throws FormatException
     */
    public double parseSeconds(String string) {
    	return parseDegrees(string) * 3600.0d;
    }
    
    /**
     * Parse an sexagesimal String as a value in radians.
     *
     * @param string Sexagesimal value to be parsed
     * @return Angle in radians
     * @throws FormatException
     */
    public double parseRadians(String string) {
    	return Math.toRadians(parseDegrees(string));
    }
    
    /**
     * Parse an sexagesimal String as an array of values.
     * e.g. " 12d34m56.000789s" -> [1, 12, 34, 56, 789, 6]
     *      "-12d34m56.000789s" -> [0, 12, 34, 56, 789, 6]
     *
     * @param string Sexagesimal value to be parsed
     * @return Array of field values [sign, hrsDeg, min, sec, frac, fracWidth]
     * @throws FormatException
     */
    protected int[] parseFields(String string) {
    	Matcher m = _pattern.matcher(string.trim());
    	if(!m.matches()) {
    	    throw new FormatException("Invalid value: " + string);
    	}
    	String group1 = m.group(1);
    	boolean neg = (group1 != null) && (group1.equals("-"));
    	int hd = Integer.parseInt(m.group(2));
    	int mn = Integer.parseInt(m.group(3));
    	int sc = Integer.parseInt(m.group(4));
    	int us = 0;
    	if(m.groupCount()==5 && m.group(5) != null) {
    	    us = Integer.parseInt(padRight(m.group(5), MAX_DECIMALS, '0'));
    	}	    	
    	return new int[] {neg?0:1, hd, mn, sc, us, MAX_DECIMALS};
    }
    
    /**
     * Create a regular expression for parsing the input.
     */
    private void createPattern() {
    	String white = "[ \t]*?";
    	
    	StringBuilder buff = new StringBuilder();
    	buff.append("([+-])??");
    	buff.append(white);
        buff.append("(\\d{1,6})");
    	buff.append(white);
    	buff.append(getSeparator(0));
    	buff.append(white);
        buff.append("(\\d{1,2})");
    	buff.append(white);
    	buff.append(getSeparator(1));
    	buff.append(white);
    	buff.append("(\\d{1,2})");
    	if(_separatorBeforePoint) {
    		buff.append(white);
    		buff.append(getSeparator(2));
    		buff.append(white);
    	}
    	buff.append("(?:(?:\\.)(\\d{1," + MAX_DECIMALS + "})??)??");
    	if(!_separatorBeforePoint) {
    		buff.append(white);
    		buff.append(getSeparator(2));
    	}
    	_pattern = Pattern.compile(buff.toString()); 
    }
    
    /**
     * Format hours/degrees, minutes and seconds as a sexagesimal String.
     * 
     * The fractional value is: frac/10^fracWidth
     * e.g. [0, 12, 34, 56, 789, 6] -> "-12d34m56.000789s"
     *
     * @param sign True if non-negative
     * @param hrsDeg Hours or Degrees field
     * @param min Minutes field
     * @param sec Seconds field
     * @param frac Fractional seconds field
     * @param fracWidth Width of fractional field
     * @return Sexagesimal string
     */
    protected String format(boolean sign, int hrsDeg, int min, int sec, int frac, int fracWidth) {
    	
    	StringBuilder buff = new StringBuilder();
    	if(!sign) {
    		buff.append("-");
    	} else if(_showPlusSign) {
    		buff.append("+");
    	} else if(!_isNonNegative) {
    		buff.append(" ");
    	}
    	if(_isNonNegative && !_hourAngle) {
    		buff.append(formatValue(hrsDeg, 3));
    	} else {
    		buff.append(formatValue(hrsDeg, 2));
    	}
    	buff.append(getSeparator(0));
		buff.append(formatValue(min, 2));
    	buff.append(getSeparator(1));
		buff.append(formatValue(sec, 2));
    	if(_separatorBeforePoint) {
        	buff.append(getSeparator(2));
    	}
    	if(_decimals  > 0) {
    		if(!_omitDecimalPoint) {
    			buff.append(".");
    		}
        	String format = "%0" + fracWidth + "d";
        	buff.append(String.format(format, frac));
    	}
    	if(!_separatorBeforePoint) {
        	buff.append(getSeparator(2));
    	}
    	return buff.toString(); 
    }
    
    /**
     * Format an integer value in specified field width with leading spaces or zeros.
     * 
     * @param value Value to be formatted
     * @param width Field width in characters
     * @return Formatted repesentation
     */
    private String formatValue(int value, int width) {
    	if(_showLeadingZeros) {
    		return String.format("%0" + width + "d", value);
    	} else {
    		return String.format("%" + width + "d", value);
    	}
    }
    
    /**
     * Pad a String with padding character to the right to specified field width.   
     * 
     *  @param s String to be padded
     *  @param fieldWidth Width of field
     *  @param padding Character to be used for padding
     *  @return Padded String
     */
    private static String padRight(String s, int fieldWidth, char padding) {
    	int n = fieldWidth - s.length();
    	for(int i=0; i<n; i++) {
    		s = s + padding;
    	}
    	return s;
    }
    
    /**
     * Return 10 to the specified power.
     * 
     * @param n Exponent (>=0)
     * @return 10^n
     */
    private static long pow10(int n) {
    	long k = 1;
    	for(int i=0; i<n; i++) {
    		k *= 10;
    	}
    	return k;
    }
    
    /**
     * Return a String representation of this object.
     *
     * @return String representation of this object.
     */
    public String toString() {
    	StringBuilder buff = new StringBuilder(getClass().getName() + "\n");
    	buff.append(" HourAngle        = " + _hourAngle + "\n");
    	buff.append(" Separators       = " + _separators + "\n");
    	buff.append(" Decimals         = " + _decimals + "\n");
    	buff.append(" SepBeforePoint   = " + _separatorBeforePoint + "\n");
    	buff.append(" OmitDecimalPoint = " + _omitDecimalPoint + "\n");
    	buff.append(" ShowPlusSign     = " + _showPlusSign + "\n");
    	buff.append(" ShowLeadingZeros = " + _showLeadingZeros + "\n");
    	buff.append(" NonNegative      = " + _isNonNegative + "\n");
    	return buff.toString();
    }
}





