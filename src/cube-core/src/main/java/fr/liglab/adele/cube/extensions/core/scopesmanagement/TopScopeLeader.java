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
import fr.liglab.adele.cube.archetype.GlobalConfig;
import fr.liglab.adele.cube.extensions.core.CoreExtension;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.TopScopeLeaderConfig;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.extensions.core.model.Scope;
import fr.liglab.adele.cube.util.id.InvalidIDException;

/**
 * Top Scope Leader.
 * 
 * @author debbabi
 *
 */
public class TopScopeLeader implements MessagesListener {

	/**
	 * ID
	 * e.g. cube://localhost:3838/fr.liglab.adele.cube.extensions.core/topscopeleader
	 */
	TopScopeLeaderID id;
	/**
	 * Core Extension
	 */
	private CoreExtension coreExtension = null;
	/**
	 * The Url of the top Scope Leader (even it is not the actual)
	 */
	private String topScopeLeaderURL = null;
	/**
	 * I'm I the top scope leader?
	 */
	private boolean imTheTopScopeLeader = false;
	/**
	 * The list of scope leaders
	 */
	private ScopeLeadersTable scopeLeaders = new ScopeLeadersTable();
	
	/**
	 * Constructor.
	 * 
	 * @param ex
	 */
	public TopScopeLeader(CoreExtension ex) {
		this.coreExtension = ex;
		try {
			this.id = new TopScopeLeaderID(ex);
		} catch (InvalidIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<GlobalConfig> gcs = ex.getCubeAgent().getArchetype().getGlobalConfigs(CoreExtensionFactory.ID, TopScopeLeaderConfig.NAME);
		if (gcs != null && gcs.size() > 0) {
			TopScopeLeaderConfig tsl = (TopScopeLeaderConfig)gcs.get(0);				
			if (tsl != null) {
				if (tsl.getUrl() != null) {
					this.topScopeLeaderURL = tsl.getUrl() + "/"+ ex.getExtensionFactory().getExtensionId() + "/topscopeleader";
					if (tsl.getUrl().equalsIgnoreCase(ex.getCubeAgent().getId().getURI())){	
						// this is the top scope leader!
						setAsTopScopeLeader();
						coreExtension.getLogger().info("I'm the TOP SCOPE LEADER :)");
					} else {
						coreExtension.getLogger().info("I'm not the TOP SCOPE LEADER :(");
					}
				}
			}
		}
		// listen to messages
		try {
			ex.getCubeAgent().getCommunicator().addMessagesListener(this.getId().toString(), this);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the actual Cube agent as the top scope leader
	 */
	private void setAsTopScopeLeader() {		
		imTheTopScopeLeader = true;
	}

	/**
	 * The local cube is the top scope leader?
	 * @return
	 */
	public boolean ImTheTopScopeLeader() {
		return imTheTopScopeLeader;
	}
	
	/**
	 * get the Top scope leader Id
	 * @return
	 */
	public TopScopeLeaderID getId() {
		return this.id;		
	}
	
	/**
	 * Get the list of scope leaders' urls
	 * 
	 * @param type
	 * @return
	 */
	public List<String> getScopeLeaders(String type) {
		if (ImTheTopScopeLeader()) {
			return scopeLeaders.get(type);
		} else {
			CMessage msg = new CMessage();
			msg.setFrom(getId().toString());
			msg.setReplyTo(getId().toString());
			msg.setTo(this.topScopeLeaderURL);
			msg.setObject(ScopeManagement.GET_SCOPE_LEADERS);
			msg.addHeader("type", type);					
			try {
				CMessage resmsg = sendAndWait(msg);
				if (resmsg != null) {
					Object sleaders = resmsg.getBody();
					if (sleaders != null) {
						String[] tmp = sleaders.toString().split(",");
						if (tmp != null && tmp.length>0) {
							List<String> result = new ArrayList<String>();
							for (int i=0; i<tmp.length; i++) {
								result.add(tmp[i]);
							}
							return result;
						}						
					} else {
						return null;
					}
				}
			} catch (TimeOutException e) {				
				e.printStackTrace();
				return null;
			}			
		}
		return null;
	}
	
	/**
	 * Get scope leader of the type and the given local id.
	 *  
	 * @param type
	 * @param localId
	 * @return
	 */
	public String getScopeLeader(String type, String localId) {
		if (ImTheTopScopeLeader()) {
			return scopeLeaders.get(type, localId);
		} else {
			CMessage msg = new CMessage();
			msg.setFrom(getId().toString());
			msg.setReplyTo(getId().toString());
			msg.setTo(this.topScopeLeaderURL);			
			msg.addHeader(ScopeManagement.SCOPE_TYPE, type);
			msg.addHeader(ScopeManagement.SCOPE_LOCAL_ID, localId);
			msg.setObject(ScopeManagement.GET_SCOPE_LEADER);
			try {
				CMessage resmsg = sendAndWait(msg);
				if (resmsg != null) {
					Object controller = resmsg.getBody();
					if (controller != null) {
						System.out.println("[INFO] TopScopeLeader : getScopeLeader.. leader of " + type+ ":" + localId +" is found " + controller.toString());
						return controller.toString();
					} else {
						System.out.println("[INFO] TopScopeLeader : getScopeLeader.. leader of " + type+ ":" + localId +" is not found!");
						return null;
					}
				}
			} catch (TimeOutException e) {				
				e.printStackTrace();
				return null;
			}			
		}
		return null;
	}

	/**
	 * Set the scope leader (scopeleaderId) as a leader of the scope identified by 
	 * the given type and local id.
	 * 
	 * @param type
	 * @param localId
	 * @param scopeleaderId
	 * @return
	 */
	public String setScopeLeader(String type, String localId, String scopeleaderId) {
		if (ImTheTopScopeLeader()) {
			if (scopeLeaders.get(type, localId) != null) {
				System.out.println(".. leader already exists!" + scopeLeaders.get(type, localId));
				return scopeLeaders.get(type, localId);
			} else {
				System.out.println(".. leader does not exists! add it! " + scopeleaderId);
				scopeLeaders.add(type, localId, scopeleaderId);		
				System.out.println(".. leader:" + scopeLeaders.get(type, localId));
				return scopeleaderId;
			}
		} else {
			CMessage msg = new CMessage();
			msg.setFrom(getId().toString());
			msg.setReplyTo(getId().toString());
			msg.setTo(this.topScopeLeaderURL);
			
			msg.addHeader(ScopeManagement.SCOPE_TYPE, type);
			msg.addHeader(ScopeManagement.SCOPE_LOCAL_ID, localId);
			msg.addHeader(ScopeManagement.SCOPE_LEADER_ID, scopeleaderId);	
			
			msg.setObject(ScopeManagement.SET_SCOPE_LEADER);
			
			try {
				CMessage resmsg = sendAndWait(msg);
				if (resmsg != null) {
					Object controller = resmsg.getBody();
					if (controller != null) {
						return controller.toString();
					} else {
						System.out.println("[WARNING] controller == null!");
						return null;
					}
				} else {
					System.out.println("[WARNING] resmsg == null!");
				}
			} catch (TimeOutException e) {				
				e.printStackTrace();
				return null;
			}									
		}
		return null;
	}	
		
	public List<String> getScopeInstances(String type) {
		if (ImTheTopScopeLeader()) {
			return scopeLeaders.getLocalIds(type);
		} else {
			CMessage msg = new CMessage();
			msg.setFrom(getId().toString());
			msg.setReplyTo(getId().toString());
			msg.setTo(this.topScopeLeaderURL);
			
			msg.addHeader(ScopeManagement.SCOPE_TYPE, type);
			
			msg.setObject(ScopeManagement.GET_SCOPE_INSTANCES);
								
			try {
				CMessage resmsg = sendAndWait(msg);
				if (resmsg != null) {
					Object controllers = resmsg.getBody();
					if (controllers != null) {
						String[] tmp = controllers.toString().split(",");
						if (tmp != null && tmp.length>0) {
							List<String> result = new ArrayList<String>();
							for (int i=0; i<tmp.length; i++) {
								result.add(tmp[i]);
							}
							return result;
						}						
					} else {
						return null;
					}
				}
			} catch (TimeOutException e) {				
				e.printStackTrace();
				return null;
			}			
		}
		return null;
	}
	
	class ScopeLeadersTable {
		List<ScopeLeadersTableEntry> entries = new ArrayList<ScopeLeadersTableEntry>();
		public List<String> get(String type) {
			List<String> result = new ArrayList<String>();
			for (ScopeLeadersTableEntry e : entries) {
				if (e.type.equalsIgnoreCase(type)) {
					result.add(e.scopeLeaderId);					
				}
			}
			return result;
		}
		public String get(String type, String localId) {
			for (ScopeLeadersTableEntry e : entries) {
				if (e.type.equalsIgnoreCase(type) && e.localId.equalsIgnoreCase(localId)) {
					return e.scopeLeaderId;					
				}
			}
			return null;
		}
		public List<String> getLocalIds(String type) {
			List<String> result = new ArrayList<String>();
			for (ScopeLeadersTableEntry e : entries) {
				if (e.type.equalsIgnoreCase(type)) {
					result.add(e.localId);					
				}
			}
			return result;
		}
		public void add(String type, String localId, String controllerId) {
			entries.add(new ScopeLeadersTableEntry(type, localId, controllerId));
		}	
	}
	
	class ScopeLeadersTableEntry {
		String type;
		String localId;
		String scopeLeaderId;
		public ScopeLeadersTableEntry(String type, String localId,
				String scopeLeaderId) {		
			this.type = type;
			this.localId = localId;
			this.scopeLeaderId = scopeLeaderId;
		}
		
	}
	
	@Override
	public String toString() {
		String out ="";
		for (ScopeLeadersTableEntry e : this.scopeLeaders.entries) {
			out += "      - " + e.type + " | " + e.localId + " | " + e.scopeLeaderId + "\n";	
		}		
		return out;		
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return this.topScopeLeaderURL;
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
	
	public void receiveMessage(CMessage msg) {
		System.out.println("[INFO] TopScopeLeader : receiveMessage..\n" + msg.toString());
		System.out.println("[INFO] TopScopeLeader : waiting correlation=" + waitingCorrelation);
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
			if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_LEADER)) {
				Object type = msg.getHeader(ScopeManagement.SCOPE_TYPE);
				Object localid = msg.getHeader(ScopeManagement.SCOPE_LOCAL_ID);
				if (type != null && localid != null) {
					if (this.ImTheTopScopeLeader()) {
						String sleader = this.getScopeLeader(type.toString(), localid.toString());
						System.out.println("..... sleader:"+sleader);
						CMessage resmsg = new CMessage();
						resmsg.setFrom(this.getId().toString());
						resmsg.setReplyTo(this.getId().toString());
						resmsg.setTo(msg.getFrom());
						resmsg.setCorrelation(msg.getCorrelation());
						resmsg.setObject("re." + msg.getObject());
						resmsg.setBody(sleader);
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
			}  else if (msg.getObject().equalsIgnoreCase(ScopeManagement.SET_SCOPE_LEADER)) {
				Object type = msg.getHeader(ScopeManagement.SCOPE_TYPE);
				Object localid = msg.getHeader(ScopeManagement.SCOPE_LOCAL_ID);
				Object controller = msg.getHeader(ScopeManagement.SCOPE_LEADER_ID);
				if (type != null && localid != null && controller != null) {
					if (this.ImTheTopScopeLeader()) {
						String contr = this.setScopeLeader(type.toString(), localid.toString(), controller.toString());
						CMessage resmsg = new CMessage();
						resmsg.setFrom(this.getId().toString());
						resmsg.setReplyTo(this.getId().toString());
						resmsg.setTo(msg.getFrom());
						resmsg.setCorrelation(msg.getCorrelation());
						resmsg.setObject("re." + msg.getObject());
						resmsg.setBody(contr);
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
			}else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_LEADERS)) {
				Object type = msg.getHeader(ScopeManagement.SCOPE_TYPE);
				if (type != null) {
					if (this.ImTheTopScopeLeader()) {
						List<String> leaders = this.getScopeLeaders(type.toString());
						if (leaders != null && leaders.size()>0) {
							String sleaders = "";
							for (String leader : leaders) {
								sleaders += leader + ",";
							}
							CMessage resmsg = new CMessage();
							resmsg.setFrom(this.getId().toString());
							resmsg.setReplyTo(this.getId().toString());
							resmsg.setTo(msg.getFrom());
							resmsg.setCorrelation(msg.getCorrelation());
							resmsg.setObject("re." + msg.getObject());
							resmsg.setBody(sleaders);
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
				
			} else if (msg.getObject().equalsIgnoreCase(ScopeManagement.GET_SCOPE_INSTANCES)) {
				Object type = msg.getHeader("type");
				if (type != null) {
					if (this.ImTheTopScopeLeader()) {
						List<String> instances = this.getScopeInstances(type.toString());
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
					}
				}
			} 
		}
	}
	
	private CMessage waitingMessage = null;
	private long TIMEOUT = 3000;
	private Object csplock = new Object();	
	private static long correlation = 1;
	private long waitingCorrelation = -1;

	
}
