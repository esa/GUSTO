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
 * A TimeScale represents a system of time, such as TAI.<p>
 *
 * A time scale may simply involve a constant offset, for example Terrestrial
 * Time (TT). It may also involve a non-linear mapping, for example Barycentric
 * Dynamical Time (TDB).
 *
 * @author  Jon Brumfitt
 */
public interface TimeScale {
    public static final TimeScale UTC = new UtcTimeScale();
    public static final TimeScale TAI = new TaiTimeScale();
    public static final TimeScale TT  = new TtTimeScale();
    public static final TimeScale TDB = new TdbTimeScale();

    /**
     * Return the abbreviated name of this time scale.
     *
     * @return The name of the time scale (e.g. TAI or UTC)
     */
    public String getName();

    /**
     * Return the suffix used to denote this time scale.
     *
     * @return The suffix for this time scale (e.g. "Z" for UTC)
     */
    public String getSuffix();

    /**
     * Convert scale-time to microseconds since TAI epoch.
     *
     * @param scale The scale time in microseconds
     * @return The time in microseconds since the TAI epoch
     */
    public long scaleToTai(long scale);

    /**
     * Convert microseconds since TAI epoch to scale-time.
     *
     * @param tai The time in microseconds since the TAI epoch
     * @return The scale time in microseconds
     */
    public long taiToScale(long tai);
}

