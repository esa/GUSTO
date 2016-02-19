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

import java.awt.Dimension;

import esa.esac.gusto.gui.sky.SimpleSkyFrame;
import esa.esac.gusto.gui.sky.SkyPane;

/**
 * Example of very simple application that draws an empty SkyPane.
 */
public class SkyDemo0 {
    public static void main(String[] args) {
	SkyPane view = new SkyPane();
	view.setPreferredSize(new Dimension(700,700));
	SimpleSkyFrame frame = new SimpleSkyFrame(view);
	frame.pack();
	frame.setVisible(true);
    }
}
