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

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A fixed-size icon button.
 * 
 * @author  Jon Brumfitt
 */
public class FixedSizeIconButton extends JButton {
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 20;
    
    /**
     * Create a new FixedSizeIconButton.
     * 
     * @param icon The icon
     */
    public FixedSizeIconButton(Icon icon) {
	super(icon); 
    }
    
    /**
     * Create a new FixedSizeIconButton.
     * 
     * @param action The button action
     * @param icon The icon
     */
    public FixedSizeIconButton(Action action, Icon icon) {
	super(action);
	setText(null);
	setIcon(icon);
    }

    /**
     * Return the minimum size of the button.
     */
    public Dimension getMinimumSize() {
	return getPreferredSize(); 
    }

    /**
     * Return the maximum size of the button.
     */
    public Dimension getMaximumSize() { 
	return getPreferredSize();
    }

    /**
     * Return the preferred size of the button.
     */
    public Dimension getPreferredSize() {
	return new Dimension(SIZE, SIZE);
    }
}
