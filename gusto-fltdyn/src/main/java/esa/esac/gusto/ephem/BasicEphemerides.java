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

package esa.esac.gusto.ephem;
 
import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.PhysicalConstants;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.TimeScale;
 
/**
 * Ephemerides for planets and spacecraft.<p>
 * 
 * This class combines the orbit and planetary ephemerides, such that it is
 * possible to request the spacecraft-centric state of a planet, for example.
 *
 * @author  Jon Brumfitt
 */

public class BasicEphemerides implements Ephem {

    private static Mjd2000TimeFormat mjdTdbFmt = new Mjd2000TimeFormat(TimeScale.TDB); 
    private static final double C = PhysicalConstants.C / 1000; // Speed of light (km/s)
    private PlanetaryEphem _planets;
    private EphemerisBlock _orbit;

    /**
     * Create a new Ephemerides reader.
     */
    public BasicEphemerides(EphemerisBlock orbit, PlanetaryEphem planets) {
	_planets = planets;
	_orbit = orbit;
    }

    /**
     * Return spacecraft-centric geometric state of a specified body.
     *
     * @param time Time of required ephemeris
     * @param body Body for which ephemeris is required
     * @return State vector from spacecraft to body
     */
    public State spacecraftTo(TaiTime time, int body) {
	double tdb2000 = mjdTdbFmt.TaiTimeToMjd2000(time);
	return spacecraftTo(tdb2000, body); 
    }

    /**
     * Return spacecraft-centric geometric state of a specified body.
     *
     * @param tdb2000 Time of required ephemeris MJD2000(TDB)
     * @param body Body for which ephemeris is required
     * @return State vector from spacecraft to body
     */
    public State spacecraftTo(double tdb2000, int body) {
	State scGeo = _orbit.interpolate(tdb2000);

	switch(body) {
	case EARTH:
	    return scGeo.mNegate();
	    
	case SPACECRAFT: 
	    return new State();

	default:
	    State bodyGeo = _planets.geocentricState(tdb2000, body);
	    return bodyGeo.subtract(scGeo);
	}
    }

    /**
     * Return barycentric geometric state of a specified body.
     */
    public State barycentricState(double tdb2000, int body) {
	if(body == SPACECRAFT) {
	    return spacecraftTo(tdb2000, SS_BARY).mNegate(); // (h,ssb)
	} else {
	    return _planets.barycentricState(tdb2000, body);
	}
    }

    /**
     * Return barycentric geometric state of a specified body.
     */
    public State barycentricState(TaiTime time, int body) {
	double tdb2000 = mjdTdbFmt.TaiTimeToMjd2000(time);
	return barycentricState(tdb2000, body);
    }

    /**
     * Return geocentric geometric state of a specified body.
     */
    public State geocentricState(double tdb2000, int body) {
	if(body == SPACECRAFT) {
	    return _orbit.interpolate(tdb2000);
	} else {
	    return _planets.geocentricState(tdb2000, body);
	}
    }

    /**
     * Return geocentric geometric state of a specified body.
     */
    public State geocentricState(TaiTime time, int body) {
	double tdb2000 = mjdTdbFmt.TaiTimeToMjd2000(time);
	return geocentricState(tdb2000, body);
    }

    /**
     * Return the earliest time for which ephemerides are available.
     *
     * @return Earliest time in MJD2000(TDB)
     */
    public double getStartTime() {
	return Math.max(_planets.getStartTime(), _orbit.getStartTime());
    }

    /**
     * Return the latest time for which ephemerides are available.
     *
     * @return Earliest time in MJD2000(TDB)
     */
    public double getEndTime() {
	return Math.min(_planets.getEndTime(), _orbit.getEndTime());
    }

    /**
     * Return the TimeInterval covered by the ephemerides.
     */
    public TimeInterval getTimeRange() {
	TaiTime start = mjdTdbFmt.mjd2000ToTaiTime(getStartTime());
	TaiTime end   = mjdTdbFmt.mjd2000ToTaiTime(getEndTime());
	
	return new TimeInterval(start, end);
    }
    
    /**
     * Non-relativistic correction for stellar aberration seen from spacecraft.
     * 
     * @param target Uncorrected position of target
     * @param time Time at which target is observed
     * @return Position corrected for stellar aberration seen from spacecraft
     */
    public Vector3 correctAberration(Vector3 target, TaiTime time) {
	Vector3 vsc = barycentricState(time, Ephemerides.SPACECRAFT).velocity();
	Vector3 h = target.normalize().cross(vsc).multiply(1.0 / C);
	double phi = Math.asin(h.norm());
	Quaternion q = new Quaternion(h, phi);
	return q.rotateVector(target);
    }
    
    /**
     * Return the radial velocity with respect a specified red-shift frame.<p>
     * 
     * Porting note: The version in the HCSS pointing package performs the conversion
     * from reference position to offset ACA. This new implementation assumes this
     * has already been done. Also, the HCSS version handles frame==null.
     * 
     * @param r Position of target
     * @param time Time of observation
     * @param frame The reference frame for the red-shift
     * @return Radial velocity in km/s (positive if distance increasing)
     */
    public double radialVelocity(Vector3 r, TaiTime time, RedshiftFrame frame) {
	Vector3 v = null;
	
	switch(frame) {
	    case GEOCENTRIC:
	        v = relativeVelocity(time, EARTH);
	        break;
	    case HELIOCENTRIC:
	        v = relativeVelocity(time, Ephemerides.SUN);
	        break;
	    case LSR: {
		Vector3 vSsb = relativeVelocity(time, Ephemerides.SS_BARY);
		v = vSsb.subtract(kinematicLsr());
		break;
	    }
  	    default:
	        throw new IllegalArgumentException("Unsupported redshiftframe: " + frame);
	}
	
	return v.dot(r.normalize());
    }

    /**
     * Return the velocity of Solar System Barycentre relative to kinematic LSR.
     * 
     * @return Velocity vector (km/s)
     */
    public static Vector3 kinematicLsr() {
	// Constants from http://www.gb.nrao.edu/~fghigo/gbtdoc/doppler.html
	final double LSR_RA  = 270.9595417;  // 18h03m50.29s
	final double LSR_DEC = 30.00466667;  // 30d00m16.8s
	final double LSR_VEL = 20.0;         // Velocity (km/s)
	
	Direction dir = Direction.fromDegrees(LSR_RA, LSR_DEC);
	return (new Vector3(dir)).mNormalize().mMultiply(LSR_VEL);
    }
    
    /**
     * Return the velocity of a body relative to the spacecraft.
     */
    private Vector3 relativeVelocity(TaiTime time, int body) {
	return spacecraftTo(time, body).velocity();
    }

    /**
     * Return a String representation of this object.
     */
    public String toString() {
	TaiTime start = mjdTdbFmt.mjd2000ToTaiTime(getStartTime());
	TaiTime end =   mjdTdbFmt.mjd2000ToTaiTime(getEndTime());

	return "Ephemerides for period:\n" + start + " to\n" + end;
    }
}
    

