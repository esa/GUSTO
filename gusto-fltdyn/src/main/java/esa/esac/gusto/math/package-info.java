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
Mathematical classes for handling spacecraft attitudes, rotations and
coordinates.<br>
<p></p>
<h2 style="color: rgb(51, 51, 255);">Overview of Package<br>
</h2>
This package provides a number of mathematical classes for handling
celestial
coordinates, spacecraft three-axis attitudes and rotations. <br>
<ul>
  <li><span style="font-family: monospace;">Vector3&nbsp;&nbsp;&nbsp;&nbsp;
    </span>A 3-vector</li>
  <li><span style="font-family: monospace;">Direction&nbsp;&nbsp; </span>Equatorial
coordinates expressed as right-ascension and declination</li>
  <li><span style="font-family: monospace;">Quaternion&nbsp; </span>A
rotation described by a quaternion</li>
  <li><span style="font-family: monospace;">AxisAngle&nbsp;&nbsp; </span>A
rotation described by a rotation angle about a vector axis</li>
  <li><span style="font-family: monospace;">Matrix3&nbsp;&nbsp;&nbsp;&nbsp;
    </span>A 3x3 matrix with support for rotation matrices</li>
  <li><span style="font-family: monospace;">Attitude&nbsp;&nbsp;&nbsp; </span>An
attitude expressed as right-ascension, declination and tilt angles</li>
  <li><span style="font-family: monospace;">Vector2&nbsp;&nbsp;&nbsp;&nbsp;
    </span>A 2-vector</li>
</ul>

<p></p>For new applications, it is recommended that internal representation of sky
coordinates should use the <tt>Vector3</tt> class and that spacecraft attitudes and
sky orientations should use the <tt>Quaternion</tt> class. The <tt>Direction</tt>
and <tt>Attitude</tt> classes should be only used for converting to/from
astronomical coordinates (e.g. right ascension, declination and position angle)
where required for input and output.</p>

<p>Quaternions provide an efficient representation of rotations and attitudes that
avoids problems with singularities at the poles. Quaternion operations also
avoid the computationally expensive trigonometric operations required when
working directly with Equatorial, Ecliptic or Galactic coordinates. It is
recommended to use the <tt>Quaternion</tt> class instead of <tt>Matrix3</tt> for
attitudes and rotations. The quaternion representation avoid problems of ensuring
that matrices are orthonormal. Also, quaternions provide a more compact
representation and quaternion operations are normally faster.

<h2 style="color: rgb(51, 51, 255);">Coordinate Systems<br>
</h2>
All coordinate systems are assumed to be right-handed, with rotations
in the mathematically positive sense (i.e. clockwise when looking along
the axis from the origin) unless otherwise stated.<br>
<br>
Coordinates may be specified with respect to various reference frames,
such as Equatorial, Ecliptic or Galactic. Transformations between these
may
be performed using a passive rotation.<br>
<br>
Spacecraft three-axis attitudes are
described as an active rotation with respect to a reference frame (e.g.
Equatorial frame).<br>
<h2 style="color: rgb(51, 51, 255);">Rotations</h2>
Rotations may be considered as either:<br>
<ul>
  <li>a <span style="font-weight: bold;">passive</span> transformation
of the coordinate system. A given physical vector is expressed in a
different coordinate system. For example, the position vector of a star
is transformed from equatorial to ecliptic coordinates.</li>
  <br>
  <li>an <span style="font-weight: bold;">active</span> rotation with
respect to a fixed coordinate system. This represents a physical
rotation, such as rotation of the spacecraft with respect to a fixed
coordinate frame.<br>
  </li>
</ul>
<ul>
</ul>
An active rotation is simply the inverse of the corresponding passive
rotation:<br>
<br style="font-family: monospace;">
<span style="font-family: monospace;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
-1</span><br style="font-family: monospace;">
<span style="font-family: monospace;">&nbsp; Ra&nbsp; = Rp</span><span
 style="font-family: monospace;"></span><br>
<br>
Passive rotations compose right-to-left, whereas active rotations
compose left-to-right. For example:<br>
<br>
<span style="font-family: monospace;">&nbsp; R3p = R2p .
R1p&nbsp;&nbsp; (passive)</span><br style="font-family: monospace;">
<span style="font-family: monospace;">&nbsp; R3a = R1a .
R2a&nbsp;&nbsp; (active)</span><br>
<br>
Hence:<br>
<span style="font-family: monospace;">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
-1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
-1&nbsp;&nbsp;&nbsp;&nbsp; -1&nbsp;&nbsp;&nbsp;&nbsp; -1</span><br
 style="font-family: monospace;">
<span style="font-family: monospace;">
&nbsp; R3a = R3p&nbsp;&nbsp; = (R2p . R1p) = R1p&nbsp; .
R2p&nbsp;&nbsp; = R1a . R2a<br>
<br>
</span>The <span style="font-weight: bold;">active</span> rotation
Rxa.Rya first rotates the body clockwise about the X axis and then
clockwise about the new Y axis. i.e. the rotations are body-centric.<br>
<br>
The <span style="font-weight: bold;">passive</span> rotation Rxp.Ryp
first rotates the axes anticlockwise about the Y axis and then
anticlockwise about the original X axis. i.e. the rotations are about
fixed global axes.<br>
<br>
The order of multiplication to compose two successive rotationsis the
same for both matrices and quaternions (either both active or both
passive) and follows from the associativity of multiplication:<br>
<br>
<span style="font-family: monospace;">&nbsp; (Ma.Mb).v =
Ma.(Mb.v)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
(matrix)</span><br style="font-family: monospace;">
<br style="font-family: monospace;">
<span style="font-family: monospace;">&nbsp; (Qa.Qb).V.(Qa.Qb)* =
Qa.(Qb.V.Qb*).Qa*&nbsp; (quaternion)</span><br>
<br>
where v is a vector and V is quaternion with imaginary component v and
* is quaternion conjugation.<br>
<h2 style="color: rgb(51, 51, 255);">Attitude</h2>
The three-axis inertial attitude of the spacecraft can be specified by
body-referenced (+Z)(-Y)(-X)&nbsp; Euler angles (RA,DEC,POS), with
respect to the Equatorial reference frame. This can be considered as an
active rotation of the spacecraft with respect to a reference attitude,
in which the spacecraft XYZ axes are aligned with those of the
Equatorial frame.<br>
<br>
RA and DEC are the right ascension and
declination of the spacecraft X axis and POS is a "position angle" that
specifies rotation about the X axis. The position angle is the rotation
between the spacecraft X-Z
plane and the plane defined by the spacecraft X axis and celestial
North, which increases as the spacecraft rotates
anticlockwise about the X axis.<br>
<br>
The position angle and right-ascension are discontinuous at the poles
(i.e. declination of +/- 90 degrees). However, the three-axis attitude
of the spacecraft is well defined on the whole celestial sphere by the
triple (RA,DEC,POS), although there is a many-to-one mapping of this
triple onto attitudes at poles. In the limit, the difference between RA 
and POS remains constant at the North pole, whereas the sum remains 
constant at the South pole. Consequently, the attitude at the poles may 
be described by a triple (0,DEC,POS), in which the RA is (arbitrarily) 
set to zero.<br>
<br>
The (RA,DEC,POS) notation may also be used to described the orientation
of an object on the sky. The position angle increases with an
anticlockwise rotation about an axis pointing from the observer to
the object on the celestial sphere.<br>
<h2 style="color: rgb(51, 51, 255);">Choice of Representation<br>
</h2>
Rotations may be expressed in a variety of ways including Axis-Angle,
Quaternion, Direction Cosine Matrix and Euler angles.<br>
<br>
<span style="font-weight: bold;">Quaternions</span> provide a compact
representation of a rotation using only 4 parameters. A rotation is
normally represented by a unit quaternion. They also allow
straightforward spherical linear interpolation between two rotations
(e.g. attitudes).<br>
<br>
A <span style="font-weight: bold;">rotation matrix</span> (Direction
Cosine Matrix) is an orthogonal matrix whose determinant is +1.
However, these conditions may be violated as a result of numerical
rounding errors. As a result, the transformation may include scaling
and skew components as well as rotation. It may then be necessary to
orthonormalize the matrix, so that it represents a true rotation. The
use of quaternions avoids this problem. The matrix representation also
requires more storage as it involves 9 parameters, as opposed to 4 for
a quaternion.<br>
<br>
The <span style="font-weight: bold;">axis-angle</span> representation
describes a rotation as an rotation angle about an axis vector. This is
simple and easily understood, but composition of two rotations normally
requires converting them to matrices or quaternions.<br>
<br>
<span style="font-weight: bold;">Euler angles</span> are useful for
physical problems, such as representing spacecraft attitude. They
provide a very compact representation of a rotation, involving only
three parameters.<br>
<br>
<span style="font-weight: bold;">(RA,DEC,POS)</span>: For a spacecraft
with an astronomical telescope pointing along the X axis, it is
convenient to describe the attitude by the right ascension (RA),
declination (DEC) of the X axis and a position angle (POS) about that
axis. The (RA,DEC,POS) angles are simply (+Z)(-Y)(-X) angles with
respect to the equatorial reference frame. The Attitude class provides
an (RA,DEC,POS) representation of attitudes.<br>
<br>
To summarise, quaternions are generally a good representation for
internal representation and manipulation of rotations. Euler angles
(e.g. (RA,DEC,POS) are useful for input and output of attitudes in a
form that relates directly to the telescope pointing direction.<br>
<h2 style="color: rgb(51, 51, 255);">Euler Angles<br>
</h2>
Euler angles describe a rotation as a sequence of rotations about the
axes. Various sequences are possible, such as ZYX, ZXZ, etc. This
package uses the ZYX order, since this corresponds closely with the
(RA,DEC,POS) representation.<br>
<br>
The Euler angles Az Ay Ax corresponds to successive active rotations of
the body (i.e. spacecraft) about the Z,Y,X axes, in that order.<br>
<br>
Euler angle representations suffer from "gimbal lock", which occurs
when one of the rotations causes two of the axes to coincide. The
effect is named by analogy with gyroscopes, where problems arise from
alignment of two of the gimbals. When using a Z(-Y)(-X) sequence to
describe (RA,DEC,POS), the problem occurs when the declination is +/-
90 degrees, such that the X axis coincides with the Z axis; there is
then a many-to-one mapping between (RA,DEC,POS) triples and attitudes.<br>
<br>
(RA,DEC,POS) is considered as an active rotation (i.e. body centered)
of the spacecraft from an initial state in which its axes align with
those of the reference frame (e.g. equatorial frame).<br>
<br>
An Attitude (RA,DEC,POS) corresponds to ZYX Euler angles [RA,-DEC,-POS]
because declination is measures anticlockwise about the Y axis and
position angle is measured anticlockwise about the Z axis.<br>
*/

package esa.esac.gusto.math;


