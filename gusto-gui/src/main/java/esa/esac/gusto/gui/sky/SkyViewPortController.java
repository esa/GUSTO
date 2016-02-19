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

package esa.esac.gusto.gui.sky;

import esa.esac.gusto.math.Quaternion;

import javax.swing.event.ChangeListener;

/**
 * Controls for adjusting the ViewPort of a SkyPane display.<p>
 * 
 * This is a facade for backward compatibility.
 *
 * @author  Jon Brumfitt
 */
public interface SkyViewPortController {

    /**
     * Set the scale (pixels per radian) and notify views.
     */
    public void setScale(double scale);
    
    /**
     * Set the view port.
     */ 
    public void setViewPort(Quaternion q);

    /**
     * Set viewport and scale and notify views.
     */
    public void setViewPort(Quaternion q, double scale);
    
    /**
     * Set the Frame for the cursor readout.
     */
    public void setFrames(CoordinateFrame coord, CoordinateFrame graticule);
    
    /**
     * Reset the ViewPort.<p>
     * 
     * Uses the selection, if set, otherwise resets to default alignment for the frame.
     */
    public void resetViewPort();
    
    /**
     * Set a ViewPort for the resetViewPort method.
     */
    public void setSelection(Quaternion q);
    

    //--------------------------
    //  Model properties
    //--------------------------

    /**
     * Return the current viewport.
     */
    public Quaternion getViewPort();

    /**
     * Return the current scale of the view;
     */
    public double getScale();
  

    //--------------------------
    //  MVC interaction
    //--------------------------

    /**
     * Add a listener to receive notification when the model state changes.
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Remove a listener so that it no longer receives notifications.
     */
    public void removeChangeListener(ChangeListener l);
}

