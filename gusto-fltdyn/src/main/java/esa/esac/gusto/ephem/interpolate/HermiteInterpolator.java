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
 * Hermite interpolator.
 *
 * @author  Jon Brumfitt
 */
public class HermiteInterpolator extends Interpolator {

    private static final int NV = 3;   // Number of variables
   
    private double[][] _fs;            // Dependent variables [nv][ns]
    private int _degree;

    /**
     * Create a new HermiteInterpolator.
     */
    public HermiteInterpolator(double[] x, double[][] fs, int degree) {
	super(x);

	_fs = fs;
	_degree = degree;
    }

    /**
     * Interpolate the data at the required x value.
     */
    public State interpolate(double xr) {

	int nv = NV;              // No. of dependent variables
	int n = _degree / 2 + 1;   // No. of sample points to interpolate

        n = 2 * (_degree /4 + 1); // TEMPORARY
  
	// Create array for result, initialized to zero.
	double[] f = new double[nv * 2];

	// Find range of points to interpolate over
	int[] range = interpolationRange(xr, n);
	int jj = range[0];
	int kk = range[1];

	for(int i=jj; i<=kk; i++) {
	    double fac10 = 1;
	    double fac11 = 0;
	    double fac2 = 1;
	    double sum = 0;

	    for(int j=jj; j<=kk; j++) {
		if(j != i) {
		    fac11 = fac11 * (xr - _x[j]) + fac10;
		    fac10 *= xr - _x[j];
		    fac2 *= _x[i] - _x[j];
		    sum += (1 / (_x[i] - _x[j]));
		}
	    }
	    
	    double fac22 = fac2 * fac2;

	    double a = fac10 * fac10 / fac22;  // li^2
	    double b = 2 * fac10 * fac11 / fac22;
	    
	    double phi10 = a * (xr - _x[i]);
	    double phi11 = b * (xr - _x[i]) + a;
	    double phi00 = a - 2 * sum * phi10; // hi(t)
	    double phi01 = b - 2 * sum * phi11;
	    
	    // Evaluate polynomial
	    for(int iv=0; iv<nv; iv++) {
		f[iv]      += phi00 * _fs[iv][i] + phi10 * _fs[iv + 3][i] * SEC_PER_DAY;
                f[iv + nv] += phi01 * _fs[iv][i] / SEC_PER_DAY + phi11 * _fs[iv + 3][i];
	    }
	}

	return new State(f);
    }
}

