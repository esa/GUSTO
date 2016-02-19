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

import org.junit.Before;
import org.junit.Test;

/**
 * Test harness for Matrix3 class.
 *
 * @author  Jon Brumfitt
 */
public class Matrix3Test {

    @Before
    public void setup() {
	new Matrix3(2,6,3,8,1,5,4,9,7);
    }

    /**
     * Test equality. The other tests rely on this.
     */
    @Test
    public void testEquality() {
	double eps = 1E-12;
	Matrix3 m0 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m2 = new Matrix3(2,6,3+eps,8,1,5,4,9,7);

	assertEquals(m0.hashCode(), m1.hashCode());

	assertTrue(m0.equals(m1));
	assertFalse(m1.equals(m2));

	assertTrue(m1.epsilonEquals(m2,eps*1.0001));
	assertFalse(m1.epsilonEquals(m2,eps));
    }

    @Test
    public void testClone() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m2 = (Matrix3)m1.clone();
	assertTrue(m1.equals(m2));
    } 

    @Test
    public void testCopy() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m2 = m1.copy();
	assertTrue(m1.equals(m2));
    }

    @Test
    public void testConstruction1() {
	Matrix3 m0 = new Matrix3(0,0,0,0,0,0,0,0,0);
	Matrix3 m1 = new Matrix3();
	assertTrue(m0.equals(m1));

	Matrix3 m2 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m3 = new Matrix3(m2);
	assertTrue(m2.equals(m3));

	Matrix3 m4 = new Matrix3(new double[]{2,6,3,8,1,5,4,9,7});
	assertTrue(m2.equals(m4));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstruction2() {
	@SuppressWarnings("unused")  // "v" is not used
	Matrix3 v = new Matrix3(new double[]{2,6,3,8,1,5,4,9});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstruction3() {
	@SuppressWarnings("unused")  // "v" is not used
	Matrix3 v = new Matrix3(new double[]{2,6,3,8,1,5,4,9,7,0});
    }

    @Test
    public void testConstruction4() {
	Matrix3 m2 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m5 = new Matrix3(new double[][]{
		new double[]{2,6,3},
		new double[]{8,1,5},
		new double[]{4,9,7}});
	assertTrue(m2.equals(m5));
    }

    @Test
    public void testIdentityMatrix() {
	assertTrue(Matrix3.identity().equals(new Matrix3(1,0,0,0,1,0,0,0,1)));
    }
    
    @Test
    public void testXRotation() {
	double angle = 0.2;
	Matrix3 m = Matrix3.xRotation(angle);
	Vector3 v = new Vector3(2,3,4);
	Vector3 v2 = m.multiply(v);

	double a = Math.atan2(v.getY(), v.getZ()) - Math.atan2(v2.getY(), v2.getZ());
	assertEquals(angle, a, 1E-14);
	assertEquals(v.norm(), v2.norm(), 1E-15);
	assertEquals(v.getX(), v2.getX(), 1E-15);
    }
    
    @Test
    public void testYRotation() {
	double angle = 0.2;
	Matrix3 m = Matrix3.yRotation(angle);
	Vector3 v = new Vector3(2,3,4);
	Vector3 v2 = m.multiply(v);

	double a = Math.atan2(v.getZ(), v.getX()) - Math.atan2(v2.getZ(), v2.getX());
	assertEquals(angle, a, 1E-14);
	assertEquals(v.norm(), v2.norm(), 1E-15);
	assertEquals(v.getY(), v2.getY(), 1E-15);
    }
    
    @Test
    public void testGet1() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	double eps = 1E-14;
	assertEquals(m1.get(0,0), 2., eps);
	assertEquals(m1.get(0,1), 6., eps);
	assertEquals(m1.get(0,2), 3., eps);
	assertEquals(m1.get(1,0), 8., eps);
	assertEquals(m1.get(1,1), 1., eps);
	assertEquals(m1.get(1,2), 5., eps);
	assertEquals(m1.get(2,0), 4., eps);
	assertEquals(m1.get(2,1), 9., eps);
	assertEquals(m1.get(2,2), 7., eps);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet2() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.get(-1,1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet3() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.get(3,1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet4() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.get(1,-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet5() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.get(1,3);
    }

    @Test
    public void testSet1() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m = new Matrix3();
	m.set(0,0,2);
	m.set(0,1,6);
	m.set(0,2,3);
	m.set(1,0,8);
	m.set(1,1,1);
	m.set(1,2,5);
	m.set(2,0,4);
	m.set(2,1,9);
	m.set(2,2,7);
	assertTrue(m.equals(m1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSet2() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.set(1,-1,1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSet3() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.set(-1,1,1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSet4() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.set(1,3,1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSet5() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.set(3,1,1);
    }

    @Test
    public void testSet6() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m2 = new Matrix3();
	m2.set(m1);
	assertTrue(m1.equals(m2));

	// Check that it is a copy
	double eps = 1E-14;
	m2.set(1,1,10);
	assertEquals(m2.get(1,1), 10., eps);
	assertEquals(m1.get(1,1), 1., eps);
    }

    @Test
    public void testGetRowOrColumn1() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	assertTrue(m1.getRow(0).equals(new Vector3(2,6,3)));
	assertTrue(m1.getRow(1).equals(new Vector3(8,1,5)));
	assertTrue(m1.getRow(2).equals(new Vector3(4,9,7)));

	assertTrue(m1.getColumn(0).equals(new Vector3(2,8,4)));
	assertTrue(m1.getColumn(1).equals(new Vector3(6,1,9)));
	assertTrue(m1.getColumn(2).equals(new Vector3(3,5,7)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetRowOrColumn2() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.getRow(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetRowOrColumn3() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.getRow(3);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetRowOrColumn4() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.getColumn(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetRowOrColumn5() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	m1.getColumn(3);
    }

    @Test
    public void testSetColumn() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m = new Matrix3();
	m.setColumn(0,new Vector3(2,8,4));
	m.setColumn(1,new Vector3(6,1,9));
	m.setColumn(2,new Vector3(3,5,7));
	assertTrue(m.equals(m1));
    }

    @Test
    public void testSetRow() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m = new Matrix3();
	m.setRow(0,new Vector3(2,6,3));
	m.setRow(1,new Vector3(8,1,5));
	m.setRow(2,new Vector3(4,9,7));
	assertTrue(m.equals(m1));
    }

    @Test
    public void getRows() {
	Matrix3 m = new Matrix3(1,2,3,4,5,6,7,8,9);
	Vector3[] v = m.getRows();
	assertTrue(v[0].epsilonEquals(new Vector3(1,2,3), 1E-15));
	assertTrue(v[1].epsilonEquals(new Vector3(4,5,6), 1E-15));
	assertTrue(v[2].epsilonEquals(new Vector3(7,8,9), 1E-15));
    }
    
    @Test
    public void getColumns() {
	Matrix3 m = new Matrix3(1,2,3,4,5,6,7,8,9);
	Vector3[] v = m.getColumns();
	assertTrue(v[0].epsilonEquals(new Vector3(1,4,7), 1E-15));
	assertTrue(v[1].epsilonEquals(new Vector3(2,5,8), 1E-15));
	assertTrue(v[2].epsilonEquals(new Vector3(3,6,9), 1E-15));
    }
    
    @Test
    public void testGetDiagonal() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	assertTrue(m1.getDiagonal().equals(new Vector3(2,1,7)));
    }  

    @Test
    public void testFromDiagonal() {
	Matrix3 m = Matrix3.fromDiagonal(new Vector3(2,6,3));
	assertTrue(m.equals(new Matrix3(2,0,0,
		0,6,0,
		0,0,3)));
    }

    @Test
    public void testGetElements() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	double[] el = m1.getElements();
	assertEquals(el.length, 9);

	double eps = 1E-14;
	assertEquals(el[0], 2., eps);
	assertEquals(el[1], 6., eps);
	assertEquals(el[2], 3., eps);
	assertEquals(el[3], 8., eps);
	assertEquals(el[4], 1., eps);
	assertEquals(el[5], 5., eps);
	assertEquals(el[6], 4., eps);
	assertEquals(el[7], 9., eps);
	assertEquals(el[8], 7., eps);
    }

    @Test
    public void testTrace() {
	double eps = 1E-14;
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	assertEquals(m1.trace(), 10., eps);
    }

    @Test
    public void testDeterminant() {
	double eps = 1E-14;
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	// Test result computed with Matlab
	assertEquals(m1.determinant(), -88., eps);
    }

    @Test
    public void testMTranspose() {
	Matrix3 m = new Matrix3(2,6,3,8,1,5,4,9,7);
	m.mTranspose();
	assertTrue(m.equals(new Matrix3(2,8,4,6,1,9,3,5,7)));
    }
    
    @Test
    public void testTranspose() {
	Matrix3 m = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m2 = m.transpose();
	assertFalse(m.equals(m2));
	assertTrue(m2.equals(new Matrix3(2,8,4,6,1,9,3,5,7)));
    }

    @Test
    public void testInvert() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 id = new Matrix3(1,0,0,0,1,0,0,0,1);

	Matrix3 m = m1.copy();
	m.mInvert();
	assertTrue(m.multiply(m1).epsilonEquals(id, 1E-15));
    }

    @Test
    public void testNegate() {
	Matrix3 m = new Matrix3(2,6,3,8,1,5,4,9,7);
	m.mNegate();
	assertTrue(m.equals(new Matrix3(-2,-6,-3,-8,-1,-5,-4,-9,-7)));
    }

    @Test
    public void testMaxAbs() {
	double eps = 1E-14;
	Matrix3 m1 = new Matrix3(-2,6,-3,8,-1,5,-4,9,-7);
	assertEquals(m1.maxAbs(), 9., eps);

	Matrix3 m2 = new Matrix3(2,-6,3,-8,1,-5,4,-9,7);
	assertEquals(m2.maxAbs(), 9., eps);
    }

    @Test
    public void testArithmetic() {
	Matrix3 m1 = new Matrix3(2,6,3,8,1,5,4,9,7);
	Matrix3 m2 = new Matrix3(2,7,3,9,6,4,8,1,5);

	// Add another matrix
	Matrix3 m3 = m1.copy();
	m3.mAdd(m2);
	assertTrue(m3.equals(new Matrix3(4,13,6,17,7,9,12,10,12)));

	// Add a constant
	Matrix3 m4 = m1.copy();
	m4.mAdd(3);
	assertTrue(m4.equals(new Matrix3(5,9,6,11,4,8,7,12,10)));

	// Subtract another matrix
	Matrix3 m5 = m1.copy();
	m5.mSubtract(m2);
	assertTrue(m5.equals(new Matrix3(0,-1,0,-1,-5,1,-4,8,2)));

	// Subtract a constant
	Matrix3 m6 = m1.copy();
	m6.mSubtract(3);
	assertTrue(m6.equals(new Matrix3(-1,3,0,5,-2,2,1,6,4)));

	// Multiply matrix by a constant in-place
	Matrix3 m7 = m1.copy();
	m7.mMultiply(3);
	assertTrue(m7.equals(new Matrix3(6,18,9,24,3,15,12,27,21)));

	// Multiply matrix by another matrix; test result computed with Matlab
	Matrix3 m8 = m1.multiply(m2);
	assertTrue(m8.equals(new Matrix3(82, 53, 45,
		65, 67, 53,
		145,89, 83)));

	// Multiply matrix by a vector; test result computed with Matlab
	Vector3 v1 = m1.multiply(new Vector3(2,5,3));
	assertTrue(v1.equals(new Vector3(43,36,74)));
    }

    @Test
    public void testIsSymmetric() {
	double eps = 0;
	assertTrue((new Matrix3(2,6,3,
		6,1,5,
		3,5,7)).isSymmetric(eps));
	assertFalse((new Matrix3(2,6,3,
		0,1,5,
		3,5,7)).isSymmetric(eps));
	assertFalse((new Matrix3(2,6,3,
		6,1,5,
		0,5,7)).isSymmetric(eps));
	assertFalse((new Matrix3(2,6,3,
		6,1,5,
		3,0,7)).isSymmetric(eps));    }

    // Conversions
    @Test
    public void testEuler() {
	double a = 0.3;  // Radians
	double b = 0.4;
	double c = 0.5;

	// Conversion from EulerZYX to matrix
	Matrix3 m1 = Matrix3.fromEulerZYX(a,b,c);
	// Test result from Matlab: eul2dcm([.3;.4;.5],[3;2;1])
	Matrix3 m1r = new Matrix3(
		0.87992317628126d,  0.27219213529543d, -0.38941834230865d,
		-0.08098482943779d,  0.89355940872708d,  0.44158016313716d,
		0.46816307120921d, -0.35701964169863d,  0.80830706677435d);
	m1r.mTranspose();
	assertTrue(m1.epsilonEquals(m1r, 1E-14));

	// Conversion from EulerXYZ to matrix
	Matrix3 m2 = Matrix3.fromEulerXYZ(a,b,c);
	// Test result from Matlab: eul2dcm([.3;.4;.5],[1;2;3])
	Matrix3 m2r = new Matrix3(
		0.80830706677435d,  0.55900577999595d, -0.18480320271513d,
		-0.44158016313716d,  0.78321387846132d,  0.43770193066667d,
		0.38941834230865d, -0.27219213529543d,  0.87992317628126d);
	m2r.mTranspose();
	assertTrue(m2.epsilonEquals(m2r, 1E-14));
    }

    @Test
    public void testRot() {
	double k = Math.PI/180.0;
	double r = Math.sqrt(3) / 2;
	double h = 0.5;
	double a30 = k * 30;

	// Test result generated with Matlab: eul2dcm([-a;0;0],[1;2;3])
	Matrix3 m1 = Matrix3.xRotation(-a30);
	assertTrue(m1.epsilonEquals(new Matrix3(1,   0,   0,
		0,   r,  h,
		0,  -h,   r),
		1E-15));

	// Test result generated with Matlab: eul2dcm([0;-a;0],[1;2;3])
	Matrix3 m2 = Matrix3.yRotation(-a30);
	assertTrue(m2.epsilonEquals(new Matrix3(r  , 0,  -h,
		0,   1,   0,
		h,   0,   r),
		1E-15));

	// Test result generated with Matlab: eul2dcm([0;0;-a],[1;2;3])
	Matrix3 m3 = Matrix3.zRotation(-a30);
	assertTrue(m3.epsilonEquals(new Matrix3(r,   h,   0,
		-h,   r,   0,
		0,   0,   1),
		1E-15));
    }

    @Test
    public void testAxisAngle() {
	Vector3 axis = new Vector3(1,2,3);
	double angle = 0.6; // radians

	// Test result generated by Matlab: axa2dcm(normalise([1;2;3])', 0.6)
	Matrix3 me = new Matrix3( 0.83781164241613d,  0.47767320104108d, -0.26438601483277d,
		-0.42776909101528d,  0.87523972493548d,  0.22576321371477d,
		0.33924217987148d, -0.07605088363735d,  0.93761986246774d);
	me.mInvert();

	Matrix3 m = new AxisAngle(axis,angle).toMatrix3();
	assertTrue(m.epsilonEquals(me, 1E-14));
    }

    /**
     * Test normal case where (trace + 1) > 0.
     */
    @Test
    public void testToQuaternion() {
	double a = 0.3;  // Radians
	double b = 0.4;
	double c = 0.5;

	// Test toQuaternion method with a rotation matrix
	Matrix3 m1 = Matrix3.fromEulerXYZ(a,b,c);
	Quaternion q = m1.toQuaternion();

	// Test results generated by Matlab: eul2q([0.3;0.4;0.5],[1;2;3])
	Quaternion qe = new Quaternion(0.19050591331489d,
		0.15409707606386d,
		0.26851547024594d,
		0.93159059161159d);

	assertTrue(q.epsilonEquals(qe, 1E-14));

	// Test constructor: Matrix3(Quaternion)
	// Test results generated by Matlab: eul2dcm([0.3;0.4;0.5],[1;2;3])
	Matrix3 m2e = new Matrix3( 0.80830706677435d,  0.55900577999595d, -0.18480320271513d,
		-0.44158016313716d,  0.78321387846132d,  0.43770193066667d,
		0.38941834230865d, -0.27219213529543d,  0.87992317628126);	
	m2e.mTranspose();

	Matrix3 m2 = q.toMatrix3();
	assertTrue(m2.epsilonEquals(m2e, 1E-10));
    }

    /**
     * Test case where (trace + 1) < 0 and m00 is the largest diagonal element.
     */
    @Test
    public void testToQuaternionM00() {
	double a = Math.toRadians(90.01);
	double b = Math.toRadians(-89.99);
	double c = Math.toRadians(90.02);

	Quaternion q = new Attitude(a, b, c).toQuaternion();
	Matrix3 m = q.toMatrix3();
	m.mMultiply(1.001);
	assertTrue(m.trace() + 1 < 0);
	assertTrue(m.get(0,0) > m.get(1,1) && m.get(0,0) > m.get(2,2));
	
	Quaternion q2 = m.toQuaternion();
	assertTrue(q.normalizeSign().epsilonEquals(q2, 1E-2));
    }

    /**
     * Test case where (trace + 1) < 0 and m11 is the largest diagonal element.
     */
    @Test
    public void testToQuaternionM11() {
	double a = Math.toRadians(179.99);
	double b = Math.toRadians(0.01);
	double c = Math.toRadians(179.99);

	Quaternion q = new Attitude(a, b, c).toQuaternion();
	Matrix3 m = q.toMatrix3();
	m.mMultiply(1.01);
	assertTrue(m.trace() + 1 < 0);
	assertTrue(m.get(1,1) > m.get(0,0) && m.get(1,1) > m.get(2,2));
	
	Quaternion q2 = m.toQuaternion().normalize();
	assertTrue(q.normalizeSign().epsilonEquals(q2.normalizeSign(), 1E-2));
    }

    /**
     * Test case where (trace + 1) < 0 and m22 is the largest diagonal element.
     */
    @Test
    public void testToQuaternionM22() {
	double a = Math.toRadians(90.02);
	double b = Math.toRadians(-89.99);
	double c = Math.toRadians(90.01);

	Quaternion q = new Attitude(a, b, c).toQuaternion();
	Matrix3 m = q.toMatrix3();
	m.mMultiply(1.01);
	assertTrue(m.trace() + 1 < 0);
	assertTrue(m.get(2,2) > m.get(0,0) && m.get(2,2) > m.get(1,1));
	
	Quaternion q2 = m.toQuaternion();
	assertTrue(q.normalizeSign().epsilonEquals(q2.normalizeSign(), 1E-2));
    }

    @Test
    public void testMgs() {
	Matrix3 a = new Matrix3(1,2,3,4,5,6,3,5,9);
	Matrix3[] qr = a.mgs();

	// Expected results from Matlab: mgs([1,2,3;4,5,6;3,5,9])
	Matrix3 qe = new Matrix3(0.19611613513818d,  0.49724515809885d, -0.84515425472852d,
		0.78446454055274d, -0.59669418971862d, -0.16903085094570d,
		0.58834840541455d,  0.62984386692521d,  0.50709255283711d);

	Matrix3 re = new Matrix3(0.56655772373253d,  0.80625522223476d,  1.17669681082910d,
		0,                  0.12891541135896d,  0.39779612647908d,
		0,                  0,                  0.11268723396380d);

	assertTrue(qr[0].epsilonEquals(qe, 1E-14));
	assertTrue(qr[1].epsilonEquals(re.mMultiply(9), 1E-13));
    }

    @Test
    // FIXME: This test could be improved
    public void testIsSymmetricPositiveSemiDefinite() {
	// Symmetric positive definite
	Matrix3 m = new Matrix3(2, -1, 0,  -1, 2, -1,  0, -1, 2);
	assertTrue(m.isSPSD(1E-15));
	
	// vT.M.v = 0 (approximately) for some v
	Matrix3 m2 = Matrix3.zRotation(-Math.PI / 2);
	assertFalse(m2.isSPSD(0));
	
	// Not positive semi-definite
	Matrix3 m3 = new Matrix3(1,0,0, 0,-1,0, 0,0,0);
	assertFalse(m3.isSPSD(1E-10));
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Matrix3 m = new Matrix3(1,2,3,4,5,6,7,8,9);
	String s = m.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}







