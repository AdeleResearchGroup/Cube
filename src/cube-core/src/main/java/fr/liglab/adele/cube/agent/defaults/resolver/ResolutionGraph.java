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
import java.util.Collection;
import java.util.List;

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.TimeOutException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.archetype.Variable;
import fr.liglab.adele.cube.util.id.ResolutionGraphID;

/**
 * Resolution Graph. 
 * 
 * 
 * @author debbabi
 *
 */
public class ResolutionGraph {

	/**
	 * Cube Agent.
	 */
	private CubeAgent agent;
	/**
	 * The Resolution Graph ID.
	 * This is global URL like: cube://localhost:38000/agent/rg/24
	 */
	private ResolutionGraphID id;	
	/**
	 * The Last unresolved constraint.
	 * This is helpful for debug.
	 */
	private RConstraint lastUnresolvedConstraint = null;
	/**
	 * The Resolution Graph Top Variable.
	 */
	private RVariable topVariable = null;
	
	/**
	 * Constructor.
	 * 
	 * @param agent
	 */
	public ResolutionGraph(CubeAgent agent) {
		this.agent = agent;
		this.id = new ResolutionGraphID(agent.getId());
	}
	
	/**
	 * Constructor with known top variable.
	 * 
	 * @param agent
	 * @param topVar
	 */
	public ResolutionGraph(CubeAgent agent, RVariable topVar) {
		this.agent = agent;
		this.id = new ResolutionGraphID(agent.getId());	
		this.setTopVariable(topVar);
	}
	
	/**
	 * Resolving this Resolution Graph.
	 * 
	 * Resolving the graph is done by resolving the top variable.
	 * When resolving the top variable, this will generate other variables related by constraints.
	 * 
	 * 
	 */
	public void resolve() {
		RVariable topVar = getTopVariable();
		if (topVar != null) {
			if (topVar.isNull() == true) {
				//TODO
			} else {
				switch(topVar.getRValue().getState()) {
				case CInstance.VALID:
				{
					/*
					 * If the top variable is already resolved, we have no thing to do!
					 */	
				}
				case CInstance.UNRESOLVED:
				{
					/*
					 * If the top variable is unresolved, we resolve it by calling its internal 
					 * method 'resolve()'
					 */
					try {
						if (topVar.validate() == true) {

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				case CInstance.UNCHECKED:
				{
					/*
					 * If the top variable is unchecked, TODO ..
					 */
				}
				}
			}
		}
	}
	
	/**
	 * Set the Top Variable.
	 * @param topVar
	 */
	public void setTopVariable(RVariable topVar) {
		this.topVariable = topVar;
	}

	/**
	 * Get the Top Variable.
	 * @return
	 */
	public synchronized RVariable getTopVariable() {
		return this.topVariable;
	}
	
	/**
	 * Get a Resolution Variable from this Resolution Graph which have the given id.  
	 * @param id
	 * @return
	 */
	public RVariable getRVariable(String id) {
		for (RVariable v : getRVariables()) {
			if (v.getId().equalsIgnoreCase(id)) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Get a Resolution Variable from the Resolution Graph which encapsulate the given 
	 * archetype variable.
	 * 
	 * @param v2id Archetype variable name
	 * @return
	 */
	public RVariable getRVariableByVarName(String v2id) {
		for (RVariable v : getRVariables()) {
			for (Variable arv : v.getArchetypeVariables()) {
				if (arv.getId().equalsIgnoreCase(v2id)) {
					return v;
				}
			}			
		}
		return null;
	}
	
	/**
	 * Get all the Resolution Variables of this Resolution Graph.
	 * @return
	 */
	public Collection<RVariable> getRVariables() {
		List<RVariable> result = new ArrayList<RVariable>();
		if (this.getTopVariable() != null) {
			result.add(getTopVariable());
			for (RVariable v:this.getTopVariable().getAllSubRVariables()) {
				result.add(v);
			}
		}
		return result;		
	}

	/**
	 * Get the Last unresolved constraint.
	 * @return
	 */
	public RConstraint getLastUnresolvedConstraint() {
		return this.lastUnresolvedConstraint;
	}

	/**
	 * Set the Last unresolved constraint.
	 * @param c
	 */
	public void setLastUnresolvedConstraint(RConstraint c) {
		this.lastUnresolvedConstraint = c;
		
	}
	
	/**
	 * Does this Resolution Graph shows debug messages on the console?
	 * This could be set as a configuration property within the Agent Configuration file.
	 * @return 
	 */
	public boolean debug() {
		return this.agent.getConfig().isDebug();
	}

	/**
	 * Get the Cube Agent.
	 * @return
	 */
	public CubeAgent getCubeAgent() {
		return this.agent;
	}

	/**
	 * Is this Resolution Graph resolved?
	 * e.g. all Resolution Variables have values and satisfies their constraints.
	 * @return
	 */
	public boolean isResolved() {	
		//TODO not yet implemented! it just checks for now the top variable!	
		//System.out.println("[INFO] ResolutionGraph : isResolved.. top var: " + getTopVariable().getRValue().getInstance() + " (original:" + getTopVariable().getRValue().getOriginalState()+ " | new:" + getTopVariable().getRValue().getState() +")");
		if (getTopVariable().isNull() == false && this.getTopVariable().getRValue().getState() == CInstance.VALID) {
			return true;
		}
		return false;
	}

	/**
	 * Get the Logger.
	 * @return
	 */
	public CubeLogger getLogger() {		
		return null;
	}

	/**
	 * Get the Resolution Graph Id.
	 * @return
	 */
	public ResolutionGraphID getId() {		
		return this.id;
	}
	
	/**
	 * Send Message and Wait.
	 * This is a synchronous message sending.. 
	 * 
	 * @param msg
	 * @return
	 * @throws TimeOutException
	 */
	public CMessage sendAndWait(CMessage msg) throws TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public static final String MSG_CSP_FINDVALUEFORCONSTRAINEDVAR = "CSPFIND";
	public static final String MSG_CSP_FINDVALUEFORCONSTRAINEDVAR_RES = "CSPFIND_R";
	public static final String MSG_CSP_PERFORM = "CSPPERFORM";
	public static final String MSG_CSP_PERFORM_RES = "CSPPERFORM_R";
	public static final String MSG_CSP_CANCEL = "CSPCANCEL";
	public static final String MSG_CSP_CANCEL_RES = "CSPCANCEL_R";
	public static final String MSG_CSP_CHECKV = "CSPCHECKV";
	public static final String MSG_CSP_CHECKV_RES = "CSPCHECKV_R";
	public static final String MSG_CSP_CHECKP = "CSPCHECKP";
	public static final String MSG_CSP_CHECKP_RES = "CSPCHECKP_R";
	public static final String MSG_CSP_OP = "CSP_OP";
	public static final String MSG_CSP_CONSTRAINT_ID = "CSP_CONSTRAINT_ID";
	public static final String MSG_CSP_VAR_VALUE = "CSP_VAR_VALUE";
	public static final String MSG_CSP_VARS = "CSP_VARS";




	
	
}
