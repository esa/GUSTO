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
 * Lagrange interpolator.
 *
 * @author  Jon Brumfitt
 */
public class LagrangeInterpolator extends Interpolator {

    private double[][] _fs;   // Dependent variables [nv][ns]
    private int _degree;

    /**
     * Create a new LagrangeInterpolator.
     */
    public LagrangeInterpolator(double[] x, double[][] fs, int degree) {
	super(x);

	_fs = fs;
	_degree = degree;
    }

    /**
     * Interpolate the data at the required x value.
     */
    public State interpolate(double xr) {

	int nv = _fs.length;            // No. of dependent variables
	int n  = 2 * (_degree / 2 + 1);  // No. of sample points to interpolate
	
	// Create array for result, initialized to zero.
	double[] f = new double[nv];
    
	// Find range of points to interpolate over
	int[] range = interpolationRange(xr, n);
	int jj = range[0];
	int kk = range[1];

	for(int i=jj; i<=kk; i++) {

	    // Evaluate Lagrange basis function
	    double phi = 1;
	    for(int j=jj; j<=kk; j++) {
		if(j != i) {
		    phi *= (xr - _x[j]) / (_x[i] - _x[j]);
		}
	    }
	    
	    // Evaluate polynomial
	    for(int iv=0; iv<nv; iv++) {
		f[iv] += phi * _fs[iv][i];
	    }
	}

	return new State(f);
    }
}

