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


package fr.liglab.adele.cube.platform;

import fr.liglab.adele.cube.agent.Communicator;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.AgentConfig;
import fr.liglab.adele.cube.agent.CubeAgentException;
import fr.liglab.adele.cube.agent.defaults.CubeAgentImpl;
import fr.liglab.adele.cube.plugins.PluginFactory;
import fr.liglab.adele.cube.util.parser.AgentConfigParser;
import fr.liglab.adele.cube.util.parser.ParseException;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation class of the CubePlatform OSGi service.
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:45 PM
 */
@Component
@Provides
@Instantiate
public class CubePlatformImpl implements CubePlatform {

    private BundleContext bundleContext;

    /**
     * <CubeAgentURI, CubeAgent>
     */
    private Map<String, CubeAgent> cubeAgents;
    {
        cubeAgents = new HashMap<String, CubeAgent>();
    }

    /**
     * Plugin Factories
     */
    @Requires(specification="fr.liglab.adele.cube.plugins.PluginFactory")
    private List<PluginFactory> pluginFactories;

    @Requires(specification = "fr.liglab.adele.cube.agent.Communicator")
    private List<Communicator> communicators;

    /**
     * Constructor.
     * @param btx Bundle Context injected automatically by iPOJO
     */
    public CubePlatformImpl(BundleContext btx) {
        this.bundleContext = btx;
        pluginFactories = new ArrayList<PluginFactory>();
    }

    /**
     * Called when the Cube Platform starts on the local OSGi Platform.
     */
    @Validate
    public void starting() {
        String msg = "\n";

        msg += "\n    _______              ";
        msg += "\n   /|      |             ";
        msg += "\n  | | CUBE |...Version:  ";
        msg += "\n  | |______|   " + getVersion();
        msg += "\n  |/______/              ";
        msg += "\n";
        System.out.println(msg);
    }

    /**
     * Called when the Cube Platform stops on the local OSGi Platform.
     * All the created Cube Agents will be destroyed.
     */
    @Invalidate
    public void stopping() {
        System.out.println(" ");
        System.out.println("[INFO] ... Stopping the CUBE Platform");

        // Stopping and destroying all the created cube agents.
        for (String a : this.cubeAgents.keySet()) {
            stopCubeAgent(a);
            destroyCubeAgent(a);
        }

        System.out.println("[INFO] ... Bye!");
        System.out.println(" ");
    }

    /**
     * Create Cube Agent.
     *
     * @param configUrl CubeAgent Config file url
     * @return Cube Agent URI
     */
    public String createCubeAgent(String configUrl) throws CubeAgentException {

        AgentConfig config = null;
        try {
            config = AgentConfigParser.parse(this, new URL(configUrl));
        } catch (ParseException e) {
            throw new CubeAgentException("Error while parsing the Cube Agent Configuration file!");
        } catch (MalformedURLException e) {
            throw new CubeAgentException("Unknown configuration file!");
        }
        if (config == null) {
            throw new CubeAgentException("No configuration was given to create the Cube Agent!");
        }
        if (config.getHost() == null || config.getHost().length() == 0) {
            throw new CubeAgentException("You should provide a correct 'host' in the Cube Agent's configuration!");
        }
        if (config.getPort() < 0) {
            throw new CubeAgentException("You should provide a correct 'port' in the Cube Agent's configuration!");
        }
        // verify that no other existing agent has the same configuration (host+port)
        for (String key: cubeAgents.keySet()) {
            if (cubeAgents.get(key).getConfig().getHost().equalsIgnoreCase(config.getHost()) &&
                    cubeAgents.get(key).getConfig().getPort() == config.getPort()) {
                throw new CubeAgentException("The agent with the configuration ("+config.getHost()+","+config.getPort()+") already exists!");
            }
        }
        // all is ok, we create the new agent
        CubeAgent ci = new CubeAgentImpl(this, config);
        cubeAgents.put(ci.getUri(), ci);
        ci.run();
        return ci.getUri().toString();

    }

    /**
     * Destroy Cube Agent.
     *
     * @param agentURI
     */
    public void destroyCubeAgent(String agentURI) {
        CubeAgent agent = getCubeAgent(agentURI);
        if (agent != null)
            agent.destroy();
    }

    /**
     * Start Cube Agent
     *
     * @param agentURI
     */
    public void runCubeAgent(String agentURI) {
        CubeAgent agent = getCubeAgent(agentURI);
        if (agent != null)
            agent.run();
    }

    /**
     * Stop Cube Agent
     *
     * @param agentURI
     */
    public void stopCubeAgent(String agentURI) {
        CubeAgent agent = getCubeAgent(agentURI);
        if (agent != null)
            agent.stop();
    }

    /**
     * Get Cube Agents in this local OSGi Platform.
     *
     * @return List of Cube Agent' URIs
     */
    public List<String> getCubeAgents() {
        List<String> ids = new ArrayList<String>();
        for (String key: cubeAgents.keySet()) {
            ids.add(key);
        }
        return ids;
    }

    /**
     * Gets Cube Agents in this local OSGi Platform.
     *
     * @return List of Cube Agent' local Ids
     */
    public List<String> getCubeAgentLocalIds() {
        List<String> ids = new ArrayList<String>();
        for (String key: cubeAgents.keySet()) {
            ids.add(cubeAgents.get(key).getLocalId());
        }
        return ids;
    }

    /**
     * Get the Cube Agent of the given URI.
     *
     * @param uri
     * @return
     */
    public CubeAgent getCubeAgent(String uri) {
        return this.cubeAgents.get(uri);
    }

    /**
     * Gets the Cube Agent of the given local Id.
     *
     * @param localId
     * @return
     */
    public CubeAgent getCubeAgentByLocalId(String localId) {
        if (localId != null) {
            for (String key: cubeAgents.keySet()) {
                if (cubeAgents.get(key).getLocalId().equalsIgnoreCase(localId)) {
                    return cubeAgents.get(key);
                }
            }
        }
        return null;
    }

    /**
     * Get the Plugin Bundle has the given id.
     * Its OSGi bundle should be already deployed.
     *
     * @param id
     * @return
     */
    public PluginFactory getPluginFactory(String id) {
        if (id != null && id.length() > 0) {
            for (PluginFactory eb : this.pluginFactories) {
                if (eb.getNamespace().equalsIgnoreCase(id))
                    return eb;
            }
        }
        return null;
    }

    /**
     * Gets the Communicator having the given id.
     *
     * @param id
     * @return
     */
    public Communicator getCommunicator(String id) {
        if (id != null && id.length() > 0) {
            for (Communicator c : this.communicators) {
                if (c.getName().equalsIgnoreCase(id))
                    return c;
            }
        }
        return null;
    }

    /**
     * Get OSGi Bundle Context.
     *
     * @return
     */
    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    /**
     * Get Cube Platform Version.
     *
     * @return Version number
     */
    public String getVersion() {
        return CUBE_VERSION;
    }
}
