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
import esa.esac.gusto.math.Matrix3;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

/**
 * Test harness for Vector3 class.
 *
 * @author  Jon Brumfitt
 */
public class Vector3Test {

    /**
     * Test equality. The other tests rely on this.
     */
    @Test
    public void testEquality() {
	double eps = 1E-12;
	Vector3 v0 = new Vector3(7,8,9);
	Vector3 v1 = new Vector3(7,8,9);
	Vector3 v2 = new Vector3(7+eps,8,9);

	assertEquals(v0.hashCode(), v1.hashCode());

	assertTrue(v0.equals(v1));
	assertFalse(v1.equals(v2));

	assertTrue(v1.epsilonEquals(v2,eps*1.0001));
	assertFalse(v1.epsilonEquals(v2,eps));
    }

    @Test
    public void testClone() {
	Vector3 v1 = new Vector3(7,8,9);
	Vector3 v2 = (Vector3)v1.clone();
	assertTrue(v1.equals(v2));
    } 

    @Test
    public void testCopy() {
	Vector3 v1 = new Vector3(7,8,9);
	Vector3 v2 = v1.copy();
	assertTrue(v1.equals(v2));
    }

    @Test
    public void testConstruction1() {
	Vector3 v0 = new Vector3(0,0,0);
	Vector3 v1 = new Vector3();
	assertTrue(v0.equals(v1));

	Vector3 v2 = new Vector3(7,8,9);
	Vector3 v3 = new Vector3(v2);
	assertTrue(v2.equals(v3));

	Vector3 v4 = new Vector3(new double[]{7,8,9});
	assertTrue(v2.equals(v4));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstruction2() {
	@SuppressWarnings("unused")  // "v" is not used
	Vector3 v = new Vector3(new double[]{7,8});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstruction3() {	

	@SuppressWarnings("unused")  // "v" is not used
	Vector3 v = new Vector3(new double[]{6,7,8,9});
    }

    @Test
    public void testUnitVectors() {
	assertTrue(Vector3.unitX().equals(new Vector3(1,0,0)));
	assertTrue(Vector3.unitY().equals(new Vector3(0,1,0)));
	assertTrue(Vector3.unitZ().equals(new Vector3(0,0,1)));
    }

    @Test
    public void testGet1() {
	double eps = 1E-14;
	Vector3 v1 = new Vector3(7,8,9);
	assertEquals(v1.getX(), 7., eps);
	assertEquals(v1.getY(), 8., eps);
	assertEquals(v1.getZ(), 9., eps);

	assertEquals(v1.get(0), 7., eps);
	assertEquals(v1.get(1), 8., eps);
	assertEquals(v1.get(2), 9., eps);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet2() {
	Vector3 v1 = new Vector3(7,8,9);
	v1.get(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet3() {
	Vector3 v1 = new Vector3(7,8,9);
	v1.get(3);
    }

    @Test
    public void testToArray() {
	double eps = 1E-14;
	Vector3 v1 = new Vector3(6,7,8);
	double[] a = v1.toArray();
	assertEquals(6, a[0], eps);
	assertEquals(7, a[1], eps);
	assertEquals(8, a[2], eps);
    }
    
    @Test
    public void testAdd() {
	Vector3 v1 = new Vector3(6,12,10);
	Vector3 v2 = new Vector3(2,4,5);

	assertTrue(v1.add(v2).equals(new Vector3(8,16,15)));
	Vector3 v3 = v1.mAdd(v2);
	assertTrue(v1.equals(new Vector3(8,16,15)));
	assertTrue(v2.equals(new Vector3(2,4,5)));
	assertTrue(v1.equals(v3));
    }
    
    @Test
    public void testSubtract() {
	Vector3 v1 = new Vector3(6,12,10);
	Vector3 v2 = new Vector3(2,4,5);

	assertTrue(v1.subtract(v2).equals(new Vector3(4,8,5)));
	Vector3 v3 = v1.mSubtract(v2);
	assertTrue(v1.equals(new Vector3(4,8,5)));
	assertTrue(v2.equals(new Vector3(2,4,5)));
	assertTrue(v1.equals(v3));
    }
    
    @Test
    public void testMultiply() {
	Vector3 v1 = new Vector3(6,12,10);

	assertTrue(v1.multiply(2).equals(new Vector3(12,24,20)));
	Vector3 v3 = v1.mMultiply(2);
	assertTrue(v1.equals(new Vector3(12,24,20)));
	assertTrue(v1.equals(v3));
    }

    @Test
    public void testProducts() {
	double eps = 1E-14;
	Vector3 v1 = new Vector3(2,4,5);
	Vector3 v2 = new Vector3(3,7,6);
	assertEquals(v1.dot(v2), 64., eps);
	assertTrue(v1.cross(v2).equals(new Vector3(-11,3,2)));
    }

    @Test
    public void testNegate() {
	Vector3 v1 = new Vector3(7,8,9);
	Vector3 v2 = v1.negate();
	assertTrue(v1.add(v2).equals(new Vector3(0,0,0)));
	
	Vector3 v3 = v1.mNegate();
	assertTrue(v1.equals(new Vector3(-7,-8,-9)));
	assertTrue(v1.equals(v3));
    }

    @Test
    public void testNorm() {
	double eps = 1E-14;
	Vector3 v1 = new Vector3(3,-4,5);

	assertEquals(v1.norm(), Math.sqrt(50.0), 1E-15);
	assertEquals(v1.normSquared(), 50.0, eps);
    }

    @Test
    public void testDimension() {
	Vector3 v1 = new Vector3(7,8,9);
	assertEquals(v1.dimension(), 3);
    }

    @Test
    public void testNormalize() {
	Vector3 v1 = new Vector3(7,-8,9);
	Vector3 v2 = v1.normalize();

	assertEquals(v2.norm(), 1.0, 1E-15);
	assertEquals(v1.cross(v2).norm(), 0.0, 1E-15);
    }

    // FIXME: Accuracy of 'angle' method could be improved
    //        for angles close to 0 and PI.
    @Test
    public void testAngle() {
	Vector3 v1 = new Vector3(2,4,5);
	Vector3 v2 = new Vector3(6,12,10);
	Vector3 v3 = new Vector3(-6,-12,10);

	// Test results generated using Matlab
	assertEquals(v1.angle(v2), 0.2005463578885062d, 0); // Acute angle
	assertEquals(v1.angle(v3), 1.6600016703424385d, 0); // Obtuse angle

	// Test when angle = 0
	assertEquals(v1.angle(v1), 0.0, 1E-7);

	// Test when angle = PI
	assertEquals(v1.angle(v1.negate()), Math.PI, 1E-7);
    }

    // Test conversions
    @Test
    public void testConversions() {
	// The test results were generated for each of the 8 octants
	// using the Matlab 'rade2uv' function.
	{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(20,40));
	    Vector3 v1e = new Vector3(0.71984631039295,
		    0.26200263022938,
		    0.64278760968654d);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(140,40));
	    Vector3 v1e = new Vector3(-0.58682408883347d,
		    0.49240387650610d,
		    0.64278760968654d);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(220,40));
	    Vector3 v1e = new Vector3(-0.58682408883347d,
		    -0.49240387650610d,
		    0.64278760968654d);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(310,40));
	    Vector3 v1e = new Vector3(0.49240387650610d,
		    -0.58682408883347d,
		    0.64278760968654d);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(20,-40));
	    Vector3 v1e = new Vector3(0.71984631039295d,
		    0.26200263022938d,
		    -0.64278760968654);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(140,-40));
	    Vector3 v1e = new Vector3(-0.58682408883347d,
		    0.49240387650610d,
		    -0.64278760968654d);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(220,-40));
	    Vector3 v1e = new Vector3(-0.58682408883347d,
		    -0.49240387650610d,
		    -0.64278760968654d);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}{
	    Vector3 v1 = new Vector3(Direction.fromDegrees(310,-40));
	    Vector3 v1e = new Vector3(0.49240387650610d,
		    -0.58682408883347d,
		    -0.64278760968654);
	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
	}
    }
    
    // Test triad
    @Test
    public void testTriad() {
	double eps = 1E-15;
	Vector3 v1 = new Vector3(2,0,0);
	Vector3 v2 = new Vector3(3,4,0);
	Matrix3 m = v1.triad(v2);
	assertTrue(new Vector3(m.getColumn(0)).epsilonEquals(Vector3.unitX(), eps));
	assertTrue(new Vector3(m.getColumn(1)).epsilonEquals(Vector3.unitY(), eps));
	assertTrue(new Vector3(m.getColumn(2)).epsilonEquals(Vector3.unitZ(), eps));
	
	Vector3 v3 = new Vector3(4,5,6);
	Vector3 v4 = new Vector3(7,3,5);
	Matrix3 m2 = v3.triad(v4);
	Vector3 va = m2.getColumn(0);
	Vector3 vb = m2.getColumn(1);
	Vector3 vc = m2.getColumn(2);
	assertTrue(va.cross(vb).epsilonEquals(vc, eps));
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Vector3 d = new Vector3(30, 40, 50);
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
}







