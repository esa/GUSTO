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
package esa.esac.gusto.demo.time;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.gui.time.BoundedTimeModel;
import esa.esac.gusto.gui.time.TimeAxisPane;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

/**
 * Demonstration of a time slider.
 * 
 * @author Jon Brumfitt
 */
public class TimeGuiDemo0 extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final SimpleTimeFormat UTC = new SimpleTimeFormat(TimeScale.UTC);
    
    private BoundedTimeModel _timeModel;
    private TimeAxisPane _timePane;

    /**
     * Create the application GUI.
     */
    public TimeGuiDemo0() {
	setPreferredSize(new Dimension(700,70));
	TaiTime tStart = UTC.parse("2010-01-01T00:00:00Z");
	TaiTime tStop  = UTC.parse("2015-01-01T00:00:00Z");

	_timeModel = new BoundedTimeModel(new TimeInterval(tStart, tStop));
	_timePane = new TimeAxisPane(_timeModel);
	add(_timePane, BorderLayout.NORTH);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	JFrame frame = new TimeGuiDemo0();
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);
    }
}




