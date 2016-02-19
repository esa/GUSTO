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
package esa.esac.gusto.demo.combined.common;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import esa.esac.gusto.ephem.Bodies;
import esa.esac.gusto.ephem.Ephem;
import esa.esac.gusto.ephem.Ephemerides;
import esa.esac.gusto.gui.sky.SkyCanvas;
import esa.esac.gusto.gui.sky.SkyDrawable;
import esa.esac.gusto.gui.time.TimeModel;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.TaiTime;


/**
 * Constraints for Herschel spacecraft.
 * 
 * @author  Jon Brumfitt
 */
public class HerschelConstraints extends ChangeAdapter implements SkyDrawable, ChangeListener {

    private static final double SUN_MAX  = 119.4;      // DEGREES
    private static final double SUN_MIN   = 60.6;      // DEGREES
    private static final double EARTH_MIN = 23.0;      // DEGREES
    private static final double MOON_MIN  = 13.0;      // DEGREES
    private static final double STR_MOON_MIN  = 20.0;  // DEGREES
    private static final double PLANET_MIN  = 1.5;     // DEGREES

    private TaiTime _time;
    private Ephem _ephem;

    /**
     * Create a new instance of HerschelConstraints.
     */
    public HerschelConstraints(Ephemerides ephem) {
	_ephem = ephem;
    }

    /**
     * Set the time.
     */
    public void setTime(TaiTime time) {
	_time = time;
	fireChange();
    }

    /**
     * Update in response to changes from TimeModel.
     */
    public void stateChanged(ChangeEvent e) {
	TimeModel model = (TimeModel)e.getSource();
	TaiTime time = model.getTime();
	setTime(time);
    }

    /**
     * Draw the constraint.
     */
    public void draw(SkyCanvas canvas) {
	if(_time != null) {
	    Vector3 sun = _ephem.spacecraftTo(_time, Bodies.SUN).position();
	    Vector3 earth = _ephem.spacecraftTo(_time, Bodies.EARTH).position();
	    Vector3 moon = _ephem.spacecraftTo(_time, Bodies.MOON).position();
	    Vector3 mars = _ephem.spacecraftTo(_time, Bodies.MARS).position();
	    Vector3 jupiter = _ephem.spacecraftTo(_time, Bodies.JUPITER).position();
	    Vector3 saturn = _ephem.spacecraftTo(_time, Bodies.SATURN).position();
	    
	    canvas.setColor(Color.GREEN);
	    canvas.drawSmallCircle(sun, Math.toRadians(SUN_MIN));
	    canvas.drawText(sun, "Sun");
	    canvas.drawSmallCircle(sun.negate(), Math.toRadians(180 - SUN_MAX));
	    canvas.drawText(sun.negate(), "AntiSun");

	    canvas.setColor(Color.CYAN);
	    canvas.drawSmallCircle(earth, Math.toRadians(EARTH_MIN));
	    canvas.drawText(earth, "Earth");
	    canvas.drawSmallCircle(moon, Math.toRadians(MOON_MIN));
	    canvas.drawText(moon, "Moon");
	    
	    canvas.setColor(new Color(150,180,255));
	    canvas.drawSmallCircle(moon.negate(), Math.toRadians(STR_MOON_MIN));
	    canvas.drawText(moon.negate(), "STR-Moon");
	    
	    canvas.setColor(Color.RED);
	    canvas.drawSmallCircle(mars, Math.toRadians(PLANET_MIN));
	    canvas.drawText(mars, "Mars");
	    canvas.drawSmallCircle(jupiter, Math.toRadians(PLANET_MIN));
	    canvas.drawText(jupiter, "Jupiter");
	    canvas.drawSmallCircle(saturn, Math.toRadians(PLANET_MIN));
	    canvas.drawText(saturn, "Saturn");
	}
    }
}
