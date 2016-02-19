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

package esa.esac.gusto;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * JUnit tests.
 *
 * @author  Jon Brumfitt
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    esa.esac.gusto.constraint.test.AllTests.class,
    esa.esac.gusto.ephem.AllTests.class,
    esa.esac.gusto.math.test.AllTests.class,
    esa.esac.gusto.time.test.AllTests.class,
    esa.esac.gusto.util.test.AllTests.class
})

public class AllTests {
}

