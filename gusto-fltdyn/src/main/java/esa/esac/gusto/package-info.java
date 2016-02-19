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
Library for flight-dynamics calculations.

<h2 style="color: rgb(51, 51, 255);">Overview of Package</h2>

<P>This package consists of the following sub-packages:</P>

<P STYLE="margin-bottom: 0in"><FONT SIZE=4><B>esa.esac.gusto.time</B></FONT></P>
<ul>
  <li>Representation of time with microsecond resolution</li>
  <li>Various astronomical time scales: TAI, UTC, TT, TDB</li>
  <li>Parsing and formatting of time: CCSDS time code A</li>
  <li>Various time formats: CCSDS, MJD2000, CUC</li>
  <li>Support for leap-seconds</li>
</ul>

<P STYLE="margin-bottom: 0in"><FONT SIZE=4><B>esa.esac.gusto.math</B></FONT></P>
<ul>
  <li>Astronomical coordinates: right ascension, declination, position angle</li>
  <li>Spacecraft attitudes, rotations, etc</li>
  <li>Math classes: <tt>Quaternion</tt>, <tt>Matrix3</tt>, <tt>Vector3</tt>,
  <tt>Vector2</tt>, <tt>AxisAngle</tt>, etc
</ul>

<P STYLE="margin-bottom: 0in"><FONT SIZE=4><B>esa.esac.gusto.ephem</B></FONT></P>
<ul>
  <li>Reading and interpolating various source of ephemeris data</li>
  <li>Support for CCSDS OEM orbit files</li>
  <li>Calculation of geometric state vectors</li>
  <li>Plaentaery aberration correction (light-time & stellar aberration) for SSOs</li>
  <li>Radial velocity computation for SSOs</li>
</ul>

<P STYLE="margin-bottom: 0in"><FONT SIZE=4><B>esa.esac.gusto.constraint</B></FONT></P>
<ul>
  <li>Time intervals and time constraints</li>
</ul>
*/
package esa.esac.gusto;


