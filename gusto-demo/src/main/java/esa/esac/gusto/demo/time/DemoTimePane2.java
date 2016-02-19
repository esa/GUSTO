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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.gui.time.AxisPainter;
import esa.esac.gusto.gui.time.BasicTimeProjection;
import esa.esac.gusto.gui.time.BoundedTimeModel;
import esa.esac.gusto.gui.time.CalendarAxisPainter;
import esa.esac.gusto.gui.time.ThumbPainter;
import esa.esac.gusto.gui.time.TimeAxisMouseListener;
import esa.esac.gusto.gui.time.TimeProjection;
import esa.esac.gusto.gui.time.TimeView;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

/**
 * Simple example of a time pane which combines a time axis with a schedule view.
 * 
 * @author  Jon Brumfitt
 */
public class DemoTimePane2 extends JPanel implements TimeView {
    private static final long serialVersionUID = 1L;
    private static final int INSET = 25;    // Horizontal inset of timeline
    private static final SimpleTimeFormat UTC = new SimpleTimeFormat(TimeScale.UTC);
    
    private TimeProjection _projection;
    private List<TimeInterval> _items;
    private AxisPainter _axisPainter;
    private BoundedTimeModel _model;
    private ThumbPainter _thumbPainter;

    /**
     * Create a new time pane.
     */
    public DemoTimePane2(BoundedTimeModel model) {
	_model = model;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	setPreferredSize(new Dimension(200, 100));
	setBackground(Color.DARK_GRAY);
	createData();

	TimeInterval initialRange = model.getRange();
	_projection = new BasicTimeProjection(this, initialRange, INSET);
	_axisPainter = new CalendarAxisPainter(_projection);
	_thumbPainter = new ThumbPainter(_projection, model, this);

	TimeAxisMouseListener adapter = new TimeAxisMouseListener(_model, this);
	adapter.setYRange(0,50);
	addMouseListener(adapter);
	addMouseMotionListener(adapter);
	addMouseWheelListener(adapter);
    }
    
    /**
     * Return the TimeProjection.
     */
    public TimeProjection getProjection() {
	return _projection;
    }

    /**
     * For the purposes of this demo, create the data in the view without a separate model.
     */
    private void createData() {
	TaiTime t1 = UTC.parse("2012-01-01T00:00:00Z");
	TaiTime t2 = UTC.parse("2012-02-01T00:00:00Z");
	TaiTime t3 = UTC.parse("2012-03-01T00:00:00Z");
	TaiTime t4 = UTC.parse("2012-04-01T00:00:00Z");
	_items = new ArrayList<TimeInterval>();
	_items.add(new TimeInterval(t1, t2));
	_items.add(new TimeInterval(t3, t4));
    }
    
    /**
     * Draw the schedule.
     */
    public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D)g;
	super.paintComponent(g);
	if(isOpaque()) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	}
        drawSchedule(g2, 50);
        g.setColor(Color.WHITE);
	_axisPainter.draw(g);
	_thumbPainter.draw(g);
    }
    
    private void drawSchedule(Graphics2D g2, int y) {
	g2.setColor(Color.GRAY);
	float xs = _projection.timeToScreen(_model.getRange().start());
	float xe = _projection.timeToScreen(_model.getRange().finish());
	g2.fill(new Rectangle2D.Float(xs, y, xe - xs, 28));
	g2.setColor(Color.YELLOW);
	for(TimeInterval item : _items) {
	    float x1 = _projection.timeToScreen(item.start());
	    float x2 = _projection.timeToScreen(item.finish());
	    g2.fill(new Rectangle2D.Float(x1, y + 5, x2 - x1, 18));
	}
    }

    /**
     * Repaint the component when notified of changes.
     */
    public void stateChanged(ChangeEvent ev) {
	repaint();
    }
}
