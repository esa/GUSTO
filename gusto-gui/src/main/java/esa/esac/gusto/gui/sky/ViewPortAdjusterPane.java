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


import java.util.Map;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Control panel for adjusting SkyView display.
 *
 * @author  Jon Brumfitt
 */
public class ViewPortAdjusterPane extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private EventListenerList _listenerList = new EventListenerList();
    private transient ChangeEvent _changeEvent = null;

    private JButton    _centerButton;
    private JButton    _zoomInButton;
    private JButton    _zoomOutButton;
    private ViewPortController _controller;
    private Map<String,Action> _actions;

    /**
     * Create a new ViewPortAdjusterPane.
     */
    public ViewPortAdjusterPane(ViewPortController controller) {
	
	_controller = controller;
	_actions = _controller.getActions();
	
	final int inset = 5;
	setBorder(new EmptyBorder(inset,inset,inset,inset));

	Box box = Box.createHorizontalBox();
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	box.add(Box.createHorizontalStrut(20));
	box.add(Box.createHorizontalGlue());
	
	_centerButton = createButton("Center", "centerIcon");
	box.add(_centerButton);
	box.add(Box.createHorizontalStrut(10));
	
	box.add(createButton("Pan up", "upIcon"));
	box.add(createButton("Pan down", "downIcon"));
	box.add(Box.createHorizontalStrut(10));
	
	box.add(createButton("Pan left", "leftIcon"));
	box.add(createButton("Pan right", "rightIcon"));
	box.add(Box.createHorizontalStrut(10));
	
	box.add(createButton("Rotate left", "turnLeftIcon"));
	box.add(createButton("Rotate right", "turnRightIcon"));
	box.add(Box.createHorizontalStrut(10));
	
	_zoomInButton = createButton("Zoom in", "zoomInIcon");
	box.add(_zoomInButton);
	
	_zoomOutButton = createButton("Zoom out", "zoomOutIcon");
	box.add(_zoomOutButton);

	add(box);
    }
 
    /**
     * Return a new FixedSizeButton with the specified icon.
     */
    private JButton createButton(String name, String iconName) {
	JButton button = new FixedSizeIconButton(_actions.get(name), getIcon(iconName));
	button.setToolTipText(name);
	return button;
    }
 
    /**
     * Return an Icon obtained from a resource file <name>.gif.
     */
    Icon getIcon(String name) {
	return new ImageIcon(getClass().getResource("images/" + name + ".gif"));
    }
    

    //--------------------------
    //  MVC interaction
    //--------------------------

    /**
     * Add a listener to receive notification when the model state changes.
     */
    public void addChangeListener(ChangeListener l) {
	_listenerList.add(ChangeListener.class, l);
    }

    /**
     * Remove a listener so that it no longer receives notifications.
     */
    public void removeChangeListener(ChangeListener l) {
	_listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Notify all listeners that the model state has changed.
     */
    protected void fireChange() {
	Object[] listeners = _listenerList.getListenerList();
	for(int i = listeners.length - 2; i >= 0; i -= 2) {
	    if(listeners[i] == ChangeListener.class) {
		if(_changeEvent == null) {
		    _changeEvent = new ChangeEvent(this);
		}
		((ChangeListener)listeners[i+1]).stateChanged(_changeEvent);
	    }
	}
    }
}

