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

import java.awt.BorderLayout;
import java.awt.Dimension;

import esa.esac.gusto.demo.combined.common.HerschelConstraints;
import esa.esac.gusto.ephem.Ephemerides;
import esa.esac.gusto.gui.sky.SimpleSkyFrame;
import esa.esac.gusto.gui.sky.SkyPane;
import esa.esac.gusto.gui.time.BoundedTimeModel;
import esa.esac.gusto.gui.time.TimeAxisPane;

/**
 * Visibility simulation for Herschel.
 * 
 * @author Jon Brumfitt
 */
public class VisibilityDemo extends SimpleSkyFrame {
    private static final long serialVersionUID = 1L;
    private static final String OEM_FILE = "H20090519_0001.LOE";
    private static final String DE405_FILE = "ascp2009-2013.405";
    private static final String GROUND_STATION = "NNO";
    private static final double MGA_CONSTRAINT = 15.0; // Degrees
    
    private TimeAxisPane _slider;
    private BoundedTimeModel _timeModel;
    private SkyPane _view;
    private HerschelConstraints _constraints;
    private MgaConstraint _mgaConstraint;

    /**
     * Create the application GUI.
     */
    public VisibilityDemo() {
	Ephemerides ephem = readEphemerides();
	
	_timeModel = new BoundedTimeModel(ephem.getTimeRange());
	_view = new SkyPane();
	_view.setPreferredSize(new Dimension(700,700));
	
	_constraints = new HerschelConstraints(ephem);
	_view.add(_constraints);
	setSkyPane(_view);
	
	_mgaConstraint = new MgaConstraint(ephem, MGA_CONSTRAINT, GROUND_STATION);
	_view.add(_mgaConstraint);
	
	_timeModel.addChangeListener(_mgaConstraint);
	_timeModel.addChangeListener(_constraints);
	_constraints.addChangeListener(_view);
//	_mgaConstraint.addChangeListener(_view);
	
	_slider = new TimeAxisPane(_timeModel);
	add(_slider, BorderLayout.NORTH);
    }
    
    /**
     * Read the ephemerides.
     */
    private Ephemerides readEphemerides() {
	String projectLoc = System.getProperty("project_loc");
	String oem = projectLoc + "/data/" + OEM_FILE;
	String de405 = projectLoc + "/data/" + DE405_FILE;
        return new Ephemerides(oem, de405);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	SimpleSkyFrame frame = new VisibilityDemo();
	frame.pack();
	frame.setVisible(true);
    }
}




