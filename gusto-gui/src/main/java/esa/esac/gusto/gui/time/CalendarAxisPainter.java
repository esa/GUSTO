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

package esa.esac.gusto.gui.time;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.gui.time.CalendarTimeAxis.Tick;
import esa.esac.gusto.time.TaiTime;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 * Painter for a calendar axis with tick marks.
 * 
 * @author  Jon Brumfitt
 */
public class CalendarAxisPainter implements AxisPainter {
    private static final int DEFAULT_MAJOR_TICK_SIZE = 8;
    private static final int DEFAULT_MINOR_TICK_SIZE = 3;
    private static final Font FONT = new Font("Lucida Grande", Font.PLAIN, 11);

    private TimeProjection _projection;
    private CalendarTimeAxis _axis;
    private int _yAxis = 25;     // Vertical position of axis line
    private int _majorTickSize = DEFAULT_MAJOR_TICK_SIZE;
    private int _minorTickSize = DEFAULT_MINOR_TICK_SIZE;
    
    /**
     * Create a new CalendarAxisPainter.
     */
    public CalendarAxisPainter(TimeProjection projection) {
	_projection = projection;
	_axis = new CalendarTimeAxis(_projection.getRange());
    }
    
    /**
     * Set the size of the tick marks.<p>
     * 
     * Negative values can be used to draw the tick marks upwards instead of downwards.
     * 
     * @param major Size of major tick mark
     * @param minor Size of minor tick mark
     */
    public void setTickSizes(int major, int minor) {
	_majorTickSize = major;
	_minorTickSize = minor;
    }
    
    /**
     * Set the vertical position of the axis line.
     */
    public void setVerticalPosition(int yAxis) {
	_yAxis = yAxis;
    }
    
    /**
     * Set the projection.
     */
    public void setProjection(TimeProjection projection) {
	_projection = projection;
	_axis = new CalendarTimeAxis(_projection.getRange());
    }
    
    /**
     * Paint the axis.
     * 
     * @param g Graphics context
     */
    public void draw(Graphics g) {
	Graphics2D g2 = (Graphics2D)g;
	g.setFont(FONT);

	TimeInterval range = _projection.getRange();
	_axis.setTimeRange(range);
	for(Tick tick : _axis) {
	    TaiTime t = tick.getTime();
	    if(!t.before(range.start())) { // It would be better not to generate these events
		float x = _projection.timeToScreen(t);
		float size = tick.isMajor() ? _majorTickSize : _minorTickSize;
		g2.draw(new Line2D.Float(x, _yAxis, x, _yAxis - size));

		String label = tick.getLabel();
		float dx = label.length() * 4;  // Estimate half label width 
		g2.drawString(label, x - dx, _yAxis + 15);
	    }
	}

	g2.draw(new Line2D.Float(_projection.timeToScreen(range.start()), _yAxis,
		                 _projection.timeToScreen(range.finish()), _yAxis));
    }
}

