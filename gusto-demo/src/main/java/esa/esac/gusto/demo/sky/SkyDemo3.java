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
package esa.esac.gusto.demo.sky;

import java.awt.Color;
import java.awt.Dimension;

import esa.esac.gusto.gui.sky.SimpleSkyFrame;
import esa.esac.gusto.gui.sky.SkyCanvas;
import esa.esac.gusto.gui.sky.SkyDrawable;
import esa.esac.gusto.gui.sky.SkyPane;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Vector3;

/**
 * Example of very simple application that draws a small circle.<p>
 * 
 * This example defines a circle as a SkyDrawable and and then adds an
 * instance to the SkyCanvas.
 */
public class SkyDemo3 {
    public static void main(String[] args) {
	SkyPane view = new SkyPane();
	Vector3 v = new Vector3(Direction.fromDegrees(30,40));
	view.add(new MySkyCircle(v, Math.toRadians(10)));
	view.setPreferredSize(new Dimension(700,700));
	SimpleSkyFrame frame = new SimpleSkyFrame(view);
	frame.pack();
	frame.setVisible(true);
    }
}

class MySkyCircle implements SkyDrawable {
    private Vector3 _center;
    private double _radius;     // Radians
    
    public MySkyCircle(Vector3 center, double radius) {
	_center = center;
	_radius = radius;
    }

    public void draw(SkyCanvas canvas) {
	canvas.setColor(Color.PINK);
	canvas.drawSmallCircle(_center, _radius);
    }
}
