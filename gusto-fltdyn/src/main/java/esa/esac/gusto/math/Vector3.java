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
 * A 3-vector.<p>
 * 
 * Like several other classes in this package, mutating versions of some of
 * the more common methods are provided. These return the mutated object,
 * allowing operations to be concatenated as expressions, without the need
 * to create new vectors as temporary variables. For example:<p>
 * 
 * <tt>Vector3 v1 = v2.copy().mAdd(v3).mAdd(v4).mAdd(v4);</tt>
 * <p>
 * 
 * The <tt>copy()</tt> operation can be omitted by simply using the non-mutating
 * <tt>add()</tt> method for the first call as follows:<p>
 * 
 * <tt>Vector3 v1 = v2.add(v3).mAdd(v4).mAdd(v5);</tt>
 *
 * @author  Jon Brumfitt
 */
public final class Vector3 implements Cloneable {

    private double _x;
    private double _y;
    private double _z;

    /**
     * Create a new zero vector.
     */
    public Vector3() {
    }

    /**
     * Create a new vector [x,y,z].
     *
     * @param x The X component
     * @param y The Y component
     * @param z The Z component
     */
    public Vector3(double x, double y, double z) {
        _x = x;
	_y = y;
	_z = z;
    }

    /**
     * Create a new Vector3 from a double[] array.
     *
     * @param xyz Array containing [X,Y,Z]
     */
    public Vector3(double[] xyz) {
	if(xyz.length != 3) {
	    throw new IllegalArgumentException("3 elements required.");
	}
	_x = xyz[0];
	_y = xyz[1];
	_z = xyz[2];
    }

    /**
     * Create a new Vector3 which is a copy of a given vector.
     *
     * @param v The vector to be copied
     */
    public Vector3(Vector3 v) {
	_x = v._x;
	_y = v._y;
	_z = v._z;
    }

    /**
     * Create a vector from a Direction (RA and DEC).
     *
     * @param dir The Direction
     */ 
    public Vector3(Direction dir) {
	double alpha = dir.getAlpha();
	double delta = dir.getDelta();

	double cosDelta = Math.cos(delta);
	_x = Math.cos(alpha) * cosDelta;
	_y = Math.sin(alpha) * cosDelta;
	_z = Math.sin(delta);
    }

    /**
     * Create a unit X vector [1,0,0].
     *
     * @return a unit X vector
     */
    public static Vector3 unitX() {
	return new Vector3(1,0,0);
    }

    /**
     * Create a unit Y vector [0,1,0].
     *
     * @return a unit Y vector
     */
    public static Vector3 unitY() {
	return new Vector3(0,1,0);
    }

    /**
     * Create a unit Z vector [0,0,1].
     *
     * @return a unit Z vector
     */
    public static Vector3 unitZ() {
	return new Vector3(0,0,1);
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
     * Return the Z component of this vector.
     *
     * @return the Z component
     */
    public double getZ() {
	return _z;
    }

    /**
     * Return element 'i' of this vector.
     *
     * @param i Index
     * @return Element 'i' of the vector
     */
    public double get(int i) {
	if(i<0 || i>2) {
	    throw new IllegalArgumentException("Index out of range");
	}
	return (i==0) ? _x : (i==1) ? _y : _z;
    }

    /**
     * Return this vector as a double[] array.
     *
     * @return Vector [X,Y,Z]
     */
    public double[] toArray() {
	return new double[]{_x, _y, _z};
    }

    /**
     * Return a copy of this array.
     *
     * @return A copy of this array
     */
    public Vector3 copy() {
	return new Vector3(_x, _y, _z);
    }

    /**
     * Return the sum of this vector and another vector, as a new vector.
     *
     * @param v The other vector
     * @return The sum of the two vectors
     */
    public Vector3 add(Vector3 v) {
	return new Vector3(_x + v._x, _y + v._y, _z + v._z);
    }

    /**
     * Add another vector in-place.
     *
     * @param v The other vector
     * @return This vector after adding the other vector
     */
     public Vector3 mAdd(Vector3 v) {
	_x += v._x;
	_y += v._y;
	_z += v._z;
	return this;
     }

    /**
     * Return the result of subtracting another vector from this vector, 
     * as a new vector.
     *
     * @param v The other vector
     * @return The difference between the two vectors
     */
    public Vector3 subtract(Vector3 v) {
	return new Vector3(_x - v._x, _y - v._y, _z - v._z);
    }

    /**
     * Subtract another vector in-place.
     *
     * @param v The other vector
     * @return This vector after subtracting the other vector
     */
     public Vector3 mSubtract(Vector3 v) {
	_x -= v._x;
	_y -= v._y;
	_z -= v._z;
	return this;
    }

    /**
     * Return the result of multiplying this vector by a scalar, as a new vector.
     *
     * @param k The scalar
     * @return The product
     */
    public Vector3 multiply(double k) {
	return new Vector3(_x * k, _y * k, _z * k);
    }

    /**
     * Multiply by a scalar in-place.
     *
     * @param k The scalar
     * @return This vector after multiplying by the scalar
     */
    public Vector3 mMultiply(double k) {
	_x *= k;
	_y *= k;
	_z *= k;
	return this;
    }
		
    /**
     * Return the dot product of this vector with another vector.
     *
     * @param v The other vector
     * @return The dot product of the two vectors
     */
    public double dot(Vector3 v) {
	return _x * v._x + _y * v._y + _z * v._z;
    }

    /**
     * Return the cross product of this vector by another vector, as
     * a new vector.
     *
     * @param v The other vector
     * @return this X v
     */
    public Vector3 cross(Vector3 v) {
	return new Vector3(_y * v._z - _z * v._y,
			   _z * v._x - _x * v._z,
			   _x * v._y - _y * v._x);
    }

    /**
     * Negate each element of the vector, returning a new vector.
     *
     * @return The negated vector
     */
    public Vector3 negate() {
	return new Vector3(-_x, -_y, -_z);
    }

    /**
     * Negate this vector in-place.
     *
     * @return This vector after negation
     */
    public Vector3 mNegate() {
	_x = -_x;
	_y = -_y;
	_z = -_z;

	return this;
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
	return _x*_x + _y*_y + _z*_z;
    }

    /**
     * Return the dimension (which is always 3).
     *
     * @return the dimension of this vector
     */
    public int dimension() {
	return 3;
    }

    /**
     * Normalize to unit length, returning a new vector.
     *
     * @return The normalized vector
     * @throws RuntimeException if a zero vector.
     */
    public Vector3 normalize() throws RuntimeException {
	return copy().mNormalize();
    }

    /**
     * Normalize to unit length, in place.<p>
     * 
     * The normalization is skipped if the vector is already normalized.
     *
     * @return This vector after normalizatiion
     * @throws RuntimeException if a zero vector.
     */
    public Vector3 mNormalize() throws RuntimeException {
	double n2 = normSquared();
	if(n2 == 0) {
	    throw new RuntimeException("Cannot normalize zero vector");
	}
	// Do nothing if it is already normalized
	if(Math.abs(n2 - 1) > 5E-16) {  // ULP = 2.22E-16
	    double norm = Math.sqrt(n2);
	    _x /= norm;
	    _y /= norm;
	    _z /= norm;
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
    public double angle(Vector3 v) throws RuntimeException {
	// FIXME: Use a more specific exception type (e.g. ZeroLengthException)
	
	double lsq = norm() * v.norm();
	double a = dot(v) / lsq;

	// Use cross product for small angles
	if(Math.abs(a) < 0.99) { 
	    return Math.acos(a);
	    
	} else if(a > 0) {
	    return Math.asin(cross(v).norm() / lsq);
	} else {
	    return Math.PI - Math.asin(cross(v).norm() / lsq);
	}
    }

    /**
     * Create a right-handed set of three orthogonal vectors from 
     * two linearly independent vectors.<p>
     *
     * The three orthogonal unit vectors {x,y,z} are returned as
     * the columns of a matrix.<p>
     *
     * If 'a' and 'b' are this vector and the argument vector, respectively:
     *
     * <ul>
     * <li> x is parallel to a </li>
     * <li> y is orthogonal to x and in the plane of a and b </li>
     * <li> z is orthogonal to x & y and forms a right-hand triad </li>
     * </ul>
     *
     * @param b The other vector b
     * @return The resulting matrix
     */
    public Matrix3 triad(Vector3 b) {
	Vector3 p = normalize();
	Vector3 u = p.multiply(p.dot(b));
	Vector3 v = b.subtract(u);

	if(v.norm() == 0) {
	    throw new IllegalArgumentException("Vectors are not linearly independent");
	}
	v = v.normalize();

	return Matrix3.fromColumns(p, v,  p.cross(v));
    }

//     // Alternative implementation
//     public Matrix3 triad(Vector3 b) {
//         Vector3 x = normalize();
//         Vector3 z = cross(b.normalize().mSubtract(a)).mNormalize();
//         Vector3 y = z.cross(x).mNormalize();
//         return Matrix3.fromColumns(x, y, z);
//     }

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
        if(!(obj instanceof Vector3)) {
	    return false;
	}
	Vector3 v = (Vector3)obj;
	return (Double.doubleToLongBits(_x) ==
		Double.doubleToLongBits(v._x))
	    && (Double.doubleToLongBits(_y) ==
		Double.doubleToLongBits(v._y))
	    && (Double.doubleToLongBits(_z) ==
		Double.doubleToLongBits(v._z));
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
	f = Double.doubleToLongBits(_z);
	result = 37*result + (int)(f^(f>>>32));

	return result;
    }

    /**
     * Returns true if this vector is approximately equal to another.<p>
     *
     * The criterion is that the L-infinte distance between the two vectors
     * u and v is less than or equal to epsilon.<p>
     * i.e. MAX(abs(u1-v1),abs(u2,v2),abs(u3-v3)) <= epsilon.
     *
     * @param v The other vector
     * @param epsilon The maximum difference
     * @return true if the vectors are approximately equal
     */
    public boolean epsilonEquals(Vector3 v, double epsilon) {
	double dx = Math.abs(_x - v._x);
	double dy = Math.abs(_y - v._y);
	double dz = Math.abs(_z - v._z);

	return Math.max(dx,Math.max(dy,dz)) <= epsilon;
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
	return "[" + _x + ", " + _y + ", " + _z + "]";
    }
}

