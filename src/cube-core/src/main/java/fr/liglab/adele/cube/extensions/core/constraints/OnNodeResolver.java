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
import fr.liglab.adele.cube.extensions.core.model.NodeInstance;
import fr.liglab.adele.cube.util.id.CInstanceID;

/**
 * On-Node Constraint Resolver.
 * 
 * @author debbabi
 *
 */
public class OnNodeResolver implements IConstraintResolver {

	/**
	 * {@inheritDoc}
	 */
	public String getConstraintName() {		
		return OnNode.NAME;
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
	public RValue find(List<RVariable> params, int which, Constraint c) {		
		//System.out.println("\n\n[OnNode] find ("+which+") ...\n\n");
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			if (which == 0) {
				CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
				// find value for constrained variable (0)
				//System.out.println("\n\n[OnNode] findValueForConstrainedVariable...\n\n");
				for (RValue v : v12.getHistoryValues()) {
					System.out.println("* " + v.getInstance());
				}
				if (v22 != null && v22.isNull() == false) {
					CInstanceID nodeID = v22.getRValue().getInstance();
					if (nodeID != null) {
						if (!nodeID.isLocal(agent.getId())) {
							//System.out.println("[CInstance] WARNING: this is not a local instance!.!.!.!.!.!.!.!.!.!.!.!.!.!.!.!.!.!");
							
						}
					}
					CInstance ni = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
					if (ni != null && ni instanceof NodeInstance && ni.getCType().equals(v22.getType())) {				
						List<CInstanceID> result = ((NodeInstance)ni).getComponentInstances(v12.getType());
						if (result != null && result.size() > 0) {
							//TODO warning, take other values for each call
							//System.out.println("\n\n[OnNode] findValueForConstrainedVariable : there is " + result.size() + " instances!");
							for (CInstanceID iii : result) {
								if (!v12.isInHistory(iii)) {
									return new RValue(iii, this);
								}
							}
							
						}
					}
				}
			} else if (which == 1) {
				CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
				if (v12 != null && v12.isNull() == false) {
					CInstance ci = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
					if (ci != null && ci instanceof ComponentInstance) {				
						CInstanceID node = ((ComponentInstance)ci).getNode();				
						if (node != null) {
							//TODO warning, take other values for each call
							RValue cspv = new RValue(node, this);
							return cspv;
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
		//System.out.println("[OnNodeResolver] performing...");
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
			CInstance ci = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
			CInstance ni = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
			if (ci != null && ci instanceof ComponentInstance && ni != null && ni instanceof NodeInstance) {
				((NodeInstance)ni).addComponentInstance(v12.getRValue().getInstance());
				((ComponentInstance)ci).setNode(v22.getRValue().getInstance());
				//System.out.println("[OnNode] PERFORMED!");
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
			CInstance ci = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
			CInstance ni = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());
			if (ci != null && ci instanceof ComponentInstance && ni != null && ni instanceof NodeInstance) {
				((NodeInstance)ni).removeComponentInstance(v12.getRValue().getInstance());
				((ComponentInstance)ci).setNode(null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean check(List<RVariable> params, Constraint c) {
		//System.out.println("[OnNodeResolver] checking...");
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
			CInstance ci = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
			CInstance ni = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());				

			if (ci != null && ci instanceof ComponentInstance && ni != null && ni instanceof NodeInstance) {
				boolean ok1 = false;
				boolean ok2 = false;		

				if (((NodeInstance)ni).getCType().equals(v22.getType()) == false) {
					ok1 = false;
				} else if (((NodeInstance)ni).hasComponentInstance(v12.getRValue().getInstance())) {
					//System.out.println("OK1");				
					ok1 = true;
				}

				if (((ComponentInstance)ci).getNode().equals(v22.getRValue().getInstance())) {
					//System.out.println("OK2");
					ok2 = true;
				}				
				//System.out.println("[OnNodeResolver] checking: " + (ok1 == true && ok2 == true));
				return (ok1 == true && ok2 == true);
			}		
		}
		//System.out.println("[OnNodeResolver] checking: false");
		return false;		
	}

}
