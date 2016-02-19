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

package esa.esac.gusto.math;

/**
 * Matrix utilities.
 *
 * @author  Jon Brumfitt
 */
public class MatrixUtils {

    /**
     * Test whether matrix is orthogonal.<p>
     *
     * A matrix M is orthogonal if MM' = I, where M' is the transpose of M
     * and I is the identity matrix.<p>
     *
     * If tolerance=0, then a value of 3*ULP (Unit in Last Place) is used.
     *
     * @param m The Matrix to be tested
     * @param tolerance The tolerance to be applied
     * @return true if the matrix is orthogonal
     */
    public static boolean isOrthogonal(Matrix3 m, double tolerance) {
	double eps = Constants.D_ULP;

	Matrix3 id = Matrix3.identity();
	Matrix3 mt = m.copy().mTranspose();

	double diff1 = m.multiply(mt)
	                .mSubtract(id)
	                .maxAbs();

	double diff2 = mt.multiply(m)
	                 .mSubtract(id)
	                 .maxAbs();

// 	System.out.println("" + diff1 + " " + diff2);

	double maxdiff = Math.max(diff1, diff2);
	double tol = Math.max(Math.sqrt(3) * eps, Math.abs(tolerance));

	return maxdiff < tol;
    }

    /**
     * Test whether matrix is orthogonal.<p>
     *
     * A matrix M is orthogonal if MM' = I, where M' is the transpose of M
     * and I is the identity matrix.<p>
     *
     * If tolerance=0, then a value of 3*ULP (Unit in Last Place) is used.
     *
     * @param m The Matrix to be tested
     * @param tolerance The tolerance to be applied
     * @return true if the matrix is orthogonal
     */
    public static boolean isOrthogonal2(Matrix3 m, double tolerance) {
	double eps = Constants.D_ULP;

	Matrix3 id = Matrix3.identity();
	Matrix3 mt = m.copy().mTranspose();

	double tol = Math.max(Math.sqrt(3) * eps, Math.abs(tolerance));

	boolean t1 = m.multiply(mt).epsilonEquals(id, tol);
	boolean t2 = mt.multiply(m).epsilonEquals(id, tol);

	return t1 && t2;
    }

    /**
     * Test whether matrix represents a rotation.<p>
     *
     * A matrix represents a rotation if it is orthogonal and its 
     * determinant is one.
     *
     * @param m The matrix to be tested
     * @param tolerance The tolerance to be applied
     * @return true if the matrix is a rotation
     */
    public static boolean isRotation(Matrix3 m, double tolerance) {
	double eps = Constants.D_ULP;

	if(!isOrthogonal(m, tolerance)) {
	    System.out.println("Non-orthogonal");
	    return false;
	}
	double tol = Math.max(Math.sqrt(6) * eps, Math.abs(tolerance));	
	if(Math.abs(m.determinant() - 1) < tol) {
	    return true;
	} else {
	    System.out.println("Left handed");
	    return false;
	}
    }
}

