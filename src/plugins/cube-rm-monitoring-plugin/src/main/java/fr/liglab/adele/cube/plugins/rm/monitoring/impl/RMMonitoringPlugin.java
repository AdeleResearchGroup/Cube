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


package fr.liglab.adele.cube.plugins.rm.monitoring.impl;

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.RuntimeModel;
import fr.liglab.adele.cube.agent.RuntimeModelListener;
import fr.liglab.adele.cube.agent.defaults.RuntimeModelImpl;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Notification;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.plugins.AbstractPlugin;
import fr.liglab.adele.cube.plugins.PluginFactory;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 7:23 PM
 */
public class RMMonitoringPlugin extends AbstractPlugin {

    MonitorGUI gui;

    public RMMonitoringPlugin(CubeAgent agent, PluginFactory bundle, Properties properties) {
        super(agent, bundle, properties);

        gui = new MonitorGUI(agent);

        agent.getRuntimeModel().addListener(new RuntimeModelListener() {
            public void update(RuntimeModel rm, Notification notification) {
                if (notification.getNotificationType() == RuntimeModelListener.UPDATED_RUNTIMEMODEL) {
                    /*
                    Object instance = notification.getNewValue();
                    if (instance != null && instance instanceof ManagedElement) {
                         gui.addNode((ManagedElement)instance);
                    }
                    */
                    gui.updateGraph();
                }
            }
        });
    }

    public void run() {
        //System.out.println("---------------- RM Monitoring Plugin -----------------");
        gui.setVisible(true);
    }

    public void stop() {

    }

    public void destroy() {

    }

    public ConstraintResolver getConstraintResolver(String name) {
        return null;
    }

    /**
     * Creates a new Managed Element Instance of the given name.
     *
     * @param element_name
     * @return
     */
    public ManagedElement newManagedElement(String element_name) {
        return null;
    }

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException {
        return null;
    }
}
