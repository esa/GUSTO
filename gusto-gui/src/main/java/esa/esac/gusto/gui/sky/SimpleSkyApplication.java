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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import esa.esac.gusto.util.PrintableComponent;

/**
 * A basic sky application.<p>
 * 
 * This class makes it easy to create user interfaces for simple applications,
 * by creating a SimpleSkyFrame with a custom subclass of SkyPane to draw the
 * required contents. The frame uses a BorderLayout, with the SkyPane as the
 * center component. Other panes can be added, such as a time pane is the North
 * component.
 *
 * @author  Jon Brumfitt
 */
public class SimpleSkyApplication extends JPanel {

    private static final long serialVersionUID = 1L;

    private SkyPane _view;
    protected JMenuBar _menuBar;
    private JMenu _viewMenu;
    private PrintRequestAttributeSet _printAttributes;
    private SkyMouseListener _mouseListener;

    /**
     * Create a new SimpleSkyFrame.
     */
    public SimpleSkyApplication() {
	setLayout(new BorderLayout());
	
	// Default SkyPane, may be replaced by setSkyPane().
	_view = new SkyPane();
	_view.setPreferredSize(new Dimension(700,700));
	
	add(new AdjustableSkyPane(_view), BorderLayout.CENTER);
	_mouseListener = new SkyMouseListener(_view);
	_view.addMouseAdapter(_mouseListener);

	createMenus();
	initPrinting();
    }
    
    /**
     * Set the SkyPane.
     */
    public void setSkyPane(SkyPane view) {
	_view = view;
	add(new AdjustableSkyPane(_view), BorderLayout.CENTER);
	view.addMouseAdapter(new SkyMouseListener(view));
	
	createMenus();
    }
    
    /**
     * Return the SkyPane.
     */
    public SkyPane getSkyPane() {
	return _view;
    }
    
    /**
     * Return the SkyMouseListener.
     */
    public SkyMouseListener getSkyMouseListener() {
	return _mouseListener;
    }
	
    /**
     * Create the default menus.
     */
    private void createMenus() {
	_menuBar = new JMenuBar();
	JMenuItem item;
	
	JMenu fileMenu = new JMenu("File");
        fileMenu.add(item = new JMenuItem("Print..."));
        item.addActionListener(event -> print());

	fileMenu.add(item = new JMenuItem("Quit"));
	item.addActionListener(event -> System.exit(0));
	_menuBar.add(fileMenu);

	SkyMenuFactory factory = new SkyMenuFactory(_view);
	_viewMenu = factory.createViewMenu();
	_menuBar.add(_viewMenu);
    }

    /**
     * Return the View menu.
     */
    public JMenu getViewMenu() {
	return _viewMenu;
    }
    
    
    //-------------------- Printing --------------------
    
    /**
     * Initialize printing.
     */
    private void initPrinting() {
	_printAttributes = new HashPrintRequestAttributeSet();
	_printAttributes.add(MediaSizeName.ISO_A4);
	_printAttributes.add(new JobName("Sky view", null));
	_printAttributes.add(JobSheets.NONE);
    }

    /**
     * Display the Print dialog and print the document.
     */
    private void print() {
	PrinterJob job = PrinterJob.getPrinterJob();
 	PrintableComponent component = new PrintableComponent(_view);
	component.setScalingMode(PrintableComponent.FIX_ASPECT);

	job.setPrintable(component);
        if (job.printDialog(_printAttributes)) {
            try {
                job.print(_printAttributes);
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //-------------------- Start application --------------------
    
    private static void createAndShowGUI() {
	JFrame frame = new JFrame("Basic Sky Application");
	SimpleSkyApplication pane = new SimpleSkyApplication();

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

