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
import esa.esac.gusto.time.CucConverter;
import esa.esac.gusto.time.TaiTime;

import org.junit.Test;

/**
 * Test harness for CucConverter.
 *
 * @author  Jon Brumfitt
 */
public class CucConverterTest {

    private static final int FINE_BITS = 16;
    private static final int COARSE_BITS = 32;
    private static final int FINE_MOD = 1 << FINE_BITS;
    private static final long COARSE_MOD = 1L << COARSE_BITS;
    private static final long CUC_MOD = 1L << (FINE_BITS + COARSE_BITS);

    /**
     * Convert CUC (coarse,fine) pair to a TaiTime and then back
     * to a CUC. Check that the original values are returned.
     */
    void doTest(long coarse, int fine) {
	CucConverter conv = new CucConverter();
	TaiTime ft = conv.toTaiTime(coarse, fine);
	long c = conv.coarse(ft);
	int  f = conv.fine(ft);
	assertEquals(coarse, c);
	assertEquals(fine, f);
    }

    /**
     * Convert CUC to a TaiTime and then back to a CUC. Check that
     * the original values are returned.
     */
    void doTest(long cuc) {
	CucConverter conv = new CucConverter();
	TaiTime ft = conv.toTaiTime(cuc);
	long micros = conv.toMicroseconds(cuc);
	long c = conv.cucValue(ft);
	assertEquals(cuc, c);
	assertEquals(micros, ft.microsecondsSince1958());
    }

    /**
     * Test the conversion of (coarse,fine) pair with various values.
     */
    @Test
    public void testCuc1() {
	doTest(0, 0);
	doTest(1, 1);
	doTest(1, FINE_MOD / 2);
	doTest(1, FINE_MOD - 1);

	int ISTEPS = 17;  // Number of different values to test
	int JSTEPS = 13;

	for(long j = 0; j < COARSE_MOD; j += (COARSE_MOD / JSTEPS)) {
	    for(int i = 0; i < FINE_MOD; i += (FINE_MOD / ISTEPS)) {
		doTest(j,i);
	    }
	}
    }

    /**
     * Test the conversion with various values.
     */
    @Test
    public void testCuc2() {
	doTest(0);
	doTest(1);
	doTest(CUC_MOD / 2);
	doTest(CUC_MOD - 1);

	int NSTEPS = 17;  // Number of different values to test

	for(long i = 0; i < CUC_MOD; i += (CUC_MOD / NSTEPS)) {
	    doTest(i);
	}
    }

    /**
     * Test the conversion with all values of 'fine' field.
     */
    @Test
    public void testCuc3() {
	long coarse = 1514764836L;

	// Try all value of 'fine' for a given value of 'coarse'.
	for(long i=0; i<FINE_MOD; i++) {
	    doTest(coarse + i);
	}
    }

    /**
     * Test conversion from TaiTime at integer number seconds.
     */
    @Test
    public void testCuc4() {
	CucConverter conv = new CucConverter();
	long coarse = 1514764836;
	TaiTime ft = conv.toTaiTime(coarse, 0);
	long micro = ft.microsecondsSince1958() % 1000000L;

	assertEquals(micro, 0);
    }
}




