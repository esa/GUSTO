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

package esa.esac.gusto.time.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.time.UnixTime;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Test harness for SimpleTimeFormat.
 *
 * @author  Jon Brumfitt
 */
public class SimpleTimeFormatTest {
    private static final long D58 = -((1970 - 1958) * 365 + 3) * 86400L * 1000000;

    /** Check parse then format gives the same string. */
    @Test
    public void testRoundTrip() {
	SimpleTimeFormat taiFormatter = new SimpleTimeFormat(TimeScale.TAI);

	String s1 = "1993-06-30T23:59:59 TAI";
	TaiTime ft = taiFormatter.parse(s1);
	String s2 = taiFormatter.format(ft);
	assertTrue(s1.equals(s2));
    }

    /** Check with a known TAI value. */
    @Test
    public void testKnownValue() {
	SimpleTimeFormat taiFormatter = new SimpleTimeFormat(TimeScale.TAI);

	String s1 = "2000-01-01T00:00:00 TAI";
	TaiTime ft = taiFormatter.parse(s1);

	// 10 leap years expected from 1958 to 1972
	long k = (((2000-1958)*365 + 10) * 86400) * 1000000L;
	assertEquals(ft.microsecondsSince1958(), k);
    }

    /**
     * Check that CcsdsATimeFormat gives same result as DateFormat
     * plus UnixTime at non-leap-seconds.
     */
    @Test
    public void testAgainstDateFormat() {
	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	TimeZone utcZone = TimeZone.getTimeZone("UTC");
	dateFormatter.setTimeZone(utcZone);
	dateFormatter.setLenient(false);

	Date d59 = dateFormatter.parse("1993-06-30T23:59:59Z", new ParsePosition(0));

	TimeFormat timeFormatter = new SimpleTimeFormat(TimeScale.UTC);
	TaiTime ft1 = UnixTime.dateToTaiTime(d59);
	TaiTime ft2 = timeFormatter.parse("1993-06-30T23:59:59Z");
	assertTrue(ft1.equals(ft2));
    }

    /**
     * Test formatting with rounding and truncation.
     */
    @Test
    public void testRounding() {
	SimpleTimeFormat formatter = new SimpleTimeFormat();

	String s = "1993-01-01T12:34:56 TAI";
	TaiTime ft = formatter.parse(s);
	ft = ft.addMicroseconds(516278);

	// Test with default constructor options (rounding=false, decimals=0).
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56 TAI");

	// Tests with rounding and decimals=0 (rounds up)
	formatter.setRounding(true);
	formatter.setDecimals(0);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:57 TAI");

	// Tests with rounding and decimals=1 (rounds down)
	formatter.setDecimals(1);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56.5 TAI");

	// Tests with rounding and decimals=4 (rounds up)
	formatter.setDecimals(4);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56.5163 TAI");

	// Tests with rounding and decimals=5 (rounds up)
	formatter.setDecimals(5);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56.51628 TAI");

	// Tests with rounding and decimals=6 (no rounding applicable)
	formatter.setDecimals(6);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56.516278 TAI");

	// Tests with no rounding and decimals=0 (truncates)
	formatter.setRounding(false);
	formatter.setDecimals(0);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56 TAI");

	// Tests with no rounding and decimals=2 (truncates)
	formatter.setDecimals(2);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56.51 TAI");

	// Tests with no rounding and decimals=6 (no truncation applicable)
	formatter.setDecimals(6);
	assertEquals(formatter.format(ft), "1993-01-01T12:34:56.516278 TAI");
    }

    /**
     * Test parsing.
     */
    @Test
    public void testParsing() {
	SimpleTimeFormat format = new SimpleTimeFormat();
	long t59 = 1120176026000000L - 27000000; // 1993-06-30T23:59:59 TAI

	format.setDecimals(0);
	assertEquals(format.parse("1993-06-30T23:59:59 TAI")
		.microsecondsSince1958(), t59);

	format.setDecimals(1);
	assertEquals(format.parse("1993-06-30T23:59:59.7 TAI")
		.microsecondsSince1958(), t59 + 700000);

	format.setDecimals(4);
	assertEquals(format.parse("1993-06-30T23:59:59.7234 TAI")
		.microsecondsSince1958(), t59 + 723400);

	format.setDecimals(6);
	assertEquals(format.parse("1993-06-30T23:59:59.723456 TAI")
		.microsecondsSince1958(), t59 + 723456);
    }

    /**
     * Test correct parsing and formatting around a leap-second.
     */
    @Test
    public void testLeapSeconds() {
	SimpleTimeFormat format = new SimpleTimeFormat(TimeScale.UTC);
	format.setDecimals(6);

	String s59 = "1993-06-30T23:59:59.000000Z";
	TaiTime ft59 = format.parse(s59);

	String s59999999 = "1993-06-30T23:59:59.999999Z";
	TaiTime ft59999999 = format.parse(s59999999);

	String s60 = "1993-06-30T23:59:60.000000Z";
	TaiTime ft60 = format.parse(s60);

	String s00 = "1993-07-01T00:00:00.000000Z";
	TaiTime ft00 = format.parse(s00);

	// seconds = 59
	String s0 = format.format(ft59);
	assertTrue(s0.equals(s59));

	// seconds = 59.999999
	String s1 = format.format(ft59999999);
	assertTrue(s1.equals(s59999999));

	// seconds = 60 (leap second)
	String s2 = format.format(ft60);
	assertTrue(s2.equals(s60));
	assertEquals(ft60.microsecondsSince1958() 
		- ft59.microsecondsSince1958(), 1000000);
	assertEquals(ft60.microsecondsSince1958() 
		- ft59999999.microsecondsSince1958(), 1);

	// seconds = 00
	String s3 = format.format(ft00);
	assertTrue(s3.equals(s00));
	assertEquals(ft00.microsecondsSince1958() 
		- ft60.microsecondsSince1958(), 1000000);
    }

    /**
     * Test correct parsing and formatting for non-leap-second.
     */
    @Test
    public void testNonLeapSeconds() {
	TimeFormat timeFormatter = new SimpleTimeFormat(TimeScale.UTC);

	// seconds = 59
	String s59 = "1992-12-31T23:59:59Z";
	TaiTime ft59 = timeFormatter.parse(s59);
	String s1 = timeFormatter.format(ft59);
	assertTrue(s1.equals(s59));

	// seconds = 00
	String s00 = "1993-01-01T00:00:00Z";
	TaiTime ft00 = timeFormatter.parse(s00);
	String s2 = timeFormatter.format(ft00);
	assertTrue(s2.equals(s00));
	assertEquals(ft00.microsecondsSince1958() 
		- ft59.microsecondsSince1958(), 1000000);
    }

    /**
     * Test free-format parsing of fractional seconds.
     */
    @Test
    public void testFreeFractions1() {
	SimpleTimeFormat format = new SimpleTimeFormat();
	format.setStrictFraction(false);

	TaiTime ft0 = format.parse("1993-06-30T23:59:59 TAI");
	TaiTime ft1 = format.parse("1993-06-30T23:59:59.1 TAI");
	assertEquals(ft1.subtract(ft0), 100000);

	TaiTime ft2 = format.parse("1993-06-30T23:59:59.12 TAI");
	assertEquals(ft2.subtract(ft0), 120000);

	TaiTime ft3 = format.parse("1993-06-30T23:59:59.123456 TAI");
	assertEquals(ft3.subtract(ft0), 123456);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFreeFractions2() {
	// Test that strictFractions option works.
	SimpleTimeFormat format = new SimpleTimeFormat();
	format.setStrictFraction(true);
	format.setDecimals(1);
	format.parse("1993-06-30T23:59:59.12 TAI");
    }

    /**
     * Test handling of parsing errors.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testParsingErrors1() {
	SimpleTimeFormat format = new SimpleTimeFormat();
	format.parse("1993:06-30T23:59:59.1 TAI");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParsingErrors2() {
	SimpleTimeFormat format = new SimpleTimeFormat();	
	format.parse("93-06-30T23:59:59 TAI");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParsingErrors3() {
	SimpleTimeFormat format = new SimpleTimeFormat();
	format.parse("1993-06-30T23:59:59 XYZ");
    }

    /**
     * Test use of different TimeScales.
     */
    @Test
    public void testTimeScales() {
	SimpleTimeFormat utcFormat = new SimpleTimeFormat(TimeScale.UTC);
	SimpleTimeFormat taiFormat = new SimpleTimeFormat(TimeScale.TAI);
	SimpleTimeFormat ttFormat  = new SimpleTimeFormat(TimeScale.TT);

	TaiTime fUtc = utcFormat.parse("1993-06-30T23:59:59Z");
	TaiTime fTai = taiFormat.parse("1993-06-30T23:59:59 TAI");
	TaiTime fTt  = ttFormat.parse("1993-06-30T23:59:59 TT");

	assertEquals(fTai.subtract(fTt), 32184000);
	assertEquals(fUtc.subtract(fTai), 27000000); // 27 leap seconds
    }
    
    /**
     * Test parsing historical dates.
     */
    @Test
    public void testParseHistoricalDates() {
	TimeFormat fmt = new SimpleTimeFormat(TimeScale.TAI);
	TaiTime t0 = fmt.parse("1957-12-31T23:59:59 TAI");
	TaiTime t1 = fmt.parse("1958-01-01T00:00:00 TAI");
	TaiTime t2 = fmt.parse("1969-12-31T23:59:59 TAI");
	TaiTime t3 = fmt.parse("1970-01-01T00:00:00 TAI");
	assertEquals(-1000000, t0.microsecondsSince1958());
	assertEquals(0, t1.microsecondsSince1958());
	assertEquals(-D58 - 1000000, t2.microsecondsSince1958());
	assertEquals(-D58, t3.microsecondsSince1958());
    }
    
    /**
     * Test formatting historical dates.
     */
    @Test
    public void testFormatHistoricalDates() {
	TimeFormat fmt = new SimpleTimeFormat(TimeScale.TAI);
	String s0 = fmt.format(new TaiTime(0));
	String s1 = fmt.format(new TaiTime(-1000000));
	String s2 = fmt.format(new TaiTime(-D58 - 1000000));
	String s3 = fmt.format(new TaiTime(-D58));
	assertEquals(s0, "1958-01-01T00:00:00 TAI");
	assertEquals(s1, "1957-12-31T23:59:59 TAI");
	assertEquals(s2, "1969-12-31T23:59:59 TAI");
	assertEquals(s3, "1970-01-01T00:00:00 TAI");
    }
    
    /**
     * Test proleptic Gregorian calendar.
     */
    @Test
    public void testProlepticCalendar() {
	TimeFormat fmt = new SimpleTimeFormat(TimeScale.TAI);
	String s1 = "1582-10-04T00:00:00 TAI";
	String s2 = "1582-10-15T00:00:00 TAI";
	long tai1 = -11841552000000000L; // Derived by manual calculation
	long tai2 = -11840601600000000L;
	
	TaiTime t1 = fmt.parse(s1);
	TaiTime t2 = fmt.parse(s2);
	assertEquals(tai1, t1.microsecondsSince1958());
	assertEquals(tai2, t2.microsecondsSince1958());
	
	assertEquals(11 * 86400 * 1000000L, (tai2 - tai1));
    }
}







