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

package esa.esac.gusto.util.test;

import esa.esac.gusto.util.CachedProvider;
import esa.esac.gusto.util.LRUCachedProvider;
import esa.esac.gusto.util.Provider;

import org.junit.Assert;
import org.junit.Test;
/**
 * Test harness for LRUCache.
 *
 * @author  Jon Brumfitt
 */
public class CachedProviderTest {

    /**
     * Test the LRU cache.<p>
     * 
     * It is not possible to check how many items are cached as this is
     * hidden within the implementation. So just check that the data is
     * accessible.
     */
    @Test
    public void testLRUCache() {
	Provider<Integer,String> map = new Provider<Integer,String>() {
	    public String get(Integer key) {
		return "" + key;
	    }
	};

	CachedProvider<Integer,String> cache = new LRUCachedProvider<Integer,String>(map,false);

	// Set initial cache size
	cache.setCacheSize(4);
	Assert.assertEquals(cache.get(25), "25");
	Assert.assertEquals(cache.get(0),  "0");
	Assert.assertEquals(cache.get(17), "17");
	Assert.assertEquals(cache.get(13), "13");
	Assert.assertEquals(cache.get(22), "22");
	Assert.assertEquals(cache.get(4),  "4");
	Assert.assertEquals(cache.get(17), "17"); // Repeated

	// Increase the cache size
	cache.setCacheSize(6);
	Assert.assertEquals(cache.get(37), "37");
	Assert.assertEquals(cache.get(9),  "9");
	Assert.assertEquals(cache.get(26), "26");
	Assert.assertEquals(cache.get(15), "15");

	// Reduce the cache size
	cache.setCacheSize(1);
	Assert.assertEquals(cache.get(12), "12");
	Assert.assertEquals(cache.get(57), "57");

	// Set cache size to zero
	cache.setCacheSize(0);
	Assert.assertEquals(cache.get(28), "28");
	Assert.assertEquals(cache.get(57), "57");
    }

    @Test
    public void testLRUCache2() {
	Provider<Integer,String> map = new Provider<Integer,String>() {
	    public String get(Integer key) {
		return "" + key;
	    }
	};

	CachedProvider<Integer,String> cache = new LRUCachedProvider<Integer,String>(map,true);

	// Set initial cache size
	cache.setCacheSize(4);
	Assert.assertEquals(cache.get(25), "25");
	Assert.assertEquals(cache.get(0),  "0");
	Assert.assertEquals(cache.get(17), "17");
	Assert.assertEquals(cache.get(13), "13");
    }
}








