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
import esa.esac.gusto.math.AxisAngle;
import esa.esac.gusto.math.Matrix3;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

/**
 * Test harness for Quaternion class.
 *
 * @author  Jon Brumfitt
 */
public class QuaternionTest {

    /**
     * Test equality. The other tests rely on this.
     */
    @Test
    public void testEquality() {
	double eps = 1E-12;
	Quaternion q0 = new Quaternion(2,6,3,8);
	Quaternion q1 = new Quaternion(2,6,3,8);
	Quaternion q2 = new Quaternion(2,6,3+eps,8);

	assertEquals(q0.hashCode(), q1.hashCode());

	assertTrue(q0.equals(q1));
	assertFalse(q1.equals(q2));

	assertTrue(q1.epsilonEquals(q2,eps*1.0001));
	assertFalse(q1.epsilonEquals(q2,eps));
    }

    @Test
    public void testClone() {
	Quaternion q1 = new Quaternion(2,6,3,8);
	Quaternion q2 = (Quaternion)q1.clone();
	assertTrue(q1.equals(q2));
    } 

    @Test
    public void testCopy() {
	Quaternion q1 = new Quaternion(2,6,3,8);
	Quaternion q2 = q1.copy();
	assertTrue(q1.equals(q2));
    }

    @Test
    public void testConstruction() {
	Quaternion q2 = new Quaternion(2,6,3,8);
	Quaternion q3 = new Quaternion(q2);
	assertTrue(q2.equals(q3));
    }

    @Test
    public void testGet() {
	// This needs changing if constructor normalizes the Quaternion.
	double eps = 1E-14;
	Quaternion q = new Quaternion(2,6,3,8);
	assertEquals(q.getX(), 2., eps);
	assertEquals(q.getY(), 6., eps);
	assertEquals(q.getZ(), 3., eps);
	assertEquals(q.getW(), 8., eps);
    }

    @Test
    public void testSet() {
	Quaternion q1 = new Quaternion(2,6,3,8);
	Quaternion q2 = new Quaternion(1,0,0,0);
	q2.set(q1);
	assertTrue(q1.equals(q2));
    }

    @Test
    public void testAxisAngle() {
	double k = Math.PI / 180;

	// Test result from Matlab: axa2q([1;0;0], pi/3)
	Quaternion qe1 = new Quaternion(0.5d, 0, 0, 0.86602540378444d);
	Quaternion q1 = new Quaternion(new Vector3(1,0,0), 60 * k);
	assertTrue(q1.epsilonEquals(qe1, 1E-14));

	// Test result from Matlab: axa2q(normalise([2;3;4])', pi/6)
	Quaternion qe2 = new Quaternion(0.09612298021395d,
		0.14418447032092d,
		0.19224596042790d,
		0.96592582628907d);
	Vector3 v = new Vector3(2,3,4);
	double a = 30 * k;
	Quaternion q2 = new Quaternion(v, a);
	assertTrue(q2.epsilonEquals(qe2, 1E-14));

	// Test constructor with AngleAxis argument
	Quaternion q3 = new Quaternion(new AxisAngle(v, a));
	assertTrue(q2.equals(q3));

	// Test conversion back to axis and angle
	Vector3 v2 = q2.axis();
	assertTrue(v2.epsilonEquals(v.normalize(), 1E-15));
	assertEquals(q2.angle(), a, 1E-15);

	AxisAngle aa = q2.toAxisAngle();
	assertTrue(aa.axis().epsilonEquals(v.normalize(), 1E-15));
	assertEquals(q2.angle(), aa.angle(), 1E-15);

	// Test extreme case of 180 degree rotation
	double a4 = Math.PI;
	Quaternion q4 = new Quaternion(v, a4);
	assertTrue(q4.axis().epsilonEquals(v.normalize(), 1E-15));
	assertEquals(q4.angle(), a4, 1E-15);
    }

    @Test
    public void testRotation() {
	Vector3 v1 = new Vector3(4,3,2).normalize();
	Vector3 v2 = new Vector3(5,-2,4).normalize();
	Quaternion q = Quaternion.rotation(v1, v2);
	Vector3 v3 = q.rotateVector(v1);
	double err = Math.toDegrees(v2.angle(v3));
	assertEquals(0, err, 1E-13);
    }
    
    @Test
    public void testNorm() {
	// This would change if constructor normalizes quaternion
	Quaternion q = new Quaternion(2,3,4,5);
	assertEquals(q.norm(), Math.sqrt(54), 1E-15);
    }
    
    @Test
    public void testNormSquared() {
	// This would change if constructor normalizes quaternion
	Quaternion q = new Quaternion(2,3,4,5);
	assertEquals(q.normSquared(), 54, 1E-15);
    }

    @Test
    public void testNormalize() {
	Quaternion q = new Quaternion(2,3,4,5);
	Quaternion qn = q.copy().mNormalize();
	assertEquals(qn.norm(), 1, 1E-15);
    }

    @Test
    public void testIsNormalized() {
	Quaternion q1 = new Quaternion(2,3,4,5);
	q1.mNormalize();
	assertTrue(q1.isNormalized(1E-15));

	// This would not apply if the constructor normalizes the Quaternion
	Quaternion q2 = new Quaternion(2,3,4,5);
	assertFalse(q2.isNormalized(1E-15));
    }

    @Test
    public void testConjugate() {
	Quaternion q1 = new Quaternion(2,3,4,5);
	Quaternion q2 = new Quaternion(-2,-3,-4,5);
	q1.mConjugate();
	assertTrue(q1.equals(q2));
    }

    @Test
    public void testMultiply() {
	Quaternion q1 = new Quaternion(2,3,4,5);
	q1.mNormalize();

	Quaternion q2 = new Quaternion(2,6,3,8);
	q2.mNormalize();

	// Test result from Matlab: qmult(qnorm([2,3,4,5]),qnorm([2,6,3,8]))
	Quaternion qe = new Quaternion(0.14081748498447d,
		0.71688901446640d,
		0.67848424583427d,
		0.07680953726426d);

	Quaternion q = q1.copy().mMultiply(q2);
	assertTrue(q.epsilonEquals(qe, 1E-14));

	// Compare this with result of multiplying the corresponding
	// rotation matrices.

	Matrix3 m1 = q1.toMatrix3();
	Matrix3 m2 = q2.toMatrix3();
	Matrix3 m = m1.multiply(m2);
	Quaternion qm = m.toQuaternion();

	assertTrue(q.epsilonEquals(qm, 1E-10));
    }

    @Test
    public void testMultiply2() {
	// Check that I.J = K
	Quaternion i = new Quaternion(1,0,0,0);
	Quaternion j = new Quaternion(0,1,0,0);
	Quaternion k = new Quaternion(0,0,1,0);
	Quaternion minus1 = new Quaternion(0,0,0,-1);

	Quaternion k1 = i.copy().mMultiply(j);
	assertTrue(k1.epsilonEquals(k, 1E-15));

	// Check that I.I = -1
	Quaternion ii = i.copy().mMultiply(i);
	assertTrue(ii.epsilonEquals(minus1, 1E-15));

	Quaternion ijk = i.copy().mMultiply(j).mMultiply(k);
	assertTrue(ijk.epsilonEquals(minus1, 1E-15));
    }
    
    @Test
    public void testRotateQuaternion() {
	Quaternion a = new Quaternion(1.2,2.3,3.7,4.6);
	Quaternion b = new Quaternion(3.1,5.2,7.3,8.4);

        Quaternion q = a.rotate(b);
        // Check the angle of b has not changed
        assertEquals(b.angle(), q.angle(), 1E-15);
        
        // Compare with explicit transformation
        Quaternion q3 = a.conjugate().mMultiply(1 / a.normSquared());
        Quaternion qe = a.multiply(b).multiply(q3);
        assertTrue(equiv(qe, q, 2E-15));
	
        // Transform back again and compare with original
        Quaternion q4 = q3.rotate(q);
        assertTrue(equiv(b, q4, 5E-16));
    }

    @Test
    public void testDot() {
	Quaternion a = new Quaternion(1,2,3,4);
	Quaternion b = new Quaternion(3,5,2,1);

	assertEquals(a.dot(b), 23, 1E-15);
    }

    @Test
    public void testRotate() {
	AxisAngle aa = new AxisAngle(new Vector3(3,2,5), Math.PI/3);
	Quaternion q = new Quaternion(aa);
	Vector3 v = new Vector3(1,2,3);

	// Rotate with a matrix
	Matrix3 m = q.toMatrix3();  // ACTIVE matrix
	Vector3 v3passive = m.copy().mTranspose().multiply(v);
	Vector3 v3active = m.multiply(v);

	// Rotate with a quaternion
	Vector3 v2passive = q.rotateAxes(v);
	Vector3 v2active  = q.rotateVector(v);

	assertTrue(v2passive.epsilonEquals(v3passive, 1E-15));
	assertTrue(v2active.epsilonEquals(v3active, 1E-15));

	// Compare with simple definition of passive quaternion rotation.
	// Note that mMultiply uses convention with reversed arguments.
	Quaternion qv = new Quaternion(v.getX(),v.getY(),v.getZ(),0);
	Quaternion q4 = q.copy().mConjugate().mMultiply(qv.copy().mMultiply(q));
	Vector3 v4 = new Vector3(q4.getX(), q4.getY(), q4.getZ());

	assertTrue(v4.epsilonEquals(v2passive, 1E-15));
    }

    @Test
    public void testRotateI() {
	Quaternion q = new Quaternion(3,5,7,11);
	Vector3 a = q.rotateI();
	Vector3 b = q.rotateVector(new Vector3(1,0,0));
	assertTrue(a.epsilonEquals(b, 1E-15));
    }

    @Test
    public void testRotateJ() {
	Quaternion q = new Quaternion(3,5,7,11);
	Vector3 a = q.rotateJ();
	Vector3 b = q.rotateVector(new Vector3(0,1,0));
	assertTrue(a.epsilonEquals(b, 1E-15));
    }

    @Test
    public void testRotateK() {
	Quaternion q = new Quaternion(3,5,7,11);
	Vector3 a = q.rotateK();
	Vector3 b = q.rotateVector(new Vector3(0,0,1));
	assertTrue(a.epsilonEquals(b, 1E-15));
    }

    @Test
    public void testPower() {
	// Create a unit quaternion
	AxisAngle aa = new AxisAngle(new Vector3(3,2,5), 2*Math.PI/3);
	Quaternion q = new Quaternion(aa);

	// Test cube
	Quaternion q3 = q.copy().mPower(3);
	Quaternion q3e = q.copy().mMultiply(q).mMultiply(q);
	assertTrue(q3.epsilonEquals(q3e, 1E-15));

	// Test cube-root
	Quaternion q13 = q.copy().mPower(1d/3);
	assertTrue(q13.axis().epsilonEquals(q.axis(), 1E-15));
	assertEquals(q13.angle(), q.angle() / 3, 1E-15);

	// Test inverse of unit quaternion
	Quaternion qi = q.copy().mPower(-1);
	assertTrue(qi.epsilonEquals(q.copy().mConjugate(), 1E-15));
    }
    
    @Test
    public void testSqrt() {
	Vector3 axis = new Vector3(1,2,3).normalize();
	double angle = 0.6;
	Quaternion q1 = new Quaternion(axis, angle);
	
	// Take sqrt then square the result
	Quaternion q2 = q1.copy().sqrt();
	Quaternion q3 = q2.multiply(q2);
	double err = q1.conjugate().multiply(q3).angle();
	assertEquals(0, err, 1E-15);
    }

    /** Test slerp method without boolean argument */
    @Test
    public void testSlerp1() {
	Quaternion qa = Attitude.fromDegrees(30,40,50).toQuaternion();
	Quaternion qb = Attitude.fromDegrees(270,40,50).toQuaternion();
	Quaternion qc = Attitude.fromDegrees(30,-40,50).toQuaternion();
	
	// Rotation from qa -> qb = 240 degrees
	// Rotation from qa -> qc = 80 degrees

	// Test simple case of alpha = 0
	assertTrue(qa.slerp(qb, 0.0).epsilonEquals(qa, 1E-15));
	
	// Test simple case of alpha = 1
	Quaternion q0t = qa.slerp(qb, 1.0);
	assertTrue(equiv(q0t, qb, 1E-15));

	// Test one-half with angle (in 4-space) > 90 degrees
	Quaternion q1t = qa.conjugate().mMultiply(qa.slerp(qb, 0.5));
	Quaternion q2t = qa.multiply(q1t).mMultiply(q1t);
	assertTrue(equiv(q2t, qb, 1E-15));
	assertEquals(Math.toDegrees(q1t.angle()), (360-240)/2, 1E-6);

	// Test one-third with angle (in 4-space) < 90 degrees
	Quaternion q3t = qa.conjugate().mMultiply(qa.slerp(qc, 1d/3));
	Quaternion q4t = qa.multiply(q3t).mMultiply(q3t).mMultiply(q3t);
	assertTrue(equiv(q4t, qc, 1E-15));
	assertEquals(Math.toDegrees(q3t.angle()), 80./3, 1E-6);
	
	// Test one-third with angle (in 4-space) > 90 degrees
	// Swap order or quaternions
	Quaternion q5t = qa.conjugate().mMultiply(qa.slerp(qb, 1d/3));
	Quaternion q6t = qa.multiply(q5t).mMultiply(q5t).mMultiply(q5t);
	assertTrue(equiv(q6t, qb, 1E-15));
	assertEquals(Math.toDegrees(q5t.angle()), (360-240)/3, 1E-6);
    }
    
    /** Test slerp method with boolean argument */
    @Test
    public void testSlerp2() {
	Quaternion qa = Attitude.fromDegrees(30,40,50).toQuaternion();
	Quaternion qb = Attitude.fromDegrees(270,40,50).toQuaternion();
	Quaternion qc = Attitude.fromDegrees(30,-40,50).toQuaternion();
	
	// Rotation from qa -> qb = 240 degrees
	// Rotation from qa -> qc = 80 degrees

	// Test simple case of alpha = 0
	assertTrue(qa.slerp(qb, 0.0, true).epsilonEquals(qa, 1E-15));
	assertTrue(qa.slerp(qb, 0.0, false).epsilonEquals(qa, 1E-15));
	
	// Test simple case of alpha = 1
	Quaternion q0t = qa.slerp(qb, 1.0, true);
	assertTrue(equiv(q0t, qb, 1E-15));
	Quaternion q0f = qa.slerp(qb, 1.0, false);
	assertTrue(equiv(q0f, qb, 1E-15));

	// Test one-half with angle (in 4-space) > 90 degrees
	Quaternion q1t = qa.conjugate().mMultiply(qa.slerp(qb, 0.5, true));
	Quaternion q2t = qa.multiply(q1t).mMultiply(q1t);
	assertTrue(equiv(q2t, qb, 1E-15));
	assertEquals(Math.toDegrees(q1t.angle()), (360-240)/2, 1E-6);
	
	Quaternion q1f = qa.conjugate().mMultiply(qa.slerp(qb, 0.5, false));
	Quaternion q2f = qa.multiply(q1f).mMultiply(q1f);
	assertTrue(equiv(q2f, qb, 1E-15));
	assertEquals(Math.toDegrees(q1f.angle()), 240/2, 1E-6);

	// Test one-third with angle (in 4-space) < 90 degrees
	Quaternion q3t = qa.conjugate().mMultiply(qa.slerp(qc, 1d/3, true));
	Quaternion q4t = qa.multiply(q3t).mMultiply(q3t).mMultiply(q3t);
	assertTrue(equiv(q4t, qc, 1E-15));
	assertEquals(Math.toDegrees(q3t.angle()), 80./3, 1E-6);

	Quaternion q3f = qa.conjugate().mMultiply(qa.slerp(qc, 1d/3, false));
	Quaternion q4f = qa.multiply(q3f).mMultiply(q3f).mMultiply(q3f);
	assertTrue(equiv(q4f, qc, 1E-15));
	assertEquals(Math.toDegrees(q3f.angle()), 80./3, 1E-6);
	
	// Test one-third with angle (in 4-space) > 90 degrees
	// Swap order or quaternions
	Quaternion q5t = qa.conjugate().mMultiply(qa.slerp(qb, 1d/3, true));
	Quaternion q6t = qa.multiply(q5t).mMultiply(q5t).mMultiply(q5t);
	assertTrue(equiv(q6t, qb, 1E-15));
	assertEquals(Math.toDegrees(q5t.angle()), (360-240)/3, 1E-6);
	
	Quaternion q5f = qa.conjugate().mMultiply(qa.slerp(qb, 1d/3, false));
	Quaternion q6f = qa.multiply(q5f).mMultiply(q5f).mMultiply(q5f);
	assertTrue(equiv(q6f, qb, 1E-15));
	assertEquals(Math.toDegrees(q5f.angle()), 240/3, 1E-6);
    }
    
    @Test
    public void testLerp() {
	Quaternion q1 = new Quaternion(1,2,3,4);
	Quaternion q2 = new Quaternion(2,4,3,5);
	Quaternion q3 = new Quaternion(-1,-2,-3,4);
	Quaternion q4 = new Quaternion(-1,-3,-3,-4);
	
	assertTrue(equiv(q1.lerp(q2, 0), q1, 1E-15));
	assertTrue(equiv(q1.lerp(q2, 1), q2, 1E-15));
	Quaternion qe = new Quaternion(1.4, 2.8, 3, 4.4);
	assertTrue(equiv(q1.lerp(q2, 0.4), qe, 1E-15));
	assertTrue(equiv(q1.lerp(q3, 0.5), new Quaternion(0,0,0,4), 1E-15));
	assertTrue(equiv(q1.lerp(q4, 0.5), new Quaternion(1,2.5,3,4), 1E-15));
	assertTrue(equiv(q4.lerp(q1, 0.5), new Quaternion(-1,-2.5,-3,-4), 1E-15));
    }
    
    @Test
    public void testMAdd() {
	Quaternion q1 = new Quaternion(1,2,3,4);
	Quaternion q2 = new Quaternion(3,-3,5,7);
	Quaternion qae = new Quaternion(4,-1,8,11);
	
	Quaternion qa = q1.mAdd(q2);
	assertTrue(equiv(qae, q1, 1E-15));
	assertTrue(equiv(qae, qa, 1E-15));
    }
    
    @Test
    public void testMSubtract() {
	Quaternion q1 = new Quaternion(1,2,3,4);
	Quaternion q2 = new Quaternion(3,-3,5,7);
	Quaternion qse = new Quaternion(-2,5,-2,-3);
	
	Quaternion qs = q1.mSubtract(q2);
	assertTrue(equiv(qse, q1, 1E-15));
	assertTrue(equiv(qse, qs, 1E-15));
    }

    private boolean equiv(Quaternion q1, Quaternion q2, double epsilon) {
	return q1.epsilonEquals(q2, epsilon)
	|| q1.epsilonEquals(q2.copy().mMultiply(-1), epsilon);
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Quaternion d = new Quaternion(1, 2, 3, 4);
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}







