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


package fr.liglab.adele.cube;

import fr.liglab.adele.cube.agent.Communicator;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.CubeAgentException;
import fr.liglab.adele.cube.plugins.PluginFactory;
import org.osgi.framework.BundleContext;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:42 PM
 */
public interface CubePlatform {

    public static final String CUBE_VERSION = "2.0";

    /**
     * Create Cube Agent.
     * @param configUrl CubeAgent Config file url
     * @return Cube Agent URI
     */
    public String createCubeAgent(String configUrl)  throws CubeAgentException;

    /**
     * Destroy Cube Agent.
     * @param cubeAgentURI
     */
    public void destroyCubeAgent(String cubeAgentURI);

    /**
     * Start Cube Agent
     * @param agentURI
     */
    public void runCubeAgent(String agentURI);

    /**
     * Stop Cube Agent
     * @param agentURI
     */
    public void stopCubeAgent(String agentURI);

    /**
     * Gets Cube Agents in this local OSGi Platform.
     * @return List of Cube Agent' URIs
     */
    public List<String> getCubeAgents();

    /**
     * Gets Cube Agents in this local OSGi Platform.
     * @return List of Cube Agent' local Ids
     */
    public List<String> getCubeAgentLocalIds();

    /**
     * Gets the Cube Agent of the given URI.
     * @param uri
     * @return
     */
    public CubeAgent getCubeAgent(String uri);

    /**
     * Gets the Cube Agent of the given local Id.
     * @param localId
     * @return
     */
    public CubeAgent getCubeAgentByLocalId(String localId);

    /**
     * Gets the Plugin Bundle has the given id.
     * Its OSGi bundle should be already deployed.
     * @param id
     * @return
     */
    public PluginFactory getPluginFactory(String id);

    /**
     * Gets the Communicator having the given id.
     * @param id
     * @return
     */
    public Communicator getCommunicator(String id);

    /**
     * Get OSGi Bundle Context.
     * @return
     */
    public BundleContext getBundleContext();
    /**
     * Get Cube Platform Version.
     * @return Version number
     */
    public String getVersion();
}
