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

package fr.liglab.adele.cube.agent.defaults.communicator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.CommunicationException;
import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.MessagesListener;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ICommunicator;
import fr.liglab.adele.cube.util.Utils;
import fr.liglab.adele.cube.util.id.CubeAgentID;
import fr.liglab.adele.cube.util.id.CubeID;
import fr.liglab.adele.cube.util.id.InvalidIDException;

/**
 * Socket Communicator.
 * 
 * @author debbabi
 *
 */
public class SocketCommunicationModule implements ICommunicator {

	private static final String NAME = "socket-communicator";

	/**
	 * Server Socket 
	 */
	private Server server = null;
	/**
	 * Client Socket Queue
	 */
	private final WorkQueue queue = new WorkQueue("clients-", 5);
	/**
	 * key: listener id (URL)
	 * value: listener
	 */
	private Map<String, MessagesListener> callbacks = new HashMap<String, MessagesListener>();		
	/**
	 * Messages that have been sent and are waiting for a response.
	 */
	private LinkedList<CMessage> pendingQueue = new LinkedList<CMessage>();
	/**
	 * Messages that need to be sent.
	 */
	private LinkedList<CMessage> outgoingQueue = new LinkedList<CMessage>();
	/**
	 * Port (the same as the cube agent port)
	 */
	private CubeAgentID localID = null;
	/**
	 * Cube Agent
	 */
	private CubeAgent cubeAgent;
	/**
	 * Logger
	 */
	private CubeLogger log = null;

	
	/**
	 * {@inheritDoc}
	 */
	public String getName() {	
		return NAME;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendMessage(CMessage msg) throws CommunicationException, IOException {
		log.info("sending msg...\n" + msg.toString());
		CubeAgentID cid;
		try {
			cid = new CubeAgentID(new CubeID(msg.getTo()));
			Client client = new Client(msg, cid.getHost(), cid.getPort());
			queue.execute(client);
			// client.send();
		} catch (InvalidIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized void addMessagesListener(String id, MessagesListener callback) throws Exception {
		//this.callbacks.add(callback);
		if (this.callbacks.containsKey(id) == false) {
			this.callbacks.put(id, callback);
		}
	}	
	
	/**
	 * {@inheritDoc}
	 */
	public void start(CubeAgent agent) throws Exception {
		System.out.println("[INFO] SocketCommunicator : starting...");		
		this.cubeAgent = agent;				
		log = new CubeLogger(this.cubeAgent.getCubePlatform().getBundleContext(), SocketCommunicationModule.class.getName());
		setLocalID(cubeAgent.getId());		
		if (this.localID == null) {
			throw new Exception("No local Id specified! Call setLocalID before starting the Endpoint");			
		}
		createServer(Utils.safeLongToInt(agent.getConfig().getPort()), agent.getCubePlatform().getBundleContext());
		queue.start();									
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		if (this.cubeAgent != null && this.cubeAgent.getConfig().isDebug()) {
			System.out.println("[INFO] SocketCommunicator : stopping...");						
		}
		if (this.server != null) {
			this.server.shutdown();
		}
	}
		
	/**
	 * Create a Server
	 * 
	 * @param port
	 * @param btx
	 */
	private void createServer(int port, BundleContext btx) {
		System.out.println("[INFO] SocketCommunicator : creating server in port: " + port);
		try {
			this.server = new Server(port, btx) {				
				@Override
				public void messageReceived(CMessage msg) {					
					notifyMessageArrival(msg);
				}				
			};
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * Notify listeners about the received message
	 * 
	 * @param msg
	 */
	private void notifyMessageArrival(CMessage msg) {		
		MessagesListener ml = null;
		if (msg != null && msg.getTo() != null && msg.getTo().trim().length() > 0) {
			synchronized (this.callbacks) {
				ml = this.callbacks.get(msg.getTo());
			} 
			if (ml != null) {
				ml.receiveMessage(msg);
			}
		}
	}
	
	/**
	 * Set the localId to obtain the port
	 * @param id
	 * @throws Exception
	 */
	private void setLocalID(CubeAgentID id) throws Exception {
		this.localID = id;
	}
	


}
