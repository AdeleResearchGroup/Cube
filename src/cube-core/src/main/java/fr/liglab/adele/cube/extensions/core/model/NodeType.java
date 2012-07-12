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

package fr.liglab.adele.cube.extensions.core.model;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

public class NodeType extends ManagedElement {

	public final static String NAME = "node";
	
	public NodeType(String id, String description, Archetype archtype) {
		super(id, description, archtype);
	}
		
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return CoreExtensionFactory.ID;
	}

	@Override
	public String toXMLString(String xmlns) {
		String out = "";		
		out += "<"+xmlns+":"+NAME+" id=\""+getId()+"\"";		
		if (getDescription() !=null)
			out += " description=\""+getDescription()+"\"";
		out += "/>\n";	
		return out;
	}

	/**
	 * Singleton within a Cube Agent.
	 */	
	public CInstance newInstance() throws Exception {
		
		for (CInstance i : getArchtype().getCubeAgent().getRuntimeModel().getCInstances(this)) {
			return i;
		}
		return new NodeInstance(this);
	}
	
}
