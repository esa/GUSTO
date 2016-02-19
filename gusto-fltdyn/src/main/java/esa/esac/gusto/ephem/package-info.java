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
<p>Plantary ephemerides and spacecraft orbital ephemerides.</p>

<h2 style="color: rgb(51, 51, 255);">Overview of Package</h2>

<p>This package calculates the state vector (position and velocity)
of a body with respect to another body at a specified time.</p>

<p>The following sources of data are used:</p>

<ul>
  <li>CCSDS OEM orbit file with Lagrange or Hermite interpolation</li>
  <li>DE405 planetary ephemerides (from JPL)  <li>Horizons ephemerides for SSOs (from JPL)</li>
</ul>

<h3 style="color: rgb(51, 51, 255);">Main Classes</h3>

<p class="classname"></p>The <tt>Ephemerides</tt> class interpolates and combines
the orbit and DE405 data to provide geometric state vectors of the Sun, Earth, Moon,
planets and solar system barycentre (SSB) relative to the spacecraft, Earth or SSB, at
a specified time. No correction for stellar aberration is applied.</p>

<p class="classname"></p>The <tt>Horizons</tt> class reads ephemeris files for Solar
System Objects(SSO) obtained from the JPL Horizons system. These are interpolated
and combined with results from the Ephemerides class, to provide state vectors of
SSOs relative to the spacecraft. The results are corrected for
planetary aberration (light-travel time and stellar aberration). On
Herschel, this is used for accurate tracking of comets, asteroids,
planetary moons, etc. The radial velocity of the target relative to
the spacecraft can also be obtained for calibrating spectra etc.</p>


<p>The Horizons files are obtained by sending an email with the subject line "JOB"
to <A HREF="mailto:horizons@ssd.jpl.nasa.gov">horizons@ssd.jpl.nasa.gov</A>,
with the following content:</p>

<pre>
!$$SOF (ssd) JPL/Horizons Execution Control VARLIST
OBJ_DATA = 'YES'
COMMAND = '1000041'
CENTER = '500@0'
START_TIME = '2010-10-10T00:00:00'
STOP_TIME = '2010-11-10T00:00:00'
STEP_SIZE = '45 min'
TIME_DIGITS = 'FRACSEC'
REF_SYSTEM = 'J2000'
TABLE_TYPE = 'VECTORS'
REF_PLANE = 'FRAME'
OUT_UNITS = 'KM-S'
VECT_TABLE = '2'
VECT_CORR = 'NONE'
CSV_FORMAT = 'NO'
!$$EOF
</pre>

<p>The resulting email message should be saved with a file name that is the NAIFID
of the target (e.g. 1000041). Normal email headers and footers need not be removed
as they are ignored by the software.</p>

<p>Only the following parameters should be modified:</p>

<ul>
  <li>COMMAND: the target SSO (using a NAIFID in this case)</li>
  <li>START_TIME: Start of period</li>
  <li>STOP_TIME: End of period</li>
  <li>STEP_SIZE: Time interval between samples</li>
</ul>

<p>Horizons returns the results as a single file if the size is less than about 1MB.
This allows a STEP_SIZE of 45 min for a period of just over a month. With the
Hermite interpolation used, this value provides accurate results for tracking
fast-moving comets etc.</p>

<p>The <tt>Ephemerides</tt> and <tt>Horizons</tt> classes both cache data to avoid
re-reading the files. Normally, applications should create a single instance of
the classes to avoid having multiple copies in memory. However, occasionally, it
may be required to have multiple instances, for example to compare two versions
of an ephemeris data. The constructors of these classes allow an optional
time-range to be specified to speed up loading of the data and to
reduce memory usage. This optimization is not yet implemented in all
cases, but it is recommended that the time range is specified to
allow for future improvements.</p>

<h3 style="color: rgb(51, 51, 255);">Using other Sources of Data</h3>

<P>The <tt>Ephemerides</tt> class extends an abstract class <tt>BasicEphemerides</tt>,
providing readers for the orbit data and DE405 data, as follows:</p>

<pre>
class Ephemerides extends BasicEphemerides {
    public Ephemerides(String orbitFile, String de405File, double tStart, double tEnd) {
        super(new CcsdsOemReader(orbitFile, tStart, tEnd),
        new De405Reader(de405File, tStart, tEnd));
    }
    ...
}		
</pre>

<p>It is possible to write new subclasses of <tt>BasicEphemerides</tt> to read
other sources of data. For example, the Herschel data processing system provides
an implementation that reads the orbit data from a Herschel <tt>OrbitEphemerisProduct</tt>
instead of a CCSDS orbit file.</p>

<p>Similarly, the <tt>Horizons</tt> class extends the abstract class <tt>BasicHorizons</tt>
to provide the implementation of a reader for Horizons ephemeris files.</p>
<P CLASS="preformatted-text"><BR><BR>
*/
package esa.esac.gusto.ephem;


