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
 * Class implementing an astronomical pointing direction.<p>
 *
 * The direction is represented in Equatorial coordinates in terms of
 * Right Ascension and Declination.
 *
 * @author  Jon Brumfitt
 */

public class Direction {

    private static final double PI_2 = Math.PI / 2;

    protected double _alpha;    // Right ascension in radians [0, 2*pi)
    protected double _delta;    // Declination in radians     [-pi, pi]

    /**
     * Create a new Direction object, using the specified Right Ascension
     * and Declination (both in degrees).<p>
     *
     *   0.0 <= ra < 360.0 <p>
     *   -90.0 <= dec <= 90.0
     *
     * @param ra Right Ascension in degrees
     * @param dec Declination in degrees
     * @return The requested Direction
     */
    public static Direction fromDegrees(double ra, double dec) {
	return new Direction(Math.toRadians(ra), Math.toRadians(dec));
    }

    /**
     * Create a new Direction object with RA = 0 and DEC = 0.
     */
    public Direction() {
	_alpha = 0;
	_delta = 0;
    }

    /**
     * Create a new Direction object, using the specified Right Ascension
     * and Declination (both in radians).<p>
     *
     *   0.0 <= ra < 2*PI <p>
     *   PI/2 <= dec <= PI/2
     *
     * @param ra Right Ascension in radians
     * @param dec Declination in radians
     */
    public Direction(double ra, double dec) {
	if((dec > PI_2) || (dec < -PI_2)) {
	    throw new IllegalArgumentException("Declination out of range");
	}
	_alpha = ra;
	_delta = dec;
    }

    /**
     * Create a new Direction, which is a copy of another Direction.
     *
     * @param dir The Direction to be copied
     */
    public Direction(Direction dir) {
	_alpha = dir._alpha;
	_delta = dir._delta;
    }

    /**
     * Create a Direction from a vector.<p>
     * 
     * If v=[0,0,1] or [0,0,-1], the resulting right ascension is zero.
     *
     * @param v A vector
     */
    public Direction(Vector3 v) {
	double norm = v.norm();
	_alpha = Math.atan2(v.getY(), v.getX());
	if(_alpha < 0) {
	    _alpha += 2 * Math.PI;
	}
	_delta = Math.asin(v.getZ() / norm);
    }

    /**
     * Return the Right Ascension angle (in degrees).
     *
     * @return The Right Ascension in degrees
     */
    public double getRaDegrees() {
	double ra = Math.toDegrees(_alpha);
    
	while(ra >= 360.0) {
	    ra -= 360.0;
	}
	while(ra < 0.0) {
	    ra += 360.0;
	}
	return ra;
    }

    /**
     * Return the Declination angle (in degrees).
     *
     * @return The Declination in degrees
     */
    public double getDecDegrees() {
	return Math.toDegrees(_delta);
    }

    /**
     * Return the Right Ascension in radians.
     *
     * @return The Right Ascension in radians
     */
    public double getAlpha() {
	return _alpha;
    }

    /**
     * Return the Declination in radians.
     *
     * @return The Declination in radians
     */
    public double getDelta() {
	return _delta;
    }

    /**
     * Return cosine of angular separation of two points.
     *
     * @param dir The other Direction
     * @return Cosine of the angular separation
     */
    public double cosDistanceTo(Direction dir) {
	return Math.sin(_delta) * Math.sin(dir._delta) 
	    + Math.cos(_delta) * Math.cos(dir._delta)
	        * Math.cos(dir._alpha - _alpha);
    }

    /**
     * Return angular separation of two points (in radians).
     *
     * @param dir The other Direction
     * @return The angular separation in radians
     */
    public double distanceTo(Direction dir) {
    //  This gives better accuracy, for very small distances,
    //  than acos(cosDistanceTo()), but is slower.

	return MathUtils.ahavsin(MathUtils.havsin(dir._delta - _delta)
		+ Math.cos(dir._delta) * Math.cos(_delta)
		* MathUtils.havsin(dir._alpha - _alpha));
    }

    /**
     *  Return position angle of 'dir' relative to self.<p>
     *
     *  Position angles are measured anticlockwise from North
     *  (i.e. declination = 90) direction. Returns zero when
     *  the points coincide exactly.
     *
     * @param dir The other Direction
     * @return Position angle in radians
     */
    public double positionAngleTo(Direction dir) {
	double x = Math.sin(dir._alpha - _alpha) * Math.cos(dir._delta);
	
	double y = Math.cos(_delta) * Math.sin(dir._delta) 
	    - Math.cos(dir._alpha - _alpha) * Math.sin(_delta)
	        * Math.cos(dir._delta);
	
	return Math.atan2(x,y);
    }

    /**
     * Return new direction offset by theta (radians) in direction
     * given by position angle chi (radians).<p>
     *
     * <b>Warning:</b> This function should be used with care when this
     * Direction is very close to a pole (Declination = +/- 90 degrees).
     *
     * @param theta Angular distance to other point
     * @param chi Position angle of other point with respect to this one
     * @return Direction of other point
     */
    public Direction offsetBy(double theta, double chi) {

	// This is an exact calculation, which works even at the poles.

	double costheta = Math.cos(theta);
	double sintheta = Math.sin(theta);

	double coschi = Math.cos(chi);
	double sinchi = Math.sin(chi);

	double cos_delta = Math.cos(_delta);
	double sin_delta = Math.sin(_delta);

	double cos_alpha = Math.cos(_alpha);
	double sin_alpha = Math.sin(_alpha);

	double delta = Math.asin(costheta * sin_delta 
			    + sintheta * cos_delta * coschi);

	double sinacosd = costheta * cos_delta * sin_alpha
	    + sintheta * (cos_alpha * sinchi 
			  - sin_delta * coschi * sin_alpha);

	double cosacosd = costheta * cos_delta * cos_alpha
	    - sintheta * (sin_alpha * sinchi 
			  + sin_delta * coschi * cos_alpha);
	
	double alpha = Math.atan2(sinacosd, cosacosd);

	if(alpha < 0.0)
	    alpha += Math.PI * 2.0;

	Direction d = new Direction();
	d._alpha = alpha;
	d._delta = delta;

	return d;
    }

    /**
     * Return a String representation of this object.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this object
     */
    public String toString() {

	// Round the RA & DEC to 5 decimal places

	int    k      = 100000;     // 5 decimal places
	double factor = (double)k;
	int    rmax   = 360 * k;
	int    dmax   = 90 * k;
    
	int r = (int)(Math.floor(getRaDegrees() * factor + 0.5));
	int d = (int)(Math.floor((getDecDegrees() + 90.0) * factor + 0.5));
    
	if( r >= rmax ) r = 0;
	d -= dmax;
	if( d > dmax)  d = dmax;
	if( d < -dmax) d = -dmax;

	double ra  = (double)(r / factor);
	double dec = (double)(d / factor);

	return ra + " " + dec;
    }
}

