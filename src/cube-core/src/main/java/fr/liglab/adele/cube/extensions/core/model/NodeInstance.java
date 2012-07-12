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
import fr.liglab.adele.cube.util.id.CInstanceUID;

public class NodeInstance extends CInstance {

	public static final String HOST = "host";
	public static final String PORT = "port";	
	
	List<CInstanceUID> scopeInstances = new ArrayList<CInstanceUID>();
	
	List<CInstanceUID> componentInstances = new ArrayList<CInstanceUID>();
	
	public NodeInstance(NodeType n) {
		super(n);		
	}
	
	
	public NodeInstance(NodeType n, String host, String port) {
		super(n);	
		if (host != null) {
			setProperty(HOST, host);
		}
		if (port != null) {
			setProperty(PORT, port);
		}
	}

	public String getHost() {
		if (getProperty(HOST) != null) {
			return getProperty(HOST).toString();
		} else {
			return null;
		}
	}

	public String getPort() {
		if (getProperty(PORT) != null) {
			return getProperty(PORT).toString();
		} else {
			return null;
		}
	}
	
	public void addScope(CInstanceUID si) {
		if (si != null) {
			if (this.scopeInstances.contains(si) == false) {
				this.scopeInstances.add(si);
			}		
		}
	}
	
	public void addComponentInstance(CInstanceUID ci) {
		if (ci != null) {
			if (this.componentInstances.contains(ci) == false) {
				this.componentInstances.add(ci);
			}
		}
	}
	
	public boolean removeComponentInstance(CInstanceUID ci) {
		if (ci != null) {
			if (this.componentInstances.contains(ci) == true) {
				return this.componentInstances.remove(ci);
			}
		}
		return false;
	}	
	
	public List<CInstanceUID> getScopes() {
		return this.scopeInstances;
	}	
	
	public List<CInstanceUID> getComponentInstances() {
		return this.componentInstances;
	}
	
	public boolean hasComponentInstance(CInstanceUID instance) {
		if (instance != null) {
			return this.componentInstances.contains(instance);
		} else {
			return false;
		}
	}
	
	public List<CInstanceUID> getComponentInstances(ManagedElement cot) {
		
		List<CInstanceUID> result = new ArrayList<CInstanceUID>();
		for (CInstanceUID instID : getComponentInstances()) {
			CInstance inst = getCubeAgent().getRuntimeModel().getCInstance(instID);
			if (inst != null && inst.getCType().equals(cot)) {
				result.add(instID);
			}
		}
		return result;
		
	}
		
	public Object clone() {
		Object clone = super.clone();
		((NodeInstance)clone).scopeInstances = new ArrayList<CInstanceUID>();
		((NodeInstance)clone).componentInstances  = new ArrayList<CInstanceUID>();
		for (CInstanceUID inC: this.scopeInstances) {
			((NodeInstance)clone).scopeInstances.add(inC);
		}		
		for (CInstanceUID outC: this.componentInstances) {
			((NodeInstance)clone).componentInstances.add(outC);
		}		
		return clone;
	}
	
	@Override
	public String toString() {
		String tmp = "\tlocalId:" + getLocalId() + "\n";								
		tmp += "\tCOMPONENTS:\n";
		for (CInstanceUID id : this.componentInstances) {
			tmp += "\t  * " + id + "\n";
		}
		tmp += "\tSCOPES:\n";
		for (CInstanceUID id : this.scopeInstances) {
			tmp += "\t  * " + id + "\n";
		}
		return  tmp;
	}
}
