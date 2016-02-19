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
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeFormat;
import esa.esac.gusto.time.TimeScale;

import org.junit.Test;

/**
 * Test harness for MjdFormat.
 *
 * @author  Jon Brumfitt
 */
public class Mjd2000FormatTest {

    private static final TimeFormat utcFormat = new SimpleTimeFormat(TimeScale.UTC);
    private static final TimeFormat taiFormat = new SimpleTimeFormat(TimeScale.TAI);

    private static final double EPSILON = 1 / 86400d / 1000000d; // 1 microsecond error in a day
    
    /**
     * Assert that two TaiTimes are equal to a specified accuracy.
     */
    private void assertTimesEquals(TaiTime ft1, TaiTime ft2, long microseconds) {
	assertTrue(Math.abs(ft2.subtract(ft1)) <= microseconds);
    }

    /** Test MJD(UTC) conversions. */
    @Test
    public void testMjdUtc() {
	final double sec = 1 / 86400d;  // One second in days
	final double EPS = EPSILON;     // Required accuracy of MJD in days

	Mjd2000TimeFormat converter = new Mjd2000TimeFormat(TimeScale.UTC);

	String s = "1999-01-01T00:00:00Z";
	TaiTime f0 = utcFormat.parse(s);
	final double mjd0 = converter.TaiTimeToMjd2000(f0);
	assertEquals(mjd0, -365.0, 1E-14);
	{
	    // Test 1998-12-31T23:59:59Z
	    TaiTime ft = f0.addMicroseconds(-2000000);
	    double mjd = converter.TaiTimeToMjd2000(ft);
	    assertEquals(mjd + sec, mjd0, EPS);

	    TaiTime f = converter.mjd2000ToTaiTime(mjd);
	    assertTimesEquals(ft, f, 1); // Equals original
	}{
	    // Test 1998-12-31T23:59:59.5Z
	    TaiTime ft = f0.addMicroseconds(-1500000);
	    double mjd = converter.TaiTimeToMjd2000(ft);
	    assertEquals(mjd + sec * 0.5, mjd0, EPS);

	    TaiTime f = converter.mjd2000ToTaiTime(mjd);
	    assertTimesEquals(ft, f, 1); // Equals original
	}{
	    // Test 1998-12-31T23:59:60Z
	    TaiTime ft = f0.addMicroseconds(-1000000);
	    double mjd = converter.TaiTimeToMjd2000(ft);
	    assertEquals(mjd, mjd0, EPS);

	    TaiTime f = converter.mjd2000ToTaiTime(mjd);
	    assertTimesEquals(f0, f, 1);
	}{
	    // Test 1998-12-31T23:59:60.5Z
	    TaiTime ft = f0.addMicroseconds(-500000);
	    double mjd = converter.TaiTimeToMjd2000(ft);
	    assertEquals(mjd, mjd0, EPS);

	    TaiTime f = converter.mjd2000ToTaiTime(mjd);
	    assertTimesEquals(f0, f, 1); // Aliased onto f0
	}{
	    // Test 1999-01-01T00:00:00Z
	    TaiTime ft = f0.addMicroseconds(0);
	    double mjd = converter.TaiTimeToMjd2000(f0);
	    assertEquals(mjd, mjd0, EPS);

	    TaiTime f = converter.mjd2000ToTaiTime(mjd);
	    assertTrue(ft.equals(f)); // Equals original
	}
    }

    /** Test MJD(TAI) conversions. */
    @Test
    public void testMjdTai() {
	String s = "1999-01-01T00:00:00 TAI";
	TaiTime ft = taiFormat.parse(s);

	Mjd2000TimeFormat format = new Mjd2000TimeFormat(TimeScale.TAI);

	// Test formatting and parsing of MJD string.
	String s1 = format.format(ft);
	assertEquals(s1, "-365.0");
	TaiTime ftp = format.parse(s1);
	assertTrue(ft.equals(ftp));

	// Test conversion of TaiTime to MJD.
	double mjd = format.TaiTimeToMjd2000(ft);
	assertEquals(mjd, -365.0, EPSILON);

	// Test conversion of MJD to TaiTime.
	TaiTime ft2 = format.mjd2000ToTaiTime(mjd);
	assertTrue(ft.equals(ft2));
    }

    /** Test MJD(TT) conversions. */
    @Test
    public void testMjdTT() {
	String s = "1999-01-01T00:00:00 TAI";
	TaiTime ft = taiFormat.parse(s);

	Mjd2000TimeFormat ttFormat  = new Mjd2000TimeFormat(TimeScale.TT);
	Mjd2000TimeFormat taiFormat = new Mjd2000TimeFormat(TimeScale.TAI);

	String s1 = ttFormat.format(ft);
	assertEquals(s1, "-364.9996275"); // 51179 - 51544 + 31.184 / 84000
	TaiTime ftp = ttFormat.parse(s1);
	System.out.println(ft + " " + ftp);
	assertEquals(ft.microsecondsSince1958(), ftp.microsecondsSince1958(), 1);

	double mjdTt  = ttFormat.TaiTimeToMjd2000(ft);
	double mjdTai = taiFormat.TaiTimeToMjd2000(ft);
	assertTrue(Math.abs(mjdTt - mjdTai - 32.184 / 86400d) < EPSILON);

	TaiTime ft2 = ttFormat.mjd2000ToTaiTime(mjdTt);
	assertEquals(ft.microsecondsSince1958(), ft2.microsecondsSince1958(), 1);
    }

    /** Test MJD(TDB) conversions. */
    @Test
    public void testMjdTDB() {
	Mjd2000TimeFormat mjdTtFormat  = new Mjd2000TimeFormat(TimeScale.TT);
	Mjd2000TimeFormat mjdTdbFormat = new Mjd2000TimeFormat(TimeScale.TDB);

	{
	    double tdt = 3000;
	    TaiTime ft = mjdTtFormat.mjd2000ToTaiTime(tdt);
	    double tdb = mjdTdbFormat.TaiTimeToMjd2000(ft);

	    // Expected result from OASW Fortran routine: TDBTDT(3000, 2)
	    double te = 3000.0000000185005;
	    double tdiff = (tdb - te) * 86400L * 1000000;  // Microseconds
	    assertEquals(tdiff, 0, 1);  // Better than 1 microsecond
	} {
	    double tdt = 4000;
	    TaiTime ft = mjdTtFormat.mjd2000ToTaiTime(tdt);
	    double tdb = mjdTdbFormat.TaiTimeToMjd2000(ft);

	    // Expected result from OASW Fortran routine: TDBTDT(4000, 2)
	    double te = 3999.99999999315787;
	    double tdiff = (tdb - te) * 86400L * 1000000;  // Microseconds
	    assertEquals(tdiff, 0, 1);  // Better than 1 microsecond
	}
    }
}







