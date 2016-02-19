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

import javax.swing.event.ChangeListener;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.time.TaiTime;

/**
 * * A TimeProjection defines the relation between times and axis positions.
 * 
 * @author  Jon Brumfitt
 */
public interface TimeProjection {
    
    /**
     * Transform time to plotting units.
     */
    public float timeToScreen(TaiTime time);
    
    /**
     * Returns the time corresponding to a certain screen position
     */
    public TaiTime screenToTime(float x);
    
    /**
     * Return the time range.
     */
    public TimeInterval getRange();
    
    /**
     * Set the time range.
     */
    public void setRange(TimeInterval range);
    
    /**
     * Returns the time scale in microseconds per pixel;
     */
    public double getScale();
    
    /**
     * Return the width including inset at each end.
     */
    public int getWidth();
    
    /**
     * Add a listener to receive notification when the model state changes.
     */
    public void addChangeListener(ChangeListener l);
    
    /**
     * Remove a listener so that it no longer receives notifications.
     */
    public void removeChangeListener(ChangeListener l);
}
