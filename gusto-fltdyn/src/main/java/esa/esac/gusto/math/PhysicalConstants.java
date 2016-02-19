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

package esa.esac.gusto.math;

/**
 * Physical Constants in SI units.<p>
 * 
 * These are 2010 values from <a href="http://physics.nist.gov/cuu/Constants">NIST</a><p>
 * 
 * Note that there is currently a proposal to change the definition of the SI base units
 * so that they are based on invariants of nature. If this is approved, the values defined
 * in this class may need to be updated.
 * 
 * See: <a href="http://www.bipm.org/en/si/new_si">Revised SI</a>
 *
 * @author  Jon Brumfitt
 */
public class PhysicalConstants {

    /**
     * Speed of light in vacuum (m.s<sup>-1</sup>).
     */
    public static final double C = 2.99792458E+08; // Exact (definition of the metre)
    
    /**
     * Planck constant (J.s).
     */
    public static final double H = 6.62606957E-34;
    
    /**
     * Gravitational constant (m<sup>3</sup>.kg<sup>-1</sup>.s<sup-2</sup>).
     */
    public static final double G = 6.67384E-11;

    /**
     * Magnetic constant (permeability of free space) (N.A<sup>-2</sup>).
     */
    public static final double MU0 = Math.PI * 4E-7;
    
    /**
     * Electric constant (permittivity of free space) (F.m<sup>-1</sup>).
     */
    public static final double EPSILON0 = 1 / (C * C * MU0); // By definition
    
    /**
     * Mass of electron (kg).
     */
    public static final double ME = 9.10938291E-31;
    
    /**
     * Boltzmann constant (J.K<sup>-1</sup>).
     */
    public static final double K = 1.3806488E-23;
    
    /**
     * Elementary charge (C).
     */
    public static final double E = 1.602176565E-19;
}
