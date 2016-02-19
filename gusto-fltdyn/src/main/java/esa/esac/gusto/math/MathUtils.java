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
 * Maths utilities.
 *
 * @author  Jon Brumfitt
 */
public class MathUtils {
    
    /**
     * Return the highest common factor of 'm' and 'n'.
     * 
     * @param m A number
     * @param n Another number
     * @return The HCF of m and n
     */
    public static int hcf(int m, int n) {
	while((m != 0) && (n != 0)) {
	    int t = n;
	    n = m % n;
	    m = t;
	}
	return (m == 0) ? n : m;
    }

    /**
     * Return haversine of x.
     *
     * @param x Angle in radians
     * @return The havsin of x
     */
    public static double havsin(double x) {
	double t = Math.sin(x * 0.5);
	return t * t;
    }

    /**
     * Return inverse haversine of x.
     *
     * @param x Value in range [0,1]
     * @return Inverse havsin of x
     */
    public static double ahavsin(double x) {
	return 2.0 * Math.asin(Math.sqrt(x));
    }
}







