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

import esa.esac.gusto.ephem.sites.Ellipsoid;
import esa.esac.gusto.math.Vector3;
import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test harness for Ellisoid class.
 * 
 * @author  Jon Brumfitt
 */
public class EllipsoidTest extends TestCase {

    private static final double WGS84_MAJOR = 6378.137000;
    private static final double WGS84_MINOR = 6356.752314;

    @Test
    public void test1() {
	Ellipsoid wgs84 = new Ellipsoid(WGS84_MAJOR, WGS84_MINOR);
	double e = wgs84.eccentricity();
	Assert.assertEquals(e, 0.081819191, 1E-9);
    }

    @Test
    public void test2() {
	double h = 1000.0;
	Ellipsoid wgs84 = new Ellipsoid(WGS84_MAJOR, WGS84_MINOR);

	Vector3 vmaj = wgs84.vectorFor(0,0,h);
	Vector3 vmajExp = new Vector3(WGS84_MAJOR + h, 0, 0);
	Assert.assertTrue(vmaj.epsilonEquals(vmajExp, 1E-10));

	Vector3 vmaj2 = wgs84.vectorFor(90,0,h);
	Vector3 vmajExp2 = new Vector3(0, WGS84_MAJOR + h, 0);
	Assert.assertTrue(vmaj2.epsilonEquals(vmajExp2, 1E-10));

	Vector3 vmin = wgs84.vectorFor(0,90,h);
	Vector3 vminExp = new Vector3(0, 0, WGS84_MINOR + h);
	Assert.assertTrue(vmin.epsilonEquals(vminExp, 1E-10));

	Vector3 vmin2 = wgs84.vectorFor(90,90,h);
	Vector3 vminExp2 = new Vector3(0, 0, WGS84_MINOR + h);
	Assert.assertTrue(vmin2.epsilonEquals(vminExp2, 1E-10));
    }
}


