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
package esa.esac.gusto.demo.sky;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import esa.esac.gusto.gui.sky.CursorPanel;
import esa.esac.gusto.gui.sky.SkyCanvas;
import esa.esac.gusto.gui.sky.SkyDrawable;
import esa.esac.gusto.gui.sky.SkyMenuFactory;
import esa.esac.gusto.gui.sky.SkyMouseListener;
import esa.esac.gusto.gui.sky.SkyPane;
import esa.esac.gusto.gui.sky.ViewPortAdjusterPane;
import esa.esac.gusto.gui.sky.ViewPortController;
import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.Vector3;

/**
 * Example which serves as the starting point for a more complex application.<p>
 * 
 * The content can be drawn either by adding a SkyDrawable, as shown, or by
 * extending the SkyPane class.
 * 
 * @author  Jon Brumfitt
 */
public class SkyDemo7 extends JPanel {
    private static final long serialVersionUID = 1L;
    private SkyPane _skyPane;
    private JMenuBar _menuBar;
    
    public SkyDemo7() {
	setLayout(new BorderLayout());
	
        CursorPanel cursorPanel = new CursorPanel(); 	
    	ViewPortController controller = new ViewPortController();
	ViewPortAdjusterPane buttonPanel = new ViewPortAdjusterPane(controller);
	        
        _skyPane = new SkyPane();
	_skyPane.setPreferredSize(new Dimension(700,700));
	_skyPane.add(new DemoDrawable1());
        _skyPane.setViewPortController(controller);	
        _skyPane.setCursorView(cursorPanel);
        controller.addChangeListener(_skyPane);
        
        MouseAdapter adapter = new SkyMouseListener(_skyPane);
	_skyPane.addMouseAdapter(adapter);
        
	Box box = Box.createHorizontalBox();
	box.add(cursorPanel);
	box.add(Box.createHorizontalStrut(20));
	box.add(Box.createHorizontalGlue());
	box.add(buttonPanel);
	add(_skyPane, "Center");
	add(box, "South");
	
	_menuBar = new JMenuBar();
	SkyMenuFactory factory = new SkyMenuFactory(_skyPane);
	JMenu viewMenu = factory.createViewMenu();
	_menuBar.add(viewMenu);
    }
    
    private static void createAndShowGUI() {
	JFrame frame = new JFrame("Demo Application");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	SkyDemo7 pane = new SkyDemo7();
	frame.setJMenuBar(pane._menuBar);
	frame.getContentPane().add("Center", pane);
	frame.pack();
	frame.setVisible(true);
    }

    public static void main(String[] args) {
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		createAndShowGUI();
	    }
	});
    }
}

class DemoDrawable1 implements SkyDrawable {
    public void draw(SkyCanvas canvas) {
	canvas.setColor(Color.PINK);
	Vector3 v = new Vector3(Direction.fromDegrees(30,40));
	canvas.drawSmallCircle(v, Math.toRadians(10));
    }
}