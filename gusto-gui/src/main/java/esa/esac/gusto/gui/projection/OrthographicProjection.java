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
 * Orthographic projection of the sky.<p>
 *
 * An orthographic projection is a view from infinity that preserves 
 * neither area nor angle.<p>
 *
 * The view is seen as if looking at the sky and is therefore reversed compared
 * with traditional map projections that look from outside the sphere. When the
 * view is fully zoomed out, it is possible to see a complete hemisphere. This 
 * is the far half of the celestial sphere with the front half removed.
 *
 * @author  Jon Brumfitt
 */
public final class OrthographicProjection extends AbstractProjection {

    /**
     * Create a new OrthographicProjection.
     */
    public OrthographicProjection() {
	setViewPort(new Quaternion());
    }

    /**
     * Create a new OrthographicProjection.
     */
    public OrthographicProjection(Quaternion quaternion) {
	setViewPort(quaternion);
    }

    /**
     * Return the name of this projection.
     */
    public String getName() {
	return "Orthographic";
    }

    /**
     * Forward transformation.<p>
     *
     * Returns null if point is not in visible hemisphere.
     */
    public Vector2 forward(Vector3 vector) {
	Vector3 vec = _quaternion.rotateAxes(vector).mNormalize();
	if(vec.getX() < 0) {
	    return null;
	}
        return new Vector2(-vec.getY() * _scale, vec.getZ() * _scale);
    }

    /**
     * Inverse transformation.<p>
     *
     * Returns null if the point is outside the sphere.
     */
    public Vector3 inverse(Vector2 pt) {
	double u = -pt.getX() / _scale;
	double v = pt.getY() / _scale;

	double rSq = u * u + v * v;
	if(rSq > 1) {
	    return null;    // Outside sphere
	}
	double z = Math.sqrt(1 - rSq);

	Vector3 vec = new Vector3(z, u, v);
	return _quaternion.rotateVector(vec);
    }
}

