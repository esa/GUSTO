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
 * A 2-vector.<p>
 * 
 * Like several other classes in this package, mutating versions of some of
 * the more common methods are provided. These return the mutated object,
 * allowing operations to be concatenated as expressions, without the need
 * to create new vectors as temporary variables. For example:<p>
 * 
 * <tt>Vector2 v1 = v2.copy().mAdd(v3).mAdd(v4).mAdd(v4);</tt>
 * <p>
 * 
 * The <tt>copy()</tt> operation can be omitted by simply using the non-mutating
 * <tt>add()</tt> method for the first call as follows:<p>
 * 
 * <tt>Vector2 v1 = v2.add(m3).mAdd(v4).mAdd(v5);</tt>
 *
 * @author  Jon Brumfitt
 */
public final class Vector2 implements Cloneable {

    private double _x;
    private double _y;

    /**
     * Create a new zero vector.
     */
    public Vector2() {
    }

    /**
     * Create a new vector [x,y].
     *
     * @param x The X component
     * @param y The Y component
     */
    public Vector2(double x, double y) {
        _x = x;
	_y = y;
    }

    /**
     * Create a new Vector2 from a double[] array.
     *
     * @param xy Array containing [X,Y]
     */
    public Vector2(double[] xy) {
	if(xy.length != 2) {
	    throw new IllegalArgumentException("2 elements required.");
	}
	_x = xy[0];
	_y = xy[1];
    }

    /**
     * Create a new Vector2 which is a copy of a given vector.
     *
     * @param v The vector to be copied
     */
    public Vector2(Vector2 v) {
	_x = v._x;
	_y = v._y;
    }

    /**
     * Create a unit X vector [1,0].
     *
     * @return a unit X vector
     */
    public static Vector2 unitX() {
	return new Vector2(1,0);
    }

    /**
     * Create a unit Y vector [0,1].
     *
     * @return a unit Y vector
     */
    public static Vector2 unitY() {
	return new Vector2(0,1);
    }

    /**
     * Return the X component of this vector.
     *
     * @return the X component
     */
    public double getX() {
	return _x;
    }

    /**
     * Return the Y component of this vector.
     *
     * @return the Y component
     */
    public double getY() {
	return _y;
    }

    /**
     * Return element 'i' of this vector.
     *
     * @param i Index
     * @return Element 'i' of the vector
     */
    public double get(int i) {
	if(i<0 || i>1) {
	    throw new IllegalArgumentException("Index out of range");
	}
	return (i==0) ? _x : _y;
    }

    /**
     * Return this vector as a double[] array.
     *
     * @return Vector [X,Y]
     */
    public double[] toArray() {
	return new double[]{_x, _y};
    }

    /**
     * Return a copy of this array.
     *
     * @return A copy of this array
     */
    public Vector2 copy() {
	return new Vector2(_x, _y);
    }

    /**
     * Return the sum of this vector and another vector, as a new vector.
     *
     * @param v The other vector
     * @return The sum of the two vectors
     */
    public Vector2 add(Vector2 v) {
	return new Vector2(_x + v._x, _y + v._y);
    }

    /**
     * Add another vector in-place.
     *
     * @param v The other vector
     * @return This vector after adding the other vector
     */
     public Vector2 mAdd(Vector2 v) {
	_x += v._x;
	_y += v._y;
	return this;
     }

    /**
     * Return the result of subtracting another vector from this vector, 
     * as a new vector.
     *
     * @param v The other vector
     * @return The difference between the two vectors
     */
    public Vector2 subtract(Vector2 v) {
	return new Vector2(_x - v._x, _y - v._y);
    }

    /**
     * Subtract another vector in-place.
     *
     * @param v The other vector
     * @return This vector after subtracting the other vector
     */
     public Vector2 mSubtract(Vector2 v) {
	_x -= v._x;
	_y -= v._y;
	return this;
    }

    /**
     * Return the result of multiplying this vector by a scalar, as a new vector.
     *
     * @param k The scalar
     * @return The product
     */
    public Vector2 multiply(double k) {
	return new Vector2(_x * k, _y * k);
    }

    /**
     * Multiply by a scalar in-place.
     *
     * @param k The scalar
     * @return This vector after multiplying by the scalar
     */
    public Vector2 mMultiply(double k) {
	_x *= k;
	_y *= k;
	return this;
    }
		
    /**
     * Return the dot product of this vector with another vector.
     *
     * @param v The other vector
     * @return The dot product of the two vectors
     */
    public double dot(Vector2 v) {
	return _x * v._x + _y * v._y;
    }

    /**
     * Negate each element of the vector, returning a new vector.
     *
     * @return The negated vector
     */
    public Vector2 negate() {
	return new Vector2(-_x, -_y);
    }

    /**
     * Negate this vector in-place.
     *
     * @return This vector after negation
     */
    public Vector2 mNegate() {
	_x = -_x;
	_y = -_y;

	return this;
    }

    /**
     * Rotate the Vector by "r0" radians.
     *
     * @param angle Rotation angle in radians
     */
    public Vector2 rotate(double angle) {
	return new Vector2(_x * Math.cos(angle) - _y * Math.sin(angle),
			   _x * Math.sin(angle) + _y * Math.cos(angle));  
    }
    
    /**
     * Return the L2 norm of this vector.
     *
     * @return The L2 norm of this vector
     */
    public double norm() {
	return Math.sqrt(normSquared());
    }

    /**
     * Return the square of the L2 norm of this vector.
     *
     * @return The square of the L2 norm
     */
    public double normSquared() {
	return _x*_x + _y*_y;
    }

    /**
     * Return the dimension (which is always 3).
     *
     * @return the dimension of this vector
     */
    public int dimension() {
	return 2;
    }

    /**
     * Normalize to unit length, returning a new vector.
     *
     * @return The normalized vector
     * @throws RuntimeException if a zero vector.
     */
    public Vector2 normalize() throws RuntimeException {
	return copy().mNormalize();
    }

    /**
     * Normalize to unit length, in place.<p>
     * 
     * The normalization is skipped if the vector is already normalized.
     *
     * @return This vector after normalization
     * @throws RuntimeException if a zero vector.
     */
    public Vector2 mNormalize() throws RuntimeException {
	double n2 = normSquared();
	if(n2 == 0) {
	    throw new RuntimeException("Cannot normalize zero vector");
	}
	// Do nothing if it is already normalized
	if(Math.abs(n2 - 1) > 5E-16) {  // ULP = 2.22E-16
	    double norm = Math.sqrt(n2);
	    _x /= norm;
	    _y /= norm;
	}
	return this;
    }

    /**
     * Return the angle in radians [0,pi], between this vector and another vector.
     *
     * @param v The other vector
     * @return The angle in radians
     * @throws RuntimeException if either vector is zero.
     */
    public double angle(Vector2 v) throws RuntimeException {
	// FIXME: Use a more specific exception type (e.g. ZeroLengthException)
	
	double lsq = norm() * v.norm();
	double a = dot(v) / lsq;

	return Math.acos(a);
    }

    /**
     * Return true if this vector is equal to another vector.<p>
     *
     * Equality is defined such that NaN=NaN and -0!=+0, which
     * is appropriate for use as a hash table key.
     *
     * @param obj The other object
     * @return true if the objects are equal
     */
    public boolean equals(Object obj) {
        if(!(obj instanceof Vector2)) {
	    return false;
	}
	Vector2 v = (Vector2)obj;
	return (Double.doubleToLongBits(_x) ==
		Double.doubleToLongBits(v._x))
	    && (Double.doubleToLongBits(_y) ==
		Double.doubleToLongBits(v._y));
    }

    /**
     * Return the hash code of this vector.
     *
     * @return The hash code of this object
     */
    public int hashCode() {
        int result = 17;
	long f = Double.doubleToLongBits(_x);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_y);
	result = 37*result + (int)(f^(f>>>32));

	return result;
    }

    /**
     * Returns true if this vector is approximately equal to another.<p>
     *
     * The criterion is that the L-infinte distance between the two vectors
     * u and v is less than or equal to epsilon.<p>
     * i.e. MAX(abs(u1-v1),abs(u2,v2)) <= epsilon.
     *
     * @param v The other vector
     * @param epsilon The maximum difference
     * @return true if the vectors are approximately equal
     */
    public boolean epsilonEquals(Vector2 v, double epsilon) {
	double dx = Math.abs(_x - v._x);
	double dy = Math.abs(_y - v._y);

	return Math.max(dx, dy) <= epsilon;
    }

    /**
     * Return a clone of this object.
     *
     * @return A clone of this object
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch(CloneNotSupportedException e) {
	    throw new Error("Assertion failed");
	}
    }

   /**
     * Return a string representation of the vector.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this vector
     */
    public String toString() {
	return "[" + _x + ", " + _y + "]";
    }
}

