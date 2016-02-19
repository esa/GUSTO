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

package esa.esac.gusto.ephem;

import esa.esac.gusto.math.Vector3;
 
/**
 * Orbital state vector describing position and velocity.
 *
 * @author  Jon Brumfitt
 */
public class State implements Cloneable {
    private double[] _a;
    
    /**
     * Create new state with zeroes.
     */
    public State() {
	_a = new double[] {0,0,0,0,0,0};
    }
    
    /**
     * Create a new State.
     * 
     * @param x  X component of position
     * @param y  Y component of position
     * @param z  Z component of position
     * @param xv X component of velocity
     * @param yv Y component of velocity
     * @param zv Z component of velocity
     */
    public State(double x, double y, double z, double xv, double yv, double zv) {
	_a = new double[] {x,y,z,xv,yv,zv};
    }
    
    /**
     * Create a new State from an array.<p>
     * 
     * The array is not copied. Do not modify the original array as this
     * behavior may change in future releases.
     * 
     * @param array [x, y, z, vx, vy, vz]
     */
    public State(double[] array) {
	_a = array;
    }
    
    /**
     * Create a new State from position and velocity vectors.
     * 
     * @param position Position vector
     * @param velocity Velocity vector
     */
    public State(Vector3 position, Vector3 velocity) {
	this(position.getX(), position.getY(), position.getZ(),
	     velocity.getX(), velocity.getY(), velocity.getZ());
    }
    
    /**
     * Return the array.
     */
    public double[] getArray() {
	return _a.clone();
    }
    
    /**
     * Return the position vector.
     * 
     * @return Position vector
     */
    public Vector3 position() {
	return new Vector3(_a[0], _a[1], _a[2]);
    }
    
    /**
     * Return the velocity vector.
     * 
     * @return Velocity vector
     */
    public Vector3 velocity() {
	return new Vector3(_a[3], _a[4], _a[5]);
    }
    
    /**
     * Return the sum of this state and another state, as a new State.
     *  
     * @param s The other state
     * @return The sum of the two states
     */
    public State add(State s) {
	return copy().mAdd(s);
    }
    
    /**
     * Add another state in-place.
     *
     * @param s The other state
     * @return This state after adding the other state
     */
    public State mAdd(State s) {
	_a[0] += s._a[0];
	_a[1] += s._a[1];	
	_a[2] += s._a[2];
    	_a[3] += s._a[3];
	_a[4] += s._a[4];
	_a[5] += s._a[5];
	return this;
    }
	
    /**
     * Return the result of subtracting another state from this one, as a new state.
     *  
     * @param s The other state
     * @return The difference between the two states
     */
    public State subtract(State s) {
	return copy().mSubtract(s);
    }
    
    /**
     * Return the result of subtracting another state from this state, 
     * as a new state.
     *
     * @param s The other state
     * @return The difference between the two states
     */
    public State mSubtract(State s) {
	_a[0] -= s._a[0];
	_a[1] -= s._a[1];	
	_a[2] -= s._a[2];
    	_a[3] -= s._a[3];
	_a[4] -= s._a[4];
	_a[5] -= s._a[5];
	return this;
    }
    
    /**
     * Return the result of multiplying this state by a scalar, as a new state.
     *
     * @param k The scalar
     * @return The product
     */
    public State multiply(double k) {
	return copy().mMultiply(k);
    }
    
    /**
     * Multiply by a scalar in-place.
     *
     * @param k The scalar
     * @return This state after multiplying by the scalar
     */
    public State mMultiply(double k) {
	_a[0] *= k;
	_a[1] *= k;	
	_a[2] *= k;
    	_a[3] *= k;
	_a[4] *= k;
	_a[5] *= k;
	return this;
    }
    
    /**
     * Negate each element of the state, returning a new state.
     *
     * @return The negated state
     */
    public State negate() {
	return copy().mNegate();
    }
	
    /**
     * Negate this state in-place.
     *
     * @return This state after negation
     */
    public State mNegate() {
	_a[0] = -_a[0];
	_a[1] = -_a[1];	
	_a[2] = -_a[2];
    	_a[3] = -_a[3];
	_a[4] = -_a[4];
	_a[5] = -_a[5];
	return this;
    }
    
    /**
     * Return a copy of this state.
     *
     * @return A copy of this state
     */
    public State copy() {
	return new State(_a.clone());
    }
    
    /**
     * Return a clone of this object.
     *
     * @return A clone of this object
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch(CloneNotSupportedException e) {
	    throw new Error("Assertion failed");
	}
    }

   /**
     * Return a string representation of the state.<p>
     *
     * The exact details of the representation are unspecified
     * and subject to change.
     *
     * @return String representation of this state
     */
    public String toString() {
	StringBuffer buff = new StringBuffer("(");
	buff.append(_a[0] + ",");
	buff.append(_a[1] + ",");
	buff.append(_a[2] + ") (");
	buff.append(_a[3] + ",");
	buff.append(_a[4] + ",");
	buff.append(_a[5] + ")");
	return buff.toString();
    }
} 