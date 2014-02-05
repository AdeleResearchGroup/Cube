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


package fr.liglab.adele.cube.extensions.loadbalancing.impl;

import fr.liglab.adele.cube.__autonomicmanager.ConstraintResolver;
import fr.liglab.adele.cube.__autonomicmanager.CubeAgent;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.extensions.AbstractPlugin;
import fr.liglab.adele.cube.extensions.PluginFactory;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 5/30/13
 * Time: 11:52 AM
 */
public class LoadbalancingPlugin extends AbstractPlugin {

    LoadBalancer loadbalancer = null;

    public LoadbalancingPlugin(CubeAgent agent, PluginFactory bundle, Properties properties) {
        super(agent, bundle, properties);

        int interval = 5000;
        Object _interval = getProperties().get("interval");
        if (_interval != null) {
            interval = new Integer(_interval.toString());
        }

        loadbalancer = new LoadBalancer(agent, interval);


    }

    public void run() {
        //System.out.println("\n\n\nSTARTING LOADBALANCING PLUGIN...\n\n\n");
        loadbalancer.start();
    }

    public void stop() {
        loadbalancer.stop();
    }

    public void destroy() {
        loadbalancer.destroy();
    }

    public ConstraintResolver getConstraintResolver(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates a new Managed ElementDescription Instance of the given name;
     *
     * @param element_name
     * @return
     */
    public ManagedElement newManagedElement(String element_name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates a new Managed ElementDescription Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
