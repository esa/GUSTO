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

package esa.esac.gusto.ephem.sites;

import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Earth sites such as ground stations relative to centre of Earth.<p>
 * 
 * The computed positions are approximate as they do not take into account the 
 * equation of the equinoxes (<1.1s), the difference between UT1 and UTC (<0.9s) 
 * and offset of WGS84 meridian from the Greenwich meridian (5.31 arcsec).

 * @author  Jon Brumfitt
 */
public class EarthSites {

    // EARTH reference ellipsoid
    private static final double WGS84_MAJOR = 6378.137000;
    private static final double WGS84_MINOR = 6356.752314;

    private static final SimpleTimeFormat UTC = new SimpleTimeFormat(TimeScale.UTC);

    private Map<String, Vector3> _map;
    private XmlEarthSiteReader _reader;
    private Ellipsoid _wgs84 = new Ellipsoid(WGS84_MAJOR, WGS84_MINOR);

    /**
     * Create a new set of Earth sites from a file.
     */
    public EarthSites(String fileName) {
	_map = new HashMap<String, Vector3>();
	File file = new File(fileName);
	_reader = new XmlEarthSiteReader(this);
	_reader.readFile(file);
    }

    public EarthSites(InputStream is) {
	_map = new HashMap<String, Vector3>();
	_reader = new XmlEarthSiteReader(this);
	_reader.readFile(is);
    }

    public EarthSites() {
	InputStream is = getClass().getResourceAsStream("/esa/esac/gusto/ephem/sites/ground_stations.xml");
	_map = new HashMap<String, Vector3>();
	_reader = new XmlEarthSiteReader(this);
	_reader.readFile(is);
    }

    /**
     * Add an Earth site, given WGS-84 geodetic coordinates.
     *
     * @param name Ground-station identifier (e.g. "NNO")
     * @param longitude Longitude in degrees
     * @param latitude Geodetic latitude in degrees
     * @param height Height above reference ellipsoid in metres
     */
    public void addStation(String id, String name, double longitude, 
	    double latitude, double height) {

	Vector3 v = _wgs84.vectorFor(longitude, latitude, height);
	//System.out.println(id + " " + v);

	_map.put(id, v);
    }

    /**
     * Return Position of station relative to centre of Earth.
     * The coordinate system is geographic.
     *
     * @param name Ground-station identifier (e.g. "NNO")
     * @return Vector (km)
     */
    public Vector3 getVector(String name) {
	return _map.get(name);
    }

    /**
     * Return position vector from centre of Earth to the site.
     *
     * @param name Ground-station identifier (e.g. "NNO")
     * @param time The time at which offset is required
     */
    public Vector3 position(String name, TaiTime time) {
	Vector3 v = getVector(name);
	double gmst = gmst(time);
	Quaternion q = Quaternion.zRotation(Math.toRadians(gmst * 15.0));

	return q.rotateVector(v);
    }
    
    /**
     * Return state of station with respect to centre of Earth.<p>
     * 
     * GAST is assumed equal to GMST, resulting in an error of up to 1.1s.
     * UTC is assumed to be the same at UT1, resulting in an error of up to 0.9s.
     * 
     * @param name Ground-station identifier (e.g. "NNO")
     * @param time The time at which offset is required
     */
    public Vector3[] state(String name, TaiTime time) {
	final double omega = Math.toRadians(24.06570982441908d * 15.0d / 86400);  // Earth rotation (rad/s)
	
	Vector3 p = position(name, time);
	Vector3 v = new Vector3(-p.getY(), p.getX(), 0).mMultiply(omega);
	
	return new Vector3[] {p, v};
    }

    /**
     * Convert time to Greenwich Mean Sidereal Time.
     *
     * UTC is assumed to be the same at UT1, resulting in an error of less than 0.9s.
     *
     * @param t Time as a TaiTime
     * @return Greenwich Mean Sidereal Time
     */
    public double gmst(TaiTime t) {
	final TaiTime epoch = UTC.parse("2000-01-01T12:00:00Z");
	double dt = t.subtract(epoch) / 1000000.0 / 86400.0;
	double h = 18.697374558 + 24.06570982441908 * dt;
	double h1 = h - (((int)h/24) * 24);
	return (h1 >= 0) ? h1 : (h1 + 24);
    }
}

