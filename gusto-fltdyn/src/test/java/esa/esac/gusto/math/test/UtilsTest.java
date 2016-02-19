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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.math.Matrix3;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.MatrixUtils;

import org.junit.Test;

/**
 * Test harness for Utils class.
 *
 * @author  Jon Brumfitt
 */
public class UtilsTest {

    @Test
    public void testIsOrthogonal() {
	// Test with a perfect rotation (allowed)
	Quaternion q = new Quaternion(1,2,3,4).normalize();
	Matrix3 m = q.toMatrix3();
	assertTrue(MatrixUtils.isOrthogonal(m, 1E-15));
	
	// Test with reflection (allowed)
	Matrix3 m4 = m.copy();
	m4.mMultiply(-1);
	assertTrue(MatrixUtils.isOrthogonal(m4, 1E-15));
	
	// Test with a small off-diagonal shear term (not allowed)
	Matrix3 m2 = m.copy();
	m2.mAdd(new Matrix3(0,0,1E-12, 0,0,0, 0,0,0));
	assertFalse(MatrixUtils.isOrthogonal(m2, 1E-13));
	assertTrue(MatrixUtils.isOrthogonal(m2, 1E-11));
	
	// Test with scaling (not allowed)
	Matrix3 m3 = m.copy();
	m3.mMultiply(1 + 1E-10);
	assertFalse(MatrixUtils.isOrthogonal(m3, 1E-11));
	assertTrue(MatrixUtils.isOrthogonal(m3, 1E-9));
    }
    
    @Test
    public void testIsOrthogonal2() {
	// Test with a perfect rotation (allowed)
	Quaternion q = new Quaternion(1,2,3,4).normalize();
	Matrix3 m = q.toMatrix3();
	assertTrue(MatrixUtils.isOrthogonal2(m, 1E-15));
	
	// Test with reflection (allowed)
	Matrix3 m4 = m.copy();
	m4.mMultiply(-1);
	assertTrue(MatrixUtils.isOrthogonal2(m4, 1E-15));
	
	// Test with a small off-diagonal shear term (not allowed)
	Matrix3 m2 = m.copy();
	m2.mAdd(new Matrix3(0,0,1E-12, 0,0,0, 0,0,0));
	assertFalse(MatrixUtils.isOrthogonal2(m2, 1E-13));
	assertTrue(MatrixUtils.isOrthogonal2(m2, 1E-11));
	
	// Test with scaling (not allowed)
	Matrix3 m3 = m.copy();
	m3.mMultiply(1 + 1E-10);
	assertFalse(MatrixUtils.isOrthogonal2(m3, 1E-11));
	assertTrue(MatrixUtils.isOrthogonal2(m3, 1E-9));
    }
    
    @Test
    public void testIsRotation() {
	Quaternion q = new Quaternion(1,2,3,4).normalize();
	Matrix3 m = q.toMatrix3();
	assertTrue(MatrixUtils.isRotation(m, 1E-15));
	
	// FIXME: This test should fail!   *****
//	Matrix3 m2 = m.copy().mMultiply(1.01);
//	System.out.println(m2.determinant());
//	assertFalse(Utils.isRotation(m, 1E-15));
    }
}

