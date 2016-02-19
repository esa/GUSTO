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
 * An inertial attitude represented by body-referenced (+Z)(-Y)(-X) Euler angles.<p>
 *
 * The inertial attitude of a body can be represented by a triple (RA,DEC,POS) which
 * defines Euler angles relative to the Equatorial reference frame. The body is
 * rotated first through RA (right ascension) about the body +Z axis, followed DEC
 * (declination) about the body -Y axis and finally POS (position angle) about the
 * body -X axis.<p>
 *
 * Hence, right ascension increases clockwise about the Z axis, declination increases
 * anticlockwise about the Y axis and position angle increases anticlockwise about 
 * the X axis. The position angle is the angle between the body X-Z plane and the
 * plane defined by the body X axis and reference frame Z axis (North).<p>
 *
 * This representation of attitude is especially useful for spacecraft with an
 * astronomical telescope pointing along the X axis. The coordinates (RA,DEC)
 * describe the pointing direction of the telescope, whilst POS describes the
 * rotation of the telescope about the line of sight. The Attitude class extends
 * Direction, since it is simply a Direction with an associated orientation.<p>
 *
 * The position angle and right ascension are discontinuous at the poles (i.e.
 * declination of +/-90 degrees). This requires care when performing calculations,
 * as rounding errors could flip the attitude by 180 degrees. Consequently, it
 * is generally preferred to represent attitudes as quaternions for performing
 * calculations, since these do not suffer from singularities.<p>
 *
 * In the limit, the sum RA+POS (mod 360) is constant at the North pole and the
 * difference RA-POS (mod 360) is constant at the South pole. Hence, the three-axis
 * attitude is well-defined on the whole celestial sphere by the triple (RA,DEC,POS),
 * although there is a many-to-one mapping of this triple onto attitudes at the poles.
 * The Attitude class provides a method to round (RA,DEC,POS) triples to a given
 * precision for output (e.g. printing), as a single atomic operation that avoids
 * problems with singularities. At the poles, many-to-one mapping of attitudes
 * onto (RA,DEC,POS) triples is avoided by (arbitrarily) choosing RA=0.
 *
 * @author  Jon Brumfitt
 */
public class Attitude extends Direction {

    private static final double twoPi = Math.PI + Math.PI;

    private double _phi;

    /*
     * Create an Attitude using specified RA, DEC & position angle in degrees.
     *
     * @param ra Right ascension in degrees [0,360)
     * @param dec Declination in degrees [-90,90]
     * @param pos Position angle in degrees [0,360)
     * @return the Attitude
     */
    public static Attitude fromDegrees(double ra, double dec, double pos) {
	return new Attitude(Math.toRadians(ra), Math.toRadians(dec), Math.toRadians(pos));
    }

    /**
     * Create an Attitude from a Direction and position angle in degrees.
     *
     * @param dir Direction of the body X axis
     * @param pos Position angle in degrees [0,360)
     * @return the Attitude
     */
    public static Attitude fromDegrees(Direction dir, double pos) {
	return new Attitude(dir, Math.toRadians(pos));
    }

    /**
     * Create a new Attitude with position angle = 0.
     */
    public Attitude() {
	_phi = 0.0;
    }

    /*
     * Create an Attitude using specified RA , DEC & position angle in radians.
     *
     * @param ra Right ascension in radians [0,2*pi)
     * @param dec Declination in radians [-pi/2,pi/2]
     * @param pos Position angle in radians [0,2*pi)
     */
    public Attitude(double ra, double dec, double pos) {
	super(ra, dec);
	_phi = pos;
    }

    /**
     * Create a copy of another Attitude.
     *
     * @param a The Attitude to be copied
     */
    public Attitude(Attitude a) {
	_alpha = a._alpha;
	_delta = a._delta;
	_phi = a._phi;
    }

    /**
     * Create an Attitude from a Direction and position angle in radians.
     *
     * @param dir Direction of the body X axis
     * @param pos Position angle in radians [0,2*pi)
     */
    public Attitude(Direction dir, double pos) {
	super(dir);
	_phi = pos;
    }

    /**
     * Create an Attitude from an active rotation matrix.
     *
     * @param m Active matrix describing rotation of body relative to equatorial frame.
     */
    public Attitude(Matrix3 m) {
	this(m.toQuaternion());
    }

    /**
     * Create an Attitude from a Quaternion.
     *
     * @param q Active quaternion describing rotation of body relative to equatorial frame.
     */
    public Attitude(Quaternion q) {
	this(q.toAttitude());
    }

    /**
     * Return the position angle (in degrees).<p> 
     *
     * <b>Warning:</b> This function should be used with care, since the position
     * angle is indeterminate at the poles. The triple (RA,DEC,POS) should be
     * considered as an entity to avoid problems with singularities.
     *
     * @return Position angle in degrees [0,360)
     */
    public double getPosDegrees() {
	double pos = Math.toDegrees(_phi);

	while(pos >= 360.0) {
	    pos -= 360.0;
	}
	while(pos < 0.0) {
	    pos += 360.0;
	}
	return pos;
    }

    /**
     * Return the position angle (in radians).<p>
     *
     * <b>Warning:</b> This function should be used with care, since the position
     * angle is indeterminate at the poles. The triple (RA,DEC,POS) should be
     * considered as an entity to avoid problems with singularities.
     *
     * @return Position angle in radians
     */
    public double getPhi() {
	return _phi;
    }

    /**
     * Return this Attitude expressed as an active rotation matrix.
     *
     * @return Active matrix describing rotation of body relative to equatorial frame.
     */
    public Matrix3 toMatrix3() {
	return Matrix3.fromEulerZYX(_alpha, -_delta, -_phi);
    }

    /**
     * Return this Attitude expressed as an active Quaternion.
     *
     * @return Active quaternion describing rotation of body relative to equatorial frame.
     */
    public Quaternion toQuaternion() {
	double phi2   = _phi / 2;
	double delta2 = _delta / 2;
	double alpha2 = _alpha / 2;

	double cx = Math.cos(phi2);
	double sx = -Math.sin(phi2);
	double cy = Math.cos(delta2);
	double sy = -Math.sin(delta2);
	double cz = Math.cos(alpha2);
	double sz = Math.sin(alpha2);

	double x = -cx * sy * sz + sx * cy * cz;
	double y = cx * sy * cz + sx * cy * sz;
	double z = cx * cy * sz - sx * sy * cz;
	double w = cx * cy * cz + sx * sy * sz;

	return new Quaternion(x, y, z, w);
    }

    /**
     * Return the relative angle to 'dir' along a great circle.<p>
     *
     * The angle is measured anticlockwise from the body Y axis, as this is in
     * the same sense as position angle.
     *
     * @param dir A point defined by a Direction
     * @return Spherical distance to point, in radians
     */
    public double relativeAngleTo(Direction dir) {
	return positionAngleTo(dir) - _phi;
    }

    /**
     * Return the Direction at a given radius and angle relative to body along
     * a great circle.<p>
     *
     * The angle is measured anticlockwise from the body Y axis, as this is in
     * the same sense as position angle.
     *
     * @param distance Distance along great circle to point, in radians.
     * @param angle Angle to point relative to body Y-axis, in radians
     * @return Direction describing the offset point
     */
    public Direction relativeOffset(double distance, double angle) {
	double a = _phi + angle;
	if( a >= twoPi ) a -= twoPi;
	if( a < 0 )      a += twoPi;

	return offsetBy(distance, a);
    }

    /**
     * Returns a new Attitude which is offset along a great-circle, by 
     * 'distance' (radians), at 'angle' (radians) relative to the frame
     * +Z axis (i.e. North when POS=0).
     *
     * @param distance Distance along great circle to point, in radians
     * @param angle Angle to point relative to body Y-axis, in radians
     * @return Attitude at the specified displacement
     */
    public Attitude offset(double distance, double angle) {
	double pos = _phi + angle;
	if( pos >= twoPi ) pos -= twoPi;
	if( pos < 0 )      pos += twoPi;
	
	Direction finish = offsetBy(distance, pos);
	double phi = finish.positionAngleTo(this) - angle + Math.PI;
	
	if( phi >= twoPi ) phi -= twoPi;
	if( phi < 0 ) _phi += twoPi;
	if( phi == twoPi ) phi=0;

	return new Attitude(finish, phi);
    }

    /**
     * Returns a new Attitude which is rotated anticlockwise about the
     * X axis by 'angle' radians.<p>
     *
     * The angle is measured anticlockwise as this is in the same sense
     * as position angle.
     *
     * @param angle Anticlockwise rotation angle in radians.
     * @return An Attitude that is rotated by angle relative to this object
     */
    public Attitude rotateX(double angle) {

	double phi = _phi + angle;

	if(phi < 0.0)    phi += twoPi;
	if(phi >= twoPi) phi -= twoPi;

	return new Attitude(this, phi);
    }

    /**
     * Return a String representation of this Attitude.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return A String representation of this object
     */
    public String toString() {
	return getRaDegrees() + " " +  getDecDegrees() + " " + getPosDegrees();
    }

    /**
     * Returns an array containing rounded (RA,DEC,POS) in degrees.<p>
     *
     * The (RA,DEC,POS) quantities are rounded in one operation, to
     * avoid problems with the singularities at the poles. The RA is 
     * normalized to zero when the rounded DEC becomes +/- 90 degrees.
     * (Note: This is a different convention to the one employed for
     * converting Quaternions to Attitudes, where the POS is set to zero.)
     *
     * @param dp Number of decimal places required
     * @return Array containing [RA,DEC,POS] in degrees
     */
    public double[] roundedRaDecPos(int dp) {
	if((dp < 0) || (dp > 6)) {
	    throw new IllegalArgumentException("dp must be in the range [0,6]");
	}

	final double factor = Math.pow(10, dp);
	final int k    = (int)factor;
	final int pmax = 360 * k;
	final int dmax = 180 * k;
	
	double ra  = getRaDegrees();   // In the range [0, 360)
	double dec = getDecDegrees();  // In the range [-90, 90]
	double pos = getPosDegrees();  // In the range [0, 360]
	
	// First round DEC and offset to the range [0, 180]
	int d = (int)(Math.floor((dec + 90) * factor + 0.5));
	if( d > dmax)  d = dmax;
	if( d < 0)     d = 0;
	    
	int r = 0;
	int p = 0;
	
	// Simple case, not at a singularity
	if((d != 0) && (d != dmax)) {
	    r = (int)(Math.floor(ra * factor + 0.5));
	    p = (int)(Math.floor(pos * factor + 0.5));
	    if( r >= pmax ) r = 0;
	    if( p >= pmax ) p = 0;

	// Singularities
	} else if(d == 0) {
	    p = (int)(Math.floor((pos + ra) * factor + 0.5));
	    if( p >= pmax ) p -= pmax;

	} else {
	    p = (int)(Math.floor((pos - ra) * factor + 0.5));
	    if(p < 0) p += pmax;
	}

	// Return an array containing rounded RA, DEC and POS angles
	return new double[]{ (double)r / factor,
		             (double)d / factor - 90.0,
		             (double)p / factor };
    }
}
