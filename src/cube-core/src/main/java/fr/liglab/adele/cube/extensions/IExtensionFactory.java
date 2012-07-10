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

import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.util.parser.AgentConfigParserPlugin;
import fr.liglab.adele.cube.util.parser.ArchetypeParserPlugin;

/**
 * Extension Factory.
 * 
 * Used to instantiate the Extension when needed.
 * 
 * @author debbabi
 *
 */
public interface IExtensionFactory {
	
	public String getExtensionId();
	public String getExtensionVersion();
		
	public ArchetypeParserPlugin getArchetypeParserPlugin();
	public AgentConfigParserPlugin getAgentConfigParserPlugin();
	//public LocalConfigurationParserPlugin getLocalConfigParserPlugin();	
	public IExtension newExtension(CubeAgent agent, AgentExtensionConfig config);
	
}
