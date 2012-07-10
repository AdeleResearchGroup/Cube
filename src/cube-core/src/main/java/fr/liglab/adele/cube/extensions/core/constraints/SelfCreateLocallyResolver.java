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

import fr.liglab.adele.cube.TypeNotDeclaredException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.resolver.RValue;
import fr.liglab.adele.cube.agent.defaults.resolver.RVariable;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.Type;
import fr.liglab.adele.cube.extensions.IConstraintResolver;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

/**
 * Self-Create Locally Constraint Resolver.
 * 
 * @author debbabi
 *
 */
public class SelfCreateLocallyResolver implements IConstraintResolver {

	/**
	 * {@inheritDoc}
	 */
	public String getConstraintName() {
		return SelfCreateLocally.NAME;
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
		//System.out.println("\n\n[SelfCreateLocally] find ("+which+") ...\n\n");
		if (which == 0) {
			RVariable v1 = params.get(0);
			
			if (v1 != null) {
				boolean foundLocalconfigs = false;
				CubeAgent agent = v1.getResolutionGraph().getCubeAgent();
				//localConfiguration localConfiguration = agent.getLocalConfiguration();			
				int index = 0;
				for (RValue tmp : v1.getHistoryValues()) {
					if (tmp.getGenerator() == this) {
						index = index + 1;
					}
				}
				
				/*if (localConfiguration != null) {					
					String type= getConstrainedVariable().getCType().getId();
					List<LocalConfig> lcs = localConfiguration.getLocalConfigsOfType(type);			
					if (lcs != null && lcs.size() > 0) {								
						if (index >= lcs.size()) {
							return null;
						}
						foundLocalconfigs = true;
										
						CType cotype = getConstrainedVariable().getCType();
						CInstance minstance;
						try {							
							minstance = cotype.newInstance(lcs.get(index), getCubeInstance().getLam().getCubeInstance());							
							getCubeInstance().getLam().getCubeInstance().getRuntimeModel().add(minstance);																
							return new CSPValue(minstance, this);						
						} catch (InvalidLocalConfigException e) {
							e.printStackTrace();
						} catch (CObjectTypeNotDeclaredException e) {
							e.printStackTrace();
						}
					} 
				}*/
				if (foundLocalconfigs == false) {
					if (index >= 1) {
						return null;
					}
					Type cotype = v1.getType();
					CInstance minstance;
								
					try {
						minstance = cotype.newInstance(agent);
						agent.getRuntimeModel().add(minstance);									
						return new RValue(minstance, this);			
					} catch (TypeNotDeclaredException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}												
				}																		
			} 
		} else if (which == 1) {
			
		}
		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void perform(List<RVariable> params, Constraint c) {
		// not called		
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel(List<RVariable> params, Constraint c) {
		// not called
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean check(List<RVariable> params, Constraint c) {
		// not called
		return false;
	}

	
}
