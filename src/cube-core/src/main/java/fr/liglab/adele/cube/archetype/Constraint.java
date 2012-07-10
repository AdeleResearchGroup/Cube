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

package fr.liglab.adele.cube.archetype;

import java.util.ArrayList;
import java.util.List;


/**
 * Archtype Constraint
 * 
 * @author debbabi
 */
public abstract class Constraint {
	
	private Archetype archtype;		
	private String id = "";	
	private String description = "";		
	
	/**
	 * 0 : high priority
	 * ++: lower priority
	 */
	int priority = 0;
	
	static int index = 0;
	
	/**
	 * Only the first parameter represents the constrained instance!
	 */
	private List<String> params = new ArrayList<String>();
	
	public abstract String getName();
	public abstract String getNamespace();
	public abstract int getArity();
	public abstract boolean isFindingConstraint();
	public abstract boolean isCheckingConstraint();
	public abstract boolean isPerformingConstraint();
	
	
	/*
	public CConstraint(String namespace, String name, String id, String description, int priority, Archetype archtype) {
		this(id, description, priority, archtype);
		this.name = name;
		this.namespace = namespace;
	}
	*/
	
	public Constraint(String id, String description, int priority, Archetype archtype, List<String> params) {
		this(id, description, priority, archtype);
		this.params = params;
	}
	public Constraint(String id, String description, int priority, Archetype archtype) {
		this.archtype = archtype;
		if (id != null) {
			this.id = id;
		} else {
			this.id = "__C" + index++; 
		}
		this.description = description;
		this.priority = priority;
	}
	
	public String getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public String getDescription() {
		return this.description;
	}
	
	public Archetype getArchtype() {
		return this.archtype;
	}
	
	public void addParameter(String param) {
		this.params.add(param);
	}
	
	public String getParameter(int i) {
		try {
			return this.params.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public String toXMLString(String xmlns) {
		String out = "";
		String tmp = "";
		for (int i=0; i<getArity(); i++) {
			tmp += "v"+i+"=\""+getParameter(i)+"\" ";
		}
				 
		out += "<"+xmlns+":"+getName()+" "+tmp+" p=\""+getPriority()+"\"/>\n";		
		return out;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Constraint) {
			Constraint o2 = (Constraint)obj;
			if (o2.getNamespace().equalsIgnoreCase(this.getNamespace()) && 
					o2.getName().equalsIgnoreCase(this.getName()) &&
					o2.getId().equalsIgnoreCase(this.getId())) {
				return true;
			}
		}
		return false;
	}

	
}
