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
import java.util.List;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.archetype.Variable;
import fr.liglab.adele.cube.extensions.IConstraintResolver;
import fr.liglab.adele.cube.extensions.IExtension;

/**
 * Resolution Graph Constraint.
 * 
 * @author debbabi
 *
 */
public class RConstraint implements Comparable<RConstraint>{

	private static final int MAX_TENTATIVES = 5;
	/**
	 * Resolution Graph
	 */
	ResolutionGraph graph;
	/**
	 * Reference to the Archetype Constraint
	 */
	Constraint constraint;
	/**
	 * Constraint Variable
	 */
	RVariable v1 = null;
	/**
	 * Related Variable
	 */
	RVariable v2 = null;
		
	/**
	 * Constructor.
	 * 
	 * @param graph
	 * @param constraint
	 * @param constrainedVar
	 */
	public RConstraint(ResolutionGraph graph, Constraint constraint, RVariable constrainedVar) {
		if (constraint != null) {
			this.graph = graph;
			this.constraint = constraint;
			this.v1 = constrainedVar;
												
			if (constraint.getArity() == 2) {
						
				String v2id = constraint.getParameter(1);
				RVariable rv2 = graph.getRVariableByVarName(v2id); 
				if (rv2 != null) {
					this.v2 = rv2;
				} else {
					Variable v = constraint.getArchtype().getVariable(v2id);
					ManagedElement t = constraint.getArchtype().getType(v.getType());
					if (t != null) {
						RVariable cspvv = new RVariable(graph, t, null);
						this.v2 = cspvv;
					} else {
						System.out.println("[ERROR] RConstraint("+getConstraint().getName()+") : type " + v2id + " is not found on the archetype!");
					}
				}								
			}			
		}
	}
	
	/**
	 * @deprecated
	 * Resolve the Constraint.
	 * 
	 * 1. if it is a binary constraint: first, we should find a value for v2 if it is null.
	 * 2. perform the constraint, and check if it is true.
	 * 
	 * @return true if resolved
	 */
	public boolean resolve() {
		if (isBinaryConstraint()) {
			if (findValueForV2() == true) {
				if (this.graph.debug()) {
					System.out.println("[CSPP] . when resolving the predicate " + getConstraint().getName() + ", the value of the related variable is found directly from the constrained variable!");
				}
			} else {
				if (this.graph.debug()) {
					System.out.println("[CSPP] . the related variable "+v2.getId()+" is NULL, we will tries to find a value for it!");
				}
				if (findValue() == false) {
					return false;
				}				
			}	
			int counter = 0;
			while (performAndCheck()==false) {
				if (counter >= MAX_TENTATIVES) {
					if (this.graph.debug()) {
						System.out.println("[CSPP] . when resolving the predicate " + getConstraint().getName() + ", we have reached the MAX TENTATIVES counter to resolve this predicate!");
					}
					return false;
				}
				counter ++;
				if (findValue() == false) {
					if (this.graph.debug()) {
						System.out.println("[CSPP] . when resolving the predicate " + getConstraint().getName() + ", no value was found for the related variable " + getRV2().getId());
					}
					return false;
				} else {
					counter = 0;
				}				
			}
			return true;
		} else if (isUnaryConstraint()) {
			perform();
			return true;
		}	
		return true;
	}

	/**
	 * @deprecated
	 * Perform the constraint and Check the related variable.
	 * 
	 * @return
	 */
	private boolean performAndCheck() {
		perform();
		if (getRV2().check() == true) {
			if (this.graph.debug()) {
				System.out.println("[CSPP] . after performing the predicate " + getConstraint().getName() +", the related variable remains VALID! so this predicate is resolved!");
			}
			return true;
		} else {
			if (this.graph.debug()) {
				System.out.println("[CSPP] . after performing the predicate " + getConstraint().getName() +", the related variable is UNRESOLVED! so this predicate will be canceled, and we will look for a new value for the related variable!");
			}
			cancel();	
			return false;
		}
	}
	/**
	 * @deprecated
	 * Perform the constraint on the variables.
	 * 
	 */
	void perform() {
		if (v1.getRValue().getOriginalState() == CInstance.UNRESOLVED) {
			if (this.graph.debug()) {
				System.out.println("[CSPPredicate] performing predicate " + getConstraint().getName());
			}
			try {
				
				if (this.constraint.getArity() == 1) {
					if (this.graph.debug()) {
						System.out.println("("+getRV1().toString()+")");
					}
									
					IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
					IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
					if (cr != null) {
						List<RVariable> params = new ArrayList<RVariable>();
						params.add(v1);
						cr.perform(params, this.constraint);
					}
					
				} else if (this.constraint.getArity() == 2) {
					if (this.graph.debug()) {
						System.out.println("("+getRV2().toString()+"|"+getRV2()+")");
					}
					if (v2.isNull()) {
						if(findValueForV2()==false) {
							v2.setRValue(v2.getHistoryValue(0));
							if (v2.isNull()) {
								if (v2.findvalueeeeold() == false) {
									throw new Exception("[CSPP.perform] could not perform this predicate because the related variable is null!;");
								} 								
							}
						}
					}
					_perform();			
				}				
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		
		if (isBinaryConstraint()) {
			for (RConstraint p: v2.getRConstraints()) {
				p.perform();
			}
		}
	}
	
	/**
	 * @deprecated
	 * 
	 * Call the Constraint Resolver perform operation.
	 * 
	 */
	private void _perform() {
		if (!v2.isNull()) {
			if (v2.getRValue().getInstance().isLocal( graph.getCubeAgent().getId()) == true ) {
				
				
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);
					params.add(v2);
					cr.perform(params, this.constraint);
				}								
				
			} else {
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);
					params.add(v2);
					cr.perform(params, this.constraint);
				}
													
				System.out.println("[RConstraint] _perform does not yet implement the remote perform operation!");
				
				/*
				CInstanceID id2 = v2.getRValue().getInstance();
				CMessage msg = new CMessage();
				msg.setTo(id2.getCubeAgentID().toString() + "/archtype/" + getConstraint().getId().getPath());
				msg.setFrom(getCSP().getId().toString());
				msg.setReplyTo(getCSP().getId().toString());				
				String body = CSP.MSG_CSP_PERFORM + "\n";
				body += ((CBinaryPredicate)this.predicate).getConstrainedVariable().getId() + "\n";
				body += v1.getCSPValue().getValue().toString() + "\n";
				body += ((CBinaryPredicate)this.predicate).getRelatedVariable().getId() + "\n";				
				body += v2.getCSPValue().getValue().toString() + "\n";				
				msg.setBody(body);
				try {					
					//System.out.println("[CSPP] _performing "+getName()+" ... " + msg.toString());
					CMessage result = getCSP().sendAndWait(msg);						
					if (result != null) {
					}
				} catch (TimeOutException toex) {				
					toex.printStackTrace();
					//return null;
				}*/
			}
		}
		//return null;	
	}

	/**
	 * @deprecated
	 * Cancel the constraint.
	 */
	public void cancel() {
		if (v1.isNull() == false) {
			if (v1.getRValue().getOriginalState() == CInstance.UNRESOLVED) {
				if (this.graph.debug()) {
					System.out.println("[CSPPredicate] canceling predicate " + getConstraint().getName());
				}
				try {
					if (isUnaryConstraint()) {
						IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
						IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
						if (cr != null) {
							List<RVariable> params = new ArrayList<RVariable>();
							params.add(v1);
							params.add(v2);
							cr.cancel(params, this.constraint);
						}									
					} else if (isBinaryConstraint()) {
						_cancel();
					}
				} catch (Exception e) {				
					e.printStackTrace();
				}
			}
			if (isBinaryConstraint()) {
				for (RConstraint p: v2.getRConstraints()) {
					p.cancel();
				}
			}
		}
	}

	/**
	 * @deprecated
	 * Call the Constraint Resolver cancel operation.
	 */
	private void _cancel() {
		if (!v2.isNull()) {
			if (v2.getRValue().getInstance().isLocal(graph.getCubeAgent().getId()) == true) {
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);
					params.add(v2);
					cr.cancel(params, this.constraint);
				}				
			} else {
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);
					params.add(v2);
					cr.cancel(params, this.constraint);
				}								
				System.out.println("[RConstraint] _cancel does not yet implement the remote cancel operation!");
				
				/*
				CInstanceID id2 = v2.getRValue().getInstance();				
				CMessage msg = new CMessage();
				msg.setTo(id2.getCubeInstanceID().toString() + "/archtype/" + getPredicate().getId().getPath());
				msg.setFrom(getCSP().getId().toString());
				msg.setReplyTo(getCSP().getId().toString());				
				String body = CSP.MSG_CSP_CANCEL + "\n";
				body += ((CBinaryPredicate)this.predicate).getConstrainedVariable().getId() + "\n";
				body += v1.getCSPValue().getValue().toString() + "\n";
				body += ((CBinaryPredicate)this.predicate).getRelatedVariable().getId() + "\n";				
				body += v2.getCSPValue().getValue().toString() + "\n";				
				msg.setBody(body);
				try {					
					CMessage result = getCSP().sendAndWait(msg);						
					if (result != null) {
						
					}
				} catch (TimeOutException toex) {				
					toex.printStackTrace();
					//return null;
				}*/
			}
		}
	}

	/**
	 * @deprecated
	 * Find Value for the variables.
	 * 
	 * @return
	 */
	private boolean findValue() {
		RValue oldvalue = getRV2().getRValue();
		// looking for a new value for the related variable..
		getRV2().findvalueeeeold();
		if (getRV2().isNull()) {
			if (this.graph.debug()) {
				System.out.println("[CSPP] . after looking for a value for the related variable " + getRV2().getId() +", no value was found!");
			}
			return false;
		} else {
			if (oldvalue != null && getRV2().getRValue().equals(oldvalue)) {
				if (this.graph.debug()) {
					System.out.println("[CSPP] . after looking for a value for the related variable " + getRV2().getId() +", the found value is the same as before!");
				}
				return false;
			} else {
				if (getRV2().isInHistory(getRV2().getRValue().getInstance())) {
					if (this.graph.debug()) {
						System.out.println("[CSPP] . after looking for a value for the related variable " + getRV2().getId() +", the found value is already returned before (it is in the history)!");
					}
					return false;
				} else {					
					// new value!	
					if (this.graph.debug()) {
						System.out.println("[CSPP] . after looking for a value for the related variable " + getRV2().getId() +", wa have found a new value!");
					}
					return true;					
				}
			}
		}
	}	
	
	public boolean findValue(int which) {
		try {				
			IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
			IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
			if (cr != null) {
				List<RVariable> params = new ArrayList<RVariable>();
				params.add(v1);
				params.add(v2);					
				RValue value = cr.find(params, 1, this.constraint);
				if (value != null) {
					//System.out.println("[CSPPredicate] warning: findValueForRelatedVariable puts directly the found value as v1 value!");
					this.v2.setRValue(value);
					return true;
				}
			}
		} catch (Exception e) {				
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Find value for the related variable.
	 */
	public boolean findValueForV2() {
		if (isBinaryConstraint()) {
			try {				
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);
					params.add(v2);					
					RValue value = cr.find(params, 1, this.constraint);
					if (value != null) {
						//System.out.println("[CSPPredicate] warning: findValueForRelatedVariable puts directly the found value as v1 value!");
						this.v2.setRValue(value);
						return true;
					}
				}
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @deprecated
	 * Check the constraint.
	 * @return
	 */
	public boolean check() {
		if (this.graph.debug()) {
			System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + "...");
		}
		try {
			if (isUnaryConstraint()) {			
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);			
					boolean result = cr.check(params, this.constraint);
					if (this.graph.debug()) {
						System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : " + result);
					}				
					return result;
				}
			} else if (isBinaryConstraint()) {	
				
				if (v2.getRValue() == null) {
					if (this.graph.debug()) {
						System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : related variable null!");
					}
					return true;
				} else {
					if (v2.getRValue().getOriginalState() == CInstance.UNRESOLVED) {						
						for (RConstraint pp: v2.getRConstraints()) {
							pp.findValueForV2();
							pp.perform();
						}						
						if (v2.check() == true) {								
							boolean result = _check();
							if (this.graph.debug()) {
								System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : " + result);
							}
							return result;
						} else {
							if (this.graph.debug()) {
								System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : related variable not checked!");
							}
							return false;					
						}																
					} else if (v2.getRValue().getOriginalState() == CInstance.VALID) {
						if (v2.check() == true) {				
							
							boolean result = _check();
							if (this.graph.debug()) {
								System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : " + result);
							}
							return result;
						} else {
							if (this.graph.debug()) {
								System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : related variable not checked!");
							}
							return false;					
						}
					}
				}
			}
		} catch (Exception e) {				
			e.printStackTrace();
		}	
		if (this.graph.debug()) {
			System.out.println("[CSPPredicate] checking predicate " + getConstraint().getName() + " : not checked!");
		}
		return false;
	}

	/**
	 * @deprecated
	 * Call the Constraint Resolver check operation.
	 * @return
	 */
	private boolean _check() {
		if (!v2.isNull()) {
			if (v2.getRValue().getInstance().isLocal(this.graph.getCubeAgent().getId()) == true) {
				if (this.graph.debug()) {
					System.out.println("[CSPP] . trying to check the predicate " + getConstraint().getName() + " with two local variables...");
				}
				try {
					IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
					IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
					if (cr != null) {
						List<RVariable> params = new ArrayList<RVariable>();
						params.add(v1);				
						params.add(v2);
						return cr.check(params, this.constraint);
					}					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (this.graph.debug()) {
					System.out.println("[CSPP] . trying to check the predicate " + getConstraint().getName() + " with related remote variable...");
				}
				IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
				IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
				if (cr != null) {
					List<RVariable> params = new ArrayList<RVariable>();
					params.add(v1);
					params.add(v2);
					boolean ok1 = cr.check(params, this.constraint);				
					if (ok1 == true) {
						
						System.out.println("[RConstraint] _check does not yet implement the remote check operation!");
						
						//System.out.println("[CSPP] . the local check of " + getName() + " returns true!");
						/*
						CInstanceID id2 = v2.getCSPValue().getValue();				
						CMessage msg = new CMessage();
						msg.setTo(id2.getCubeInstanceID().toString() + "/archtype/" + getPredicate().getId().getPath());
						msg.setFrom(getCSP().getId().toString());
						msg.setReplyTo(getCSP().getId().toString());				
						String body = CSP.MSG_CSP_CHECKP + "\n";
						body += ((CBinaryPredicate)this.predicate).getConstrainedVariable().getId() + "\n";
						body += v1.getCSPValue().getValue().toString() + "\n";
						body += ((CBinaryPredicate)this.predicate).getRelatedVariable().getId() + "\n";				
						body += v2.getCSPValue().getValue().toString() + "\n";				
						msg.setBody(body);
						try {					
							CMessage result = getCSP().sendAndWait(msg);							
							if (result != null) {
								String rr = result.getBody().toString();						
								if (rr != null) {							
									String[] tmp = rr.split("\n");
									String ok2 = tmp[1];
									if (ok2.trim().equalsIgnoreCase("true")) {
										if (this.csp.debug()) {
										System.out.println("[CSPP] . the local check of " + getConstraint().getName() + " returns true!");
										}
										return true;
									} else {
										if (this.csp.debug()) {
											System.out.println("[CSPP] . the remote check of " + getConstraint().getName() + " returns false!");
										}
									}
									//return vvv;
								}
							}
						} catch (TimeOutException toex) {				
							toex.printStackTrace();
							//return null;
						}*/
					} else {
						if (this.graph.debug()) {
							System.out.println("[CSPP] . the local check of " + getConstraint().getName() + " returns false!");
						}
					}
				}
			}
		}
		return false;	
	}

	/**
	 * Is it an Unary Constraint? 
	 * @return
	 */
	public boolean isUnaryConstraint() {
		return this.constraint != null && this.constraint.getArity() == 1;
	}
	
	/**
	 * Is it a Binary Constraint?
	 * @return
	 */
	public boolean isBinaryConstraint() {
		return this.constraint != null && this.constraint.getArity() == 2;
	}
	
	/**
	 * Get the related Archetype Constraint
	 * @return
	 */
	public Constraint getConstraint() {
		return this.constraint;				
	}
	
	/**
	 * Get the constrained variable.
	 * @return
	 */
	public RVariable getRV1() {
		return this.v1;
	}
	
	/**
	 * @deprecated
	 * Get the related Variable.
	 * @return
	 */
	public RVariable getRV2() {
		return this.v2;
	}

	public void perform2() {
		if (this.graph.debug() == true) {
			System.out.println(" >> performing the constraint "+getConstraint().getName()+" ...");
		}
		IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
		IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
		if (cr != null) {
			List<RVariable> params = new ArrayList<RVariable>();
			params.add(v1);				
			params.add(v2);
			if (cr.check(params, this.constraint) == false) {
				cr.perform(params, this.constraint);
			}
		}		
	}

	public boolean check2() {
		if (this.graph.debug() == true) {
			System.out.println(" >> checking the constraint "+getConstraint().getName()+" ...");
		}
		IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
		IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
		if (cr != null) {
			List<RVariable> params = new ArrayList<RVariable>();
			params.add(v1);				
			params.add(v2);
			return cr.check(params, this.constraint);
		}	
		return false;
	}

	public void cancel2() {
		if (this.graph.debug() == true) {
			System.out.println(" >> Canceling the constraint "+getConstraint().getName()+" ...");
		}
		IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
		IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
		if (cr != null) {
			List<RVariable> params = new ArrayList<RVariable>();
			params.add(v1);				
			params.add(v2);
			cr.cancel(params, this.constraint);
		}		
	}

	public RValue findValueForConstrainedVariable() {
		if (this.graph.debug() == true) {
			System.out.println(" >> finding value for the constrained variable of the constraint "+getConstraint().getName()+" ...");
		}		
		IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
		IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
		if (cr != null) {
			List<RVariable> params = new ArrayList<RVariable>();
			params.add(v1);				
			params.add(v2);
			return cr.find(params, 0, this.constraint);
		} else {
			System.out.println("\n[INFO] RConstraint : no constraint resolver found for the constraint '"+this.constraint.getNamespace()+":"+this.constraint.getName()+"'");
		}
		return null;
	}

	public void findValueForRelatedVariable() {
		if (this.graph.debug() == true) {
			System.out.println(" >> finding value for the related variable of the constraint "+getConstraint().getName()+" ...");
		}
		IExtension ex = this.graph.getCubeAgent().getExtension(this.constraint.getNamespace());
		IConstraintResolver cr = ex.getConstraintResolver(this.constraint.getNamespace(), this.constraint.getName());
		if (cr != null) {
			List<RVariable> params = new ArrayList<RVariable>();
			params.add(v1);				
			params.add(v2);
			RValue v = cr.find(params, 1, this.constraint);
			if (v != null) {
				if (this.graph.debug() == true) {
					System.out.println("   ["+this.getConstraint().getName()+"] initializing variable "+this.v2.getId()+"("+this.v2.getType().getId()+") with: "+v.getInstance().getURI());
				}
				this.v2.setRValue(v);
			}
		}		
	}

	public RVariable getRelatedVariable() {
		// TODO Auto-generated method stub
		return this.v2;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(RConstraint compareObject) {
		if (getConstraint().getPriority() < compareObject.getConstraint().getPriority())
            return -1;
        else if (getConstraint().getPriority() == compareObject.getConstraint().getPriority())
            return 0;
        else
            return 1;
	}
}
