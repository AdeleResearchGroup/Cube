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


package fr.liglab.adele.cube.util.deploy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.cube.ICubePlatform;
import fr.liglab.adele.cube.util.id.CubeAgentID;
import fr.liglab.adele.cube.util.parser.AgentConfigParser;


/**
 * Hot Archetype Deployer.
 * 
 * When you put your archetype xml file (.arch) on the <b>load</b> directory, 
 * the Cube Platform will automatically creates an agent to handle it.
 * 
 * @author debbabi
 *
 */
@Component
@Provides
@Instantiate
public class HotDeployer implements ArtifactInstaller {

	@Requires
	private ICubePlatform cp;
	
	/**
	 * Key: fileUrl
	 * Value: CubeAgentID
	 */
	private Map<String, String> handledFiles = new HashMap<String, String>();
		
	public boolean canHandle(File file) {
		if (file.getName().endsWith(AgentConfigParser.CUBE_AGENT_CONFIG_EXTENSION) ){
			return true;
		}
		return false;
	}

	public void install(File file) throws Exception {
		if (file.getName().endsWith(AgentConfigParser.CUBE_AGENT_CONFIG_EXTENSION) ){
			if (this.cp != null) {
				CubeAgentID id = this.cp.createCubeAgent("file:" + file.getAbsolutePath());
				handledFiles.put(file.getAbsolutePath(), id.toString());
			}
		}
	}

	public void uninstall(File file) throws Exception {
		if (this.cp != null) {			
			String id = this.handledFiles.get(file.getAbsolutePath());
			if (id != null) {
				this.cp.destroyCubeAgent(id);
			}
		}
	}

	public void update(File file) throws Exception {
		uninstall(file);
		install(file);
	}
	
}
