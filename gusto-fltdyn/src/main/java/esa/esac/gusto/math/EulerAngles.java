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
 * Converts between Euler angles and quaternions or rotation matrices.<p>
 * 
 * This class handles both symmetric axis sequences (XYX, XZX, YXY, YZY, ZXZ and ZYZ)
 * and asymmetric sequences (XYZ, XZY, YXZ, YZX, ZXY, ZYX). The latter are sometimes 
 * known as Tait-Bryan angles. In both cases, the axes are body-referenced and the 
 * corresponding quaternion or matrix represents an active rotation.<p>
 * 
 * The quaternions Q and -Q both convert to the same Euler angles. Hence, the conversion
 * from a quaternion to Euler angles and back to a quaternion does not necessarily yield 
 * the original quaternion. This is because quaternions require a rotation of 4&pi; to 
 * return to the starting point.<p>
 * 
 * Also, a conversion from Euler angles to a quaternion and back to Euler angles does
 * not necessarily yield the original angles because there are pairs of Euler angle
 * representations that correspond to the same overall rotation. For example:<p>
 * 
 * <blockquote>
 * [a, b, c] &equiv; [a+&pi;, &pi;-b, c+&pi;] (asymmetric axes)<br>
 * [a, b, c] &equiv; [a+&pi;, -b, c+&pi;] (symmetric axes)<br>
 * </blockquote>
 * 
 * Multiple solutions can also occur because Euler angle representations suffer from 
 * singularities. For example:
 * 
 * <blockquote>
 * &forall; k . [a, &pi;/2, c] &equiv; [a+k, &pi;/2, c+k] (asymmetric anticyclic axes)<br>
 * &forall; k . [a, -&pi;/2, c] &equiv; [a+k, -&pi;/2, c-k] (asymmetric anticyclic axes)<br>
 * <br>
 * &forall; k . [a, &pi;/2, c] &equiv; [a+k, &pi;/2, c-k] (asymmetric cyclic axes)<br>
 * &forall; k . [a, -&pi;/2, c] &equiv; [a+k, -&pi;/2, c+k] (asymmetric cyclic axes)<br>
 * <br>
 * &forall; k . [a, 0, c] &equiv; [a+k, 0, c-k] (symmetric axes)<br>
 * &forall; k . [a, &pi;, c] &equiv; [a+k, &pi;, c+k] (symmetric axes)<br>
 * </blockquote>
 * 
 * @author Jon Brumfitt
 */
public class EulerAngles {
    
    /**
     * Sequences of rotation axes for the Euler angles.
     */
    public enum Axes { XYX, XYZ, XZX, XZY, YXY, YXZ, YZX, YZY, ZXY, ZXZ, ZYX, ZYZ };
    
    private static final double PI_2 = Math.PI / 2;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private EulerAngles() {
    }
    
    /**
     * Convert body-referenced Euler angles to an active Quaternion.<p>
     * 
     * Returns a Quaternion Q with a non-zero scalar component (w).
     * 
     * @param axes Sequence of rotation axes
     * @param a Rotation about first axis (radians)
     * @param b Rotation about second axis (radians)
     * @param c Rotation about third axis (radians)
     * @return Quaternion representing the combined rotation
     */
    public static Quaternion toQuaternion(Axes axes, double a, double b, double c) {
	switch(axes) {
	    case XYX: 
		return Quaternion.xRotation(a).mMultiply(Quaternion.yRotation(b).mMultiply(Quaternion.xRotation(c)));
	    case XYZ: 
		return Quaternion.xRotation(a).mMultiply(Quaternion.yRotation(b).mMultiply(Quaternion.zRotation(c)));
	    case XZX: 
		return Quaternion.xRotation(a).mMultiply(Quaternion.zRotation(b).mMultiply(Quaternion.xRotation(c)));
	    case XZY: 
		return Quaternion.xRotation(a).mMultiply(Quaternion.zRotation(b).mMultiply(Quaternion.yRotation(c)));
	    case YXY:
		return Quaternion.yRotation(a).mMultiply(Quaternion.xRotation(b).mMultiply(Quaternion.yRotation(c)));
	    case YXZ: 
		return Quaternion.yRotation(a).mMultiply(Quaternion.xRotation(b).mMultiply(Quaternion.zRotation(c)));
	    case YZX: 
		return Quaternion.yRotation(a).mMultiply(Quaternion.zRotation(b).mMultiply(Quaternion.xRotation(c)));
	    case YZY: 
		return Quaternion.yRotation(a).mMultiply(Quaternion.zRotation(b).mMultiply(Quaternion.yRotation(c)));
	    case ZXY: 
		return Quaternion.zRotation(a).mMultiply(Quaternion.xRotation(b).mMultiply(Quaternion.yRotation(c)));
	    case ZXZ: 
		return Quaternion.zRotation(a).mMultiply(Quaternion.xRotation(b).mMultiply(Quaternion.zRotation(c)));
	    case ZYX: 
		return Quaternion.zRotation(a).mMultiply(Quaternion.yRotation(b).mMultiply(Quaternion.xRotation(c)));
	    case ZYZ: 
		return Quaternion.zRotation(a).mMultiply(Quaternion.yRotation(b).mMultiply(Quaternion.zRotation(c)));
	    default: 
		throw new IllegalArgumentException("Unknown sequence: " + axes);
	}
    }

    /**
     * Convert Quaternion to ZYX Euler angles.<p>
     * 
     * A singularity occurs when the Y angle is PI/2 or -PI/2. 
     * This method returns the solution 0 <= b <= PI.<p>
     * 
     * When the <tt>cyclic</tt> argument is true, the result is reversed
     * and negated.
     * 
     * @param x Quaternion x component
     * @param y Quaternion y component
     * @param z Quaternion z component
     * @param w Quaternion w component
     * @param cyclic True if axes are in cyclic order
     * @return Array containing ZYX angles in radians
     */
    private static double[] toEulerZYX(double x, double y, double z, double w, boolean cyclic) {
	/*
	 * Set the threshold for handling of singularities. If the margin is too
	 * small, the accuracy of 'a' and 'c' suffer. If it is set too large, the 
	 * accuracy of 'b' suffers (as it is rounded). 1E-13 degrees is a good 
	 * compromise which corresponds to an error in 'b' of 0.09 arcseconds.
	 */
        final double limit = 0.5 * (1 - 1E-13);

	double a = 0;
	double b = 0;
	double c = 0;
	double uu = x*x + y*y + z*z + w*w;
	double d = (w*y - z*x) / uu;
	
	if(Math.abs(d) > limit) { // Singularity when b = PI/2 or -PI/2
	    a = 0;
	    b = d > 0 ? PI_2 : - PI_2;
	    c = - Math.atan2(2 * (x*y - z*w), y*y + w*w - x*x - z*z);
	} else {
	    a = Math.atan2(2 * (w*x + y*z) , z*z + w*w - x*x - y*y);
	    b = Math.asin(2 * d);
	    c = Math.atan2(2 * (w*z + x*y) , x*x + w*w - y*y - z*z);
	}
	if(cyclic) {
	    return new double[] { -a, -b, -c };
	} else {
	    return new double[] { c, b, a };
	}
    }
    
    /**
     * Convert Quaternion to XYX Euler angles.<p>
     * 
     * A singularity occurs when the Y angle is 0 or PI.
     * This method returns the solution -PI <= b <= PI.
     * 
     * @param x Quaternion x component
     * @param y Quaternion y component
     * @param z Quaternion z component
     * @param w Quaternion w component
     * @return Array containing XYX angles (in radians)
     */
    private static double[] toEulerXYX(double x, double y, double z, double w) {
	/*
	 * Set the threshold for handling of singularity. If this is set too
	 * low, the accuracy of 'a' and 'c' suffer. If it is set too high, the 
	 * accuracy of 'b' suffers (as it is rounded). 1E-13 degrees is a good 
	 * compromise which corresponds to an error in 'b' of 0.09 arcseconds.
	 */
	final double limit = 1.0 - 1E-13;
	
	double a = 0;
	double b = 0;
	double c = 0;
	double uu = x*x + y*y + z*z + w*w;
	double d = (w*w + x*x - y*y - z*z) / uu;
	
	if(d > limit) { // Singularity when b = 0
	    c = Math.acos((w*w + y*y - x*x - z*z) / uu);
	} else if(d < -limit) { // Singularity when b = PI
	    b = Math.PI;
	    c = Math.acos((w*w + y*y - x*x - z*z) / uu);
	} else {
	    a = Math.atan2(x*y + z*w, y*w - x*z);
	    b = Math.acos(d);
	    c = Math.atan2(x*y - z*w, y*w + x*z);
	}
	return new double[] { a, b, c };
    }
    
    /**
     * Convert an active Quaternion to body-referenced Euler angles [a, b, c].<p>
     * 
     * For asymmetric axis sequences (e.g. ZYX), the angles returned are in the 
     * following ranges:<p>
     * <blockquote>
     * -&pi; &le; a &le; &pi;<br>
     * -&pi;/2 &le; b &le; &pi;/2<br>
     * -&pi; &le; c &le; &pi;<br>
     * </blockquote>
     *
     * For symmetric axis sequences (e.g. XYX), the angles returned are in the 
     * following ranges:<p>
     * <blockquote>
     * -&pi; &le; a &le; &pi;<br>
     * 0 &le; b &le; &pi;<br>
     * -&pi; &le; c &le; &pi;<br>
     * </blockquote>
     * 
     * At singularities, the solution returned is the one with a=0 for cyclic and
     * symmetric axes sequences and c=0 for anticyclic sequences. See the class 
     * description for further details on singularities.
     *
     * @param axes Sequence of rotation axes
     * @param q The Quaternion
     * @return Array containing the angles (in radians)
     */
    public static double[] fromQuaternion(Axes axes, Quaternion q) {
	double x = q.getX();
	double y = q.getY();
	double z = q.getZ();
	double w = q.getW();
	
	// Use symmetries to convert to any valid sequence of axes.
	switch(axes) {
	    case ZXY:
		return toEulerZYX(-z, -x, -y, w, true);
	    case XYX:
		return toEulerXYX(x, y, z, w);
	    case XYZ:
		return toEulerZYX(-x, -y, -z, w, true);
	    case XZX:
		return toEulerXYX(x, z, -y, w);
	    case XZY:
		return toEulerZYX(y, z, x, w, false);
	    case YXY:
		return toEulerXYX(y, x, -z, w);
	    case YXZ:
		return toEulerZYX(z, x, y, w, false);
	    case YZX:
		return toEulerZYX(-y, -z, -x, w, true);
	    case YZY:
		return toEulerXYX(y, z, x, w);
	    case ZXZ:
		return toEulerXYX(z, x, y, w);
	    case ZYX:
		return toEulerZYX(x, y, z, w, false);
	    case ZYZ:
		return toEulerXYX(z, y, -x, w);
	    default:
		throw new IllegalArgumentException("Unknown sequence: " + axes);
	}
    }
    
    /**
     * Convert body-referenced Euler angles to an active rotation matrix.
     * 
     * @param axes Sequence of rotation axes
     * @param a Rotation about first axis (radians)
     * @param b Rotation about second axis (radians)
     * @param c Rotation about third axis (radians)
     * @return Matrix3 representing the combined rotation
     */
    public static Matrix3 toMatrix3(Axes axes, double a, double b, double c) {
	return toQuaternion(axes, a, b, c).toMatrix3();
    }
    
    /**
     * Convert an active rotation matrix to body-referenced Euler angles [a, b, c].<p>
     * 
     * For asymmetric axis sequences (e.g. ZYX), the angles returned are in the 
     * following ranges:<p>
     * <blockquote>
     * -&pi; &le; a &le; &pi;<br>
     * -&pi;/2 &le; b &le; &pi;/2<br>
     * -&pi; &le; c &le; &pi;<br>
     * </blockquote>
     *
     * For symmetric axis sequences (e.g. XYX), the angles returned are in the 
     * following ranges:<p>
     * <blockquote>
     * -&pi; &le; a &le; &pi;<br>
     * 0 &le; b &le; &pi;<br>
     * -&pi; &le; c &le; &pi;<br>
     * </blockquote>
     * 
     * At singularities, the solution returned is the one with a=0 for cyclic and
     * symmetric axes sequences and c=0 for anticyclic sequences. See the class 
     * description for further details on singularities.
     *
     * @param axes Sequence of rotation axes
     * @param m The matrix
     * @return Array containing the angles (in radians)
     */
    public static double[] fromMatrix3(Axes axes, Matrix3 m) {
	// Orthonormalization of matrix is handled by convertingit to a Quaternion
	return fromQuaternion(axes, m.toQuaternion());
    }
}

