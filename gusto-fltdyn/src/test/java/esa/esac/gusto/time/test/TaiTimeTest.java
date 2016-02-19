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

import org.junit.Test;

/**
 * Test harness for TaiTime.
 *
 * @author  Jon Brumfitt
 */
public class TaiTimeTest {

    @Test
    public void testConstruction() {
	TaiTime ft1 = new TaiTime(1000);
	assertEquals(ft1.microsecondsSince1958(), 1000);
    }

    @Test
    public void testEquality() {
	TaiTime ft1 = new TaiTime(1000);
	TaiTime ft2 = new TaiTime(1000);
	TaiTime ft3 = new TaiTime(1001);
	assertTrue(ft1.equals(ft2));
	assertFalse(ft1.equals(ft3));

	assertEquals(ft1.hashCode(), ft2.hashCode());
	assertEquals(ft1.compareTo(ft2), 0);
	assertEquals(ft1.compareTo(ft3), -1);
	assertEquals(ft3.compareTo(ft1), 1);
    }

    @Test
    public void testArithmetic() {
	TaiTime ft1 = new TaiTime(1000);
	TaiTime ft2 = ft1.addMicroseconds(7);

	assertEquals(ft1.microsecondsSince1958(), 1000); // unchanged 
	assertEquals(ft2.microsecondsSince1958() - ft1.microsecondsSince1958(), 7);
	assertEquals(ft2.subtract(ft1), 7);

	TaiTime ft3 = ft1.addSeconds(3);
	assertEquals(ft3.subtract(ft1), 3000000);
    }

    @Test
    public void testComparisons() {
	TaiTime ft1 = new TaiTime(1000);
	TaiTime ft2 = new TaiTime(1000);
	TaiTime ft3 = new TaiTime(1001);

	assertTrue(ft1.before(ft3));
	assertFalse(ft3.before(ft1));
	assertTrue(ft3.after(ft1));
	assertFalse(ft1.after(ft3));

	assertTrue(ft1.atOrBefore(ft3));
	assertTrue(ft1.atOrBefore(ft2));
	assertFalse(ft3.atOrBefore(ft1));

	assertTrue(ft3.atOrAfter(ft1));
	assertTrue(ft1.atOrAfter(ft2));
	assertFalse(ft1.atOrAfter(ft3));

	assertEquals(ft1.earliest(ft3), ft1);
	assertEquals(ft3.earliest(ft1), ft1);
	assertEquals(ft1.latest(ft3), ft3);
	assertEquals(ft3.latest(ft1), ft3);
    }
}







