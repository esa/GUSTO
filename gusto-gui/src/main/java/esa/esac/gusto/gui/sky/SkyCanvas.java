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
import esa.esac.gusto.math.Vector3;

import java.awt.Color;

/**
 * A drawing canvas for the sky.<p>
 * 
 * This interface defines a set of drawing primitives for drawing the sky.
 * The view of this canvas (e.g. projection and view-port) are controlled
 * by a separate SkyView interface.
 * 
 * @author  Jon Brumfitt
 */
public interface SkyCanvas {
    
    /**
     * Set the coordinate frame of the data.<p>
     * 
     * The vectors and Quaternions to be plotted are with respect to this frame.
     */
    public void setCoordinateFrame(CoordinateFrame frame);
    
    /**
     * Get the coordinate frame of the data.<p>
     * 
     * The vectors and Quaternions to be plotted are with respect to this frame.
     */
    public CoordinateFrame getCoordinateFrame();
    
    /**
     * Set the foreground color.
     */
    public void setColor(Color color);
    
    /**
     * Return the foreground Color.
     */
    public Color getColor();
    
    /**
     * Set the background color.
     */
    public void setBackgroundColor(Color color);

    /**
     * Return the background Color.
     */
    public Color getBackgroundColor();
    
    /**
     * Draw a text label.
     * 
     * @param v Position vector
     * @param text Text string
     */
    public void drawText(Vector3 v, String text);

    /**
     * Draw a filled circle.<p>
     * 
     * The center is projected but the circle is drawn in screen coordinates.
     * 
     * @param center Center of circle
     * @param radius Radius in pixels
     */
    public void drawFilledCircle(Vector3 center, int radius);

    /**
     * Draw a circle.<p>
     * 
     * The center is projected but the cicle is drawn in screen coordinates.
     * 
     * @param center Center of circle
     * @param radius Radius in pixels
     */
    public void drawCircle(Vector3 center, int radius);

    /**
     * Draw a great circle.
     * 
     * @param axis Axis vector for center of circle
     */
    public void drawGreatCircle(Vector3 axis);
    
    /**
     * Draw the arc of a great circle from current position to position 'v'.
     * 
     * @param v Position vector
     */
    public void drawGreatArcTo(Vector3 v);

    /**
     * Draw a small circle with a specified center and radius.
     * 
     * @param axis Axis vector for center of circle
     * @param radius Radius of circle in radians
     */
    public void drawSmallCircle(Vector3 axis, double radius);

    /**
     * Draw an arc of a  small circle.
     *
     * @param v Initial vector
     * @param axis Rotation axis
     * @param angle Rotation angle in radians
     */
    public void drawSmallArc(Vector3 v, Vector3 axis, double angle);
    
    /**
     * Move to specified attitude without drawing.
     * 
     * @param q Quaternion
     */
    public void moveTo(Quaternion q);

    /**
     * Move to specified position without drawing.
     * 
     * @param v Position vector
     */
    public void moveTo(Vector3 v);

    /**
     * Draw a straight line from current position to new position.<p>
     *
     * This should be used for drawing very short segments of a curve.
     *
     * @param v Position vector of end point 
     */
    public void drawLineTo(Vector3 v);

    /**
     * Draw a straight line from position v1 to v2.<p>
     *
     * This should be used for drawing very short segments of a curve.
     *
     * @param v1 Position vector of start point 
     * @param v2 Position vector of end point 
     */
    public void drawLine(Vector3 v1, Vector3 v2);

    /**
     * Draw an eigenaxis slew path from current attitude to new attitude.
     * 
     * @param q2 Quaternion of end point
     */
    public void drawEigenaxisSlewTo(Quaternion q2);

    /**
     * Draw an eigenaxis slew path from attitude <tt>q1</tt> to <tt>q2</tt>.
     * 
     * @param q1 Start quaternion
     * @param q2 end quaternion
     */
    public void drawEigenaxisSlew(Quaternion q1, Quaternion q2);
}
