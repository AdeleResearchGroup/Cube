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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.agent.AgentConfig;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ICommunicator;
import fr.liglab.adele.cube.agent.IResolverFactory;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.id.CubeAgentID;

/**
 * Cube Platform Implementation.
 * 
 * It manages the local {@link fr.liglab.adele.cube.agent.__CubeAgent Cube Agents}.
 * 
 * @author debbabi
 *
 */
public class CubePlatform implements ICubePlatform {

	public static final String CUBE_VERSION = "1.2.0-SNAPSHOT";
	private static final String DEFAULT_AGENT_CONFIG_FILE_URL = "file:cubeagent.xml";

	/**
	 * <CubeAgentID, CubeAgent>
	 */
	Map<String, CubeAgent> cubeAgents = new HashMap<String, CubeAgent>();
	
	List<IExtensionFactory> extensionFactories = new ArrayList<IExtensionFactory>();
	
	List<IResolverFactory> resolverFactories = new ArrayList<IResolverFactory>();	
	
	List<ICommunicator> communicators = new ArrayList<ICommunicator>();
	
	BundleContext bundleContext;
	
	CubeLogger log = null;	

	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {		
		return CUBE_VERSION;
	}
	
	/**
	 * Constructor.
	 * @param btx
	 */
	public CubePlatform(BundleContext btx) {
		this.bundleContext = btx;
		log = new CubeLogger(btx, CubePlatform.class.getName());
	}
	
	/**
	 * Called when the Cube Platform starts on the local OSGi Platform.
	 */
	public void starting() {	
		System.out.println(" ");
		System.out.println("");
		System.out.println("    _______              ");
		System.out.println("   /|      |             ");
		System.out.println("  | | CUBE |...Starting the CUBE Platform  ");
 	    System.out.println("  | |______|   " + getVersion());	
		System.out.println("  |/______/              ");
		System.out.println("");
		
	
		// demo!
//		try {
//			createCubeAgent("file:/home/debbabi/dev/cube/demo/testplatform/demo.arch");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Called when the Cube Platform stops on the local OSGi Platform.
	 * All the created Cube Agents will be destroyed.
	 */
	public void stopping() {		
		System.out.println(" ");
		System.out.println("[INFO] ... Stopping the CUBE Platform");
		for (CubeAgent ci: this.cubeAgents.values()) {
			ci.stop();
		}
		System.out.println("[INFO] ... Bye!");
		System.out.println(" ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<IExtensionFactory> getExtensionFactories() {
		return this.extensionFactories;		
	}
	/**
	 * {@inheritDoc}
	 */
	public IExtensionFactory getExtensionFactory(String id) {	
		for (IExtensionFactory c : extensionFactories) {
			if (c.getExtensionId().equalsIgnoreCase(id)) {
				return c;
			}
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	public IExtensionFactory getExtensionFactory(String id, String version) {	
		if (version == null) {
			return getExtensionFactory(id);
		}
		for (IExtensionFactory c : extensionFactories) {
			if (c.getExtensionId().equalsIgnoreCase(id) && c.getExtensionVersion().equalsIgnoreCase(version)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CubeAgentID createCubeAgent(String configUrl) throws Exception {
		CubeAgent ci = new CubeAgent(this, configUrl);
		cubeAgents.put(ci.getId().toString(), ci);
		ci.run();
		return ci.getId();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CubeAgentID createCubeAgent(AgentConfig configuration) throws Exception {
		CubeAgent ci = new CubeAgent(this, configuration);
		cubeAgents.put(ci.getId().toString(), ci);
		ci.run();
		return ci.getId();
	}
	
	/**
	 * {@inheritDoc}
	 * @deprecated
	 */
	public CubeAgentID createCubeAgent(String configUrl, String archetypeUrl) throws Exception {				
		CubeAgent ci = new CubeAgent(this, configUrl, archetypeUrl);
		cubeAgents.put(ci.getId().toString(), ci);
		ci.run();
		return ci.getId();
	}
	
	/**
	 * {@inheritDoc}
	 * @deprecated
	 */
	public CubeAgentID createCubeAgent(String archetypeUrl, String host, int port) throws Exception {					
		/*
		CubeAgent ci = new CubeAgent(this, archetypeUrl, host, port);
		if (ci != null) {
			cubeAgents.put(ci.getId().toString(), ci);
			ci.run();
			return ci.getId();
		} else {
			return null;
		}
		*/
		try {
			throw new Exception("CubePlatform.createCubeAgent(String, String, int) Not yet implemented!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stopCubeAgent(String cubeAgentID) {
		log.warning("Stopping a Cube Instance is not yet implemented!");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void destroyCubeAgent(String CubeAgentID) {
		CubeAgent ca = this.cubeAgents.remove(CubeAgentID);
		if (ca != null) {
			ca.stop();			
			ca = null;			
		}
		//log.warning("Destroying a Cube Instance is not yet implemented!");
	}
		
	/**
	 * {@inheritDoc}
	 */
	public List<String> getCubeAgents() {
		List<String> ids = new ArrayList<String>();
		for (String key: cubeAgents.keySet()) {
			ids.add(cubeAgents.get(key).getId().toString());
		}
		return ids;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CubeAgent getCubeAgent(String cubeid) {
		return cubeAgents.get(cubeid);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public BundleContext getBundleContext() {
		return this.bundleContext;
	}

	public List<IResolverFactory> getResolverFactories() {		
		return this.resolverFactories;
	}

	public IResolverFactory getResolverFactory(String name) {
		for (IResolverFactory r : this.resolverFactories) {			
			if (r.getResolverId() != null && r.getResolverId().equalsIgnoreCase(name)) {
				return r;
			}
		}
		return null;
	}

	public List<ICommunicator> getCommunicators() {
		return this.communicators;
	}

	public ICommunicator getCommunicator(String name) {
		for (ICommunicator c : this.communicators) {
			if (c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}

	
	
}
