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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

/**
 * An element represents an XML ElementDescription.
 * It contains a name, a namepace, Attribute objects
 * and sub-elements. This class is used to parse Archtype.
 * @author 
 */

public class XMLElement {

    /**
     * The name of the element.
     */
    private String name;

    /**
     * The namespace of the element.
     */
    private String nameSpace;

    /**
     * The map of attributes of the element.
     * The map key is the qualified name of the attribute (<code>ns:name</code>)
     * The value is the attribute object.
     */
    private Map attributes = new HashMap();

    /**
     * The map of the sub-element of the element.
     * The map key is the element qualified name (ns:name).
     * The value is the array of element of this name.
     */
    private Map elements = new HashMap();

    /**
     * Creates an XMLElement.
     * @param name the name of the element
     * @param ns the namespace of the element
     */
    public XMLElement(String name, String ns) {
        this.name = name.toLowerCase();
        if (ns != null && ns.length() > 0) {
            this.nameSpace = ns.toLowerCase();
        }
    }

    /**
     * Gets sub-elements.
     * If no sub-elements, an empty array is returned.
     * @return the sub elements
     */
    public XMLElement[] getElements() {
        Collection col = elements.values();
        Iterator it = col.iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            XMLElement[] v = (XMLElement[]) it.next();
            for (int i = 0; i < v.length; i++) {
                list.add(v[i]);
            }
        }
        return (XMLElement[]) list.toArray(new XMLElement[list.size()]);
    }

    /**
     * Gets element attributes.
     * If no attributes, an empty array is returned.
     * @return the attributes
     */
    public XMLAttribute[] getAttributes() {
        return (XMLAttribute[]) attributes.values().toArray(new XMLAttribute[0]);
    }

    /**
     * Gets element name.
     * @return the name of the element
     */
    public String getName() {
        return name;
    }

    /**
     * Gets element namespace.
     * @return the namespace of the element
     */
    public String getNameSpace() {
    	if(this.nameSpace == null) {
    		return CoreExtensionFactory.NAMESPACE;
    	}
        return nameSpace;
    }

    /**
     * Returns the value of the attribute given in parameter.
     * @param name the name of the searched attribute
     * @return the value of the attribute given in parameter,
     * <code>null</code> if the attribute does not exist
     */
    public String getAttribute(String name) {
        name = name.toLowerCase();
        XMLAttribute att = (XMLAttribute) attributes.get(name);
        if (att == null) {
            return null;
        } else {
            return att.getValue();
        }
    }

    /**
     * Returns the value of the attribute "name" of the namespace "ns".
     * @param name the name of the attribute to find
     * @param ns the namespace of the attribute to find
     * @return the String value of the attribute, or 
     * <code>null</code> if the attribute is not found.
     */
    public String getAttribute(String name, String ns) {
        name = ns.toLowerCase() + ":" + name.toLowerCase();
        return getAttribute(name);
    }
    
    /**
     * Gets the qualified name of the current element.
     * @return the qualified name of the current element.
     */
    private String getQualifiedName() {
        if (nameSpace == null) {
            return name;
        } else {
            return nameSpace + ":" + name;
        }
    }

    /**
     * Adds a sub-element.
     * @param elem the element to add
     */
    public void addElement(XMLElement elem) {
        XMLElement[] array = (XMLElement[]) elements.get(elem.getQualifiedName());
        if (array == null) {
            elements.put(elem.getQualifiedName(), new XMLElement[] {elem});
        } else {
        	XMLElement[] newElementsList = new XMLElement[array.length + 1];
            System.arraycopy(array, 0, newElementsList, 0, array.length);
            newElementsList[array.length] = elem;
            elements.put(elem.getQualifiedName(), newElementsList);
        }
    }

    /**
     * Removes a sub-element.
     * @param elem the element to remove
     */
    public void removeElement(XMLElement elem) {
    	XMLElement[] array = (XMLElement[]) elements.get(elem.getQualifiedName());
        if (array == null) {
            return;
        } else {
            int idx = -1;
            for (int i = 0; i < array.length; i++) {
                if (array[i] == elem) {
                    idx = i;
                    break;
                }
            }

            if (idx >= 0) {
                if ((array.length - 1) == 0) {
                    elements.remove(elem.getQualifiedName());
                } else {
                	XMLElement[] newElementsList = new XMLElement[array.length - 1];
                    System.arraycopy(array, 0, newElementsList, 0, idx);
                    if (idx < newElementsList.length) {
                        System.arraycopy(array, idx + 1, newElementsList, idx, newElementsList.length - idx);
                    }
                    elements.put(elem.getQualifiedName(), newElementsList); // Update the stored list.
                }
            }
        }
    }

    /**
     * Adds a attribute.
     * @param att the attribute to add
     */
    public void addAttribute(XMLAttribute att) {
    	if (att != null) {
    		if (att.getName() != null) {
		        String name = att.getName().toLowerCase();
		        if (att.getNameSpace() != null) {
		            name = att.getNameSpace().toLowerCase() + ":" + name;
		        }
		        attributes.put(name, att);
    		}
    	}
    }

    /**
     * Removes an attribute.
     * @param att the attribute to remove
     */
    public void removeAttribute(XMLAttribute att) {
        String name = att.getName();
        if (att.getNameSpace() != null) {
            name = att.getNameSpace() + ":" + name;
        }
        attributes.remove(name);
    }

    /**
     * Gets the elements array of the element type given in parameter. 
     * This method looks for an empty namespace.
     * @param name the type of the element to find (element name)
     * @return the resulting element array (<code>null</code> if the search failed)
     */
    public XMLElement[] getElements(String name) {
    	XMLElement[] elems = (XMLElement[]) elements.get(name.toLowerCase());
        return elems;
    }

    /**
     * Gets the elements array of the element type given in parameter.
     * @param name the type of the element to find (element name)
     * @param ns the namespace of the element
     * @return the resulting element array (<code>null</code> if the search failed)
     */
    public XMLElement[] getElements(String name, String ns) {
        if (ns == null || ns.length() == 0) {
            return getElements(name);
        }
        name = ns + ":" + name;
        return getElements(name);
    }

    /**
     * Does the element contain a sub-element of the type given in parameter.
     * @param name the type of the element to check.
     * @return <code>true</code> if the element contains an element of the type "name"
     */
    public boolean containsElement(String name) {
        return elements.containsKey(name.toLowerCase());
    }

    /**
     * Does the element contain a sub-element of the type given in parameter. 
     * @param name the type of the element to check.
     * @param ns the namespace of the element to check.
     * @return <code>true</code> if the element contains an element of the type "name"
     */
    public boolean containsElement(String name, String ns) {
        if (ns != null && ns.length() != 0) {
            name = ns + ":" + name;
        }
        return containsElement(name);
    }

    /**
     * Does the element contain an attribute of the name given in parameter.
     * @param name the name of the element
     * @return <code>true</code> if the element contains an attribute of the type "name"
     */
    public boolean containsAttribute(String name) {
        return attributes.containsKey(name.toLowerCase());
    }
    
    /**
     * Gets the XML form of this element.
     * @return the XML snippet representing this element.
     */
    public String toXMLString() {
        return toXMLString(0);
    }

    /**
     * Internal method to get XML form of an element.
     * @param indent the indentation to used.
     * @return the XML snippet representing this element.
     */
    private String toXMLString(int indent) {
        StringBuffer xml = new StringBuffer();

        StringBuffer tabs = new StringBuffer();
        for (int j = 0; j < indent; j++) {
            tabs.append("\t");
        }

        xml.append(tabs);
        if (nameSpace == null) {
            xml.append("<" + name);
        } else {
            xml.append("<" + nameSpace + ":" + name);
        }
        
        Set keys = attributes.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
        	XMLAttribute current = (XMLAttribute) attributes.get(it.next());
            if (current.getNameSpace() == null) {
                xml.append(" " + current.getName() + "=\"" + current.getValue() + "\"");
            } else {
                xml.append(" " + current.getNameSpace() + ":" + current.getName() + "=\"" + current.getValue() + "\"");
            }
        }


        if (elements.size() == 0) {
            xml.append("/>");
            return xml.toString();
        } else {
            xml.append(">");
            keys = elements.keySet();
            it = keys.iterator();
            while (it.hasNext()) {
            	XMLElement[] e = (XMLElement[]) elements.get(it.next());
                for (int i = 0; i < e.length; i++) {
                    xml.append("\n");
                    xml.append(e[i].toXMLString(indent + 1));
                }
            }
            xml.append("\n" + tabs + "</" + name + ">");
            return xml.toString();
        }
    }

    /**
     * To String method.
     * @return the String form of this element. <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
     * @see Object#toString()
     */
    public String toString() {
        return toString(0);
    }

    /**
     * Internal method to compute the toString method.
     * @param indent the indentation to use.
     * @return the String form of this element.
     */
    private String toString(int indent) {
        StringBuffer xml = new StringBuffer();

        StringBuffer tabs = new StringBuffer();
        for (int j = 0; j < indent; j++) {
            tabs.append("\t");
        }

        xml.append(tabs);
        if (nameSpace == null) {
            xml.append(name);
        } else {
            xml.append(nameSpace + ":" + name);
        }

        Set keys = attributes.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
        	XMLAttribute current = (XMLAttribute) attributes.get(it.next());
            if (current.getNameSpace() == null) {
                xml.append(" " + current.getName() + "=\"" + current.getValue() + "\"");
            } else {
                xml.append(" " + current.getNameSpace() + ":" + current.getName() + "=\"" + current.getValue() + "\"");
            }
        }

        if (elements.size() == 0) {
            return xml.toString();
        } else {
            keys = elements.keySet();
            it = keys.iterator();
            while (it.hasNext()) {
            	XMLElement[] e = (XMLElement[]) elements.get(it.next());
                for (int i = 0; i < e.length; i++) {
                    xml.append("\n");
                    xml.append(e[i].toString(indent + 1));
                }
            }
            return xml.toString();
        }
    }

}