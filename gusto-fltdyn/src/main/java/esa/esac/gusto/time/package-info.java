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
Time classes with microsecond resolution and handling of leap-seconds.

<h2 style="color: rgb(51, 51, 255);">Overview of Package<br>
</h2>
<p>The Java <span style="font-family: monospace;">Date</span> class
and its associated <span style="font-family: monospace;">DateFormat</span>
classes are suitable for business applications, where leap-seconds can
be ignored and
microsecond resolution is not required. The time package, on the other
hand,
is intended for scientific applications, primarily in the field of
space data systems and astronomy, where such factors are important.<br>
</p>
<p>The class <span style="font-family: monospace;">TaiTime</span>
represents an absolute time measured in elapsed SI seconds since the
reference <span style="font-style: italic;">epoch</span> 1958 January
1. The resolution
is
one microsecond and the allowable range is: epoch +/-290,000 years
approximately. Because of the high accuracy of the atomic time
standard, TaiTime may
be thought of as an abstraction of the time measured by a perfect clock
at mean sea level.<br>
</p>
<p>There are variety of <span style="font-family: monospace;">TimeScale</span>
classes, each representing a time scale such as TAI, UTC, TT or TDB
(see below).
These scales may be simple linear offsets from TAI (such as TT) or may
non-linear, such as TDB. The <span style="font-family: monospace;">TimeScale</span>
provides the mapping between TaiTime and a given time scale.<br>
</p>
<p>The divison of time into years, months, days, hours, minutes and
seconds is handled by the <span style="font-family: monospace;">GregorianTimeCalendar</span>
class which implements the <span style="font-family: monospace;">TimeCalendar</span>
interface. Unlike the standard Java <span
 style="font-family: monospace;">GregorianCalendar</span>, the <span
 style="font-family: monospace;">GregorianTimeCalendar</span> supports
leap seconds and microsecond resolution.<br>
<br>
Parsing time strings and formatting times for output is handled by a <span
 style="font-family: monospace;">TimeFormat</span> class. The <span
 style="font-family: monospace;">SimpleTimeFormat</span> class
implements a formatter, based on the CCSDS ASCII Time Format A. Used in
conjunction with a <span style="font-family: monospace;">TimeScale,</span>
it is possible to parse and format times as calendar dates and times
using time scales such as UTC, TAI, TT, TDB, etc. For example:<br>
</p>
<pre>    SimpleTimeFormat tai = new SimpleTimeFormat(TimeScale.TAI);
    tai.setDecimals(6);
    TaiTime time = tai.parse("1993-06-30T23:59:59 TAI");
    System.out.println(tai.format(time));

    SimpleTimeFormat utc = new SimpleTimeFormat(TimeScale.UTC);
    utc.setDecimals(6);<br>    System.out.println(utc.format(time));

    SimpleTimeFormat tt = new SimpleTimeFormat(TimeScale.TT);
    tt.setDecimals(6);<br>    System.out.println(tt.format(time));

    SimpleTimeFormat tdb = new SimpleTimeFormat(TimeScale.TDB);
    tdb.setDecimals(6);<br>    System.out.println(tdb.format(time));
</pre>
This prints the following:<br>
<pre>    1993-06-30T23:59:59.000000 TAI
    1993-06-30T23:59:32.000000Z
    1993-07-01T00:00:31.184000 TT
    1993-07-01T00:00:31.182981 TDB
</pre>
Note that the TAI-UTC difference, resulting from leap seconds is <span
 style="font-style: italic;">not</span> implemented as a non-linear <span
 style="font-family: monospace;">TimeScale</span>. It is simply a
linear time scale used in conjunction with a calendar that handles
leap-seconds in much the same way as leap-years. The <span
 style="font-family: monospace;">SimpleTimeFormat</span> class
automatically handles leap seconds when used in conjunction with the <span
 style="font-family: monospace;">UtcTimeScale</span>.<br>
<br>
The <span style="font-family: monospace;">MjdTimeFormat</span> class
allows a Modified Julian Date (MJD) to be parsed and formatted. It also
allows conversion between a decimal MJD value and a <span
 style="font-family: monospace;">TaiTime</span>. The <span
 style="font-family: monospace;">MjdTimeFor<small
 style="font-family: monospace;">ma</small></span><small
 style="font-family: monospace;">t</small> can also be used in
conjunction with a <span style="font-family: monospace;">TimeScale</span>,
so that it is possible to parse, format and convert various forms of
MJD, such as MJD(TAI), MJD(TT) and MJD(TDB). <br>
<br>
For example
<pre>    MjdTimeFormat mjdTai = new MjdTimeFormat(TimeScale.TAI);<br>    TaiTime time = mjdTai.parse("51179.0");<br><br>    double mjd = mjdTai.TaiTimeToMjd(time);<br>    System.out.println(mjd);<br><br>    MjdTimeFormat mjdTdb = new MjdTimeFormat(TimeScale.TDB);<br>    System.out.println(mjdTdb.format(time));<br></pre>
<p>This prints the following:<br>
</p>
<p style="font-family: monospace;">&nbsp;&nbsp;&nbsp; 51179.0<br>
&nbsp;&nbsp;&nbsp; 51179.00037251238</p>
<p>The time package also
includes converters for other representations of
time, such as the CCSDS unsegmented time
code format.<br>
</p>
<h2 style="color: rgb(51, 51, 255);">Systems of Time<br>
</h2>
<p><span style="font-weight: bold;">Temps Atomique International</span>
(TAI) is now the primary international time scale. It is defined by
measurements made by over 200 atomic clocks around the world and now
provides the definition of the SI second. In its
simplest form, TAI is simply the number of elapsed seconds since the
TAI epoch 1958 January 1.<br>
</p>
<p><span style="font-weight: bold;">Universal Time</span> (UT) is a
time scale based on astronomical measurements of the rotation of the
Earth. There are several variants, such as UT0, UT1 and UT2, that make
various
corrections. Collectively, these are known as Universal Time (UT), when
the small differences between them (less than 50 milliseconds) can be
ignored. UT1 is defined in terms of the mean solar day and consequently
there is slowly varying difference between UT1 and TAI, resulting from
variations in the rotation
of the Earth.</p>
<p><span style="font-weight: bold;">Coordinated Universal Time</span>
(UTC) was introduced in 1960. From 1960
to 1971, inclusive, the length of the second was occasionally changed
in steps of a few milliseconds to keep UTC in approximate agreement
with Universal Time. Since 1972, the UTC second has been made exactly
equal to the TAI (SI) second, by the introduction of occasional <span
 style="font-style: italic;">leap-seconds</span>. As a result, UTC
always differs from TAI by an exact number of seconds. Leap seconds are
chosen so as to keep UTC in agreement with UT1 to within 0.9
seconds. The TAI epoch is defined as 1958
January
1, 00:00:00 UT2 and consequently the UT2 and TAI time scales coincide
at this point.<br>
</p>
<p>Because of these complexities, this time package only supports UTC
conversions for dates from 1972 onwards.<br>
</p>
<p><span style="font-weight: bold;">Terrestrial Time</span> (TT) is the
proper time measured on the surface of the (rotating) geoid. It can be
thought of as the time measured by a perfect clock, whereas TAI is
subject to measurement errors. For practical purposes, TT differs from
TAI
by a constant offset, such that TT - TAI = 32.184 seconds.<br>
</p>
<p><span style="font-weight: bold;">Ephemeris Time</span> (ET) is an
obsolete time scale, based on the <span style="font-style: italic;">Ephemeris
Second</span>, which is defined in terms of the Earth's revolution
around the Sun. It was used for the calculation of planetary
ephemerides, but has been superceeded for this purpose by Barycentric
Dynamical Time (TDB) which takes relativistic effects into account.<br>
</p>
<p><span style="font-weight: bold;">Barycentric Dynamical Time</span>
(TDB) was defined as the relativistic replacement for Ephemeris Time,
for use in the calculation of planetary ephemerides. It measures the
time at the Solar System Barycentre (centre of mass), but uses a second
with a mean length equal to that of the SI second so that physical
constants remain the
same as their pre-relativistic values. Consequently, TDB differs from
TT only by periodic variations with a zero mean, the maximum difference
being less than 2 milliseconds. It was later realised that TDB was not
well defined and so Barycentric Coordinate Time (TCB) was introduced as
a replacement. However, TDB is still widely used for the calculation of
ephemerides.<br>
<span style="font-weight: bold;"><br>
Barycentric Coordinate Time</span> (TCB) is a coordinate time with its
spatial origin at the Solar System Barycentre. Such a clock ticks
faster than TDB due to relativistic effects with the result that the
values of some physical constants need to be changed.<br>
</p>
<p>Further information on systems of time can be found at:<br>
</p>
<ul>
  <li><a href="http://tycho.usno.navy.mil/systime.html">http://tycho.usno.navy.mil/systime.html</a></li>
  <li><a
 href="http://www.sp.se/metrology/timefreq/eng/timekeep_history.htm">http://www.sp.se/metrology/timefreq/eng/timekeep_history.htm</a><br>
  </li>
</ul>
<p></p>
<h2><span style="color: rgb(51, 51, 255);">Julian Dates</span></h2>
<p>The <span style="font-weight: bold;">Julian Day Number</span> is
the number of days elapsed since noon on 1 January 4713 BC in the
proleptic Julian Calendar.<br>
</p>
<p>The <span style="font-weight: bold;">Julian Date</span> (JD) is the
Julian Day number followed by the fraction of a day elapsed since the
preceding noon. The term Julian Date is also used to refer to a date in
the Julian Calendar, although the usage is normally obvious from the
context.<br>
</p>
<p>Julian Dates are useful in numerical calculations in astronomy,
where
it is required to represent the date a single real (i.e. floating
point) number. However, current dates correspond to very large JD
values, resulting in significant numerical errors when two dates are
subtracted. For such purposes, it is better to use an offset Julian
Date (i.e. with a later epoch), such as MJD or MJD2000.<br>
</p>
<p>The <span style="font-weight: bold;">Modified Julian Date</span>
(MJD) is defined as MJD = JD - 2400000.5, which corresponds to an epoch
of 1858-11-17T00:00:00. Note that MJD times are measured from midnight,
whereas JD is measured from noon.<br>
</p>
<p>More recently, <span style="font-weight: bold;">MJD2000</span> has
been introduced, with an epoch of 2000-01-01T00:00:00 (i.e. midnight).
Note that this is <span style="font-weight: bold;">not</span> the same
as the J2000.0 epoch, which is defined as 2000-01-01T12:00:00 (i.e.
midday).<br>
</p>
<p>Julian Dates (JD, MJD, MJD2000, etc) can be specified for any time
scale, such as TAI, TT or TDB. For example, 15340 MJD (TDB). Julian
dates should not normally be specified using UTC, such as 12345 MJD
(UTC), because the presence of leap seconds results in days of unequal
length and consequently a non-linear time scale.<br>
</p>
<h2><span style="color: rgb(51, 51, 255);">Leap Seconds</span><br>
</h2>
<p>Leap-seconds are announced periodically by the <a
 href="http://maia.usno.navy.mil/">International Earth Rotation Service</a>
in Bulletin C and always occur at midnight on December 31 or June 30.
In principle, it is possible for leap-seconds to be added or
subtracted, although for the foreseeable future leap-seconds will
continue to be added, as the rotation of the Earth is slowing down.
This results in the last minute of the last hour of the day having 61
seconds (numbered 0 to 60), instead of 60.<br>
</p>
Leap-second announcements are
made at quite short notice (6 months ahead). It should therefore be
appreciated that any conversion to/from UTC for times beyond the
validity of the current leap-second table, may not be exactly right.
The leap-second table for this package is currently an ASCII file
included in the JAR file and updates to the leap-second table
consequently require a new software release. A later version of this
package might update
the table automatically, using a suitable server.<br>
<h2 style="color: rgb(51, 51, 255);">Java Date</h2>
<p>The Java <tt>Date</tt> class is intended to represent UTC but does
not
do so exactly as it skips leap seconds. The <span
 style="font-family: monospace;">getTime</span> method returns the
number of milliseconds since the epoch 1 January 1970 <span
 style="font-style: italic;">ignoring leap seconds</span>. Hence, if
the difference between two Dates is calculated, it will not be correct
if there are one or more intervening leap seconds. For example:<br>
</p>
<p></p>
<p></p>
<pre>    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    Date d1 = formatter.parse("1998-12-31T23:59:59.000Z");
    Date d2 = formatter.parse("1999-01-01T00:00:00.000Z");
    System.out.println(d2.getTime() - d1.getTime());
</pre>
<p>This prints 1000 (milliseconds), whereas the correct time difference is 
2000, as there is a leap second in between.
<br>
</p>
<p>The time package avoids this problem by correctly representing time
as true elapsed time and by providing UTC formatters and converters
that handle leap seconds correctly. Where possible, new applications
should use <span style="font-family: monospace;">TaiTime</span> to
represent time internally, and use the <span
 style="font-family: monospace;">SimpleTimeFormat</span> class to input
and output times. Where it is necessary to interface to existing Java
code that makes use of the <span style="font-family: monospace;">Date</span>
class, the class <span style="font-family: monospace;">UnixTime</span>
is provided to convert between <span style="font-family: monospace;">Date</span>
and <span style="font-family: monospace;">TaiTime</span>, or vice
versa. However, it should be noted that conversion is not exact at it <span
 style="font-style: italic;">aliases</span> leap seconds.<br>
</p>
<h2><span style="color: rgb(51, 51, 255);">CCSDS</span><br>
</h2>
<p>The <a href="http://www.ccsds.com">Consultative Committe for Space
Data Systems</a> (CCSDS) has defined a
standard for the representation of time for space data systems. This
includes ASCII formats for representing UTC times and binary
representations for use in telecommanding and telemetry. This standard
is contained in CCSDS Blue Book 301.0-B-3, January 2002.<br>
</p>
<p>The <span style="font-family: monospace;">SimpleTimeFormat</span>
class, when used in conjuction with a UTC <span
 style="font-family: monospace;">TimeScale</span>, supports formatting
and parsing of time strings in CCSDS ASCII Time Code Format A (e.g.
"2003-07:23T21:52:49Z"). The <span style="font-family: monospace;">CucConverter</span>
class allows <span style="font-family: monospace;">TaiTimes</span> to
be converted to and from the binary CCSDS Unsegmented Time Code (CUC)
format, which is used for satellite telecommanding and telemetry.<br>
*/

package esa.esac.gusto.time;


