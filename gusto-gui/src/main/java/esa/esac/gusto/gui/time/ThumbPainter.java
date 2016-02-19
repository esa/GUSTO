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

import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Painter for a sliding thumb.
 * 
 * @author  Jon Brumfitt
 */
public class ThumbPainter implements AxisPainter {
    private static final int THUMB_MARGIN = 4;    // Margin beside thumb caption
    
    private SimpleTimeFormat _utc = new SimpleTimeFormat(TimeScale.UTC);
    private Image _thumbImage;
    private TimeProjection _projection;
    private BoundedTimeModel _model;
    private JComponent _component;
    private boolean _showHairLine;         // Show the hair-line cursor
    private boolean _showLabel = true;     // Show the cursor label
    private int _yThumb = 25;              // Vertical position of top of thumb
    private int _yLabel = 15;              // Vertical position of label
    
    /**
     * Create a new thumb painter.
     * 
     * @param projection The TimeProjection
     * @param model The BoundedTimeModel
     * @param component The component defining the width of the view
     */
    public ThumbPainter(TimeProjection projection, BoundedTimeModel model, JComponent component) {
	_projection = projection;
	_model = model;
	_component = component;
	_thumbImage = loadImage("arrow_up.png");
    }
    
    /**
     * Set the projection.
     */
    public void setProjection(TimeProjection projection) {
	_projection = projection;
    }
    
    /**
     * Enable display of hair-line cursor.
     */
    public void setShowHairLine(boolean enable) {
	_showHairLine = enable;
    }
    
    /**
     * Enable display of selected time as a cursor label.
     */
    public void setShowLabel(boolean enable) {
	_showLabel = enable;
    }
    
    /**
     * Set the number of decimal places of seconds shown in thumb label.
     * 
     * @param decimals Number of decimals places (0 to 6).
     */
    public void setThumbDecimals(int decimals) {
	if(decimals < 0 || decimals > 6) {
	    throw new IllegalArgumentException("Expected value inrange [0,6]");
	}
	_utc.setDecimals(decimals);
    }
    
    /**
     * Set the vertical position.
     * 
     * @param yThumb Vertical position of top of thumb
     * @param yLabel Vertical position of label
     */
    public void setPosition(int yThumb, int yLabel) {
	_yThumb = yThumb;
	_yLabel = yLabel;
    }
    
    /**
     * Draw the thumb.
     */
    public void draw(Graphics g) {
	TaiTime time = _model.getTime();
	int x = (int)_projection.timeToScreen(time);

	// Draw cursor hair
	if(_showHairLine) {
	    Graphics2D g2 = (Graphics2D)g;
	    g.setColor(Color.RED);
	    float xf = _projection.timeToScreen(time);
	    g2.draw(new Line2D.Float(xf, 0, xf, _component.getHeight()));
	}
	
	// Draw the thumb
	int w = _thumbImage.getWidth(null);
	g.drawImage(_thumbImage, x - w / 2, _yThumb, null);

	// Label the thumb with the time
	if(_showLabel) {
	    g.setColor(Color.WHITE);
	    String s = _utc.format(time);
	    int sWidth = g.getFontMetrics().stringWidth(s);
	    int xs = clipRange(x - sWidth/2, THUMB_MARGIN, _component.getWidth() - sWidth - THUMB_MARGIN);
	    g.drawString(s, xs , _yLabel);
	}
    }

    /**
     * Load the resources.
     */
    private Image loadImage(String name) {
	InputStream is = getClass().getResourceAsStream("/esa/esac/gusto/gui/time/" + name);
	try {
	    return ImageIO.read(is);
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
    }
    
    /**
     * Return a value clipped to the range <tt>min<=value<=max</tt>.
     */
    private int clipRange(int value, int min, int max) {
	if(value < min) {
	    value = min;
	} else if(value > max) {
	    value = max;
	}
	return value;
    }
}
