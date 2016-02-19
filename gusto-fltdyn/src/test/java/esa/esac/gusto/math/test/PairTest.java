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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.math.Pair;

import org.junit.Test;

/**
 * Test harness for Pair class.
 *
 * @author  Jon Brumfitt
 */
public class PairTest {

    /**
     * Test constructors and accessors.
     */
    @Test
    public void testConstruction() {
	Pair p1 = new Pair();
	assertEquals(0, p1.first(), 1E-15);
	assertEquals(0, p1.second(), 1E-15);
	
	Pair p2 = new Pair(3.1, 2.7);
	assertEquals(3.1, p2.first(), 1E-15);
	assertEquals(2.7, p2.second(), 1E-15);
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Pair d = new Pair(30, 40);
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}

