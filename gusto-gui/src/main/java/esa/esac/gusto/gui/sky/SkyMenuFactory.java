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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import esa.esac.gusto.gui.projection.SkyProjections;

/**
 * Factory for creating View menus.
 * 
 * @author  Jon Brumfitt
 */
public class SkyMenuFactory {
    private SkyPane _pane;
    
    /**
     * Create a new menu factory.
     * 
     * @param pane the SkyPane to be notified by the menu actions.
     */
    public SkyMenuFactory(SkyPane pane) {
	_pane = pane;
    }

    /**
     * Create a menu checkbox to hide/show the graticule.
     */
    public JCheckBoxMenuItem createGraticuleItem() {
	final JCheckBoxMenuItem graticuleItem = new JCheckBoxMenuItem("Hide graticule");
	graticuleItem.setSelected(!_pane.getShowGraticule());
	graticuleItem.addActionListener(event ->
	    _pane.setShowGraticule(!graticuleItem.isSelected()));
	return graticuleItem;
    }
	
    /**
     * Create a menu checkbox to choose format for displaying coordinates.
     */
    public JCheckBoxMenuItem createSexagesimalItem() {
	final JCheckBoxMenuItem sexagesimalItem = new JCheckBoxMenuItem("Sexagessimal");
	sexagesimalItem.addActionListener(event -> {
	    SkyCursorView cursor = _pane.getCursorView();
	    if(cursor != null) {
		cursor.setSexagesimalMode(sexagesimalItem.isSelected());
	    }
	});
	return sexagesimalItem;
    }
    
    /**
     * Create menu to select the coordinate system.
     */
    public JMenu createCoordinateMenu() {
	JMenu menu = new JMenu("Coordinates");
	JRadioButtonMenuItem equButton = new JRadioButtonMenuItem(EQU_NAME);
	JRadioButtonMenuItem eclButton = new JRadioButtonMenuItem(ECL_NAME);
	JRadioButtonMenuItem galButton = new JRadioButtonMenuItem(GAL_NAME);

	ButtonGroup group = new ButtonGroup();
	group.add(equButton);
	group.add(eclButton);
	group.add(galButton);
//	equButton.setSelected(true);
	
	menu.add(equButton);
	menu.add(eclButton);
	menu.add(galButton);
	
	ActionListener list = event -> {
	    String cmd = event.getActionCommand();
	    if(cmd.equals(EQU_NAME)) {
		_pane.setGraticuleFrame(CoordinateFrame.EQUATORIAL);
	    } else if(cmd.equals(ECL_NAME)) {
		_pane.setGraticuleFrame(CoordinateFrame.ECLIPTIC);
	    } else if(cmd.equals(GAL_NAME)) {
		_pane.setGraticuleFrame(CoordinateFrame.GALACTIC);
	    }
	};
	equButton.addActionListener(list);
	eclButton.addActionListener(list);
	galButton.addActionListener(list);
	
	return menu;
    }
    
    /**
     * Create a menu for displaying fundamental planes.
     */
    public JMenu createPlaneMenu() {
	JMenu menu = new JMenu("Show planes");
	
	final JCheckBoxMenuItem equatorialItem = new JCheckBoxMenuItem("Show equatorial plane");
	equatorialItem.setSelected(_pane.getShowEquatorialPlane());
	equatorialItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _pane.setShowEquatorialPlane(equatorialItem.isSelected());
            }
        });
	menu.add(equatorialItem);
	
	final JCheckBoxMenuItem eclipticItem = new JCheckBoxMenuItem("Show ecliptic plane");
	eclipticItem.setSelected(_pane.getShowEclipticPlane());
	eclipticItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _pane.setShowEclipticPlane(eclipticItem.isSelected());
            }
        });
	menu.add(eclipticItem);
	
	final JCheckBoxMenuItem galacticItem = new JCheckBoxMenuItem("Show galactic plane");
	galacticItem.setSelected(_pane.getShowGalacticPlane());
	galacticItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _pane.setShowGalacticPlane(galacticItem.isSelected());
            }
        });
	menu.add(galacticItem);
	
	return menu;
    }
    
    /**
     * Create a menu for selecting the projection.
     */
    public JMenu createProjectionMenu() {
	final SkyProjections proj = new SkyProjections();
	final ButtonGroup group = new ButtonGroup();
	JMenu menu = new JMenu("Projection");
	ActionListener listener = event -> {
	    String s = event.getActionCommand();
	    _pane.setProjection(proj.get(s));
	};

	for(String name : proj.getNames()) {
	    JMenuItem item = new JRadioButtonMenuItem(name);
	    item.addActionListener(listener);
	    group.add(item);
	    menu.add(item);
	}
	return menu;	
    }
    
    private static final String EQU_NAME = "Equatorial (J2000)";
    private static final String ECL_NAME = "Ecliptic";
    private static final String GAL_NAME = "Galactic";
    
    /**
     * Create a complete View menu with all the available sub-menus.
     */
    public JMenu createViewMenu() {
	JMenu viewMenu = new JMenu("View");
	viewMenu.add(createCoordinateMenu());
    	viewMenu.add(createPlaneMenu());
	viewMenu.add(createProjectionMenu());
	viewMenu.add(createGraticuleItem());
	viewMenu.add(createSexagesimalItem());
	return viewMenu;
    }
}
