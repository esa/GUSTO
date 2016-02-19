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
 * Constants representing Solar System bodies.
 *
 * @author  Jon Brumfitt
 */
public interface Bodies {

    public static final int SPACECRAFT =  0;
    public static final int MERCURY    =  1;
    public static final int VENUS      =  2;
    public static final int EARTH      =  3;
    public static final int MARS       =  4;
    public static final int JUPITER    =  5;
    public static final int SATURN     =  6;
    public static final int URANUS     =  7;
    public static final int NEPTUNE    =  8;
    public static final int PLUTO      =  9;
    public static final int MOON       = 10;
    public static final int SUN        = 11;
    
    /** Solar-system barycentre */
    public static final int SS_BARY    = 12;
    
    /** Earth-Moon barycentre */
    public static final int EM_BARY    = 13;
}
