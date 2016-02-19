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
import esa.esac.gusto.math.AxisAngle;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

/**
 * Test harness for AxisAngle class.
 *
 * @author  Jon Brumfitt
 */
public class AxisAngleTest {

//    /**
//     * Normalize angle to the range 0-360 degrees.
//     */
//    private double mod360(double angle) {
//	while(angle < 0) {
//	    angle += 360;
//	}
//	while(angle >= 360) {
//	    angle -= 360;
//	}
//	return angle;
//    }

//    /**
//     * Test two angles for congruence (mod 360).
//     */
//    private void angleEquals(double degrees1, double degrees2, double epsilon) {
//	double x = mod360(degrees1 - degrees2 + 180);
//
//	assertEquals(x, 180d, epsilon);
//    }

    /**
     * Test construction and accessors.
     */
    @Test
    public void testConstructors() {  
	double a = Math.toRadians(30);
	Vector3 v = new Vector3(1,2,3);
	AxisAngle aa = new AxisAngle(v, a);

	Vector3 v2 = aa.axis();
	double a2 = aa.angle();
	assertTrue(v.epsilonEquals(v2, 1E-15));
	assertEquals(a, a2, 1E-15);
    }

    @Test
    public void testClone() {
	double a = Math.toRadians(30);
	Vector3 v = new Vector3(1,2,3);
	AxisAngle aa = new AxisAngle(v, a);
	
	AxisAngle aa2 = (AxisAngle)aa.clone();
	assertTrue(v.epsilonEquals(aa2.axis(), 1E-15));
	assertEquals(a, aa2.angle(), 1E-15);
	assertFalse(aa == aa2);	
    }

    @Test
    public void testEpsilonEquals() {
	double e = 1E-10;
	double a = Math.toRadians(30);
	Vector3 dvx = new Vector3(e, 0, 0);
	Vector3 dvy = new Vector3(0, e, 0);
	Vector3 dvz = new Vector3(0, 0, e);

	Vector3 v = new Vector3(1,2,3);
	AxisAngle r0 = new AxisAngle(v, a);
	AxisAngle r1 = new AxisAngle(v, a);
	AxisAngle r2 = new AxisAngle(v, a+e);
	AxisAngle r3 = new AxisAngle(v.add(dvx), a+e);
	AxisAngle r4 = new AxisAngle(v.add(dvy), a+e);
	AxisAngle r5 = new AxisAngle(v.add(dvz), a+e);

	double eLow = e * 0.99;
	double eHigh = e * 1.01;
	assertTrue(r0.epsilonEquals(r0, 1E-15));
	assertTrue(r0.epsilonEquals(r1, 1E-15));
	assertFalse(r0.epsilonEquals(r2, eLow));
	assertTrue(r0.epsilonEquals(r2, eHigh));
	assertFalse(r0.epsilonEquals(r3, eLow));
	assertTrue(r0.epsilonEquals(r3, eHigh));
	assertFalse(r0.epsilonEquals(r4, eLow));
	assertTrue(r0.epsilonEquals(r4, eHigh));
	assertFalse(r0.epsilonEquals(r5, eLow));
	assertTrue(r0.epsilonEquals(r5, eHigh));
    }

    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Vector3 v = new Vector3(1,2,3);
	AxisAngle a = new AxisAngle(v, 0.3);
	String s = a.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}







