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

package esa.esac.gusto.ephem.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import esa.esac.gusto.ephem.State;
import esa.esac.gusto.math.Vector3;

/**
 * Test harness for Vector2 class.
 *
 * @author  Jon Brumfitt
 */
public class StateTest {

    @Test
    public void testConstruction() {
	State ste = new State(1,2,3,4,5,6);
	Vector3 p = new Vector3(1,2,3);
	Vector3 v = new Vector3(4,5,6);
	
	State st1 = new State(p, v);
	assertTrue(equals(ste, st1));
	
	State st2 = new State(new double[]{1,2,3,4,5,6});
	assertTrue(equals(ste, st2));
    }
    
    @Test
    public void testPosition() {
	State st = new State(1,2,3,4,5,6);
	Vector3 p = st.position();
	assertTrue(p.equals(new Vector3(1,2,3)));
    }
    
    @Test
    public void testVelocity() {
	State st = new State(1,2,3,4,5,6);
	Vector3 p = st.velocity();
	assertTrue(p.equals(new Vector3(4,5,6)));
    }
    
    @Test
    public void testGetArray() {
	State st = new State(1,2,3,4,5,6);
	double[] a = st.getArray();
	double[] e = new double[] {1,2,3,4,5,6};
	assertTrue(equals(e,a));
    }
    
    @Test
    public void testAdd() {
	State st1 = new State(1,2,3,4,5,6);
	State st2 = new State(8,2,5,1,9,7);
	State ste = new State(9,4,8,5,14,13);
	
	State s1 = st1.add(st2);
	assertTrue(equals(s1, ste));
	assertTrue(equals(st1, new State(1,2,3,4,5,6)));
	
	State s2 = st1.mAdd(st2);
	assertTrue(equals(st1, ste));
	assertTrue(equals(s2, ste));
    }
    
    @Test
    public void testSubtract() {
	State st1 = new State(1,2,3,4,5,6);
	State st2 = new State(8,2,5,1,9,7);
	State ste = new State(-7,0,-2,3,-4,-1);
	
	State s1 = st1.subtract(st2);
	assertTrue(equals(s1, ste));
	assertTrue(equals(st1, new State(1,2,3,4,5,6)));
	
	State s2 = st1.mSubtract(st2);
	assertTrue(equals(st1, ste));
	assertTrue(equals(s2, ste));
    }
    
    @Test
    public void testMultiply() {
	State st1 = new State(1,2,3,4,5,6);
	State ste = new State(3,6,9,12,15,18);
	
	State s1 = st1.multiply(3);
	assertTrue(equals(s1, ste));
	assertTrue(equals(st1, new State(1,2,3,4,5,6)));
	
	State s2 = st1.mMultiply(3);
	assertTrue(equals(st1, ste));
	assertTrue(equals(s2, ste));
    }
    
    @Test
    public void testNegate() {
	State st1 = new State(1,2,3,4,5,6);
	State ste = new State(-1,-2,-3,-4,-5,-6);
	
	State s1 = st1.negate();
	assertTrue(equals(s1, ste));
	assertTrue(equals(st1, new State(1,2,3,4,5,6)));
	
	State s2 = st1.mNegate();
	assertTrue(equals(st1, ste));
	assertTrue(equals(s2, ste));
    }
    
    @Test
    public void testCopy() {
	State st1 = new State(1,2,3,4,5,6);
	State st2 = st1.copy();
	assertTrue(equals(st1, st2));
	assertTrue(equals(st1, new State(1,2,3,4,5,6)));
    }
    
    @Test
    public void testClone() {
	State st1 = new State(1,2,3,4,5,6);
	State st2 = (State)st1.clone();
	assertTrue(equals(st1, st2));
	assertTrue(equals(st1, new State(1,2,3,4,5,6)));
    }
    
    /**
     * Test toString.
     * The format is subject to change so this is a partial test.
     */
    @Test
    public void testToString() {
	State st = new State(1,2,3,4,5,6);
	String s = st.toString();
	assertFalse(s == null);
	assertTrue(s.length() > 0);
    }
    
    /**
     * Utility method to test two arrays for equality.
     */
    private static boolean equals(double[] a, double[] b) {
	if(a.length != b.length) {
	    return false;
	}
	for(int i=0; i<a.length; i++) {
	    if(a[i] != b[i]) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Utility method to test two States for equality.
     */
    private static boolean equals(State a, State b) {
	Vector3 pa = a.position();
	Vector3 pb = b.position();
	Vector3 va = a.velocity();
	Vector3 vb = b.velocity();
	return pa.equals(pb) && va.equals(vb); 
    }
}
