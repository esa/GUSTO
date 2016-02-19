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
 * A triple of double numbers.
 *
 * @author  Jon Brumfitt
 */

public class Triple {

    private double _x1;
    private double _x2;
    private double _x3;

    /**
     * Create a new Triple with value (0,0,0);
     */
    public Triple() {
	_x1 = 0;
	_x2 = 0;
	_x3 = 0;
    }

    /**
     * Create a new Triple with specified values.
     *
     * @param first First value
     * @param second Second value
     */
    public Triple(double first, double second, double third) {
	_x1 = first;
	_x2 = second;
	_x3 = third;
    }

    /**
     * Return the first element.
     *
     * @return The first value
     */
    public double first() {
	return _x1;
    }

    /**
     * Return the second element.
     *
     * @return The second value
     */
    public double second() {
	return _x2;
    }

    /**
     * Return the third element.
     *
     * @return The third value
     */
    public double third() {
	return _x3;
    }

    /**
     * Return a String representation of this object.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this triple
     */
    public String toString() {
	return "(" + _x1 + ", " + _x2 + ", " + _x3 + ")";
    }
}

