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
import esa.esac.gusto.math.Coordinates;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.Epoch;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import org.junit.Test;

/**
 * Test harness for Coordinates class.
 *
 * @author  Jon Brumfitt
 */
public class CoordinatesTest {
    // Test cases computed using http://ned.ipac.caltech.edu/forms/calculator.html
    
    private static final SimpleTimeFormat TT = new SimpleTimeFormat(TimeScale.TT);
    
    @Test
    public void testCorrectProperMotion0() {
	Direction d0 = Direction.fromDegrees(100, 30);
	TaiTime time = TT.parse("2010-01-01T12:00:00 TT");
	Vector3 v1 = Coordinates.correctProperMotion(d0, 0, 0, time);
	Vector3 v0 = new Vector3(d0);
	assertEquals(0, v0.angle(v1), 1E-15);
    }
    
    @Test
    public void testCorrectProperMotion() {
	Direction d0 = Direction.fromDegrees(100, 30);
	TaiTime time = TT.parse("2010-01-01T12:00:00 TT");
	Vector3 v1 = Coordinates.correctProperMotion(d0, 10, 20, time);
	// Approx expected value
	Vector3 ve = new Vector3(Direction.fromDegrees(100.032075, 30.055555));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-4);
    }
    
    @Test
    public void testCorrectProperMotionNorth() {
	Direction d0 = Direction.fromDegrees(100, 90);
	TaiTime time = TT.parse("2010-01-01T12:00:00 TT");
	Vector3 v1 = Coordinates.correctProperMotion(d0, 10, 20, time);
	// Approx expected value
	Vector3 ve = new Vector3(Direction.fromDegrees(253.43495, 89.93788));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-4);
    }
    
    @Test
    public void testCorrectProperMotionSorth() {
	Direction d0 = Direction.fromDegrees(100, -90);
	TaiTime time = TT.parse("2010-01-01T12:00:00 TT");
	Vector3 v1 = Coordinates.correctProperMotion(d0, 10, 20, time);
	// Approx expected value
	Vector3 ve = new Vector3(Direction.fromDegrees(126.56505, -89.93788));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-4);
    }
    
    @Test
    public void testB1950ToJ2000() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	Vector3 v1 = Coordinates.b1950ToJ2000Frame().rotateAxes(v0);	
	Vector3 ve = new Vector3(Direction.fromDegrees(100.79849730, 29.94975185));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-4);
    } 
    
    @Test
    public void testEquToEcl() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	Vector3 v1 = Coordinates.equToEclFrame().rotateAxes(v0);
	Vector3 ve = new Vector3(Direction.fromDegrees(98.71206316, 6.86263602));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-4);
    }
    
    @Test
    public void testEclToEqu() {
	Quaternion q = Coordinates.eclToEquFrame().multiply(Coordinates.equToEclFrame());
	assertEquals(0, q.angle(), 1E-15);
    }
    
    @Test
    public void testEquToGal() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	Vector3 v1 = Coordinates.equToGalFrame().rotateAxes(v0);
	Vector3 ve = new Vector3(Direction.fromDegrees(184.68501689, 10.92612446));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 2E-4);
    }
    
    @Test
    public void testGalToEqu() {
	Quaternion q = Coordinates.equToGalFrame().multiply(Coordinates.galToEquFrame());
	assertEquals(0, q.angle(), 1E-15);
    }
    
    @Test
    public void testEclToGal() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	Vector3 v1 = Coordinates.eclToGalFrame().rotateAxes(v0);
	Vector3 ve = new Vector3(Direction.fromDegrees(163.54388876, 22.25678074));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 2E-4);
    }
    
    @Test
    public void testGalToEcl() {
	Quaternion q = Coordinates.galToEclFrame().multiply(Coordinates.eclToGalFrame());
	assertEquals(0, q.angle(), 1E-15);
    }
    
    @Test
    public void testPrecessToJ2000() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	TaiTime t = Epoch.J2000.addSeconds(36525 * 8640);
	Vector3 v1 = Coordinates.precessToJ2000(v0, t);
	Vector3 ve = new Vector3(Direction.fromDegrees(99.84020388, 30.00959124));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-8);
    }
    
    @Test
    public void testPrecessFromJ2000() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	TaiTime t = Epoch.J2000.addSeconds(36525 * 8640);
	Vector3 v1 = Coordinates.precessFromJ2000(v0, t);
	Vector3 ve = new Vector3(Direction.fromDegrees(100.15976822, 29.99025586));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-8);
    }
    
    @Test
    public void testPrecessionJ2000ToEpoch() {
	Vector3 v0 = new Vector3(Direction.fromDegrees(100, 30));
	TaiTime time = Epoch.J2000.addSeconds(36525 * 8640);
	Vector3 v1 = Coordinates.precessionJ2000ToEpoch(time).rotateAxes(v0);
	Vector3 ve = new Vector3(Direction.fromDegrees(100.15976822, 29.99025586));
	double err = Math.toDegrees(v1.angle(ve));
	assertTrue(err < 1E-8);
    }
}

