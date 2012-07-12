/*
 * Copyright 2011 Adele Team LIG (http://www-adele.imag.fr/)
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

package fr.liglab.adele.cube.extensions.core.model;

import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.util.id.CInstanceUID;

public class ComponentInstance extends CInstance {

	private CInstanceUID node = null;
	
	List<CInstanceUID> inComponents = new ArrayList<CInstanceUID>();
	List<CInstanceUID> outComponents = new ArrayList<CInstanceUID>();
	
	public ComponentInstance(ComponentType co) {
		super(co);
		if (co != null) {	
						
			List<CInstance> nodes = co.getArchtype().getCubeAgent().getRuntimeModel().getCInstances(CoreExtensionFactory.ID, NodeType.NAME);
			if (nodes != null && nodes.size() > 0) {
				for (CInstance i : nodes) {
					// set by defautl the node to the already created local node instance!
					this.node = i.getId();					
					break;
				}
			}
			
		}
		// TODO Auto-generated constructor stub
	}
	
	public void setNode(CInstanceUID node) {
		this.node = node;
	}
	
	public CInstanceUID getNode() {
		return this.node;
	}
	
	public void addInComponent(CInstanceUID c) {
		if (!this.inComponents.contains(c)) {
			this.inComponents.add(c);
		}
	}
	
	public void removeInComponent(CInstanceUID c) {
		this.inComponents.remove(c);
	}
	
	public void addOutComponent(CInstanceUID c) {
		if (!outComponents.contains(c)) {
			this.outComponents.add(c);
		}
	}
	
	public void removeOutComponent(CInstanceUID c) {
		this.outComponents.remove(c);
	}
	
	
	
	public List<CInstanceUID> getInComponents() {
		return inComponents;
	}

	public List<CInstanceUID> getInComponents(ManagedElement cObjectType) {
		List<CInstanceUID> result = new ArrayList<CInstanceUID>();		
		for (CInstanceUID id : this.inComponents) {			
			CInstance instance = getCubeAgent().getRuntimeModel().getCInstance(id);
			if (instance != null && instance.getCType().equals(cObjectType)) {
				result.add(id);
			}			
		}		
		return result;
	}	
	
	public boolean hasInComponent(CInstanceUID instance) {
		return this.inComponents.contains(instance);
	}
		
	public List<CInstanceUID> getOutComponents() {
		return outComponents;
	}

	public List<CInstanceUID> getOutComponents(ManagedElement cObjectType) {
		List<CInstanceUID> result = new ArrayList<CInstanceUID>();		
		for (CInstanceUID id : this.outComponents) {			
			CInstance instance = getCubeAgent().getRuntimeModel().getCInstance(id);
			if (instance != null && instance.getCType().equals(cObjectType)) {
				result.add(id);
			}			
		}		
		return result;
	}	
	
	public boolean hasOutComponent(CInstanceUID instance) {
		return this.outComponents.contains(instance);
	}
	
	@Override
	protected void updateProperties() {	
		super.updateProperties();
		
	}
	
	@Override
	public Object clone() {
		Object clone = super.clone();
		((ComponentInstance)clone).inComponents = new ArrayList<CInstanceUID>();
		((ComponentInstance)clone).outComponents  = new ArrayList<CInstanceUID>();
		for (CInstanceUID inC: this.inComponents) {
			((ComponentInstance)clone).inComponents.add(inC);
		}		
		for (CInstanceUID outC: this.outComponents) {
			((ComponentInstance)clone).outComponents.add(outC);
		}		
		return clone;
	}
	
	@Override
	public String toString() {
		String tmp ="\tlocalId: " + getLocalId() + "\n";
		tmp += "\tNode: " + getNode() + "\n";
		tmp += "\tInComps:\n";
		for (CInstanceUID id: inComponents) {
			tmp += "\t  * " + id +"\n";
		}
		tmp += "\tOutComps:\n";
		for (CInstanceUID id: outComponents) {
			tmp += "\t  * " + id +"\n";
		}
		return tmp;
	}


}
