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

import esa.esac.gusto.math.EulerAngles.Axes;
import esa.esac.gusto.time.Epoch;
import esa.esac.gusto.time.TaiTime;

/**
 * Conversions between astronomical coordinate systems.
 * 
 * @author  Jon Brumfitt
 */
public class Coordinates {
    private static final double SEC_PER_JYEAR = 365.25 * 86400;
    private static double JD_PER_JCENTURY = 36525.0;
    
    // Frame rotation for precession from B1950 (FK4) to J2000 (FK5).
    // Matrix from "Reconsidering the Galactic coordinate system", A&A 10-10-2010,
    // which is stated to have an accuracy of 0.1 milli-arcseconds.
    private static final Quaternion B1950_J2000 = new Matrix3(
		+0.999925679496d, +0.011181483239d, +0.004859003772d,
		-0.011181483221d, +0.999937484893d, -0.000027170294d,
		-0.004859003815d, -0.000027162595d, +0.999988194602d
	).toQuaternion();

    // Obliquity of ecliptic system of J2000.0 (From Astronomical Almanac 2012)
    private static final double OBLIQUITY = Math.toRadians(84381.406 / 3600.0);

    // Coordinates of Galactic pole (B1950) (Exact values by definition: IAU 1958)
    private static final double P1950 = Math.toRadians(192.25);  // Right ascension
    private static final double Q1950 = Math.toRadians(27.4);    // Declination
    private static final double R1950 = Math.toRadians(123.0);   // Position angle

    // Frame rotation from equatorial (B1950) to galactic coordinate system
    private static final Quaternion EQU1950_TO_GAL = EulerAngles.toQuaternion(Axes.ZYZ, 
	    R1950 - Math.PI, Q1950 - Math.PI/2, Math.PI * 2 - P1950);

    // Derived rotations between coordinate frames
    private static final Quaternion EQU_TO_ECL = Quaternion.xRotation(OBLIQUITY);
    private static final Quaternion EQU_TO_GAL = EQU1950_TO_GAL.multiply(B1950_J2000).conjugate();
    private static final Quaternion ECL_TO_GAL = EQU_TO_ECL.conjugate().multiply(EQU_TO_GAL);
    
    /**
     * Private constructor as this is a utility class.
     */
    private Coordinates() {
    }

    /**
     * Return position corrected for proper motion.<p>
     * 
     * Proper motion is the tangential velocity of a star. It is specified by components in
     * the right ascension and declination directions in the tangent plane. These are given
     * as angular velocities (in arcseconds per year) at the tangent point.<p>
     *
     * The J2000 position is given as a Direction, rather than a vector, so that the proper 
     * motion direction is well defined even at the poles.
     * 
     * @param dir J2000 coordinates
     * @param muA Component of proper motion in right ascension direction (arcseconds per year)
     * @param muD Component of proper motion in declination direction (arcseconds per year)
     * @param time Epoch of observation
     * @return Position vector at time
     */
    public static Vector3 correctProperMotion(Direction dir, double muA, double muD, TaiTime time) {
	if((muA == 0) && (muD == 0)) {
	    return new Vector3(dir);
	}
	double c = time.subtract(Epoch.J2000) / SEC_PER_JYEAR / 1000000L * Math.toRadians(1 / 3600.0);	
	Vector3 v = new Vector3(0, muA * c, muD * c);
	Quaternion qz = Quaternion.zRotation(dir.getAlpha());
	Quaternion qy = Quaternion.yRotation(-dir.getDelta());
	Vector3 v2 = qz.multiply(qy).rotateVector(v);
	
	return new Vector3(dir).add(v2).mNormalize();
    }
    
    /**
     * Rotation of J2000 (FK5) frame with respect to the B1950 (FK4) frame.<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>v2000 = b1950ToJ2000Frame().rotateAxes(v1950)</tt><br>
     * <tt>v1950 = b1950ToJ2000Frame().rotateVector(v2000)</tt><p>
     * 
     * Accurate precession of stars with proper motion from FK4 to FK5 requires a more
     * complex procedure than simply transforming the position with this Quaternion. 
     * For more information, see Seidelmann, "Explanatory Supplement to the Astronomical
     * Almanac", section 3.59.
     */
    public static final Quaternion b1950ToJ2000Frame() {
	return B1950_J2000.copy();
    }
    
    /**
     * Rotation of ecliptic frame with respect to the equatorial frame (J2000.0).<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>vEcl = equToEclFrame().rotateAxes(vEqu)</tt>
     */
    public static final Quaternion equToEclFrame() {
	return EQU_TO_ECL.copy();
    }
    
    /**
     * Rotation of equatorial frame (J2000) with respect to the ecliptic frame.<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>vEqu = eclToEquFrame().rotateAxes(vEcl)</tt>
     */
    public static final Quaternion eclToEquFrame() {
	return EQU_TO_ECL.conjugate();
    }

    /**
     * Rotation of galactic frame with respect to the equatorial frame (J2000).<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>vGal = equToGalFrame().rotateAxes(vEqu)</tt>
     */
    public static final Quaternion equToGalFrame() {
	return EQU_TO_GAL.copy();
    }
    
    /**
     * Rotation of equatorial frame (J2000) with respect to the galactic frame.<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>vEqu = galToEquFrame().rotateAxes(vGal)</tt>
     */
    public static final Quaternion galToEquFrame() {
	return EQU_TO_GAL.conjugate();
    }
    
    /**
     * Rotation of galactic frame with respect to the ecliptic frame.<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>vGal = eclToGalFrame().rotateAxes(vEcl)</tt>
     */
    public static final Quaternion eclToGalFrame() {
	return ECL_TO_GAL.copy();
    }
    
    /**
     * Rotation of ecliptic frame with respect to the galactic frame.<p>
     * 
     * To transform a vector (e.g star position) the <tt>rotateAxes()</tt> method
     * should be used, as follows:<p>
     * 
     * <tt>vGal = galToEclFrame().rotateAxes(vEcl)</tt>
     */
    public static final Quaternion galToEclFrame() {
	return ECL_TO_GAL.conjugate();
    }
    
    /**
     * Precess a vector from the given epoch to J2000.0.<p>
     * 
     * <tt>v2000 = Coordinates.precessToJ2000(vEpoch, epoch)</tt>
     * 
     * @param v Vector to be precessed
     * @param time Epoch as a TaiTime
     * @return Precessed vector
     */
    public static Vector3 precessToJ2000(Vector3 v, TaiTime time) {
	return precessionJ2000ToEpoch(time).rotateVector(v);
    }
    
    /**
     * Precess a vector to the given epoch from J2000.0.<p>
     * 
     * <tt>vEpoch = Coordinates.precessFromJ2000(v2000, epoch)</tt>
     * 
     * @param v Vector to be precessed
     * @param time Epoch as a TaiTime
     * @return Precessed vector
     */
    public static Vector3 precessFromJ2000(Vector3 v, TaiTime time) {
	return precessionJ2000ToEpoch(time).rotateAxes(v);
    }
    
    /**
     * Rotation of frame from equatorial J2000 to a specified epoch.<p>
     * 
     * To precess a vector (e.g star position) from J2000 to an epoch, the
     * <tt>rotateAxes()</tt> method should be used, as follows:<p>
     *
     * <tt>v2000 = precessionToJ2000(epoch).rotateVector(vEpoch)</tt>
     * 
     * @param time Epoch of observation
     * @return Precession as a rotation of the coordinate frame
     */
    public static Quaternion precessionJ2000ToEpoch(TaiTime time) {
	double t = time.subtract(Epoch.J2000) / 86400.0d / 1000000L / JD_PER_JCENTURY;
	
	double t2 = t * t;
	double t3 = t2 * t;
	double x = Math.toRadians(0.6406161 * t + 8.39E-5 * t2 + 5.0E-6 * t3);
	double z = Math.toRadians(0.6406161 * t + 3.041E-4 * t2 + 5.1E-6 * t3);
	double theta = Math.toRadians(0.5567530 * t - 1.185E-4 * t2 - 1.16E-5 * t3);
	
	return EulerAngles.toQuaternion(Axes.ZYZ, x, -theta, z).mConjugate();
    }
}



