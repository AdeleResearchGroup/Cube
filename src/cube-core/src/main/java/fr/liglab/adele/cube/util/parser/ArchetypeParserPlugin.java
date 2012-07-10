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

package fr.liglab.adele.cube.util.parser;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.Type;
import fr.liglab.adele.cube.archetype.GlobalConfig;
import fr.liglab.adele.cube.util.xml.XMLElement;

/**
 * Archtype parser plugin interface
 * 
 * Each new extension of Cube should implement this interface to parse the domain specific elements.
 * 
 * @author debbabi
 *
 */
public interface ArchetypeParserPlugin {
	
	public Type parseType(XMLElement element, Archetype archtype) throws ParseException;
	public Constraint parseConstraint(XMLElement element, Archetype archtype) throws ParseException;
	public GlobalConfig parseGlobalConfig(XMLElement element, Archetype archtype) throws ParseException;
	
}

