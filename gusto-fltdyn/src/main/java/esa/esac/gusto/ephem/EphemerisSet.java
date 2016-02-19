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

import java.util.List;
import java.util.Vector;

/**
 * A sequence of EphemerisBlocks.
 *
 * @author  Jon Brumfitt
 */
public class EphemerisSet implements EphemerisBlock {

    private List<AbstractEphemerisBlock> _blocks; // OrbitEphemerisBlocks
    private int _nBlocks;                 // Number of ephemeris blocks in file
    private double _tStart;               // Start time MJD2000(TDB)
    private double _tEnd;                 // End time MJD2000(TDB)

    private transient AbstractEphemerisBlock _currentBlock;

    /**
     * Create a new orbit file reader.
     */
    public EphemerisSet() {
	_blocks = new Vector<AbstractEphemerisBlock>();
    }
    
    /**
     * Create a new orbit file reader.
     */
    public EphemerisSet(double tStart, double tEnd) {
	// FIXME - The time arguments are currently ignored
	this();
    }
    
    /**
     * Add an EphemerisBlock.<p>
     * 
     * The blocks must be added in increasing time order.
     * 
     * @param block
     */
    public void addBlock(AbstractEphemerisBlock block) {
	_blocks.add(block);
	_nBlocks++;
	
	if(_nBlocks == 1) {
	    _tStart = block.getStartTime();
	}
	_tEnd = block.getEndTime();
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
     * Return the state interpolated at the specified time.
     */
    public State interpolate(double tdb) {
	AbstractEphemerisBlock block = _currentBlock;

	// First try the block we used last
        if(block != null && block.contains(tdb)) {
            return block.interpolate(tdb);
	}

	// Else perform a binary search for the block
        int lower = 0;
        int upper = _nBlocks - 1;

        while(lower <= upper) {
            int mid = (lower + upper) / 2;

            block = _blocks.get(mid);
            if(block.getStartTime() > tdb) {
                upper = mid - 1;

            } else if(block.getEndTime() < tdb) {
                lower = mid + 1;
            }
            else if(block.contains(tdb)) {
		_currentBlock = block;
                return block.interpolate(tdb);
	    }
        }
	// Should be a checked exception type
	throw new EphemerisException("Time not covered by orbit file: " + tdb);
    }
}

