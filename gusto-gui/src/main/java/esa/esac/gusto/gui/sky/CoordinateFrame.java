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

package esa.esac.gusto.gui.sky;

import esa.esac.gusto.math.Coordinates;
import esa.esac.gusto.math.Quaternion;

/**
 * A celestial coordinate frame.
 * 
 * @author  Jon Brumfit
 */
public enum CoordinateFrame {
    EQUATORIAL, ECLIPTIC, GALACTIC;
    
    /**
     * Return the CoordinateFrame with a given name.
     *
     * @param name the coordinate frame
     * @return the CoordinateFrame
     * @throws IllegalArgumentException if the supplied name is invalid
     */
    public static CoordinateFrame get(String name) {
        return Enum.valueOf(CoordinateFrame.class, name.toUpperCase());
    }
    
    /**
     * Return the transformation from one frame to another.
     */
    public static Quaternion transformation(CoordinateFrame from, CoordinateFrame to) {
	switch(from) {
	case EQUATORIAL:
	    switch(to) {
	    case EQUATORIAL:
		return new Quaternion();
	    case ECLIPTIC:
		return Coordinates.equToEclFrame();
	    case GALACTIC:
		return Coordinates.equToGalFrame();
	    }
	case ECLIPTIC:
	    switch(to) {
	    case EQUATORIAL:
		return Coordinates.eclToEquFrame();
	    case ECLIPTIC:
		return new Quaternion();
	    case GALACTIC:
		return Coordinates.eclToGalFrame();
	    }
	case GALACTIC:
	    switch(to) {
	    case EQUATORIAL:
		return Coordinates.galToEquFrame();
	    case ECLIPTIC:
		return Coordinates.galToEclFrame();
	    case GALACTIC:
		return new Quaternion();
	    }
	}
	return null;
    }
}
