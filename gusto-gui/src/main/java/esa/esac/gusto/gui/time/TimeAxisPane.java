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

import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * A slider control for adjusting a time.<p>
 * 
 * The control responds to the following mouse events:<br>
 * 
 * <table border="1">
 * <tr><td>Click-and-drag scale</td><td>Shift time range</td></tr>
 * <tr><td>Click-and-drag thumb</td><td>Adjust time</td></tr>
 * <tr><td>Double-click</td><td>Jump to time</td></tr>
 * <tr><td>Mouse wheel</td><td>Zoom time range</td></tr>
 * <tr><td>Right double-click</td><td>Zoom out fully</td></tr>
 * </table><br>
 * 
 * By default, a UTC calendar scale is used for the axis. The <tt>setAxisPainter</tt>
 * method allows other axis types to be used, such as relative times or Julian dates.
 * 
 * @author Jon Brumfitt
 */
public class TimeAxisPane extends JPanel implements TimeView {
    private static final long serialVersionUID = 1L;
    private static final int INSET = 25;          // Horizontal inset of timeline
    
    private SimpleTimeFormat _utc = new SimpleTimeFormat(TimeScale.UTC);
    private BoundedTimeModel _model;    // The time model
    private int _thumbDecimals;         // Decimal places of seconds for thumb label
    private AxisPainter _axisPainter;
    private ThumbPainter _thumbPainter;
    private TimeProjection _projection;
    private TimeAxisMouseListener _mouseListener;
 
    /**
     * Create a new TimeAxisPane.
     */
    public TimeAxisPane(BoundedTimeModel model, TimeProjection projection) {
	_model = model;
	_projection = projection;
	
	setPreferredSize(new Dimension(200, 50));
	setOpaque(true);
	setBackground(Color.DARK_GRAY);
	
	_mouseListener = new TimeAxisMouseListener(_model, this);
	_mouseListener.setYRange(0,50);
	addMouseListener(_mouseListener);
	addMouseMotionListener(_mouseListener);
	addMouseWheelListener(_mouseListener);
	
	_model.setTime(_model.getTime());
	_axisPainter = new CalendarAxisPainter(projection);
	_thumbPainter = new ThumbPainter(_projection, model, this);
	
	_utc.setDecimals(_thumbDecimals);
    }
    
    /**
     * Create a TimeAxisPane with a default TimeProjection.
     */
    public TimeAxisPane(BoundedTimeModel model) {
	this(model, new BasicTimeProjection(null, model.getRange(), INSET));
	((BasicTimeProjection)_projection).setComponent(this); // ******************
    }
    
    /**
     * Return the TimeProjection.
     */
    public TimeProjection getProjection() {
	return _projection;
    }
    
    /**
     * Return the time model.
     */
    public BoundedTimeModel getModel() {
	return _model;
    }
    
    /**
     * Set the AxisPainter used to draw the axis.
     */
    public void setAxisPainter(AxisPainter painter) {
	_axisPainter = painter;
	_axisPainter.setProjection(_projection);
    }
    
    /**
     * Return the AxisPainter.
     */
    public AxisPainter getAxisPainter() {
	return _axisPainter;
    }

    /**
     * Limit the amount the view can be zoomed in, by setting a minimum time period.
     */
    public void setMinimumPeriod(int seconds) {
	int max = (int)(_model.getRange().duration() / 1000000L);
	if(seconds <= max) {
	    _mouseListener.setMinimumPeriod(seconds);
	} else {
	    _mouseListener.setMinimumPeriod(max);
	}
    }
    
    /**
     * Paint the component.
     */
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	if(isOpaque()) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	}
	g.setColor(Color.WHITE);
	_axisPainter.draw(g);
	_thumbPainter.draw(g);
    }
}
