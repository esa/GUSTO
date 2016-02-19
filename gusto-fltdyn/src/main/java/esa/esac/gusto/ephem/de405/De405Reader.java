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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import esa.esac.gusto.util.FormatException;

/**
 * Reader for DE405 planetary ephemeris files.<p>
 * 
 * The file contains a series of blocks, each corresponding to
 * an interval of 32 days.
 * 
 * @author Jon Brumfitt
 */
public class De405Reader {
    private static final double JD2000 = 2451544.5; // JD - MJD2000 (days)
    private static final int NCOEFF = 1018;         // From header.405
    private static final int VALUES = 816;          // Number of coefficients needed
    private double _firstStart;  // Start time of data (MJD2000 TDB)
    private double _lastStop;    // End time of data (MJD2000 TDB)
    private TokenizingReader _reader;
    private List<De405Interval> _intervals = new ArrayList<De405Interval>();

    /**
     * Read the whole file as a List of intervals.
     * 
     * @param is InputStream for reading the data
     */
    public void readFile(InputStream is) throws IOException {
	_reader = new TokenizingReader((is));
	De405Interval block = readBlock();
	while(block != null) {
	    _intervals.add(block);
	    block = readBlock();
	}
	_reader.close();
    }
    
    /**
     * Return specified interval, counting from 0.
     * 
     * @param number The interval number counting from 0
     * @return the interval
     */
    public De405Interval getInterval(int number) {
	return _intervals.get(number);
    }
    
    /**
     * Return the start time of the first interval (MJD2000 TDB).
     * 
     * @return the start time of the data
     */
    public double getStartTime() {
	return _firstStart;
    }
    
    /**
     * Return the end time of the last interval (MJD2000 TDB).
     * 
     * @return the end time of the data
     */
    public double getEndTime() {
	return _lastStop;
    }   
    
    /**
     * Return the next interval.
     * 
     * @return the next De405Interval or null at EOF
     */
    private De405Interval readBlock() throws IOException {
	double[] array = new double[VALUES];
	boolean ok = readBlockHeader();
	if(!ok) {
	    return null;
	}
	double previous = _lastStop;
	double startTime = _reader.nextFortranDouble() - JD2000;
	double endTime = _reader.nextFortranDouble() - JD2000;
	if(previous != 0 && startTime != previous) {
	    throw new FormatException("Block time intervals not contiguous");
	}
	if(_firstStart == 0) {
	    _firstStart = startTime;
	}
	_lastStop = endTime;
	
	int i = 0;
	while(i < VALUES) {
	    double value = _reader.nextFortranDouble();
	    array[i++] = value;
	}
	_reader.skipTokens(NCOEFF - VALUES); // Skip remainder of block

	return new De405Interval(array, startTime, endTime);
    }
    
    /**
     * Read and check the block header.
     * 
     * @return false if there is no more input
     */
    private boolean readBlockHeader() throws IOException {
	if(!_reader.hasNext()) {
	    return false;
	}
	_reader.nextInt(); // Block number
	int size = _reader.nextInt();
	if(size != NCOEFF) {
	    throw new FormatException("Invalid block size");
	}
	return true;
    }
}
