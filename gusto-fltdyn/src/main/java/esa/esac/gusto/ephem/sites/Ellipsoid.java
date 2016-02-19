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

import esa.esac.gusto.math.Vector3;

/**
 * Ellipsoid.
 *
 * @author  Jon Brumfitt
 */
public class Ellipsoid {

    private final double _major; // Semi-major axis
    private final double _minor; // Semi-minor axis
    
    /**
     * Create a new Ellipsoid.
     *
     * @param major Semi-major axis
     * @param minor Semi-minor axis
     */
    public Ellipsoid(double major, double minor) {
	_major = major;
	_minor = minor;
    }

    /**
     * Return Cartesian (ECEF) coordinates of a point.
     *
     * @param longitude Longitude (degrees)
     * @param latitude Geodetic latitude (degrees)
     * @param height Height above reference ellipsoid
     * @return Cartesian (ECEF) vector
     */
    public Vector3 vectorFor(double longitude, double latitude, double height) {

	double phi = Math.toRadians(latitude);
	double lambda = Math.toRadians(longitude);
	double cosPhi = Math.cos(phi);
	double sinPhi = Math.sin(phi);
	double cosLambda = Math.cos(lambda);
	double sinLambda = Math.sin(lambda);

	double e = eccentricity();
	double h = height;
	double es = e * sinPhi;
	double rn = _major / Math.sqrt(1 - es * es);

	double x = (rn + h) * cosPhi * cosLambda;
	double y = (rn + h) * cosPhi * sinLambda;
	double z = ((1 - e * e) * rn + h) * sinPhi;

	return new Vector3(x, y, z);
    }
    
    /**
     * Return the eccentricity.
     */
    public double eccentricity() {
	return Math.sqrt(1 - (_minor * _minor) / (_major * _major));
    }
}


