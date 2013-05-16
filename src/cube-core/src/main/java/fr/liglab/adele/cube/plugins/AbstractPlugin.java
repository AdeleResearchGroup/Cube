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


package fr.liglab.adele.cube.plugins;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:57 PM
 */
public abstract class AbstractPlugin implements Plugin {

    private String uri = null;

    private CubeAgent agent = null;
    private PluginFactory bundle = null;
    private Properties properties = new Properties();

    public AbstractPlugin(CubeAgent agent, PluginFactory bundle, Properties properties) {
        this.uri = agent.getUri() + "/plugin/" + bundle.getName();
        this.agent = agent;
        this.bundle = bundle;
        if (properties != null) {
            for (Object key: properties.keySet()) {
                this.properties.put(key, properties.get(key));
            }
        }
    }

    public String getUri() {
        return this.uri;
    }

    public CubeAgent getCubeAgent() {
        return this.agent;
    }

    public PluginFactory getPluginFactory() {
        return this.bundle;
    }

    public Properties getProperties(){
        return this.properties;
    }

    /**
     * Track the given managed element.
     * If its state, properties, or references change, the extension is notified.
     * @param me The Managed Element to track
     */
    protected void trackManagedElement(AbstractManagedElement me) {

    }

}
