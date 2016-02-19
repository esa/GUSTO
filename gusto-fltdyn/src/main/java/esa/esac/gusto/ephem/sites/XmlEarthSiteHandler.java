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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for XML Earth sites file.
 *
 * @author  Jon Brumfitt
 */
class XmlEarthSiteHandler extends DefaultHandler {
    
//  private Locator _locator;
    private StringBuffer _buff;
    private String _id;
    private String _name;
    private double _longitude;
    private double _latitude;
    private double _altitude;

    private EarthSites _stations;

    /**
     * Create a new XmlEarthSitesHandler.
     */
    public XmlEarthSiteHandler(EarthSites stations) {
	_stations = stations;
    }


    //--------------- ContentHandler ---------------

    /**
     * Receive notification of start of document.
     */
    public void startDocument() {
    }

    /**
     * Receive notification of end of document.
     */
    public void endDocument() {
    }

    /**
     * Receive notification of start of an element.
     */
    public void startElement(String uri, String localName, String qualifiedName,
                             Attributes attrs)
	    throws SAXException {

	String name = localName;
        if (name.equals("")) {
            name = qualifiedName;
        }

	_buff = new StringBuffer();
	
	if(name.equals("ground_stations")) {
	    
	} else if(name.equals("ground_station")) {
	    
	    // Get attributes
	    for(int i=0; i<attrs.getLength(); i++) {
		String aName = attrs.getQName(i);
		String aVal = attrs.getValue(i);
		
		if(aName.equals("id")) {
		    _id = aVal;
		}
	    }
	    _longitude = 0;
	    _latitude = 0;
	    _altitude = 0;
	    _name = "";
	}
    }
    
    /**
     * Receive notification of end of an element.
     */
    public void endElement(String uri, String localName, String qualifiedName)
    throws SAXException {

	String name = localName;
	if(name.equals("")) {
	    name = qualifiedName;
	}

	try {
	    if(name.equals("ground_stations")) {

	    } else if(name.equals("ground_station")) {
		_stations.addStation(_id, _name, _longitude, _latitude, _altitude);

	    } else if(name.equals("name")) {
		_name = _buff.toString().trim();

	    } else if(name.equals("longitude")) {
		_longitude = Double.parseDouble(_buff.toString().trim());
		if(_longitude < 0) {
		    _longitude += 360.0;
		}

	    } else if(name.equals("latitude")) {
		_latitude = Double.parseDouble(_buff.toString().trim());

	    } else if(name.equals("altitude")) {
		_altitude = Double.parseDouble(_buff.toString().trim());
	    }
	} catch(NumberFormatException e) {
	    throw new SAXException("Number format", e);
	}
    }

    /**
     * Receive notification of ignorable whitespace.
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
	    throws SAXException {
    }

    /**
     * Receive notification of character data inside an element.
     */
    public void characters(char[] text, int start, int length)
	    throws SAXException {
	_buff.append(text, start, length);
    }

//    /**
//     * Receive notification of the Locator.
//     */
//    public void setDocumentLocator(Locator locator) {
//	_locator = locator;
//    }


    //--------------- ErrorHandler ---------------

    /**
     * Receive notification of a recoverable error.<p>
     *
     * This is typically a validation error.
     */
    public void error(SAXParseException e) {
	String message = formatMessage("Error", e);
	throw new RuntimeException(message, e);
    }
    
    /**
     * Receive notification of a non-recoverable error.<p>
     *
     * This is typically a well-formedness error.
     */
    public void fatalError(SAXParseException e) {
	String message = formatMessage("Error", e);
	throw new RuntimeException(message, e);
    }
    
    /**
     * Receive notification of a warning.
     */
    public void warning(SAXParseException e) {
	String message = formatMessage("XML warning", e);
	throw new RuntimeException(message, e);
    }
    
    /**
     * Format a SAXParseException as an error message.
     */
    private String formatMessage(String prefix, SAXParseException e) {
	StringBuffer buff = new StringBuffer(prefix);
	buff.append(" in file ");
	buff.append(" at line=");
	buff.append(e.getLineNumber());
	buff.append(", column=");
	buff.append(e.getColumnNumber());
	buff.append("\n");
	buff.append(e.getMessage());
	
	return buff.toString();
    }
}

