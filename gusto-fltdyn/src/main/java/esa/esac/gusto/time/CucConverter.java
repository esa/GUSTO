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

package esa.esac.gusto.time;

/**
 * Converts between TaiTime and CCSDS Unsegmented Time Code (CUC) format.<p>
 *
 * The converter supports CUC format with 4 bytes of coarse time and two
 * bytes of fine time, with the TAI epoch of 1 January 1958. The corresponding
 * CCSDS Preamble (P) field value for this format is 00011110 (binary).<p>
 *
 * CUC times are multiples of 1/65536 sec and cannot be expressed as an exact
 * multiple of 1 microsecond (the resolution of TaiTime). However, the following
 * relations hold for 'coarse' and 'fine' values in the allowed range:
 * <pre>
 *    fine(toTaiTime(c,f)) == f
 *    coarse(toTaiTime(c,f)) = c
 * </pre> <p>
 * 
 * Note that the TAI times returned are nominal values that do not take into account
 * drift of the on-board clock or relativistic effects that cause the clock to run
 * at a different rate.
 *
 * Reference: CCSDS 301.0-B-3 Blue Book, January 2002.
 *
 * @author  Jon Brumfitt
 */
public final class CucConverter {

    // The two relations above rely on the following: RESOLUTION > 2 * FINE_MOD.
    private static final int  FINE_BITS   = 16;          // Size of 'fine' field
    private static final int  COARSE_BITS = 32;          // Size of 'coarse' field
    private static final long RESOLUTION  = 1000000L;    // Microsecond resolution
    private static final long HALF_RES    = RESOLUTION / 2;
    
    private static final long FINE_MOD      = 1L << FINE_BITS;                 // 2^16
    private static final long HALF_FINE_MOD = FINE_MOD / 2;                    // 2^15
    private static final long COARSE_MOD    = 1L << COARSE_BITS;               // 2^32
    private static final long CUC_MOD       = 1L << (COARSE_BITS + FINE_BITS); // 2^48

    private long _epoch; // Microseconds offset of the CUC representation

    /**
     * Create a new CucConverter for the TAI epoch (1958-01-01T00:00:00 TAI).
     */
    public CucConverter() {
	_epoch = 0;
    }

    /**
     * Create a new CucConverter for an agency-defined epoch.<p>
     *
     * @param epoch The epoch expressed in microseconds since the TAI epoch.
     */
    public CucConverter(long epoch) {
	_epoch = epoch;
    }
    
    /**
     * Return the number of microseconds since the epoch.
     * 
     * @param cuc  Number of 1/65536 fractional seconds since
     *             epoch 1 Jan 1958 (0 <= fine < 2^48).
     * @return Number of microseconds since the epoch.
     */
    public long toMicroseconds(long cuc) {
	long coarse = cuc / FINE_MOD;
	int fine = (int)(cuc % FINE_MOD);
	return coarse * RESOLUTION + (fine * RESOLUTION + HALF_FINE_MOD) / FINE_MOD;
    }

    /**
     * Return a new TaiTime constructed from CUC coarse & fine fields.<p>
     *
     * The 'fine' value is rounded to the nearest microsecond.
     *
     * @param coarse  Number of whole seconds since epoch 1 Jan 1958 
     *        (0 <= coarse < 2^32)
     * @param fine    Number of 1/65536 fractional seconds
     *        (0 <= fine < 2^16)
     * @return the resulting TaiTime
     * @throws IllegalArgumentException if coarse<0 || fine<0 || fine>=2^16
     */
    public TaiTime toTaiTime(long coarse, int fine) {
	if((coarse < 0) || (coarse >= COARSE_MOD) || (fine < 0) || (fine >= FINE_MOD)) {
	    throw new IllegalArgumentException("coarse=" + coarse + " fine=" + fine);
	}
	long ft = coarse * RESOLUTION + (fine * RESOLUTION + HALF_FINE_MOD) / FINE_MOD;
	assert ft >= 0;
	return new TaiTime(ft + _epoch);
    }
    
    /**
     * Return a new TaiTime constructed from a 48-bit CUC time.<p>
     *
     * The value is rounded to the nearest microsecond.
     *
     * @param cuc  Number of 1/65536 fractional seconds since
     *             epoch 1 Jan 1958 (0 <= fine < 2^48).
     * @return the resulting TaiTime
     * @throws IllegalArgumentException if cuc<0 || cuc>=2^48
     */
    public TaiTime toTaiTime(long cuc) {
	if((cuc < 0) || (cuc >= CUC_MOD)) {
	    throw new IllegalArgumentException("cuc" + cuc);
	}
	long coarse = cuc / FINE_MOD;
	int fine = (int)(cuc % FINE_MOD);
	return toTaiTime(coarse, fine);
    }
    
    /**
     * Return the number of whole seconds since the TAI epoch 1 Jan 1958.
     *
     * @param t The TaiTime to be converted
     * @return Number of whole seconds since TAI epoch
     */
    public long coarse(TaiTime t) {
	long tai = t.microsecondsSince1958() - _epoch;
	if(tai < 0) {
	    throw new IllegalArgumentException("Time before TAI epoch");
	}
        return tai / RESOLUTION;
    }
    
    /**
     * Return the fractional part of the number of 1/65536 seconds since
     * the epoch 1 Jan 1958. The result is rounded to the nearest integer.
     *
     * @param t The TaiTime to be converted
     * @return Fractional part
     */
    public int fine(TaiTime t) {
	long tai = t.microsecondsSince1958() - _epoch;
	if(tai < 0) {
	    throw new IllegalArgumentException("Time before TAI epoch");
	}

	// Rounding 'fine' cannot cause a carry to 'coarse',
	// because RESOLUTION > 2 * FINE_MOD.
  	long n = tai % RESOLUTION;
	return (int)((n * FINE_MOD + HALF_RES) / RESOLUTION);
    }

    /**
     * Return the number of 1/65536 fractional seconds since the TAI epoch
     * 1 Jan 1958. The result is rounded to the nearest long.
     *
     * @param t The TaiTime to be converted
     * @return number of 1/65536 fractional seconds since TAI epoch
     */
    public long cucValue(TaiTime t) {
  	return coarse(t) * FINE_MOD + fine(t);
    }
}






