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

import esa.esac.gusto.math.Vector3;

/**
 * A circular region on the celestial sphere.
 * 
 * @author  Jon Brumfitt
 */
public class SkyCircle implements SkyDrawable {

    private Vector3 _center;
    private double _radius;         // Radians
    
    /**
     * Create a new SkyCircle.
     * 
     * @param center Center of circle
     * @param radius Radius in radians of arc
     */
    public SkyCircle(Vector3 center, double radius) {
	_center = center;
	_radius = radius;
    }
    
    /**
     * Draw the item on the canvas.
     * 
     * @param canvas The drawing canvas
     */
    public void draw(SkyCanvas canvas) {
	canvas.drawSmallCircle(_center, _radius);
    }
}
