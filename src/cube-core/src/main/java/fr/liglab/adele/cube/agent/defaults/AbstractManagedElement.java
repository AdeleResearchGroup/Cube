/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
 * LIG Laboratory (http://www.liglab.fr)
 * 
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


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.*;
import fr.liglab.adele.cube.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/25/13
 * Time: 5:34 PM
 */
public abstract class AbstractManagedElement extends Observable implements ManagedElement, Cloneable, Serializable {

    /**
     * Current state of the Managed Element.
     */
    private int state = UNMANAGED;

    /**
     * Concept name.
     */
    private String name = null;

    /**
     * Concept namespace.
     */
    private String namespace = null;

    /**
     * Instance uuid.
     */
    private String uuid = null;

    /**
     * Hosted Cube Agent URI.
     */
    private String cubeAgent = null;

    /**
     * Instance properties.
     */
    private List<Property> properties = new ArrayList<Property>();

    /**
     * Instance references.
     */
    private List<Reference> references = new ArrayList<Reference>();

    /**
     * Index used to create the URIs.
     */
    private static int index=0;

    /**
     * Is the current element instance in a currently holding resolution process?
     */
    private boolean inResolution = false;

    /**
     * Gets the instance current state.
     * @return
     */
    public int getState() {
        return this.state;
    }

    /**
     * Gets the instance current state as String.
     *
     * @return
     */
    public String getStateAsString() {
        switch (this.state) {
            case UNMANAGED:
                return "UNMANAGED";
            case UNCHECKED:
                return "UNCHECKED";
            case VALID:
                return "VALIRD";
        }
        return "";
    }

    public AbstractManagedElement(CubeAgent agent) {
        setCubeAgent(agent.getUri());
        this.uuid = Utils.GenerateUUID();
    }

    public AbstractManagedElement(CubeAgent agent, Properties properties) throws PropertyExistException, InvalidNameException{
        this.uuid = Utils.GenerateUUID();
        if (properties != null) {
            for (Object key : properties.keySet()) {
                addProperty(key.toString(), properties.get(key).toString());
            }
        }
        if (this.cubeAgent == null)
            setCubeAgent(agent.getUri());
    }

    int updateState(int newState) {
        int oldState = this.state;
        this.state = newState;
        if (newState == VALID) {
            //updateProperties();
            //notifyObservers(new Notification(ManagedElementListener.CHANGED_STATE));
        }
        return oldState;
    }

    public abstract String getName();

    public abstract String getNamespace();

    public String getCubeAgent() {
        return this.cubeAgent;
    }

    public void setCubeAgent(String uri) {
        this.cubeAgent = uri;
    }

    public String getUUID() {
        return this.uuid;
    }

    /**
     * Gets the URI of the current Managed Element.
     * @return
     */
    public String getUri() {
        return getCubeAgent() + "/" + getName().toLowerCase() + "/" + getUUID();
    }

    /**
     * Get Managed Element Properties
     * @return
     */
    public List<Property> getProperties() {
        return this.properties;
    }

    /**
     * Get Property
     * @param name
     * @return NULL if name is null or no property found with the name 'name'; the found property other else.
     */
    private Property _getProperty(String name) {

        if (name != null && name.length() > 0) {
            for (Property p : this.getProperties()) {
                if (p.getName() != null && p.getName().equalsIgnoreCase(name.toLowerCase())) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Get Property value
     * @param name
     * @return NULL if 'name' is null or no property found with 'name' name; the value of the found property other else
     */
    public String getProperty(String name) {
        if (name != null && name.length() > 0) {
            Property p = _getProperty(name);
            if (p != null) {
                return p.getValue();
            }
        }
        return null;
    }

    /**
     * Checks if the Managed Element has the given property.
     *
     * @param name Property name
     * @return TRUE if it has the provided property.
     */
    public boolean hasProperty(String name) {
        if (name != null) {
            for(Property p : this.properties) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Adding property
     * @param name
     * @param value
     * @return TRUE if the property added; FALSE other else.
     * @throws PropertyExistException
     */
    public boolean addProperty(String name, String value) throws PropertyExistException, InvalidNameException {
         if (name == null || name.length() == 0) {
             return false;
         }
        if (_getProperty(name.toLowerCase()) != null) {
            throw new PropertyExistException("You are trying to add an existing property '"+name+"'!");
        }
        return this.properties.add(new Property(name.toLowerCase(), value));
    }

    /**
     * Update property
     * @param name
     * @param newValue
     * @return NULL is name is null; or Old value if a property exist with 'name' name.
     * @throws PropertyNotExistException
     */
    public String updateProperty(String name, String newValue) throws PropertyNotExistException {
        if (name == null || name.length() <= 0) {
            if (_getProperty(name) == null)
                throw new PropertyNotExistException("You are trying to update unexistant property '" + name + "'!");
            Property p = _getProperty(name);
            String oldValue = p.getValue();
            p.setValue(newValue);
            return oldValue;
        }
        return null;
    }

    int validate() {
        int old = updateState(VALID);
        setChanged();
        notifyObservers();
        return old;
    }

    /**
     * Get Managed Element References
     * @return
     */
    public List<Reference> getReferences() {
        return this.references;
    }

    /**
     * Get Regerence
     * @param name
     * @return
     */
    public Reference getReference(String name) {
        if (name != null && name.length() > 0) {
            for (Reference r : this.getReferences()) {
                if (r.getName() != null && r.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the Managed Element has the given reference.
     *
     * @param name Reference name
     * @return TRUE if it has the provided reference name.
     */
    public boolean hasReference(String name) {
        if (name != null) {
            for (Reference r : getReferences()) {
                if (r.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Reference addReference(String name, boolean onlyOne) throws InvalidNameException {
        Reference r = getReference(name);
        if (getReference(name) == null) {
            r = new Reference(name, onlyOne);
            this.references.add(r);
        }
        return r;
    }

    /**
     * Gets a textual description of the element.
     *
     * @return
     */
    public String getTextualDescription() {
        String msg = "";
        msg += "\n + " + this.getUri();
        if (this.getProperties().size() > 0) {
            msg += "\n    | PROPERTIES";
            for (Property p : this.getProperties()) {
                msg += "\n    |   " + p.getName() + "=" + p.getValue();
            }
        }
        if (this.getReferences().size() > 0) {
            msg += "\n    | REFERENCES";
            for (Reference r : this.getReferences()) {
                msg += "\n    |   " + r.getName() + ":";
                for (String s : r.getReferencedElements()) {
                    msg += "\n    |     " + s;
                }
            }
        }
        return msg;
    }

    public String getHTMLDescription() {
        String msg = "<html>";
        msg += "<br/> <b>" + this.getUUID() + "</b><br/><hr/>";
        msg += "<p><b>MANAGED ELEMENT</b><br/><ul>";
        msg += "<li> " + this.getName() + "</li>";
        msg += "</ul>";
        if (this.getProperties().size() > 0) {
            msg += "<p><b>PROPERTIES</b><br/><ul>";
            for (Property p : this.getProperties()) {
                msg += "<li> " + p.getName() + "=" + p.getValue() + "</li>";
            }
            msg += "</ul></p>";
        }
        if (this.getReferences().size() > 0) {
            msg += "<p><b>REFERENCES</b><ul>";
            for (Reference r : this.getReferences()) {
                msg += "<li>" + r.getName() + ":<br/>";
                msg += "<ul>";
                for (String s : r.getReferencedElements()) {
                    msg += "<li>" + s + "</li>";
                }
                msg += "</ul>";
                msg += "</li>";
            }
            msg += "</ul></p>";
        }
        return msg + "</html>";
    }

    public synchronized  boolean removeReferencedElement(String ref) {
        boolean changed = false;
        if (ref != null) {
            List<Reference> toBeRemoved = new ArrayList<Reference>();
            for (Reference r : this.references) {
                if (r.removeReferencedElement(ref) == true) {
                    changed = true;
                    if (r.getReferencedElements().size() == 0) {
                        toBeRemoved.add(r);
                    }
                }
            }
            for (Reference r : toBeRemoved) {
                this.references.remove(r);
            }
            if (changed) {
                if (getState() == VALID)
                    updateState(ManagedElement.UNCHECKED);
            }
        }
        return changed;
    }

    public synchronized boolean removeEmptyProperties() {
        List<Property> toBeRemoved = new ArrayList<Property>();
        for (Property p : getProperties()) {
            if (p.getValue() == null) {
                toBeRemoved.add(p);
            }

        }
        boolean changed = false;
        for (Property p : toBeRemoved) {
            this.properties.remove(p);
            changed = true;
        }
        return changed;
    }

    public boolean removeEmptyReferences() {
        List<Reference> toBeRemoved = new ArrayList<Reference>();
        for (Reference r : getReferences()) {
            if (r.getReferencedElements().size() == 0) {
                toBeRemoved.add(r);
            }

        }
        boolean changed = false;
        for (Reference r : toBeRemoved) {
            this.references.remove(r);
            changed = true;
        }
        return changed;
    }

    public boolean isSimilar(ManagedElement managedElement) {
        if (getNamespace() != null && getNamespace().equalsIgnoreCase(managedElement.getNamespace()) && getName() != null
                && getName().equalsIgnoreCase(managedElement.getName())) {
            if (getProperties().size() != managedElement.getProperties().size()) {
                return false;
            }
            for (Property p : getProperties()) {
                if (managedElement.hasProperty(p.getName()) == false) {
                    return false;
                }
                if (!managedElement.getProperty(p.getName()).equalsIgnoreCase(p.getValue())) {
                    return false;
                }
            }
            if (getReferences().size() != managedElement.getReferences().size()) {
                return false;
            }
            for (Reference r : getReferences()) {
                if (managedElement.hasReference(r.getName()) == false) {
                    return false;
                }
                if (r.getReferencedElements().size() != managedElement.getReference(r.getName()).getReferencedElements().size()) {
                    return false;
                }
                for (String ref : r.getReferencedElements()) {
                    if (!managedElement.getReference(r.getName()).hasReferencedElement(ref)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean isInResolution() {
        return inResolution;
    }

    public void setInResolution(boolean inResolution) {
        this.inResolution = inResolution;
    }
}
