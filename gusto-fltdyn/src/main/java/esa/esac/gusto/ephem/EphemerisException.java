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

/**
 * Exception when reading an ephemeris.
 *
 * @author  Jon Brumfitt
 */
public class EphemerisException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new EphemerisException without a message.
     */
    public EphemerisException() {
	super();
    }

    /**
     * Create a new EphemerisException with a message.
     *
     * @param message A message to explain the reason for the exception
     */
    public EphemerisException(String message) {
	super(message);
    }

    /**
     * Create a new EphemerisException with a cause.
     *
     * @param cause The original cause of the exception (may be null)
     */
    public EphemerisException(Throwable cause) {
	super(cause);
    }

    /**
     * Create a new EphemerisException with a message and a cause.
     *
     * @param message A message to explain the reason for the exception
     * @param cause The original cause of the exception (may be null)
     */
    public EphemerisException(String message, Throwable cause) {
	super(message, cause);
    }
}

