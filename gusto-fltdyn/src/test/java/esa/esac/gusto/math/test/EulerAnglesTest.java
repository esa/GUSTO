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
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.math.EulerAngles;
import esa.esac.gusto.math.EulerAngles.Axes;
import esa.esac.gusto.math.Matrix3;
import esa.esac.gusto.math.Quaternion;

import org.junit.Test;

/**
 * Test harness for EulerAngles class.
 *
 * @author  Jon Brumfitt
 */
public class EulerAnglesTest {
    
    private static final double EPSILON = 1E-15;
    
    private static final Axes[] CYCLIC = new Axes[] { Axes.XYZ, Axes.YZX, Axes.ZXY };
    private static final Axes[] ANTICYCLIC = new Axes[] { Axes.ZYX, Axes.YXZ, Axes.XZY };
    private static final Axes[] SYMMETRIC = new Axes[] { Axes.XYX, Axes.XZX, Axes.YXY, Axes.YZY, Axes.ZXZ, Axes.ZYZ };
    private static final Axes[] ASYMMETRIC = new Axes[] { Axes.XYZ, Axes.XZY, Axes.YXZ, Axes.YZX, Axes.ZXY, Axes.ZYX };
    private static final Axes[] ALL = Axes.values();
    
    /**
     * Test the 'absoluteAngle' test method.
     */
    @Test
    public void testAbsoluteAngle() {
	assertEquals(30, absoluteAngle(30), EPSILON);
	assertEquals(30, absoluteAngle(-30), EPSILON);
	assertEquals(30, absoluteAngle(330), EPSILON);
	assertEquals(30, absoluteAngle(-330), EPSILON);
	assertEquals(30, absoluteAngle(390), EPSILON);
	assertEquals(30, absoluteAngle(-390), EPSILON);
    }
    
    
    /******************** Test Quaternion conversions ********************/
    
    /**
     * Test Euler to Quaternion conversions.
     */
    @Test
    public void testEulerToQuaternion() {
	double a = Math.toRadians(10);
	double b = Math.toRadians(20);
	double c = Math.toRadians(30);
	
	// Symmetric cases
	{
	    Quaternion qe = qx(a).mMultiply(qy(b)).mMultiply(qx(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.XYX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Quaternion qe = qx(a).mMultiply(qz(b)).mMultiply(qx(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.XZX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Quaternion qe = qy(a).mMultiply(qx(b)).mMultiply(qy(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.YXY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Quaternion qe = qy(a).mMultiply(qz(b)).mMultiply(qy(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.YZY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Quaternion qe = qz(a).mMultiply(qx(b)).mMultiply(qz(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.ZXZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Quaternion qe = qz(a).mMultiply(qy(b)).mMultiply(qz(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.ZYZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}
        // Asymmetric cases
	{
	    Quaternion qe = qx(a).mMultiply(qy(b)).mMultiply(qz(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.XYZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Quaternion qe = qx(a).mMultiply(qz(b)).mMultiply(qy(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.XZY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Quaternion qe = qy(a).mMultiply(qx(b)).mMultiply(qz(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.YXZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Quaternion qe = qy(a).mMultiply(qz(b)).mMultiply(qx(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.YZX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Quaternion qe = qz(a).mMultiply(qx(b)).mMultiply(qy(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.ZXY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Quaternion qe = qz(a).mMultiply(qy(b)).mMultiply(qx(c));
	    Quaternion q = EulerAngles.toQuaternion(Axes.ZYX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 
    }
    
    /**
     * Symmetric and asymmetric axes with a>0 and c>0.
     */
    @Test
    public void testQ1a() {
	double[] a = angles(10, 20, 30);
	for(Axes axes: Axes.values()) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Symmetric and asymmetric axes with c<0.
     */
    @Test
    public void testQ1b() {
	double[] a = angles(100, 20, -60);
	for(Axes axes: ALL) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Symmetric and asymmetric axes with a<0.
     */
    @Test
    public void testQ1c() {
	double[] a = angles(-100, 20, -60);
	for(Axes axes: ALL) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Symmetric and asymmetric axes with a<0 and c<0.
     */
    @Test
    public void testQ1d() {
	double[] a = angles(-100, 20, 60);
	for(Axes axes: ALL) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, a, as));
	}
    }
	
    /**
     * Symmetric axes with b>90.
     */
    @Test
    public void testQ2() {
	double[] a = angles(10, 160, 30);
	for(Axes axes: SYMMETRIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Asymmetric axes with b<0.
     */
    @Test
    public void testQ3() {
	double[] a = angles(10, -20, 30);
	for(Axes axes: ASYMMETRIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, a, as));
	}
    }
	
    /**
     * Test that Quaternion at singularity is consistent with Quaternion close to singularity.
     */
    @Test
    public void testQSingularities() {
	for(Axes axes: ANTICYCLIC) {
	    {
		// Difference (a-c) is constant at pole b = PI/2
		Quaternion q1 = EulerAngles.toQuaternion(axes, Math.toRadians(120), Math.toRadians(89.99), Math.toRadians(90));
		Quaternion q2 = EulerAngles.toQuaternion(axes, Math.toRadians(40), Math.toRadians(90), Math.toRadians(10));
		double diff = Math.toDegrees(q1.conjugate().multiply(q2).angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }{
		// Sum (a+c) is constant at pole b = -PI/2
		Quaternion q1 = EulerAngles.toQuaternion(axes, Math.toRadians(120), Math.toRadians(-89.999), Math.toRadians(290));
		Quaternion q2 = EulerAngles.toQuaternion(axes, Math.toRadians(40), Math.toRadians(-90), Math.toRadians(10));
		double diff = Math.toDegrees(q1.conjugate().multiply(q2).angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }
	}

	for(Axes axes: CYCLIC) {
	    {
		// Sum (a+c) is constant at pole b = -PI/2
		Quaternion q1 = EulerAngles.toQuaternion(axes, Math.toRadians(120), Math.toRadians(-89.99), Math.toRadians(90));
		Quaternion q2 = EulerAngles.toQuaternion(axes, Math.toRadians(40), Math.toRadians(-90), Math.toRadians(10));
		double diff = Math.toDegrees(q1.conjugate().multiply(q2).angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }{
		// Difference (a-c) is constant at pole b = PI/2
		Quaternion q1 = EulerAngles.toQuaternion(axes, Math.toRadians(120), Math.toRadians(89.999), Math.toRadians(290));
		Quaternion q2 = EulerAngles.toQuaternion(axes, Math.toRadians(40), Math.toRadians(90), Math.toRadians(10));
		double diff = Math.toDegrees(q1.conjugate().multiply(q2).angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }
	}

	for(Axes axes: SYMMETRIC) {
	    {
		// Sum (a+c) is constant at pole b = 0
		Quaternion q1 = EulerAngles.toQuaternion(axes, Math.toRadians(120), Math.toRadians(0.01), Math.toRadians(-90));
		Quaternion q2 = EulerAngles.toQuaternion(axes, Math.toRadians(40), Math.toRadians(0), Math.toRadians(-10));
		double diff = Math.toDegrees(q1.conjugate().multiply(q2).angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }{
		// Difference (a-c) is constant at pole b = PI
		Quaternion q1 = EulerAngles.toQuaternion(axes, Math.toRadians(120), Math.toRadians(179.99), Math.toRadians(-290));
		Quaternion q2 = EulerAngles.toQuaternion(axes, Math.toRadians(40), Math.toRadians(180), Math.toRadians(-10));
		double diff = Math.toDegrees(q1.conjugate().multiply(q2).angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }
	}
    }

    /**
     * Anticyclic asymmetric axes, singularity at b = PI/2
     */
    @Test
    public void testQAnticyclicSingularity90() {
	double[] a = angles(10, 90, 30);
	double[] ae = angles(-20, 90, 0); // a' = a - c
	for(Axes axes: ANTICYCLIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }

    /**
     * Anticyclic asymmetric axes, singularity at b = -PI/2
     */
    @Test
    public void testQAnticyclicSingularityM90() {
	double[] a = angles(10, -90, 30);
	double[] ae = angles(40, -90, 0); // a' = a + c
	for(Axes axes: ANTICYCLIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Cyclic asymmetric axes, singularity at b = PI/2
     */
    @Test
    public void testQCyclicSingularity90() {
	double[] a = angles(10, 90, 30);
	double[] ae = angles(0, 90, 40); // c' = c + a
	for(Axes axes: CYCLIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Cyclic asymmetric axes, singularity at b = -PI/2
     */
    @Test
    public void testQCyclicSingularityM90() {
	double[] a = angles(10, -90, 30);
	double[] ae = angles(0, -90, 20); // c' = c - a
	for(Axes axes: CYCLIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Symmetric axes, singularity at b = 0
     */
    @Test
    public void testQSymmetricSingularity0() {
	double[] a = angles(10, 0, 30);
	double[] ae = angles(0, 0, 40); // c' = c + a
	for(Axes axes: SYMMETRIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Symmetric axes, b = PI/2
     */
    @Test
    public void testQSymmetric90() {
	double[] a = angles(10, 90, 30);
	double[] ae = a;
	for(Axes axes: SYMMETRIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Symmetric axes, singularity at b = PI
     */
    @Test
    public void testQSymmetricSingularity180() {
	double[] a = angles(10, 180, 30);
	double[] ae = angles(0, 180, 20); // c' = c - a
	for(Axes axes: SYMMETRIC) {
	    Quaternion q = EulerAngles.toQuaternion(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromQuaternion(axes, q);
	    assertTrue(equals(axes, ae, as));
	}
    }
	
    
    /******************** Test Matrix conversions ********************/
    
    /**
     * Test Matrix to Quaternion conversions.
     */
    @Test
    public void testEulerToMatrix() {
	double a = Math.toRadians(10);
	double b = Math.toRadians(20);
	double c = Math.toRadians(30);
	
	// Symmetric cases
	{
	    Matrix3 me = mx(a).mMultiply(my(b)).mMultiply(mx(c));
	    Matrix3 m = EulerAngles.toMatrix3(Axes.XYX, a, b, c);
	    assertTrue(me.epsilonEquals(m, EPSILON));
	}{
	    Matrix3 qe = mx(a).mMultiply(mz(b)).mMultiply(mx(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.XZX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Matrix3 qe = my(a).mMultiply(mx(b)).mMultiply(my(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.YXY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Matrix3 qe = my(a).mMultiply(mz(b)).mMultiply(my(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.YZY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Matrix3 qe = mz(a).mMultiply(mx(b)).mMultiply(mz(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.ZXZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Matrix3 qe = mz(a).mMultiply(my(b)).mMultiply(mz(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.ZYZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}
        // Asymmetric cases
	{
	    Matrix3 qe = mx(a).mMultiply(my(b)).mMultiply(mz(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.XYZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Matrix3 qe = mx(a).mMultiply(mz(b)).mMultiply(my(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.XZY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Matrix3 qe = my(a).mMultiply(mx(b)).mMultiply(mz(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.YXZ, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Matrix3 qe = my(a).mMultiply(mz(b)).mMultiply(mx(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.YZX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 	{
	    Matrix3 qe = mz(a).mMultiply(mx(b)).mMultiply(my(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.ZXY, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	}{
	    Matrix3 qe = mz(a).mMultiply(my(b)).mMultiply(mx(c));
	    Matrix3 q = EulerAngles.toMatrix3(Axes.ZYX, a, b, c);
	    assertTrue(qe.epsilonEquals(q, EPSILON));
	} 
    }
    

    /**
     * Symmetric and asymmetric axes with a>0 and c>0.
     */
    @Test
    public void testM1a() {
	double[] a = angles(10, 20, 30);
	for(Axes axes: Axes.values()) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Symmetric and asymmetric axes with c<0.
     */
    @Test
    public void testM1b() {
	double[] a = angles(100, 20, -60);
	for(Axes axes: ALL) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Symmetric and asymmetric axes with a<0.
     */
    @Test
    public void testM1c() {
	double[] a = angles(-100, 20, -60);
	for(Axes axes: ALL) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, a, as));
	}
    }
    
    /**
     * Symmetric and asymmetric axes with a<0 and c<0.
     */
    @Test
    public void testM1d() {
	double[] a = angles(-100, 20, 60);
	for(Axes axes: ALL) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, a, as));
	}
    }
	
    /**
     * Symmetric axes with b>90.
     */
    @Test
    public void testM2() {
	double[] a = angles(10, 160, 30);
	for(Axes axes: SYMMETRIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, a, as, 1.2E-15)); // Tolerance increased slightly for this test
	}
    }
    
    /**
     * Asymmetric axes with b<0.
     */
    @Test
    public void testM3() {
	double[] a = angles(10, -20, 30);
	for(Axes axes: ASYMMETRIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, a, as));
	}
    }
	
    /**
     * Test that Matrix3 at singularity is consistent with Matrix3 close to singularity.
     */
    @Test
    public void testMSingularities() {
	for(Axes axes: ANTICYCLIC) {
	    {
		// Difference (a-c) is constant at pole b = PI/2
		Matrix3 m1 = EulerAngles.toMatrix3(axes, Math.toRadians(120), Math.toRadians(89.99), Math.toRadians(90));
		Matrix3 m2 = EulerAngles.toMatrix3(axes, Math.toRadians(40), Math.toRadians(90), Math.toRadians(10));
		double diff = Math.toDegrees(m1.transpose().multiply(m2).toQuaternion().angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }{
		// Sum (a+c) is constant at pole b = -PI/2
		Matrix3 m1 = EulerAngles.toMatrix3(axes, Math.toRadians(120), Math.toRadians(-89.999), Math.toRadians(290));
		Matrix3 m2 = EulerAngles.toMatrix3(axes, Math.toRadians(40), Math.toRadians(-90), Math.toRadians(10));
		double diff = Math.toDegrees(m1.transpose().multiply(m2).toQuaternion().angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }
	}

	for(Axes axes: CYCLIC) {
	    {
		// Sum (a+c) is constant at pole b = -PI/2
		Matrix3 m1 = EulerAngles.toMatrix3(axes, Math.toRadians(120), Math.toRadians(-89.99), Math.toRadians(90));
		Matrix3 m2 = EulerAngles.toMatrix3(axes, Math.toRadians(40), Math.toRadians(-90), Math.toRadians(10));
		double diff = Math.toDegrees(m1.transpose().multiply(m2).toQuaternion().angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }{
		// Difference (a-c) is constant at pole b = PI/2
		Matrix3 m1 = EulerAngles.toMatrix3(axes, Math.toRadians(120), Math.toRadians(89.999), Math.toRadians(290));
		Matrix3 m2 = EulerAngles.toMatrix3(axes, Math.toRadians(40), Math.toRadians(90), Math.toRadians(10));
		double diff = Math.toDegrees(m1.transpose().multiply(m2).toQuaternion().angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }
	}

	for(Axes axes: SYMMETRIC) {
	    {
		// Sum (a+c) is constant at pole b = 0
		Matrix3 m1 = EulerAngles.toMatrix3(axes, Math.toRadians(120), Math.toRadians(0.01), Math.toRadians(-90));
		Matrix3 m2 = EulerAngles.toMatrix3(axes, Math.toRadians(40), Math.toRadians(0), Math.toRadians(-10));
		double diff = Math.toDegrees(m1.transpose().multiply(m2).toQuaternion().angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }{
		// Difference (a-c) is constant at pole b = PI
		Matrix3 m1 = EulerAngles.toMatrix3(axes, Math.toRadians(120), Math.toRadians(179.99), Math.toRadians(-290));
		Matrix3 m2 = EulerAngles.toMatrix3(axes, Math.toRadians(40), Math.toRadians(180), Math.toRadians(-10));
		double diff = Math.toDegrees(m1.transpose().multiply(m2).toQuaternion().angle());
		assertEquals(0, absoluteAngle(diff), 1E-2);
	    }
	}
    }

    /**
     * Anticyclic asymmetric axes, singularity at b = PI/2
     */
    @Test
    public void testMAnticyclicSingularity90() {
	double[] a = angles(10, 90, 30);
	double[] ae = angles(-20, 90, 0); // a' = a - c
	for(Axes axes: ANTICYCLIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }

    /**
     * Anticyclic asymmetric axes, singularity at b = -PI/2
     */
    @Test
    public void testMAnticyclicSingularityM90() {
	double[] a = angles(10, -90, 30);
	double[] ae = angles(40, -90, 0); // a' = a + c
	for(Axes axes: ANTICYCLIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Cyclic asymmetric axes, singularity at b = PI/2
     */
    @Test
    public void testMCyclicSingularity90() {
	double[] a = angles(10, 90, 30);
	double[] ae = angles(0, 90, 40); // c' = c + a
	for(Axes axes: CYCLIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Cyclic asymmetric axes, singularity at b = -PI/2
     */
    @Test
    public void testMCyclicSingularityM90() {
	double[] a = angles(10, -90, 30);
	double[] ae = angles(0, -90, 20); // c' = c - a
	for(Axes axes: CYCLIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Symmetric axes, singularity at b = 0
     */
    @Test
    public void testMSymmetricSingularity0() {
	double[] a = angles(10, 0, 30);
	double[] ae = angles(0, 0, 40); // c' = c + a
	for(Axes axes: SYMMETRIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Symmetric axes, b = PI/2
     */
    @Test
    public void testMSymmetric90() {
	double[] a = angles(10, 90, 30);
	double[] ae = a;
	for(Axes axes: SYMMETRIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }
    
    /**
     * Symmetric axes, singularity at b = PI
     */
    @Test
    public void testMSymmetricSingularity180() {
	double[] a = angles(10, 180, 30);
	double[] ae = angles(0, 180, 20); // c' = c - a
	for(Axes axes: SYMMETRIC) {
	    Matrix3 m = EulerAngles.toMatrix3(axes, a[0], a[1], a[2]);
	    double[] as = EulerAngles.fromMatrix3(axes, m);
	    assertTrue(equals(axes, ae, as));
	}
    }
	
    

    /******************** Utility methods to support test ********************/
    
    /**
     * Return array of angles converted from degrees.
     */
    private static double[] angles(double a, double b, double c) {
	return new double[] { Math.toRadians(a), Math.toRadians(b), Math.toRadians(c) };
    }
    
    /**
     * Return a rotation about the X axis.
     */
    private static Quaternion qx(double angle) {
	return Quaternion.xRotation(angle);
    }
    
    /**
     * Return a rotation about the Y axis.
     */
    private static Quaternion qy(double angle) {
	return Quaternion.yRotation(angle);
    }
    
    /**
     * Return a rotation about the Z axis.
     */
    private static Quaternion qz(double angle) {
	return Quaternion.zRotation(angle);
    }
    
    /**
     * Return a rotation about the X axis.
     */
    private static Matrix3 mx(double angle) {
	return Matrix3.xRotation(angle);
    }
    
    /**
     * Return a rotation about the Y axis.
     */
    private static Matrix3 my(double angle) {
	return Matrix3.yRotation(angle);
    }
    
    /**
     * Return a rotation about the Z axis.
     */
    private static Matrix3 mz(double angle) {
	return Matrix3.zRotation(angle);
    } 
 
    /**
     * Normalize angular error to the range [0, 180].
     */
    private double absoluteAngle(double a) {
	double d = Math.abs(a) % 360;
	return (d > 180) ? 360 - d : d;
    }
    
    /**
     * Test whether two arrays of angles are equal.
     */
    private static boolean equals(Axes axes, double[] exp, double[] act, double epsilon) {
	boolean ok = (Math.abs(exp[0] - act[0]) < epsilon) 
	          && (Math.abs(exp[1] - act[1]) < epsilon) 
	          && (Math.abs(exp[2] - act[2]) < epsilon);	
	if(!ok) {
	    print(axes + " Expected: ", exp);
	    print(axes + " Actual:   ", act);
	    System.err.println();
	}
	return ok;
    }
    
    /**
     * Test whether two arrays of angles are equal.
     */
    private static boolean equals(Axes axes, double[] exp, double[] act) {
	return equals(axes, exp, act, EPSILON);
    }
    
    /**
     * Print array of angles as degrees.
     */
    private static void print(String prefix, double[] as) {
	System.err.print(prefix + " [");
	for(int i=0; i<as.length; i++) {
	    System.err.print(String.format("%6.2f ", Math.toDegrees(as[i])));
	}
	System.err.println("]");
    }
}

