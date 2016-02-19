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
import esa.esac.gusto.math.MathUtils;

import org.junit.Test;

/**
 * Test harness for MathUtils class.
 *
 * @author  Jon Brumfitt
 */
public class MathUtilsTest {

    @Test
    public void testHavSin() {
	double a = 0.3;
	double y = (1 - Math.cos(a)) / 2;
	
	double h = MathUtils.havsin(a);
	assertEquals(y, h, 1E-15);
    }
    
    @Test
    public void testAhavSin() {
	double a = 0.3;
	
	double h = MathUtils.havsin(a);
	double a2 = MathUtils.ahavsin(h);
	assertEquals(a, a2, 1E-15);
    }
    
    @Test
    public void testHcf() {
	assertEquals(0, MathUtils.hcf(0,0), 0);
	assertEquals(0, MathUtils.hcf(5,0), 5);
	assertEquals(0, MathUtils.hcf(0,5), 5);
	assertEquals(0, MathUtils.hcf(20,12), 4);
	assertEquals(0, MathUtils.hcf(12,20), 4);
    }
}



