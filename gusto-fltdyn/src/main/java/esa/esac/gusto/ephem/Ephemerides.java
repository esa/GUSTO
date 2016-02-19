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
import esa.esac.gusto.ephem.de405.De405Ephemerides;
import esa.esac.gusto.ephem.oem.CcsdsOemReader;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.TimeScale;
 
/**
 * Ephemerides for planets and spacecraft.
 *
 * @author  Jon Brumfitt
 */
public class Ephemerides extends BasicEphemerides {

    private static Mjd2000TimeFormat mjdTdbFmt = new Mjd2000TimeFormat(TimeScale.TDB); 

    /**
     * Create a new Ephemerides reader.
     *
     * @param orbitFile Name of orbit file
     * @param de405File Name of DE405 planetary ephemeris file
     */
    public Ephemerides(String orbitFile, String de405File) {
	super(new CcsdsOemReader(orbitFile),
	      new De405Ephemerides(de405File));
    }

    /**
     * Create a new Ephemerides reader for a specified period.
     *
     * @param orbitFile Name of orbit file
     * @param de405File Name of DE405 planetary ephemeris file
     * @param tStart Earliest time required MJD2000(TDB)
     * @param tEnd Latest time required MJD2000(TDB)
     */
    public Ephemerides(String orbitFile, String de405File, double tStart, double tEnd) {
	super(new CcsdsOemReader(orbitFile, tStart, tEnd),
	      new De405Ephemerides(de405File, tStart, tEnd));
    }
    
    /**
     * Create a new Ephemerides reader for a specified period.
     *
     * @param orbitFile Name of orbit file
     * @param de405File Name of DE405 planetary ephemeris file
     * @param interval Required time range
     */
    public Ephemerides(String orbitFile, String de405File, TimeInterval interval) {
	this(orbitFile, de405File,
	     mjdTdbFmt.TaiTimeToMjd2000(interval.start()),
	     mjdTdbFmt.TaiTimeToMjd2000(interval.finish()));
    }
}
    

