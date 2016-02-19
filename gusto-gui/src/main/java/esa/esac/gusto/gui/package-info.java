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

/**
Java components for building interactive sky views.

<h2 style="color: rgb(51, 51, 255);">Overview of Package</h2>

<h3 style="color: rgb(51, 51, 255);">Main Classes</h3>

<p>The following are some of the major classes:</p>

<b>SkyPane</b>
<ul>
  <li>base-class for building complex sky views (a <tt>JPanel</tt>)</li>
  <li>sub-class it to draw content and handle custom mouse events</li>
  <li>scrollable and zoomable, including zoom selected area</li>
  <li>pluggable projections</li>
  <li>displays grid lines</li>
</ul>

<b>SkyViewAdjuster</b>
<ul>
  <li>panel with buttons for zooming, scrolling, centering, etc</li>
  <li>displays cursor coordinates as RA and DEC</li>
</ul>

<b>Projection</b>
<ul>
  <li>MollweideProjection, OrthographicProjection, GnomonicProjection</li>
<li>forward projection for display</li>
<li>inverse projection for mouse handling</li>
</ul>

<b>SimpleSkyFrame</b>
<ul>
  <li>JFrame for easily creating GUI windows</li>
  <li>supports printing</li>
</ul>

<h3 style="color: rgb(51, 51, 255);">Usage</h3>

<p>The <tt>SkyPane</tt> class is an abstract base class for displaying a projected
view of the sky. Applications should implement a sub-class which overrides the
<tt>drawContents</tt> method to draw the application-specific content.</p>

<p>The view is specified by setting a viewport and scale. These are normally provided
by using <tt>SkyPane</tt> in conjuction with a <tt>SkyViewAdjuster</tt>. The latter
is a panel that provides buttons for zooming, rtoating and scrolling the view. It
also provides a readout ofthe current cursor position as right ascension and declination.
For convenience, the class <tt>AdjustableSkyPane</tt> provides a single panel
that contains a <tt>SkyPane</tt> connected to a <tt>SkyViewAdjuster</tt>.</p>

The projection may be set using the <tt>setProjection</tt> method and an instance
of a Projection that implements the <tt>Projection</tt> interface. Three standard
projections are provided: <tt>OrthographicProjection</tt>, <tt>GnomonicProjection</tt>
and <tt>MollweideProjection</tt>. The user may also define custom projections.</p>

<p><tt>SkyPane</tt> provides a number of useful methods for drawing great circles,
small circles, eigenaxis paths, text labels etc. There are also methods for drawing
short straight-line segments between vectors, so that other more complex paths
may be approximated.</p>

<p>The <tt>SkyPane</tt> class implements the <tt>ChangeListener</tt> interface,
so that it can be used as the view component with a model-view-controller pattern.
It also implements handling of mouse events, with support for zooming by rubber-band
selection of an area.

<h3 style="color: rgb(51, 51, 255);">SimpleSkyPane</h3>

<p>The class <tt>SimpleSkyFrame</tt> provides an <tt>AdjustableSkyPane</tt>
in a <tt>JFrame</tt> window, so that a sky viewer can be created in a few lines
of code. The window supports printing and provides a menu-bar with a list of
projections. It is simply necessary to implemnet <tt>drawContents</tt> to draw
the data, as in the following example:</p>

<pre>
// Extend SkyPane to draw your data
public class DemoView extends SkyPane {
    private List<Vector3> _vectors;
    
    public DemoView(List<Vector3> d) {
        super();
        _vectors = d;
        setPreferredSize(new Dimension(700, 700));
        MouseAdapter mouse = createZoomMouseListener();
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    protected void drawContents(Graphics g) {
        g.setColor(Color.red);
        Vector3 d = _vectors.get(0);
        moveTo(d);
        for(Vector3 di : _vectors) {
            drawLineTo(g, di);
        }
    }
}
</pre>
*/

package esa.esac.gusto.gui;


