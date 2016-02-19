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

package esa.esac.gusto.ephem.horizons;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import esa.esac.gusto.ephem.AbstractEphemerisBlock;
import esa.esac.gusto.ephem.LineReader;
import esa.esac.gusto.ephem.State;

/**
 * Ephemerides from Horizons in the form of state vectors.
 *
 * @author  Jon Brumfitt
 */
public class HorizonsEphemerisBlock extends AbstractEphemerisBlock {
    
    private static final int ORDER = 10;       // Interpolation order
    
    private int _order;  // Interpolation order
    private LineReader _reader;
    
    /**
     * Create a new HorizonsEphemerisBlock.
     */
    public HorizonsEphemerisBlock(LineReader reader) {
	_reader = reader;
	_order = ORDER;
    }

    /**
     * Parse decimal JD time and return as MJD2000.<p>
     *
     * Valid for JD times in the range [2400000, 2500000).
     */
    private double parseJdTime(String s) {
	// Subtracting offset reduces rounding errors.
	// FIXME: Get the 'time' package to do this.
	if(s.startsWith("24")) {
	    s = s.substring(2);
	} else {
	    throw new IllegalArgumentException("Julian Date out of range: " + s);
	}
	return Double.parseDouble(s) - 51544.5; // MJD2000 = JD - 2451544.5
    }
    
    /**
     * Read the file.
     */
    public boolean read() throws IOException {
	createArrays();
	return readEphemerisBlock();
    }

    /**
     * Read the ephemeris records.
     */
    protected boolean readEphemerisBlock() throws IOException {
	_nSamples = 0;

	// Read the ephemeris records
	while((_reader.getLine() != null) && (!_reader.getLine().equals("$$EOE"))) {
	    ensureSize(_nSamples + 1);

	    StringTokenizer st = new StringTokenizer(_reader.getLine());
		
	    // Read time and convert to MJD2000
	    _x[_nSamples] = parseJdTime(st.nextToken());

	    _reader.nextLine();
	    st = new StringTokenizer(_reader.getLine());

	    // Read state vector (km & km/s)
	    for(int i=0; i<3; i++) {
		_fs[i][_nSamples] = Double.parseDouble(st.nextToken());
	    }
	    _reader.nextLine();
	    st = new StringTokenizer(_reader.getLine());
	    for(int i=3; i<6; i++) {
		_fs[i][_nSamples] = Double.parseDouble(st.nextToken());
	    }
	    _reader.nextLine();
	    _nSamples++;
	}

	_tStart = _x[0];
	_tEnd = _x[_nSamples - 1];

	resizeArrays(_nSamples);
	
	setInterpolator("HERMITE", _order);

	return (_reader.getLine() != null);
    }   

    /**
     * Return an Iterator over the ephemeris records/
     */
    public Iterator<EphemerisRecord> iterator() {
	return new Iterator<EphemerisRecord>() {
	    private int _i = 0;

	    public boolean hasNext() {
		return _i < _nSamples;
	    }

	    public EphemerisRecord next() {
		State state = new State(_fs[0][_i],
					_fs[1][_i],
					_fs[2][_i],
					_fs[3][_i],
					_fs[4][_i],
					_fs[5][_i]);

		return new EphemerisRecord(_x[_i++], state);
	    }

	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
}


