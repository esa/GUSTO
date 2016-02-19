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
 * Coordinated Universal Time (UTC).<p>
 *
 * This scale is linear with respect to TAI. Leap seconds do not introduce
 * discontinuities into the time scale; they simply get interpreted by
 * the UTC Gregorian calendar, in a similar way to leap years.
 *
 * @author  Jon Brumfitt
 */
public class UtcTimeScale implements TimeScale {

    public String getName() {
	return "UTC";
    }

    public String getSuffix() {
	return "Z";
    }

    public long scaleToTai(long scale) {
	return scale;
    }

    public long taiToScale(long tai) {
	return tai;
    }
}

