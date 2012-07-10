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

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.IExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.parser.AgentConfigParserPlugin;
import fr.liglab.adele.cube.util.parser.ArchetypeParserPlugin;

/**
 * Core Extension Factory.
 * 
 * @author debbabi
 *
 */
public class CoreExtensionFactory implements IExtensionFactory {
	
	public static final String ID = "fr.liglab.adele.cube.core";
	public static final String VERSION = CubePlatform.CUBE_VERSION;
	
	private AgentConfigParserPlugin agentParser = null;
	private ArchetypeParserPlugin archParser = null;
	
	/**
	 * {@inheritDoc}
	 */
	public String getExtensionId() {
		// TODO Auto-generated method stub
		return ID;
	}
	/**
	 * {@inheritDoc}
	 */
	public String getExtensionVersion() {
		// TODO Auto-generated method stub
		return VERSION;
	}
	/**
	 * {@inheritDoc}
	 */
	public AgentConfigParserPlugin getAgentConfigParserPlugin() {
		if (this.agentParser == null) {
			this.agentParser = new CoreAgentConfigParserPlugin();
		}
		return this.agentParser;
	}
	/**
	 * {@inheritDoc}
	 */
	public ArchetypeParserPlugin getArchetypeParserPlugin() {
		if (this.archParser == null) {
			this.archParser = new CoreArchtypeParserPlugin();
		}
		return this.archParser;
	}
	/**
	 * {@inheritDoc}
	 */
	public IExtension newExtension(CubeAgent agent, AgentExtensionConfig config) {
		// TODO Auto-generated method stub
		return new CoreExtension(agent, this, config);
	}
	



}
