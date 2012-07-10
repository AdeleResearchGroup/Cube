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

import java.util.ArrayList;
import java.util.List;

/**
 * Agent's Extension Config.
 * 
 * @author debbabi
 *
 */
public class AgentExtensionConfig {
	
	private String id = null;
	private String version = null;	
	private List<ExtensionConfiguration> configurations = new ArrayList<ExtensionConfiguration>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<ExtensionConfiguration> getConfigurations() {
		return configurations;
	}
	
	public void addConfiguration(ExtensionConfiguration ec) {
		if (ec != null) {
			this.configurations.add(ec);
		}
	}
	
	public void setConfigurations(List<ExtensionConfiguration> configurations) {
		this.configurations = configurations;
	}
	public ExtensionConfiguration getConfiguration(String name) {
		for (ExtensionConfiguration ec : this.configurations) {
			if (ec.getName().equalsIgnoreCase(name)) {
				return ec;
			}
		}
		return null;
	}	
	
	
}
