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

import esa.esac.gusto.math.Direction;
import esa.esac.gusto.math.SexagesimalFormatter;
import esa.esac.gusto.math.Vector3;

import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Display for cursor position.
 * 
 * @author  Jon Brumfitt
 */
public class CursorPanel extends JPanel implements SkyCursorView {
    private static final long serialVersionUID = 1L;
    
    private static final DecimalFormat DEC_FMT = new DecimalFormat("##0.00000");
    private static final SexagesimalFormatter X_FMT = new SexagesimalFormatter(SexagesimalFormatter.Mode.RA_HMS_LOWER);
    private static final SexagesimalFormatter Y_FMT = new SexagesimalFormatter(SexagesimalFormatter.Mode.DEC_DMS_LOWER);

    private JLabel _label;
    private JTextField _xField;
    private JTextField _yField;
    private boolean _sexagesimalMode;

    /**
     * Create a new CursorPanel
     */
    public CursorPanel() {
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	_xField = new JTextField("", 10);
	_xField.setToolTipText("Cursor right-ascension (degrees)");
	_xField.setMinimumSize(_xField.getPreferredSize());
	_xField.setMaximumSize(_xField.getPreferredSize());
	_xField.setEditable(false);

	_yField = new JTextField("", 10);
	_yField.setToolTipText("Cursor declination (degrees)");
	_yField.setMinimumSize(_yField.getPreferredSize());
	_yField.setMaximumSize(_yField.getPreferredSize());
	_yField.setEditable(false);

	_label = new JLabel();

	add(_label);
	add(_xField);
	add(_yField);

	setCursorFrame(CoordinateFrame.EQUATORIAL); 
    }
    
    /**
     * Set the coordinate frame.
     */
    public void setCursorFrame(CoordinateFrame frame) {
	switch(frame) {
	case EQUATORIAL:
	    setFieldLabel("RA/DEC (J2000)");
	    break;
	case ECLIPTIC:
	    setFieldLabel("ECL Long/Lat");
	    break;
	case GALACTIC:
	    setFieldLabel("GAL Long/Lat");
	    break;
	}
    }

    /**
     * Set sexagesimal or decimal mode.
     */
    public void setSexagesimalMode(boolean enable) {
	_sexagesimalMode = enable;
    }

    /**
     * Select the field label.
     */
    private void setFieldLabel(String label) {
	_label.setText(" " + label + " ");
    }

    /**
     * Set the displayed cursor coordinates.
     */
    public void setCursorReadout(Vector3 v) {
	if(v == null) {
	    _xField.setText("");
	    _yField.setText("");
	} else {
	    Direction dir = new Direction(v);
	    if(_sexagesimalMode) {
		_xField.setText(X_FMT.formatDegrees(dir.getRaDegrees()));   
		_yField.setText(Y_FMT.formatDegrees(dir.getDecDegrees()));   
	    } else {
		_xField.setText(DEC_FMT.format(dir.getRaDegrees()));
		_yField.setText(DEC_FMT.format(dir.getDecDegrees()));
	    }
	}
    }
}
