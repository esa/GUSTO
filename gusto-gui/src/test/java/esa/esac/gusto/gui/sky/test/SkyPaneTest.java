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
	
package esa.esac.gusto.gui.sky.test;

import esa.esac.gusto.gui.projection.SkyProjections;
import esa.esac.gusto.gui.sky.AdjustableSkyPane;
import esa.esac.gusto.gui.sky.SkyMenuFactory;
import esa.esac.gusto.gui.sky.SkyMouseListener;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 * Manual test harness for exercising SkyPane with projections and graticule.
 *
 * @author  Jon Brumfitt
 */
public final class SkyPaneTest extends JPanel {
    private static final long serialVersionUID = 1L;

    private JMenuBar  _menuBar;
    private SkyTestPane _skyPane;

    /**
     * Create the main GUI window.
     */
    SkyPaneTest() {
	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	setPreferredSize(new Dimension(700,700));
	
	_skyPane = new SkyTestPane();
	_skyPane.addMouseAdapter(new SkyMouseListener(_skyPane));
	_skyPane.setProjection(new SkyProjections().get("Mollweide"));
	add(new AdjustableSkyPane(_skyPane));
	
    	SkyMenuFactory factory = new SkyMenuFactory(_skyPane);
    	JMenu viewMenu = factory.createViewMenu();
    	_menuBar = new JMenuBar();
	_menuBar.add(viewMenu);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
	SkyPaneTest test = new SkyPaneTest();
	JFrame frame = new JFrame("SkyPane test");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setJMenuBar(test._menuBar);
	frame.getContentPane().add("Center", test);
	frame.pack();
	frame.setVisible(true);
    }

    /**
     * Main program to start the application.
     */
    public static void main(String[] args) {
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		createAndShowGUI();
	    }
	});
    }
}






