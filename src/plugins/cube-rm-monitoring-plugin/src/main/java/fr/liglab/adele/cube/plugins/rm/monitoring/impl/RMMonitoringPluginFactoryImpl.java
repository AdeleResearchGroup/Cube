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

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.plugins.Plugin;
import fr.liglab.adele.cube.plugins.rm.monitoring.RMMonitoringPluginFactory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 7:22 PM
 */
@Component
@Provides
@Instantiate
public class RMMonitoringPluginFactoryImpl implements RMMonitoringPluginFactory {

    public String getName() {
        return NAME;
    }

    public String getPrefix() {
        return PREFIX;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public Plugin getPluginInstance(CubeAgent agent, Properties properties) {
        return new RMMonitoringPlugin(agent, this, properties);
    }
}
