/*
 * Copyright 2011 Adele Team LIG (http://www-adele.imag.fr/)
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

package fr.liglab.adele.cube.util.id;

public class CInstanceUID extends CubeID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static long indexm=0;	
	
	public CInstanceUID(CubeAgentID cubeinstance) {
		super();		
		setHierarchicalPart(cubeinstance.getHost() + ":" + cubeinstance.getPort() + "/objects/" + indexm++);
		setFilter(cubeinstance.getFilter());		
	}
	
	public CInstanceUID(String instanceID) {
		super();
		if (instanceID != null) {
			String uri = instanceID.replace("cube://", "");
			setHierarchicalPart(uri);
			setFilter("");
		}		
	}

	public CubeAgentID getCubeAgentID() {
		String uri = getURI();
		String[] tmp1 = uri.split("://");
		String[] tmp2 = tmp1[1].split(":");
		String host = tmp2[0];
		String port = tmp2[1];
		if (port.contains("/")) {
			port = port.substring(0, port.indexOf("/"));
		}
		CubeAgentID id;
		try {
			id = new CubeAgentID(host, new Integer(port).intValue(), "");
			return id;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isLocal(CubeAgentID cubeInstance) {
		CubeAgentID cid = this.getCubeAgentID();
		if (cid != null && cid.equals(cubeInstance)) {
			return true;
		}		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CInstanceUID) {
			return ((CInstanceUID)obj).getURI().equalsIgnoreCase(this.getURI());
		}
		return false;
	}
}
