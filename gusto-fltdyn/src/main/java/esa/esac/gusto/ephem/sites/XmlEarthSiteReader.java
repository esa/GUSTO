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

//package herschel.mps.scheduler.xml;

package esa.esac.gusto.ephem.sites;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reader for XML Earth sites file.
 *
 * @author  Jon Brumfitt
 */
public class XmlEarthSiteReader {

    private DefaultHandler _handler;

    /**
     * Create a new XmlEarthSitesReader.
     */
    public XmlEarthSiteReader(EarthSites stations) {
	_handler = new XmlEarthSiteHandler(stations);
	//	((XmlIdHandler)_handler).setDialogHandler(dialogs);
    }

    /**
     * EntityResolver for DTD file.
     */
    static class DtdResolver implements EntityResolver {
	public InputSource resolveEntity (String publicId, String systemId) {
	    if(systemId.endsWith("ground_stations.dtd")) {
		// Read the DTD from a resource in the JAR file
		InputStream is = getClass().getResourceAsStream("ground_stations.dtd");
		return new InputSource(is);
	    } else {
		return null;  // Default
	    }
	}
    }

    /**
     * Read from a file.
     */
    public void readFile(InputStream is) {    
	try {
	    XMLReader parser = XMLReaderFactory.createXMLReader();

	    parser.setContentHandler(_handler);
	    parser.setErrorHandler(_handler);
	    parser.setEntityResolver(new DtdResolver());

	    parser.setFeature("http://xml.org/sax/features/validation", true);

	    InputSource src = new InputSource(is);
	    parser.parse(src);
	    is.close();
	} 
	catch(SAXException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
	catch(IOException e) {
	    throw new RuntimeException("Cannot read XML file", e);
	}
    }

    /**
     * Read from a file.
     */
    public void readFile(File file) {    
	try {
	    XMLReader parser = XMLReaderFactory.createXMLReader();

	    parser.setContentHandler(_handler);
	    parser.setErrorHandler(_handler);
	    parser.setEntityResolver(new DtdResolver());

	    parser.setFeature("http://xml.org/sax/features/validation", true);

	    FileInputStream fis = new FileInputStream(file);
	    InputSource src = new InputSource(fis);
	    parser.parse(src);
	    fis.close();
	} 
	catch(SAXException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
	catch(IOException e) {
	    throw new RuntimeException("Cannot read XML file", e);
	}
    }
}

