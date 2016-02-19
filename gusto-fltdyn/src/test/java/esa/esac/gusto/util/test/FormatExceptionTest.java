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

import static org.junit.Assert.assertTrue;
import esa.esac.gusto.util.FormatException;

import org.junit.Test;

public class FormatExceptionTest {

    @Test(expected=FormatException.class)
    public void test1() {
	throw new FormatException();
    }

    @Test
    public void test2() {
	try {
	    throw new FormatException("message");
	} catch(FormatException e) {
	    assertTrue(e.getMessage().equals("message"));
	}
    }

    @Test
    public void test3() {
	try {
	    Throwable t = new RuntimeException("runtime");
	    throw new FormatException(t);
	} catch(FormatException e) {
	    assertTrue(e.getCause().getMessage().equals("runtime"));
	}
    }

    @Test
    public void test4() {
	try {
	    Throwable t = new RuntimeException("runtime");
	    throw new FormatException("message", t);
	} catch(FormatException e) {
	    assertTrue(e.getMessage().equals("message"));
	}
    }
}




