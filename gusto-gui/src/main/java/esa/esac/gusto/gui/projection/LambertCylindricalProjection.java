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
 * Lambert equal-area cylindrical projection of the sky.
 *
 * @author  Jon Brumfitt
 */
public final class LambertCylindricalProjection extends AbstractProjection {

    private static final double CLIP = Math.toRadians(177.5);

    /**
     * Create a new LambertCylindricalProjection.
     */
    public LambertCylindricalProjection() {
	setViewPort(new Quaternion());
    }

    /**
     * Create a new LambertCylindricalProjection.
     */
    public LambertCylindricalProjection(Quaternion quaternion) {
	setViewPort(quaternion);
    }

    /**
     * Return the name of this projection.
     */
    public String getName() {
	return "Lambert cylindrical";
    }

    /**
     * Forward transformation.<p>
     *
     * Returns null if the point is not in the visible region.
     */
    public Vector2 forward(Vector3 vector) {
	// Transform w.r.t. rotated view-point
	Vector3 vec = getViewPort().rotateAxes(vector).mNormalize();

	double u = Math.atan2(vec.getY(), vec.getX());
	double v = vec.getZ();
	if(Math.abs(u) > CLIP) {
	    return null;
	}

	return new Vector2(-u * getScale(), v * getScale());
    }

    /**
     * Inverse transformation.
     */
    public Vector3 inverse(Vector2 pt) {
	double scale = getScale();
	double u = -pt.getX() / scale;
	double v = pt.getY() / scale;

	if(v*v > 1 || Math.abs(u) > Math.PI) {
	    return null;
	}
	
	double c = Math.sqrt(1 - v*v);
	double x = Math.cos(u) * c;
	double y = Math.sin(u) * c;
	double z = v;
	Vector3 vec = new Vector3(x, y, z).mNormalize();

	return getViewPort().rotateVector(vec);
    }
}






