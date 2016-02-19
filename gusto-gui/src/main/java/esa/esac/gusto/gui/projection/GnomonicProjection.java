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
 * Gnomonic projection of the sky.<p>
 *
 * A gnomonic projection is a non-conformal azimuthal map projection. It has
 * the useful property that great circles map onto straight lines.
 *
 * @author  Jon Brumfitt
 */
public final class GnomonicProjection extends AbstractProjection {

    // Clip the projection to 71 degrees (90 degrees maps to infinity)
    private static final double CLIP = Math.tan(Math.toRadians(71));
    private static final double CLIP_SQ = CLIP * CLIP;

    /**
     * Create a new GnomonicProjection.
     */
    public GnomonicProjection() {
	setViewPort(new Quaternion());
    }

    /**
     * Create a new GnomonicProjection.
     */
    public GnomonicProjection(Quaternion quaternion) {
	setViewPort(quaternion);
    }

    /**
     * Return the name of this projection.
     */
    public String getName() {
	return "Gnomonic";
    }

    /**
     * Forward transformation.<p>
     *
     * Returns null if the point is not in the visible region.
     */
    public Vector2 forward(Vector3 vector) {
	// Transform w.r.t. rotated view-point
	Vector3 vec = _quaternion.rotateAxes(vector);

	// Project only one hemisphere
	double x  = vec.getX();
	if(x <= 0) {
	    return null;
	}

	// Forward projection
	double u = vec.getY() / x;
	double v = vec.getZ() / x;

	// Clip the projection to prevent extreme distortion
	if(u*u + v*v > CLIP_SQ) {
	    return null;
	}

	return new Vector2(-u * _scale, v * _scale);
    }

    /**
     * Inverse transformation.
     */
    public Vector3 inverse(Vector2 pt) {
	double u = -pt.getX() / _scale;
	double v = pt.getY() / _scale;

	Vector3 vec = new Vector3(1, u, v).mNormalize();

	return _quaternion.rotateVector(vec);
    }
}

