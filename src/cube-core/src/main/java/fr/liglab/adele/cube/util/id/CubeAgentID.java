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


package fr.liglab.adele.cube.util.id;

/**
 * Cube Agent ID.
 * 
 * @author debbabi
 *
 */
public class CubeAgentID extends CubeID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4575246230168858752L;
	String host;
	long port;
	 
	public CubeAgentID(String host, long port, String filter) throws InvalidIDException {
		super(host + ":" + port, filter);
		this.host = host;
		this.port = port;
	}

	public CubeAgentID(CubeID cubeId) throws InvalidIDException {
		super(cubeId.getHierarchicalPart(), cubeId.getFilter());
		String hierar = cubeId.getHierarchicalPart();
		if (hierar.contains("/")) {
			hierar = hierar.substring(0, hierar.indexOf("/"));
		}
		String[] tmp = hierar.split(":");
		if (tmp.length>=2) {
			this.host = tmp[0];
			this.port = new Integer(tmp[1]).intValue();
		} else {
			throw new InvalidIDException("Invalid Cube Agent ID! " + cubeId.toString());
		}
	}

	
	
	public String getHost() {
		return host;
	}

	public long getPort() {
		return port;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CubeAgentID) {
			return ((CubeAgentID)obj).getPort() == this.getPort() && this.getHost().equalsIgnoreCase(((CubeAgentID)obj).getHost());
		} else {
			return super.equals(obj);
		}
	}
}