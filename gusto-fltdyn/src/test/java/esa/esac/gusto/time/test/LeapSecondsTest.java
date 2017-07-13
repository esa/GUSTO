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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.LeapSeconds;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;
import esa.esac.gusto.time.UnixTime;

import org.junit.Test;

/**
 * Test harness for LeapSeconds.
 *
 * @author  Jon Brumfitt
 */
public class LeapSecondsTest{

    private static final SimpleTimeFormat UTC = new SimpleTimeFormat(TimeScale.UTC);

    /**
     * Test the <tt>isLeapSeconds</tt> method.
     */
    @Test
    public void testIsLeapSecond() {
	// Test at a leap second
	TaiTime f2006 = UTC.parse("2006-01-01T00:00:00Z");
	long t2006 = f2006.microsecondsSince1958();

	assertFalse(LeapSeconds.isLeapSecond(t2006 - 1500000)); // 23:59:59.5Z
	assertFalse(LeapSeconds.isLeapSecond(t2006 - 1000001)); // 23:59:59.999999Z
	assertTrue(LeapSeconds.isLeapSecond( t2006 - 1000000)); // 23:59:60Z
	assertTrue(LeapSeconds.isLeapSecond( t2006 -  500000)); // 23:59:60.5Z
	assertTrue(LeapSeconds.isLeapSecond( t2006 -       1)); // 23:59:60.999999Z
	assertFalse(LeapSeconds.isLeapSecond(t2006));            // 00:00:00Z
	assertFalse(LeapSeconds.isLeapSecond(t2006 +  500000)); // 00:00:00.5Z

	// Test when it is not a leap second
	TaiTime f2007 = UTC.parse("2007-01-01T00:00:00Z");
	long t2007 = f2007.microsecondsSince1958();

	assertFalse(LeapSeconds.isLeapSecond(t2007 - 1500000)); // 23:59:59.5Z
	assertFalse(LeapSeconds.isLeapSecond(t2007 - 1000000)); // 00:00:00Z
	assertFalse(LeapSeconds.isLeapSecond(t2007 -  500000)); // 00:00:00.5Z
    }

    /**
     * Test the <tt>leapSeconds</tt> method.
     */
    @Test
    public void testLeapSeconds() {
	// Test at a leap second
	TaiTime f2006 = UTC.parse("2006-01-01T00:00:00Z");
	long t2006 = f2006.microsecondsSince1958();

	// Times up to the end of the leap second
	assertEquals(LeapSeconds.leapSeconds(t2006 - 1000000), 32); // 23:59:60Z
	assertEquals(LeapSeconds.leapSeconds(t2006 -  500000), 32); // 23:59:60.5Z
	assertEquals(LeapSeconds.leapSeconds(t2006 -       1), 32); // 23:59:60.999999Z

	// Times from the leap second onwards
	assertEquals(LeapSeconds.leapSeconds(t2006),           33); // 00:00:00Z
	assertEquals(LeapSeconds.leapSeconds(t2006  + 500000), 33); // 00:00:00.5Z
    }
    
    /**
     * Test the leap-second added at 2017-01-01.
     */
    @Test
    public void testLeapSecond2017() {
        // Test at a leap second
        TaiTime f2017 = UTC.parse("2017-01-01T00:00:00Z");
        long t2017 = f2017.microsecondsSince1958();

        // Times up to the end of the leap second
        assertEquals(LeapSeconds.leapSeconds(t2017 - 1000000), 36); // 23:59:60Z
        assertEquals(LeapSeconds.leapSeconds(t2017 -  500000), 36); // 23:59:60.5Z
        assertEquals(LeapSeconds.leapSeconds(t2017 -       1), 36); // 23:59:60.999999Z

        // Times from the leap second onwards
        assertEquals(LeapSeconds.leapSeconds(t2017),           37); // 00:00:00Z
        assertEquals(LeapSeconds.leapSeconds(t2017  + 500000), 37); // 00:00:00.5Z
    } 

    /**
     * Test the <tt>taiToUnixTime</tt> method.
     */
    @Test
    public void testTaiToUnixTime() {
	TaiTime f1999 = UTC.parse("1999-01-01T00:00:00Z");
	long u1999 = 915148800000000L;

	// Before the leap second
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-1500000)), u1999 - 500000); // 23:59:59.5Z

	// During leap second
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-1000000)), u1999);          // 23:59:60Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-500000)), u1999+500000);    // 23:59:60.5Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(-1)), u1999+999999);         // 23:59:60.999999Z
	
	// After the leap second
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(0)), u1999);                 // 00:00:00Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(500000)), u1999+500000);     // 00:00:00.5Z
	assertEquals(UnixTime.taiToUnixTime(f1999.addMicroseconds(1000000)), u1999+1000000);   // 00:00:01Z
    }
}







