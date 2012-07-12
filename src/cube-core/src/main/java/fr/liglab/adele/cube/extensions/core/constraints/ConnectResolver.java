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

package fr.liglab.adele.cube.extensions.core.constraints;

import java.util.List;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.resolver.RValue;
import fr.liglab.adele.cube.agent.defaults.resolver.RVariable;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.extensions.IConstraintResolver;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.ComponentInstance;
import fr.liglab.adele.cube.util.id.CInstanceUID;


/**
 * Connect Constraint Resolver.
 * 
 * @author debbabi
 *
 */
public class ConnectResolver implements IConstraintResolver {

	/**
	 * {@inheritDoc}
	 */
	public String getConstraintName() {
		return Connect.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getConstraintNamespace() {
		return CoreExtensionFactory.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public RValue find(List<RVariable> params, int which, Constraint cc) {
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			if (which == 0) {
				// TODO
				System.out.println("[WARNING] ConnectResolver : find for constrained variable not yet implemented!");
			} else if (which == 1) {
				CubeAgent agent = v12.getResolutionGraph().getCubeAgent();			
				if (v12.getRValue().getInstance().isLocal(agent.getId()) == true) {
					CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());					
					if (ci1 != null && ci1 instanceof ComponentInstance) {
						
						List<CInstanceUID> cmps = ((ComponentInstance)ci1).getOutComponents(v22.getType());
						for (CInstanceUID c : cmps) {
							if (!v12.isInHistory(c)) {
								return new RValue(c, this);
							}
						}
					}		
				} 
			}
		}				
		return null;		
	}

	/**
	 * {@inheritDoc}
	 */
	public void perform(List<RVariable> params, Constraint c) {	
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();			
			if (v12.getRValue().getInstance().isLocal(agent.getId()) == true 
					&& v22.getRValue().getInstance().isLocal(agent.getId()) == true) {
				/*
				 * this is a local perform!
				 */
				CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
				CInstance ci2 = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
				if (ci1 != null && ci1 instanceof ComponentInstance && ci2 != null && ci2 instanceof ComponentInstance) {			
					((ComponentInstance)ci1).addOutComponent(v22.getRValue().getInstance());
					((ComponentInstance)ci2).addInComponent(v12.getRValue().getInstance());
				}		
			} else {
				if (v22.getRValue().getInstance().isLocal(agent.getId()) == false) {
					CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());				
					if (ci1 != null && ci1 instanceof ComponentInstance) {			
						((ComponentInstance)ci1).addOutComponent(v22.getRValue().getInstance());
						//System.out.println("[Connect] ci1=" + ci1.toString());
					}	
				} else if (v12.getRValue().getInstance().isLocal(agent.getId()) == false){				
					CInstance ci2 = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
					if (ci2 != null && ci2 instanceof ComponentInstance) {								
						((ComponentInstance)ci2).addInComponent(v12.getRValue().getInstance());
						//System.out.println("[Connect] ci2=" + ci2.toString());
					}
				}	
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel(List<RVariable> params, Constraint c) {		
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();			
			if (v12.getRValue().getInstance().isLocal(v12.getResolutionGraph().getCubeAgent().getId()) == true 
					&& v22.getRValue().getInstance().isLocal(v22.getResolutionGraph().getCubeAgent().getId()) == true) {
				CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
				CInstance ci2 = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
				if (ci1 != null && ci1 instanceof ComponentInstance && ci2 != null && ci2 instanceof ComponentInstance) {			
					((ComponentInstance)ci1).removeOutComponent(v22.getRValue().getInstance());
					((ComponentInstance)ci2).removeInComponent(v12.getRValue().getInstance());
				}
			} else {
				
				if (v22.getRValue().getInstance().isLocal(agent.getId()) == false) {
					CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());				
					if (ci1 != null && ci1 instanceof ComponentInstance) {		
						((ComponentInstance)ci1).removeOutComponent(v22.getRValue().getInstance());					
					}
				} else if (v12.getRValue().getInstance().isLocal(agent.getId()) == false){								
					CInstance ci2 = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
					if (ci2 != null && ci2 instanceof ComponentInstance) {								
						((ComponentInstance)ci2).removeInComponent(v12.getRValue().getInstance());
					}
				}	
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean check(List<RVariable> params, Constraint c) {
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		
		
		if (v12 != null && v22 != null) {
			//System.out.println("\n\n********** v12: " + v12.getRValue());
			//System.out.println("\n\n********** v22: " + v22.getRValue());
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
			if (v12.getRValue().getInstance().isLocal(agent.getId()) == true 
					&& v22.getRValue().getInstance().isLocal(agent.getId()) == true) {
				/*
				 * this is a local perform!
				 */		
				CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
				CInstance ci2 = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
				if (ci1 != null && ci1 instanceof ComponentInstance && ci2 != null && ci2 instanceof ComponentInstance) {	
					Boolean ok1 = false;
					Boolean ok2 = false;
					
					if (((ComponentInstance)ci1).hasOutComponent(v22.getRValue().getInstance())) {
						ok1 = true;
						//System.out.println("OK1");				
					} else {
						//System.out.println("nOK1");
					}
					
					if (((ComponentInstance)ci2).hasInComponent(v12.getRValue().getInstance())) {
						ok2 = true;
						//System.out.println("OK2");				
					} else {
						//System.out.println("nOK2");
					}
					return (ok1 == true && ok2 == true);
				}
			} else {
				if (v22.getRValue().getInstance().isLocal(agent.getId()) == false) {
					CInstance ci1 = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());	
					//System.out.println("[Connect] ci1=" + ci1.toString());
					if (ci1 != null && ci1 instanceof ComponentInstance) {						
						if (((ComponentInstance)ci1).hasOutComponent(v22.getRValue().getInstance())) {
							return true;
						}
					}
				} else if (v12.getRValue().getInstance().isLocal(agent.getId()) == false){					
					CInstance ci2 = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
					//System.out.println("[Connect] ci2=" + ci2.toString());
					if (ci2 != null && ci2 instanceof ComponentInstance) {	
						if (((ComponentInstance)ci2).hasInComponent(v12.getRValue().getInstance())) {
							return true;				
						}
					}
				}
			}
		}
		return false;	
	}	
}
