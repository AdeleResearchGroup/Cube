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

package fr.liglab.adele.cube.extensions.core.communicator;

import java.io.IOException;
import java.util.*;

import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.MessagesListener;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicationException;
import fr.liglab.adele.cube.extensions.AbstractCommunicator;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.AutonomicManager;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.util.Utils;

/**
 * Socket CommunicatorExtensionPoint.
 * 
 * @author debbabi
 *
 */
/*
@Component
@Provides
@Instantiate
*/
public class SocketCommunicator extends AbstractCommunicator {

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
	private List<MessagesListener> callbacks = new ArrayList<MessagesListener>();
	/**
	 * Messages that have been sent and are waiting for a response.
	 */
	private LinkedList<CMessage> pendingQueue = new LinkedList<CMessage>();
	/**
	 * Messages that need to be sent.
	 */
	private LinkedList<CMessage> outgoingQueue = new LinkedList<CMessage>();
	/**
	 * Port (the same as the cube __autonomicmanager port)
	 */
    private long port;
    private String host;

	//private CubeAgentID localID = null;
	/**
	 * Cube Agent
	 */
	private AutonomicManager cubeAgent;
	/**
	 * Logger
	 */
	private CubeLogger log;

    public SocketCommunicator(Extension extension) {
        super(extension);
    }


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
	public synchronized void addMessagesListener(MessagesListener callback)  {
		//this.callbacks.add(callback);
        this.callbacks.add(callback);
	}	
	
	/**
	 * {@inheritDoc}
	 */
	public void start() {
        System.out.println("[INFO] Starting Socket-Communicator..");
		AutonomicManager agent = getExtension().getAutonomicManager();
        this.port = agent.getConfiguration().getPort();
        this.host = agent.getConfiguration().getHost();
        this.log = new CubeLogger(agent.getAdministrationService().getBundleContext(), SocketCommunicator.class.getName());
		//setLocalID(cubeAgent.getUUID());
		//if (this.localID == null) {
		//	throw new Exception("No local Id specified! Call setLocalID before starting the Endpoint");
		//}
		createServer(Utils.safeLongToInt(this.port), agent.getAdministrationService().getBundleContext());
		queue.start();									
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() {
        System.out.println("[INFO] Stopping Socket-Communicator..");
		if (this.cubeAgent != null && this.cubeAgent.getConfiguration().isDebug()) {
			log.info("stopping...");
		}
		if (this.server != null) {
			this.server.shutdown();
		}
	}

    public void destroy() {
        stop();
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
            for (MessagesListener ml : this.callbacks) {
                ml.receiveMessage(msg);
            }
        }
	}
}
