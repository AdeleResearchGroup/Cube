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

import fr.liglab.adele.cube.agent.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.util.Utils;

/**
 * Socket Communicator.
 * 
 * @author debbabi
 *
 */
@Component
@Provides
@Instantiate
public class SocketCommunicationModule implements Communicator {

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
    private long port;
    private String host;

	//private CubeAgentID localID = null;
	/**
	 * Cube Agent
	 */
	private CubeAgent cubeAgent;
	/**
	 * Logger
	 */
	private CubeLogger log;

	
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
		//log.info("sending msg...\n" + msg.toString());
        if (msg != null) {
            //if (msg.getObject() != null && !msg.getObject().equalsIgnoreCase("keepalive"))
            //    System.out.println("send: " + msg.toString());
            String cid;

            cid = msg.getTo();
            if (cid == null)
                throw new CommunicationException("Null or Invalid destination!");
            Client client = new Client(msg,  Utils.hostFromURI(cid), Utils.portFromURI(cid));
            queue.execute(client);
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
	public void run(CubeAgent agent) throws Exception {
		this.cubeAgent = agent;				
        this.port = agent.getConfig().getPort();
        this.host = agent.getConfig().getHost();
        this.log = new CubeLogger(agent.getPlatform().getBundleContext(), SocketCommunicationModule.class.getName());
		//setLocalID(cubeAgent.getUUID());
		//if (this.localID == null) {
		//	throw new Exception("No local Id specified! Call setLocalID before starting the Endpoint");
		//}
		createServer(Utils.safeLongToInt(this.port), agent.getPlatform().getBundleContext());
		queue.start();									
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		if (this.cubeAgent != null && this.cubeAgent.getConfig().isDebug()) {
			log.info("stopping...");
		}
		if (this.server != null) {
			this.server.shutdown();
		}
	}

    public void destroy() {

    }

    /**
	 * Create a Server
	 * 
	 * @param port
	 * @param btx
	 */
	private void createServer(int port, BundleContext btx) {
        //log.info("creating server in port: " + port);
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
        if (msg != null) {
            //if (msg.getObject() != null && !msg.getObject().equalsIgnoreCase("keepalive"))
            //    System.out.println("receive: " + msg.toString());
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
	}
}
