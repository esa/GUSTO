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

package esa.esac.gusto.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cached provider uses a Least Recently Used (LRU) caching algorithm.
 *
 * @author  Jon Brumfitt
 */
public class LRUCachedProvider<K,T> implements CachedProvider<K,T> {
    
    private static final int MAX_CACHE = 50;         // Number of targets to cache
    private static final int INITIAL_CAPACITY = 16;  // Initial size of MAP
    private static final float LOAD_FACTOR = 0.75F;  // HashMap loaed factor
    
    private Map<K,T>_map;
    private Provider<K,T> _provider;
    private int _cacheSize;
    
    /**
     * Create a new LRUCachedProvider.
     * 
     * The parameter 'synchronized' provided synchronized access to the cache.
     * It does not ensure that the underlying provider is synchronized.
     * 
     * @param provider The source of data to be cached.
     * @param synchronize True if synchronized access is required.
     */
    @SuppressWarnings("serial")
    public LRUCachedProvider(Provider<K,T> provider, boolean synchronize) {
	_provider = provider;
	_cacheSize = MAX_CACHE;

	_map = new LinkedHashMap<K, T>(INITIAL_CAPACITY, LOAD_FACTOR, true) {
	    // This method is called just after a new entry has been added
	    public boolean removeEldestEntry(Map.Entry<K,T> eldest) {
		return size() > _cacheSize; }
  	}; 
  	
  	if(synchronize) {
  	    _map = Collections.synchronizedMap(_map);
  	}
    }
	
    /**
     * Change the maximum number of items that are cached.<p>
     * 
     * If the value is reduced below the number of items currently in the cache,
     * the cache may not be reduced in size. However, increasing the value above
     * the number of items currently in the map will allow the cache to grow.
     * 
     * @param cacheSize
     */
    public void setCacheSize(int cacheSize) {
	_cacheSize = cacheSize;
    }
    
    /**
     * Return an item for a given key.
     */
    public T get(K key) {
	T item = _map.get(key);
	if(item == null) {
	    item = _provider.get(key);
	    _map.put(key, item);
	}
	return item;
    }
}

