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

/**
 * An entity that can be drawn on a SkyCanvas.<p>
 * 
 * A SkyDrawable can be added to a SkyCanvas. This is an alternative to extending
 * the SkyPane to implement drawing. It has the advantage that it uses composition
 * rather than inheritance. All drawing may be implemented in a single SkyDrawable
 * or multiple SkyDrawables may be used to draw different aspects of the view
 * (e.g. observations, constraints, etc).<p>
 * 
 * For example:
 * <pre>
 * class MyDrawable implements SkyDrawable {
 *     public void draw(SkyCanvas canvas) {
 *         canvas.setColor(Color.RED);
 *         canvas.drawGreatCircle(new Vector3(1,0,0));
 *     }
 * }
 * SkyPane pane = new SkyPane();
 * pane.add(new MyDrawable());
 * </pre>
 *
 * @author  Jon Brumfitt
 */
public interface SkyDrawable {
    
    /**
     * Draw the item on the canvas.
     * 
     * @param canvas The drawing canvas
     */
    public void draw(SkyCanvas canvas);
    
    /**
     * Set the SkyPane associated with this SkyDrawable.
     * 
     * @param pane The SkyPane
     */
    public default void setPane(SkyPane pane) {
    }
}
