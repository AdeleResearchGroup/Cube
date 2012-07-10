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

package fr.liglab.adele.cube.archetype;

/**
 * Global configuration of the archtype.
 * 
 * @author debbabi
 *
 */
public abstract class GlobalConfig {

	Archetype archtype;
	
	String id;
	String description;
	
	static int index = 0;
	
	public GlobalConfig(String id, String description, Archetype archtype) {
		this.archtype = archtype;
		if (id != null) {
			this.id = id;
		} else {
			this.id = "" + index++;
		}
		this.description = description;
	}
		
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	public abstract String getName();
	public abstract String getNamespace();
	
	public abstract String toXMLString(String xmlns);
	
}
