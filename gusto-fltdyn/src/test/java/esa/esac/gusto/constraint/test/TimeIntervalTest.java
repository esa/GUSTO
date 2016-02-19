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

package esa.esac.gusto.constraint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.time.TaiTime;

import org.junit.Test;

/**
 * Test harness forTimeInterval class.
 *
 * @author  Jon Brumfitt
 */
public class TimeIntervalTest {

    // Test construction with time and duration
    @Test
    public void testConstruction1() {
	long origin = 123456;
	int d = 10;
	TaiTime t0 = new TaiTime(origin);
	TimeInterval i = new TimeInterval(t0, d);

	assertTrue(i.start().equals(t0));
	assertEquals(i.duration(), d);
	assertTrue(i.finish().equals(t0.addMicroseconds(d)));
    }

    // Test construction with start and end times
    @Test
    public void testConstruction2() {
	long origin = 123456;
	int d = 10;
	TaiTime t0 = new TaiTime(origin);
	TaiTime t1 = (new TaiTime(origin)).addMicroseconds(d);
	TimeInterval i = new TimeInterval(t0, t1);

	assertTrue(i.start().equals(t0));
	assertEquals(i.duration(), d);
	assertTrue(i.finish().equals(t0.addMicroseconds(d)));
    }

    // FIXME - Add tests for end < start.

    // Test equality
    @Test
    public void testEquality() {
	long origin = 123456;
	int d = 10;
	TaiTime t0 = new TaiTime(origin);
	TaiTime t1 = (new TaiTime(origin)).addMicroseconds(d);

	// Equal intervals
	TimeInterval i1 = new TimeInterval(t0, d);
	TimeInterval i2 = new TimeInterval(t0, t1);
	assertTrue(i1.equals(i2));

	// Same start, different duration
	TimeInterval i3 = new TimeInterval(t0, d+1);
	assertFalse(i1.equals(i3));

	// Different start, same duration
	TimeInterval i4 = new TimeInterval(t0.addMicroseconds(1), d);
	assertFalse(i1.equals(i4));
    }

    // Test for empty (zero duration) interval
    @Test
    public void testisNull() {
	long origin = 123456;
	TaiTime t0 = new TaiTime(origin);
	assertTrue((new TimeInterval(t0, 0)).isNull());

	assertFalse((new TimeInterval(t0, 1)).isNull());
    }

    //     // Test setStart method
    //    @Test
    //     public void testSetStart() {
    // 	long origin = 123456;
    // 	int d = 10;
    // 	Time t0 = new TaiTime(origin);

    // 	TimeInterval i1 = new TimeInterval(t0, d);
    // 	i1.setStart(t0.plus(2));

    // 	TimeInterval i2 = new TimeInterval(t0.plus(2), d);
    // 	assertTrue(i1.equals(i2));
    //     }

    //     // Test setDuration method
    //     public void testSetDuration() {
    // 	long origin = 123456;
    // 	Time t0 = new TaiTime(origin);

    // 	TimeInterval i1 = new TimeInterval(t0, 1);
    // 	i1.setDuration(2);

    // 	TimeInterval i2 = new TimeInterval(t0, 2);
    // 	assertTrue(i1.equals(i2));
    //     }

    // Test 'contains(Time t)'
    @Test
    public void testContains1() {
	long origin = 123456;
	TaiTime t0 = new TaiTime(origin);
	TimeInterval i1 = new TimeInterval(t0, 5);

	assertFalse(i1.contains(t0.addMicroseconds(-1)));
	assertTrue(i1.contains(t0));
	assertTrue(i1.contains(t0.addMicroseconds(1)));
	assertTrue(i1.contains(t0.addMicroseconds(4)));
	assertFalse(i1.contains(t0.addMicroseconds(5)));
    }

    // Test 'contains(TimeInterval i)'
    @Test
    public void testContains2() {
	long origin = 123456;
	TaiTime t0 = new TaiTime(origin);
	TimeInterval i1 = new TimeInterval(t0, 5);

	assertTrue(i1.contains(new TimeInterval(t0, 5)));
	assertTrue(i1.contains(new TimeInterval(t0.addMicroseconds(1), 4)));
	assertTrue(i1.contains(new TimeInterval(t0, 4)));

	assertTrue(i1.contains(new TimeInterval(t0, 0)));
	assertTrue(i1.contains(new TimeInterval(t0.addMicroseconds(4), 0)));

	assertFalse(i1.contains(new TimeInterval(t0, 6)));
	assertFalse(i1.contains(new TimeInterval(t0.addMicroseconds(-1), 5)));
    }

    // Test comparisons etc
    @Test
    public void testComparisons() {
	long origin = 123456;
	TaiTime t0 = new TaiTime(origin);
	TimeInterval i1 = new TimeInterval(t0, 5);
	TimeInterval i2 = new TimeInterval(t0.addMicroseconds(1), 5);

	assertTrue(i1.startsBefore(i2));
	assertFalse(i2.startsBefore(i1));
	assertFalse(i1.startsBefore(i1));

	assertFalse(i1.startsAfter(i2));
	assertTrue(i2.startsAfter(i1));
	assertFalse(i1.startsAfter(i1));

	assertTrue(i1.startsAtOrBefore(i2));
	assertFalse(i2.startsAtOrBefore(i1));
	assertTrue(i1.startsAtOrBefore(i1));

	assertFalse(i1.startsAtOrAfter(i2));
	assertTrue(i2.startsAtOrAfter(i1));
	assertTrue(i1.startsAtOrAfter(i1));

	assertEquals(i1.compareStartTo(i2), -1);
	assertEquals(i2.compareStartTo(i1), 1);
	assertEquals(i1.compareStartTo(i1), 0);
    }
}







