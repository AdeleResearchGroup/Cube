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

import java.io.Serializable;

/**
 * CubeID super class
 * 
 * @author debbabi
 *
 */
public class CubeID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8322096423529306793L;
	
	String schemeName = "cube";
	String hierarchicalPart = "";
	String filter ="";
	String fragment = "";		
	
	public CubeID() {
		
	}
	
	public CubeID(String hierarchicalPart, String filter) {
		prepare(hierarchicalPart, filter);
	}
	
	private void prepare(String hierarchicalPart, String filter) {
		if (hierarchicalPart != null) {
			// remove scheme part!
			if (hierarchicalPart.startsWith(schemeName +"://")) {
				hierarchicalPart = hierarchicalPart.substring(hierarchicalPart.indexOf("/")+2);
			}
			if (hierarchicalPart.endsWith("/")) {
				hierarchicalPart = hierarchicalPart.substring(0, hierarchicalPart.length());
			}
			this.hierarchicalPart = hierarchicalPart;
		}
		if (filter != null) {
			if (filter.endsWith("/")) {
				filter = filter.substring(0, filter.length());
			}
			this.filter = filter;
		}	
	}
	
	public CubeID(String cubeID) {
		if (cubeID != null && cubeID.length() > 0) {
			if (cubeID.contains("?")) {
				prepare(cubeID.substring(0, cubeID.indexOf("?")), cubeID.substring(cubeID.indexOf("?")+1, cubeID.length()));
			} else {
				prepare(cubeID, null);
			}
		}
	}
	
	
	
	protected void setHierarchicalPart(String hierarchicalPart) {
		this.hierarchicalPart = hierarchicalPart;
	}

	protected void setFilter(String filter) {
		this.filter = filter;
	}

	public String getHierarchicalPart() {
		return this.hierarchicalPart;
	}
	
	public String getFilter() {
		return this.filter;
	}
	
	public String getURI() {
		String uri = this.schemeName + "://";
		if (hierarchicalPart != null) uri += hierarchicalPart;
		if (filter != null && !filter.trim().equals("")) uri += "?" + filter;
		return uri;
	}
	
	public String toString() {
		return this.getURI();		
	}
	
	public static boolean check(CubeID id) {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CubeID) {
			if (this.getURI().equalsIgnoreCase(((CubeID)obj).getURI())) {
				return true;
			}
		}
		return false;
		//return super.equals(obj);
	}
}
