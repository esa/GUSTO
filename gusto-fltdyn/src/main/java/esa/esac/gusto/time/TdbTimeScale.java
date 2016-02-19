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
 * Barycentric Dynamic Time (TDB).<p>
 *
 * This is the ideal time at the Solar System Barycentre (centre of mass),
 * taking into account relativistic corrections. The difference between
 * TDB and TT is less than 2 milliseconds.
 *
 * @author  Jon Brumfitt
 */
public class TdbTimeScale implements TimeScale {

    private static final int TT_TAI = 32184000;  // TT - TAI microseconds

    /*
     * Strictly, this should be MJD(UT) but the error is negligible
     * (about 0.02 microseconds maximum when TAI-UTC = 32 seconds).
     */
    private static final MjdTimeFormat _mjdTaiFmt = new MjdTimeFormat(TimeScale.TAI);
    private static final Mjd2000TimeFormat _mjd2000TaiFmt = new Mjd2000TimeFormat(TimeScale.TAI);

    public String getName() {
	return "TDB";
    }

    public String getSuffix() {
	return "TDB";
    }

    public long scaleToTai(long scale) {
	long tai1 = scale - TT_TAI;      // First approximation to TAI
	return tai1 - tdbMinusTt(tai1);  // Improved approximation
    }

    public long taiToScale(long tai) {
	return tai + TT_TAI + tdbMinusTt(tai);
    }

    /**
     * Return the difference TDB-TT for a time given in TAI.<p>
     *
     * See P.K.Seidelmann (ed.), "Explanatory Supplement to the Astronomical
     * Almanac", pp 42, University Science Books, 1992.
     */
    @SuppressWarnings("unused")  // Alternative definition - not currently used
    private long tdbMinusTtStandard(long tai) {
	// Time in days since J2000.0 epoch (mid-day) (not MJD2000 which is since midnight).
	double t = _mjd2000TaiFmt.TaiTimeToMjd2000(new TaiTime(tai)) - 0.5;
	double g = (357.53 + 0.9856003 * t) * Math.PI / 180;
	double offset = 0.001658 * Math.sin(g) + 0.000014 * Math.sin(2*g);
	return (long)Math.round(offset * 1000000);
    }

    /**
     * Return the difference TDB-TT for a time given in TAI.<p>
     *
     * Based on ESOC OASW library Fortran function TDBUTC, by Trevor Morley.
     * This uses a more accurate formula derived from Moyer (1981),
     * neglecting terms with an amplitude less than 21 microseconds.<p>
     *
     * This algorithm seems to have little advantage over the standard one,
     * as the two are within 2 microseconds of each other and it negects
     * terms less than 21 microseconds.
     *
     * See P.K.Seidelmann (ed.), "Explanatory Supplement to the Astronomical
     * Almanac", pp 42-45, University Science Books, 1992.
     *
     * @param tai TAI microseconds since TAI epoch
     * @return TDB-TT microseconds
     */
    private long tdbMinusTt(long tai) {
	double mjd2000 = _mjdTaiFmt.TaiTimeToMjd(new TaiTime(tai - 51544 * 86400L * 1000000));

	final double cof = 0.0016567d;
	final double ecc = 0.01671d;  // Eccentricity of orbit of Earth-Moon barycentre
	final double rme = 6.231435d;
	final double rmd = 0.01720197d;

	double rm = rme + rmd * mjd2000;  // Mean anomaly of orbit of Earth-Moon barycentre
	double ea = rm +  ecc * (Math.sin(rm) + 0.5 * ecc * Math.sin(2 * rm));  // Eccentric anomaly
	double dt = cof * Math.sin(ea);

	return (long)Math.round(dt * 1000000);
    }
}


