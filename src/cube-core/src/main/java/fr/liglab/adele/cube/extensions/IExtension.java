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

import java.util.List;

import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CubeAgent;


/**
 * Extension Interface.
 * 
 * @author debbabi
 *
 */
public interface IExtension {

	public IExtensionFactory getExtensionFactory();		
	public AgentExtensionConfig getExtensionConfig();
	public CubeAgent getCubeAgent();
	public List<IMonitor> getMonitors();
	public List<IExecutor> getExecutors();
	public List<IConstraintResolver> getConstraintResolvers();
	public IConstraintResolver getConstraintResolver(String constraintNamespace, String constraintName);
	
	/**
	 * Create and starts the different modules: Monitors, Executors, ConstraintResolvers, etc.
	 * 
	 */
	public void start();
	public void stop();
}
