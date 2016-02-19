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
import java.awt.Dimension;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import esa.esac.gusto.gui.sky.AdjustableSkyPane;
import esa.esac.gusto.gui.sky.SkyCircle;
import esa.esac.gusto.gui.sky.SkyComposite;
import esa.esac.gusto.gui.sky.SkyMenuFactory;
import esa.esac.gusto.gui.sky.SkyMouseListener;
import esa.esac.gusto.gui.sky.SkyPane;
import esa.esac.gusto.math.Vector3;

/**
 * Example showing a set of SkyDrawables being displayed by two independent SkyViews.<p>
 * 
 * There are no concurrency problems because there is only one GUI event-dispatching thread.
 * 
 * @author  Jon Brumfitt
 */
public class SkyDemo8 extends JPanel {
    private static final long serialVersionUID = 1L;
    private SkyPane _skyPane1;
    private SkyPane _skyPane2;
    private JMenuBar _menuBar;
    private Random _rand = new Random();
    
    public SkyDemo8() {
	setLayout(new BorderLayout());
	
	final SkyComposite drawable = new SkyComposite();

	_skyPane1 = new SkyPane();
	_skyPane1.setPreferredSize(new Dimension(700,700));
	_skyPane1.addMouseAdapter(new SkyMouseListener(_skyPane1));
	_skyPane1.add(drawable);
	
	_skyPane2 = new SkyPane();
	_skyPane2.setPreferredSize(new Dimension(700,700));
	_skyPane2.addMouseAdapter(new SkyMouseListener(_skyPane2));
	_skyPane2.add(drawable);
	
	Box box = Box.createHorizontalBox();
	box.add(new AdjustableSkyPane(_skyPane1));
	box.add(Box.createHorizontalStrut(20));
	box.add(new AdjustableSkyPane(_skyPane2));
	add(box);
	
	_menuBar = new JMenuBar();
	JMenuItem item;
	JMenu fileMenu = new JMenu("File");
	fileMenu.add(item = new JMenuItem("Add item"));
	item.addActionListener(event -> {
	    double x = (2 * _rand.nextDouble()) - 1;
	    double y = (2 * _rand.nextDouble()) - 1;
	    double z = (2 * _rand.nextDouble()) - 1;
	    double r = Math.toRadians(_rand.nextDouble() * 10);
	    drawable.add(new SkyCircle(new Vector3(x,y,z), r));
	    _skyPane1.repaint();
	    _skyPane2.repaint();
	});
	_menuBar.add(fileMenu);
	
	SkyMenuFactory factory1 = new SkyMenuFactory(_skyPane1);
	JMenu viewMenu1 = factory1.createViewMenu();
	_menuBar.add(viewMenu1);
	
	SkyMenuFactory factory2 = new SkyMenuFactory(_skyPane2);
	JMenu viewMenu2 = factory2.createViewMenu();
	_menuBar.add(viewMenu2);
    }
    
    private static void createAndShowGUI() {
	JFrame frame = new JFrame("Demo Application");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	SkyDemo8 pane = new SkyDemo8();
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

