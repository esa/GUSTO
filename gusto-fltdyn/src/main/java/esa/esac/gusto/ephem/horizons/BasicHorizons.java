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

package esa.esac.gusto.ephem.horizons;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.ephem.Bodies;
import esa.esac.gusto.ephem.Ephem;
import esa.esac.gusto.ephem.EphemerisException;
import esa.esac.gusto.ephem.State;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.TimeScale;
import esa.esac.gusto.util.Provider;

/**
 * Ephemerides of an SSO body with respect to the spacecaraft.
 *
 * @author  Jon Brumfitt
 */
public class BasicHorizons {
    private static final int DAY_SEC = 86400;        // Seconds per day
    private static final double C = 0.299792458E+06; // Speed of light (km/s)

    private static Mjd2000TimeFormat mjdTdbFmt = new Mjd2000TimeFormat(TimeScale.TDB);
    
    /**  Correction to be applied to state. */
    public enum Correction { 
	/** GEOMETRIC state (No correction) */
	NONE, 
	/** ASTROMETRIC state (Light Time correction only) */
	LT, 
	/** APPARENT state (Light Time and Stellar aberration correction) */
	LTS; }

    private Ephem _ephem;
    private Provider<Integer, HorizonsEphem> _ephemSet;

    /**
     * Create a new BasicHorizons object.<p>
     * 
     * The caller supplies a Provider object that acts as a source of Horizons
     * data for a given NAIFID.
     * 
     * The Provider may return null if data is not available for the specified 
     * naifid. In this case other methods of this class may throw an EphemerisException.
     * Alternatively, the Provider may throw an EphemerisException (or a subclass)
     * to provide a more specific error message.
     * 
     * @param provider A Provider that returns HorizonsEphem for a given naifid
     * @param ephem Ephemerides for spacecraft and planets
     */
    public BasicHorizons(Provider<Integer, HorizonsEphem> provider, Ephem ephem) {
	_ephemSet = provider;
	_ephem = ephem;
    }
    
    /**
     * Create a new BasicHorizons object.
     * 
     * The object must be initialized by calling 'init'.
     */
    protected BasicHorizons() {
    }
    
    /**
     * Initialize the object.<p>
     * 
     * See constructor for details of Provider.
     * 
     * @param provider A Provider that returns HorizonsEphem for a given naifid
     * @param ephem Ephemerides for spacecraft and planets
     */
    protected void init(Provider<Integer, HorizonsEphem> provider, Ephem ephem) {
	_ephemSet = provider;
	_ephem = ephem;
    }
    
    /**
     * Return the HorizonsEphem for a specified naifid.
     *  
     * @param naifid NAIFID of body
     * @return HorizonsEphem for the specified naifid
     * @throws EphemerisException if data is not available for the given naifid
     */
    private HorizonsEphem getHorizonsEphem(int naifid) {
	HorizonsEphem ephem = _ephemSet.get(naifid);
	if(ephem == null) {
	    throw new EphemerisException("No ephemeris found for NAIFID=" + naifid);
	}
	return ephem;
    }

    /**
     * Return the TimeInterval covered by the ephemerides.
     * 
     * @param naifid NAIFID of body
     * @return TimeInterval for which ephemerides are available
     * @throws EphemerisException if data is not available for the given naifid
     */
    public TimeInterval getTimeRange(int naifid) {
	HorizonsEphem reader = getHorizonsEphem(naifid);

	TaiTime start = mjdTdbFmt.mjd2000ToTaiTime(reader.getStartTime());
	TaiTime end   = mjdTdbFmt.mjd2000ToTaiTime(reader.getEndTime());
	
	return new TimeInterval(start, end);
    }
    
    /**
     * Return spacecraft-centric state of body.
     * 
     * @param naifid NAIFID of body
     * @param time Time
     * @param correct Correction to be applied to state
     * @return Position and velocity vectors of body with respect to spacecraft
     * @throws EphemerisException if data is not available for the given naifid
     */
    public State stateOf(int naifid, TaiTime time, Correction correct) {
	double tdb = mjdTdbFmt.TaiTimeToMjd2000(time);
	return stateOf(naifid, tdb, correct);
    }
    
    /**
     * Return spacecraft-centric state of body.
     * 
     * @param naifid NAIFID of body
     * @param tdb Time in MJD2000(TDB)
     * @param correct Correction to be applied to state
     * @return Position and velocity vectors of body with respect to spacecraft
     * @throws EphemerisException if data is not available for the given naifid
     */
    public State stateOf(int naifid, double tdb, Correction correct) {
	HorizonsEphem horizonsT = getHorizonsEphem(naifid);

	// Get barycentric state of spacecraft
	State sE = _ephem.barycentricState(tdb, Bodies.SPACECRAFT);

	// Newtonian correction for light-time and stellar aberration
	switch(correct) {
	    case LT:
	    case LTS: {

		// Initial approximation of light time
		State sT = horizonsT.interpolate(tdb);
		Vector3 pE = sE.position();
		Vector3 vE = sE.velocity();
		double lt = sT.position().subtract(pE).norm() / C;

		// Second approximation
		State sT1 = horizonsT.interpolate(tdb - lt / DAY_SEC);
		Vector3 pT1 = sT1.position();
		Vector3 r = pT1.subtract(pE);
		lt = r.norm() / C;

		// Final approximation
		State sT2 = horizonsT.interpolate(tdb - lt / DAY_SEC);
		r = sT2.position().subtract(pE);

		// Calculate apparent velocity
		Vector3 v = sT2.velocity().subtract(vE);

		// Correct for stellar aberration
		if(correct.equals(Correction.LTS)) {
		    Vector3 h = r.normalize().cross(vE).multiply(1.0 / C);
		    double phi = Math.asin(h.norm());
		    Quaternion q = new Quaternion(h, phi);
		    r = q.rotateVector(r);
	        }
	        return new State(r, v);
	    }
	
	    case NONE:
	    default: {
		State state = horizonsT.interpolate(tdb);
		return state.mSubtract(sE);
	    }
	}
    }

    /**
     * Return the radial velocity of a body in km/s.
     * 
     * @param naifid NAIFID of body
     * @param tdb Time in MJD2000(TDB)
     * @return Radial velocity in km/s (positive if moving apart)
     * @throws EphemerisException if data is not available for the given naifid
     */
    public double radialVelocity(int naifid, double tdb) {
	State state = stateOf(naifid, tdb, Correction.LTS);
	Vector3 r = state.position();
	Vector3 v = state.velocity();

	return v.dot(r.normalize());
    }
    
    /**
     * Return the radial velocity of a body in km/s.
     *
     * @param naifid NAIFID of body
     * @param time Time
     * @return Radial velocity in km/s (positive if moving apart)
     * @throws EphemerisException if data is not available for the given naifid
     */
    public double radialVelocity(int naifid, TaiTime time) {
	double tdb = mjdTdbFmt.TaiTimeToMjd2000(time);

	return radialVelocity(naifid, tdb);
    }
}

