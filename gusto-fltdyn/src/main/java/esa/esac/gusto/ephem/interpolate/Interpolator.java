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

package esa.esac.gusto.ephem.interpolate;

import esa.esac.gusto.ephem.State;

/**
 * Interpolator for ephemerides.
 */
public abstract class Interpolator {
    
    protected static final int SEC_PER_DAY = 86400; // Seconds in a day

    protected double[] _x;                          // Independent variables
    private int _nSamples;                          // Number of samples in the block

    /**
     * Create a new Interpolator.
     */
    protected Interpolator(double[] x) {
	_x = x;
	_nSamples = x.length;
    }
    
    /**
     * Return the number of samples.
     */
    public int getSampleCount() {
	return _x.length;
    }

    /**
     * Interpolate the data at the required x value.
     */
    public abstract State interpolate(double xr);

    /**
     * Check whether block contains a given time.<p>
     *
     * The block is treated as a closed interval.
     */
    public boolean contains(double t) {
	return (t >= _x[0]) && (t <= _x[_nSamples-1]);
    }

    /**
     * Return starting index 'i' of row containing 't'.<p>
     *
     * Returns the index of the largest element that is
     * less than or equal to 't':<p>
     *
     * _t[i] <= t < _t[i+1]
     */
    protected int findIndex(double t) {
	if(!contains(t)) {
	    throw new IllegalArgumentException("Time " + t + " is outside block: ["
					       + _x[0] + "," + _x[_nSamples-1] + "]");
	}

	// Binary search
	int j = 0;
	int k = _nSamples - 1;
	while (k - j > 1) {
	    int i = (k + j) >> 1;
	    if (_x[i] > t)
		k = i;
	    else 
		j = i;
	}
	return j;
    }

    /**
     * Return the start/end indices of the interpolation range.<p>
     *
     * The width of the range is reduced towards each end of the block.
     */
    protected int[] interpolationRange(double xr, int n) {
	int i = findIndex(xr);
	int j = i - n / 2 + 1;
	int k = j + n - 1;

	// If range extends beyond end of block, shift top down
	if(k >= _nSamples) {
	    k = _nSamples - 1;
	}

	// If range extends below start of block, shift bottom up
	if(j < 0) {
	    j = 0;
	}

	return new int[] {j, k};
    }
}

