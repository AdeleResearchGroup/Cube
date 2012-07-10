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

import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.ComponentType;

/**
 * Cube Internal Monitoring Extension.
 * 
 * @author debbabi
 *
 */
public class InternalMonitoringExtension extends AbstractExtension {
	
	MonitorGUI gui;
	
	public InternalMonitoringExtension(CubeAgent agent,
			IExtensionFactory factory, AgentExtensionConfig config) {
		super(agent, factory, config);		
	}
	
	@Override
	public void start() {
		System.out.println("\n\n\nCUBE INTERNAL MONITORING started.\n\n");		
		this.gui = new MonitorGUI(getCubeAgent());
		this.gui.setVisible(true);
		for (CInstance i : getCubeAgent().getRuntimeModel().getCInstances(CoreExtensionFactory.ID, ComponentType.NAME, CInstance.VALID)) {
			this.gui.addComponent(i);
		}
	}

	public void stateChanged(CInstance coi, int oldState, int newState) {
		// TODO Auto-generated method stub
		
	}

	public void validatedInstance(CInstance coi) {
		if (coi != null) {			
			if (coi.getCType().getNamespace().equalsIgnoreCase(CoreExtensionFactory.ID) && coi.getCType().getName().equalsIgnoreCase(ComponentType.NAME)) {
				if (this.gui != null) {
					this.gui.addComponent(coi);
				}
			}
		}
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
