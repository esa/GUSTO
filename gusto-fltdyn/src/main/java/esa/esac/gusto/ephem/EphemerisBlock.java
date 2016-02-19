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

/**
 * Reads CCSDS orbit file and interpolates ephemerides.
 *
 * @author  Jon Brumfitt
 */
public interface EphemerisBlock {
    
    /**
     * Return the earliest time available MJD2000(TDB).
     */
    public double getStartTime();

    /**
     * Return the latest time available MJD2000(TDB).
     */
    public double getEndTime();

    /**
     * Return the state interpolated at the specified time.
     * 
     * @param tdb Required time in MJD2000(TDB)
     * @return State vector [x, y, z, vx, vy, vz] in km, km/s
     * @throws EphemerisException
     */
    public State interpolate(double tdb);
    
    
    /**
     * Check whether block contains a given time.<p>
     *
     * The block is treated as a closed interval.
     */
    public boolean contains(double t); 
}

