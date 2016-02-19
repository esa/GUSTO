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

import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector2;
import esa.esac.gusto.math.Vector3;

/**
 * Mollweide projection of the sky.<p>
 *
 * The Mollweide projection is also known as the elliptical projection or 
 * homolographic equal-area projection. It has the useful property that 
 * it is area-preserving. Meridians map onto ellipses, whilst parallels
 * map onto straight lines.<p>
 *
 * The aspect ratio is adjusted such that the X and Y scale factors are
 * the same at the centre of the projection, so that the projection
 * asymptotically approaches the other projections, as the view is 
 * zoomed in.
 *
 * @author  Jon Brumfitt
 */
public final class MollweideProjection extends AbstractProjection {
    
    private static final boolean DEBUG = false;

    private static final double PI = Math.PI;
    private static final double TWO_PI = Math.PI * 2;

    /**
     * Create a new MollweideProjection.
     */
    public MollweideProjection() {
	setViewPort(new Quaternion());
    }

    /**
     * Create a new MollweideProjection.
     */
    public MollweideProjection(Quaternion quaternion) {
	setViewPort(quaternion);
    }

    /**
     * Return the name of this projection.
     */
    public String getName() {
	return "Mollweide";
    }

    /**
     * Forward transformation.<p>
     *
     * Returns null if point is not in visible hemisphere.
     */
    public Vector2 forward(Vector3 vector) {

	// Transform w.r.t. rotated view-point
	Vector3 vec = _quaternion.rotateAxes(vector);
	Direction dir = new Direction(vec);

	double alpha = dir.getAlpha();
	double delta = dir.getDelta();

	// Adjust alpha to the range +/- PI
	while(alpha <= 0) {
	    alpha += TWO_PI;
	}
	while(alpha > PI) {
	    alpha -= TWO_PI;
	}

	// Don't plot quite up to poles to avoid strange effects
	if(Math.abs(delta) > Math.toRadians(89.99)) {
	    return null;
	}

	// Don't plot quite to +/- PI to avoid plotted line segments
	// jumping across the sphere.
	double al = Math.toDegrees(alpha);
	if(Math.abs(al) > 175) {
	    return null;
	}

	// Initial approximation using polynomial
	final double a = -(PI - PI*PI*PI / 8) / 6 / 2;
	final double b = -(PI - PI*PI*PI*PI*PI / 32) / 120 / 2 / 2; // Spurious 2 and sign !!!
	double psi = delta * (PI/2 + delta*delta * (a + delta*delta * b));
	//psi = delta * Math.PI / 2 + delta*delta*delta*0.064;  // Initial approximation
	
	// Solve 2*psi + sin(2*psi) = PI*sin(delta) by Newton-Raphson iteration
	double piSinD = PI * Math.sin(delta);
	double dpsi = 0;
	//	System.out.println("\n" + delta + " " +  psi);
	do {
	    dpsi = (psi + Math.sin(psi) - piSinD) / (1 + Math.cos(psi));
	    psi -= dpsi;
	    //System.out.println(psi);
	} while(Math.abs(dpsi) > 0.001);

	// For testing, print warning if approximation error is significant
	if(DEBUG) {
	    double deltaE = Math.asin((psi + Math.sin(psi)) / PI);
	    if(Math.abs(Math.toDegrees(deltaE - delta)) > ( 0.2 / 3600.0)) { // 0.2 arcsec
		System.out.println("Warning: projection error = "
				   + Math.toDegrees(deltaE - delta) + " degrees");
	    }
	}
	    
	// The aspect ratio is such that the X & Y scales are the same at the centre.
	double theta = psi / 2;
	double u = alpha * Math.cos(theta) * _scale;
	double v = 4 / PI * Math.sin(theta) * _scale;
 
        return new Vector2(-u, v);
    }

    /**
     * Inverse transformation.<p>
     *
     * Returns null if the point is outside the sphere.
     */
    public Vector3 inverse(Vector2 pt) {
	double u = -pt.getX() / _scale;
	double v =  pt.getY() / _scale;

	// Check whether point is inside bounding ellipse
	double PI_SQ = PI * PI;
	double c = v * PI / 4;

	double rSq = u * u / PI_SQ + c * c;
	if(rSq > 1) {
	    return null;    // Outside sphere
	}

	double psi = 2 * Math.asin(c);

	double zz = (psi + Math.sin(psi)) / PI;
	double ca = Math.sqrt(1 - zz*zz);
	double ct = Math.sqrt(1 - c*c);
	double xx = ca * Math.cos(u / ct);
	double yy = ca * Math.sin(u / ct);
	Vector3 vec = new Vector3(xx, yy, zz);

	return _quaternion.rotateVector(vec);
    }
}

