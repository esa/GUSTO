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

package esa.esac.gusto.ephem.de405;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

/**
 * Reader for files with multiple tokens per line.<p>
 * 
 * This allows a text file with several tokens per line to be read ignoring line breaks.
 * 
 * @author Jon Brumfitt
 */
public class TokenizingReader {

    private BufferedReader _reader;
    private String[] _toks;
    private int _index;
    private int _lineNumber;
    private String _regex;

    /**
     * Create a new TokenizingReader for an InputStream with default space separator.
     * 
     * @param is An inputStream
     */
    public TokenizingReader(InputStream is) throws IOException {
	this(is, " +");
    }
    
    /**
     * Create a new TokenizingReader for an InputStream.
     * 
     * @param is An inputStream   
     * @param regex Regular expression used as the token delimiter
     */
    public TokenizingReader(InputStream is, String regex) throws IOException {
	_regex = regex;
	_reader = new BufferedReader(new InputStreamReader(is));
	readLine();
    }   
    
    /**
     * Check whether there are more token to be read.
     * 
     * @return whether there are more tokens
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
	// FIXME: Does not handle blank lines
	return _index < _toks.length;
    }
    
    /**
     * Return the next token as a String.
     * 
     * @return the next token
     */
    public String nextToken() {
	try {
	    String token = _toks[_index++];
	    if(_index >= _toks.length) {
		readLine();
	    }
	    return token;
	} catch(IOException e) {
	    throw new UncheckedIOException(e);
	}
    }
    
    /**
     * Return the next token as an integer value.
     * 
     * @return the next token
     */
    public int nextInt() {
	return Integer.parseInt(nextToken());
    }
    
    /**
     * Return the next token as a double value.
     * 
     * @return the next token
     */
    public double nextDouble() {
	return Double.parseDouble(nextToken());
    }

    /**
     * Return the next token as a double value.<p>
     * 
     * Fortran doubles may use 'D' instead of 'E' before the exponent.
     * e.g. 1.23D-04
     * 
     * @return the next token
     */
    public double nextFortranDouble() {
	String s = nextToken().replace('D','E');
	return Double.parseDouble(s);
    }
    
    /**
     * Read the next line of the file.
     * 
     * @return false if end-of-file is reached
     * @throws IOException
     */
    private boolean readLine() throws IOException {
	String line = _reader.readLine();
	if(line == null) {
	    return false;
	}
	_toks = line.trim().split(_regex);
	_index = 0;
	_lineNumber++;
	return true;
    }
    
    /**
     * Skip the next 'n' tokens.
     * 
     * @param n Number of tokens to skip
     */
    public void skipTokens(int n) {
	for(int i=0; i<n; i++) {
	    nextToken();
	}
    }
    
    /**
     * Return the line number (counting from 1)
     * 
     * @return Line number
     */
    public int getLineNumber() {
	return _lineNumber;
    }
    
    /**
     * Return the token number on the current line.
     * 
     * @return the token number
     */
    public int getTokenNumber() {
	return _index;
    }
    
    /**
     * Close the stream.
     */
    public void close() throws IOException {
	_reader.close();
    }
}
