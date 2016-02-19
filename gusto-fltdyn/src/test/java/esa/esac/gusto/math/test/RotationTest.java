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

package esa.esac.gusto.math.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.math.Attitude;
import esa.esac.gusto.math.AxisAngle;
import esa.esac.gusto.math.EulerAngles;
import esa.esac.gusto.math.EulerAngles.Axes;
import esa.esac.gusto.math.Matrix3;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;

import org.junit.Test;

/**
 * Test harness to check various attitude/rotation representations are consistent.
 *
 * @author  Jon Brumfitt
 */
public class RotationTest {

    private static final double ERR = 1E-15;
    private static final Vector3 V = new Vector3(3,4,5);  // Test vector

    @Test
    public void test1() {
	double a = 0.1;
	double b = 0.2;
	double c = 0.3;

	// Euler->Matrix
	Attitude att = new Attitude(a,-b,-c);
	Matrix3 m = Matrix3.fromEulerZYX(a,b,c);
//	double[] v1 = m.toEulerZYX();
	double[] v1 = EulerAngles.fromQuaternion(Axes.ZYX, m.toQuaternion());
	assertEquals(a, v1[0], ERR);
	assertEquals(b, v1[1], ERR);
	assertEquals(c, v1[2], ERR);

	// Attitude<->Matrix
	Attitude att2 = new Attitude(m);
	Matrix3 m2 = att2.toMatrix3();
	assertTrue(m.epsilonEquals(m2, ERR));
	assertEquals(att.getAlpha(), att2.getAlpha(), ERR);
	assertEquals(att.getDelta(), att2.getDelta(), ERR);
	assertEquals(att.getPhi(), att2.getPhi(), ERR);

	// Quaternion<->Matrix
	Quaternion q = m.toQuaternion();
	Matrix3 m3 = q.toMatrix3();
	assertTrue(m.epsilonEquals(m3, ERR));

	// Check matrix and quaternion rotation give the same result
	Vector3 vm = m.multiply(V);
	Vector3 vq = q.rotateVector(V);
	assertTrue(vm.epsilonEquals(vq, ERR));

	// Quaternion<->Axis-Angle
	AxisAngle aa = q.toAxisAngle();
	Quaternion q2 = new Quaternion(aa);
	assertTrue(q.epsilonEquals(q2, ERR));

	double angle = aa.angle();
	Vector3 axis = aa.axis();
	double sn = Math.sin(angle / 2);
	double cs = Math.cos(angle / 2);
	Quaternion q3 = new Quaternion(axis.getX() * sn,
		axis.getY() * sn,
		axis.getZ() * sn,
		cs);
	assertTrue(q.epsilonEquals(q3, ERR));

	// AxisAngle<->Matrix
	Matrix3 m4 = aa.toMatrix3();
	assertTrue(m.epsilonEquals(m4, ERR));

	//	AxisAngle aa2 = m.toAxisAngle();   // INVERT - Not implemented
	// 	assertTrue(aa.epsilonEquals(aa2, ERR);

    }

    @Test
    public void test2() {
	double angle = 1; // 1 radian

	Quaternion qx = Quaternion.xRotation(angle);
	Matrix3 mx = Matrix3.xRotation(-angle).mTranspose(); // rotX is currently passive!

	assertTrue(mx.toQuaternion().epsilonEquals(qx, ERR));
    }
}







