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

package esa.esac.gusto.math.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.math.SexagesimalFormatter;
import esa.esac.gusto.math.SexagesimalFormatter.Mode;
import esa.esac.gusto.util.FormatException;

import org.junit.Test;

public class SexagesimalFormatterTest {

    public static final double EPS = 1E-15; // Small margin for rounding errors

    /**
     * Test that parsing an illegal String throws an exception.
     */
    @Test(expected=FormatException.class)
    public void testZero() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	@SuppressWarnings("unused")
	double deg = fmt.parseDegrees("+12d34n56.012345s");
    }

    /**
     * Test formatting and parsing of an hour angle with "hms" separators.
     */
    @Test
    public void testHourAngleHms() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_HMS_LOWER);
	fmt.setDecimals(6);
	double sec = (12 * 60 + 34) * 60 + 56.012345;

	String str1 = fmt.formatDegrees(sec / 3600.0 * 15.0);
	assertEquals("12h34m56.012345s", str1);

	String str2 = fmt.formatSeconds(sec * 15.0);
	assertEquals("12h34m56.012345s", str2);

	String str3 = fmt.formatRadians(Math.toRadians(sec / 3600.0 * 15.0));
	assertEquals("12h34m56.012345s", str3);

	double d = fmt.parseDegrees(str1);
	assertEquals(sec / 3600.0 * 15.0, d, EPS);

	double s = fmt.parseSeconds(str1);
	assertEquals(sec, s/15.0, EPS);

	double r = fmt.parseRadians(str1);
	assertEquals(Math.toRadians(sec / 3600.0 * 15.0), r, EPS);
    }

    /**
     * Test formatting and parsing of an hour angle with upper-case separators.
     */
    @Test
    public void testHourAngleHMS() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_HMS_UPPER);
	fmt.setDecimals(6);
	double sec = (12 * 60 + 34) * 60 + 56.012345;

	String str = fmt.formatDegrees(sec / 3600.0 * 15.0);
	assertEquals("12H34M56.012345S", str);

	double d = fmt.parseDegrees(str);
	assertEquals(sec / 3600.0 * 15.0, d, EPS);
    }

    /**
     * Test formatting and parsing zero value.
     */
    @Test
    public void testZeroValue() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_HMS_LOWER);
	fmt.setDecimals(3);

	String str = fmt.formatDegrees(0);
	assertEquals(" 0h 0m 0.000s", str);

	double d1 = fmt.parseDegrees(str);
	assertEquals(0, d1, EPS);

	// Parse again without the leading spaces
	double d2 = fmt.parseDegrees("0h0m0.0s");
	assertEquals(0, d2, EPS);
    }

    /**
     * Test formatting and parsing hour-angle with leading zeros.
     */
    @Test
    public void testLeadingZerosHms() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_HMS_LOWER);
	fmt.setDecimals(3);
	fmt.setShowLeadingZeros(true);

	String str = fmt.formatDegrees(0);
	assertEquals("00h00m00.000s", str);

	double d = fmt.parseDegrees(str);
	assertEquals(0, d, EPS);
    }

    /**
     * Test formatting and parsing DMS angle with leading zeros.
     */
    @Test
    public void testLeadingZerosDms() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	fmt.setDecimals(3);
	fmt.setShowLeadingZeros(true);

	String str = fmt.formatDegrees(0);
	assertEquals("000d00m00.000s", str);

	double d = fmt.parseDegrees(str);
	assertEquals(0, d, EPS);
    }

    /**
     * Test formatting and parsing of an angle with "dms" separators.
     */
    @Test
    public void testDms() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	fmt.setDecimals(6);
	double sec = (12 * 60 + 34) * 60 + 56.012345;

	String str1 = fmt.formatDegrees(sec / 3600.0);
	assertEquals(" 12d34m56.012345s", str1);

	String str2 = fmt.formatSeconds(sec);
	assertEquals(" 12d34m56.012345s", str2);

	String str3 = fmt.formatRadians(Math.toRadians(sec / 3600.0));
	assertEquals(" 12d34m56.012345s", str3);

	double d = fmt.parseDegrees(str1);
	assertEquals(sec / 3600.0, d, EPS);

	double s = fmt.parseSeconds(str1);
	assertEquals(sec, s, EPS);

	double r = fmt.parseRadians(str1);
	assertEquals(Math.toRadians(sec / 3600.0), r, EPS);
    }

    /**
     * Test formatting of an angle with positive and negative values.
     */
    @Test
    public void testPositiveAndNegativeDms() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	fmt.setDecimals(6);

	double sec = (12 * 60 + 34) * 60 + 56.012345;

	// Test default
	String s1 = fmt.formatDegrees(sec / 3600.0);
	assertEquals(" 12d34m56.012345s", s1);
	String s2 = fmt.formatDegrees(-sec / 3600.0);
	assertEquals("- 12d34m56.012345s", s2);

	// Repeat with "+" sign enabled
	fmt.setShowPlusSign(true);
	String s3 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+ 12d34m56.012345s", s3);
	String s4 = fmt.formatDegrees(-sec / 3600.0);
	assertEquals("- 12d34m56.012345s", s4);

	// Repeat with "+" disabled
	fmt.setShowPlusSign(false);
	String s5 = fmt.formatDegrees(sec / 3600.0);
	assertEquals(" 12d34m56.012345s", s5);
	String s6 = fmt.formatDegrees(-sec / 3600.0);
	assertEquals("- 12d34m56.012345s", s6);
    }


    /**
     * Test formatting and parsing of an angle with upper-case separators.
     */
    @Test
    public void testDMS() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_UPPER);
	fmt.setDecimals(6);
	double sec = (12 * 60 + 34) * 60 + 56.012345;

	String str = fmt.formatDegrees(sec / 3600.0);
	assertEquals(" 12D34M56.012345S", str);

	double d = fmt.parseDegrees(str);
	assertEquals(sec / 3600.0, d, EPS);
    }

    /**
     * Test parsing with added whitespace.
     */
    @Test
    public void testParsingWhiteSpace() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	fmt.setDecimals(6);

	double deg0 = fmt.parseDegrees(" +12d34m56.012345s ");
	assertEquals(12.582225651388889, deg0, EPS);

	double deg1 = fmt.parseDegrees("+12d34m56.012345s");
	assertEquals(12.582225651388889, deg1, EPS);

	double deg2 = fmt.parseDegrees("-12d 34m 56.012345s");
	assertEquals(-12.582225651388889, deg2, EPS);

	double deg3 = fmt.parseDegrees(" 12 d\t34 m\t56.012345 s ");
	assertEquals(12.582225651388889, deg3, EPS);

	double deg4 = fmt.parseDegrees("12d34m56.s");
	assertEquals(12.582222222222223, deg4, EPS);

	double deg5 = fmt.parseDegrees("12d34m56s");
	assertEquals(12.582222222222223, deg5, EPS);
    }

    /**
     *  Test formatting and parsing and angle with symbolic separators.
     */
    @Test
    public void testDmsSymbols() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.DEC_DMS_SYMBOL);
	fmt.setDecimals(6);
	double sec = (12 * 60 + 34) * 60 + 56.012345;

	String str = fmt.formatDegrees(sec / 3600.0);
	assertEquals(" 12\u00b034'56.012345\"", str);

	double deg = fmt.parseDegrees(" 12 \u00b0\t \t34 '\t56.012345 \" ");
	assertEquals(12.582225651388889, deg, EPS);
    }

    /**
     *  Test formatting and parsing with seconds separator before decimal point.
     */
    @Test
    public void testSeparatorBeforePoint() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.DEC_DMS_SYMBOL);
	fmt.setSeparatorBeforePoint(true);
	fmt.setDecimals(6);
	double sec = (12 * 60 + 34) * 60 + 56.012345;

	String str = fmt.formatDegrees(sec / 3600.0);
	assertEquals(" 12\u00b034'56\".012345", str);

	double deg = fmt.parseDegrees(" 12 \u00b0\t \t34 '\t56\" .012345 ");
	assertEquals(12.582225651388889, deg, EPS);
    }

    /**
     * Test formatting with various numbers of decimal places.
     */
    @Test
    public void testSettingDecimalPlaces() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.DEC_DMS_SYMBOL);
	fmt.setShowPlusSign(true);
	fmt.setSeparatorBeforePoint(true);
	fmt.setDecimals(6);

	double sec = (12 * 60 + 34) * 60 + 56.0123456;
	String s1 = fmt.formatDegrees(-sec / 3600.0);
	assertEquals("-12\u00b034'56\".012346", s1);

	fmt.setDecimals(0);
	String s2 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\"", s2);

	fmt.setDecimals(1);
	String s3 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".0", s3);

	fmt.setDecimals(2);
	String s4 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".01", s4);

	fmt.setDecimals(3);
	String s5 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".012", s5);

	fmt.setDecimals(6);
	String s6 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".012346", s6);

	fmt.setDecimals(7);
	String s7 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".0123456", s7);

	fmt.setDecimals(8);
	String s8 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".01234560", s8);

	fmt.setDecimals(9);
	String s9 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\".012345600", s9);

	fmt.setOmitDecimalPoint(true);
	fmt.setDecimals(3);
	String s10 = fmt.formatDegrees(sec / 3600.0);
	assertEquals("+12\u00b034'56\"012", s10);
    }


    /**
     * Test that setting a negative number of decimal places throws an exception.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNegativeDecimalPlaces() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	fmt.setDecimals(-1);
    }

    /**
     * Test that setting too large a number of decimal places throws an exception.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testTooManyDecimalPlaces() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	fmt.setDecimals(10);
    }

    /**
     * Test formatting and parsing with custom separators.
     */
    @Test
    public void testCustomSeparators() {
	double sec = (12 * 60 + 34) * 60 + 56.0123456;
	SexagesimalFormatter fmt3 = new SexagesimalFormatter("abc", false, false);
	fmt3.setDecimals(6);

	String s1 = fmt3.formatDegrees(sec / 3600.0);
	assertEquals(" 12a34b56.012346c", s1);

	double deg = fmt3.parseDegrees(" 12 a\t \t34 b\t56.012345 c ");
	assertEquals(12.582225651388889, deg, EPS);

	// Repeat with separator before decimal point.
	fmt3.setSeparatorBeforePoint(true);

	String s2 = fmt3.formatDegrees(sec / 3600.0);
	assertEquals(" 12a34b56c.012346", s2);

	double deg2 = fmt3.parseDegrees(" 12 a\t \t34 b\t56 c .012345 ");
	assertEquals(12.582225651388889, deg2, EPS);

	// Now try with a missing seconds separator

	SexagesimalFormatter fmt4 = new SexagesimalFormatter("::", false, false);
	fmt4.setDecimals(3);

	String s3 = fmt4.formatDegrees(sec / 3600.0);
	assertEquals(" 12:34:56.012", s3);

	double deg3 = fmt4.parseDegrees(" 12 :\t \t34 :\t56.012345");
	assertEquals(12.582225651388889, deg3, EPS);
    }

    /**
     * Test the toString method.
     * Just checks that it does not throw an exception. The return value is
     * not checked as the method makes no guarantee about the String contents.
     */
    @Test
    public void testToString() {
	SexagesimalFormatter fmt = new SexagesimalFormatter(Mode.RA_DMS_LOWER);
	String s = fmt.toString();
	assertTrue(s != null);
    }
}


