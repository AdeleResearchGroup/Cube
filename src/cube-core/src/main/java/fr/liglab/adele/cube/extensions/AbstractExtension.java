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

package fr.liglab.adele.cube.extensions;

import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.cube.RuntimeModelListener;
import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CubeAgent;

/**
 * Abstract Extension Class.
 * 
 * All user-provided extensions should extends this class.
 * 
 * @author debbabi
 *
 */
public abstract class AbstractExtension implements IExtension, RuntimeModelListener {
	
	
	private IExtensionFactory factory;
	private AgentExtensionConfig config;
	private CubeAgent agent;
	
	List<IMonitor> monitors = new ArrayList<IMonitor>();
	List<IExecutor> executors = new ArrayList<IExecutor>();
	List<IConstraintResolver> constraintResolvers = new ArrayList<IConstraintResolver>();
	
	public AbstractExtension(CubeAgent agent, IExtensionFactory factory, AgentExtensionConfig config) {
		this.agent = agent;		
		this.factory = factory;
		this.config = config;
		this.agent.getRuntimeModel().addListener(this);
	}

	public CubeAgent getCubeAgent() {
		return agent;
	}

	public IExtensionFactory getExtensionFactory() {		
		return factory;
	}

	public AgentExtensionConfig getExtensionConfig() {
		return config;
	}

	public List<IMonitor> getMonitors() {
		return this.monitors;
	}

	protected void addMonitor(IMonitor monitor) {
		this.monitors.add(monitor);
		monitor.run();
	}
	
	public List<IExecutor> getExecutors() {
		return this.executors;
	}

	protected void addExecutor(IExecutor executor) {
		this.executors.add(executor);
	}
	
	public List<IConstraintResolver> getConstraintResolvers() {
		return this.constraintResolvers;
	}
	
	protected void addConstraintResolver(IConstraintResolver constraintResolver) {
		this.constraintResolvers.add(constraintResolver);
	}
	
	public IConstraintResolver getConstraintResolver(
			String constraintNamespace, String constraintName) {
		for (IConstraintResolver cr : this.constraintResolvers) {
			if (cr.getConstraintName().equalsIgnoreCase(constraintName) && cr.getConstraintNamespace().equalsIgnoreCase(constraintNamespace)) {
				return cr;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public abstract void start();
}
