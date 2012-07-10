/*
 * Copyright 2011-2012 Adele Research Group (http://adele.imag.fr/) 
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

import java.util.List;

import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.agent.AgentConfig;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ICommunicator;
import fr.liglab.adele.cube.agent.IResolverFactory;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.id.CubeAgentID;

/**
 * Cube Platform OSGi Service Interface
 * 
 * @author debbabi
 *
 */
public interface ICubePlatform {
	
	/**
	 * Create Cube Agent.
	 * @param configUrl CubeAgent Config file url
	 * @return Cube Agent ID
	 */
	public CubeAgentID createCubeAgent(String configUrl) throws Exception;
	/**
	 * Create Cube Agent.
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public CubeAgentID createCubeAgent(AgentConfig configuration) throws Exception;
	/**
	 * Create Cube Agent.
	 * @param archetypeUrl
	 * @return Cube Agent ID
	 */
	public CubeAgentID createCubeAgent(String archetypeUrl, String configUrl) throws Exception;
	/**
	 * Create Cube Agent.
	 * @param archetypeUrl
	 * @return Cube Agent ID
	 */
	public CubeAgentID createCubeAgent(String archetypeUrl, String host, int port) throws Exception;
	/**
	 * Stop Cube Agent.
	 * @param cubeAgentID
	 */
	public void stopCubeAgent(String cubeAgentID);
	/**
	 * Destroy Cube Agent.
	 * @param cubeAgentID
	 */
	public void destroyCubeAgent(String cubeAgentID);
	/**
	 * Get Cube Agents in this local OSGi Platform.	
	 * @return List of Cube agents' IDs
	 */
	public List<String> getCubeAgents();	
	/**
	 * Get the Cube Agent of the given ID.
	 * @param id
	 * @return
	 */
	public CubeAgent getCubeAgent(String id);
	/**
	 * Get the Extensions' Factories.
	 * @return
	 */
	public List<IExtensionFactory> getExtensionFactories();
	/**
	 * Get the Extension Factory of the given id.
	 * @param id
	 * @return
	 */
	public IExtensionFactory getExtensionFactory(String id);
	/**
	 * Get the Extension Factory of the given id and version.
	 * @param id
	 * @param version
	 * @return
	 */
	public IExtensionFactory getExtensionFactory(String id, String version);
	/**
	 * Get Resolvers available on the local Platform.
	 * @return
	 */
	public List<IResolverFactory> getResolverFactories();
	/**
	 * Get Resolver Factory from the local Platform which has the given name.
	 * @param name
	 * @return
	 */
	public IResolverFactory getResolverFactory(String name);
	/**
	 * Get Communicators available on the local Platform.
	 * @return
	 */
	public List<ICommunicator> getCommunicators();
	/**
	 * Get Communicator from the local Platform which has the given name.
	 * @param name
	 * @return
	 */
	public ICommunicator getCommunicator(String name);
	
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
