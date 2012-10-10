/*
 * Copyright 2011-2012 Adele Team LIG (http://www-adele.imag.fr/)
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

package fr.liglab.adele.cube.extensions.core;

import fr.liglab.adele.cube.extensions.core.model.ScopeInstance;
import fr.liglab.adele.cube.extensions.core.scopesmanagement.ScopeLeader;
import fr.liglab.adele.cube.extensions.core.scopesmanagement.TopScopeLeader;

/**
 * Scope Controller
 * 
 * @author debbabi
 *
 */
public class ScopeController {
	
	private CoreExtension extension;
	
	/**
	 * Top Scope Leader
	 */
	private TopScopeLeader topScopeLeader = null;
	/**
	 * Scope Leader
	 */
	private ScopeLeader scopeLeader = null;	
	
	
	public ScopeController(CoreExtension extension) {
		this.extension = extension;
		topScopeLeader = new TopScopeLeader(extension);
	}
	
	
	public void run() {
		System.out.println("[CoreExtension.ScopeController] running...");
	}
	
	public void control(ScopeInstance coi) {

		this.scopeLeader = new ScopeLeader(this.extension, coi.getCType().getName().toString(), coi.getLocalId());						
		String sleader = this.topScopeLeader.getScopeLeader(coi.getCType().getId().toString(), coi.getLocalId());
		if (sleader == null) {
			System.out.println("[INFO] CoreExtension: no scope leader for " + coi.getCType().getId().toString() +":"+ coi.getLocalId());
			System.out.println("[INFO] CoreExtension: creating new scope leader! " + this.scopeLeader.getId().toString());
			sleader = this.topScopeLeader.setScopeLeader(coi.getCType().getId(), coi.getLocalId(), this.scopeLeader.getId().toString());											
		} 
		System.out.println("[INFO] CoreExtension: scope.leader=" + sleader);
					
		this.scopeLeader.setScopeLeaderUrl(sleader);			
		this.scopeLeader.addMember(this.scopeLeader.getId().getURI().toString());
	}
	
	
	public TopScopeLeader getTopScopeLeader() {
		return this.topScopeLeader;
	}
	
	public ScopeLeader getScopeLeader() {
		return this.scopeLeader;
	}
}
