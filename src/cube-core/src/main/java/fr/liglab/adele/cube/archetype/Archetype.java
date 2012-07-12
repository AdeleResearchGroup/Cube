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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.resolver.RConstraint;

/**
 * Cube Archetype.
 * @author debbabi
 *
 */
public class Archetype {
	
	private String id = "fr.liglab.adele.cube.myarchetype";
	private String name = "archetype";
	private String version = "1.0";
	private String description = "";
	
	/**
	 * key: shortcut
	 * value: full namespace
	 */
	Map<String, String> namespaces = new HashMap<String, String>();
	/**
	 * Associated CubeAgent.
	 */
	private CubeAgent cubeAgent;
	
	List<ManagedElement> types = new ArrayList<ManagedElement>();
	List<Constraint> constraints = new ArrayList<Constraint>();
	List<GlobalConfig> globalConfigs = new ArrayList<GlobalConfig>();
	
	/**
	 * key: variable id
	 * value: CVariable
	 */
	Map<String, Variable> variables = new HashMap<String, Variable>();
	
	/**
	 * Constructor. 
	 * @param cubeAgent
	 */
	public Archetype() {		
	}
	
	public Archetype(CubeAgent cubeAgent) {
		this.cubeAgent = cubeAgent;
	}
		
	public CubeAgent getCubeAgent() {
		return cubeAgent;
	}
	
	public void setCubeAgent(CubeAgent cubeAgent) {
		this.cubeAgent = cubeAgent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
	
	public List<ManagedElement> getTypes() {
		return this.types;
	}
	
	public void addType(ManagedElement cmo) {	
		if (cmo != null) {
			this.types.add(cmo);
			addExtensionNamespace(cmo.getNamespace());
		}
	}
	
	public boolean removeType(ManagedElement cmo) {
		return this.types.remove(cmo);
	}
	
	public ManagedElement getType(String namespace, String name, String id) {
		for (ManagedElement cmo : this.types) {
			if (cmo.getId().equalsIgnoreCase(id) && cmo.getName().equalsIgnoreCase(name) && cmo.getNamespace().equalsIgnoreCase(namespace)) {
				return cmo;
			}
		}
		return null;
	}
	
	public ManagedElement getType(String id) {
		if (id != null) {
			for (ManagedElement cmo : this.types) {
				if (cmo.getId().equalsIgnoreCase(id)) {
					return cmo;
				}
			}
		}
		return null;
	}
	
	public List<ManagedElement> getTypes(String id) {
		List<ManagedElement> result = new ArrayList<ManagedElement>();
		for (ManagedElement cmo : this.types) {
			if (cmo.getId().equalsIgnoreCase(id)) {
				result.add(cmo);
			}
		}
		return result;
	}
	
	
	public List<Constraint> getConstraints() {
		return this.constraints;
	}

	public List<Constraint> getConstraintsOnType(ManagedElement type) {
		List<Constraint> result = new ArrayList<Constraint>();
		for (Constraint cc: this.constraints) {
			/*List<CVariable> vars = getCVariables(cot);
			if (vars.size() > 0) {
				result.add(cc);
			}*/
		}
		return result;
	}
	public List<Constraint> getConstraintsRelatedToType(ManagedElement cot) {
		List<Constraint> result = new ArrayList<Constraint>();
		for (Constraint cc: this.constraints) {
			List<Variable> vars = getVariablesOfType(cot);
			if (vars.size() > 0) {
				result.add(cc);
			}
		}
		return result;
	}
	
	public void addConstraint(Constraint cmo) {
		this.constraints.add(cmo);		
	}
	
	public boolean removeConstraint(Constraint cmo) {
		return this.constraints.remove(cmo);
	}
	
	public Constraint getConstraint(String id) {
		for (Constraint cmo : this.constraints) {
			if (cmo.getId().equalsIgnoreCase(id)) {
				return cmo;
			}
		}
		return null;
	}
	
	public List<GlobalConfig> getGlobalConfigs() {
		return this.globalConfigs;
	}
	
	public List<GlobalConfig> getGlobalConfigs(String namespace) {
		List<GlobalConfig> result = new ArrayList<GlobalConfig>();
		for (GlobalConfig c : this.globalConfigs) {
			if (c.getNamespace().equalsIgnoreCase(namespace)) {
				result.add(c);
			}
		}
		return result;
	}
	
	public List<GlobalConfig> getGlobalConfigs(String namespace, String name) {
		List<GlobalConfig> result = new ArrayList<GlobalConfig>();
		for (GlobalConfig c : this.globalConfigs) {
			if (c.getNamespace().equalsIgnoreCase(namespace) && c.getName().equalsIgnoreCase(name)) {
				result.add(c);
			}
		}
		return result;
	}
	
	public void addGlobalConfig(GlobalConfig cmo) {
		this.globalConfigs.add(cmo);
		addExtensionNamespace(cmo.getNamespace());
	}
	
	public boolean removeGlobalConfig(GlobalConfig cmo) {
		return this.globalConfigs.remove(cmo);
	}
	
	public GlobalConfig getGlobalConfig(String id) {
		for (GlobalConfig cmo : this.globalConfigs) {
			if (cmo.getId().equalsIgnoreCase(id)) {
				return cmo;
			}
		}
		return null;
	}
	
	public void addExtensionNamespace(String namespace) {			
		if (!namespaces.containsKey(namespace)) {
			//TODO be attention if the namespace does not have dots!
			String nsxml = "";
			if (namespace.contains(".")) {
				nsxml = namespace.substring(namespace.lastIndexOf(".")+1, namespace.length());
			} else {
				nsxml = namespace;	
			}
			namespaces.put(namespace, nsxml);
		}	
	}
	
	public String getExtensionNamespaceShortcut(String namespace) {
		return this.namespaces.get(namespace);
	}
	
	public Set<String> getExtensionsNamespaces() {
		return this.namespaces.keySet();
	}
	
	public void addVariable(Variable v) {
		if (v != null) {
			v.setArchetype(this);
			this.variables.put(v.getId(), v);		
		}
	}
	
	public Variable getCVariable(String id) {
		if (id != null) {
			for (String key : this.variables.keySet()) {
				if (key.equalsIgnoreCase(id)) {
					return this.variables.get(key);
				}
			}
		}
		return null;
	}
	
	public Variable getVariable(String id, ManagedElement cot) {
		for (String key : this.variables.keySet()) {
			if (key.equalsIgnoreCase(id) && this.variables.get(key).getType().equals(cot)) {
				return this.variables.get(key);
			}
		}
		return null;
	}

	public Variable getVariable(String id) {
		for (String key : this.variables.keySet()) {
			if (key.equalsIgnoreCase(id)) {
				return this.variables.get(key);
			}
		}
		return null;
	}
	
	public List<Variable> getVariablesOfType(ManagedElement cot) {
		List<Variable> result = new ArrayList<Variable>();
		for (String key : this.variables.keySet()) {
			if (this.variables.get(key).getType().equals(cot.getId())) {
				result.add(this.variables.get(key));
			}
		}
		return result;
	}
	
	public List<Variable> getVariables() {
		List<Variable> result = new ArrayList<Variable>();
		for (String key : this.variables.keySet()) {			
			result.add(this.variables.get(key));			
		}
		return result;	
	}
	
	
	
	public Variable getCTypeVariable(ManagedElement cType) {
		for (Variable v : getVariablesOfType(cType)) {
			if (v.isTypeVariable() == true) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Return list of constraints that contains the given variable.
	 * 
	 * @param v
	 * @return
	 */
	public List<Constraint> getConstraintsContainsTheVariable(Variable v) {
		List<Constraint> result = new ArrayList<Constraint>();
		for (Constraint c : this.getConstraints()) {
			for (int j=0; j<c.getArity(); j++) {
				if (c.getParameter(j) != null && c.getParameter(j).equalsIgnoreCase(v.getId())) {
					result.add(c);
				}
			}
//			if (c instanceof UnaryConstraint) {
//				if (((UnaryConstraint)c).getV() != null && ((UnaryConstraint)c).getV().equalsIgnoreCase(v.getId())) {
//					result.add(c);
//				}
//			} else if (c instanceof BinaryConstraint) {
//				if (((BinaryConstraint)c).getV1() != null && ((BinaryConstraint)c).getV1().equalsIgnoreCase(v.getId()) ) {
//					result.add(c);
//				} 
//				if (((BinaryConstraint)c).getV2() != null && ((BinaryConstraint)c).getV2().equalsIgnoreCase(v.getId()) ) {
//					result.add(c);
//				}
//			}
		}
		return result;
	}

	public List<Constraint> getConstraintsContainsTheVariableTypeOf(String type) {
		List<Constraint> result = new ArrayList<Constraint>();
		List<Constraint> tmp = getConstraints();
		System.out.println("[WARNING] Archetype : getConstraintsContainsTheVariableTypeOf(String) is not yet implemented!");
//		for (Constraint p : tmp) {
//			
//			if (p instanceof UnaryConstraint) {
//				if (((UnaryConstraint)p).getV() != null && getVariable(((UnaryConstraint)p).getV()).isTypeVariable() && getVariable(((UnaryConstraint)p).getV()).getType().equals(type)) {
//					result.add(p);
//				}	
//			} else if (p instanceof BinaryConstraint) {
//				if (((BinaryConstraint)p).getV1() != null && getVariable(((BinaryConstraint)p).getV1()).isTypeVariable() && getVariable(((BinaryConstraint)p).getV1()).getType().equals(type)) {
//					result.add(p);
//				}	
//			}  
//						
//		}
		return result;
	}
	
	/**
	 * Get Constraints that has the given variable constrained.
	 * 
	 * @param v the constrained variable
	 * @return
	 */
	public List<Constraint> getConstraintsOnVariable(Variable v) {
		List<Constraint> result = new ArrayList<Constraint>();
		for (Constraint c : this.getConstraints()) {
			if (c.getParameter(0) != null && c.getParameter(0).equalsIgnoreCase(v.getId())) {
				result.add(c);
			}			
		}
		return result;
	}

}
