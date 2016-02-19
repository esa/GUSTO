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

import java.util.ArrayList;
import java.util.List;

/**
 * A SkyDrawable that may contain other SkyDrawables.<p>
 * 
 * This class uses the Composite design pattern to allow a SkyDrawable to contain
 * other SkyDrawables.
 * 
 * @author  Jon Brumfitt
 */
public class SkyComposite implements SkyDrawable, SkyComposable {
    private List<SkyDrawable> _drawables = new ArrayList<SkyDrawable>();
    private SkyPane _pane;
    
    /**
     * Set the SkyPane associated with this SkyDrawable.
     */
    public void setPane(SkyPane pane) {
	_pane = pane;
    }
    
    /**
     * Draw the item on the canvas.
     * 
     * @param canvas The drawing canvas
     */
    public void draw(SkyCanvas canvas) {
	for(SkyDrawable d : _drawables) {
	    d.draw(canvas);
	}
    }

    /**
     * Add a drawable item.
     */
    public void add(SkyDrawable drawable) {
	_drawables.add(drawable);
	drawable.setPane(_pane);
    }

    /**
     * Remove a drawable item.
     */
    public void remove(SkyDrawable drawable) {
	_drawables.remove(drawable);
	drawable.setPane(null);
    }

    /**
     * Remove all SkyDrawable item.
     */
    public void removelAllDrawables() {
	_drawables.clear();
    }
}
