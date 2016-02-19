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

package esa.esac.gusto.ephem.de405;

import esa.esac.gusto.ephem.State;

/**
 * An interval within a DE405 file.<p>
 * 
 * DE405 ephemeris data is split into intervals of 32 days each. 
 * Each interval is further sub-divided into sub-intervals.
 * 
 * @author Jon Brumfitt
 */
public class De405Interval {

    // Seconds per day.
    private static final int SEC_PER_DAY = 86400;
    
    // Length in days of each interval of the DE405 file.
    private static final int DAYS_PER_INTERVAL = 32;
    
    // Start locations of each body in an interval (from GROUP 1050).
    // These assume start/end times are included and the array index starts at 1.
    private static final int[] START = new int[] {
	0, 3, 171, 231, 309, 342, 366, 387, 405, 423, 441, 753, 819, 899 // Body 0 is unused
    };
    
    // Number of Chebyshev coefficients used for each body (from GROUP 1050)
    private static final int[] NCOEFF = new int[] {
	0, 14, 10, 13, 11, 8, 7, 6, 6, 6, 13, 11, 10, 10  // Body 0 is unused
    };
    
    // Number of coefficient sets for each body (from GROUP 1050)
    private static final int[] NSETS = new int[] {
	0, 4, 2, 2, 1, 1, 1, 1, 1, 1, 8, 2, 4, 4  // Body 0 is unused
    };
    
    private double _startTime; // Start time of this interval (MJD2000 TDB)
    private double _endTime;   // End time of this interval (MJD2000 TDB)    
    private double[] _coeffs;  // Coefficients
    
    /**
     * Create a new De405Interval.
     * 
     * @param coeffs Coefficient array
     * @param start Start time of the interval
     * @param stop End time of the interval
     */
    public De405Interval(double[] coeffs, double start, double stop) {
	_coeffs = coeffs;
	_startTime = start;
	_endTime = stop;
    }

    /**
     * Return the start time of the interval MJD2000(TDB).
     */
    public double getStartTime() {
        return _startTime;
    }
    
    /**
     * Return the end time of the interval MJD2000(TDB).
     */
    public double getEndTime() {
        return _endTime;
    }
    
    /**
     * Calculates state of a body at a specified time.<p>
     *
     * @param mjd2000 Time MJD2000 (TDB)
     * @param de405Body Body using DE405 internal numbering convention
     * @return State at the specified time
     */
    public State state(double mjd2000, int de405Body) {

	// Structure parameters from group 1050 of header.405
	int ncoeff = NCOEFF[de405Body];
	int nsets = NSETS[de405Body];
	int start = START[de405Body];
	
	// Find the correct sub-interval
	double timeOffset = mjd2000 - _startTime;
	double subIntervalDuration = DAYS_PER_INTERVAL / nsets;
	int subinterval = (int)(Math.floor(timeOffset) / subIntervalDuration);

	// Find Chebyshev time within the subinterval in the range [-1,+1]
	double subStartTime = subinterval * subIntervalDuration;
	double ctime = 2 * (timeOffset - subStartTime) / subIntervalDuration - 1;
	
	// Find index of start of sub-interval in the array
	int first = (start - 3) + subinterval * 3 * ncoeff;

	// Chebyshev position polynomial	
	double[] _pc = new double[ncoeff];
	_pc[0] = 1;
	_pc[1] = ctime;
	for(int j=2; j<ncoeff; j++) {
	    _pc[j] = 2 * ctime * _pc[j-1] - _pc[j-2];
	}

	// Chebyshev velocity polynomial
	double[] _vc = new double[ncoeff];
	_vc[0] = 0;
	_vc[1] = 1;
	_vc[2] = 4 * ctime;
	for(int j=3; j<ncoeff; j++) {
	    _vc[j] = 2 * ctime * _vc[j-1] + 2 * _pc[j-1] - _vc[j-2];
	}
	
	// Evaluate polynomials
	double derivScale = nsets * 2.0d / DAYS_PER_INTERVAL / SEC_PER_DAY;
	int p = first;
	double[] state = new double[6];
	for(int j=0; j<3; j++) {
	    for(int k=0; k<ncoeff; k++) {
		state[j]   += _coeffs[p] * _pc[k];
		state[j+3] += _coeffs[p++] * _vc[k];
	    }
	    // Scale derivative to km/s.
	    state[j+3] *=  derivScale;
	}

	return new State(state);
    }
}
