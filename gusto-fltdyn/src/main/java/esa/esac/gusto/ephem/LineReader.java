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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads orbit file line by line.
 *
 * @author  Jon Brumfitt
 */
public class LineReader {

    private BufferedReader _reader;
    private String _line;
    private int _lineNo;

    /**
     * Create a new LineReader.
     */
    public LineReader(String fileName) throws IOException {
	File file = new File(fileName);

	if(!file.exists() || !file.canRead()) {
	    throw new FileNotFoundException("Can't read " + fileName);
	}
	
	FileReader fr = new FileReader(file);
	_reader = new BufferedReader(fr);
    }

    /**
     * Create a new LineReader.
     */
    public LineReader(InputStream stream) throws IOException {
	InputStreamReader reader = new InputStreamReader(stream);

	_reader = new BufferedReader(reader);
    }

    
    public void close() throws IOException {
	_reader.close();
	_reader = null;
    }

    /**
     * Read the next non-blank line, skipping comments.
     */
    public void nextLine() throws IOException {
	do {
	    _line = _reader.readLine();
	    _lineNo++;
	    if(_line == null) {
		break;
	    }
	    _line = _line.trim(); 
	} while((_line.length() == 0) || (_line.startsWith("COMMENT")));
    }

    /**
     * Return the current line number.
     */
    public int getLineNumber() {
	return _lineNo;
    }

    /**
     * Return the current line.
     */
    public String getLine() {
	return _line;
    }
}

