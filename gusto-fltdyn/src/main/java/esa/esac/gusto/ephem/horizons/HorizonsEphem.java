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

import java.util.Iterator;

import esa.esac.gusto.ephem.EphemerisBlock;
import esa.esac.gusto.ephem.State;

/**
 * Horizons ephemerides.
 *
 * @author  Jon Brumfitt
 */
public interface HorizonsEphem extends Iterable<EphemerisRecord>,
                                       EphemerisBlock {

    /**
     * Return the earliest time available MJD2000(TDB).
     */
    public double getStartTime();
    
    /**
     * Return the latest time available MJD2000(TDB).
     */
    public double getEndTime();

    /**
     * Return the geocentric state of the body at the specified time.
     */
    public State interpolate(double tdb);

    /**
     * Return an Iterator over the records.
     */
    public Iterator<EphemerisRecord> iterator();
}

