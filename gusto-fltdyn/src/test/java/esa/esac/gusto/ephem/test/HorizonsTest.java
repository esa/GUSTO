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

package esa.esac.gusto.ephem.test;

import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.ephem.BasicEphemerides;
import esa.esac.gusto.ephem.Bodies;
import esa.esac.gusto.ephem.Ephem;
import esa.esac.gusto.ephem.EphemerisException;
import esa.esac.gusto.ephem.State;
import esa.esac.gusto.ephem.de405.De405Ephemerides;
import esa.esac.gusto.ephem.horizons.BasicHorizons;
import esa.esac.gusto.ephem.horizons.BasicHorizons.Correction;
import esa.esac.gusto.ephem.horizons.HorizonsEphem;
import esa.esac.gusto.ephem.horizons.HorizonsReader;
import esa.esac.gusto.ephem.oem.CcsdsOemReader;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.TimeScale;
import esa.esac.gusto.util.LRUCachedProvider;
import esa.esac.gusto.util.Provider;

/**
 * Test harness for Horizons classes.
 *
 * @author  Jon Brumfitt
 */
public class HorizonsTest {
    
    private static final boolean DEBUG = false;

    private static final String OEM_FILE = "data/H20100427_0001_short.LOE";
    private static final String DE405_FILE = "data/ascp_short.405";
    private static final String SSO_FILE = "data/6";
    
    // Tolerance used for comparison with expected result
    private static final double MAX_POS = 1E-3;  // km
    private static final double MAX_VEL = 1E-6;  // km/s
    private static final double MAX_ANG = 1E-6;  // arcseconds

    private static Ephem _ephem;
    private static BasicHorizons _horizons;
    
    private static SimpleTimeFormat formatter = new SimpleTimeFormat(TimeScale.TDB);
    private static Mjd2000TimeFormat mjdTdbFmt = new Mjd2000TimeFormat(TimeScale.TDB);

    static {
	formatter.setDecimals(6);
    }
    
    @BeforeClass
    public static void oneTimeSetUp() {		
	InputStream de405 = HorizonsTest.class.getResourceAsStream(DE405_FILE);
	InputStream oem = HorizonsTest.class.getResourceAsStream(OEM_FILE);
	
	_ephem = new BasicEphemerides(new CcsdsOemReader(oem), new De405Ephemerides(de405));

	Provider<Integer, HorizonsEphem> reader = new Provider<Integer, HorizonsEphem>() {
	    public HorizonsEphem get(Integer naifid) {
		InputStream sso = HorizonsTest.class.getResourceAsStream("data/" + naifid);
		return new HorizonsReader(sso);
	    }
	};
	Provider<Integer, HorizonsEphem> cachedReader = 
	    new LRUCachedProvider<Integer, HorizonsEphem>(reader, false);
	_horizons = new BasicHorizons(cachedReader, _ephem);
    }

    @AfterClass
    public static void oneTimeTearDown() {
	_ephem = null;
	_horizons = null;
    }

    /**
     * Test exception when data for NAIFID does not exist.
     */
    @Test(expected=EphemerisException.class)
    public void test0() {
	int naifid = 999;  // NAIFID for which there is no data
	_horizons.getTimeRange(naifid);
    }
    
    /*
     * Test Horizons by comparing spacecraft-centric state of a planet obtained 
     * using Horizons with state obtained using Ephemerides class.
     */
    @Test
     public void test1() {

	 // Saturn barycenter
	 int body = Bodies.SATURN;
	 int naifid = 6;

	 TimeInterval range = _horizons.getTimeRange(naifid);
	 double tstart = mjdTdbFmt.TaiTimeToMjd2000(range.start());
	 double tend = mjdTdbFmt.TaiTimeToMjd2000(range.finish());       

	 Assert.assertTrue(tend - tstart > 10);
	 
	 double maxPos = 0;
	 double maxVel = 0;
	 double maxAng = 0;

	 // Repeat for a large number of different times
	 double tdb = tstart;
	 while(tdb < tend) {
	     TaiTime time = mjdTdbFmt.mjd2000ToTaiTime(tdb);

	     // Find state using Horizons file
	     State hs = _horizons.stateOf(naifid, tdb, Correction.NONE);

	     // Find state using Ephemerides class (DE405)
	     State es = _ephem.spacecraftTo(time, body); 

	     double d = hs.position().subtract(es.position()).norm();
	     double v = hs.velocity().subtract(es.velocity()).norm();
	     double a = Math.toDegrees(hs.position().angle(es.position())) * 3600.0;	  

	     // Calculate worst case
	     if(d > maxPos) { maxPos = d; }
	     if(v > maxVel) { maxVel = v; }	   
	     if(a > maxAng) { maxAng = a; }

	     tdb += 0.001; // Step in time in days
	 }
	 if(DEBUG) {
	     System.out.println("test1: maxPos = " + maxPos + "km  maxVel=" + maxVel
		              + "km/s  maxAng=" + maxAng + " arcsec");
	 }
	 
	 Assert.assertTrue(maxPos < MAX_POS);
	 Assert.assertTrue(maxVel < MAX_VEL);
	 Assert.assertTrue(maxAng < MAX_ANG);
     }

     /**
      * Test HorizonsReader by comparing barycentric state of a planet obtained using
      * Horizons with state obtained using DE405.
      */
    @Test
     public void test2() throws IOException {

	 // Saturn barycentre
	 int body = Bodies.SATURN;

	 InputStream is = HorizonsTest.class.getResourceAsStream(SSO_FILE);
	 HorizonsReader reader = new HorizonsReader(is);
	 is.close();

	 double tstart = reader.getStartTime();
	 double tend = reader.getEndTime();

	 Assert.assertTrue(tend - tstart > 10);

	 double maxPos = 0;
	 double maxVel = 0;
	 double maxAng = 0;

	 // Repeat for a large number of different times
	 double tdb = tstart;
	 while(tdb < tend) {
	     TaiTime time = mjdTdbFmt.mjd2000ToTaiTime(tdb);

	     // Find state using Horizons file
	     State hs = reader.interpolate(tdb);

	     // Find state using Ephemerides (DE405)
	     State es = _ephem.barycentricState(time, body);

	     double d = hs.position().subtract(es.position()).norm();
	     double v = hs.velocity().subtract(es.velocity()).norm();
	     double a = Math.toDegrees(hs.position().angle(es.position())) * 3600.0;	
	     
	     // Calculate worst case
	     if(d > maxPos) { maxPos = d; }
	     if(v > maxVel) { maxVel = v; }	   
	     if(a > maxAng) { maxAng = a; }

	     tdb += 0.001; // Step in time in days
	 }
	 if(DEBUG) {
	     System.out.println("test2: maxPos = " + maxPos + "km  maxVel=" + maxVel
		              + "km/s  maxAng=" + maxAng + " arcsec");
	 }
	 
	 Assert.assertTrue(maxPos < MAX_POS);
	 Assert.assertTrue(maxVel < MAX_VEL);
	 Assert.assertTrue(maxAng < MAX_ANG);
     }

     /**
      * Compare APPARENT state of planet barycenter with spacecraft-centric 
      * value obtained from JPL Horizons.
      */
    @Test
     public void test3() {
	 int naifid = 6; // Saturn barcenter

	 TaiTime ft = formatter.parse("2010-01-15T00:00:00TDB");
	 double tdb = mjdTdbFmt.TaiTimeToMjd2000(ft);
	 
	 // Expected values, from Horizons: Time=2010-01-15T00:00:00(CT)
	 // CENTER=500@-486, COMMAND=6, VECT_CORR=LT+S (source H20111011_0001.LOE)
	 Vector3 pe = new Vector3(-1.355602858867110E+09,
		                  -1.216198913429524E+08,
		                   8.093800097202687E+06);
	 Vector3 ve = new Vector3(2.712350391180988E+01,
		                  2.517945682813838E+00,
		                  1.472028155251321E+00);
	 
	 State vh  = _horizons.stateOf(naifid, tdb, Correction.LTS);
	 State vh1 = _horizons.stateOf(naifid, ft,  Correction.LTS);
	 Assert.assertTrue(equals(vh, vh1, 1E-15));

	 double dp = vh.position().subtract(pe).norm();
	 double dv = vh.velocity().subtract(ve).norm();
	 double da = Math.toDegrees(vh.position().angle(pe)) * 3600.0;
	 
	 if(DEBUG) {
	     System.out.println("test3: maxPos = " + dp + "km  maxVel=" + dv
	                      + "km/s  maxAng=" + da + " arcsec");
	 }
	 
	 // The position error is larger than expected but still well
	 // within acceptable limits.
	 
	 Assert.assertTrue(dp < 3);    // km
	 Assert.assertTrue(dv < 1E-9); // km/s
	 Assert.assertTrue(da < 1E-3); // arcsec
     }
     
     /**
      * Compare ASTROMETRIC state of planet barycenter with spacecraft-centric 
      * value obtained from JPL Horizons.
      */
    @Test
     public void test4() {
	 int naifid = 6; // Saturn barcenter

	 TaiTime ft = formatter.parse("2010-01-15T00:00:00TDB");
	 double tdb = mjdTdbFmt.TaiTimeToMjd2000(ft);
	 
	 // Expected values, from Horizons: Time=2010-01-15T00:00:00(CT)
	 // CENTER=500@-486, COMMAND=6, VECT_CORR=LT (source H20111011_0001.LOE)
	 Vector3 pe = new Vector3(-1.355606343677041E+09,
	 			  -1.215794344800738E+08,
	 			  8.117920487199545E+06);
		
	 Vector3 ve = new Vector3(2.712350391180988E+01,
		 		  2.517945682813838E+00,
		 		  1.472028155251321E+00);
	 
	 State vh  = _horizons.stateOf(naifid, tdb, Correction.LT);
	 State vh1 = _horizons.stateOf(naifid, ft,  Correction.LT);
	 Assert.assertTrue(equals(vh, vh1, 1E-15));

	 double dp = vh.position().subtract(pe).norm();
	 double dv = vh.velocity().subtract(ve).norm();
	 double da = Math.toDegrees(vh.position().angle(pe)) * 3600.0;
	 
	 if(DEBUG) {
	     System.out.println("test4: maxPos = " + dp + "km  maxVel=" + dv
	                      + "km/s  maxAng=" + da + " arcsec");
	 }

	 Assert.assertTrue(dp < 1E-5); // km
	 Assert.assertTrue(dv < 1E-9); // km/s
	 Assert.assertTrue(da < 1E-6); // arcsec
     }
     
     /**
      * Compare GEOMETRIC state of planet barycenter with spacecraft-centric 
      * value obtained from JPL Horizons.
      */
    @Test
     public void test5() {
	 int naifid = 6; // Saturn barcenter

	 TaiTime ft = formatter.parse("2010-01-15T00:00:00TDB");
	 double tdb = mjdTdbFmt.TaiTimeToMjd2000(ft);
	 
	 // Expected values, from Horizons: Time=2010-01-15T00:00:00(CT)
	 // CENTER=500@-486, COMMAND=6, VECT_CORR=NONE (source H20111011_0001.LOE)
	 Vector3 pe = new Vector3(-1.355609537814897E+09,
		                  -1.216201049358906E+08,
		                  8.101259413728788E+06);

	 Vector3 ve = new Vector3(2.712380337164236E+01,
		                  2.517945126271883E+00,
		                  1.472015031551165E+00);
	 
	 State vh  = _horizons.stateOf(naifid, tdb, Correction.NONE);
	 State vh1 = _horizons.stateOf(naifid, ft,  Correction.NONE);
	 Assert.assertTrue(equals(vh, vh1, 1E-15));

	 double dp = vh.position().subtract(pe).norm();
	 double dv = vh.velocity().subtract(ve).norm();
	 double da = Math.toDegrees(vh.position().angle(pe)) * 3600.0;
	 
	 if(DEBUG) {
	     System.out.println("test4: maxPos = " + dp + "km  maxVel=" + dv
	                      + "km/s  maxAng=" + da + " arcsec");
	 }
	 
	 Assert.assertTrue(dp < 1E-5); // km
	 Assert.assertTrue(dv < 1E-9); // km/s
	 Assert.assertTrue(da < 1E-6); // arcsec
     }
     
     /**
      * Test radial velocity calculation.
      */
    @Test
     public void test6() {
	 int naifid = 6; // Saturn barcenter
	 TaiTime ft = formatter.parse("2010-01-15T00:00:00TDB");
	 double tdb = mjdTdbFmt.TaiTimeToMjd2000(ft);
	 
	 double v  = _horizons.radialVelocity(naifid, ft);
	 double v1 = _horizons.radialVelocity(naifid, tdb);
	 
	 // Expected value from Horizons
	 double ve = -2.723076158634031E+01;
	 Assert.assertEquals(ve, v,  1E-8);
	 Assert.assertEquals(ve, v1, 1E-8);
     }
     
     /**
      * Compare two state vectors for equality.
      */
     private static boolean equals(State a, State b, double epsilon) {
	 return a.position().epsilonEquals(b.position(), epsilon)
	    &&  a.velocity().epsilonEquals(b.velocity(), epsilon);
     }
}

