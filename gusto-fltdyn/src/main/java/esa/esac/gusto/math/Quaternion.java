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
 * A quaternion w + ix + jy + kz.<p>
 * 
 * Quaternions provide an efficient way to represent and manipulate rotations
 * including spacecraft attitudes. They do not suffer from the singularity
 * problems of Euler angles (e.g. the Attitude class).<p>
 * 
 * Like several other classes in this package, mutating versions of some of
 * the more common methods are provided. These return the mutated object,
 * allowing operations to be concatenated as expressions, without the need
 * to create new Quaternions as temporary variables. For example:<p>
 * 
 * <tt>Quaternion q1 = q2.copy().mMultiply(q3).mMultiply(q4).mMultiply(q4);</tt>
 * <p>
 * 
 * The <tt>copy()</tt> operation can be omitted by simply using the non-mutating
 * <tt>multiply()</tt> method for the first call as follows:<p>
 * 
 * <tt>Quaternion q1 = q2.multiply(q3).mMultiply(q4).mMultiply(q5);</tt>
 *
 * @author  Jon Brumfitt
 */
public class Quaternion implements Cloneable {
    
    private double _x;
    private double _y;
    private double _z;
    private double _w;

    /**
     * Create a new Quaternion (0,0,0,1) representing a zero rotation.
     */
    public Quaternion() {
	_x = 0;
	_y = 0;
	_z = 0;
	_w = 1;
    }

    /**
     * Create a new Quaternion w + ix + jy + kz.<p>
     *
     * Note that some math libraries specify quaternion arguments in the order
     * XYZW while others use WXYZ. This library uses XYZW, which is consistent
     * with Java3D's Quat4d class and the conventions used in the Herschel/Planck
     * ACMS.
     *
     * @param x The X component
     * @param y The Y component
     * @param z The Z component
     * @param w The W (scalar) component
     */
    public Quaternion(double x, double y, double z, double w) {
	_x = x;
	_y = y;
	_z = z;
	_w = w;
    }

    /**
     * Create a copy of another Quaternion.
     *
     * @param q The quaternion to be copied
     */
    public Quaternion(Quaternion q) {
	set(q);
    }

    /**
     * Create a unit Quaternion corresponding to a rotation of 'angle' about 'axis'.<p>
     *
     * The axis vector need not be normalized.
     *
     * @param aa An AxisAngle
     */
    public Quaternion(AxisAngle aa) {
	this(aa.axis(), aa.angle());
    }

    /**
     * Create a unit Quaternion corresponding to a rotation of 'angle' about 'axis'.<p>
     *
     * The axis vector need not be normalized.
     *
     * @param axis The rotation angle
     * @param angle The rotation angle in radians
     */
    public Quaternion(Vector3 axis, double angle) {
	// Normalizing the vector ensures the quaternion is normalized.
	Vector3 v = axis.normalize();

	double sinA = Math.sin(angle / 2);
	double cosA = Math.cos(angle / 2);

	_x = v.getX() * sinA;
	_y = v.getY() * sinA;
	_z = v.getZ() * sinA;
	_w = cosA;
    }
    
    /**
     * Create the Quaternion that gives the shortest rotation from v1 to v2.
     * 
     * @param v1 Initial vector
     * @param v2 Final vector
     * @return Quaternion that rotates v1 to v2
     * @throws RuntimeException if v1 and v2 are collinear
     */
    public static Quaternion rotation(Vector3 v1, Vector3 v2) {
	return new Quaternion(v1.cross(v2), v1.angle(v2));
    }
    
    /**
     * Create an active rotation about the X axis.
     *
     * @param angle Rotation angle in radians
     * @return Quaternion representing the rotation
     */
    public static Quaternion xRotation(double angle) {
	return new Quaternion(Math.sin(angle / 2),
			      0,
			      0,
			      Math.cos(angle / 2));
    }

    /**
     * Create an active rotation about the Y axis.
     *
     * @param angle Rotation angle in radians
     * @return Quaternion representing the rotation
     */
    public static Quaternion yRotation(double angle) {
	return new Quaternion(0,
			      Math.sin(angle / 2),
			      0,
			      Math.cos(angle / 2));
    }
 
    /**
     * Create an active rotation about the Z axis.
     *
     * @param angle Rotation angle in radians
     * @return Quaternion representing the rotation
     */
    public static Quaternion zRotation(double angle) {
	return new Quaternion(0,
			      0,
			      Math.sin(angle / 2),
			      Math.cos(angle / 2));
    }

    /**
     * Set this Quaternion equal to another one.
     *
     * @param q The quaternion
     */
    public void set(Quaternion q) {
	_x = q._x;
	_y = q._y;
	_z = q._z;
	_w = q._w;
    }

    /**
     * Return the rotation matrix that corresponds to this Quaternion.<p>
     *
     * If the quaternion represents an active rotation, the matrix will
     * also be an active rotation.
     *
     * @return A rotation matrix equivalent to this quaternion
     */
    public Matrix3 toMatrix3() {
	Matrix3 m = new Matrix3();
	m.set(this);
	return m;
    }

    /**
     * Convert this Quaternion to an equivalent Attitude.<p>
     *
     * The quaternion does not have to be normalized.
     *
     * @return an Attitude equivalent to this Quaternion
     */
    public Attitude toAttitude() {
	double xx = _x * _x;
	double yy = _y * _y;
	double zz = _z * _z;
	double ww = _w * _w;
	double d  = _x * _z - _y * _w;

	double uu = xx + yy + zz + ww;

	double xa = 0;
	double ya = 0;
	double za = 0;

	/*
	 * Set the threshold for special handling close to poles.
	 * If this is set too low, the accuracy of RA and POS suffers.
	 * If it is set too high, the accuracy of DEC suffers (as it is rounded).
	 * 1E-13 degrees is a good trade-off.
	 */
	final double threshold = 1E-13;  // DEC=89.999974 degrees (0.09 arcsec)

	double limit = 0.5 * (1 - threshold);
	if(Math.abs(d) > limit * uu) { // Close to a pole
	    za = -Math.atan2(2 * (_x * _y - _z * _w), yy + ww - xx - zz);
	    if(d >= 0) {
		ya = Math.PI / 2;
	    } else {
		ya = -Math.PI / 2;
	    }

	} else {
	    xa = -Math.atan2(2 * (_y * _z + _x * _w), zz + ww - xx - yy);
	    ya = Math.asin(2 * d / uu);
	    za = Math.atan2(2 * (_x * _y + _z * _w), xx + ww - yy - zz);
	}

	return new Attitude(za, ya, xa);
    }

    /**
     * Return the X component of the quaternion.
     *
     * @return The X component
     */
    public double getX() {
	return _x;
    }

    /**
     * Return the Y component of the quaternion.
     *
     * @return The Y component
     */
    public double getY() {
	return _y;
    }

    /**
     * Return the Z component of the quaternion.
     *
     * @return The Z component
     */
    public double getZ() {
	return _z;
    }

    /**
     * Return the W (scalar) component of the quaternion.
     *
     * @return The W component
     */
    public double getW() {
	return _w;
    }

    /**
     * Normalize this quaternion.<p>
     * 
     * The normalization is skipped if the Quaternion is already normalized.
     *
     * @return This quaternion after normalization
     */
    public Quaternion mNormalize() throws RuntimeException {
	double n2 = normSquared();
	if(n2 == 0) {
	    throw new RuntimeException("Cannot normalize zero quaternion");
	}
	// Do nothing if it is already normalized
	if(Math.abs(n2 - 1) > 5E-16) {  // ULP = 2.22E-16
	    double norm = Math.sqrt(n2);
	    _w /= norm;
	    _x /= norm;
	    _y /= norm;
	    _z /= norm;
	}
	return this;
    }

    /**
     * Normalize this quaternion, returning a new object.
     *
     * @return A new normalized quaternion
     */
    public Quaternion normalize() throws RuntimeException {
	return copy().mNormalize();
    }

    /**
     * Normalize the signs to ensure scalar component is non-negative.<p>
     *
     * Negating all four components of a quaternion, effectively adds
     * PI to the rotation angle. This is equivalent to a rotation of
     * (2*PI - angle) in the opposite direction. However, when using 
     * quaternions to represent attitudes this is irrelevant and it
     * is convenient to normalize the quaternion so that the scalar
     * component is non-negative. 
     *
     * @return This quaternion after normalization
     */
    public Quaternion mNormalizeSign() {
	if(_w < 0) {
	    _x = -_x;
	    _y = -_y;
	    _z = -_z;
	    _w = -_w;
	}

	return this;
    }

    /**
     * Normalize the signs to ensure scalar component is non-negative,
     * returning a new object.
     *
     * @return A new quaternion with normalized signs
     * @see #mNormalizeSign
     */
    public Quaternion normalizeSign() {
	return copy().mNormalizeSign();
    }
    
    /**
     * Return the L2 norm of this quaternion.
     *
     * @return L2 norm of this quaternion
     */
    public double norm() {
	return Math.sqrt(_w*_w + _x*_x + _y*_y + _z*_z);
    }
    
    /**
     * Return the square of the L2 norm of this quaternion.
     *
     * @return Squarer of the L2 norm of this quaternion
     */
    public double normSquared() {
	return _w*_w + _x*_x + _y*_y + _z*_z;
    }

    /**
     * Multiply this quaternion by another one, in place.<p>
     *
     * This is the Grassman product, which corresponds to composition
     * of two rotations. If A and B are active rotations, A.B is
     * rotation A followed by rotation B.
     *
     * @param q The other quaternion
     * @return This quaternion after multication by other quaternion
     */
    public Quaternion mMultiply(Quaternion q) {
	double x = _w*q._x + _x*q._w + _y*q._z - _z*q._y;
	double y = _w*q._y + _y*q._w + _z*q._x - _x*q._z;
	double z = _w*q._z + _z*q._w + _x*q._y - _y*q._x;

	_w = _w*q._w - _x*q._x - _y*q._y - _z*q._z;
	_x = x;
	_y = y;
	_z = z;

	return this;
    }

    /**
     * Multiply this quaternion by another one, returning a new object.
     *
     * @param q The other quaternion
     * @return The Grassman product
     * @see #mMultiply
     */
    public Quaternion multiply(Quaternion q) {
	return copy().mMultiply(q);
    }

    /**
     * Conjugate this quaternion in place.
     *
     * @return This quaternion after conjugation
     */
    public Quaternion mConjugate() {
	_x = -_x;
	_y = -_y;
	_z = -_z;

	return this;
    }

    /**
     * Conjugate this quaternion, returning a new object.
     *
     * @return The conjugate
     */
    public Quaternion conjugate() {
	return copy().mConjugate();
    }

    /**
     * Rotate the rotation P with this Quaternion Q, returning Q.P.Qinv.<p>
     * 
     * If this quaternion Q represents the rotation of frame B with respect to 
     * frame A, then <tt>Q.rotate(P)</tt> transforms the rotation P from frame A
     * to frame B. Also, <tt>Q.conjugate().rotate(P)</tt>transforms P from frame B
     * to frame A.<p>
     * 
     * <tt>Q.rotate(P) == Q.multiply(P).multiply(P.conjugate())</tt><p>
     * 
     * @param p Quaternion to be rotated
     * @return Rotated quaternion
     * @throws RuntimeException if this quaternion is zero
     */
    public Quaternion rotate(Quaternion p) {
	double m2 = _w*_w + _x*_x + _y*_y + _z*_z;
	if(m2 == 0) {
	    throw new RuntimeException("Zero quaternion");
	}

	double vx = p.getX();
	double vy = p.getY();
	double vz = p.getZ();

	double ax = _y * vz - _z * vy;
	double ay = _z * vx - _x * vz;
	double az = _x * vy - _y * vx;

	double rx = 2 / m2 * (_w * ax + _y * az - _z * ay) + vx;
	double ry = 2 / m2 * (_w * ay + _z * ax - _x * az) + vy;
	double rz = 2 / m2 * (_w * az + _x * ay - _y * ax) + vz;

	return new Quaternion(rx, ry, rz, p.getW());
    }
    
    /**
     * Return the dot product of this quaternion with another.
     *
     * @param q The other quaternion
     * @return The dot product
     */
    public double dot(Quaternion q) {
	return _w*q._w + _x*q._x + _y*q._y + _z*q._z;
    }

    /**
     * Return the rotation axis and angle.<p>
     *
     * If the angle is zero, the axis vector is set to (1,0,0).
     *
     * @return The rotation expressed as an AxisAngle
     */
    public AxisAngle toAxisAngle() {
	double a = angle();

	if(a == 0) {
	    return new AxisAngle(new Vector3(1,0,0), 0);
	} else {
	    Vector3 v = new Vector3(_x, _y, _z);
	    return new AxisAngle(v.normalize(), a);
	}
    }

    /**
     * Return the unit vector of the rotation axis.<p>
     *
     * If the angle is zero, the axis vector is set to (1,0,0).
     *
     * @return The normalized axis vector
     */
    public Vector3 axis() {
	double a = angle();

	if(a == 0) {
	    return new Vector3(1,0,0);
	} else {
	    return new Vector3(_x, _y, _z).mNormalize();
	}
    }

    /**
     * Return the rotation angle (radians) about the axis vector.<p>
     *
     * The result is in the range 0 to 2*PI.
     * The quaternion need not be normalized.
     *
     * @return The rotation angle in radians
     */
    public double angle() {
	double r = Math.sqrt(_x*_x + _y*_y + _z*_z);
	return 2 * Math.atan2(r, _w);
    }

    /**
     * Return true if this quaternion is equal to another quaternion.<p>
     *
     * Equality is defined such that NaN=NaN and -0!=+0, which
     * is appropriate for use as a hash table key.
     *
     * @param obj The object to be compared
     * @return true if the objects are equal
     */
    public boolean equals(Object obj) {
        if(!(obj instanceof Quaternion)) {
	    return false;
	}
	Quaternion q = (Quaternion)obj;
	return (Double.doubleToLongBits(_x) ==
		Double.doubleToLongBits(q._x))
	    && (Double.doubleToLongBits(_y) ==
		Double.doubleToLongBits(q._y))
	    && (Double.doubleToLongBits(_z) ==
		Double.doubleToLongBits(q._z))
	    && (Double.doubleToLongBits(_w) ==
		Double.doubleToLongBits(q._w));
    }

    /**
     * Return the hash code of this quaternion.
     *
     * @return The hash code
     */
    public int hashCode() {
        int result = 17;
	long f = Double.doubleToLongBits(_x);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_y);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_z);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_w);
	result = 37*result + (int)(f^(f>>>32));

	return result;
    }

    /**
     * Returns true if this quaternion is approximately equal to another.<p>
     *
     * The criterion is that the L-infinte distance between the two quaternions
     * u and v is less than or equal to epsilon.
     *
     * @param q The other quaternion
     * @param epsilon The maximum difference
     * @return true if the quaternions are approximately equal
     */
    public boolean epsilonEquals(Quaternion q, double epsilon) {
	double dx = Math.abs(_x - q._x);
	double dy = Math.abs(_y - q._y);
	double dz = Math.abs(_z - q._z);
	double dw = Math.abs(_w - q._w);

	return Math.max(dx,Math.max(dy,Math.max(dz,dw))) <= epsilon;
    }

    /**
     * Return a copy of this quaternion.
     *
     * @return A copy of this quaternion
     */
    public Quaternion copy() {
	return new Quaternion(this);
    }

    /**
     * Return a clone of this object.
     *
     * @return A clone of this quaternion
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch(CloneNotSupportedException e) {
	    throw new Error("Assertion failed");
	}
    }

   /**
     * Return a string representation of the quaternion.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this object
     */
    public String toString() {
        return "[" + _x + ", " + _y + ", " + _z + ", " + _w + "]";
    }

    /**
     * Rotate a vector.<p>
     *
     * This returns <tt>Q.[0,V].Qinv</tt>, where <tt>[0,V]</tt> is a quaternion
     * with scalar part 0 and vector part V.<p>
     * 
     * If the quaternion Q represents the rotation of frame B with respect to 
     * frame A, then <tt>Q.rotateVector(v)</tt> is an active rotation of a vector
     * in frame A or a passive transformation of a vector from frame B to frame A.
     *
     * @param v The vector to be rotated
     * @return The rotated vector
     */
    public Vector3 rotateVector(Vector3 v) {
	double m2 = _w*_w + _x*_x + _y*_y + _z*_z;
	if(m2 == 0) {
	    throw new RuntimeException("Zero quaternion");
	}

	double vx = v.getX();
	double vy = v.getY();
	double vz = v.getZ();

	double ax = _y * vz - _z * vy;
	double ay = _z * vx - _x * vz;
	double az = _x * vy - _y * vx;

	double rx = 2 * (_w * ax + _y * az - _z * ay) / m2 + vx;
	double ry = 2 * (_w * ay + _z * ax - _x * az) / m2 + vy;
	double rz = 2 * (_w * az + _x * ay - _y * ax) / m2 + vz;

	return new Vector3(rx, ry, rz);
    }

    /**
     * Inverse rotation of a vector.<p>
     *
     * This returns <tt>Qinv.[0,V].Q</tt>, where [0,V] is a Quaternion 
     * with scalar part 0 and vector part V. It is equivalent to 
     * <tt>Q.inverse().rotateVector(v)</tt><p>
     * 
     * If the quaternion Q represents the rotation of frame B with respect to 
     * frame A, then <tt>Q.rotateAxes(v)</tt> is passive transformation of 
     * a vector from frame A to frame B or an active rotation of a vector in 
     * frame B.
     *
     * @param v The vector to be transformed
     * @return The transformed vector
     */
    public Vector3 rotateAxes(Vector3 v) {
	return conjugate().rotateVector(v);
    }
    
    /**
     * Return the I vector rotated by this quaternion.<p>
     * 
     * This is the X axis in the rotated frame.
     * It is equivalent to rotateVector(new Vector3(1,0,0)).
     * 
     * @return I vector rotated by this quaternion.
     */
    public Vector3 rotateI() {
	double m2 = _w*_w + _x*_x + _y*_y + _z*_z;
	if(m2 == 0) {
	    throw new RuntimeException("Zero quaternion");
	}

	double rx = -2 * (_y * _y + _z * _z) / m2 + 1;
	double ry = 2 * (_w * _z + _x * _y) / m2;
	double rz = 2 * (_x * _z - _w * _y) / m2;

	return new Vector3(rx, ry, rz);
    }

    /**
     * Return the J vector rotated by this quaternion.<p>
     * 
     * This is the Y axis in the rotated frame.
     * It is equivalent to rotateVector(new Vector3(1,0,0)).
     * 
     * @return J vector rotated by this quaternion.
     */
    public Vector3 rotateJ() {
	double m2 = _w*_w + _x*_x + _y*_y + _z*_z;
	if(m2 == 0) {
	    throw new RuntimeException("Zero quaternion");
	}

	double rx = 2 * (_y * _x - _w * _z) / m2;
	double ry = -2 * (_z * _z + _x * _x) / m2 + 1;
	double rz = 2 * (_w * _x + _y * _z) / m2;

	return new Vector3(rx, ry, rz);
    }

    /**
     * Return the K vector rotated by this quaternion.<p>
     * 
     * This is the Z axis in the rotated frame.
     * It is equivalent to rotateVector(new Vector3(0,0,1)).
     * 
     * @return K vector rotated by this quaternion.
     */
    public Vector3 rotateK() {
	double m2 = _w*_w + _x*_x + _y*_y + _z*_z;
	if(m2 == 0) {
	    throw new RuntimeException("Zero quaternion");
	}

	double rx = 2 * (_w * _y + _z * _x) / m2;
	double ry = 2 * (_z * _y - _w * _x) / m2;
	double rz = -2 * (_x * _x + _y * _y) / m2 + 1;

	return new Vector3(rx, ry, rz);
    }

    /**
     * Test whether the quaternion is normalized.<p>
     *
     * The quaternion is considered normalized if the square of its
     * norm does not differ from one by more than 2*epsilon.
     *
     * @param epsilon Allowed tolerance
     */
    public boolean isNormalized(double epsilon) {
	double sq = _w*_w + _x*_x + _y*_y + _z*_z;
	return Math.abs(sq - 1) <= epsilon * 2;
    }

    /**
     * Raise a quaternion to a scalar power, in-place.<p>
     * 
     * This returns a quaternion with unit norm and hence does
     * not raise the norm to the exponent.
     *
     * @param t The exponent
     * @return The resulting quaternion
     */
    public Quaternion mPower(double t) {
	double theta =  t * angle() / 2;

	Vector3 v = axis();
	double s = Math.sin(theta);

	_w = Math.cos(theta);
	_x = v.getX() * s;
	_y = v.getY() * s;
	_z = v.getZ() * s;

	return this;
    }

    /**
     * Return the square root of this quaternion.<p>
     * 
     * This method is faster than <tt>mPower(0.5)</tt>.
     * It returns the positive sqrt for [0,0,0,1] and throws an exception for [0,0,0,-1].
     * 
     * @throws RuntimeException if this quaternion normalized is [0,0,0,-1].
     */
    public Quaternion sqrt() {
	Quaternion q = normalize();
	q._w += 1;
	return q.normalize();
    }
    
    /**
     * Spherical Linear Interpolation (SLERP) between two quaternions.<p>
     *
     * Equivalent to <tt>slerp(q, alpha, true)</tt>
     *
     * @param q The other quaternion
     * @param alpha The interpolation factor
     * @return The interpolated quaternion
     */
    public Quaternion slerp(Quaternion q, double alpha) {
	return slerp(q, alpha, true);
    }
    
    /**
     * Spherical Linear Interpolation (SLERP) between two quaternions.<p>
     *
     * For interpolation, <tt>alpha</tt> is in the range [0,1].
     * When <tt>alpha=0</tt>, the result equals this quaternion.
     * When <tt>alpha=1</tt>, the result equals the other quaternion 
     * (possibly negated). Values outside the range [0,1] may be used 
     * for extrapolation.<p>
     *
     * If <tt>shortest==true</tt> and the angle between the quaternions, in 4-space 
     * is greater than 90 degrees, one of the quaternions is negated so that rotation 
     * is in the shortest direction.
     *
     * @param q The other quaternion
     * @param alpha The interpolation factor
     * @param shortest Return the shortest rotation
     * @return The interpolated quaternion
     */
    public Quaternion slerp(Quaternion q, double alpha, boolean shortest) {
	Quaternion qb = q.copy();

	// If angle (in 4D) > 90 degrees, invert one of the quaternions,
	// to give an acute angle, to rotate in the shortest direction.
	if(shortest && dot(qb) < 0) {
	    qb.mMultiply(-1);
	}
	return multiply(conjugate().mMultiply(qb).mPower(alpha));
    }
    
    /**
     * Linear Interpolation (LERP) between two quaternions.<p>
     * 
     * This is faster than SLERP but is non-linear and does not preserve normalization.
     * It is intended for fast graphics applications.<p>
     *
     * For interpolation, <tt>alpha</tt> is in the range [0,1].
     * When <tt>alpha=0</tt>, the result equals this quaternion.
     * When <tt>alpha=1</tt>, the result equals the other quaternion 
     * (possibly negated). Values outside the range [0,1] may be used 
     * for extrapolation.<p>
     *
     * The shortest rotation (<= 180 degrees) is always used.
     *
     * @param q The other quaternion
     * @param alpha The interpolation factor
     * @return The interpolated quaternion
     */
    public Quaternion lerp(Quaternion q, double alpha) {
	Quaternion qb = q.copy();

	// If angle (in 4D) > 90 degrees, invert one of the quaternions,
	// to give an acute angle, to rotate in the shortest direction.
	if(dot(qb) < 0) {
	    qb.mMultiply(-1);
	}
	double beta = 1 - alpha;
	double x = _x * beta + qb._x * alpha;
	double y = _y * beta + qb._y * alpha;
	double z = _z * beta + qb._z * alpha;
	double w = _w * beta + qb._w * alpha;
	
	return new Quaternion(x, y, z, w);
    }

    /**
     * Multiply this quaternion by a scalar, in place.<p>
     *
     * This method does not preserve normalization.
     *
     * @param k The scalar
     * @return This quaternion after multiplication
     */
    public Quaternion mMultiply(double k) {
	_x *= k;
	_y *= k;
	_z *= k;
	_w *= k;

	return this;
    }
    
    /**
     * Add another quaternion in-place.<p>
     * 
     * This method does not preserve normalization.
     * 
     * @param q The other quaternion
     * @return This quaternion after adding the other quaternion
     */
    public Quaternion mAdd(Quaternion q) {
	_x += q._x;
	_y += q._y;
	_z += q._z;
	_w += q._w;

	return this;
    }
    
    /**
     * Subtract another quaternion in-place.
     * 
     * This method does not preserve normalization.
     * 
     * @param q The other quaternion
     * @return This quaternion after subtracting the other quaternion
     */
    public Quaternion mSubtract(Quaternion q) {
	_x -= q._x;
	_y -= q._y;
	_z -= q._z;
	_w -= q._w;

	return this;
    }
}

