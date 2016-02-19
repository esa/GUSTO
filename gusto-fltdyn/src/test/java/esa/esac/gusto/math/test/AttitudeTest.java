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
import esa.esac.gusto.math.Attitude;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Matrix3;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

/**
 * Test harness for Attitude class.
 *
 * @author  Jon Brumfitt
 */
public class AttitudeTest {

    /**
     * Normalize angle to the range 0-360 degrees.
     */
    private double mod360(double angle) {
	while(angle < 0) {
	    angle += 360;
	}
	while(angle >= 360) {
	    angle -= 360;
	}
	return angle;
    }

    /**
     * Test two angles for congruence (mod 360).
     */
    private void angleEquals(double degrees1, double degrees2, double epsilon) {
	double x = mod360(degrees1 - degrees2 + 180);

	assertEquals(x, 180d, epsilon);
    }

    /**
     * Test construction and accessors.
     */
    @Test
    public void testGet() {
	double eps = 1E-14;

	// Constructor with no arguments
	Attitude a0 = new Attitude();
	assertEquals(a0.getRaDegrees(),  0.0, eps);
	assertEquals(a0.getDecDegrees(), 0.0, eps);
	assertEquals(a0.getPosDegrees(), 0.0, eps);

	// Constructor with RA,DEC,POS
	Attitude a1 = Attitude.fromDegrees(30,40,50);
	assertEquals(a1.getRaDegrees(),  30.0, eps);
	assertEquals(a1.getDecDegrees(), 40.0, eps);
	assertEquals(a1.getPosDegrees(), 50.0, eps);

	double k = Math.PI / 180;
	assertEquals(a1.getAlpha(), 30.0*k, eps);
	assertEquals(a1.getDelta(), 40.0*k, eps);
	assertEquals(a1.getPhi(),   50.0*k, eps);

	// Constructor with Direction,POS
	Direction d = Direction.fromDegrees(30,40);
	Attitude a2 = Attitude.fromDegrees(d, 50);
	assertEquals(a2.getRaDegrees(),  30.0, eps);
	assertEquals(a2.getDecDegrees(), 40.0, eps);
	assertEquals(a2.getPosDegrees(), 50.0, eps);
    }

    /**
     * Test conversion to/from rotation matrix.
     */
    @Test
    public void testToFromMatrix1() {
	double ra  = 30;
	double dec = 40;
	double pos = 50;

	// Test result from Matlab: rdpz2dcm(30*k,40*k,50*k), where k=pi/180
	Matrix3 me = new Matrix3(0.66341394816894d, 0.38302222155949d,  0.64278760968654d,
		0.10504046113295d, 0.80287233747947d, -0.58682408883347d,
		-0.74084305686149d, 0.45682599258567d,  0.49240387650610d)
	.mTranspose();

	Attitude a = Attitude.fromDegrees(ra,dec,pos);
	Matrix3 m = a.toMatrix3();
	assertTrue(m.epsilonEquals(me, 1E-14));

	Attitude a2 = new Attitude(m);
	double eps = 1E-14;
	angleEquals(a2.getRaDegrees(),  ra,  eps);
	angleEquals(a2.getDecDegrees(), dec, eps);
	angleEquals(a2.getPosDegrees(), pos, eps);
    }

    /**
     * Test conversion to/from rotation matrix.
     */
    @Test
    public void testToFromMatrix2() {
	// Test very close to poles
	{
	    double ra  = 30;
	    double dec = 89.99995;
	    double pos = 50; 

	    Attitude a = Attitude.fromDegrees(ra, dec, pos);
	    Matrix3 m = a.toMatrix3();

	    Attitude a2 = new Attitude(m);
	    double eps = 1E-7;

	    angleEquals(a2.getRaDegrees(),  ra,  eps);
	    angleEquals(a2.getDecDegrees(), dec, eps);
	    angleEquals(a2.getPosDegrees(), pos, eps);
	} {
	    double ra  = 30;
	    double dec = -89.99995;
	    double pos = 50; 

	    Attitude a = Attitude.fromDegrees(ra, dec, pos);
	    Matrix3 m = a.toMatrix3();

	    Attitude a2 = new Attitude(m);
	    double eps = 1E-7;

	    angleEquals(a2.getRaDegrees(),  ra,  eps);
	    angleEquals(a2.getDecDegrees(), dec, eps);
	    angleEquals(a2.getPosDegrees(), pos, eps);
	}

	// Test at poles
	double eps = 1E-13;
	{
	    Attitude a = Attitude.fromDegrees(70, 90, 50);
	    Matrix3 m = a.toMatrix3();
	    Attitude aa = new Attitude(m);
	    assertEquals(aa.getRaDegrees() - aa.getPosDegrees(),  20.0, eps);
	    assertEquals(aa.getDecDegrees(),  90.0, eps);

	    angleEquals(aa.getRaDegrees(),   20.0, eps);
	    angleEquals(aa.getDecDegrees(),  90.0, eps);
	    angleEquals(aa.getPosDegrees(),   0.0, eps);
	}{
	    Attitude a = Attitude.fromDegrees(70, -90, 50);
	    Matrix3 m = a.toMatrix3();
	    Attitude aa = new Attitude(m);
	    assertEquals(aa.getRaDegrees() + aa.getPosDegrees(),  120.0, 1E-13);
	    assertEquals(aa.getDecDegrees(),  -90.0, eps);

	    angleEquals(aa.getRaDegrees(),  120.0, eps);
	    angleEquals(aa.getDecDegrees(), -90.0, eps);
	    angleEquals(aa.getPosDegrees(),   0.0, eps);
	}
    }

    /**
     * Test conversion to/from quaternion.
     */
    @Test
    public void testToFromQuaternion1() {
	double ra  = 30;
	double dec = 40;
	double pos = 50;

	// Test result from Matlab: rdpz2q(30*k,40*k,50*k), where k=pi/180
	Quaternion qe = new Quaternion(-0.30337177447126d,
		-0.40219849353411d,
		0.08080468869084d,
		0.86004217369768d);
	Attitude a = Attitude.fromDegrees(ra,dec,pos);
	Quaternion q = a.toQuaternion();
	assertTrue(q.epsilonEquals(qe, 1E-14));

	Attitude a2 = new Attitude(q);
	double eps = 1E-13;
	angleEquals(a2.getRaDegrees(),  ra,  eps);
	angleEquals(a2.getDecDegrees(), dec, eps);
	angleEquals(a2.getPosDegrees(), pos, eps);
    }

    /**
     * Test conversion to/from quaternion.
     */
    @Test
    public void testToFromQuaternion2() {

	// Test very close to poles
	{
	    double ra  = 30;
	    double dec = 89.99995;
	    double pos = 50; 

	    Attitude a = Attitude.fromDegrees(ra, dec, pos);
	    Quaternion q = a.toQuaternion();
	    assertTrue(q.epsilonEquals(q, 1E-14));

	    Attitude a2 = new Attitude(q);
	    double eps = 1E-7;

	    angleEquals(a2.getRaDegrees(),  ra,  eps);
	    angleEquals(a2.getDecDegrees(), dec, eps);
	    angleEquals(a2.getPosDegrees(), pos, eps);
	} {
	    double ra  = 30;
	    double dec = -89.99995;
	    double pos = 50; 

	    Attitude a = Attitude.fromDegrees(ra, dec, pos);
	    Quaternion q = a.toQuaternion();
	    assertTrue(q.epsilonEquals(q, 1E-14));

	    Attitude a2 = new Attitude(q);
	    double eps = 1E-7;

	    angleEquals(a2.getRaDegrees(),  ra,  eps);
	    angleEquals(a2.getDecDegrees(), dec, eps);
	    angleEquals(a2.getPosDegrees(), pos, eps);
	}

	// Test at poles
	{
	    Attitude a = Attitude.fromDegrees(70, 90, 50);
	    Quaternion q = a.toQuaternion();
	    Attitude aa = new Attitude(q);
	    angleEquals(aa.getRaDegrees(),   20.0, 1E-13);
	    angleEquals(aa.getDecDegrees(),  90.0, 1E-13);
	    angleEquals(aa.getPosDegrees(),  0.0, 1E-13);
	}{
	    Attitude a = Attitude.fromDegrees(70, -90, 50);
	    Quaternion q = a.toQuaternion();
	    Attitude aa = new Attitude(q);
	    angleEquals(aa.getRaDegrees(),  120.0, 1E-13);
	    angleEquals(aa.getDecDegrees(), -90.0, 1E-13);
	    angleEquals(aa.getPosDegrees(),   0.0, 1E-13);
	}
    }

    /**
     * Test relativeOffset and relativeAngleTo.
     */
    @Test
    public void testRelativeOffset() {
	double ra = Math.toRadians(20);
	double dec = Math.toRadians(40);
	double pos = Math.toRadians(30);
	double distance = Math.toRadians(15);
	double angle = Math.toRadians(120);

	Attitude a1 = new Attitude(ra, dec, pos);
	Direction d2 = a1.relativeOffset(distance, angle);
	
	Quaternion q1 = a1.toQuaternion();
	Quaternion qd = Quaternion.yRotation(-distance);
	Quaternion qa = Quaternion.xRotation(angle);
	Quaternion qe = q1.multiply(qa.conjugate()).multiply(qd).multiply(qa);
	Direction de = new Direction(new Attitude(qe));
	assertEquals(d2.getAlpha(), de.getAlpha(), 1E-15);
	assertEquals(d2.getDelta(), de.getDelta(), 1E-15);
    }

    @Test
    public void testOffset() {
	double ra = Math.toRadians(20);
	double dec = Math.toRadians(40);
	double pos = Math.toRadians(30);
	double distance = Math.toRadians(15);
	double angle = Math.toRadians(120);

	Attitude a1 = new Attitude(ra, dec, pos);
	Attitude a2 = a1.offset(distance, angle);

	Quaternion q1 = a1.toQuaternion();
	Quaternion q2 = a2.toQuaternion();
	Quaternion qd = q1.conjugate().multiply(q2);
	assertEquals(distance, qd.angle(), 1E-15);
	double ang = qd.axis().angle(Vector3.unitY().negate());
	assertEquals(angle, ang, 1E-15);
	assertEquals(angle, a1.relativeAngleTo(a2), 1E-15);
    }

    @Test
    public void testRotateX() {
	double ra = Math.toRadians(20);
	double dec = Math.toRadians(40);
	double pos = Math.toRadians(30);
	double angle = Math.toRadians(120);
	
	Attitude a1 = new Attitude(ra, dec, pos);
	Attitude a2 = a1.rotateX(angle);
	assertEquals(a1.getAlpha(), a2.getAlpha(), 1E-15);
	assertEquals(a1.getDelta(), a2.getDelta(), 1E-15);
	assertEquals(a1.getPhi() + angle, a2.getPhi(), 1E-15);
    }

    @Test
    public void testRoundedRaDecPos() {
	{
	    // Simple case
	    Attitude a = Attitude.fromDegrees(33.1234, 43.9876, 56.666666);
	    double[] v = a.roundedRaDecPos(3);
	    assertEquals(33.123, v[0], 1E-15);
	    assertEquals(43.988, v[1], 1E-15);
	    assertEquals(56.667, v[2], 1E-15);
	}{
	    // Singularity at DEC = +90 degrees
	    Attitude a = Attitude.fromDegrees(33.1234, 89.9999, 56.6666);
	    double[] v = a.roundedRaDecPos(2);
	    assertEquals(0, v[0], 1E-15);
	    assertEquals(90, v[1], 1E-15);
	    assertEquals(23.54, v[2], 1E-15);
	}{
	    // Singularity at DEC = +90 degrees with large RA and POS
	    Attitude a = Attitude.fromDegrees(359.1234, 89.9999, 359.6666);
	    double[] v = a.roundedRaDecPos(2);
	    assertEquals(0, v[0], 1E-15);
	    assertEquals(90, v[1], 1E-15);
	    assertEquals(0.54, v[2], 1E-15);
	}{
	    // Singularity at DEC = -90 degrees
	    Attitude a = Attitude.fromDegrees(33.1234, -89.9999, 46.4444);
	    double[] v = a.roundedRaDecPos(2);
	    assertEquals(0, v[0], 1E-15);
	    assertEquals(-90, v[1], 1E-15);
	    assertEquals(79.57, v[2], 1E-15);
	}{
	    // Singularity at DEC = -90 degrees with large RA and POS
	    Attitude a = Attitude.fromDegrees(359.1234, -89.9999, 359.4444);
	    double[] v = a.roundedRaDecPos(2);
	    assertEquals(0, v[0], 1E-15);
	    assertEquals(-90, v[1], 1E-15);
	    assertEquals(358.57, v[2], 1E-15);
	}
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Attitude d = Attitude.fromDegrees(30, 40, 50);
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}







