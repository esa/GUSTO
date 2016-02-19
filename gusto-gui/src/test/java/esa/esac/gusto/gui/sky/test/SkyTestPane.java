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

package esa.esac.gusto.gui.sky.test;

import esa.esac.gusto.gui.sky.SkyPane;
import esa.esac.gusto.math.Coordinates;
import esa.esac.gusto.math.Vector3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * A SkyPane that handles mouse events for testing.
 *
 * @author  Jon Brumfitt
 */
class SkyTestPane extends SkyPane {
    private static final long serialVersionUID = 1L;
    
    SkyTestPane() {
//	setGraticuleFrame(CoordinateFrame.ECLIPTIC);
    }

    public void drawContents(Graphics g) {
	super.drawContents(g);
	double radius = Math.toRadians(0.001);

	Font font = new Font("Sans", Font.PLAIN, 10);
	g.setFont(font);
	
	g.setColor(Color.YELLOW);
	Vector3 y = new Vector3(0,1,0);
	this.drawSmallCircle(y, radius);
	drawText(y, "Y-EQU");
	
	Vector3 yEcl = Coordinates.equToEclFrame().rotateVector(y);
	this.drawSmallCircle(yEcl, radius);
	drawText(yEcl, "Y-ECL");
	
	Vector3 yGal = Coordinates.equToGalFrame().rotateVector(y);
	this.drawSmallCircle(yGal, radius);
	drawText(yGal, "Y-GAL");
    }
}







