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

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.MessagesListener;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.IResolver;
import fr.liglab.adele.cube.util.id.CInstanceID;
import fr.liglab.adele.cube.util.perf.Measure;
import fr.liglab.adele.cube.util.perf.PerformanceChecker;

/**
 * Default Resolver.
 * 
 * @author debbabi
 *
 */
public class DefaultResolver implements IResolver, MessagesListener{
	
	/**
	 * Cube Agent
	 */
	private CubeAgent agent;
	/**
	 * Performance Checker
	 */
	private final PerformanceChecker perf = new PerformanceChecker();
	
	/**
	 * Cube Logger
	 */
	private CubeLogger log;
	
	/**
	 * Constructor
	 * @param agent
	 */
	public DefaultResolver(CubeAgent agent) {
		this.agent = agent;	
		log = new CubeLogger(agent.getCubePlatform().getBundleContext(), DefaultResolver.class.getSimpleName());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resolveNewInstance(CInstance coi) {
		//log.info("Resolving new instance " + coi.getId() + " ...");
		if (coi != null) {
			ResolutionGraph resolutionGraph = new ResolutionGraph(this.agent);			
			RVariable topVar = new RVariable(resolutionGraph, coi.getCType(), new RValue(coi, this));			
			resolutionGraph.setTopVariable(topVar);			
			Measure m = new Measure(agent.getId().toString(), coi.getLocalId() + ":" + coi.getCType().getId());
			m.start();
			try {
				 resolutionGraph.resolve();  
			} finally {
			  m.end();
			}			
			
			if (resolutionGraph.isResolved()) {
				if (this.agent.isDebugable() == true) {
					log.info("The instance " + coi.getId() + "(" +  coi.getCType().getId() + ") is resolved.");
				}
				validateSolution(resolutionGraph, m);
			} else {
				//log.info("No solution found for this problem!");
				if (this.agent.isDebugable() == true) {
					log.error("The instance " + coi.getId() + "(" + coi.getCType().getId() + ") is not resolved!");
				}				
				if (resolutionGraph.getLastUnresolvedConstraint() != null) {
					if (this.agent.isDebugable() == true) {
						log.error("It does not satisfy the following constraint: " + resolutionGraph.getLastUnresolvedConstraint().getConstraint().getName());
					}
				}
			}
		}
	}

	/**
	 * Validate the found solution.
	 * Set the different UNRESOLVED instances to VALID within the Runtime Model.
	 * @param sol
	 * @param m
	 */
	private void validateSolution(ResolutionGraph resolutionGraph, Measure m) {

		//log.info("validateSolution...");
		if (resolutionGraph != null) {
			for (RVariable v : resolutionGraph.getRVariables()) {
				if (v.isNull() == false 
						&& v.getRValue().getState() == CInstance.VALID						
						&& v.getRValue().getInstance().isLocal(this.agent.getId())) {
					//log.info("validating : " + v2.getCSPValue().toString() + "~" + v2.getCSPValue().getOriginalState());
					CInstanceID instanceID = v.getRValue().getInstance();					
					validateInstance(instanceID, m);
				}
			}
			m.calculate();
			perf.addMeasure(m);
			if (this.agent.getConfig().isPerf() == true) {
				System.out.println("[PERF] " + m.toString());
			}
		}

	}
	
	private void validateInstance(CInstanceID instanceID) {
		if (instanceID != null) {
			//TODO check if it is local
			CInstance instance = this.agent.getRuntimeModel().getCInstance(instanceID);
			if (instance != null) {												
				this.agent.getRuntimeModel().validate(instance);						
			}
		}
	}
	
	private void validateInstance(CInstanceID instanceID, Measure m) {
		if (instanceID != null) {
			//TODO check if it is local
			CInstance instance = this.agent.getRuntimeModel().getCInstance(instanceID);
			if (instance != null) {			
				if (instance.getState() == CInstance.UNRESOLVED) {
					m.addNewValidInstance(instance.getLocalId() + ":" + instance.getCType().getId());
				}
				this.agent.getRuntimeModel().validate(instance);						
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void receiveMessage(CMessage msg) {
		
	}

	
}
