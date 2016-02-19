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

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.gui.time.BasicTimeProjection;
import esa.esac.gusto.gui.time.BoundedTimeModel;
import esa.esac.gusto.gui.time.TimeAxisPane;

/**
 * Simple example of a time pane which combines a time axis with a schedule view.
 * 
 * @author  Jon Brumfitt
 */
public class DemoTimePane extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int INSET = 25;    // Horizontal inset of timeline

    private DemoSchedulePane _schedulePane;

    public DemoTimePane(BoundedTimeModel model) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	TimeInterval initialRange = model.getRange();
	final BasicTimeProjection projection = new BasicTimeProjection(this, initialRange, INSET);
	
	TimeAxisPane tPane = new TimeAxisPane(model, projection);
	_schedulePane = new DemoSchedulePane(model, projection);
	projection.addChangeListener(_schedulePane);

	add(tPane);
	add(_schedulePane);
    }
}
