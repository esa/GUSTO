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

import esa.esac.gusto.gui.projection.SkyProjection;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector2;
import esa.esac.gusto.math.Vector3;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * A view of a SkyCanvas.<p>
 * 
 * A SkyView defines the way in which the sky is displayed, including the projection,
 * viewport, graticule, etc. The content is defined by a SkyCanvas.
 *
 * @author  Jon Brumfitt
 */
public interface SkyView {
    
    //------------------------------------------
    //  Graticule
    //------------------------------------------

    /**
     * Set the graticule color.
     */
    public void setGraticuleColor(Color color);
    
    /**
     * Return the graticule Color;
     */
    public Color getGraticuleColor();

    /**
     * Enable display of graticule.
     */
    public void setShowGraticule(boolean enable) ;

    /**
     * Check whether the graticule is displayed.
     */
    public boolean getShowGraticule();
    
    /**
     * Set the frame used for the graticule.
     */
    public void setGraticuleFrame(CoordinateFrame frame);
    
    /**
     * Return the graticule frame.
     */
    public CoordinateFrame getGraticuleFrame();
    
    
    //------------------------------------------
    //  Fundamental planes
    //------------------------------------------

    /**
     * Enable display of equatorial plane.
     */
    public void setShowEquatorialPlane(boolean enable);

    /**
     * Check whether equatorial plane is displayed.
     */
    public boolean getShowEquatorialPlane();

    /**
     * Enable display of ecliptic plane.
     */
    public void setShowEclipticPlane(boolean enable);

    /**
     * Check whether ecliptic plane is displayed.
     */
    public boolean getShowEclipticPlane();

    /**
     * Enable display of galactic plane.
     */
    public void setShowGalacticPlane(boolean enable);

    /**
     * Check whether galactic plane is displayed.
     */
    public boolean getShowGalacticPlane();

    
    //------------------------------------------
    //  Projection and view port
    //------------------------------------------

    /**
     * Zoom in the screen rectangle 'rect'.<p>
     *
     * The new view will only correspond approximately to the selected
     * area of sky, given the effects of the projection.
     */
    public void selectArea(Rectangle rect);
    
    /**
     * Set the projection.
     */
    public void setProjection(SkyProjection projection);

    /**
     * Return the projection.
     */
    public SkyProjection getProjection();

    /**
     * Set the position and orientation of the centre of the view.
     */
    public void setViewPort(Quaternion q);
    
    /**
     * Return the view port.
     */
    public Quaternion getViewPort();

    /**
     * Return the SkyViewAdjuster.
     */
    public SkyViewPortController getViewPortController();
    
    /**
     * Set the ViewPortController.
     */
    public void setViewPortController(SkyViewPortController adjuster);
    
    
    //------------------------------------------
    //  Mouse coordinates
    //------------------------------------------
    
    /**
     * Return mouse position relative to origin.
     */
    public Vector2 getMousePosition(MouseEvent ev);
    
    /**
     * Return mouse position converted into a sky vector.
     */
    public Vector3 getMouseVector(MouseEvent ev);
}
