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
import fr.liglab.adele.cube.util.id.CInstanceID;

public class ScopeInstance extends CInstance {

	
	List<CInstanceID> nodeInstances = new ArrayList<CInstanceID>();
	
	public ScopeInstance(ScopeType st) {
		super(st);
	}
	
	public void addNode(CInstanceID ci) {
		if (ci != null) {
			if (this.nodeInstances.contains(ci) == false) {
				this.nodeInstances.add(ci);
			}
		}
	}
	
	/**
	 * This methods implies asynchronous communication with other nodes!
	 * 
	 * @return
	 */
	public List<CInstanceID> getNodes() {		
		/*
		try {
			throw new Exception("scope.getNode not yet implemented!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("[ScopeInstance] getNodes : not yet implemented!");
		//return result;		
		return this.nodeInstances;
	}	
	
	public Object clone() {
		Object clone = super.clone();
		((ScopeInstance)clone).nodeInstances = new ArrayList<CInstanceID>();		
		for (CInstanceID inC: this.nodeInstances) {
			((ScopeInstance)clone).nodeInstances.add(inC);
		}				
		return clone;
	}
	
	@Override
	public String toString() {
		String tmp = "";
		tmp += "\tlocalId:" + getLocalId();		
		tmp += "\n";
		tmp += "\tNODES:\n";
		for (CInstanceID id : this.nodeInstances) {
			tmp += "\t  * " + id + "\n";
		}
		return  tmp;
	}
}
