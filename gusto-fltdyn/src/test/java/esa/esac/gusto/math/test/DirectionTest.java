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
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

/**
 * Test harness for Direction class.
 *
 * @author  Jon Brumfitt
 */
public class DirectionTest {

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
    public void testGet() {
	double eps = 1E-14;

	// Constructor with no arguments
	Direction a0 = new Direction();
	assertEquals(a0.getRaDegrees(),  0.0, eps);
	assertEquals(a0.getDecDegrees(), 0.0, eps);
	
	// Constructor with angles in radians and getAlpah/getDelta
	Direction a1 = new Direction(0.3, 0.4);
	assertEquals(a1.getAlpha(), 0.3, eps);
	assertEquals(a1.getDelta(), 0.4, eps);

	// Constructor with RA,DEC
	Direction a2 = Direction.fromDegrees(30,40);
	assertEquals(a2.getAlpha(), Math.toRadians(30.0), eps);
	assertEquals(a2.getDelta(), Math.toRadians(40.0), eps);
	
	// getRaDegrees and getDecDegrees
	assertEquals(a2.getRaDegrees(),  30.0, eps);
	assertEquals(a2.getDecDegrees(), 40.0, eps);

	// Copy constructor
	Direction a3 = new Direction(a2);
	assertEquals(a3.getRaDegrees(),  30.0, eps);
	assertEquals(a3.getDecDegrees(), 40.0, eps);
	
	// Construct from Vector3
	double r = Math.toRadians(30);
	double d = Math.toRadians(40);
	double x = Math.cos(r) * Math.cos(d);
	double y = Math.sin(r) * Math.cos(d);
	double z = Math.sin(d);
	Vector3 v = new Vector3(x, y, z).multiply(2); // Non-unit vector
	Direction a4 = new Direction(v);
	assertEquals(a4.getRaDegrees(),  30.0, eps);
	assertEquals(a4.getDecDegrees(), 40.0, eps);
    }

    /**
     * Test distanceTo and cosDistanceTo
     */
    @Test
    public void testDistanceTo() {
	Vector3 v1 = new Vector3(1,2,3);
	Vector3 v2 = new Vector3(5,3,7);
	Direction d1 = new Direction(v1);
	Direction d2 = new Direction(v2);
	double dist = d1.distanceTo(d2);
	double eDist = v1.angle(v2);
	assertEquals(eDist, dist, 1E-15);
	
	double cos = d1.cosDistanceTo(d2);
	double eCos = Math.cos(eDist);
	assertEquals(eCos, cos, 1E-15);
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Direction d = Direction.fromDegrees(30, 40);
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}







