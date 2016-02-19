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
package esa.esac.gusto.demo.combined.schedule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import esa.esac.gusto.constraint.TimeConstraint;
import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.gui.time.BoundedTimeModel;
import esa.esac.gusto.gui.time.TimeProjection;
import esa.esac.gusto.time.TaiTime;

/**
 * A pane that displays a Schedule as a time-line.
 *
 * @author  Jon Brumfitt
 */
class SchedulePane extends JPanel implements ChangeListener {
    private static final long serialVersionUID = 1L;
    private Schedule _schedule;
    private TimeProjection _projection;

    /**
     * Create a new TimePane.
     */
    public SchedulePane(BoundedTimeModel model, TimeProjection projection, Schedule schedule) {
	_projection = projection;
	_schedule = schedule;
	MouseZoomListener adapter = new MouseZoomListener();
	adapter.setYRange(0,Integer.MAX_VALUE);
	addMouseListener(adapter);
	addMouseMotionListener(adapter);
	
	setPreferredSize(new Dimension(200, 40));
	setBackground(Color.DARK_GRAY);
	setOpaque(true);
    }
    
    /**
     * Redraw the bars showing scheduling constraints.
     */
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	if(isOpaque()) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	}
	int y = 0;
	drawConstraint(g, _schedule.planningPeriod(), Color.GRAY, y, 28);
	drawObservations(g, y + 5);
    }

    private void drawObservations(Graphics g, int y) {
	Graphics2D g2 = (Graphics2D)g;
	// FIXME: Observation being dragged (or selected) should be drawn on top
	g2.setColor(Color.YELLOW);
	for(ScheduledItem item : _schedule.getObservations()) {
	    Schedulable s = item.getItem();
	    TaiTime start = item.getStartTime();
	    TaiTime stop = start.addSeconds(s.getDuration());
	    float x1 = _projection.timeToScreen(start);
	    float x2 = _projection.timeToScreen(stop);
	    g2.fill(new Rectangle2D.Float(x1, y, x2 - x1, 18));
	}
    }
    
    /**
     * Draw a constraint as a coloured bar.
     */
    private int drawConstraint(Graphics g, TimeConstraint constraint, Color color, int pos, int height) {
	Graphics2D g2 = (Graphics2D)g;
	g2.setColor(color);
	for(TimeInterval i : constraint) {
	    float start = _projection.timeToScreen(i.start());
	    float dur = (float)(i.duration() / _projection.getScale());
	    g2.fill(new Rectangle2D.Float(start, pos, dur, height));
	}
	return pos + height;
    }

    /**
     * Draw an interval as a coloured bar.
     */
    private int drawConstraint(Graphics g, TimeInterval interval, Color color, int pos, int height) {
	TimeConstraint constraint = new TimeConstraint(interval);
	return drawConstraint(g, constraint, color, pos, height);
    }

    class MouseZoomListener extends MouseAdapter {
	private ScheduledItem _dragging;
	private TaiTime _tStart;
	private TaiTime _tDown;
	private int _yMin = Integer.MIN_VALUE;
	private int _yMax = Integer.MAX_VALUE;

	public void setYRange(int min, int max) {
	    _yMin = min;
	    _yMax = max;
	    _dragging = null;
	}
	
	/**
	 * Handle mousePressed event.
	 */
	public void mousePressed(MouseEvent ev) {
	    if(ev.getY() < _yMin || ev.getY() > _yMax) return;
	    int xDown = ev.getX();
	    _tDown = _projection.screenToTime(xDown);
	    List<ScheduledItem> obs = _schedule.getObservations();
	    for(int i=0; i<obs.size(); i++) {
		ScheduledItem item = obs.get(i);
		Schedulable s = item.getItem();
		TaiTime start = item.getStartTime();
		TaiTime stop = start.addSeconds(s.getDuration());
		if(new TimeInterval(start, stop).contains(_tDown)) {
		    _dragging = item;
		    _tStart = start;
		    break;
		}
	    }
	}

	/**
	 * Handle mouseReleased event.
	 */
	public void mouseReleased(MouseEvent ev) {
//	    if(ev.getY() < _yMin || ev.getY() > _yMax) return;
	    _dragging = null;
	}

	/**
	 * Handle mouse dragged event.
	 */
	public void mouseDragged(MouseEvent ev) {
//	    if(ev.getY() < _yMin || ev.getY() > _yMax) return;
	    int x = ev.getX();
	    if(_dragging != null) {
		TaiTime time = _projection.screenToTime(x);
		time = time.addMicroseconds(_tStart.subtract(_tDown));
		_schedule.moveObservation(_dragging, time);
		repaint();
	    }
	}
    }
    
    public void stateChanged(ChangeEvent ev) {
	repaint();
    }
}
