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
import fr.liglab.adele.cube.archetype.Type;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.util.id.CInstanceID;

public class ComponentInstance extends CInstance {

	private CInstanceID node = null;
	
	List<CInstanceID> inComponents = new ArrayList<CInstanceID>();
	List<CInstanceID> outComponents = new ArrayList<CInstanceID>();
	
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
	
	public void setNode(CInstanceID node) {
		this.node = node;
	}
	
	public CInstanceID getNode() {
		return this.node;
	}
	
	public void addInComponent(CInstanceID c) {
		if (!this.inComponents.contains(c)) {
			this.inComponents.add(c);
		}
	}
	
	public void removeInComponent(CInstanceID c) {
		this.inComponents.remove(c);
	}
	
	public void addOutComponent(CInstanceID c) {
		if (!outComponents.contains(c)) {
			this.outComponents.add(c);
		}
	}
	
	public void removeOutComponent(CInstanceID c) {
		this.outComponents.remove(c);
	}
	
	
	
	public List<CInstanceID> getInComponents() {
		return inComponents;
	}

	public List<CInstanceID> getInComponents(Type cObjectType) {
		List<CInstanceID> result = new ArrayList<CInstanceID>();		
		for (CInstanceID id : this.inComponents) {			
			CInstance instance = getCubeAgent().getRuntimeModel().getCInstance(id);
			if (instance != null && instance.getCType().equals(cObjectType)) {
				result.add(id);
			}			
		}		
		return result;
	}	
	
	public boolean hasInComponent(CInstanceID instance) {
		return this.inComponents.contains(instance);
	}
		
	public List<CInstanceID> getOutComponents() {
		return outComponents;
	}

	public List<CInstanceID> getOutComponents(Type cObjectType) {
		List<CInstanceID> result = new ArrayList<CInstanceID>();		
		for (CInstanceID id : this.outComponents) {			
			CInstance instance = getCubeAgent().getRuntimeModel().getCInstance(id);
			if (instance != null && instance.getCType().equals(cObjectType)) {
				result.add(id);
			}			
		}		
		return result;
	}	
	
	public boolean hasOutComponent(CInstanceID instance) {
		return this.outComponents.contains(instance);
	}
	
	@Override
	protected void updateProperties() {	
		super.updateProperties();
		
	}
	
	@Override
	public Object clone() {
		Object clone = super.clone();
		((ComponentInstance)clone).inComponents = new ArrayList<CInstanceID>();
		((ComponentInstance)clone).outComponents  = new ArrayList<CInstanceID>();
		for (CInstanceID inC: this.inComponents) {
			((ComponentInstance)clone).inComponents.add(inC);
		}		
		for (CInstanceID outC: this.outComponents) {
			((ComponentInstance)clone).outComponents.add(outC);
		}		
		return clone;
	}
	
	@Override
	public String toString() {
		String tmp ="\tlocalId: " + getLocalId() + "\n";
		tmp += "\tNode: " + getNode() + "\n";
		tmp += "\tInComps:\n";
		for (CInstanceID id: inComponents) {
			tmp += "\t  * " + id +"\n";
		}
		tmp += "\tOutComps:\n";
		for (CInstanceID id: outComponents) {
			tmp += "\t  * " + id +"\n";
		}
		return tmp;
	}


}
