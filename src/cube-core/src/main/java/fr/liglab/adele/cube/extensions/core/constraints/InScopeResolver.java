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
import fr.liglab.adele.cube.extensions.core.model.NodeInstance;
import fr.liglab.adele.cube.extensions.core.model.ScopeInstance;
import fr.liglab.adele.cube.util.id.CInstanceUID;

/**
 * On-Node Constraint Resolver.
 * 
 * @author debbabi
 *
 */
public class InScopeResolver implements IConstraintResolver {
	
	/**
	 * {@inheritDoc}
	 */
	public String getConstraintName() {		
		return InScope.NAME;
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
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			if (which == 0) {
				CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
				
			} else if (which == 1) {
				CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
				if (v12 != null && v12.isNull() == false) {
				
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
			CInstance ni = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
			CInstance si = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());												
			if (ni != null && ni instanceof NodeInstance && si != null && si instanceof ScopeInstance) {
				((NodeInstance)ni).addScope(v22.getRValue().getInstance());
				((ScopeInstance)si).addNode(v12.getRValue().getInstance());
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
			System.out.println("\n*\n*\n[WARNING] InScopeResolver : cancel not yet implemented!\n*\n*\n");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean check(List<RVariable> params, Constraint c) {		
		RVariable v12 = params.get(0);
		RVariable v22 = params.get(1);
		if (v12 != null && v22 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
			CInstance ni = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());
			CInstance si = agent.getRuntimeModel().getCInstance(v22.getRValue().getInstance());												
			if (ni != null && ni instanceof NodeInstance && si != null && si instanceof ScopeInstance) {
				Boolean ok1 = false;
				Boolean ok2 = false;
				for (CInstanceUID scop:((NodeInstance)ni).getScopes()) {
					if (scop.getURI().equalsIgnoreCase(si.getId().getURI())) {
						ok1 = true;
						break;
					}
				}
				for (CInstanceUID nod : ((ScopeInstance)si).getNodes()) {
					if (nod.getURI().equalsIgnoreCase(ni.getId().getURI())) {
						ok2 = true;
						break;
					}
				}
				return (ok1 == true && ok2 == true);
			}		
			return false;	
		}
		return false;		
	}
}
