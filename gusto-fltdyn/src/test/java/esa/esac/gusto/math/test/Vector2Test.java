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
import esa.esac.gusto.math.Vector2;

import org.junit.Test;

/**
 * Test harness for Vector2 class.
 *
 * @author  Jon Brumfitt
 */
public class Vector2Test {

    /**
     * Test equality. The other tests rely on this.
     */
    @Test
    public void testEquality() {
	double eps = 1E-12;
	Vector2 v0 = new Vector2(7,8);
	Vector2 v1 = new Vector2(7,8);
	Vector2 v2 = new Vector2(7+eps,8);

	assertEquals(v0.hashCode(), v1.hashCode());

	assertTrue(v0.equals(v1));
	assertFalse(v1.equals(v2));

	assertTrue(v1.epsilonEquals(v2,eps*1.0001));
	assertFalse(v1.epsilonEquals(v2,eps));
    }

    @Test
    public void testClone() {
	Vector2 v1 = new Vector2(7,8);
	Vector2 v2 = (Vector2)v1.clone();
	assertTrue(v1.equals(v2));
    } 

    @Test
    public void testCopy() {
	Vector2 v1 = new Vector2(7,8);
	Vector2 v2 = v1.copy();
	assertTrue(v1.equals(v2));
    }

    @Test
    public void testConstruction1() {
	Vector2 v0 = new Vector2(0,0);
	Vector2 v1 = new Vector2();
	assertTrue(v0.equals(v1));

	Vector2 v2 = new Vector2(7,8);
	Vector2 v3 = new Vector2(v2);
	assertTrue(v2.equals(v3));

	Vector2 v4 = new Vector2(new double[]{7,8});
	assertTrue(v2.equals(v4));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstruction2() {
	@SuppressWarnings("unused")  // "v" is not used
	Vector2 v = new Vector2(new double[]{7});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstruction3() {	

	@SuppressWarnings("unused")  // "v" is not used
	Vector2 v = new Vector2(new double[]{6,7,8});
    }

    @Test
    public void testUnitVectors() {
	assertTrue(Vector2.unitX().equals(new Vector2(1,0)));
	assertTrue(Vector2.unitY().equals(new Vector2(0,1)));
    }

    @Test
    public void testGet1() {
	double eps = 1E-14;
	Vector2 v1 = new Vector2(7,8);
	assertEquals(v1.getX(), 7., eps);
	assertEquals(v1.getY(), 8., eps);

	assertEquals(v1.get(0), 7., eps);
	assertEquals(v1.get(1), 8., eps);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet2() {
	Vector2 v1 = new Vector2(7,8);
	v1.get(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGet3() {
	Vector2 v1 = new Vector2(7,8);
	v1.get(3);
    }

    @Test
    public void testToArray() {
	double eps = 1E-14;
	Vector2 v1 = new Vector2(6,12);
	double[] a = v1.toArray();
	assertEquals(6, a[0], eps);
	assertEquals(12, a[1], eps);
    }
    
    @Test
    public void testAdd() {
	Vector2 v1 = new Vector2(6,12);
	Vector2 v2 = new Vector2(2,4);

	assertTrue(v1.add(v2).equals(new Vector2(8,16)));
	Vector2 v3 = v1.mAdd(v2);
	assertTrue(v1.equals(new Vector2(8,16)));
	assertTrue(v2.equals(new Vector2(2,4)));
	assertTrue(v1.equals(v3));
    }

    @Test
    public void testSubtract() {
	Vector2 v1 = new Vector2(6,12);
	Vector2 v2 = new Vector2(2,4);

	assertTrue(v1.subtract(v2).equals(new Vector2(4,8)));
	Vector2 v3 = v1.mSubtract(v2);
	assertTrue(v1.equals(new Vector2(4,8)));
	assertTrue(v2.equals(new Vector2(2,4)));
	assertTrue(v1.equals(v3));
    }
    
    @Test
    public void testMultiply() {
	Vector2 v1 = new Vector2(6,12);

	assertTrue(v1.multiply(2).equals(new Vector2(12,24)));
	Vector2 v3 = v1.mMultiply(2);
	assertTrue(v1.equals(new Vector2(12,24)));
	assertTrue(v1.equals(v3));
    }
    
    @Test
    public void testProducts() {
	double eps = 1E-14;
	Vector2 v1 = new Vector2(2,4);
	Vector2 v2 = new Vector2(3,7);
	assertEquals(v1.dot(v2), 34., eps);
    }

    @Test
    public void testNegate() {
	Vector2 v1 = new Vector2(7,8);
	Vector2 v2 = v1.negate();
	assertTrue(v1.add(v2).equals(new Vector2(0,0)));
	
	Vector2 v3 = v1.mNegate();
	assertTrue(v1.equals(new Vector2(-7,-8)));
	assertTrue(v1.equals(v3));
    }

    @Test
    public void testRotate() {
	double x = 3;
	double y = 4;
	double angle = Math.toRadians(50);
	Vector2 v = new Vector2(x, y);
	Vector2 v2 = v.rotate(angle);
	
	double a = Math.atan2(y, x);
	double xe = Math.cos(a + angle);
	double ye = Math.sin(a + angle);
	
	assertEquals(v.norm(), v2.norm(), 1E-15);
	Vector2 vn = v2.normalize();
	assertEquals(xe, vn.getX(), 1E-15);
	assertEquals(ye, vn.getY(), 1E-15);
    }
    
    @Test
    public void testNorm() {
	double eps = 1E-14;
	Vector2 v1 = new Vector2(3,-4);

	assertEquals(v1.norm(), 5.0, 1E-15);
	assertEquals(v1.normSquared(), 25.0, eps);
    }

    @Test
    public void testDimension() {
	Vector2 v1 = new Vector2(7,8);
	assertEquals(v1.dimension(), 2);
    }

    @Test
    public void testNormalize() {
	Vector2 v1 = new Vector2(7,-8);
	Vector2 v2 = v1.normalize();
	assertEquals(v2.norm(), 1.0, 1E-15);
    }

    // FIXME: Accuracy of 'angle' method could be improved
    //        for angles close to 0 and PI.
    @Test
    public void testAngle() {
	Vector2 v1 = new Vector2(2,4);
	Vector2 v2 = new Vector2(6,8);
	Vector2 v3 = new Vector2(-6,-8);

	double exp12 = Math.acos(44.0 / Math.sqrt(20 * 100)); 
	double exp13 = Math.acos(-44.0 / Math.sqrt(20 * 100)); 
	assertEquals(v1.angle(v2), exp12, 0); // Acute angle
	assertEquals(v1.angle(v3), exp13, 0); // Obtuse angle

	// Test when angle = 0
	assertEquals(v1.angle(v1), 0.0, 1E-7);

	// Test when angle = PI
	assertEquals(v1.angle(v1.negate()), Math.PI, 1E-7);
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	Vector2 d = new Vector2(30, 40);
	String s = d.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }

//    // Test conversions
//    @Test
//    public void testConversions() {
//	// The test results were generated for each of the 8 octants
//	// using the Matlab 'rade2uv' function.
//	{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(20,40));
//	    Vector2 v1e = new Vector2(0.71984631039295,
//		    0.26200263022938,
//		    0.64278760968654d);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(140,40));
//	    Vector2 v1e = new Vector2(-0.58682408883347d,
//		    0.49240387650610d,
//		    0.64278760968654d);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(220,40));
//	    Vector2 v1e = new Vector2(-0.58682408883347d,
//		    -0.49240387650610d,
//		    0.64278760968654d);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(310,40));
//	    Vector2 v1e = new Vector2(0.49240387650610d,
//		    -0.58682408883347d,
//		    0.64278760968654d);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(20,-40));
//	    Vector2 v1e = new Vector2(0.71984631039295d,
//		    0.26200263022938d,
//		    -0.64278760968654);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(140,-40));
//	    Vector2 v1e = new Vector2(-0.58682408883347d,
//		    0.49240387650610d,
//		    -0.64278760968654d);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(220,-40));
//	    Vector2 v1e = new Vector2(-0.58682408883347d,
//		    -0.49240387650610d,
//		    -0.64278760968654d);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}{
//	    Vector2 v1 = new Vector2(Direction.fromDegrees(310,-40));
//	    Vector2 v1e = new Vector2(0.49240387650610d,
//		    -0.58682408883347d,
//		    -0.64278760968654);
//	    assertTrue(v1.epsilonEquals(v1e, 1e-14));
//	}
//    }
}







