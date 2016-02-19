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

package esa.esac.gusto.ephem.horizons;

import esa.esac.gusto.ephem.Ephem;
import esa.esac.gusto.util.CachedProvider;
import esa.esac.gusto.util.LRUCachedProvider;
import esa.esac.gusto.util.Provider;

/**
 * Ephemerides from Horizons state vector files.
 *
 * @author  Jon Brumfitt
 */
public class Horizons extends BasicHorizons {
    
    private CachedProvider<Integer, HorizonsEphem> _cache;

    /**
     * Create a new Horizons object.
     */
    public Horizons(String ssoDir, Ephem ephem) {
	Provider<Integer, HorizonsEphem> horiz = new HorizonsFileEphemSet(ssoDir);
	_cache = new LRUCachedProvider<Integer,HorizonsEphem>(horiz, false);
	init(_cache, ephem);
    }
    
    /**
     * Set the maximum cache size.
     */
    public void setCache(int cacheSize) {
	_cache.setCacheSize(cacheSize);
    }
}

