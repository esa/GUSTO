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

import esa.esac.gusto.math.Vector3;

import java.awt.event.MouseEvent;

/**
 * Interface for a handler for special mouse events.<p>
 * 
 * The SkyMouseListener class provides a default implementation of standard mouse
 * interaction with a SkyPane. Application-specific actions can be implemented by
 * extending SkyMouseListener. Alternatively, this interface can be used to define
 * a handler for these actions, making use of composition instead of inheritance.
 * 
 * @author  Jon Brumfitt
 */
public interface SkyMouseHandler {
    
    /**
     * Handle a mouse clicked event.
     * 
     * @param ev The MouseEvent
     * @param vector The cursor location as a sky vector (or null)
     */
    public void clicked(MouseEvent ev, Vector3 vector);
    
    /**
     * Handle a menu pop-up event.
     * 
     * @param ev The MouseEvent
     * @param vector The cursor location as a sky vector (or null)
     */
    public void showPopupMenu(MouseEvent ev, Vector3 vector);
}
