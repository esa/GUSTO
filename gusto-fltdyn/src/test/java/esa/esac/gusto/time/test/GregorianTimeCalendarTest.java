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
import esa.esac.gusto.time.DateConverter;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.GregorianTimeCalendar;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Test harness for GregorianTimeCalendar class.
 *
 * @author  Jon Brumfitt
 */
public class GregorianTimeCalendarTest {

    static final double EPSILON = 1 / 86400d / 1000000d; // 1 microsecond error in a day

    //    /**
    //     * Assert that two TaiTimes are equal to a specified accuracy.
    //     */
    //    private void assertEquals(TaiTime ft1, TaiTime ft2, long microseconds) {
    //	assertTrue(Math.abs(ft2.subtract(ft1)) <= microseconds);
    //    }

    /**
     * Assert that two int[] arrays are equal.
     */
    private void assertArraysEquals(int[] a, int[] b) {
	assertEquals(a.length, b.length);
	for(int i=0; i<a.length; i++) {
	    assertEquals(a[i], b[i]);
	}
    }

    /** Test conversions from time to fields and back again. */
    @Test
    public void test1() {
	GregorianTimeCalendar cal = new GregorianTimeCalendar();
	cal.enableLeapSeconds(true);

	DateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	TimeZone utcZone = TimeZone.getTimeZone("UTC");
	formatter.setTimeZone(utcZone);
	formatter.setLenient(false);

	// Create some times for the tests.
	String s = "1992-07-01T00:00:00.000Z";
	Date d = formatter.parse(s, new ParsePosition(0));
	TaiTime f000 = DateConverter.dateToTaiTime(d);
	TaiTime f590 = f000.addMicroseconds(-2000000);
	TaiTime f595 = f000.addMicroseconds(-1500000);
	TaiTime f600 = f000.addMicroseconds(-1000000);
	TaiTime f605 = f000.addMicroseconds(-500000);

	SimpleTimeFormat fmt = new SimpleTimeFormat(TimeScale.UTC);
	fmt.setDecimals(6);

	// Check that getTime() returns the value previously set.
	cal.setTime(f590);
	assertEquals(f590, cal.getTime());

	// Check conversion of time to fields
	int[] fields = cal.getFields();
	assertArraysEquals(fields, new int[]{1992,6,30,23,59,59,0});

	cal.setTime(f595);
	assertArraysEquals(cal.getFields(), new int[]{1992,6,30,23,59,59,500000});

	cal.setTime(f600);
	assertArraysEquals(cal.getFields(), new int[]{1992,6,30,23,59,60,0});

	cal.setTime(f605);
	assertArraysEquals(cal.getFields(), new int[]{1992,6,30,23,59,60,500000});

	cal.setTime(f000);
	assertArraysEquals(cal.getFields(), new int[]{1992,7,1,0,0,0,0});

	// Check conversion of fields to time
	cal.setFields(new int[]{1992,6,30,23,59,59,0});
	assertEquals(cal.getTime(), f590);

	cal.setFields(new int[]{1992,6,30,23,59,59,500000});
	assertEquals(cal.getTime(), f595);

	cal.setFields(new int[]{1992,6,30,23,59,60,0});
	assertEquals(cal.getTime(), f600);

	cal.setFields(new int[]{1992,6,30,23,59,60,500000});
	assertEquals(cal.getTime(), f605);

	cal.setFields(new int[]{1992,7,1,0,0,0,0});
	assertEquals(cal.getTime(), f000);
    }
}







