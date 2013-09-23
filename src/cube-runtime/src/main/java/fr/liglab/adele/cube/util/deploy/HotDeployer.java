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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException;
import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.Configuration;
import fr.liglab.adele.cube.util.parser.AutonomicManagerConfigParser;
import fr.liglab.adele.cube.util.parser.ParseException;
import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;


/**
 * Hot Archetype Deployer.
 * 
 * When you put your archetype xml file (.arch) on the <b>load</b> directory, 
 * the Cube Platform will automatically creates an __autonomicmanager to handle it.
 * 
 * @author debbabi
 *
 */
@Component
@Provides
@Instantiate
public class HotDeployer implements ArtifactInstaller {

	@Requires
	private AdministrationService cp;
	
	/**
	 * Key: fileUrl
	 * Value: CubeAgentID
	 */
	private Map<String, String> handledFiles = new HashMap<String, String>();
		
	public boolean canHandle(File file) {
		if (file.getName().endsWith(AutonomicManagerConfigParser.CUBE_AGENT_CONFIG_EXTENSION) ){
			return true;
		}
		return false;
	}

	public void install(File file) throws Exception {
		if (file.getName().endsWith(AutonomicManagerConfigParser.CUBE_AGENT_CONFIG_EXTENSION) ){
			if (this.cp != null) {
                Configuration config = null;
                try {
                    config = AutonomicManagerConfigParser.parse(this.cp, new URL("file:" + file.getAbsolutePath()));
                } catch (ParseException e) {
                    throw new AutonomicManagerException("Error while parsing Autonomic Manager Configuration file!");
                } catch (MalformedURLException e) {
                    throw new AutonomicManagerException("Unknown configuration file!");
                }
                if (config != null) {
				    String id = this.cp.createAutonomicManager(config);
                    handledFiles.put(file.getAbsolutePath(), id.toString());
                }

			}
		}
	}

	public void uninstall(File file) throws Exception {
		if (this.cp != null) {			
			String id = this.handledFiles.get(file.getAbsolutePath());
			if (id != null) {
				this.cp.destroyAutonomicManager(id);
			}
		}
	}

	public void update(File file) throws Exception {
		uninstall(file);
		install(file);
	}
	
}
