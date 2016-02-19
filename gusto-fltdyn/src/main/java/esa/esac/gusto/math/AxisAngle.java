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
 * A rotation represented by a rotation axis and a rotation angle.
 *
 * @author  Jon Brumfitt
 */
public final class AxisAngle implements Cloneable {

    private static final double TWO_PI = 2 * Math.PI;

    private double _x, _y, _z;
    private double _angle;

    /**
     * Create a new AxisAngle.
     *
     * @param axis Axis vector
     * @param angle Angle in radians
     */
    public AxisAngle(Vector3 axis, double angle) {
	_x = axis.getX();
	_y = axis.getY();
	_z = axis.getZ();
	_angle = angle;
    }

    /**
     * Return this rotation as a active rotation matrix.
     *
     * @return Rotation matrix
     */
    public Matrix3 toMatrix3() {
	Matrix3 m = new Matrix3();
	m.set(this);
	return m;
    }

    /**
     * Return the axis vector.
     *
     * @return Axis vector
     */
    public Vector3 axis() {
	return new Vector3(_x, _y, _z);
    }

    /**
     * Return the angle in radians.
     *
     * @return Rotation angle in radians
     */
    public double angle() {
	return _angle;
    }

    /**
     * Returns true if this AxisAngle is approximately equal to another.<p>
     *
     * The criterion is that the L-infinte distance between them is less
     * than or equal to epsilon. The angular difference is computed modulo
     * 2*PI.
     *
     * @param aa Another AxisAngle
     * @param epsilon The maximum distance
     * @return True if axis and angle are approximately equal
     */
    public boolean epsilonEquals(AxisAngle aa, double epsilon) {
	double dx = Math.abs(_x - aa._x);
	double dy = Math.abs(_y - aa._y);
	double dz = Math.abs(_z - aa._z);
	double da = Math.abs(_angle - aa._angle);
	if(da > Math.PI) {
	    da = TWO_PI - da;
	}
	return Math.max(da,Math.max(dx,Math.max(dy,dz))) <= epsilon;
    }

    /**
     * Return a clone of this object.
     *
     * @return Clone of this object.
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch(CloneNotSupportedException e) {
	    throw new Error("Assertion failed");
	}
    }

   /**
     * Return a string representation of this AxisAngle.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this object
     */
    public String toString() {
	return "[" + _x + ", " + _y + ", " + _z + "], " + _angle;
    }
}

