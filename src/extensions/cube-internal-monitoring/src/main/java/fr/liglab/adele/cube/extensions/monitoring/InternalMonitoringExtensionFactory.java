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



package fr.liglab.adele.cube.extensions.monitoring;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.IExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.parser.AgentConfigParserPlugin;
import fr.liglab.adele.cube.util.parser.ArchetypeParserPlugin;

/**
 * Cube Internal Monitoring Extension Factory.
 * 
 * @author debbabi
 *
 */
@Component
@Provides
@Instantiate
public class InternalMonitoringExtensionFactory implements IExtensionFactory {

	public static final String ID = "fr.liglab.adele.cube.monitoring";
	public static final String VERSION = CubePlatform.CUBE_VERSION;
	
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
	public ArchetypeParserPlugin getArchetypeParserPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public AgentConfigParserPlugin getAgentConfigParserPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IExtension newExtension(CubeAgent agent, AgentExtensionConfig config) {
		return new InternalMonitoringExtension(agent, this, config);
	}

}
