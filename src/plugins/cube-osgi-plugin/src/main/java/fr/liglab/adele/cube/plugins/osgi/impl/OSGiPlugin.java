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


package fr.liglab.adele.cube.plugins.osgi.impl;

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.plugins.AbstractPlugin;
import fr.liglab.adele.cube.plugins.PluginFactory;
import fr.liglab.adele.cube.plugins.core.CorePluginFactory;
import fr.liglab.adele.cube.plugins.core.model.Node;
import org.osgi.framework.BundleContext;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 11:51 AM
 */
public class OSGiPlugin extends AbstractPlugin {

    private static final String CUBE_NODE_TYPE = "cube.node.type";
    private static final String CUBE_NODE_ID = "cube.node.id";
    private static int index = 1;

    public OSGiPlugin(CubeAgent agent, PluginFactory bundle, Properties properties) {
        super(agent, bundle, properties);
    }

    public void run() {

        // initialize type and id from the cube agent's configuration file.
        Object type = getProperties().get("type");
        Object id = getProperties().get("id");


        // if not found, initialize them from OSGi configuration properties
        BundleContext btx = getCubeAgent().getPlatform().getBundleContext();
        if (type == null)
            type = btx.getProperty(CUBE_NODE_TYPE);
        if (id == null)
            id = btx.getProperty(CUBE_NODE_ID);

        // else, take default values.

        if (type == null) type = "OSGi";
        if (id == null) id = "OSGi-" + index++;

        Properties properties = new Properties();
        // TODO add type and id to properties!
        try {
            ManagedElement me = getCubeAgent().newManagedElement(CorePluginFactory.NAMESPACE, Node.NAME, properties);
            if (me != null && me instanceof Node) {
                ((Node)me).setNodeId(id.toString());
                ((Node)me).setNodeType(type.toString());
                getCubeAgent().getRuntimeModel().add(me);
                getCubeAgent().getRuntimeModel().refresh();
            }
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }

    }

    public void stop() {

    }

    public void destroy() {

    }

    public ConstraintResolver getConstraintResolver(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates a new Managed Element Instance of the given name;
     *
     * @param element_name
     * @return
     */
    public ManagedElement newManagedElement(String element_name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
