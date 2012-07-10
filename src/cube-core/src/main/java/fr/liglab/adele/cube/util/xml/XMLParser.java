/*
 * Copyright 2011 Adele Team LIG (http://www-adele.imag.fr/)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.liglab.adele.cube.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import fr.liglab.adele.cube.extensions.core.CoreExtension;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.util.parser.ParseException;

/**
 * XML Metadata parser.
 * 
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class XMLParser implements ContentHandler, ErrorHandler {

    /**
     * Element of the metadata.
     */
    private XMLElement[] m_elements = new XMLElement[0];

    /**
     * Get parsed metadata.
     * The document must be parsed before calling this method. 
     * @return : all the metadata.
     * @throws ParseException : occurs if an error occurs during the parsing.
     */
    public XMLElement[] getMetadata() throws ParseException {
        return m_elements[0].getElements();
    }


    /**
     * Characters.
     * @param ch : character
     * @param start : start
     * @param length : length
     * @throws SAXException : can never occurs.
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        // NOTHING TO DO

    }


    /**
     * End the document.
     * @throws SAXException : can never occrus.
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
    }


    /**
     * End of an element.
     * @param namespaceURI : element namespace
     * @param localName : local name
     * @param qName : qualified name
     * @throws SAXException : occurs when the element is malformed
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        // Get the last element of the list
    	XMLElement lastElement = removeLastElement();

        // The name is consistent
        // Add this element last element with if it is not the root
        if (m_elements.length != 0) {
        	XMLElement newQueue = m_elements[m_elements.length - 1];
            newQueue.addElement(lastElement);
        } else {
            // It is the last element
            addElement(lastElement);
        }

    }

    /**
     * End prefix mapping.
     * @param prefix : ended prefix
     * @throws SAXException : can never occurs.
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        // NOTHING TO DO
    }


    /**
     * Ignore whitespace.
     * @param ch : character
     * @param start : start
     * @param length : length
     * @throws SAXException : can never occurs. 
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // NOTHING TO DO
    }

    /**
     * Process an instruction.
     * @param target : target
     * @param data : data
     * @throws SAXException : can never occurs.
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String target, String data) throws SAXException {
        // DO NOTHING
    }

    /**
     * Set Document locator.
     * @param locator : new locator.
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
        // NOTHING TO DO

    }

    /**
     * Skipped entity.
     * @param name : name.
     * @throws SAXException : can never occurs.
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) throws SAXException {
        // NOTHING TO DO

    }

    /**
     * Start a document.
     * @throws SAXException : can never occurs.
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
    }


    /**
     * Start an element.
     * @param namespaceURI : element namespace.
     * @param localName : local element.
     * @param qName : qualified name.
     * @param atts : attribute
     * @throws SAXException : occurs if the element cannot be parsed correctly.
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        String namespace = namespaceURI;
        if (namespaceURI != null
                && (namespaceURI.equalsIgnoreCase(CoreExtensionFactory.ID))) {
            namespace = null; // Remove the 'org.apache.felix.ipojo' namespace
        }
        //System.out.println("XML.e/// " + namespace + ":" + localName);
        XMLElement elem = new XMLElement(localName, namespace);
        for (int i = 0; i < atts.getLength(); i++) {
            String name = (String) atts.getLocalName(i);
            String ns = (String) atts.getURI(i);
            String value = (String) atts.getValue(i);
            //System.out.println("XML.a/// " + ns + ":" + name + "=" + value);
            XMLAttribute att = new XMLAttribute(name, ns, value);
            elem.addAttribute(att);
        }

        addElement(elem);
        //System.out.println("XML/// good");
    }

    /**
     * Start a prefix mapping.
     * @param prefix : prefix.
     * @param uri : uri.
     * @throws SAXException : can never occurs.
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // NOTHING TO DO

    }

    /**
     * Add an element.
     * @param elem : the element to add
     */
    private void addElement(XMLElement elem) {
        for (int i = 0; (m_elements != null) && (i < m_elements.length); i++) {
            if (m_elements[i] == elem) {
                return;
            }
        }

        if (m_elements != null) {
        	XMLElement[] newElementsList = new XMLElement[m_elements.length + 1];
            System.arraycopy(m_elements, 0, newElementsList, 0, m_elements.length);
            newElementsList[m_elements.length] = elem;
            m_elements = newElementsList;
        } else {
            m_elements = new XMLElement[] { elem };
        }
    }

    /**
     * Remove an element.
     * @return : the removed element.
     */
    private XMLElement removeLastElement() {
        int idx = -1;
        idx = m_elements.length - 1;
        XMLElement last = m_elements[idx];
        if (idx >= 0) {
            if ((m_elements.length - 1) == 0) {
                // It is the last element of the list;
                m_elements = new XMLElement[0];
            } else {
                // Remove the last element of the list :
            	XMLElement[] newElementsList = new XMLElement[m_elements.length - 1];
                System.arraycopy(m_elements, 0, newElementsList, 0, idx);
                m_elements = newElementsList;
            }
        }
        return last;
    }


    /**
     * An error occurs during the XML-Schema checking.
     * This method propagates the error except if the error concerns
     * no XML-Schemas are used   (<code>cvc-elt.1</code>).
     * @param saxparseexception the checking error
     * @throws SAXException the propagated exception
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException saxparseexception) throws SAXException {
        if (saxparseexception.getMessage().indexOf("cvc-elt.1") != -1) {
            return; // Do not throw an exception when no schema defined.
        }
        throw saxparseexception;
    }


    /**
     * A fatal error occurs during the XML-Schema checking.
     * This method always propagates the error.
     * @param saxparseexception the checking error
     * @throws SAXException the propagated exception
     * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
     */
    public void fatalError(SAXParseException saxparseexception)
        throws SAXException {
        System.err.println("Fatal error during XML-Schema parsing : " + saxparseexception);
        throw saxparseexception;
    }

    /**
     * A warning was detected during the XML-Schema checking.
     * This method always propagate the warning message to
     * {@link System#out}.
     * @param saxparseexception the checking error
     * @throws SAXException nothing.
     * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
     */
    public void warning(SAXParseException saxparseexception)
        throws SAXException {
        System.err.println("Warning : an error was detected in the metadata file : " + saxparseexception);
        
    }
}