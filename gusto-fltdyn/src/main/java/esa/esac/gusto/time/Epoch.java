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

package esa.esac.gusto.time;

/**
 * Constants defining various time epochs.
 *
 * @author  Jon Brumfitt
 */
public final class Epoch {
    
    /** TAI-UTC seconds at 1 Jan 1972. */
    private static final int LEAP_1972 = 10;

    /** Microseconds (epoch 1958) of 1 Jan 1972 UTC. */
    private static final long UTC_72 = (((1972 - 1958) * 365 + 3) * 86400L + LEAP_1972)
	                               * 1000000;

    /**
     * The J2000.0 TT epoch.<p>
     *
     * This is defined as JD 2451545.0 TT (IAU 1994). Note that this is
     * 2000-01-01T12:00:00 TT (i.e. mid day).<p>
     *
     * Note: The J2000.0 epoch is sometimes defined as JD 2451545.0 TDB.
     * However, the difference is negligible for most practical purposes.
     */
    public static final TaiTime J2000 = (new MjdTimeFormat(TimeScale.TT))
	                                 .mjdToTaiTime(51544.5);

    /**
     * The TAI 1958 epoch.<p>
     *
     * This is the origin of the TAI time scale and is defined to be
     * 1958-01-01 00:00:00 UT.
     */
    public static final TaiTime TAI_1958 = new TaiTime(0);

    /**
     * The epoch at which UTC was introduced.<p>
     *
     * Universal Time Coordinated (UTC) was introduced in 1972. At this
     * time, the difference between UTC and TAI was: UTC = TAI - 10 seconds.
     */
    public static final TaiTime UTC_1972 = new TaiTime(UTC_72);
}



