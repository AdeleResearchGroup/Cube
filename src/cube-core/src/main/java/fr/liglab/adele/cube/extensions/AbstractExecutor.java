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

package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.RuntimeModelListener;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.util.id.ExecutorID;
import fr.liglab.adele.cube.util.id.InvalidIDException;

public abstract class AbstractExecutor implements RuntimeModelListener {
	
	ExecutorID id;
	CubeAgent cubeAgent;
	
	
	public AbstractExecutor(CubeAgent ci, String name) {		
		if (ci != null) {
			this.cubeAgent = ci;			
			try {
				this.id = new ExecutorID(ci.getId(), name);
			} catch (InvalidIDException e) {				
				e.printStackTrace();
			}			
			//this.cubeInstance.addExecutor(this);
		}
		
	}
	
	public ExecutorID getId() {
		return this.id;
	}
	
	public CubeAgent  getCubeAgent() {
		return this.cubeAgent;
	}
	
	public void run() {
		if (this.cubeAgent != null) {
			this.cubeAgent.getRuntimeModel().addListener(this);
		}
	}
	public void stop() {
		if (this.cubeAgent != null) {
			this.cubeAgent.getRuntimeModel().removeListener(this);
		}
	}
	
	
}
