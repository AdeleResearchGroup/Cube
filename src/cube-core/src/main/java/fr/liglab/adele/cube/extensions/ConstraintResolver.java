/*
 * Copyright 2012 Adele Team LIG (http://www-adele.imag.fr/)
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

import java.util.List;
import java.util.Vector;

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.MessagesListener;
import fr.liglab.adele.cube.TimeOutException;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.archetype.ArchtypeParsingException;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.Variable;
import fr.liglab.adele.cube.util.id.CPredicateID;

/**
 * Predicate of the user constraints.
 * 
 * This is an abstract class for Binary and Unary predicates, and will be implemented/specialized 
 * by user-provided predicates
 * 
 * @author debbabi
 *
 */
public abstract class ConstraintResolver implements MessagesListener {
	
	private CPredicateID cid = null;
	protected Constraint constraint;	
	private ConstraintResolver parent = null;
	private Vector<ConstraintResolver> childs = new Vector<ConstraintResolver>();
	private int priority = 0;			
	
	protected static CubeLogger log = null; 
	
	public ConstraintResolver(Constraint constraint, ConstraintResolver parent) throws ArchtypeParsingException {
		this.parent = parent;
		this.constraint = constraint;	
		log = new CubeLogger(constraint.getArchtype().getCubeAgent().getCubePlatform().getBundleContext(), ConstraintResolver.class.getName());
	}
	
	public ConstraintResolver(Constraint constraint, ConstraintResolver parent, int priority) throws ArchtypeParsingException {
		this(constraint, parent);
		setPriority(priority);
	}
	
	public void setId(CPredicateID id) {
		this.cid = id;		
	}	

	public CPredicateID getId() {
		return this.cid;
	}
	
	public void setPriority(int priority) {
		if (priority < 0) {
			this.priority = 0;
		} else {
			this.priority = priority;
		}
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public void addPredicate(ConstraintResolver cp) throws ArchtypeParsingException {		
		if (cp != null) {			
			this.childs.add(cp);		
		}
	}		
	
	public Vector<ConstraintResolver> getPredicates() {
		return childs;
	}

	public Constraint getConstraint() {
		return this.constraint;
	}
	
	protected CubeAgent getCubeInstance() {
		if (this.constraint != null) {
			return this.constraint.getArchtype().getCubeAgent();
		} else {
			return null;
		}
	}
	
	
	public abstract List<Variable> getCVariables();
	
	public ConstraintResolver getParent() {
		return this.parent;
	}
	
	public abstract void run();
	public void stop() {
		
	}
	public abstract String getName();
	public abstract String getNamespace();
	/**
	 * 
	 * @param xmlns xmlns shortcut!!
	 * @return
	 */
	public abstract String toXMLString();

	@Override
	public String toString() {		
		return this.getNamespace() + "." + this.getName();
	}
	
	/**
	 * Send message and wait the response.
	 * 
	 * The predicate is blocked until it receives the response or timeout exception.
	 * 
	 * @param msg
	 * @return
	 * @throws TimeOutException
	 */
	public CMessage sendAndWait(CMessage msg) throws TimeOutException {					
		if (msg != null) {
			String to = msg.getTo();
			msg.setCorrelation(++correlation);
			waitingCorrelation = msg.getCorrelation();			
			//System.out.println(msg.toString());
			try {
				this.waitingMessage = null;
				getCubeInstance().getCommunicator().sendMessage(msg);					
			} catch (Exception e) {			
				log.warning("The predicate " + getName() + " could not send a message to " + to + "!");
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
				log.warning("The predicate " + getName() + " waits for a response message from " + to + " but no answer! timeout excedded!");
			}			
			return this.waitingMessage;
		} else {
			return null;
		}
	}
	
	/**
	 * called when the predicate receives the message. 
	 * It unblocks the waiting thread.
	 * 
	 */
	public void receiveMessage(CMessage msg) {		
		if (msg != null) {			
			if (msg.getCorrelation() == waitingCorrelation) {				
				this.waitingMessage = msg;
				if (csplock != null) {
					synchronized (csplock) {
						csplock.notify();
					}
				}
				waitingCorrelation = -1;
			}
		}
	}
	
	private CMessage waitingMessage = null;
	private long TIMEOUT = 3000;
	private Object csplock = new Object();	
	private static long correlation = 1;
	private long waitingCorrelation = -1;
	
}
