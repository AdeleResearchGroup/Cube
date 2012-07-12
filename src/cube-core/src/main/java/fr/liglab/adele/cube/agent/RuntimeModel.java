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

package fr.liglab.adele.cube.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prefuse.util.UpdateListener;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.RuntimeModelListener;
import fr.liglab.adele.cube.TypeNotDeclaredException;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.util.id.CInstanceUID;

public class RuntimeModel {

	CubeAgent ca;

	/**
	 * key: instanceID
	 * value instance
	 */
	Map<String, CInstance> coinstances = new HashMap<String, CInstance>();

	List<RuntimeModelListener> rmlisteners = new ArrayList<RuntimeModelListener>();

	CubeLogger log;

	public RuntimeModel(CubeAgent ci) {
		this.ca = ci;
		log = new CubeLogger(ci.getCubePlatform().getBundleContext(),
				RuntimeModel.class.getSimpleName());
	}

	public boolean add(CInstance coi) throws TypeNotDeclaredException {		
		if (coi != null) {
			if (this.coinstances.values().contains(coi)) {
				// TODO!!!
				return false;
			}
			/* check if the type of this instance is declared on the archtype */
			if ((coi.getCType() != null)
					&& (ca.getArchetype().getType(
							coi.getCType().getNamespace(),
							coi.getCType().getName(),
							coi.getCType().getId()) != null)) {
				coi.changeState(CInstance.UNRESOLVED);
				addInstance(coi);	
				return true;
			} else {
				String msg = "The CType " + coi.getCType()
						+ " is not specified on the archtype!";
				log.error(msg);
				throw new TypeNotDeclaredException(msg);
			}
		}
		return false;
	}

	private synchronized void addInstance(CInstance coi) {
		//log.info("** Adding instance " + coi.getId() + " to the Runtime Model.");
		this.coinstances.put(coi.getId().toString(), coi);		
	}

	public void resolve(CInstance coi) {
		if (coi != null) {
			//log.debug("resolving instance " + coi.getId() + " [" + coi.getStateAsString() + "]");
			synchronized (this.coinstances) {
				if (this.coinstances.containsKey(coi.getId().toString())) {
					if (coi.getState() == CInstance.UNRESOLVED || coi.getState() == CInstance.UNCHECKED) {
						//coi.changeState(CObjectInstance.UNRESOLVED);
						getCubeAgent().getResolver().resolveNewInstance(coi);
					} 
				} 
			}
		}
	}
	
	public void validate(CInstance coi) {
		if (coi != null) {
			//log.debug("validating instance " + coi.getId() + " [" + coi.getStateAsString() + "]");
			synchronized (this.coinstances) {
				if (this.coinstances.containsKey(coi.getId().toString())) {
					if (coi.getState() == CInstance.UNRESOLVED) {						
						coi.changeState(CInstance.VALID);
						notifyInstanceValid(coi);
					}
				} 
			}
		}
	}
	
	/**
	 * Called by the LyfeCycleManager when the instance is resolved! so it will
	 * be added to the runtime model
	 * 
	 * @param coi
	 */
	public void addAndValidate(CInstance coi) throws TypeNotDeclaredException {
		if (coi != null) {			
			this.add(coi);			
			this.resolve(coi);
			/*
			if (coi.getState() == CObjectInstance.RESOLVED) {
				coi.setRuntimeModel(this);
				coinstances.put(coi.getId().toString(), coi);
				coi.changeState(CObjectInstance.VALID);
				notifyInstanceValid(coi);
			} else {
				log.warning("the instance "
						+ coi
						+ " of type "
						+ coi.getCObjectType().getId()
						+ " could not be added to the runtime model because it is not yet resolved!");
			}*/
		}
	}

	void updateAndValidate(String id, CInstance newCoi) {
		if (newCoi != null) {
			//log.info("updating the instance '" + newCoi.getId() + "'");
			//log.info("new instance " + newCoi);
			CInstance coi = this.coinstances.get(id);
			if (coi != null) {
				newCoi.setId(coi.getId());
				this.coinstances.put(id, newCoi);				
				//coi = newCoi;
			}
			//log.info("updated instance " + coi);
			//log.info("updating the instance '" + this.coinstances.get(id) + "' of type '"+this.coinstances.get(id).getCObjectType().getId()+"'");
		}
	}

	private void notifyInstanceValid(CInstance coi) {
		//synchronized (this.rmlisteners) {
		// create a copy of the list of listeners to notify
		List<RuntimeModelListener> listeners = new ArrayList<RuntimeModelListener>();		
		synchronized (this.rmlisteners) {
			for (RuntimeModelListener rml : rmlisteners) {
				listeners.add(rml);
				//rml.validatedInstance(coi);
			}	
		}
		for (RuntimeModelListener rml : listeners) {
			//System.out.println("*************** notify listener: " + rml + "\n for instance: " + coi.getId());
			rml.validatedInstance(coi);
		}		
		//}
	}

	public void addListener(RuntimeModelListener rml) {
		synchronized (this.rmlisteners) {
			this.rmlisteners.add(rml);
		}
	}

	public void removeListener(RuntimeModelListener rml) {
		synchronized (this.rmlisteners) {
			this.rmlisteners.remove(rml);
		}
	}

	/*
	public synchronized void updateListeners() {
		if (this.toBeAddedrmlisteners.size() > 0) {
			for (RuntimeModelListener l : this.toBeAddedrmlisteners) {
				this.rmlisteners.add(l);
			}
			for (RuntimeModelListener l : this.toBeRemoveddrmlisteners) {
				this.rmlisteners.remove(l);
			}
		}
	}*/
	
	public CubeAgent getCubeAgent() {
		return ca;
	}

	public Map<String, CInstance> getCInstances() {
		return coinstances;
	}

	public List<CInstance> getCInstances(String namesapce,
			String name) {
		List<CInstance> result = new ArrayList<CInstance>();
		for (String key : this.coinstances.keySet()) {
			if (this.coinstances.get(key).getCType().getNamespace()
					.equalsIgnoreCase(namesapce)
					&& this.coinstances.get(key).getCType().getName()
							.equalsIgnoreCase(name)) {
				result.add(this.coinstances.get(key));
			}
		}
		return result;
	}
	public List<CInstance> getCInstances(String namesapce,
			String name, int state) {
		List<CInstance> result = new ArrayList<CInstance>();
		for (String key : this.coinstances.keySet()) {
			if (this.coinstances.get(key).getCType().getNamespace()
					.equalsIgnoreCase(namesapce)
					&& this.coinstances.get(key).getCType().getName()
							.equalsIgnoreCase(name) && this.coinstances.get(key).getState() == state) {
				result.add(this.coinstances.get(key));
			}
		}
		return result;
	}
	
	public synchronized CInstance getCInstance(CInstanceUID id) {
		if (id != null) {
			for (String key : this.coinstances.keySet()) {
				if (this.coinstances.get(key).getId().equals(id)) {
					return this.coinstances.get(key);
					
				}
			}
		}
		return null;
	}

	public synchronized CInstance getCInstance(String id) {
		if (id != null) {
			for (String key : this.coinstances.keySet()) {
				if (this.coinstances.get(key).getId().toString().equalsIgnoreCase(id)) {
					return this.coinstances.get(key);
				}
			}
		}
		return null;
	}
	
	public List<CInstance> getCInstances(ManagedElement cot) {
		List<CInstance> result = new ArrayList<CInstance>();
		for (CInstance coi : coinstances.values()) {
			if (coi.getCType().equals(cot)) {
				result.add(coi);
			}
		}
		return result;
	}



}
