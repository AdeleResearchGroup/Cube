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

package fr.liglab.adele.cube.agent;

/**
 * Cube Agent Resolver Interface.
 * 
 * @author debbabi
 *
 */
public interface IResolver {
	/**
	 * Resolve a new created instance.
	 * This method is called by the Runtime Model to validate the created instance
	 * against what was specified as constraints on it within the archetype.
	 *  
	 * Initially, the target instance is UNRESOLVED.
	 * When the Resolver resolves its constraints, it should call the Runtime Model 
	 * method ''validate'' with the same Instance coi. This will change its state
	 * to VALID, and the executors will be notified.
	 *  
	 * @param coi
	 */
	public void resolveNewInstance(CInstance coi);
	
	// TODO
	
}
