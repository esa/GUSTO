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
 
import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.time.TaiTime;
 
/**
 * Ephemerides for planets and spacecraft.<p>
 * 
 * This interface combines the orbit and planetary ephemerides, such that it is
 * possible to request the spacecraft-centric state of a planet, for example.
 *
 * @author  Jon Brumfitt
 */

public interface Ephem extends Bodies {

    /**
     * Return spacecraft-centric geometric state of a specified body.
     *
     * @param time Time of required ephemeris
     * @param body Body for which ephemeris is required
     * @return State vector from spacecraft to body
     */
    public State spacecraftTo(TaiTime time, int body);

    /**
     * Return spacecraft-centric geometric state of a specified body.
     *
     * @param tdb2000 Time of required ephemeris MJD2000(TDB)
     * @param body Body for which ephemeris is required
     * @return State vector from spacecraft to body
     */
    public State spacecraftTo(double tdb2000, int body);

    /**
     * Return barycentric geometric state of a specified body.
     */
    public State barycentricState(double tdb2000, int body);

    /**
     * Return barycentric geometric state of a specified body.
     */
    public State barycentricState(TaiTime time, int body);

    /**
     * Return geocentric geometric state of a specified body.
     */
    public State geocentricState(double tdb2000, int body);

    /**
     * Return geocentric geometric state of a specified body.
     */
    public State geocentricState(TaiTime time, int body);

    /**
     * Return the earliest time for which ephemerides are available.
     *
     * @return Earliest time in MJD2000(TDB)
     */
    public double getStartTime();

    /**
     * Return the latest time for which ephemerides are available.
     *
     * @return Earliest time in MJD2000(TDB)
     */
    public double getEndTime();

    /**
     * Return the TimeInterval covered by the ephemerides.
     */
    public TimeInterval getTimeRange();
}
    

