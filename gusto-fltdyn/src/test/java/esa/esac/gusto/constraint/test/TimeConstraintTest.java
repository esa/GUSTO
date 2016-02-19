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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.constraint.TimeConstraint;
import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.time.TaiTime;

import java.util.Iterator;

import org.junit.Test;

/**
 * Test harness for TimeConstraint class.
 *
 * @author  Jon Brumfitt
 */
public class TimeConstraintTest{

    // Test construction with time and duration
    @Test
    public void testConstruction1() {
	// An empty constraint is never satisfied
	TimeConstraint c = new TimeConstraint();
	assertFalse(c.contains(new TaiTime(-1000000)));
	assertFalse(c.contains(new TaiTime(0)));
	assertFalse(c.contains(new TaiTime(1000000)));
    }

    // Test construction from aTimeInterval.
    @Test
    public void testConstruction2() {
	TaiTime t0 = new TaiTime(123456);
	TimeInterval i1 = new TimeInterval(t0, 10);
	TimeConstraint c = new TimeConstraint(i1);

	assertFalse(c.contains(t0.addMicroseconds(-1)));
	assertTrue(c.contains(t0.addMicroseconds(1)));
	assertTrue(c.contains(t0.addMicroseconds(9)));
	assertFalse(c.contains(t0.addMicroseconds(10)));
	assertFalse(c.contains(t0.addMicroseconds(11)));
    }

    // Test construction with start and end times.
    @Test
    public void testConstruction3() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, t0.addMicroseconds(10));

	assertFalse(c.contains(t0.addMicroseconds(-1)));
	assertTrue(c.contains(t0.addMicroseconds(1)));
	assertTrue(c.contains(t0.addMicroseconds(9)));
	assertFalse(c.contains(t0.addMicroseconds(10)));
	assertFalse(c.contains(t0.addMicroseconds(11)));
    }

    // Test construction with start time and duration.
    @Test
    public void testConstruction4() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, 10);

	assertFalse(c.contains(t0.addMicroseconds(-1)));
	assertTrue(c.contains(t0.addMicroseconds(1)));
	assertTrue(c.contains(t0.addMicroseconds(9)));
	assertFalse(c.contains(t0.addMicroseconds(10)));
	assertFalse(c.contains(t0.addMicroseconds(11)));
    }

    // Test equality
    @Test
    public void testEquality() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c1 = new TimeConstraint(t0, 10);
	TimeConstraint c2 = new TimeConstraint(t0, 10);
	TimeConstraint c3 = new TimeConstraint(t0, 9);

	assertTrue(c1.equals(c1));
	assertTrue(c1.equals(c2));
	assertFalse(c1.equals(c3));
    }

    // Test iterator
    @Test
    public void testIterator() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c1 = new TimeConstraint(t0, 10);

	Iterator<TimeInterval> it = c1.iterator();
	assertTrue(it.hasNext());
	TimeInterval i1 = it.next();
	assertTrue(i1.equals(new TimeInterval(t0, 10)));
	assertFalse(it.hasNext());
    }

    // Test 'contains(Time)'
    @Test
    public void testContains1() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, 10);

	assertFalse(c.contains(t0.addMicroseconds(-1)));
	assertTrue(c.contains(t0.addMicroseconds(1)));
	assertTrue(c.contains(t0.addMicroseconds(9)));
	assertFalse(c.contains(t0.addMicroseconds(10)));
	assertFalse(c.contains(t0.addMicroseconds(11)));
    }

    // Test 'contains(TimeInterval)'
    @Test
    public void testContains2() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, 10);

	assertTrue(c.contains(new TimeInterval(t0, 10)));
	assertFalse(c.contains(new TimeInterval(t0.addMicroseconds(-1), 10)));
	assertFalse(c.contains(new TimeInterval(t0.addMicroseconds(1), 10)));
    }

    // Test 'contains(start, duration)'
    @Test
    public void testContains3() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, 10);

	assertTrue(c.contains(t0, 10));
	assertFalse(c.contains(t0.addMicroseconds(-1), 10));
	assertFalse(c.contains(t0.addMicroseconds(1), 10));
    }

    // Test 'contains(start, finish)'
    @Test
    public void testContains4() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, 10);

	assertTrue(c.contains(t0,
		t0.addMicroseconds(10)));
	assertFalse(c.contains(t0.addMicroseconds(-1),
		t0.addMicroseconds(9)));
	assertFalse(c.contains(t0.addMicroseconds(1),
		t0.addMicroseconds(11)));
    }

    // Test 'earliest/latest'
    @Test
    public void testEarliest() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c = new TimeConstraint(t0, 10);

	assertTrue(c.earliest().equals(t0));
	assertTrue(c.latest().equals(t0.addMicroseconds(10)));

	assertTrue(c.earliestFor(10).equals(t0));
	assertNull(c.earliestFor(11));

	assertTrue(c.latestFor(10).equals(t0));
	assertTrue(c.latestFor(4).equals(t0.addMicroseconds(6)));
	assertNull(c.latestFor(11));
    }

    // Test 'nearestFor'
    @Test
    public void testNearestFor() {
	TaiTime t0 = new TaiTime(100000);
	TaiTime t1 = new TaiTime(100010);
	TaiTime t2 = new TaiTime(100020);
	TaiTime t3 = new TaiTime(100030);

	TimeConstraint c0 = new TimeConstraint(t0, t1);
	TimeConstraint c1 = new TimeConstraint(t2, t3);
	TimeConstraint c = c0.union(c1);

	TaiTime t = t0.addMicroseconds(-1);
	assertTrue(c.nearestFor(5,t0).equals(t0));

	assertTrue(c.nearestFor(5,t0).equals(t0));

	t = t0.addMicroseconds(5);
	assertTrue(c.nearestFor(5,t).equals(t));

	t = t0.addMicroseconds(6);
	TaiTime tr = t0.addMicroseconds(5);
	assertTrue(c.nearestFor(5,t).equals(tr));
	assertTrue(c.nearestFor(5,t1).equals(tr));

	t = t2.addMicroseconds(-1);
	assertTrue(c.nearestFor(5,t).equals(t2));
	assertTrue(c.nearestFor(5,t2).equals(t2));

	t = t2.addMicroseconds(5);
	assertTrue(c.nearestFor(5,t).equals(t));

	t = t2.addMicroseconds(6);
	tr = t2.addMicroseconds(5);
	assertTrue(c.nearestFor(5,t).equals(tr));

	t = t3.addMicroseconds(5);
	tr = t2.addMicroseconds(5);
	assertTrue(c.nearestFor(5,t3).equals(tr));
	assertTrue(c.nearestFor(5,t).equals(tr));
    }

    // Test 'earliestNotBefore'
    @Test
    public void testEarliestNotBefore() {
	TaiTime t0 = new TaiTime(100000);
	TaiTime t1 = new TaiTime(100010);
	TaiTime t2 = new TaiTime(100020);
	TaiTime t3 = new TaiTime(100030);

	TimeConstraint c0 = new TimeConstraint(t0, t1);
	TimeConstraint c1 = new TimeConstraint(t2, t3);
	TimeConstraint c = c0.union(c1);

	TaiTime t = t0.addMicroseconds(-1);
	assertTrue(c.earliestNotBefore(5,t).equals(t0));

	assertTrue(c.earliestNotBefore(5,t0).equals(t0));

	t = t0.addMicroseconds(5);
	assertTrue(c.earliestNotBefore(5,t).equals(t));

	t = t0.addMicroseconds(6);
	assertTrue(c.earliestNotBefore(5,t).equals(t2));

	t = t2.addMicroseconds(5);
	assertTrue(c.earliestNotBefore(5,t).equals(t));

	t = t2.addMicroseconds(6);
	assertNull(c.earliestNotBefore(5,t));	
    }

    // Test 'latestNotAfter'
    @Test
    public void testLatestNotAfter() {
	TaiTime t0 = new TaiTime(100000);
	TaiTime t1 = new TaiTime(100010);
	TaiTime t2 = new TaiTime(100020);
	TaiTime t3 = new TaiTime(100030);

	TimeConstraint c0 = new TimeConstraint(t0, t1);
	TimeConstraint c1 = new TimeConstraint(t2, t3);
	TimeConstraint c = c0.union(c1);

	TaiTime t = t3.addMicroseconds(-5);
	assertTrue(c.latestNotAfter(5,t3).equals(t));

	assertTrue(c.latestNotAfter(5,t).equals(t));

	assertTrue(c.latestNotAfter(5,t2).equals(t2));

	t = t2.addMicroseconds(-1);
	TaiTime tr = t1.addMicroseconds(-5);
	assertTrue(c.latestNotAfter(5,t).equals(tr));

	t = t0.addMicroseconds(3);
	assertTrue(c.latestNotAfter(5,t).equals(t));

	assertTrue(c.latestNotAfter(5,t0).equals(t0));

	t = t0.addMicroseconds(-1);
	assertNull(c.latestNotAfter(5,t));
    }

    // Test operators union, intersection, etc
    @Test
    public void testOperators() {
	TaiTime t0 = new TaiTime(100000);
	TaiTime t1 = new TaiTime(100010);
	TaiTime t2 = new TaiTime(100020);
	TaiTime t3 = new TaiTime(100030);

	TimeConstraint c0 = new TimeConstraint(t0, t1);
	TimeConstraint c1 = new TimeConstraint(t1, t3);
	TimeInterval   i2 = new TimeInterval(t0, t2);
	TimeConstraint c2 = new TimeConstraint(i2);

	assertTrue(c1.union(c2).equals(new TimeConstraint(t0, t3)));
	assertTrue(c1.intersection(c2).equals(new TimeConstraint(t1, t2)));
	assertTrue(c1.exclude(c2).equals(new TimeConstraint(t2, t3)));

	// Repeat using interval as argument
	assertTrue(c1.union(i2).equals(new TimeConstraint(t0, t3)));
	assertTrue(c1.intersection(i2).equals(new TimeConstraint(t1, t2)));
	assertTrue(c1.exclude(i2).equals(new TimeConstraint(t2, t3)));

	// Test with abutting intervals
	assertTrue(c0.union(c1).equals(new TimeConstraint(t0, t3)));
	assertTrue(c0.intersection(c1).equals(new TimeConstraint()));
	assertTrue(c0.exclude(c1).equals(c0));

	// Repeat with order reversed
	assertTrue(c2.union(c1).equals(new TimeConstraint(t0, t3)));
	assertTrue(c2.intersection(c1).equals(new TimeConstraint(t1, t2)));
	assertTrue(c2.exclude(c1).equals(new TimeConstraint(t0, t1)));
	assertTrue(c1.union(c0).equals(new TimeConstraint(t0, t3)));
	assertTrue(c1.intersection(c0).equals(new TimeConstraint()));
	assertTrue(c1.exclude(c0).equals(c1));

	// FIXME - Add tests for multi-period constraints and
	//         various kinds of overlap.
    }

    // Test isEmpty
    @Test
    public void isEmpty() {
	TaiTime t0 = new TaiTime(123456);
	TimeConstraint c0 = new TimeConstraint();
	TimeConstraint c1 = new TimeConstraint(t0, 10);

	assertTrue(c0.isEmpty());
	assertFalse(c1.isEmpty());
    }

    // FIXME - Add tests for the following:
    //   neither(TimeConstraint)
    //   merge(TimeConstraint c, BooleanOp op)
}







