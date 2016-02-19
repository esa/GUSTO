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

package esa.esac.gusto.time.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import esa.esac.gusto.time.Epoch;
import esa.esac.gusto.time.MjdTimeFormat;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeFormat;
import esa.esac.gusto.time.TimeScale;

import org.junit.Test;

/**
 * Test harness for Epoch class.
 *
 * @author  Jon Brumfitt
 */
public class EpochTest {

    static final TimeFormat utcFormat = new SimpleTimeFormat(TimeScale.UTC);
    static final TimeFormat taiFormat = new SimpleTimeFormat(TimeScale.TAI);

    static final double EPSILON = 1 / 86400d / 1000000d; // 1 microsecond error in a day

    /**
     * Test J2000 constant.
     */
    @Test
    public void testJ2000() {
	// FIXME: This is not a very good test as it uses the same converter
	// as the definition of the epoch!
	MjdTimeFormat converter = new MjdTimeFormat(TimeScale.TT);
	double mjdTt = converter.TaiTimeToMjd(Epoch.J2000);
	// J2000.0 is MJD(TT) 51544.5 (i.e. mid-day)
	assertEquals(mjdTt, 51544.5, EPSILON);
    }

    /**
     * Test the UTC_1972 epoch.
     */
    @Test
    public void testUTC1972() {
	String s = utcFormat.format(Epoch.UTC_1972);
	assertTrue(s.equals("1972-01-01T00:00:00Z"));
    }
}







