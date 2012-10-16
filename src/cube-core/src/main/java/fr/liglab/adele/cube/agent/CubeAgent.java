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

package fr.liglab.adele.cube.agent;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.ICubePlatform;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.extensions.IExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.id.CubeAgentID;
import fr.liglab.adele.cube.util.parser.AgentConfigParser;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;

/**
 * Cube Agent.
 * 
 * It manages a part of the application using the provided archetype.
 *  
 * @author debbabi
 *
 */
public class CubeAgent {
	
	/**
	 * Cube Agent ID.
	 */
	private CubeAgentID id;	
	/**
	 * Cube Platform where this agent is running.
	 */
	private ICubePlatform cubePlatform;
	/**
	 * Local Cube Agent Configuration.
	 */
	private AgentConfig config;
	/**
	 * Archetype.
	 */
	private Archetype archetype;
	/**
	 * Runtime Model Container.
	 */
	private RuntimeModel runtimeModel;
	/**
	 * Communicator.
	 */
	private ICommunicator communicator;
	/**
	 * Resolver.
	 */
	private IResolver resolver;
	/**
	 * Extensions.
	 */
	private List<IExtension> extensions = new ArrayList<IExtension>();
	
	/**
	 * Cube Logger.
	 */
	private CubeLogger log;
	
	/**
	 * Constructor.
	 * @param cp
	 * @param configUrl
	 * @throws Exception
	 */
	public CubeAgent(ICubePlatform cp, String configUrl)  throws Exception {
		//System.out.println("[CubeAgent] creating cube agent...");
		this.cubePlatform = cp;
		this.runtimeModel = new RuntimeModel(this);		
		this.config = AgentConfigParser.parse(cp, new URL(configUrl));
		if (this.config.getArchetypeUrl() == null) {
			throw new Exception("No archetype Url was specified on the Cube Agent Configuration!");
		}
		initialize();		
		this.archetype = ArchetypeParser.parse(cp, new URL(this.config.getArchetypeUrl()));
		this.archetype.setCubeAgent(this);
		createExtensions();
	}
	
	/**
	 * Constructor.
	 * @param cp
	 * @param configuration
	 * @throws Exception
	 */
	public CubeAgent(ICubePlatform cp, AgentConfig configuration)  throws Exception {
		this.cubePlatform = cp;
		this.runtimeModel = new RuntimeModel(this);
		this.config = configuration;
		if (this.config.getArchetypeUrl() == null) {
			throw new Exception("No archetype Url was specified on the Cube Agent Configuration!");
		}
		initialize();
		this.archetype = ArchetypeParser.parse(cp, new URL(this.config.getArchetypeUrl()));
		this.archetype.setCubeAgent(this);
		createExtensions();
	}
	
	/**
	 * Constructor.
	 * @param cp
	 * @param configUrl
	 * @param archetypeUrl
	 * @throws Exception
	 * @Deprecated
	 */
	public CubeAgent(ICubePlatform cp, String configUrl, String archetypeUrl) throws Exception {
		this.cubePlatform = cp;
		this.runtimeModel = new RuntimeModel(this);
		this.config = AgentConfigParser.parse(cp, new URL(configUrl));
		initialize();		
		this.archetype = ArchetypeParser.parse(cp, new URL(archetypeUrl));
		this.archetype.setCubeAgent(this);
		createExtensions();
	}
	
	private void initialize() throws Exception {
		//System.out.println("[CubeAgent] initializing cube agent...");
		log = new CubeLogger(this.cubePlatform.getBundleContext(), CubeAgent.class.getName());
		this.id = new CubeAgentID(this.config.getHost(), this.config.getPort(), null);
		this.communicator = this.cubePlatform.getCommunicator(this.config.getCommunicatorName());
		if (this.communicator != null) {
			this.communicator.start(this);			
		} else {
			throw new Exception("No Communicator found!");
		}
		IResolverFactory rf = this.cubePlatform.getResolverFactory(this.config.getResolverName());
		if (rf != null) {
			this.resolver = rf.newResolver(this);
		}						
		if (this.resolver == null) {
			throw new Exception("No Resolver found!");
		}
		// TODO: debug
		// TODO: persistence				
	}
	
	private void createExtensions() {
		//System.out.println("[CubeAgent] creating cube agent extensions...");
		List<AgentExtensionConfig> extensions = this.config.getExtensions();
		if (extensions != null && extensions.size()>0) {
			//System.out.println("[CubeAgent] creating cube agent extensions... " + extensions.size() + " EXTENSION(S).");
			for (AgentExtensionConfig extension : extensions) {
				String id = extension.getId();
				String version = extension.getVersion();
				IExtensionFactory factory = getCubePlatform().getExtensionFactory(id, version);
				if (factory != null) {					
					IExtension extensionInstance = factory.newExtension(this, extension);
					this.extensions.add(extensionInstance);
					extensionInstance.start();
				} else {
					log.error("NO FACTORY FOUND FOR THE EXTENSION: " + id+":"+version);					
				}
			}
		} else {
			log.warning("No defined Extensions for this Cube Agent!");			
		}
	}
	
	/**
	 * Get Cube Agent Extension.
	 * @param id
	 * @param version
	 * @return
	 */
	public IExtension getExtension(String id, String version) {
		for (IExtension e : this.extensions) {
			if (e.getExtensionFactory().getExtensionId().equalsIgnoreCase(id) && e.getExtensionFactory().getExtensionVersion().equalsIgnoreCase(version)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Get Cube Agent Extension by its local ID
	 * @param localID
	 * @return
	 */
	public IExtension getExtensionByLocalID(String localID) {
		for (IExtension e : this.extensions) {
			if (e.getLocalId().equalsIgnoreCase(localID)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Get Cube Agent Extension.
	 * @param id
	 * @param version
	 * @return
	 */
	public IExtension getExtension(String id) {
		for (IExtension e : this.extensions) {
			if (e.getExtensionFactory().getExtensionId().equalsIgnoreCase(id)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Get the list of Extensions.
	 * 
	 * @return
	 */
	public List<IExtension> getExtensions() {
		return this.extensions;
	}
	
	/**
	 * Starting the agent.
	 */
	public void run() {
		System.out.println("[INFO] ... starting the CubeAgent: " + id.toString());
	}
	
	/**
	 * Stopping the agent.
	 */
	public void stop() {
		System.out.println("[INFO] ... destroying the CubeAgent: " + id.toString());
		for (IExtension ex : this.extensions) {
			ex.stop();
		}
		if (this.communicator != null) {
			this.communicator.stop();			
		}
		
	}	
	
	public CubeAgentID getId() {
		return id;
	}

	public ICubePlatform getCubePlatform() {
		return cubePlatform;
	}

	public AgentConfig getConfig() {
		return config;
	}

	public Archetype getArchetype() {
		return archetype;
	}

	public RuntimeModel getRuntimeModel() {
		return runtimeModel;
	}

	public ICommunicator getCommunicator() {
		return communicator;
	}

	public IResolver getResolver() {
		return resolver;
	}
	
	public boolean isDebugable() {
		return this.config.isDebug();
	}	
	
}
