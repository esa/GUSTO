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
import esa.esac.gusto.time.DateConverter;
import esa.esac.gusto.time.TaiTime;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Test harness for DateConverter.
 *
 * @author  Jon Brumfitt
 */
public class DateConverterTest {

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
     * Test aliasing at leap seconds.<p>
     *
     * All times within a leap-second should map onto midnight
     * in the Date representation.
     */
    @Test
    public void testAliasing() {
	String s = "1992-07-01T00:00:00.000Z";
	Date d = formatter.parse(s, new ParsePosition(0));
	TaiTime f000 = DateConverter.dateToTaiTime(d);
	long t000 = d.getTime();
	{
	    // Test "1992:06:30T23:59:59.000Z"
	    TaiTime f590 = f000.addMicroseconds(-2000000);
	    Date d590 = DateConverter.TaiTimeToDate(f590);
	    assertEquals(d590.getTime(), t000 - 1000);
	}{
	    // Test "1992:06:30T23:59:59.500Z"
	    TaiTime f595 = f000.addMicroseconds(-1500000);
	    Date d595 = DateConverter.TaiTimeToDate(f595);
	    assertEquals(d595.getTime(), t000 - 500);
	}{
	    // Test "1992:06:30T23:59:60.000Z"
	    TaiTime f600 = f000.addMicroseconds(-1000000);
	    Date d600 = DateConverter.TaiTimeToDate(f600);
	    assertEquals(d600.getTime(), t000);
	}{
	    // Test "1992:06:30T23:59:60.500Z"
	    TaiTime f605 = f000.addMicroseconds(-500000);
	    Date d605 = DateConverter.TaiTimeToDate(f605);
	    assertEquals(d605.getTime(), t000);
	}{
	    // Test "1992:07:00T00:00:00.000Z"
	    Date d000 = DateConverter.TaiTimeToDate(f000);
	    assertEquals(d000.getTime(), t000);
	}{
	    // Test "1992:07:00T00:00:00.500Z"
	    TaiTime f005 = f000.addMicroseconds(500000);
	    Date d005 = DateConverter.TaiTimeToDate(f005);
	    assertEquals(d005.getTime(), t000 + 500);
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
	TaiTime ft = DateConverter.dateToTaiTime(d);

	// Convert TaiTime to Date
	Date d1 = DateConverter.TaiTimeToDate(ft);
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
	TaiTime ft1 = DateConverter.dateToTaiTime(d);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPre1972FineToDate2() {
	Date d = formatter.parse("1972-01-01T00:00:00.000Z", new ParsePosition(0));

	// Now try one millisecond earlier
	@SuppressWarnings("unused")
	TaiTime ft2 = DateConverter.dateToTaiTime(new Date(d.getTime() - 1));
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
	Date d1 = DateConverter.TaiTimeToDate(new TaiTime(tai));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPre1972DateToFine2() {
	Date d = formatter.parse("1972-01-01T00:00:00.000Z", new ParsePosition(0));
	long tai = (d.getTime() - m1958 + LEAP_1972 * 1000) * 1000;

	// Now try one microsecond earlier
	@SuppressWarnings("unused")
	Date d2 = DateConverter.TaiTimeToDate(new TaiTime(tai - 1));
    }
}








