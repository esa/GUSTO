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

/**
Reads and interpolates JPL DE405 planetary ephemeris files.

<p>The planetary ephemeris file is read and then interpolated using
Chebychev polynomials to find an ephemeris at a specified time.</p>

<p>The ephemeris file 'ascp2000.405' can be obtained from:
<a href="ftp://ssd.jpl.nasa.gov/pub/eph/export/ascii">ftp://ssd.jpl.nasa.gov/pub/eph/export/ascii</a>
</p>
*/

package esa.esac.gusto.ephem.de405;


