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

package esa.esac.gusto.ephem.sites.test;

import esa.esac.gusto.ephem.sites.EarthSites;
import esa.esac.gusto.ephem.sites.Ellipsoid;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test harness for EarthSites class.
 * 
 * @author  Jon Brumfitt
 */
public class EarthSitesTest extends TestCase {
    private static final String LS = System.getProperty("line.separator", "\n");    
    private static final SimpleTimeFormat UTC = new SimpleTimeFormat(TimeScale.UTC);
    private static final double WGS84_MAJOR = 6378.137000;
    private static final double WGS84_MINOR = 6356.752314;

    @Test
    public void testConstructors() {
	InputStream is = getClass().getResourceAsStream("/esa/esac/gusto/ephem/sites/ground_stations.xml");
	EarthSites stations = new EarthSites(is);
	Assert.assertNotNull(stations);
    }

    @Test(expected=IOException.class)
    public void testConstructors2() throws IOException {
	File f = getResourceAsFile("/esa/esac/gusto/ephem/sites/ground_stations.xml", "ground_stations.xml");
	EarthSites stations = new EarthSites(f.getName());
	Assert.assertNotNull(stations);
    }

    @Test
    public void testUnknownStation() {
	EarthSites stations = new EarthSites();
	Vector3 v = stations.getVector("XYZ");
	Assert.assertNull(v);
    }

    @Test
    public void testVectors() {
	EarthSites stations = new EarthSites();
	Vector3 v1 = stations.getVector("NNO");
	// Reference result from MOC
	Vector3 v1ref = new Vector3(-2414.066824, 4907.869366, -3270.605535);
	Assert.assertTrue(v1.epsilonEquals(v1ref, 1E-6));

	Vector3 v2 = stations.getVector("CEB");
	// Reference result from MOC
	Vector3 v2ref = new Vector3( 4846.7339530, -370.1747836, 4116.8788162);
	Assert.assertTrue(v2.epsilonEquals(v2ref, 1E-6));
    }

    @Test
    public void testGmst() {
	// GMST reference epoch is 2000-01-01T12:00:00 UT 
	// (UTC is correct to within 0.9s)
	// At this time the GMST should be tEpoch
	TaiTime epoch = UTC.parse("2000-01-01T12:00:00Z");
	double tEpoch = 18.697374558;

	EarthSites stations = new EarthSites();
	double gmst = stations.gmst(epoch);
	Assert.assertEquals(gmst, tEpoch, 1E-10);

	// Check one date after epoch
	TaiTime time = UTC.parse("2000-01-02T12:00:00Z");
	// One UT1 day in sidereal hours
	double day = 24.06570982441908;
	double gmst2 = stations.gmst(time);
	double tExp2 = tEpoch + day- 24;
	Assert.assertEquals(gmst2, tExp2, 1E-10);
    }

    @Test
    public void testAddStation() {
	// Add a ficticious ground station
	EarthSites stations = new EarthSites();
	stations.addStation("XYZ", "Test station", 50, 30, 1);
	Ellipsoid ref = new Ellipsoid(WGS84_MAJOR, WGS84_MINOR);
	Vector3 v = stations.getVector("XYZ");
	Vector3 vExp = ref.vectorFor(50, 30, 1);
	Assert.assertTrue(v.epsilonEquals(vExp, 1E-10));
    }

    @Test
    public void testGroundStationOffset() {
	// Rather than simply repeating the calculation, we project the
	// vectors from getVector and groundStationOffset onto the equatorial
	// plane and compare the angle between them with the GMST.
	EarthSites stations = new EarthSites();
	TaiTime time = UTC.parse("2000-01-01T12:00:00Z");
	double gmst = stations.gmst(time);
	Vector3 v = stations.position("NNO", time);
	Vector3 v1 = stations.getVector("NNO");
	Vector3 vxy = new Vector3(v.getX(), v.getY(), 0);
	Vector3 v1xy = new Vector3(v1.getX(), v1.getY(), 0);
	double hours = 24 - Math.toDegrees(vxy.angle(v1xy)) / 15;
	Assert.assertEquals(hours, gmst, 1E-6);
    }
    
    @Test
    public void testState(){
	EarthSites stations = new EarthSites();
	TaiTime t1 = UTC.parse("2010-01-01T00:00:00Z");
	int dt = 1;
	String site = "NNO";
	
	// Estimate velocity from change in position in time 'dt'
	TaiTime t2 = t1.addSeconds(dt);
	Vector3 p1 = stations.position(site, t1);
	Vector3 p2 = stations.position(site, t2);
	Vector3 ve = p2.subtract(p1).multiply(1.0d / dt);
	
	// Calculate state and compare it with estimate
	Vector3[] s = stations.state(site, t1);
	Assert.assertTrue(p1.epsilonEquals(s[0], 1E-15));
	Assert.assertTrue(ve.epsilonEquals(s[1], 2E-5));
    }

    /**
     * Get a resource as a file.
     * Cannot use getResource() as it does not work when it is a jar file.
     */
    private static File getResourceAsFile(String name, String fileName) 
    throws IOException {
	InputStream is = EarthSitesTest.class.getResourceAsStream(name);
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	File file = new File(fileName);
	file.deleteOnExit();
	FileWriter writer = new FileWriter(file);
	String line;
	while((line = reader.readLine()) !=null) {
	    writer.write(line + LS);
	}
	writer.close();
	return file;        
    }     
}

