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

package esa.esac.gusto.ephem.de405;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import esa.esac.gusto.ephem.Bodies;
import esa.esac.gusto.ephem.EphemerisException;
import esa.esac.gusto.ephem.PlanetaryEphem;
import esa.esac.gusto.ephem.State;

/**  
 * Obtain planetary ephemerides using JPL DE405 files.<p>
 * 
 * The input file is an ASCII DE405 ephemeris file (from JPL), without the
 * header.405 header attached. For example, the file ascp2000.405, which covers
 * the 20-year period from 2000 to 2020. Consecutive files may be appended for
 * longer time periods and complete intervals may be trimmed from the start and
 * end of the resulting file if required.
 *
 * @author Jon Brumfitt
 */
public class De405Ephemerides implements PlanetaryEphem {
    
    /* The DE405 file uses the following internal numbering for bodies.
     * See the Bodies interface for the numbering used for the 'body' argument.
     * 
     *  1  Mercury
     *  2  Venus
     *  3  Earth-Moon barycentre
     *  4  Mars
     *  5  Jupiter
     *  6  Saturn
     *  7  Uranus
     *  8  Neptune
     *  9  Pluto
     * 10  Moon (Geocentric)
     * 11  Sun
     * 12  Nutations (not used)
     * 13  Librations (not used)
     */
    
    // Ratio of mass of Earth to mass of Moon
    private static final double EM_RATIO = 81.3005600000000044d;
    
    // Length in days of each interval of the DE405 file.
    private static final int DAYS_PER_INTERVAL = 32;
    
    private De405Reader _reader;
    private double _startTime;  // Start time of first interval
    private double _endTime;    // End time of last interval

    /**
     * Create a new De405Ephemerides reader.
     * 
     * @param fileName Name of the ASCII DE405 file
     */
    public De405Ephemerides(String fileName) {
	this(fileName, Double.NaN, Double.NaN);
    }
    
    /**
     * Create a new De405Ephemerides reader.
     * 
     * @param is InputStream to be read
     */
    public De405Ephemerides(InputStream is) {
	this(is, Double.NaN, Double.NaN);
    }

    /**
     * Create a new De405Ephemeris reader for a specified time range.<p>
     * 
     * The time range arguments are used to load a subset of the data
     * to reduce memory usage. These arguments are currently ignored.
     * 
     * @param Name of file
     * @param tStart Start of time range [MJD2000(TDB)]
     * @param tEnd End of time range [MJD2000(TDB)]
     */
    public De405Ephemerides(String fileName, double tStart, double tEnd) {
	try {
	    FileInputStream is = new FileInputStream(fileName);
	    _reader = new De405Reader();
	    _reader.readFile(is);  
	    _startTime = _reader.getStartTime();
	    _endTime = _reader.getEndTime();
	} catch(IOException e) {
	    throw new EphemerisException("Cannot read DE405 file " + fileName, e);
	}
    }
    
    /**
     * Create a new De405Ephemeris reader.<p>
     * 
     * The time range arguments are used to load a subset of the data
     * to reduce memory usage. These arguments are currently ignored.
     * 
     * @param is InputStream    
     * @param tStart Start of time range [MJD2000(TDB)]
     * @param tEnd End of time range [MJD2000(TDB)]
     */
    public De405Ephemerides(InputStream is, double tStart, double tEnd) {
	try {
	    _reader = new De405Reader();
	    _reader.readFile(is);
	    _startTime = _reader.getStartTime();
	    _endTime = _reader.getEndTime();
	} catch(IOException e) {
	    throw new EphemerisException("Cannot read DE405 stream", e);
	}
    }

    /**
     * Return the earliest time available MJD2000(TDB).
     * 
     * @return The start time of the data
     */
    public double getStartTime() {
        return _startTime;
    }
    
    /**
     * Return the latest time available MJD2000(TDB).
     * 
     * @return The start time of the data
     */
    public double getEndTime() {
        return _endTime;
    }
  
    /**
     * Return the barycentric state of the Earth.
     * 
     * @param tdb Time (MJD2000 TDB)
     * @return The state vector
     */
    private State baryEarth(double tdb) {
	State e = state(tdb, 3);  // Earth-Moon barycentre
	State m = state(tdb, 10); // Moon geocentric
	double f = 1 / (1 + EM_RATIO);
	return e.mSubtract(m.mMultiply(f));
    }
    
    /**
     * Return the barycentric state of the Moon.
     * 
     * @param tdb Time (MJD2000 TDB)
     * @return The state vector at the specified time
     */
    private State baryMoon(double tdb) {
	State e = state(tdb, 3);  // Earth-Moon barycentre
	State m = state(tdb, 10); // Moon geocentric
	double f = 1 / (1 + EM_RATIO);
	e.mSubtract(m.multiply(f));
	return m.mAdd(e);
    }

    /**
     * Return the geocentric state of the Earth-Moon barycentre.
     * 
     * @param tdb Time (MJD2000 TDB)
     * @return The state vector at the specified time
     */
    private State geoEmBary(double tdb) {
	State m = state(tdb, 10);  // Moon geocentric
	double f = 1 / (1 + EM_RATIO);
	return m.mMultiply(f);
    }
    
    /**
     * Return the geocentric geometric state of a body.
     * 
     * @param tdb Time (MJD2000 TDB)
     * @param body The required body (see Bodies interface)
     * @return The geocentric state vector at the specified time
     */
    public State geocentricState(double tdb, int body) {
	switch(body) {
	case Bodies.EARTH:
	    return new State();
	    
	case Bodies.MOON:
	    return state(tdb, 10);
	    
	case Bodies.EM_BARY:
	    return geoEmBary(tdb);
	    
	case Bodies.SS_BARY:
	    return barycentricState(tdb, Bodies.EARTH).mNegate();
	    
	default: // Planet or SUN
	    State bodyBary  = barycentricState(tdb, body);
	    State earthBary = barycentricState(tdb, Bodies.EARTH);
	    return bodyBary.subtract(earthBary);
	}
    }
    
    /**
     * Return the geometric barycentric state of a body.
     * 
     * @param tdb Time (MJD2000 TDB)
     * @param body The required body (see Bodies interface)
     * @return The barycentric state vector at the specified time
     */
    public State barycentricState(double tdb, int body) {
	switch(body) {
	case Bodies.EARTH:
	    return baryEarth(tdb);
	    
	case Bodies.MOON:
	    return baryMoon(tdb);
	    
	case Bodies.EM_BARY:
	    return state(tdb, 3);
	    
	case Bodies.SS_BARY:
	    return new State();	    
	    
	default:  // Planet or SUN
	    return state(tdb, body);
	}
    }

    /**
     * Calculates state of a body at a specified time.<p>
     * 
     * The <tt>de405Body</tt> is the internal body number used within the
     * DE405 data. The resulting state may be Solar-System barycentric, 
     * Earth-Moon barycentric or geocentric, depending on the body.
     *
     * @param mjd2000 Time MJD2000(TDB)
     * @param de405Body Body using DE405 internal numbering convention
     * @return State at the specified time
     */
    private State state(double mjd2000, int de405Body) {

	// Find the correct interval
	double s = _reader.getStartTime();
	int interval = (int)(Math.floor((mjd2000 - s) / DAYS_PER_INTERVAL));
	
	De405Interval iv = _reader.getInterval(interval);
	return iv.state(mjd2000, de405Body);
    }
}

