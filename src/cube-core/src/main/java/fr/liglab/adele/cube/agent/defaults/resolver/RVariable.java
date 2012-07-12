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


package fr.liglab.adele.cube.agent.defaults.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.TimeOutException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.archetype.Variable;
import fr.liglab.adele.cube.util.id.CInstanceUID;

/**
 * Resolution Graph Variable.
 * 
 * @author debbabi
 *
 */
public class RVariable {
	/**
	 * Resolution Graph
	 */
	ResolutionGraph graph = null;
	/**
	 * Variable Type
	 */
	ManagedElement type = null;
	/**
	 * Variable Value
	 */
	RValue value = null;
	/**
	 * Variable history values
	 */
	Vector<RValue> valuesHistory = new Vector<RValue>();
	/**
	 * Variable Related Constraints (out arcs on the resolution graph) 
	 */
	List<RConstraint> constraints = new ArrayList<RConstraint>();
	/**
	 * Related Archetype Variables
	 */
	List<Variable> vars = new ArrayList<Variable>();
	/**
	 * Variable ID
	 */
	private String id;	
	static int index = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param graph
	 * @param type
	 * @param initialValue
	 */
	public RVariable(ResolutionGraph graph, ManagedElement type, RValue initialValue) {
		this.graph = graph;
		this.type = type;		
		this.id = "x" + index++;
		if (initialValue != null) {
			this.value = initialValue;
			this.value.setVariable(this);
		}
		// set variable
		//System.out.println("** gettting variables from the archetype of type " + type.getId());
		List<Variable> vv = graph.getCubeAgent().getArchetype().getVariablesOfType(type);
		//System.out.println("** types " + vv.size());
		for (Variable v : vv) {
			this.vars.add(v);
		}
	}

	/**
	 * Get Resolution Graph.
	 * 
	 * @return
	 */
	public ResolutionGraph getResolutionGraph() {
		return this.graph;
	}
	
	/**
	 * Is this Variable Null?
	 * @return true if no value is already affected. 
	 */
	public boolean isNull() {		
		return this.value == null || this.value.getInstance() == null;
	}

	/**
	 * Get the current value
	 * 
	 * @return
	 */
	public RValue getRValue() {
		return this.value;
	}
	
	/**
	 * Set Resolution Value.
	 * Add the old value to the history values.
	 * 
	 * @param value
	 */
	public void setRValue(RValue value) {
		if (this.value != null) {
			valuesHistory.add(this.value);
		}
		this.value = value;
	}
	
	/**
	 * 1. if null, 
	 *       add finding constraints
	 *       for each constraint c
	 *           
	 * @return
	 */
	public boolean validate() {
		if (this.graph.debug() == true) {
			System.out.println("\n ******************************************************** ");
			System.out.println(" ***** Validating the variable " + getId() + "(" + getType().getId()+")");
			System.out.println(" ******************************************************** \n");			
		}
		
		if (this.isNull() == true) {
			if (this.graph.debug() == true) {
				System.out.println("\n The variable "+getId() + "(" + getType().getId()+") is NULL.");
				System.out.println(" We will first tries to find a candidate value.");
			}
			if (findValue() == false) {
				return false;
			} else {
				return validate();
			}
		} else {
			if (this.value.getState() == CInstance.UNRESOLVED) {
				if (this.graph.debug() == true) {
					System.out.println("\n The variable "+getId() + "(" + getType().getId()+") is UNRESOLVED.");
					System.out.println(" We will tries to resolve it.");
				}	
				if (resolve2() == false) {
					if (this.graph.debug() == true) {
						System.out.println("\n The variable "+getId() + "(" + getType().getId()+") could not be resolved!");
					}
					return false;
				} else {
					// TODO validate instances hierarchy..
					if (this.graph.debug() == true) {
						System.out.println("\n The variable "+getId() + "(" + getType().getId()+") is resolved.");
					}
					_validate();
					return true;
				}
				
				
			} else if (this.value.getState() == CInstance.VALID) {
				// jamais on arrivera ici!
			}
		}
		return true;
	}
	
	private void _validate() {
		if (getRValue() != null) {
			if (getRValue().getState() == CInstance.UNRESOLVED) {
				this.getRValue().setState(CInstance.VALID);
			}
			for (RConstraint c : getPerformingConstraints()) {
				if (c.getRelatedVariable() != null) {				
					c.getRelatedVariable()._validate();
				}
			}
		}
	}
	
	/**
	 * Find value for this variable from its related finding constraints.
	 * 
	 * @return false if no new value is found
	 */
	private boolean findValue() {	
		if (this.graph.debug() == true) {
			System.out.println("\n >> finding value for the variable "+ getId()+"(" +getType().getId()+") ...\n");
		}
		if (this.graph.debug() == true) {
			System.out.println("\n - adding finding constraints...");
		}
		addFindingConstraints();
		for (RConstraint c : getFindingConstraints()) {			
			RValue newValue = c.findValueForConstrainedVariable();
			if (newValue != null) {
				this.setRValue(newValue);
				return true;
			}			
		}		
		return false;
	}
	
	
	/**
	 * Resolve this variable with the current value.
	 * 
	 * @return false if not resolved with this current value
	 */
	private boolean resolve2() {
		if (this.graph.debug() == true) {
			System.out.println("\n-----------------------------------------------------------");
			System.out.println(" R E S O L V I N G the variable " + getId() + "(" + getType().getId()+")");
			System.out.println("-----------------------------------------------------------\n");
		}		
		//
		// perform constraints and check!
		//
		if (this.graph.debug() == true) {
			System.out.println("\n - adding performing constraints...");
		}
		addPerformingConstraints();
		//
		// initialize related variables values from the direct related constraints
		//
		if (this.graph.debug() == true) {
			System.out.println("\n - adding finding constraints...");
		}
		addFindingConstraints();
		if (this.graph.debug() == true) {
			System.out.println("\n - initializing the related variables values using the Finding Constraints...");
		}
		for (RConstraint c : getFindingConstraints()) {			
			c.findValueForRelatedVariable();
		}	
		// resolving constraints..
		for (RConstraint c : getPerformingConstraints()) {
			//
			// all constraints should be resolved!
			//			
			if (resolveConstraint(c) == false) {
				return false;
			} 
		}
		return true;
	}

	/**
	 * Resolve Constraint.
	 * 
	 * Apply changes and check if it remains valid
	 * 
	 * @param c
	 * @return
	 */
	private boolean resolveConstraint(RConstraint c) {
		if (this.graph.debug() == true) {
			System.out.println("\n >>> resolving the constraint "+c.getConstraint().getName()+" ...\n");
		}
		if (c.getConstraint().getArity() == 1) {			
			c.perform2();	
			return true;
		} else if (c.getConstraint().getArity() == 2) {
			if (c.getRelatedVariable().isNull() == true) {
				if (this.graph.debug() == true) {
					System.out.println(" When resolving the constraint "+c.getConstraint().getName()+", we found that the related variable "+c.getRelatedVariable().getId() + "(" +c.getRelatedVariable().getType().getId()+") is NULL.\n We will try first to find a candidate value for it.");
				}				
				//
				// find value for related variable if null
				//				
				if (c.getRelatedVariable().findValue() == false) {
					return false;
				} else {
					return resolveConstraint(c);
				}
			} else {				
				c.perform2();
				if (c.getRelatedVariable().check2() == false) {									
					c.cancel2();
					if (c.getRelatedVariable().findValue() == false) {
						return false;
					} else {
						return resolveConstraint(c);
					}
				} else {
					return true;
				}
			}
			
		}
		return true;
	}


	private boolean check2() {
		if (this.graph.debug() == true) {
			System.out.println("\n >> checking the variable "+ getId()+"(" +getType().getId()+") ...\n");
		}
		
		if (this.getRValue() != null && this.getRValue().getOriginalState() == CInstance.UNRESOLVED) {
			if (this.graph.debug() == true) {
				System.out.println("\n the original state of this variable's value was UNRESOLVED. So, we will first start by resolving it too.");
			}	
			if (this.resolve2() == false) {
				return false;
			} 
		}
		if (this.graph.debug() == true) {
			System.out.println("\n - adding checking constraints...");
		}
		addCheckingConstraints();		
		if (this.graph.debug() == true) {
			System.out.println("\n - adding finding constraints...");
		}
		addFindingConstraints();
		if (this.graph.debug() == true) {
			System.out.println("\n - initializing the related variables values using the Finding Constraints...");
		}
		for (RConstraint c : getFindingConstraints()) {			
			c.findValueForRelatedVariable();
		}	
		for (RConstraint c : getCheckingConstraints()) {
			if (c.check2() == false) {
				return false;
			} 
		}		
		// TODO Auto-generated method stub
		return true;
		
	}

	/**
	 * Get Finding Constraints among its direct related constraints.
	 * 
	 * @return
	 */
	private List<RConstraint> getFindingConstraints() {
		List<RConstraint> result = new ArrayList<RConstraint>();
		for (RConstraint c : this.constraints) {
			if (c.getConstraint().isFindingConstraint() == true) {
				result.add(c);
			}					
		}
		Collections.sort(result);
		return result;
	}
	
	private List<RConstraint> getPerformingConstraints() {
		List<RConstraint> result = new ArrayList<RConstraint>();
		for (RConstraint c : this.constraints) {
			if (c.getConstraint().isPerformingConstraint() == true) {
				result.add(c);
			}					
		}
		Collections.sort(result);
		return result;
	}
	
	private List<RConstraint> getCheckingConstraints() {
		List<RConstraint> result = new ArrayList<RConstraint>();
		for (RConstraint c : this.constraints) {
			if (c.getConstraint().isCheckingConstraint() == true) {
				result.add(c);
			}					
		}
		Collections.sort(result);
		return result;
	}
	
	/**
	 * Adding the direct related Finding Constraints to this actual Resolultion Variable.
	 */
	private void addFindingConstraints() {		
		for (Variable v : getArchetypeVariables()) {			
			List<Constraint> constraints = v.getArchetype().getConstraintsOnVariable(v);			
			for (Constraint cp : constraints) {
				if (cp.isFindingConstraint()) {
					this.addRConstraint(cp);														
				}
			}
		}		
	}

	/**
	 * Adding the direct related Performing Constraints to this actual Resolultion Variable.
	 */
	private void addPerformingConstraints() {
		for (Variable v : getArchetypeVariables()) {			
			List<Constraint> constraints = v.getArchetype().getConstraintsOnVariable(v);			
			for (Constraint cp : constraints) {
				if (cp.isPerformingConstraint()) {					
					this.addRConstraint(cp);										
				}
			}
		}		
	}
	
	/**
	 * Adding the direct related Checking Constraints to this actual Resolultion Variable.
	 */
	private void addCheckingConstraints() {
		for (Variable v : getArchetypeVariables()) {			
			List<Constraint> constraints = v.getArchetype().getConstraintsOnVariable(v);			
			for (Constraint cp : constraints) {
				if (cp.isCheckingConstraint()) {					
					this.addRConstraint(cp);										
				}
			}
		}		
	}
	
	/**
	 * Resolve this Variable.
	 * 
	 * 1. First, retrieve its related constraints from the archetype.
	 * 2. Second, resolve its constraints one after another. Notice here that if one of its constraints 
	 * goes wrong, the actual variable is unresolved. And notice also that when resolving a constraint
	 * all the possibilities are verified. so when not resolved this mean no solution was found.
	 * 3. check the actual variable if it is valid. Otherelse, unresolve it (back to initial state).
	 * 
	 * @return true if resolved
	 */
	public boolean resolve() {
		if (this.graph.debug() == true) {
			System.out.println("\n[CSPV] ********************************* ");
			System.out.println("[CSPV] ***** resolving the variable " + getId() + "(" + getType().getId()+")");
			System.out.println("[CSPV] ********************************* \n");
			
			// build related predicates if does not already created!
			System.out.println("\n[CSPV] (1) -- build related constraints if does not already created! ---- " + getId() + "\n");
		}
		
		buildRelatedConstraints();
		
		for (RConstraint c : getRConstraints()) {
			if (this.graph.debug()) {
				System.out.println("\n+------------------------------------------------");
				System.out.println("+ resolving the constraint: " + c.getConstraint().getName());
				System.out.println("+------------------------------------------------\n");
			}
			if (c.resolve() == false) {
				if (this.graph.debug()) {
					System.out.println("[CSPV] . could not resolve the predicate " + c.getConstraint().getName() + " for the variable " + getId());
				}
				graph.setLastUnresolvedConstraint(c);
				unresolve();
				return false;
			} 						
		}
		if (this.graph.debug()) {
			System.out.println("[CSPV] . all the predicates of the variable " + getId() + " are resolved! ");
		}
		
		if (this.check() == true) {
			if (this.graph.debug()) {
				System.out.println("[CSPV] . all the predicates of the variable " + getId() + " are checked! this variable will become VALID!");
			}
			return true;
		} else {
			if (this.graph.debug()) {
				System.out.println("[CSPV] . not all the predicates of the variable " + getId() + " are checked! this variable will remains UNRESOLVED!");
			}
			unresolve();
			//getCSP().getLogger().debug("The variable " + toString() + " is not resolved!");
			return false;
		}				
		
	}

	/**
	 * Check the variable if all its related constraints are true.
	 * 
	 * @return
	 */
	public boolean check() {
		if (isNull() == false) {
			if (this.value.getInstance().isLocal(this.graph.getCubeAgent().getId()) == true) {
				if (this.graph.debug()) {
					System.out.println("[CSPV] . checking the local instance " + toString());
				}
				if (this.getDirectSubRVariables().size() == 0) {
					buildRelatedConstraints();
					for (RConstraint p : getRConstraints()) {
						p.findValueForV2();
					}
				}			
				for (RConstraint p : getRConstraints()) {
					if (this.value != null && this.value.getOriginalState() == CInstance.VALID) {
						if (p.isUnaryConstraint() && p.check() == false) {
							if (this.graph.debug()) {
								System.out.println("[CSPV] . checking the variable " + getId() + " returns FALSE");
							}
							if (this.value.getOriginalState() == CInstance.UNRESOLVED) {
								this.value.setState(CInstance.UNRESOLVED);
							} else if (this.value.getOriginalState() == CInstance.VALID) {
								this.value.setState(CInstance.UNCHECKED);
							}
							//getCSP().getLogger().debug("The predicate " + p.toString() + " is not resolved!");
							this.graph.setLastUnresolvedConstraint(p);
							return false;
						}  
					} else if (this.value != null && this.value.getOriginalState() == CInstance.UNRESOLVED) {
						if (p.check() == false) {
							if (this.graph.debug()) {
								System.out.println("[CSPV] . checking the variable " + getId() + " returns FALSE");
							}
							if (this.value.getOriginalState() == CInstance.UNRESOLVED) {
								this.value.setState(CInstance.UNRESOLVED);
							} else if (this.value.getOriginalState() == CInstance.VALID) {
								this.value.setState(CInstance.UNCHECKED);
							}
							//graph.getLogger().debug("The predicate " + p.getConstraint().toString() + " is not resolved!");
							graph.setLastUnresolvedConstraint(p);
							return false;
						}
					}						
				}
			} else {
				if (this.graph.debug()) {
					System.out.println("[CSPV] . checking the remote instance " + toString());
				}
				/*
				 * check remote instance!
				 */						
				CMessage msg = new CMessage();
					
				msg.setTo(this.value.getInstance().getCubeAgentID().toString() + "/lam");
				msg.setFrom(graph.getId().toString());
				msg.setReplyTo(graph.getId().toString());				
				
				msg.addHeader(ResolutionGraph.MSG_CSP_OP, ResolutionGraph.MSG_CSP_CHECKV);
				String constraintID = "";
				if (getArchetypeVariables().size() > 0) {
					//constraintID = //getCVariables().get(0).getConstraint().getId();	
					System.out.println("\n\n[WARNING] RVariable.check does not add the constraint id to the message!");
				}
				//msg.addHeader(CSP.MSG_CSP_CONSTRAINT_ID, constraintID);
				String varstmp = "";
				for (Variable cv : this.vars) {
					varstmp += cv.getId() + "@";
				}
				msg.addHeader(ResolutionGraph.MSG_CSP_VARS, varstmp);
				msg.addHeader(ResolutionGraph.MSG_CSP_VAR_VALUE, this.value.getInstance());
				
				//msg.setHeaderProperty(name, msg)							
				//String body = CSP.MSG_CSP_CHECKV + "\n";
				//body += getCVariable().getConstraint().getId() + "\n";
				//body += getCVariable().getId() + "\n";
				//body += getCSPValue().getValue() + "\n";						
				//msg.setBody(body);
				
				try {					
					//System.out.println("[CSPV] checking remote .." + msg.toString());
					CMessage result = this.graph.sendAndWait(msg);					
					if (result != null) {											
						String rr = result.getBody().toString();						
						if (rr != null) {							
							String[] tmp = rr.split("\n");
							String checkresult = tmp[1];							
							if (checkresult.trim().equalsIgnoreCase("false")) {				
								//System.out.println("[CSPV] checking remote FALSE!!!!");								
								return false;
							}							
						}
					}
				} catch (TimeOutException toex) {				
					toex.printStackTrace();
					return false;
				}
			}
		} else {
			if (this.graph.debug()) {
				System.out.println("[CSPV] . checking NULL instance not possible! * " + toString());
			}
		}
		if (this.graph.debug()) {
			System.out.println("[CSPV] . checking the variable " + getId() + " returns TRUE");
		}
		this.value.setState(CInstance.VALID);
		return true;
	}

	/**
	 * Un-resolve the variable.
	 */
	private void unresolve() {
		for (RConstraint c : getRConstraints()) {
			c.cancel();
		}
	}
	
	/**
	 * Find Value for this variable from its related Constraints.
	 * 
	 * @return
	 */
	public boolean findvalueeeeold() {
		if (this.graph.debug() == true) {
			System.out.println("\n >>> finding value for the variable "+ getId()+"(" +getType().getId()+") ...");
		}
		RValue oldvalue = getRValue();
		setRValue(null); // implies: addCurrentValueToHistory();
		buildRelatedConstraints(); // if not already created!	
		
		//List<CSPPredicate> toBeRemoved= new ArrayList<CSPPredicate>();
		//boolean found = false;
		for (RConstraint p : getRConstraints()) {
			//if (found == false) {
				if (p.findValueForV2() == true) {
					
					//System.out.println("ahum ahum ahum");
					if (oldvalue != null && getRValue().equals(oldvalue)) {
						//System.out.println("[CSPV] ***** finding value for " + getId() + " : no new value found!");
						continue;
					} else {
						if (this.graph.debug()) {
							System.out.println("[CSPV] ***** finding value for " + getId() + " : " + getRValue().toString());
						}
						initRelatedVars();
						return true;
					}
					//found = true;
				} else {
					
				}
		}
		/*for(CSPPredicate p : toBeRemoved) {
			p.getConstrainedCSPVariable().removeCSPPredicate(p);
		}*/
		if (this.graph.debug()) {
			System.out.println("[CSPV] ***** findinding value for " + getId() + " : NOT FOUND!");
		}
		return false;
	}
	
	/**
	 * Find Values for related direct variables using the constraint it self.
	 */
	private void initRelatedVars() {
		buildRelatedConstraints(); // if not already created!	
		for (RConstraint p : getRConstraints()) {
			if (p.isBinaryConstraint()) {
				if (p.findValueForV2() == true) {
					if (this.graph.debug()) {
						System.out.println("[CSPV] . when founding value for the variable " + getId() + ", we have initialized the related variable " + p.getRV2().toString());
					}
				}
			}
		}
	}

	/**
	 * Is the given instance is in the history?
	 * 
	 * @param instance
	 * @return true if already saved on the history list.
	 */
	public boolean isInHistory(CInstanceUID instance) {
		for (RValue v : this.valuesHistory) {
			if (v.getInstance() != null && v.getInstance().equals(instance)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get Value from the history list.
	 * 
	 * @param i
	 * @return
	 */
	public RValue getHistoryValue(int i) {
		if (index >=0 && index < this.valuesHistory.size()) {
			return this.valuesHistory.get(index);
			}
			return null;
	}

	/**
	 * Get History Values.
	 * 
	 * @return
	 */
	public List<RValue> getHistoryValues() {
		return this.valuesHistory;
	}
	
	/**
	 * Get archetype variables of this resolution graph variable.
	 * @return
	 */
	public List<Variable> getArchetypeVariables() {
		return this.vars;
	}
		
	/**
	 * Get all direct sub variables of this variable.
	 * 
	 * @return
	 */
	public List<RVariable> getDirectSubRVariables() {
		List<RVariable> result = new ArrayList<RVariable>();		
		for (RConstraint r : this.getRConstraints()) {
			if (r.getRV2() != null) {
				result.add(r.getRV2());						
			}
		}
		return result;
	}
	
	/**
	 * Get all the sub variables from this resolution graph node (variable)
	 * until the leaves.
	 * 
	 * @return
	 */
	public List<RVariable> getAllSubRVariables() {
		List<RVariable> result = new ArrayList<RVariable>();		
		for (RConstraint r : this.getRConstraints()) {
			if (r.getRV2() != null) {
				result.add(r.getRV2());
				for (RVariable v : r.getRV2().getAllSubRVariables()) {
					result.add(v);
				}				
			}
		}
		return result;
	}
	
	/**
	 * Get all the constraints of this variable.
	 * @return
	 */
	public List<RConstraint> getRConstraints() {
		Collections.sort(this.constraints);		
		return this.constraints;
	}
	
	/**
	 * Get the resolution graph constraint that encapsulates the given archetype constraint.
	 *  
	 * @param c
	 * @return
	 */
	public RConstraint getRConstraint(Constraint c) {
		for (RConstraint rc : this.constraints) {
			if (rc.getConstraint() != null && rc.getConstraint().equals(c)) {
				return rc;
			}
		}
		return null;
	}
	
	/**
	 * Add the related constraints of this variable.
	 * 
	 * They are found on the archetype, 
	 */
	private void buildRelatedConstraints() {				
		System.out.println("[CSPV] Retrieve Related Constraints of "+getId()+"...");
		if (this.constraints.size() <= 0) {
			System.out.println("[CSPV] "+getId()+" has no constraints yet!");
			for (Variable v : getArchetypeVariables()) {
				System.out.println("// getting constraints related to variable " + v.getId());
				List<Constraint> predicates = v.getArchetype().getConstraintsOnVariable(v);
				if (predicates.size() == 0) {
					System.out.println("// no constraints! ");
				}
				for (Constraint cp : predicates) {
					/*
					 * add the predicates to the variable.
					 * this will also adds the related variables
					 */
					if (getRConstraint(cp) == null) {
						RConstraint cspp = new RConstraint(graph, cp, this);
						this.addRConstraint(cspp);
						if (cspp.getRV2() != null) {
							if (this.graph.debug()) {
								System.out.println("[CSPV] + predicate: " + cspp.getConstraint().getName() + "("+cspp.getRV2().getId()+")");
							}
						} else {
							if (this.graph.debug()) {
								System.out.println("[CSPV] + predicate: " + cspp.getConstraint().getName() + "()");
							}
						}						
					}
				}
				// adding also predicate of type variables
				// but do not replace exisings
				List<Constraint> predicates2 = v.getArchetype().getConstraintsContainsTheVariableTypeOf(v.getType());
				for (Constraint cp : predicates2) {
					if (this.getRConstraint(cp) == null) {
						RConstraint cspp = new RConstraint(graph, cp, this);					
						this.addRConstraint(cspp);
						
						if (cspp.getRV2() != null) {
							if (this.graph.debug()) {
								System.out.println("[CSPV] * predicate: " + cspp.getConstraint().getName() + "("+cspp.getRV2().getId()+")");
							}
						} else {
							if (this.graph.debug()) {
								System.out.println("[CSPV] * predicate: " + cspp.getConstraint().getName() + "()");
							}
						}
					}					
				}
			}
		} else {
			System.out.println("// This variable already has constraintes! " + this.constraints.size());
		}
	}	
	
	/**
	 * Add Graph Resolution Constraint to this Resolution Variable.
	 * 
	 * @param c
	 */
	public synchronized void addRConstraint(Constraint c) {
		if (c != null) {
			if (this.graph.debug()) {
				System.out.println("   + adding constraint " + c.getName() + " to the variable " + getId() + "(" + getType().getId() + ")");
			}
			synchronized (this.constraints) {
				if (getRConstraint(c) == null) {
					RConstraint cspp = new RConstraint(graph, c, this);
					this.addRConstraint(cspp);	
				}				
			}
		}
	}
	
	/**
	 * Add Graph Resolution Constraint to this Resolution Variable.
	 *   
	 * @param cspp
	 */
	public synchronized void addRConstraint(RConstraint cspp) {
		if (cspp != null) {					
			synchronized (this.constraints) {				
				this.constraints.add(cspp);	
			}			
		}
	}

	/**
	 * Get this variable Id.
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get Archetype type.
	 * @return
	 */
	public ManagedElement getType() {
		return type;
	}

	@Override
	public String toString() {
		String out = "";
		if (getRValue() != null) {
			out = getId() + "(" + getRValue() + ")";
		} else {
			out = getId() + "( null )";
		}
		return out;
	}



}
