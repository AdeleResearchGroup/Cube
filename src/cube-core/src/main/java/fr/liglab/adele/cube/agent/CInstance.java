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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.util.id.CInstanceUID;
import fr.liglab.adele.cube.util.id.CubeAgentID;

public class CInstance implements Cloneable {
	
	public static final int UNRESOLVED = 0;
	public static final int VALID = 1;
	public static final int UNCHECKED = 2;
	
	private int state = UNRESOLVED;
	
	private Object locker = null;
	
	//private static int index = 0;
	
	private CInstanceUID id;
	
	private static int index=0;
	
	private String localId;
	
	private ManagedElement cObject; /* type */	
	
	private RuntimeModel rm = null;
	
	/**
	 * Properties.
	 * All the extended classes should add any new property to this list.
	 */
	Properties properties = new Properties();
	
	/**
	 * Contained CObjectInstances
	 */
	Map<String, CInstance> referencedInstances = new HashMap<String, CInstance>();		
	
	/*
	public CObjectInstance(CObjectType co, CubeInstance ci) {
		this(co, null, ci);
	}*/	
	
	public CInstance(ManagedElement co) {
		this(co, new Properties());		
	}
	
	public CInstance(ManagedElement co, String localId) {
		this(co, new Properties());
		if (localId != null && localId.trim().length() > 0) {
			this.localId = localId.trim();
		} 
	}

	
	
	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public CInstance(ManagedElement co, Properties properties) {
		this(co, null, null);
	}
	
	public CInstance(ManagedElement co, Properties properties, RuntimeModel rm) {		
		this.state = UNRESOLVED;
		this.localId = "" + index++;
		setId(new CInstanceUID(co.getArchtype().getCubeAgent().getId()));
		this.cObject = co;		
		this.rm = rm;		
		if (properties != null) {
			this.properties = properties;
		}
		setRuntimeModel(rm);
	}
	
	public boolean isLocal() {	
		CubeAgentID cid = this.getId().getCubeAgentID();
		if (cid != null && cid.equals(getCubeAgent().getId())) {
			return true;
		}
		System.out.println("[CInstance] warning: this is not a local instance!!");
		return false;
	}
	
	public void setId(CInstanceUID id) {
		this.id = id;
	}
	
	protected void setRuntimeModel(RuntimeModel rm) {						
		if (rm != null) {
			if (rm != this.rm) {
				this.rm = rm;				
			}
		} 
	}
	
	public void validate() {
		getCubeAgent().getRuntimeModel().resolve(this);
	}
	
	/*
	public CObjectInstance(CObjectType co, Properties properties, CubeInstance ci) {
		this.state = UNMANAGED;
		this.cObject = co;
		this.id = new CObjectInstanceID(ci.getId());
		if (properties != null) {
			this.properties = properties;
		}
	}*/	
	
	public Object getProperty(Object property) {
		return this.properties.get(property);
	}
	
	protected void setProperty(Object key, Object value) {
		if (key != null) {
			this.properties.put(key, value);
		}
	}	
	
	public Collection<CInstance> getChildCObjectInstances() {		
		return this.referencedInstances.values();
	}
	
	protected void addReferencedCObjectInstance(CInstance coi) {
		//TODO verify that all was specified on the archtype types!
		this.referencedInstances.put(coi.getId().toString(), coi);
	}
	
	protected CInstance removeChildCObjectInstance(String id) {
		return this.referencedInstances.remove(id);
	}
	
	/**
	 * Change the instance state!
	 * 
	 * only LyfeCycleManager and RuntimeModel could change the state!
	 * 
	 * @param newState
	 * @return oldstate
	 */
	public int changeState(int newState) {
		int oldState = this.state;
		this.state = newState;		
		if (newState == VALID) {
			updateProperties();
		}
		return oldState;
	}

	protected void updateProperties() {
		
	}

	public int getState() {
		return state;
	}

	public String getStateAsString() {
		if (getState() == VALID) {
			return "VALID";
		}
		if (getState() == UNRESOLVED) {
			return "UNRESOLVED";
		}
		if (getState() == UNCHECKED) {
			return "UNCHECKED";
		}
		return "";
	}
	
	public ManagedElement getCType() {
		return cObject;
	}

	public CInstanceUID getId() {
		return id;
	}
	
	public String getLocalId() {
		return this.localId;
	}
	
	public CubeAgent getCubeAgent() {
		if (getCType() != null) {
			return this.getCType().getArchtype().getCubeAgent();
		}
		return null;
	}
	
	public synchronized  void lock(Object lam) {
		this.locker = lam;	
	}
	
	public synchronized void unlock(Object lam) {
		if (this.locker == lam) {
			this.locker = null;
		}
	}
	
	public boolean isLocked() {
		return this.locker != null;
	}
	
	public Object clone() {
		CInstance coi = null;
		try {
			coi = (CInstance) super.clone();
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return coi;
	}
}
