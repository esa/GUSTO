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

package esa.esac.gusto.ephem;

import esa.esac.gusto.ephem.interpolate.HermiteInterpolator;
import esa.esac.gusto.ephem.interpolate.Interpolator;
import esa.esac.gusto.ephem.interpolate.LagrangeInterpolator;

/**
 * A block of ephemeris records.
 *
 * @author  Jon Brumfitt
 */
public abstract class AbstractEphemerisBlock implements EphemerisBlock {

    protected static final int SEC_PER_DAY = 86400; // Seconds in a day
    protected static final int NV = 6;              // Number of variables

    private static final int N = 50;             // Initial size of arrays
    private static final double GROW = 1.5;      // Factor for growing array size
	
    private Interpolator _interpolator;          // Interpolator for data
    protected double[] _x;                       // Independent variables
    protected double[][] _fs;                    // Dependent variables [nv][ns]
    protected double _tStart = Double.NaN;       // Start time of block MJD2000(TDB)
    protected double _tEnd = Double.NaN;         // End time of block MJD2000(TDB)
    protected int _nSamples = 0;                 // Number of samples in the block

    /**
     * Create a new EphemerisBlock.
     */
    public AbstractEphemerisBlock() {
    }

    /**
     * Return the earliest time available MJD2000(TDB).
     */
    public double getStartTime() {
	return _tStart;
    }

    /**
     * Return the latest time available MJD2000(TDB).
     */
    public double getEndTime() {
	return _tEnd;
    }
    
    /**
     * Check whether block contains a given time.<p>
     *
     * The block is treated as a closed interval.
     */
    public boolean contains(double t) {
	return (t >= _x[0]) && (t <= _x[_nSamples-1]);
    }
  
    // ---------- Interpolation ----------
    
    /**
     * Interpolate to obtain state at a given time.
     */
    public State interpolate(double xr) {
	return _interpolator.interpolate(xr);
    }	
    
    /**
     * Set the interpolator for this block.
     */
    public void setInterpolator(String interpolationType, int order) {
	if(interpolationType.equals("HERMITE")) {
	    _interpolator = new HermiteInterpolator(_x, _fs, order);

	} else if(interpolationType.equals("LAGRANGE")) {
	    _interpolator = new LagrangeInterpolator(_x, _fs, order);

	} else {
	    throw new IllegalArgumentException("Unsuported interpolation method: "
		    	                       + interpolationType);
	}
    }
    

    //---------- Storage management ----------

    /**
     * Create arrays to hold data.
     */
    protected void createArrays() {
	_x = new double[N];
	_fs = new double[NV][];

	for(int i=0; i<NV; i++) {
	    _fs[i] = new double[N];
	}
    }

    /**
     * Grow the arrays if necessary to at least 'length' elements.
     */
    protected void ensureSize(int length) {
	if(length > _x.length) {
	    int n = (int)(length * GROW);
	    resizeArrays(n);
	}
    }

    /**
     * Resize the arrays to n elements.
     */
    protected void resizeArrays(int n) {
	double[] old = _x;
	_x = new double[n];
	System.arraycopy(old, 0, _x, 0, _nSamples);
	
	for(int i=0; i<NV; i++) {
	    old = _fs[i];
	    _fs[i] = new double[n];
	    System.arraycopy(old, 0, _fs[i], 0, _nSamples);
	}
    }

    
    // ---------- Various ----------
    
    /**
     * Return a String representation of the block.
     */
    public String toString() {
	StringBuffer buff = new StringBuffer();
	for(int i=0; i<_nSamples; i++) {
	    buff.append(_x[i] + "\nfs=");
	    for(int j=0; j<NV; j++) {
		buff.append(_fs[j][i] + " ");
	    }
	    buff.append("\n");
	}
	return buff.toString();
    }
}

