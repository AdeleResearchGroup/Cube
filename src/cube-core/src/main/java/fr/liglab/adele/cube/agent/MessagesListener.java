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

import fr.liglab.adele.cube.agent.CMessage;

/**
 * Messages Listener.
 * 
 * @author debbabi
 *
 */
public interface MessagesListener {
	/**
	 * This method is called when the Communicator receives a message.
	 * 
	 * @param msg
	 */
	public void receiveMessage(CMessage msg);
}
