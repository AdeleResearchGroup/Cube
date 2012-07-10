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


package fr.liglab.adele.cube.archetype;

import fr.liglab.adele.cube.TypeNotDeclaredException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;

/**
 * Top parent Managed Cube Object.
 * 
 * @author debbabi
 *
 */
public abstract class Type {
	
	Archetype archtype;
	
	String id;
	String description;		
	
	static int index = 0;
	
	//Map<String, CInstance> instances = new HashMap<String, CInstance>();
			
	public Type(String id, String description, Archetype archtype) {
		this.archtype = archtype;
		if (id != null) {
			this.id = id;
		} else {
			this.id = "" + index++;
		}
		this.description = description;
	}
		
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Archetype getArchtype() {
		return this.archtype;
	}
	
	public abstract String getName();
	public abstract String getNamespace();
	
	protected abstract CInstance newInstance() throws Exception;

	
	public CInstance newInstance(CubeAgent cubeagent) throws Exception, TypeNotDeclaredException {	
		CInstance instance = newInstance();
		if (instance != null) {						
			cubeagent.getRuntimeModel().add(instance);
		}
		return instance;		
	}
	
	public abstract String toXMLString(String xmlns);
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Type) {
			Type o2 = (Type)obj;
			if (o2.getNamespace().equalsIgnoreCase(this.getNamespace()) && 
					o2.getName().equalsIgnoreCase(this.getName()) &&
					o2.getId().equalsIgnoreCase(this.getId())) {
				return true;
			}
		}
		return false;
	}
}
