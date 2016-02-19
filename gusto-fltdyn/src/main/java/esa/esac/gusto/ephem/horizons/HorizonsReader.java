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
import java.io.InputStream;
import java.util.Iterator;

import esa.esac.gusto.ephem.EphemerisException;
import esa.esac.gusto.ephem.LineReader;
import esa.esac.gusto.ephem.State;

/**
 * Reader for Horizons state vector files.
 *
 * @author  Jon Brumfitt
 */
public class HorizonsReader implements HorizonsEphem {

    private double _tStart;      // Start time MJD2000(TDB)
    private double _tEnd;        // End time MJD2000(TDB)
    private String _fileName;
    private LineReader _reader;
    private HorizonsEphemerisBlock _block;

    /**
     * Create a new file reader.
     *
     * @param fileName Name of file
     */
    public HorizonsReader(String fileName) {
	_fileName = fileName;

	try {
	    _reader = new LineReader(fileName);
	    readFile();

	} catch(IOException e) {
	    String s = "";
	    if(_reader != null) {
		s = " at line " + _reader.getLineNumber()+ ":\n" + _reader.getLine();
	    }
	    throw new EphemerisException("Error reading Horizons file: " + fileName + s, e);
	}
    }
    
    /**
     * Create a new reader.
     *
     * @param is InputStream
     */
    public HorizonsReader(InputStream is) {
	if(is == null) {
	    throw new EphemerisException("Error reading Horizons stream");
	}
	try {
	    _reader = new LineReader(is);
	    readFile();
	    is.close();

	} catch(IOException e) {
	    String s = "";
	    if(_reader != null) {
		s = " at line " + _reader.getLineNumber()+ ":\n" + _reader.getLine();
	    }
	    throw new EphemerisException("Error reading Horizons stream: " + s, e);
	}
    }

    /**
     * Close the file.
     */
    public void close() {
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
	return (t >= _tStart) && (t <= _tEnd);
    }

    /**
     * Read the file into arrays.
     */
    private void readFile() throws IOException {
	long start = System.currentTimeMillis();

	_reader.nextLine();
	readFileHeader();

	_block = new HorizonsEphemerisBlock(_reader);
	_block.read();

	_reader.close();

	_tStart = _block.getStartTime();
	_tEnd   = _block.getEndTime();

	long stop = System.currentTimeMillis();
	System.out.println("Reading Horizons file " + _fileName
		+ " took " + (stop - start) + " ms");
    }

    /**
     * Read file header.
     */
    private void readFileHeader() throws IOException {
	expect("Center body name", "Solar System Barycenter");
	expect("Center-site name", "BODY CENTER");
	expect("Output units", "KM-S");
	expect("Output format", "02");
	expect("Reference frame", "ICRF/J2000.0");
	expect("Output type", "GEOMETRIC cartesian states");

	String line = _reader.getLine();
	while(!line.startsWith("$$SOE")) {
	    _reader.nextLine();
	    line = _reader.getLine();
	}
	_reader.nextLine();
    }

    /**
     * Check that header key has expected value.<p>
     *
     * @param key Header key
     * @param value Expected prefix of value 
     */
    private void expect(String key, String value) throws IOException {
	// Search for keyword
	String s = _reader.getLine();
	while(!s.startsWith(key) && !s.startsWith("$$SOE")) {
	    _reader.nextLine();
	    s = _reader.getLine();
	}

	if(s.startsWith("$$SOE")) {
	    throw new EphemerisException("Horizons file " + _fileName
		    + " has invalid header; expected key: " + key);
	}
	int i = s.indexOf(":");
	String v = s.substring(i + 1).trim();

	// Check value is as expected
	if(!v.startsWith(value)) {
	    throw new EphemerisException("Horizons file " + _fileName
		    + " Invalid value for: " + key);
	}
    }

    /**
     * Return the geocentric state of the body at the specified time.
     */
    public State interpolate(double tdb) {
	return _block.interpolate(tdb);
    }

    /**
     * Return an Iterator over the records.
     */
    public Iterator<EphemerisRecord> iterator() {
	return _block.iterator();
    }
}

