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
 * Hammer-Aitoff projection of the sky.
 *
 * @author  Jon Brumfitt
 */
public final class HammerProjection extends AbstractProjection {
    private static final double SQRT2 = Math.sqrt(2);
    
    private final double K = Math.cos(Math.toRadians(175.0));
    private final double K2 = K * K;

    /**
     * Create a new HammerProjection.
     */
    public HammerProjection() {
	setViewPort(new Quaternion());
    }

    /**
     * Create a new HammerProjection.
     */
    public HammerProjection(Quaternion quaternion) {
	setViewPort(quaternion);
    }

    /**
     * Return the name of this projection.
     */
    public String getName() {
	return "Hammer";
    }

    /**
     * Forward transformation.<p>
     *
     * Returns null if point is not visible.
     */
    public Vector2 forward(Vector3 vector) {
	
	Vector3 vec = getViewPort().rotateAxes(vector).mNormalize();

	double x = vec.getX();
	double y = vec.getY();
	double z = vec.getZ();
	double r2 = x * x + y * y;

	if(x < 0 && x*x > K2 * r2) return null;
	
	double r = Math.sqrt(r2);
	double c = Math.sqrt((r + x) * r / 2);
	double s = Math.sqrt((r - x) * r);

	if(y < 0) s = -s;

	double d = Math.sqrt(1 + c);
	double u = 2 * s / d;
	double v = SQRT2 * z / d;
	
	double scale = getScale();
	return new Vector2(-u * scale, v * scale);
    }

    /**
     * Inverse transformation.<p>
     *
     * Returns null if the point is outside the sphere.
     */
    public Vector3 inverse(Vector2 pt) {
	double scale = getScale();
	double u = -pt.getX() / scale;
	double v = pt.getY() / scale;
	
	double r2 = 1 - u * u / 16 - v * v / 4;
	if(r2 < 0.5) return null;
	
	double r = Math.sqrt(r2);
	double a = r * u;
	double z = r * v;
	double c = 2 * (2 * r2 - 1);
	double d2 = a * a + c * c;
	double b = Math.sqrt(1 - z * z);
	double x = b * (c * c - a * a) / d2;
	double y = 2 * b * a * c / d2;
	
	Vector3 vec = new Vector3(x, y, z);
	return getViewPort().rotateVector(vec);
    }
}







