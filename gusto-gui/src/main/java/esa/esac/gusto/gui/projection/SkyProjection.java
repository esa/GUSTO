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
import esa.esac.gusto.math.Vector2;
import esa.esac.gusto.math.Vector3;

/**
 * A projection of the celestial sphere onto a plane.
 *
 * @author  Jon Brumfitt
 */
public interface SkyProjection {

    /**
     * Return the name of this projection.
     */
    public String getName();

    /**
     * Set the scaling factor in pixels per radian.
     */
    public void setScale(double scale);

    /**
     * Return the scale factor in pixels per radian.
     */
    public double getScale();

    /**
     * Set position and orientation of centre of view.
     */
    public void setViewPort(Quaternion center);

    /**
     * Get position and orientation of centre of view.
     */
    public Quaternion getViewPort();
   
    /**
     * Project Vector returning a real point.
     */
    public Vector2 forward(Vector3 v);

    /**
     * Inverse transformation.
     */
    public Vector3 inverse(Vector2 pt);
}

