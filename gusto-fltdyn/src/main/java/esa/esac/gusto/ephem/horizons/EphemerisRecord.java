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

import esa.esac.gusto.ephem.State;
import esa.esac.gusto.math.Vector3;

/**
 * A single ephemeris record.
 *
 * @author  Jon Brumfitt
 */
public class EphemerisRecord {
    
    private double _tdb;   // Time MJD2000(TDB)
    private State _state;  // State vector

    /**
     * Create a new EphemerisRecord.
     */
    public EphemerisRecord(double tdb, State state) {
	_tdb = tdb;
	_state = state;
    }

    /**
     * Return the time of this record in MJD2000(TDB)
     */
    double getTime() {
	return _tdb;
    }

    /**
     * Return the state vector (x, y, x, vx, vy, vz) in km, km/s.
     */
    State getState() {
	return _state;
    }

    /**
     * Return the position vector [km].
     */
    Vector3 getVector() {
	return _state.position();
    }
}


