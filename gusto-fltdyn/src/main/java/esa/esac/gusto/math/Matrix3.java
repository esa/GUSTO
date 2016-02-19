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
 * A 3x3 matrix.<p>
 * 
 * This class is intended to be used as a Direction Cosine Matrix (DCM) for 
 * representing rotations and attitudes. However, it is recommended that new 
 * applications use Quaternions instead. DCMs do not suffer from the singularity
 * problems of Euler angles. However, a 3x3 matrix can represent not only
 * rotation but also shearing, scaling and reflection. The process of
 * orthonormalizing a matrix, to ensure that it is just a rotation is
 * computationally expensive and may lead to loss of accuracy.<p>
 * 
 * Like several other classes in this package, mutating versions of some of
 * the more common methods are provided. These return the mutated object,
 * allowing operations to be concatenated as expressions, without the need
 * to create new Matrices as temporary variables. For example:<p>
 * 
 * <tt>Matix3 m1 = m2.copy().mAdd(m3).mMultiply(m4).mAdd(m4);</tt>
 * <p>
 * 
 * The <tt>copy()</tt> operation can be omitted by simply using the non-mutating
 * <tt>add()</tt> method for the first call as follows:<p>
 * 
 * <tt>Matrix3 m1 = m2.add(m3).mMultiply(m4).mAdd(m5);</tt>
 *
 * @author  Jon Brumfitt
 */
public class Matrix3 implements Cloneable {

    private double _m00;
    private double _m01;
    private double _m02;
    private double _m10;
    private double _m11;
    private double _m12;
    private double _m20;
    private double _m21;
    private double _m22;

    /**
     * Create a zero matrix.
     */
    public Matrix3() {
    }

    /**
     * Create a 3x3 matrix from a vector of the elements.<p>
     *
     * @param elements [m00, m01, m02, m10, m11, m12, m20, m21, m22].
     */
    public Matrix3(double[] elements) {
	if(elements.length != 9) {
	    throw new IllegalArgumentException("9 elements expected");
	}
	_m00 = elements[0];
	_m01 = elements[1];
	_m02 = elements[2];
	_m10 = elements[3];
	_m11 = elements[4];
	_m12 = elements[5];
	_m20 = elements[6];
	_m21 = elements[7];
	_m22 = elements[8];
    }

    /**
     * Create a 3x3 matrix from a 2D array of the elements.<p>
     *
     * @param elements [[m00,m01,m02],[m10,m11,m12],[m20,m21,m22]].
     */
    public Matrix3(double[][] elements) {
	_m00 = elements[0][0];
	_m01 = elements[0][1];
	_m02 = elements[0][2];
	_m10 = elements[1][0];
	_m11 = elements[1][1];
	_m12 = elements[1][2];
	_m20 = elements[2][0];
	_m21 = elements[2][1];
	_m22 = elements[2][2];
    }

    /**
     * Create a 3x3 matrix with specified elements.<p>
     */
    public Matrix3(double m00, double m01, double m02,
		   double m10, double m11, double m12,
		   double m20, double m21, double m22) {
	_m00 = m00;
	_m01 = m01;
	_m02 = m02;
	_m10 = m10;
	_m11 = m11;
	_m12 = m12;
	_m20 = m20;
	_m21 = m21;
	_m22 = m22;
    }

    /**
     * Create a copy of another matrix.
     */
    public Matrix3(Matrix3 m) {
	set(m);
    }

    /**
     * Create an active rotation matrix from body-referenced ZYX Euler angles.<p>
     *
     * The rotations are applied in the order zr, yr, xr. The angles
     * are specified in radians.
     *
     * @param zr Rotation about Z axis (radians)
     * @param yr Rotation about Y axis (radians)
     * @param xr Rotation about X axis (radians)
     */
    private Matrix3(double zr, double yr, double xr) {
	double cx = Math.cos(xr);
	double sx = -Math.sin(xr);
	double cy = Math.cos(yr);
	double sy = -Math.sin(yr);
	double cz = Math.cos(zr);
	double sz = -Math.sin(zr);

	double cxsy = cx * sy;
	double sxsy = sx * sy;

	_m00 = cy * cz;
	_m01 = sxsy * cz + cx * sz;
	_m02 = -cxsy * cz + sx * sz;
	_m10 = -cy * sz;
	_m11 = -sxsy * sz + cx * cz;
	_m12 = cxsy * sz + sx * cz;
	_m20 = sy;
	_m21 = -sx * cy;
	_m22 = cx * cy;
    }

    /**
     * Create a Matrix from three column vectors.
     *
     * @param a Column 0
     * @param b Column 1
     * @param c Column 2
     * @return A matrix with the specified columns
     */
    public static Matrix3 fromColumns(Vector3 a, Vector3 b, Vector3 c) {
	return new Matrix3(a.getX(), b.getX(), c.getX(),
			   a.getY(), b.getY(), c.getY(),
			   a.getZ(), b.getZ(), c.getZ());
    }

    /**
     * Convert body-referenced ZYX Euler angles to an active rotation matrix.<p>
     *
     * Note that the arguments are in the order Z,Y,X. This convention
     * supports future extension to Euler sequences such as XYX or YZY,
     * where the angles do not correspond to the X,Y,Z axes.
     *
     * @param zr Rotation about Z axis in radians
     * @param yr Rotation about Y axis in radians
     * @param xr Rotation about X axis in radians
     * @return The resulting matrix
     */
    public static Matrix3 fromEulerZYX(double zr, double yr, double xr) {
	return new Matrix3(zr, yr, xr);
    }

    /**
     * Convert body-referenced XYZ Euler angles to an active rotation matrix.
     *
     * @param xr Rotation about X axis in radians
     * @param yr Rotation about Y axis in radians
     * @param zr Rotation about Z axis in radians
     * @return The resulting matrix
     */
    public static Matrix3 fromEulerXYZ(double xr, double yr, double zr) {
	return new Matrix3(-zr, -yr, -xr).mTranspose();
    }

    /**
     * Create a matrix with 'v' as the leading diagonal.
     *
     * @param v The leading diagonal of the matrix
     * @return The resulting matrix
     */
    public static Matrix3 fromDiagonal(Vector3 v) {
	return new Matrix3(v.getX(), 0, 0,
			   0, v.getY(), 0,
			   0, 0, v.getZ());
    }

    /**
     * Set this matrix to the active rotation described by an AxisAngle.
     *
     * @param aa The rotation axis and angle
     */
    public void set(AxisAngle aa) {
	Vector3 axis = aa.axis();
	double angle = aa.angle();

	Vector3 v = axis.normalize();

	double x = v.getX();
	double y = v.getY();
	double z = v.getZ();

	double c = Math.cos(angle);
	double s = Math.sin(angle);
	double t = 1 - c;

	double sx = s * x;
	double sy = s * y;
	double sz = s * z;

	double tx = t * x;
	double ty = t * y;
	double tz = t * z;

	double txy = tx * y;
	double tyz = ty * z;
	double txz = tx * z;

	_m00 = tx * x + c;
	_m01 = txy - sz;
	_m02 = txz + sy;
	_m10 = txy + sz;
	_m11 = ty * y + c;
	_m12 = tyz - sx;
	_m20 = txz - sy;
	_m21 = tyz + sx;
	_m22 = tz * z + c;
    }

    /**
     * Return a 3x3 identity matrix.
     *
     * @return An identity matrix
     */
    public static Matrix3 identity() {
	return new Matrix3(new double[]{1,0,0,0,1,0,0,0,1});
    }

    /**
     * Create an active rotation about X.<p>
     *
     * Rotates clockwise when looking away from origin assuming
     * a right-handed coordinate system.
     *
     * @param angle Rotation angle in radians
     * @return Active rotation matrix
     */
    public static Matrix3 xRotation(double angle) {
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	return new Matrix3(new double[]{1,0,0, 0,c,-s, 0,s,c});
    }

    /**
     * Create an active rotation about Y.<p>
     *
     * Rotates clockwise when looking away from origin assuming
     * a right-handed coordinate system.
     *
     * @param angle Rotation angle in radians
     * @return Active rotation matrix
     */
    public static Matrix3 yRotation(double angle) {
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	return new Matrix3(new double[]{c,0,s, 0,1,0, -s,0,c});
    }
 
    /**
     * Create an active rotation about Z.<p>
     *
     * Rotates clockwise when looking away from origin assuming
     * a right-handed coordinate system.
     *
     * @param angle Rotation angle in radians
     * @return Active rotation matrix
     */
    public static Matrix3 zRotation(double angle) {
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	return new Matrix3(new double[]{c,-s,0, s,c,0, 0,0,1});
    }

    /**
     * Set the value of a specified element.
     *
     * @param row Row index
     * @param column Column index
     * @param value Value to be assigned to element
     */
    public void set(int row, int column, double value) {
	if(row<0 || row>2 || column<0 || column>2) {
	    throw new IllegalArgumentException("Invalid indices");
	}
	switch(row * 3 + column) {
	    case 0: _m00 = value; break;
	    case 1: _m01 = value; break;
	    case 2: _m02 = value; break;
	    case 3: _m10 = value; break;
	    case 4: _m11 = value; break;
	    case 5: _m12 = value; break;
	    case 6: _m20 = value; break;
	    case 7: _m21 = value; break;
	    case 8: _m22 = value; break;
	    default: throw new IllegalArgumentException("Invalid indices");
	}
    }

    /**
     * Set the elements of this matrix equal to those of another matrix.
     *
     * @param m The other matrix
     */
    public void set(Matrix3 m) {
	_m00 = m._m00;
	_m01 = m._m01;
	_m02 = m._m02;
	_m10 = m._m10;
	_m11 = m._m11;
	_m12 = m._m12;
	_m20 = m._m20;
	_m21 = m._m21;
	_m22 = m._m22;
    }

    /**
     * Set this matrix to a rotation matrix derived from a Quaternion.<p>
     *
     * If the quaternion represents an active rotation, the resulting
     * matrix will also be an active rotation.<p>
     *
     * The quaternion must be normalized to ensure that resulting matrix
     * represents a pure rotation.
     *
     * @param q The quaternion
     */
    public void set(Quaternion q) {
	double x = q.getX();
	double y = q.getY();
	double z = q.getZ();
	double w = q.getW();

        double ww = w * w;
	double xx = x * x;
        double xy = x * y;
        double xz = x * z;
        double xw = x * w;
        double yy = y * y;
        double yz = y * z;
        double yw = y * w;
        double zz = z * z;
        double zw = z * w;

        _m00 = ww + xx - yy - zz;
	_m01 = 2 * (xy - zw);
	_m02 = 2 * (xz + yw);
	_m10 = 2 * (xy + zw);
	_m11 = ww + yy - xx - zz;
	_m12 = 2 * (yz - xw);
	_m20 = 2 * (xz - yw);
	_m21 = 2 * (yz + xw);
	_m22 = ww + zz - xx - yy;
    }

    /**
     * Convert a rotation matrix to a Quaternion.<p>
     *
     * If the matrix represents an active rotation, the quaternion
     * will also be an active rotation.
     *
     * @return The resulting quaternion
     */
    public Quaternion toQuaternion() {
	double t = 1 + _m00 + _m11 + _m22;  // Trace + 1
	double x,y,z,w;

	// If trace + 1 is positive, use direct calculation
	if(t > 1E-8) {        // XXX: Is this the best value?
// 	    System.out.println("trace + 1 is positive");
	    double s = Math.sqrt(t) * 2;
	    x = (_m21 - _m12) / s;
	    y = (_m02 - _m20) / s;
	    z = (_m10 - _m01) / s;
	    w = 0.25 * s;
	} else {
	    /*
	     * To avoid loss of accuracy, choose the largest diagonal element,
	     * to give the largest argument for the square-root, then 
	     * substitute to get the remaining 3 quaternion parameters.
	     */
	    if((_m00 > _m11) && (_m00 > _m22)) { // m00 is largest
		double s = Math.sqrt(1 + _m00 -_m11 -_m22) * 2;
		x = 0.25 * s;
		y = (_m01 + _m10) / s;
		z = (_m02 + _m20) / s;
		w = (_m21 - _m12) / s;

	    } else if(_m11 > _m22) { // m11 is largest
		double s = Math.sqrt(1 + _m11 -_m00 -_m22) * 2;
		x = (_m01 + _m10) / s;
		y = 0.25 * s;
		z = (_m21 + _m12) / s;
		w = (_m02 - _m20) / s;

	    } else { // m22 is largest
		double s = Math.sqrt(1 + _m22 -_m00 -_m11) * 2;
		x = (_m02 + _m20) / s;
		y = (_m21 + _m12) / s;
		z = 0.25 * s;
		w = (_m10 - _m01) / s;
	    }
	}
	return new Quaternion(x,y,z,w);
    }

    /**
     * Add another matrix in-place.
     *
     * @param m The other matrix
     * @return This matrix after adding the other matrix
     */
    public Matrix3 mAdd(Matrix3 m) {
	_m00 += m._m00;
	_m01 += m._m01;
	_m02 += m._m02;
	_m10 += m._m10;
	_m11 += m._m11;
	_m12 += m._m12;
	_m20 += m._m20;
	_m21 += m._m21;
	_m22 += m._m22;

	return this;
    }

    /**
     * Add a constant to each matrix element in-place.
     *
     * @param k The constant
     * @return This matrix after adding the constant
     */
    public Matrix3 mAdd(double k) {
	_m00 += k;
	_m01 += k;
	_m02 += k;
	_m10 += k;
	_m11 += k;
	_m12 += k;
	_m20 += k;
	_m21 += k;
	_m22 += k;

	return this;
    }

    /**
     * Subtract another matrix from this one in-place.
     *
     * @param m The other matrix
     * @return This matrix after subtracting the other matrix
     */
    public Matrix3 mSubtract(Matrix3 m) {
	_m00 -= m._m00;
	_m01 -= m._m01;
	_m02 -= m._m02;
	_m10 -= m._m10;
	_m11 -= m._m11;
	_m12 -= m._m12;
	_m20 -= m._m20;
	_m21 -= m._m21;
	_m22 -= m._m22;

	return this;
    }

    /**
     * Subtract a constant from each matrix element in-place.
     *
     * @param k The constant
     * @return This matrix after subtracting the constant
     */
    public Matrix3 mSubtract(double k) {
	_m00 -= k;
	_m01 -= k;
	_m02 -= k;
	_m10 -= k;
	_m11 -= k;
	_m12 -= k;
	_m20 -= k;
	_m21 -= k;
	_m22 -= k;

	return this;
    }

    /**
     * Multiply by another matrix, returning a new matrix.
     *
     * @param m The other matrix
     * @return The resulting product
     */
    public Matrix3 multiply(Matrix3 m) {
	return copy().mMultiply(m);
    }

    /**
     * Multiply this matrix by another matrix, returning this.
     *
     * @param m The other matrix
     * @return This matrix after multiplying by the other matrix
     */
    public Matrix3 mMultiply(Matrix3 m) {
	double t00 = _m00*m._m00 + _m01*m._m10 + _m02*m._m20;
	double t01 = _m00*m._m01 + _m01*m._m11 + _m02*m._m21;
	double t02 = _m00*m._m02 + _m01*m._m12 + _m02*m._m22;

	double t10 = _m10*m._m00 + _m11*m._m10 + _m12*m._m20;
	double t11 = _m10*m._m01 + _m11*m._m11 + _m12*m._m21;
	double t12 = _m10*m._m02 + _m11*m._m12 + _m12*m._m22;

	double t20 = _m20*m._m00 + _m21*m._m10 + _m22*m._m20;
	double t21 = _m20*m._m01 + _m21*m._m11 + _m22*m._m21;
	double t22 = _m20*m._m02 + _m21*m._m12 + _m22*m._m22;

	_m00 = t00;
	_m01 = t01;
	_m02 = t02;
	_m10 = t10;
	_m11 = t11;
	_m12 = t12;
	_m20 = t20;
	_m21 = t21;
	_m22 = t22;

	return this;
    }

    /**
     * Multiply this matrix by a vector, returning a new Vector.
     *
     * @param v The vector
     * @return The resulting product
     */
    public Vector3 multiply(Vector3 v) {
	double vx = v.getX();
	double vy = v.getY();
	double vz = v.getZ();

	double x = _m00*vx + _m01*vy + _m02*vz;
	double y = _m10*vx + _m11*vy + _m12*vz;
	double z = _m20*vx + _m21*vy + _m22*vz;

	return new Vector3(x, y, z);
    }

    /**
     * Multiply by a constant, in-place.
     *
     * @param k The constant
     * @return This matrix after multiplying by the constant
     */
    public Matrix3 mMultiply(double k) {
	_m00 *= k;
	_m01 *= k;
	_m02 *= k;
	_m10 *= k;
	_m11 *= k;
	_m12 *= k;
	_m20 *= k;
	_m21 *= k;
	_m22 *= k;

	return this;
    }

    /**
     * Return the matrix element [row, column].
     *
     * @param row Row index
     * @param column Column index
     * @return The requested element
     */
    public double get(int row, int column) {
	if(row<0 || row>2 || column<0 || column>2) {
	    throw new IllegalArgumentException("Invalid indices");
	}
	switch(row * 3 + column) {
	    case 0: return _m00;
	    case 1: return _m01;
	    case 2: return _m02;
	    case 3: return _m10;
	    case 4: return _m11;
	    case 5: return _m12;
	    case 6: return _m20;
	    case 7: return _m21;
	    case 8: return _m22;
	    default: throw new IllegalArgumentException("Invalid indices");
	}
    }

    /**
     * Return the specified matrix row as a vector.
     *
     * @param row Row index
     * @return The requested row as a vector
     */
    public Vector3 getRow(int row) {
	switch(row) {
	    case 0: return new Vector3(_m00, _m01, _m02);
	    case 1: return new Vector3(_m10, _m11, _m12);
	    case 2: return new Vector3(_m20, _m21, _m22);
	    default: throw new IllegalArgumentException("Row out of range");
	}
    }

    /**
     * Return the specified matrix column as a vector.
     *
     * @param col Column index
     * @return The requested column as a vector
     */
    public Vector3 getColumn(int col) {
	switch(col) {
	    case 0: return new Vector3(_m00, _m10, _m20);
	    case 1: return new Vector3(_m01, _m11, _m21);
	    case 2: return new Vector3(_m02, _m12, _m22);
	    default: throw new IllegalArgumentException("Column out of range");
	}
    }

    /**
     * Set the specified matrix row from a vector.
     *
     * @param row Row index
     * @param v The row as a vector
     */
    public void setRow(int row, Vector3 v) {
	double x = v.getX();
	double y = v.getY();
	double z = v.getZ();

	switch(row) {
	    case 0: {_m00 = x; _m01 = y; _m02 = z; break; }
	    case 1: {_m10 = x; _m11 = y; _m12 = z; break; }
	    case 2: {_m20 = x; _m21 = y; _m22 = z; break; }
	    default: throw new IllegalArgumentException("Row out of range: " + row);
	}
    }

    /**
     * Set the specified matrix column from a vector.
     *
     * @param col Column index
     * @param v The column as a vector
     */
    public void setColumn(int col, Vector3 v) {
	double x = v.getX();
	double y = v.getY();
	double z = v.getZ();

	switch(col) {
	    case 0: {_m00 = x; _m10 = y; _m20 = z; break; }
	    case 1: {_m01 = x; _m11 = y; _m21 = z; break; }
	    case 2: {_m02 = x; _m12 = y; _m22 = z; break; }
	    default: throw new IllegalArgumentException("Column out of range: " + col);
	}
    }

    /**
     * Return the matrix columns as an array of vectors.
     *
     * @return Array of column vectors
     */
    public Vector3[] getColumns() {
	return new Vector3[]{ new Vector3(_m00, _m10, _m20),
			      new Vector3(_m01, _m11, _m21),
			      new Vector3(_m02, _m12, _m22) };
    }

    /**
     * Return the matrix rows as an array of vectors.
     *
     * @return Array of row vectors
     */
    public Vector3[] getRows() {
	return new Vector3[]{ new Vector3(_m00, _m01, _m02),
			      new Vector3(_m10, _m11, _m12),
			      new Vector3(_m20, _m21, _m22) };
    }

    /**
     * Return the leading diagonal as a vector.
     *
     * @return The leading diagonal as a vector
     */
    public Vector3 getDiagonal() {
	return new Vector3(_m00, _m11, _m22);
    }

    /**
     * Return the elements as as array.<p>
     *
     * The returned array is [m00,m01,m02,m10,m11,m12,m20,m21,m22].
     *
     * @return The array of elements
     */
    public double[] getElements() {
	return new double[]{_m00, _m01, _m02, _m10, _m11, _m12, _m20, _m21, _m22};
    }

    /**
     * Return the trace of this matrix.
     *
     * @return The trace
     */
    public double trace() {
	return _m00 + _m11 + _m22;
    }

    /**
     * Return the determinant of this matrix. 
     *
     * @return The determinant
     */
    public double determinant() {
	return _m00 * (_m11 * _m22 - _m21 * _m12)
	     - _m01 * (_m10 * _m22 - _m20 * _m12)
	     + _m02 * (_m10 * _m21 - _m20 * _m11);
    }

    /**
     * Transpose this matrix in place.
     *
     * @return This matrix after transposing it
     */
    public Matrix3 mTranspose() {
	double t = _m01;
	_m01 = _m10;
	_m10 = t;

	t = _m21;
	_m21 = _m12;
	_m12 = t;

	t = _m02;
	_m02 = _m20;
	_m20 = t;

	return this;
    }
    
    /**
     * Transpose this matrix, returning a new matrix.
     *
     * @return The transposed matrix
     */
    public Matrix3 transpose() {
	return copy().mTranspose();
    }
    
    /**
     * Invert the matrix in place.<p>
     *
     * Note: For a rotation matrix: inverse = transpose.
     *
     * @return This matrix after inverting it
     */
    public Matrix3 mInvert() {
	double det = determinant();
	
	double m00 = _m00;
	double m01 = _m01;
	double m02 = _m02;
	double m10 = _m10;
	double m11 = _m11;
	double m12 = _m12;
	double m20 = _m20;
	double m21 = _m21;
	double m22 = _m22;
	
	_m00 = (m11 * m22 - m21 * m12) / det;
	_m01 = (m02 * m21 - m22 * m01) / det;
	_m02 = (m01 * m12 - m11 * m02) / det;
	_m10 = (m12 * m20 - m22 * m10) / det;
	_m11 = (m00 * m22 - m20 * m02) / det;
	_m12 = (m02 * m10 - m12 * m00) / det;
	_m20 = (m10 * m21 - m20 * m11) / det;
	_m21 = (m01 * m20 - m21 * m00) / det;
	_m22 = (m00 * m11 - m10 * m01) / det;

	return this;
    }

    /**
     * Negate the matrix.
     *
     * @return This matrix after negating it
     */
    public Matrix3 mNegate() {
	_m00 = -_m00;
	_m01 = -_m01;
	_m02 = -_m02;
	_m10 = -_m10;
	_m11 = -_m11;
	_m12 = -_m12;
	_m20 = -_m20;
	_m21 = -_m21;
	_m22 = -_m22;

	return this;
    }

    /**
     * Return a copy of this matrix.
     *
     * @return A copy of this matrix
     */
    public Matrix3 copy() {
	return new Matrix3(this);
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
     * Return a String representation of this matrix.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this object
     */
    public String toString() {
	StringBuffer buff = new StringBuffer();
	buff.append("[" + _m00 + ", " + _m01 + ", " + _m02 + "]\n");
	buff.append("[" + _m10 + ", " + _m11 + ", " + _m12 + "]\n");
	buff.append("[" + _m20 + ", " + _m21 + ", " + _m22 + "]\n");
	return buff.toString();
    }

    /**
     * Return true if this matrix is equal to another matrix.<p>
     *
     * Equality is defined such that NaN=NaN and -0!=+0, which
     * is appropriate for use as a hash table key.
     *
     * @param obj The object to be compared
     * @return true if the objects are equal
     */
    public boolean equals(Object obj) {
        if(!(obj instanceof Matrix3)) {
	    return false;
	}
	Matrix3 m = (Matrix3)obj;
	return (Double.doubleToLongBits(_m00) ==
		Double.doubleToLongBits(m._m00))
	    && (Double.doubleToLongBits(_m01) ==
		Double.doubleToLongBits(m._m01))
	    && (Double.doubleToLongBits(_m02) ==
		Double.doubleToLongBits(m._m02))
	    && (Double.doubleToLongBits(_m10) ==
		Double.doubleToLongBits(m._m10))
	    && (Double.doubleToLongBits(_m11) ==
		Double.doubleToLongBits(m._m11))
	    && (Double.doubleToLongBits(_m12) ==
		Double.doubleToLongBits(m._m12))
	    && (Double.doubleToLongBits(_m20) ==
		Double.doubleToLongBits(m._m20))
	    && (Double.doubleToLongBits(_m21) ==
		Double.doubleToLongBits(m._m21))
	    && (Double.doubleToLongBits(_m22) ==
		Double.doubleToLongBits(m._m22));
    }

    /**
     * Return the hash code of this matrix.
     *
     * @return The hashcode of this object
     */
    public int hashCode() {
        int result = 17;
	long f = Double.doubleToLongBits(_m00);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m01);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m02);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m10);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m11);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m12);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m20);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m21);
	result = 37*result + (int)(f^(f>>>32));
	f = Double.doubleToLongBits(_m22);
	result = 37*result + (int)(f^(f>>>32));
	return result;
    }


    /**
     * Returns true if this matrix is approximately equal to another.<p>
     *
     * The criterion is that the L-infinte distance between the two matrices
     * u and v is less than or equal to epsilon.
     *
     * @param m The other matrix
     * @param epsilon The maximum difference
     * @return True if the objects are approximately equal
     */
    public boolean epsilonEquals(Matrix3 m, double epsilon) {
	double x = Math.abs(_m00 - m._m00);
	x = Math.max(x, Math.abs(_m01 - m._m01));
	x = Math.max(x, Math.abs(_m02 - m._m02));
	x = Math.max(x, Math.abs(_m10 - m._m10));
	x = Math.max(x, Math.abs(_m11 - m._m11));
	x = Math.max(x, Math.abs(_m12 - m._m12));
	x = Math.max(x, Math.abs(_m20 - m._m20));
	x = Math.max(x, Math.abs(_m21 - m._m21));
	x = Math.max(x, Math.abs(_m22 - m._m22));

	return x <= epsilon;
    }

    /**
     * Return the element with the maximum absolute value.
     *
     * @return The maximum absolute value an element
     */
    public double maxAbs() {
	double max = 0;
	double x = Math.abs(_m00);
	if(x > max) max = x;
	x = Math.abs(_m01);
	if(x > max) max = x;
	x = Math.abs(_m02);
	if(x > max) max = x;
	x = Math.abs(_m10);
	if(x > max) max = x;
	x = Math.abs(_m11);
	if(x > max) max = x;
	x = Math.abs(_m12);
	if(x > max) max = x;
	x = Math.abs(_m20);
	if(x > max) max = x;
	x = Math.abs(_m21);
	if(x > max) max = x;
	x = Math.abs(_m22);
	if(x > max) max = x;

	return max;
    }

    /**
     * Test whether the matrix is symmetric.<p>
     *
     * The matrix is considered symmetric if the absolute difference
     * between each element and the corresponding element in its
     * transpose is less than a given value <tt>epsilon</tt>.
     *
     * @param epsilon Maximum difference
     * @return True if this matrix is symmetric
     */
    public boolean isSymmetric(double epsilon) {
	double d1 = Math.abs(_m01 - _m10);
	double d2 = Math.abs(_m02 - _m20);
	double d3 = Math.abs(_m12 - _m21);

	return (d1 <= epsilon) && (d2 <= epsilon) && (d3 <= epsilon);
    }

    /**
     * Modified Gram-Schmidt factorization of a Matrix.<p>
     *
     * Factorizes the matrix A into two matrices Q and R, such that A = Q * R.
     * Q is an orthogonal matrix and R is an upper triangular matrix. <p>
     *
     * Uses Gram-Schmidt factorization, as modified by Golub for better
     * numerical stability. This subtracts the projection onto q1, then 
     * subtracts the projection of what remains onto q2, and so on. <p>
     *
     * Reference: Gene H. Golub, Charles F. van Loan, "Matrix Computations",
     * John Hopkins University Press.
     *
     * @return 2-element array containing matrices Q and R
     */
    public Matrix3[] mgs() {

	Vector3[] a = getColumns();
	Matrix3 r = new Matrix3();

	double maxa = maxAbs();
	// FIXME: Throw exception if maxa == 0

	for(int i=0; i<3; i++) {
	    double rii = a[i].norm();
	    r.set(i, i, rii);

	    // 2 * 3 * 3 = 18
	    if(rii <= Math.sqrt(18 * i) * Constants.D_ULP * maxa) {
		System.err.println("Matrix has rank " + i);
	    }
	    a[i].mMultiply(1 / rii);
	    
	    for(int j=i+1; j<3; j++) {
		double rij = a[i].dot(a[j]);
		r.set(i, j, rij);
		a[j].mSubtract(a[i].multiply(rij));
	    }
	}

	Matrix3 q = Matrix3.fromColumns(a[0], a[1], a[2]);
	
	// An alternative would be to mutate A into Q and return R.
	return new Matrix3[]{q, r};
    }

    /**
     * Test whether matrix is a symmetric positive semi-definite matrix.<p>
     *
     * A real matrix is positive semi-definite if it is symmetric and
     * all its eigenvalues are non-negative.
     *
     * @param epsilon Tolerance to be applied
     * @return true if this matrix is symmetric positive semi-definite
     */
    public boolean isSPSD(double epsilon) {
	// FIXME: Consider removing epsilon argument.
	// The Flight Dynamics Matlab function does not have this argument

	if(!isSymmetric(epsilon)) {
	    return false;
	}

	double b = -(_m00 + _m11 + _m22);
	double c = _m00*_m11 + _m11*_m22 + _m00*_m22
	           - _m10*_m01 - _m12*_m21 - _m20*_m02;
	double d = _m10*_m01*_m22 + _m12*_m21*_m00 + _m20*_m02*_m11
	           - _m00*_m11*_m22 - _m01*_m12*_m20 - _m02*_m10*_m21;

	System.out.println("" + b + " " + c + " " + d);
	System.out.println("" + (9*b*c) + " " + (3*d));

	double q = (3 * c - b * b) / 9;
	double r = (9*b*c - 27*d - 2*b*b*b) / 54;
	System.out.println("q="  + q);
	System.out.println("r="  + r);

	double h2 = q*q*q + r*r;
	System.out.println("h2 = "  + h2);

	double b3 = -b/3;

	double x1, x2, x3;  // Roots

	// As it is symmetric, the eigenvalues must all be real.
 	if(h2 > 0) {
	    // Complex roots not possible if matrix is symmetric
	    throw new RuntimeException("Not symmetric!");

	} else if(-h2 < 1E-6) {
	    // all real and at least two equal
	    System.out.println("\nAll real and at least two equal");

	    double S = cubeRoot(r);
	    x1 = b3 + 2.0 * S;
	    x2 = b3 - S;
	    x3 = x2;

    	} else { // h2 < 0) {
	    // All real and unequal
	    System.out.println("\nAll real and unequal");

	    double h = Math.sqrt(-h2);
	    
	    // Complex cube-root
	    double theta = Math.atan2(h, r) / 3;
	    double mod = cubeRoot(Math.sqrt(r * r - h2));
	    double sr = mod * Math.cos(theta);
	    double si = mod * Math.sin(theta);

	    x1 = b3 + 2 * sr;
	    
	    double y = Math.sqrt(3) * si;
	    x2 = b3 - sr - y;
	    x3 = b3 - sr + y;
	}
	
	System.out.println("Eigenvalues = " + x1 + ", " + x2 + ", " + x3);

	return (x1 >= 0) && (x2 >= 0) && (x3 >= 0);
    }

    /**
     * Return the cube root of a number.
     *
     * @param x A number
     * @return The cube root of x
     */
    private static double cubeRoot(double x) {
	return Math.exp(Math.log(x) / 3);
    }
}

