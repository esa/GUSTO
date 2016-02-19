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

package esa.esac.gusto.ephem.oem;

import esa.esac.gusto.ephem.EphemerisException;
import esa.esac.gusto.ephem.EphemerisSet;
import esa.esac.gusto.ephem.LineReader;
import esa.esac.gusto.time.TaiTime;
import esa.esac.gusto.time.Mjd2000TimeFormat;
import esa.esac.gusto.time.SimpleTimeFormat;
import esa.esac.gusto.time.TimeScale;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

/**
 * Reads CCSDS orbit file and interpolates ephemerides.
 *
 * @author  Jon Brumfitt
 */
public class CcsdsOemReader extends EphemerisSet {

    private static final Mjd2000TimeFormat mjdTdbFmt = new Mjd2000TimeFormat(TimeScale.TDB);
    private static SimpleTimeFormat formatter = new SimpleTimeFormat(TimeScale.TDB);

    static {
	formatter = new SimpleTimeFormat(TimeScale.TDB);
	formatter.setDecimals(6);
    }

    private LineReader _reader;
    private String _fileName;

    /**
     * Create a new OEM reader for a file.
     *
     * @param fileName Name of orbit file
     */
    public CcsdsOemReader(String fileName) {
	init(fileName);
    }
    
    /**
     * Create a new OEM reader for a stream.
     */
    public CcsdsOemReader(InputStream is) {
	init(is);
    }
    
    /**
     * Create a new OEM reader for a file for a specified time range.
     */
    public CcsdsOemReader(String fileName, double tStart, double tEnd) {
	// FIXME - The time arguments are currently ignored
	this(fileName);
    }
    
    /**
     * Create a new OEM reader for a stream for a specified time range.
     */
    public CcsdsOemReader(InputStream is, double tStart, double tEnd) {
	// FIXME - The time arguments are currently ignored
	this(is);
    }

    /**
     * Initialize the orbit reader.
     */
    private void init(InputStream is) {
	_fileName = "<inputstream>";
	try {
	    _reader = new LineReader(is);
	    readOrbitFile();
	
	} catch(IOException e) {
	    throw new EphemerisException("Error reading OEM stream", e);
	}
    } 
    
    /**
     * Initialize the orbit reader.
     */
    private void init(String fileName) {
	_fileName = fileName;
	try {
	    _reader = new LineReader(fileName);
	    readOrbitFile();
	
	} catch(IOException e) {
	    throw new EphemerisException("Error reading OEM file: " + fileName, e);
	}
    }

    /**
     * Read the orbit file into arrays.
     */
    private void readOrbitFile() throws IOException {
	_reader.nextLine();
	readFileHeader();
	boolean more;
	do {
	    CcsdsEphemerisBlock block = new CcsdsEphemerisBlock(_reader);

	    more = block.read();
	    addBlock(block);
	    
	} while(more);
	_reader.close();
    }
    
    /**
     * Read file header.
     */
    private void readFileHeader() throws IOException {
	// Parse file format and version
	StringTokenizer st = new StringTokenizer(_reader.getLine());
	String name = st.nextToken("=").trim();
	String value = st.nextToken().trim();
	
	if(name.equals("CCSDS_OEM_VERS")) {
	    System.out.println("Reading CCSDS orbit file: " + _fileName);
	    if(!value.equals("1.0")) {
		throw new EphemerisException("Unsupported OEM version = " + value);
	    }

	    // FIXME: Parse the header properly *** !!!
	    _reader.nextLine();
	    _reader.nextLine();
	} else {
	    throw new EphemerisException("Unknown orbit file format");
	}

	_reader.nextLine();
    }
    
    /**
     * Parse a CCSDS time with up to 6 decimal places.
     */
    public static double parseTime(String s) {
	TaiTime ft = formatter.parse(s + " TDB");
	return mjdTdbFmt.TaiTimeToMjd2000(ft);
    }
}

