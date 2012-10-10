/*
 * Copyright 2011-2012 Adele Team LIG (http://www-adele.imag.fr/)
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

package fr.liglab.adele.cube.extensions.core;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ExtensionConfiguration;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.extensions.core.constraints.ComponentsPerNodeResolver;
import fr.liglab.adele.cube.extensions.core.constraints.ConnectResolver;
import fr.liglab.adele.cube.extensions.core.constraints.FindLocallyResolver;
import fr.liglab.adele.cube.extensions.core.constraints.InComponentsResolver;
import fr.liglab.adele.cube.extensions.core.constraints.InScopeResolver;
import fr.liglab.adele.cube.extensions.core.constraints.OnNodeResolver;
import fr.liglab.adele.cube.extensions.core.constraints.OutComponentsResolver;
import fr.liglab.adele.cube.extensions.core.constraints.CreateLocallyResolver;
import fr.liglab.adele.cube.extensions.core.model.ScopeInstance;
import fr.liglab.adele.cube.extensions.core.scopesmanagement.ScopeLeader;
import fr.liglab.adele.cube.extensions.core.scopesmanagement.TopScopeLeader;

/**
 * Core Extension.
 * 
 * @author debbabi
 *
 */
public class CoreExtension extends AbstractExtension {
		
	/**
	 * Logger
	 */
	private CubeLogger log;
	
	/**
	 * Initializer
	 */
	private Initializer initializer = null;	
	/**
	 * Scope Controller
	 */
	private ScopeController scopeController = null;
	
	/**
	 * Constructor
	 * 
	 * @param agent
	 * @param factory
	 * @param config
	 */
	public CoreExtension(CubeAgent agent, IExtensionFactory factory,
			AgentExtensionConfig config) {
		super(agent, factory, config);
		log = new CubeLogger(agent.getCubePlatform().getBundleContext(), CoreExtension.class.getSimpleName());
	}

	/**
	 * {@inheritDoc}
	 */
	public void start() {		
		
		//
		// registering the different constraint resolvers.
		//
		addConstraintResolver(new ConnectResolver());			
		addConstraintResolver(new OnNodeResolver());
		addConstraintResolver(new FindLocallyResolver());
		addConstraintResolver(new CreateLocallyResolver());
		addConstraintResolver(new InComponentsResolver());
		addConstraintResolver(new OutComponentsResolver());
		addConstraintResolver(new InScopeResolver());
		addConstraintResolver(new ComponentsPerNodeResolver());
								
		//
		// register the scopes controller
		//		
		scopeController = new ScopeController(this);
		scopeController.run();
		
		//
		// registering the initializer
		//
		ExtensionConfiguration cc = getExtensionConfig().getConfiguration(InitializerConfiguration.NAME);				
		initializer = new Initializer(this, (InitializerConfiguration) cc);
		initializer.run();
				
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stateChanged(CInstance coi, int oldState, int newState) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void validatedInstance(CInstance coi) {
		if (coi != null && coi instanceof ScopeInstance) {
			/*
			 * If there is a new scope instance created locally:
			 * we should integrate it with its right scope group.
			 */
			scopeController.control((ScopeInstance)coi);
		}
	}

	
	
	/**
	 * Get Logger
	 * @return
	 */
	public CubeLogger getLogger() {
		return log;
	}
	
	@Override
	public String toString() {
		String out = "";
		out += "+ CoreExtension\n";
		
		if (this.scopeController.getTopScopeLeader() != null) {
			out += "  + scopes.management\n";
			out += "    - top.scope.leader.url: " + this.scopeController.getTopScopeLeader().getUrl() + "\n";
			out += "    - is.top.scope.leader: " + this.scopeController.getTopScopeLeader().ImTheTopScopeLeader() + "\n";
			if (this.scopeController.getTopScopeLeader().ImTheTopScopeLeader() == true) {
				out += this.scopeController.getTopScopeLeader().toString();
			}
		}
		if (this.scopeController.getScopeLeader() != null) {
			out += "    - scope.leader.url: " + this.scopeController.getScopeLeader().getUrl() + "\n";
			out += "    - is.scope.leader: " + this.scopeController.getScopeLeader().ImTheScopeLeader() + "\n";
			if (this.scopeController.getScopeLeader().ImTheScopeLeader() == true) {
				out += this.scopeController.getScopeLeader().toString();
			}
		}
		return out;
	}


}
