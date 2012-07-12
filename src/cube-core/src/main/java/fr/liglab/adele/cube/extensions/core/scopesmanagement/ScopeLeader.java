/*
 * Copyright 2011-2012 Adele Team LIG (http://www-adele.imag.fr/)
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

package fr.liglab.adele.cube.extensions.core.scopesmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.CommunicationException;
import fr.liglab.adele.cube.MessagesListener;
import fr.liglab.adele.cube.TimeOutException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.extensions.core.CoreExtension;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.extensions.core.model.Scope;
import fr.liglab.adele.cube.util.id.InvalidIDException;

/**
 * Scope Leader.
 * 
 * @author debbabi
 *
 */
public class ScopeLeader implements MessagesListener {

	/**
	 * Identifier.
	 * e.g. cube://localhost:3838/fr.liglab.adele.cube.extensions.core/scopeleader
	 */
	ScopeLeaderID id;
	/**
	 * Core Extension
	 */
	private CoreExtension coreExtension = null;
	/**
	 * List of Cube Agent members of this scope group
	 */
	private List<String> members = new ArrayList<String>();
	/**
	 * am I the scope leader?
	 */
	private boolean imTheScopeLeader = false;
	/**
	 * The Url of the scope leader of the given type and localId.
	 */
	private String scopeLeaderUrl = null;

	/**
	 * Constructor.
	 * 
	 * @param cc
	 * @param type
	 * @param localId
	 * @param scopeLeaderId
	 */
	public ScopeLeader(CoreExtension cc, String type, String localId, String scopeLeaderId) {

		this(cc, type, localId);

		this.scopeLeaderUrl = scopeLeaderId;

		if (getId().getURI().toString().equalsIgnoreCase(scopeLeaderId)) {
			setAsScopeLeader();
		}

	}

	public ScopeLeader(CoreExtension ex, String type, String localId) {
		this.coreExtension = ex;
		try {
			this.id = new ScopeLeaderID(ex);
		} catch (InvalidIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		// listen to messages
		try {
			ex.getCubeAgent().getCommunicator().addMessagesListener(this.getId().toString(), this);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	public void setScopeLeaderUrl(String scopeLeaderId) {
		this.scopeLeaderUrl = scopeLeaderId;		
		if (getId().getURI().toString().equalsIgnoreCase(scopeLeaderId)) {
			setAsScopeLeader();
		}
	}

	/**
	 * Get the scope leader ID
	 * @return
	 */
	public ScopeLeaderID getId() {
		return this.id;
	}

	/**
	 * Add a member (presented by its local scope leader object) to the list.
	 * 
	 * @param scopeLeaderId
	 */
	public void addMember(String scopeLeaderId) {
		if (ImTheScopeLeader()) {
			members.add(scopeLeaderId);
		} else {
			CMessage msg = new CMessage();
			msg.setFrom(getId().toString());
			msg.setReplyTo(getId().toString());
			msg.setTo(this.scopeLeaderUrl);

			msg.addHeader(ScopeManagement.SCOPE_LEADER_ID, scopeLeaderId);

			msg.setObject(ScopeManagement.ADD_SCOPE_MEMBER);			

			try {
				this.coreExtension.getCubeAgent().getCommunicator().sendMessage(msg);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public List<String> getMembers() {
		if (ImTheScopeLeader()) {
			return this.members;
		} 
		return null;
	}

	private void setAsScopeLeader() {
		coreExtension.getLogger().info("I'm the SCOPE LEADER :)");
		this.imTheScopeLeader = true;
	}

	/**
	 * The local cube is the scope leader?
	 * @return
	 */
	public boolean ImTheScopeLeader() {
		return this.imTheScopeLeader;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return this.scopeLeaderUrl ;
	}

	@Override
	public String toString() {
		String out = "";
		for (String m : this.members) {
			out += "      - " + m + "\n";
		}
		return out;
	}

	public void receiveMessage(CMessage msg) {			
		if (msg.getCorrelation() == waitingCorrelation) {				
			this.waitingMessage = msg;
			if (csplock != null) {
				synchronized (csplock) {
					csplock.notify();
				}
			}
			waitingCorrelation = -1;												
		}
		if (msg.getObject() != null) {
			if (msg.getObject().equalsIgnoreCase(ScopeManagement.ADD_SCOPE_MEMBER)) {
				Object controller = msg.getHeader(ScopeManagement.SCOPE_LEADER_ID);
				if (controller != null) {
					if (this.ImTheScopeLeader()) {
						this.addMember(controller.toString());								
					}
				}
			}else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_MEMBERS)) {				
				List<String> instances = this.getMembers();
				if (instances != null && instances.size()>0) {
					String sinstances = "";
					for (String inst : instances) {
						sinstances += inst + ",";
					}
					CMessage resmsg = new CMessage();
					resmsg.setFrom(this.getId().toString());
					resmsg.setReplyTo(this.getId().toString());
					resmsg.setTo(msg.getFrom());
					resmsg.setCorrelation(msg.getCorrelation());
					resmsg.setObject("re." + msg.getObject());
					resmsg.setBody(sinstances);
					try {
						this.coreExtension.getCubeAgent().getCommunicator().sendMessage(resmsg);
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (msg.getObject().equalsIgnoreCase(ScopeManagement.ADD_SCOPE_MEMBER)) {
				Object controller = msg.getHeader("controller");
				if (controller != null) {
					if (this.ImTheScopeLeader()) {
						this.addMember(controller.toString());								
					}
				}
			} else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_RUNTIME_INSTANCE)) {
				List<CInstance> tmps = this.coreExtension.getCubeAgent().getRuntimeModel().getCInstances(this.coreExtension.getExtensionFactory().getExtensionId(), Scope.NAME);
				String scope = null;
				if (tmps != null && tmps.size()>0) {
					scope = tmps.get(0).getId().toString();
				}							
				CMessage resmsg = new CMessage();
				resmsg.setFrom(this.getId().toString());
				resmsg.setReplyTo(this.getId().toString());
				resmsg.setTo(msg.getFrom());
				resmsg.setCorrelation(msg.getCorrelation());
				resmsg.setObject("re." + msg.getObject());
				resmsg.setBody(scope);
				try {
					//System.out.println("[INFO] CoreController.receiveMessage : sending response to get scope runtime instance:\n" + resmsg.toString());
					this.coreExtension.getCubeAgent().getCommunicator().sendMessage(resmsg);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_NODE_INSTANCE)) {
				List<CInstance> tmps = this.coreExtension.getCubeAgent().getRuntimeModel().getCInstances(this.coreExtension.getExtensionFactory().getExtensionId(), Node.NAME);
				String node = null;
				if (tmps != null && tmps.size()>0) {
					node = tmps.get(0).getId().toString();
				}							
				CMessage resmsg = new CMessage();
				resmsg.setFrom(this.getId().toString());
				resmsg.setReplyTo(this.getId().toString());
				resmsg.setTo(msg.getFrom());
				resmsg.setCorrelation(msg.getCorrelation());
				resmsg.setObject("re." + msg.getObject());
				resmsg.setBody(node);
				try {
					//System.out.println("[INFO] CoreController.receiveMessage : sending response to get scope runtime instance:\n" + resmsg.toString());
					this.coreExtension.getCubeAgent().getCommunicator().sendMessage(resmsg);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_NODES)) {
				if (this.ImTheScopeLeader()) {
					List<String> result = new ArrayList<String>();
					for (String m : this.getMembers()) {
						CMessage msg2 = new CMessage();
						msg2.setFrom(this.getId().toString());
						msg2.setReplyTo(this.getId().toString());
						msg2.setTo(m);
						msg2.setObject(ScopeManagement.GET_SCOPE_NODE);
						try {
							CMessage resmsg = sendAndWait(msg2);
							if (resmsg != null) {
								Object node = resmsg.getBody();
								if (node != null) {									
									result.add(node.toString());									
								}
							}
						} catch (TimeOutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					String body = "";
					for (String n : result) {
						body += n + ",";
					}
					CMessage resmsg = new CMessage();
					resmsg.setFrom(this.getId().toString());
					resmsg.setReplyTo(this.getId().toString());
					resmsg.setTo(msg.getFrom());
					resmsg.setCorrelation(msg.getCorrelation());
					resmsg.setObject("re." + msg.getObject());
					resmsg.setBody(body);
					try {
						this.coreExtension.getCubeAgent().getCommunicator().sendMessage(resmsg);
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_NODE)) {
				List<CInstance> tmps = this.coreExtension.getCubeAgent().getRuntimeModel().getCInstances(this.coreExtension.getExtensionFactory().getExtensionId(), Node.NAME);
				String node = null;
				if (tmps != null && tmps.size()>0) {
					node = tmps.get(0).getId().toString();
				}							
				CMessage resmsg = new CMessage();
				resmsg.setFrom(this.getId().toString());
				resmsg.setReplyTo(this.getId().toString());
				resmsg.setTo(msg.getFrom());
				resmsg.setCorrelation(msg.getCorrelation());
				resmsg.setObject("re." + msg.getObject());
				resmsg.setBody(node);
				try {
					this.coreExtension.getCubeAgent().getCommunicator().sendMessage(resmsg);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 			
		}		
	}


	public CMessage sendAndWait(CMessage msg) throws TimeOutException {		
		if (msg != null) {
			String to = msg.getTo();
			msg.setCorrelation(++correlation);
			waitingCorrelation = msg.getCorrelation();			
			//System.out.println(msg.toString());
			try {
				this.waitingMessage = null;

				this.coreExtension.getCubeAgent().getCommunicator().sendMessage(msg);					
			} catch (Exception e) {			
				this.coreExtension.getLogger().warning("The CoreController could not send a message to " + to + "!");
			}		
			try {								
				long initialTime = System.currentTimeMillis();
				long currentTime = initialTime;
				long waitingTime = TIMEOUT;
				synchronized (csplock) {		
					while (((currentTime < (initialTime + TIMEOUT)) && waitingTime > 1)
							&& (this.waitingMessage == null)) {					
						csplock.wait(waitingTime);
						currentTime = System.currentTimeMillis();
						waitingTime = waitingTime - (currentTime - initialTime);
					}
				}
			} catch (InterruptedException e) {									
				this.coreExtension.getLogger().warning("The CoreController waits for a response message from " + to + " but no answer! timeout excedded!");
			}			
			return this.waitingMessage;
		} else {
			return null;
		}
	}

	private CMessage waitingMessage = null;
	private long TIMEOUT = 3000;
	private Object csplock = new Object();	
	private static long correlation = 1;
	private long waitingCorrelation = -1;


}
