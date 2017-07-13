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
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.TimeScale;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Test harness for UnixTime.
 *
 * @author  Jon Brumfitt
 */
public class UnixTimeTest {
    private static final SimpleTimeFormat UTC = new SimpleTimeFormat(TimeScale.UTC);

    /** TAI-UTC seconds at 1 Jan 1972. */
    private static final int LEAP_1972 = 10;

    // DateFormat for parsing UTC test dates
    static final DateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
	TimeZone utcZone = TimeZone.getTimeZone("UTC");
	formatter.setTimeZone(utcZone);
	formatter.setLenient(false);
    }

    static final long m1958 = formatter.parse("1958-01-01T00:00:00.000Z",
	    new ParsePosition(0)).getTime();


    /**
     * Test aliasing at leap seconds.
     */
    @Test
    public void testAliasing() {
	String s = "1992-07-01T00:00:00.000Z";
	Date d = formatter.parse(s, new ParsePosition(0));
	TaiTime f000 = UnixTime.dateToTaiTime(d);
	long t000 = d.getTime();
	{
	    // Test "1992:06:30T23:59:59.000Z"
	    TaiTime f590 = f000.addMicroseconds(-2000000);
	    Date d590 = UnixTime.TaiTimeToDate(f590);
	    assertEquals(t000 - 1000, d590.getTime());
	}{
	    // Test "1992:06:30T23:59:59.500Z"
	    TaiTime f595 = f000.addMicroseconds(-1500000);
	    Date d595 = UnixTime.TaiTimeToDate(f595);
	    assertEquals(t000 - 500, d595.getTime());
	}{
	    // Test "1992:06:30T23:59:60.000Z"
	    TaiTime f600 = f000.addMicroseconds(-1000000);
	    Date d600 = UnixTime.TaiTimeToDate(f600);
	    assertEquals(t000, d600.getTime());
	}{
	    // Test "1992:06:30T23:59:60.500Z"
	    TaiTime f605 = f000.addMicroseconds(-500000);
	    Date d605 = UnixTime.TaiTimeToDate(f605);
	    assertEquals(t000 + 500, d605.getTime());
	}{
	    // Test "1992:07:00T00:00:00.000Z"
	    Date d000 = UnixTime.TaiTimeToDate(f000);
	    assertEquals(t000, d000.getTime());
	}{
	    // Test "1992:07:00T00:00:00.500Z"
	    TaiTime f005 = f000.addMicroseconds(500000);
	    Date d005 = UnixTime.TaiTimeToDate(f005);
	    assertEquals(t000 + 500, d005.getTime());
	}
    }

    /**
     * Test Date to TaiTime to Date conversions.<p>
     *
     * This does not test behaviour at or within a leap-second, as leap-seconds
     * do not occur in the original Date representation.
     */
    @Test
    public void testConversions() {
	doTest("1972-01-01T00:00:00.000Z", 10); // Earliest valid date
	doTest("1972-12-31T23:59:59.000Z", 11); // Just before leap-second
	doTest("1972-12-31T23:59:59.500Z", 11); // During a leap-second
	doTest("1973-01-01T00:00:00.000Z", 12); // Just after leap-second
	doTest("1973-05-01T00:00:00.000Z", 12); // Not near leap-second
	doTest("1998-12-31T23:59:59.000Z", 31); // Just before leap-second
	doTest("1999-01-01T00:00:00.000Z", 32); // Just after leap-second
    }

    /**
     * Convert both ways and check results.
     *
     * @param date String specifying UTC date/time
     * @param leap Number of leap-seconds at that time (TAI-UTC)
     */
    void doTest(String date, int leap) {
	// Create a Date from the String
	Date d = formatter.parse(date, new ParsePosition(0));

	// Convert Date to TaiTime
	TaiTime ft = UnixTime.dateToTaiTime(d);

	// Convert TaiTime to Date
	Date d1 = UnixTime.TaiTimeToDate(ft);
	assertTrue(d1.equals(d));

	// Check TAI value
	long tai = ft.microsecondsSince1958();
	assertEquals(tai, (d.getTime() - m1958 + leap * 1000) * 1000);
    }

    /**
     * Test that exception is thrown for date before 1 Jan 1972.
     */
    @Test
    public void testPre1972FineToDate1() {
	Date d = formatter.parse("1972-01-01T00:00:00.000Z", new ParsePosition(0));

	// This should not throw an exception
	@SuppressWarnings("unused")
	TaiTime ft1 = UnixTime.dateToTaiTime(d);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPre1972FineToDate2() {
	Date d = formatter.parse("1972-01-01T00:00:00.000Z", new ParsePosition(0));

	// Now try one millisecond earlier
	@SuppressWarnings("unused")
	TaiTime ft2 = UnixTime.dateToTaiTime(new Date(d.getTime() - 1));
    }

    /**
     * Test that exception is thrown for date before 1 Jan 1972.
     */
    @Test
    public void testPre1972DateToFine1() {

	Date d = formatter.parse("1972-01-01T00:00:00.000Z", new ParsePosition(0));

	// Create corresponding TAI with epoch 1 Jan 1958
	// FIXME - This could be simplified.
	long tai = (d.getTime() - m1958 + LEAP_1972 * 1000) * 1000;
	// This should not throw an exception
	@SuppressWarnings("unused")
	Date d1 = UnixTime.TaiTimeToDate(new TaiTime(tai));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPre1972DateToFine2() {
	Date d = formatter.parse("1972-01-01T00:00:00.000Z", new ParsePosition(0));
	long tai = (d.getTime() - m1958 + LEAP_1972 * 1000) * 1000;

	// Now try one microsecond earlier
	@SuppressWarnings("unused")
	Date d2 = UnixTime.TaiTimeToDate(new TaiTime(tai - 1));
    }
    
    
    /**
     * Test the <tt>taiToUnixTime</tt> method with the 'freeze' option.
     */
    @Test
    public void testTaiToUnixTimeWithFreeze() {
	TaiTime f1999 = UTC.parse("1999-01-01T00:00:00Z");
	long u1999 = 915148800000000L;

	// Before the leap second
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-1500000), true), u1999 - 500000); // 23:59:59.5Z

	// During leap second - All UTC values are aliased onto the same TAI time
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-1000000), true), u1999);          // 23:59:60Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-500000), true), u1999);           // 23:59:60.5Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-1), true), u1999);                // 23:59:60.999999Z
	
	// After the leap second
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(0), true), u1999);                 // 00:00:00Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(500000), true), u1999+500000);     // 00:00:00.5Z
    } 
    
    /**
     * Test the <tt>unixToTaiTime</tt> method.
     */
    @Test
    public void testUnixToTaiTime() {
	TaiTime f1999 = UTC.parse("1999-01-01T00:00:00Z");
	long t1999 = f1999.microsecondsSince1958();
	long u1999 = 915148800000000L;

	// Before the leap second
	assertEquals(UnixTime.unixToTaiTime(u1999-1000000).microsecondsSince1958(), t1999-2000000); // 23:59:59Z
	assertEquals(UnixTime.unixToTaiTime(u1999-500000).microsecondsSince1958(), t1999-1500000);  // 23:59:59.5Z
	assertEquals(UnixTime.unixToTaiTime(u1999-1).microsecondsSince1958(), t1999-1000001);       // 23:59:59.999999Z

	// A jump of one second occurs in the TAI value at this point
	assertEquals(UnixTime.unixToTaiTime(u1999).microsecondsSince1958(), t1999);                 // 00:00:00Z
	assertEquals(UnixTime.unixToTaiTime(u1999+500000).microsecondsSince1958(), t1999+500000);   // 00:00:00.5Z
	assertEquals(UnixTime.unixToTaiTime(u1999+1000000).microsecondsSince1958(), t1999+1000000); // 00:00:01Z
    }
}








