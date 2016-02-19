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
package esa.esac.gusto.demo.combined.visibility;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import esa.esac.gusto.demo.combined.common.ChangeAdapter;
import esa.esac.gusto.ephem.Ephem;
import esa.esac.gusto.ephem.Ephemerides;
import esa.esac.gusto.ephem.sites.EarthSites;
import esa.esac.gusto.gui.sky.SkyCanvas;
import esa.esac.gusto.gui.sky.SkyDrawable;
import esa.esac.gusto.gui.time.TimeModel;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector3;
import esa.esac.gusto.time.TaiTime;

/**
 * Medium Gain Antenna spatial sky constraint.
 *
 * @author  Jon Brumfitt
 */
public class MgaConstraint extends ChangeAdapter implements SkyDrawable, ChangeListener {
    private static final double SQRT_2 = Math.sqrt(2);

    private double _radius;
    private Ephemerides _ephem;
    private String _stationName;
    private EarthSites _groundStations;
    private TaiTime _time;

    /**
     * Create a new MgaConstraint.
     */
    public MgaConstraint(Ephemerides ephem, double radius, String station) {
	_ephem = ephem;
	_radius = radius;
	_stationName = station;
	_groundStations = new EarthSites();
    }
    
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
	canvas.setColor(Color.PINK);
	
	double radius = Math.toRadians(_radius);
	Vector3 sun = _ephem.spacecraftTo(_time, Ephem.SUN).position();
	Vector3 earth = _ephem.spacecraftTo(_time, Ephem.EARTH).position();
	Vector3 offset = _groundStations.state(_stationName, _time)[0];
	Vector3 station = earth.add(offset).normalize();

	double step0 = Math.toRadians(1);  // Initial step size
	
	// Find a perpendicular to the Ground Station vector
	Vector3 vpe = null;
	if(station.getX() > SQRT_2) {
	    vpe = station.cross(Vector3.unitY());
	} else {
	    vpe = station.cross(Vector3.unitX());
	}
	
	// Create a vector at an angle 'radius' to the Ground Station vector
	Quaternion qa = new Quaternion(vpe, radius);
	Vector3 vz0 = qa.rotateVector(station);

	Vector3 x0 = vz0.triad(sun).getColumn(1);
	Vector3 previousX = x0.copy();
	
	double angle = 0;
	double alpha = step0 / 2;
	while(angle < 2 * Math.PI) {
	    // Step the Z axis around the Ground Station at the MGA constraint radius
	    Quaternion q = new Quaternion(station, angle);
	    
	    // Find X axis for zero alpha angle
	    Vector3 z = q.rotateVector(vz0);
	    Vector3 x = z.triad(sun).getColumn(1);
	    
	    // Iteratively adjust Z steps so that X steps are approximately constant size
	    double delta = x.angle(previousX);
	    if(delta > 0) {
		alpha *= step0 / delta;
	    }
	    angle += alpha;

	    canvas.drawLine(previousX, x);
	    canvas.drawLine(previousX.negate(), x.negate());
	    previousX = x;
	}
	// Draw the final segment to close the loop
	canvas.drawLine(previousX, x0);
	canvas.drawLine(previousX.negate(), x0.negate());

	// Label the constraint
	Vector3 xc = station.triad(sun).getColumn(1);
	canvas.drawText(xc, _stationName);
	canvas.drawText(xc.negate(), _stationName);
    }
}

