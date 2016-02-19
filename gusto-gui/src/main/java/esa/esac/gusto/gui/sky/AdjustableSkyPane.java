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

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * A SkyPane with an associated ViewPortController and CursorPanel.
 */
public class AdjustableSkyPane extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private SkyPane _skyPane;
    private CursorPanel _cursorPanel;
    private ViewPortController _controller;
    private ViewPortAdjusterPane _buttonPanel;
    
    /**
     * Create a new AdjustableSkyPane.
     */
    public AdjustableSkyPane(SkyPane skyPane) {
	_skyPane = skyPane;
	setLayout(new BorderLayout());

	_cursorPanel = new CursorPanel();
	_controller = new ViewPortController();
	_skyPane.setViewPortController(_controller);
	_skyPane.setCursorView(_cursorPanel);
	_buttonPanel = new ViewPortAdjusterPane(_controller);
	
	Box box = Box.createHorizontalBox();
	box.add(_cursorPanel);
	box.add(Box.createHorizontalStrut(20));
	box.add(Box.createHorizontalGlue());
	box.add(_buttonPanel);

	add(_skyPane, "Center");
	add(box, "South");
    }
}
