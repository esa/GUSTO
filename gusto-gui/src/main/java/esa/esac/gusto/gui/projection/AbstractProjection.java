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

package esa.esac.gusto.gui.projection;

import esa.esac.gusto.math.Quaternion;

/**
 * Abstract projection of the sky.
 *
 * @author  Jon Brumfitt
 */
public abstract class AbstractProjection implements SkyProjection {
    
    private static final double DEFAULT_SCALE = 300; // Pixels/radian

    protected Quaternion _quaternion;
    protected double _scale = DEFAULT_SCALE;

    /**
     * Set the scaling factor in pixels per radian.
     */
    public void setScale(double scale) {
	_scale = scale;
    }

    /**
     * Return the scale factor in pixels per radian.
     */
    public double getScale() {
	return _scale;
    }

    /**
     * Set position and orientation of centre of view.
     */
    public void setViewPort(Quaternion center) {
	_quaternion = center;
    }

    /**
     * Get position and orientation of centre of view.
     */
    public Quaternion getViewPort() {
	return _quaternion;
    }

   /**
     * Return a String representation of this projection.
     */
    public String toString() {
	StringBuffer buff = new StringBuffer(getName());
	buff.append("centre=" + _quaternion);
	buff.append(", scale=" + _scale);
	return buff.toString();
    }
}

