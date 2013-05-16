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


package fr.liglab.adele.cube.plugins.core.model;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.metamodel.PropertyNotExistException;
import fr.liglab.adele.cube.metamodel.Reference;
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;
import fr.liglab.adele.cube.plugins.core.CorePluginFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 1:00 PM
 */
public class Component extends AbstractManagedElement {

    public static final String NAME = "Component";

    public static final String CORE_COMPONENT_ID = "id";
    public static final String CORE_COMPONENT_TYPE = "type";
    public static final String CORE_COMPONENT_NODE = "node";
    public static final String CORE_COMPONENT_INPUTS = "inputs";
    public static final String CORE_COMPONENT_OUTPUTS = "outputs";

    public Component(CubeAgent agent) {
        super(agent);
    }

    public Component(CubeAgent agent, Properties properties) throws PropertyExistException, InvalidNameException {
        super(agent, properties);
        //agent.getRuntimeModelController().getLocalElement()
    }
    /**
     * Sets the Component's local identifier.
     *
     * @param component_identifier
     */
    public void setComponentId(String component_identifier) {
        try {
            if (this.getProperty(CORE_COMPONENT_ID) == null)
                this.addProperty(CORE_COMPONENT_ID, component_identifier);
            else
                this.updateProperty(CORE_COMPONENT_ID, component_identifier);
        } catch (PropertyNotExistException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Component's local Id
     * @return
     */
    public String getComponentId() {
        return this.getProperty(CORE_COMPONENT_ID);
    }

    /**
     * Sets the Node's local identifier.
     *
     * @param component_type
     */
    public void setComponentType(String component_type) {
        try {
            if (this.getProperty(CORE_COMPONENT_TYPE) == null)
                this.addProperty(CORE_COMPONENT_TYPE, component_type);
            else
                this.updateProperty(CORE_COMPONENT_TYPE, component_type);
        } catch (PropertyNotExistException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Node's type
     * @return
     */
    public String getComponentType() {
        return this.getProperty(CORE_COMPONENT_TYPE);
    }

    public void setNode(String node_url) {
        Reference r = null;
        try {
            r = this.addReference(CORE_COMPONENT_NODE, true);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        r.addReferencedElement(node_url);
    }

    public String getNode() {
        Reference r = getReference(CORE_COMPONENT_NODE);
        if (r != null && r.getReferencedElements().size() > 0) {
            return r.getReferencedElements().get(0);
        }
        return null;
    }

    /**
     * Add Input Component
     * @param compURI
     * @return
     */
    public boolean addInputComponent(String compURI) {
        Reference r = null;
        try {
            r = addReference(CORE_COMPONENT_INPUTS, false);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        return r.addReferencedElement(compURI);
    }

    /**
     * Get input Components
     * @return
     */
    public List<String> getInputComponents() {
        Reference r = this.getReference(CORE_COMPONENT_INPUTS);
        if (r != null) {
            return r.getReferencedElements();
        }
        return new ArrayList<String>();
    }


    /**
     * Add Input Component
     * @param compURI
     * @return
     */
    public boolean addOutputComponent(String compURI) {
        Reference r = null;
        try {
            r = addReference(CORE_COMPONENT_OUTPUTS, false);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        return r.addReferencedElement(compURI);
    }

    /**
     * Get input Components
     * @return
     */
    public List<String> getOutputComponents() {
        Reference r = this.getReference(CORE_COMPONENT_OUTPUTS);
        if (r != null) {
            return r.getReferencedElements();
        }
        return new ArrayList<String>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getNamespace() {
        return CorePluginFactory.NAMESPACE;
    }
}
