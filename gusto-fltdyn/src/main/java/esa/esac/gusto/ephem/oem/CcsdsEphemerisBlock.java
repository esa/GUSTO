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

import esa.esac.gusto.ephem.AbstractEphemerisBlock;
import esa.esac.gusto.ephem.LineReader;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * A block of orbit ephemeris records for a CCSDS OEM orbit file.<p>
 *
 * The format of this file is described in the following CCSDS
 * standard: "Orbit Data Messages", CCSDS 502.0-B-1, Blue Book,
 * September 2004.
 *
 * @author  Jon Brumfitt
 */
class CcsdsEphemerisBlock extends AbstractEphemerisBlock {

    private String _interpolationType = "LAGRANGE";
    private int _order = 8;
    private LineReader _reader;
	
    /**
     * Create a new EphemerisBlock.
     */
    CcsdsEphemerisBlock(LineReader reader) {
	_reader = reader;
    }
    
    /**
     * Read the file.
     */
    public boolean read() throws IOException {
	readMetaData();
	createArrays();
	return readEphemerisBlock();
    }
    
    /**
     * Read the meta-data.
     */
    protected void readMetaData() throws IOException {
	parseKeyword("META_START");
	parseKeywordString("OBJECT_NAME");
	parseKeywordString("OBJECT_ID");
	parseKeywordString("CENTER_NAME", "EARTH");
	parseKeywordString("REF_FRAME", "EME2000");
	parseKeywordString("TIME_SYSTEM", "TDB");

	/*
	 * Get the usable start and stop times of the block.
	 * If these are not present, use the actual start and stop times.
	 */
	_tStart = parseKeywordTime("START_TIME");
 	String s = parseOptionalKeywordString("USEABLE_START_TIME");
	if(s != null) {
	    _tStart = CcsdsOemReader.parseTime(s);
	}
	s = parseOptionalKeywordString("USEABLE_STOP_TIME");	
	_tEnd = parseKeywordTime("STOP_TIME");
	if(s != null) {
	    _tEnd = CcsdsOemReader.parseTime(s);
	}

	// Read the interpolation method and polynomial degree (optional).
	String interp = parseOptionalKeywordString("INTERPOLATION");
	
	if(interp != null) {
	    _interpolationType = interp;
	    String degree = parseKeywordString("INTERPOLATION_DEGREE");
	    _order = Integer.parseInt(degree);
	}

	parseKeyword("META_STOP");
    }
    
    /**
     * Read the ephemeris records.
     */
    protected boolean readEphemerisBlock() throws IOException {
	_nSamples = 0;
	    
	// Read the ephemeris records
	while((_reader.getLine() != null) && (!_reader.getLine().equals("META_START"))) {
	    ensureSize(_nSamples + 1);

	    StringTokenizer st = new StringTokenizer(_reader.getLine());
		
	    // Read time and convert to MJD2000
	    _x[_nSamples] = CcsdsOemReader.parseTime(st.nextToken());
		
	    // Read state vector (km & km/s)
	    for(int i=0; i<NV; i++) {
		_fs[i][_nSamples] = Double.parseDouble(st.nextToken());
	    }

	    _nSamples++;
	    _reader.nextLine();
	}
	resizeArrays(_nSamples);
	setInterpolator(_interpolationType, _order);

	return (_reader.getLine() != null);
    }   

    //---------- Parsing keywords ----------

    /**
     * Parse a keyword.
     */
    protected void parseKeyword(String keyword) throws IOException {
	StringTokenizer st = new StringTokenizer(_reader.getLine());
	String name = st.nextToken().trim();

	if(!name.equals(keyword)) {
	    parseError("Expected keyword: " + keyword + ", found: " + name);
	}
	_reader.nextLine();
    }

    /**
     * Parse a keyword=string and return the value String.
     */
    protected String parseKeywordString(String keyword) throws IOException {
	StringTokenizer st = new StringTokenizer(_reader.getLine());
	String name = st.nextToken("=").trim();
	String value = st.nextToken().trim();
	
	if(!name.equals(keyword)) {
	    parseError("Expected keyword: " + keyword + ", found: " + name);
	}
	_reader.nextLine();
	return value;
    }

    /**
     * Parse keyword and check for expected String value.
     */
    protected void parseKeywordString(String keyword, String expected) throws IOException {
	String value = parseKeywordString(keyword);

	if(!value.equals(expected)) {
	    parseError("Keyword: " + keyword 
		       + ", expected value=" + expected + ", found: " + value);
	}
    }

    /**
     * Parse an optional keyword=string and return the value if found, else null.
     */
    protected String parseOptionalKeywordString(String keyword) throws IOException {
	StringTokenizer st = new StringTokenizer(_reader.getLine());
	String name = st.nextToken("=").trim();
	
	if(name.equals(keyword)) {
	    String value = st.nextToken().trim();
	    _reader.nextLine();
	    return value;
	} else {
	    return null;
	}
    }

    /**
     * Parse keyword=CcsdsTime and return the time value.
     */
    private double parseKeywordTime(String keyword) throws IOException {
	return CcsdsOemReader.parseTime(parseKeywordString(keyword));
    }
    
    
    //---------- Error handling ----------
    
    /**
     * Throw an exception giving details of a parse error.
     */
    private void parseError(String message) throws IOException {
	throw new IOException("Error parsing orbit file\n"
			      + message + " at line " + _reader.getLineNumber());
    }
}

