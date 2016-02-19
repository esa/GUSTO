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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import esa.esac.gusto.ephem.State;
import esa.esac.gusto.ephem.de405.De405Ephemerides;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.TimeScale;

/**
 * Test harness for ephemeris classes.<p>
 *
 * This test harness checks computed ephemerides against a file of
 * previously calculated test cases, which are generated using
 * the ESOC EASW Fortran library.
 *
 * @author  Jon Brumfitt
 */
public class EphemTest {

    private static final String DE405_FILE = "data/ascp_short.405";
    private static final String EPHEM_DATA = "data/ephem.dat";

    // Tolerance used for comparison with expected result
    private static final double MAX_POS = 1E-6;  // 1 mm
    private static final double MAX_VEL = 1E-9;  // 1 micron/sec

    protected static De405Ephemerides _ephem;

    private static SimpleTimeFormat formatter = new SimpleTimeFormat(TimeScale.TDB);
    private static Mjd2000TimeFormat mjdTdbFmt = new Mjd2000TimeFormat(TimeScale.TDB);

    static {
	formatter.setDecimals(6);
    }

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
	InputStream is = EphemTest.class.getResourceAsStream(DE405_FILE);
	_ephem = new De405Ephemerides(is);
	is.close();
    }

    @AfterClass
    public static void oneTimeTearDown() {
	_ephem = null;
    }

    /**
     * Check computed ephemerides against test cases read from a file.
     */
    @Test
    public void test1() throws IOException {
	InputStream is = EphemTest.class.getResourceAsStream(EPHEM_DATA);
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));

	double dxMax = 0;
	double dvMax = 0;

	String line;
	int ntest = 0;
	while((line = reader.readLine()) != null) {
	    line = line.trim();
	    if(line.startsWith("#") || (line.length() == 0)) {
		continue;
	    }

	    StringTokenizer st = new StringTokenizer(line);
	    TaiTime ft = formatter.parse(st.nextToken() + " TDB");	    
	    double tdb2000 = mjdTdbFmt.TaiTimeToMjd2000(ft);
	    int body = Integer.parseInt(st.nextToken());
	    State state = _ephem.geocentricState(tdb2000, body);

	    double[] ex = new double[6];
	    for(int i=0;i<6; i++) {
		ex[i] = Double.parseDouble(st.nextToken());
	    }
	    State expected = new State(ex);

	    State ds = state.subtract(expected);
	    double dx = ds.position().norm();
	    double dv = ds.velocity().norm();

	    if(dx > dxMax) dxMax = dx;
	    if(dv > dvMax) dvMax = dv;
	    
	    if((dx >= MAX_POS) || (dv >= MAX_VEL)) {
		System.out.println("Failed at:");
		System.out.print(formatter.format(mjdTdbFmt.mjd2000ToTaiTime(tdb2000)));
		System.out.print("  body=" + body);
		System.out.print("  dx=" + dx);
		System.out.println("  dv=" + dv);
		System.out.print("state    = " + state);
		System.out.print("expected = " + expected);
		System.out.println();
	    }

 	    assertTrue(dx < MAX_POS);
 	    assertTrue(dv < MAX_VEL);
	    ntest++;
	}
	System.out.println("Ran " + ntest + " tests on planetary ephemerides");
	assertTrue(ntest > 0); // Ensure the tests were not skipped

	System.out.println("dxMax=" + dxMax);
	System.out.println("dvMax=" + dvMax);

	reader.close();
    }

    /**
     * Return the distance between the position components of two vectors.
     */
    public double distance(double[] a, double[] b) {
	double x = 0;
	for(int i=0; i<3; i++) {
	    double d = a[i] - b[i];
	    x += d * d;
	}
	return Math.sqrt(x);
    }

    /**
     * Return the distance between the velocity components of two vectors.
     */
    public double velocity(double[] a, double[] b) {
	double v = 0;
	for(int i=3; i<6; i++) {
	    double d = a[i] - b[i];
	    v += d * d;
	}
	return Math.sqrt(v);
    }
}

