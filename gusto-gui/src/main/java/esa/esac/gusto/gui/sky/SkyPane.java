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

import esa.esac.gusto.gui.projection.OrthographicProjection;
import esa.esac.gusto.gui.projection.SkyProjection;
import esa.esac.gusto.math.Attitude;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Quaternion;
import esa.esac.gusto.math.Vector2;
import esa.esac.gusto.math.Vector3;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A SkyPane displays a set of celestial sources as a sky projection.<p>
 * 
 * The class implements a SkyCanvas for drawing the sky and a SkyView for viewing the
 * canvas with various projections etc. The SkyView provides options for drawing a 
 * graticule, fundamental planes, etc. Application-specific content may be drawn by
 * either extending this class to implementing the drawContents() method or by
 * adding one or mode SkyDrawable objects. The latter approach has the advantage
 * that it uses composition rather than inheritance.<p>
 * 
 * The methods of the SkyCanvas interface may only be called by the drawContents
 * method of a sub-class, or by the draw() method of a SkyDrawable. This is because
 * the Java Graphics context is only properly defined in these cases.
 *
 * @author  Jon Brumfitt
 */
public class SkyPane extends JPanel implements SkyCanvas, SkyView, SkyComposable, ChangeListener {

    private static final long serialVersionUID = 1L;
    private static final CoordinateFrame DEFAULT_FRAME = CoordinateFrame.EQUATORIAL;

    // Default colors
    private Color _backgroundColor = Color.black;
    private Color _graticuleColor = new Color(140, 140, 0);
    
    private SkyProjection _projection;
    private SkyViewPortController _viewPortController;
    private MouseMotionListener _mouseMotionListener;
    private Vector3 _lastVector;
    private Vector2 _lastPoint;
    private Quaternion _lastQuaternion;
    private boolean _drawEquatorial;
    private boolean _drawEcliptic;
    private boolean _drawGalactic;
    private boolean _showGraticule;
    private CoordinateFrame _coordFrame = DEFAULT_FRAME;
    private CoordinateFrame _graticuleFrame = DEFAULT_FRAME;
    private List<SkyDrawable> _drawables;
    private Graphics _graphics;
    
    private SkyCursorView _skyCursor;

    //================================================================================
    // Construction
    //================================================================================
    
    /**
     * Create a SkyPane to display the specified model.
     */
    public SkyPane() {
	_mouseMotionListener = new MouseMovementListener();
	addMouseMotionListener(_mouseMotionListener);

	// Default projection
	_projection = new OrthographicProjection();
	_showGraticule = true;
	
	_drawables = new ArrayList<SkyDrawable>();

	setBackground(_backgroundColor);
	setOpaque(true);
	setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	
	_graphics = getGraphics(); // Updated by paint method
    }
    
    
    //================================================================================
    //  Listener management
    //================================================================================
    
    /**
     * Add a MouseAdapter.<p>
     * 
     * This is a convenience method that adds the adapter as a MouseListener,
     * MouseMotionListener and MouseWheelListener.
     */
    public void addMouseAdapter(MouseAdapter adapter) {
	addMouseListener(adapter);
	addMouseMotionListener(adapter);
	addMouseWheelListener(adapter);
    }
    
    /**
     * Remove a MouseAdapter.
     */
    public void removeMouseAdapter(MouseAdapter adapter) {
	removeMouseListener(adapter);
	removeMouseMotionListener(adapter);
	removeMouseWheelListener(adapter);
    }

    
    //================================================================================
    //  Painting component
    //================================================================================

    /**
     * Invoked when the state of the model changes, to update the view.
     */
    public void stateChanged(ChangeEvent e) {
	repaint();
    }

    /**
     * Paint the view.
     */
    public void paintComponent(Graphics g) {
	super.paintComponent(g);

	Insets insets = getInsets();
	int width  = getWidth() - insets.left - insets.right;
	int height = getHeight() - insets.top - insets.bottom;

	int x0 = width / 2;
	int y0 = height / 2;

	// Translate origin to centre of pane
	g.translate(x0 + insets.left, y0 + insets.top);
	_graphics = g;

	// Initialize projection
	if(_viewPortController != null) {
	    _projection.setViewPort(_viewPortController.getViewPort());
	    _projection.setScale(_viewPortController.getScale());
	}

	// Reset lastPoint in case projection (e.g. view-port) has changed
	if(_lastVector != null) {
	    _lastPoint = _projection.forward(_lastVector);
	}

	drawGraticule(10);
	drawPlanes();
	drawDrawables();
	drawContents(g);
    }

    /**
     * Override this method to draw the contents.
     */
    protected void drawContents(Graphics g) {
    }
    
    /**
     * Draw the SkyDrawable items.
     */
    private void drawDrawables() {
	for(SkyDrawable drawable : _drawables){
	    drawable.draw(this);
	}
    }
    
    /**
     * Draw the graticule.
     * *
     * @param step Step size in degrees
     */
    private void drawGraticule(int step) {
	if(_showGraticule) {
	    _graphics.setColor(_graticuleColor);

	    // Transform the graticule axes to the relevant frame
	    Quaternion qt = CoordinateFrame.transformation(_coordFrame, _graticuleFrame);
	    Vector3 z = qt.rotateVector(Vector3.unitZ());
	    Vector3 x = qt.rotateVector(Vector3.unitX());

	    // Draw Meridians
	    for(int i=0; i<180; i+=step) {
		Quaternion q = new Quaternion(z, Math.toRadians(i));
		Vector3 a = q.rotateVector(x);
		drawGreatCircle(a);
	    }

	    // Draw parallels
	    Vector3 zn = z.negate();
	    for(int j=step; j<90; j+=step) {
		double ang = Math.toRadians(j);
		drawSmallCircle(z, ang);
		drawSmallCircle(zn, ang);	    
	    }
	    drawGreatCircle(z);  // Equator
	}
    }
    
    /**
     * Draw the equatorial, ecliptic and galactic planes.
     */
    private void drawPlanes() {
	_graphics.setColor(Color.YELLOW);
	if(_drawEquatorial) {
	    Quaternion qt = CoordinateFrame.transformation(_coordFrame, CoordinateFrame.EQUATORIAL);
	    drawGreatCircle(qt.rotateVector(Vector3.unitZ()));
	}
	if(_drawEcliptic) {
	    Quaternion qt = CoordinateFrame.transformation(_coordFrame, CoordinateFrame.ECLIPTIC);
	    drawGreatCircle(qt.rotateVector(Vector3.unitZ()));
	}
	if(_drawGalactic) {
	    Quaternion qt = CoordinateFrame.transformation(_coordFrame, CoordinateFrame.GALACTIC);
	    drawGreatCircle(qt.rotateVector(Vector3.unitZ()));
	}
    }
    
    
    //================================================================================
    //  Protocol implementation: SkyView
    //================================================================================
    
    //---------------- Graticule ---------------
    
    /**
     * Set the graticule color.
     */
    public void setGraticuleColor(Color color) {
	_graticuleColor = color;
    }
    
    /**
     * Return the graticule Color;
     */
    public Color getGraticuleColor() {
	return _graticuleColor;
    }

    /**
     * Enable display of graticule.
     */
    public void setShowGraticule(boolean enable) {
	_showGraticule = enable;
	repaint();
    }
    
    /**
     * Check whether the graticule is displayed.
     */
    public boolean getShowGraticule() {
	return _showGraticule;
    }
    
    /**
     * Set the frame used for the graticule.
     */
    public void setGraticuleFrame(CoordinateFrame graticule) {
	_graticuleFrame = graticule;
	if(_viewPortController != null) {
	    _viewPortController.setFrames(_coordFrame, _graticuleFrame);
	}
	if(_skyCursor != null) {
	    _skyCursor.setCursorFrame(_graticuleFrame);
	}
	repaint();
    }
    
    /**
     * Return the graticule frame.
     */
    public CoordinateFrame getGraticuleFrame() {
	return _graticuleFrame;
    }
    
    
    //---------------- Fundamental planes ---------------
    
    /**
     * Enable display of equatorial plane.
     */
    public void setShowEquatorialPlane(boolean enable) {
	_drawEquatorial = enable;
	repaint();
    }

    /**
     * Check whether equatorial plane is displayed.
     */
    public boolean getShowEquatorialPlane() {
	return _drawEquatorial;
    }

    /**
     * Enable display of ecliptic plane.
     */
    public void setShowEclipticPlane(boolean enable) {
	_drawEcliptic = enable;
	repaint();
    }

    /**
     * Check whether ecliptic plane is displayed.
     */
    public boolean getShowEclipticPlane() {
	return _drawEcliptic;
    }

    /**
     * Enable display of galactic plane.
     */
    public void setShowGalacticPlane(boolean enable) {
	_drawGalactic = enable;
	repaint();
    }

    /**
     * Check whether galactic plane is displayed.
     */
    public boolean getShowGalacticPlane() {
	return _drawGalactic;
    }
    
    
    //---------------- Projection and view port ---------------
    
    /**
     * Zoom in the screen rectangle 'rect'.<p>
     *
     * The new view will only correspond approximately to the selected
     * area of sky, given the effects of the projection.
     */
    public void selectArea(Rectangle rect) {
	Insets insets = getInsets();
	int width  = getWidth() - insets.left - insets.right;
	int height = getHeight() - insets.top - insets.bottom;

	int x0 = width / 2 + insets.left;
	int y0 = height / 2 + insets.top;

	int xc = rect.x + rect.width/2;
	int yc = rect.y + rect.height/2;

	Vector3 vc = _projection.inverse(new Vector2(xc-x0, y0-yc));

	// Ignore if centre does not map onto a valid point on sky.
	if(vc == null) {
	    return;
	}

	double oldScale = getViewPortController().getScale();

	double scale1 = oldScale * getWidth() / rect.width;
	double scale2 = oldScale * getHeight() / rect.height;
	double newScale = Math.min(scale1, scale2);

	Quaternion old = getViewPortController().getViewPort();
	double phi = new Attitude(old).getPhi();

	Direction dc = new Direction(vc);
	Quaternion q = new Attitude(dc, phi).toQuaternion();

	getViewPortController().setViewPort(q, newScale);
    }
    
    /**
     * Set the projection.
     */
    public void setProjection(SkyProjection projection) {
	_projection = projection;
	repaint();
    }

    /**
     * Return the projection.
     */
    public SkyProjection getProjection() {
	return _projection;
    }

    /**
     * Set the position and orientation of the centre of the view.
     */
    public void setViewPort(Quaternion q) {
	if(_viewPortController != null) {
	    _viewPortController.setViewPort(q);
	}
    }

    /**
     * Return the view port.
     */
    public Quaternion getViewPort() {
	if(_viewPortController != null) {
	    return _viewPortController.getViewPort();
	} else {
	    return null;
	}
    }
    
    /**
     * Return the ViewPortController.
     */
    public SkyViewPortController getViewPortController() {
	return _viewPortController;
    }
    
    /**
     * Set the ViewPortController.
     */
    public void setViewPortController(SkyViewPortController adjuster) {
	// Remove any existing controller
	if(_viewPortController != null) {
	    _viewPortController.removeChangeListener(this);
	}
	_viewPortController = adjuster;
	// Set up the new controller
	if(_viewPortController != null) {
	    _viewPortController.setFrames(_coordFrame, _graticuleFrame);
	    _viewPortController.resetViewPort();
	    _viewPortController.addChangeListener(this);
	}
    }
    
    
    //================================================================================
    //  Protocol implementation: SkyCanvas
    //================================================================================
    
    /**
     * Set the coordinate frame of the data.<p>
     * 
     * The vectors and Quaternions to be plotted are with respect to this frame.
     */
    public void setCoordinateFrame(CoordinateFrame frame) {
	_coordFrame = frame;
    }
   
    /**
     * Get the coordinate frame of the data.<p>
     * 
     * The vectors and Quaternions to be plotted are with respect to this frame.
     */
    public CoordinateFrame getCoordinateFrame() {
	return _coordFrame;
    }
   
    /**
     * Set the foreground color.
     */
    public void setColor(Color color) {
	_graphics.setColor(color);
    }

    /**
     * Return the foreground Color.
     */
    public Color getColor() {
	return _graphics.getColor();
    }
    
    /**
     * Set the background color.
     */
    public void setBackgroundColor(Color color) {
	_backgroundColor = color;
	setBackground(_backgroundColor);
    }
    
    /**
     * Return the background Color.
     */
    public Color getBackgroundColor() {
	return _backgroundColor;
    }
    
    /**
     * Draw a text label.
     * 
     * @param v Position vector
     * @param text Text string
     */
    public void drawText(Vector3 v, String text) {
	Vector2 pt = _projection.forward(v);
	if(pt != null) {		    
	    int x = (int)pt.getX();
	    int y = (int)-pt.getY();
	    _graphics.drawString(text, x + 3, y);
	}
    }

    /**
     * Draw a filled circle.<p>
     * 
     * The center is projected but the circle is drawn in screen coordinates.
     * 
     * @param center Center of circle
     * @param radius Radius in pixels
     */
    public void drawFilledCircle(Vector3 center, int radius) {
	Vector2 pt = _projection.forward(center);

	if(pt != null) {
	    int xs = (int)pt.getX();
	    int ys = (int)-pt.getY();

	    // Draw initial attitude
	    int d = radius + radius + 1;
	    _graphics.fillOval(xs-radius, ys-radius, d, d);
	}
    }

    /**
     * Draw a circle.<p>
     * 
     * The center is projected but the circle is drawn in screen coordinates.
     * 
     * @param center Center of circle
     * @param radius Radius in pixels
     */
    public void drawCircle(Vector3 center, int radius) {
	Vector2 pt = _projection.forward(center);

	if(pt != null) {
	    int xs = (int)pt.getX();
	    int ys = (int)-pt.getY();

	    // Draw initial attitude
	    int d = radius + radius + 1;
	    _graphics.drawOval(xs-radius, ys-radius, d, d);
	}
    }

    /**
     * Draw a great circle.
     * 
     * @param axis Axis vector for center of circle
     */
    public void drawGreatCircle(Vector3 axis) {
	Vector3 a = axis.normalize();

	// Create a vector orthogonal to the axis
	Vector3 v = null;
	if(Math.abs(a.getX()) > 0.7071) {
	    v = a.cross(Vector3.unitY());
	} else {
	    v = a.cross(Vector3.unitX());
	}
	int step = 2;
	moveTo(v);
	for(int j=step; j<=360; j+=step) {
	    Quaternion q = new Quaternion(a, Math.toRadians(j));
	    Vector3 vec = q.rotateVector(v);
	    drawLineTo(vec);
	}
    }

    /**
     * Draw the arc of a great circle from current position to position 'v'.
     * 
     * @param v Position vector
     */
    public void drawGreatArcTo(Vector3 v) {
	Vector3 firstVector = _lastVector.copy();
	double angle = _lastVector.angle(v);
	double nsteps = Math.ceil(angle / Math.toRadians(1.0));
	double step = angle / nsteps;
	
	Vector3 axis = _lastVector.cross(v).normalize();
	for (double alpha = 0.0; alpha <=angle + 1E-10; alpha+=step) {
	    Quaternion rot = new Quaternion(axis, alpha);
	    drawLineTo(rot.rotateVector(firstVector));
	}
    }

    /**
     * Draw a small circle with a specified center and radius.
     *
     * @param axis Axis vector for center of circle
     * @param radius Radius of circle in radians
     */
    public void drawSmallCircle(Vector3 axis, double radius) {	
	Vector3 a = axis.normalize();

	// Create a vector orthogonal to the axis
	Vector3 v = null;
	if(Math.abs(a.getX()) > 0.7071) {
	    v = a.cross(Vector3.unitY());
	} else {
	    v = a.cross(Vector3.unitX());
	}

	// Create a vector at an angle 'radius' to the axis
	Quaternion q2 = new Quaternion(v, radius);
	Vector3 v3 = q2.rotateVector(a);

	int step = 2;
	moveTo(v3);
	for(int j=step; j<=360; j+=step) {
	    Quaternion q = new Quaternion(a, Math.toRadians(j));
	    Vector3 vec = q.rotateVector(v3);
	    drawLineTo(vec);
	}
    }

    /**
     * Draw an arc of a  small circle.
     *
     * @param v Initial vector
     * @param axis Rotation axis
     * @param angle Rotation angle in radians
     */
    public void drawSmallArc(Vector3 v, Vector3 axis, double angle) {
	Vector3 a = axis.normalize();
	double nsteps = Math.ceil(angle / Math.toRadians(1.0));
	double step = angle / nsteps;

	moveTo(v);
	for(double phi=step; phi<angle + 1E-10; phi+=step) {
	    Quaternion q = new Quaternion(a, phi);
	    Vector3 vec = q.rotateVector(v);
	    drawLineTo(vec);
	}
    }
    
    /**
     * Move to specified attitude without drawing.
     * 
     * @param q Quaternion
     */
    public void moveTo(Quaternion q) {
	_lastQuaternion = q;
    }
    
    /**
     * Move to specified position without drawing.
     * 
     * @param v Position vector
     */
    public void moveTo(Vector3 v) {
	_lastVector = v;
	_lastPoint = _projection.forward(v);
    }


    /**
     * Draw a straight line from current position to new position.<p>
     *
     * This should be used for drawing very short segments of a curve.
     *
     * @param v Position vector of end point 
     */
    public void drawLineTo(Vector3 v) {
	if(_lastVector != null) {
	    Vector2 startPt = _lastPoint;
	    Vector2 endPt = _projection.forward(v);
	    if((startPt != null) && (endPt != null)) {
		int x1 = (int)startPt.getX();
		int y1 = (int)-startPt.getY();
		int x2 = (int)endPt.getX();
		int y2 = (int)-endPt.getY();
		_graphics.drawLine(x1, y1, x2, y2);
	    }
	    _lastVector = v;
	    _lastPoint = endPt;

	} else {
	    moveTo(v);
	}
    }

    /**
     * Draw a straight line from position v1 to v2.<p>
     *
     * This should be used for drawing very short segments of a curve.
     *
     * @param v1 Position vector of start point 
     * @param v2 Position vector of end point 
     */
    public void drawLine(Vector3 v1, Vector3 v2) {
	if(v1 != _lastVector) {
	    _lastPoint = _projection.forward(v1);
	}
	drawLineTo(v2);
    }
    
    /**
     * Draw an eigenaxis slew path from current attitude to new attitude.
     * 
     * @param q2 Quaternion of end point
     */
    public void drawEigenaxisSlewTo(Quaternion q2) {
	if(_lastQuaternion != null) {

	    final int PIX = 4;  // Maximum number of pixels per step

	    double angle = eigenaxisAngle(_lastQuaternion, q2);
	    double scale = _projection.getScale();
	    double pix = angle * scale;  // Worst case number of pixels

	    int steps = (int)pix / PIX + 1;
	    int max_steps = getWidth() / PIX;
	    if(steps > max_steps) {
		steps = max_steps;
	    }

	    boolean visible = false;
	    int xs = 0;
	    int ys = 0;

	    Vector2 pt = _projection.forward(_lastQuaternion.rotateI());
	    if(pt != null) {
		xs = (int)pt.getX();
		ys = (int)-pt.getY();
		visible = true;
	    }

	    double step = 1.0/steps;

	    // Overshoot by a fraction of an arcsecond because final alpha
	    // may be slightly greater than 1.0 due to rounding errors.
	    for(double alpha=0; alpha <=1.0000001; alpha += step) {
		Quaternion q = _lastQuaternion.slerp(q2, alpha, true);
		pt = _projection.forward(q.rotateI());
		if(pt != null) {
		    int x = (int)pt.getX();
		    int y = (int)-pt.getY();

		    if(visible) {
			_graphics.drawLine(xs,ys,x,y);
		    }
		    xs = x;
		    ys = y;
		    visible = true;
		} else {
		    visible = false;
		}
	    }
	}
	_lastQuaternion = q2;
    }

    /**
     * Draw an eigenaxis slew path from attitude <tt>q1</tt> to <tt>q2</tt>.
     * 
     * @param q1 Start quaternion
     * @param q2 end quaternion
     */
    public void drawEigenaxisSlew(Quaternion q1, Quaternion q2) {
	_lastQuaternion = q1;
	drawEigenaxisSlewTo(q2);
    }

    /**
     * Return the eigenaxis slew angle (in radians) from <tt>q1</tt> to <tt>q2</tt>.
     * 
     * @param q1 Start quaternion
     * @param q2 end quaternion
     */
    private double eigenaxisAngle(Quaternion q1, Quaternion q2) {
	Quaternion q = q1.conjugate().mMultiply(q2);

	// Choose rotation direction such that angle <= PI
	if(q.getW() < 0) {
	    q.mMultiply(-1);
	}	
	return q.angle();
    }
    
    
    //================================================================================
    //  SkyComposable
    //================================================================================
    
    /**
     * Add a drawable item.
     */
    public void add(SkyDrawable drawable) {
	drawable.setPane(this);
	_drawables.add(drawable);
    }
    
    /**
     * Remove a drawable item.
     */
    public void remove(SkyDrawable drawable) {
	drawable.setPane(null);
	_drawables.remove(drawable);
    }
    
    /**
     * Remove all SkyDrawable item.
     */
    public void removelAllDrawables() {
	for(SkyDrawable drawable : _drawables) {
	    drawable.setPane(null);
	}
	_drawables.clear();
    }
    
    
    //================================================================================
    //  Mouse handling
    //================================================================================

    /**
     * Listener for mouse events in this pane.
     */
    class MouseMovementListener extends MouseMotionAdapter {

	/**
	 * Handle mouse moved event.
	 */
	public void mouseMoved(MouseEvent ev) { 
	    if(_viewPortController != null) {
		Vector3 v = _projection.inverse(getMousePosition(ev));
		if(_skyCursor != null) {
		    if(v != null) {
			Quaternion qt = CoordinateFrame.transformation(_coordFrame, _graticuleFrame);
			v = qt.rotateAxes(v);
		    }
		    _skyCursor.setCursorReadout(v);
		}
	    }
	}
    }
    
    /**
     * Return mouse position relative to origin.
     */
    public Vector2 getMousePosition(MouseEvent ev) {
	Insets insets = getInsets();
	int width  = getWidth() - insets.left - insets.right;
	int height = getHeight() - insets.top - insets.bottom;
	
	// Get mouse position relative to origin at centre of pane.
	int x = ev.getPoint().x- width/2 - insets.left;
	int y = ev.getPoint().y - height/2 - insets.top;
	
	return new Vector2(x, -y);
    }
    
    /**
     * Return mouse position converted into a sky vector.
     */
    public Vector3 getMouseVector(MouseEvent ev) {
	return _projection.inverse(getMousePosition(ev));
    }
    
    /**
     * Set a view to be notified of cursor movement.
     */
    public void setCursorView(SkyCursorView view) {
	_skyCursor = view;
	_skyCursor.setCursorFrame(_graticuleFrame);
    }
    
    /**
     * Return the SkyCursorView.
     */
    public SkyCursorView getCursorView() {
	return _skyCursor;
    }
}

