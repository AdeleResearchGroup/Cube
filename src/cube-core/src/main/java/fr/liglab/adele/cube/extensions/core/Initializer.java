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

import fr.liglab.adele.cube.TypeNotDeclaredException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.ExtensionConfiguration;
import fr.liglab.adele.cube.extensions.IExtension;

/**
 * Core Extension Monitor : Initializer.
 * 
 * When the CubePlatform starts, this monitor creates automatically scope and node 
 * instances with the configuration provided in the extension configuration 
 * {@link fr.liglab.adele.cube.extensions.core.InitializerConfiguration InitializerConfiguration}.
 * 
 * @author debbabi
 *
 */
public class Initializer {
			
	CoreExtension extension;
	InitializerConfiguration conf;
	
	public Initializer(CoreExtension extension, InitializerConfiguration conf) {		
		this.extension = extension;
		this.conf = conf;
	}

	public void run() {
		System.out.println("[CoreExtension.Initializer] running...");
		
		CInstance scopeInst = null;
		try {
			if (true) {}
			if (conf != null) {
				if (conf.getScopeType() != null) { 
					if (extension.getCubeAgent().getArchetype().getType(conf.getScopeType()) != null) {				
						scopeInst = extension.getCubeAgent().getArchetype().getType(conf.getScopeType()).newInstance(extension.getCubeAgent());
						if (conf.getScopeId() != null) {
							scopeInst.setLocalId(conf.getScopeId());
						}
						extension.getCubeAgent().getRuntimeModel().addAndValidate(scopeInst);
					}
				}
			}
		} catch (TypeNotDeclaredException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		CInstance nodeInst = null;
		try {
			if (conf != null && conf.getNodeType() != null && extension.getCubeAgent().getArchetype().getType(conf.getNodeType()) != null) {
				nodeInst = extension.getCubeAgent().getArchetype().getType(conf.getNodeType()).newInstance(extension.getCubeAgent());
				if (conf.getNodeId() != null) {
					nodeInst.setLocalId(conf.getNodeId());
				}
				extension.getCubeAgent().getRuntimeModel().addAndValidate(nodeInst);
			}
		} catch (TypeNotDeclaredException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}	

}
