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

package esa.esac.gusto.gui.projection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The set of available projections.
 * 
 * @author  Jon Brumfitt
 */
public class SkyProjections {
    private Map<String, SkyProjection> _map;
    
    /**
     * Create a set of Projections.
     */
    public SkyProjections() {
	_map = new LinkedHashMap<String, SkyProjection>();
	addProjection(new OrthographicProjection());
	addProjection(new GnomonicProjection());
	addProjection(new MollweideProjection());
	addProjection(new HammerProjection());
	addProjection(new LambertCylindricalProjection());
    }
    
    /**
     * Add a new Projection.
     * 
     * @param projection
     */
    public void addProjection(SkyProjection projection) {
	_map.put(projection.getName(), projection);
    }
    
    /**
     * Return the set of projection names.
     * 
     * @return The set of projection names
     */
    public Set<String> getNames() {
	return _map.keySet();
    }
    
    /**
     * Return the available Projections.
     * 
     * @return The Projections
     */
    public Collection<SkyProjection> getProjections() {
	return _map.values();
    }

    /**
     * Return the Projection with the specified name.
     * 
     * @param name Name of Projection
     * @return The Projection
     */
    public SkyProjection get(String name) {
	return _map.get(name);
    }
}
