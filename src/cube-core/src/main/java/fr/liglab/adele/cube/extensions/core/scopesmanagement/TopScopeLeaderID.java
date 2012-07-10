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

package fr.liglab.adele.cube.extensions.core.scopesmanagement;

import fr.liglab.adele.cube.extensions.core.CoreExtension;
import fr.liglab.adele.cube.util.id.CubeAgentID;
import fr.liglab.adele.cube.util.id.CubeID;
import fr.liglab.adele.cube.util.id.InvalidIDException;

/**
 * Top Scope Leader ID
 * 
 * @author debbabi
 *
 */
public class TopScopeLeaderID extends CubeAgentID {

	private String extensionID;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TopScopeLeaderID(CubeID ciid) throws InvalidIDException {
		super(ciid);		
		if (ciid.getHierarchicalPart().contains("/topscopeleader")) {
			String hierar = ciid.getHierarchicalPart();
			String tmp1 = hierar.substring(0, hierar.lastIndexOf("/"));
			this.extensionID = tmp1.substring(tmp1.lastIndexOf("/")+1,tmp1.length());			
		} else {
			throw new InvalidIDException("Invalid TopScopeLeader ID! " + ciid);
		}
	}
		
	public TopScopeLeaderID(CoreExtension ex) throws InvalidIDException  {
		super(ex.getCubeAgent().getId());				
		extensionID = ex.getExtensionFactory().getExtensionId();
	}
		
	public static boolean check(CubeID id) {	
		if (id != null) {
			if (id.getHierarchicalPart().contains("/topscopeleader")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return "cube://"+this.getHost()+":"+this.getPort() +"/"+this.extensionID +"/topscopeleader";
	}
}
