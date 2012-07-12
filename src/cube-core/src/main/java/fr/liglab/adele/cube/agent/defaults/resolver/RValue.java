package fr.liglab.adele.cube.agent.defaults.resolver;

import java.util.Date;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.util.id.CInstanceUID;

public class RValue {

	/**
	 * Value states
	 */	
	private int state = CInstance.UNRESOLVED;
	private int originalState = CInstance.UNRESOLVED;
	
	/**
	 * Value
	 */
	private CInstanceUID value = null;
	
	private RVariable cspVariable = null;		
	private Object generator = null;
	private Date timestamp = null;
	
	
	public RValue(RVariable v, CInstanceUID value, Object generator) {
		this.cspVariable = v;
		this.value = value;
		this.generator = generator;
		this.timestamp = new Date();	
	}

	public RValue(CInstanceUID value, Object generator) {		
		this.value = value;
		this.state = CInstance.VALID;
		this.originalState = this.state;
		this.generator = generator;
		this.timestamp = new Date();		
	}
	
	public RValue(CInstance value, Object generator) {
		if (value != null) {
			this.value = value.getId();
			this.state = value.getState();
			this.originalState = this.state;
			this.generator = generator;
			this.timestamp = new Date();
		}
	}

	/**
	 * Get the value state
	 * @return
	 */
	public int getState() {
		return this.state;
	}
	
	public int getOriginalState() {
		return this.originalState;
	}
	
	private String getStateAsString() {
		// TODO Auto-generated method stub
		switch(getState()) {
		case CInstance.VALID:
			return "VALID";
		case CInstance.UNCHECKED:
			return "UNCHECKED";
		case CInstance.UNRESOLVED:
			return "UNRESOLVED";
		}
		return "";
	}
	
	/**
	 * Set the value state
	 * @param newState
	 * @return
	 */
	public int setState(int newState) {
		//System.out.println("[CSPValue] changing state from ["+this.state+"] to ["+newState+"]");
		int oldState = this.state;		
		if (newState < 0) {
			newState = 0;
		} 
		if (newState > 2) {
			newState = 2;
		}
		this.state = newState;
		//System.out.println("[CSPValue] changing state from ["+oldState+"] to ["+this.state+"]");
		return oldState;			
	}
	
	public void setRVariable(RVariable v) {
		this.cspVariable = v;
	}
	
	public RVariable getRVariable() {
		return cspVariable;
	}

	public void setVariable(RVariable rVariable) {
		this.cspVariable = rVariable;
		
	}
	
	public CInstanceUID getInstance() {
		return value;
	}

	public Object getGenerator() {
		return generator;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int compareTo(RValue o) {		
		return this.getTimestamp().compareTo(o.getTimestamp());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RValue) {
			if (((RValue)obj).getInstance() != null && this.getInstance() != null) {
				return ((RValue)obj).getInstance().equals(this.getInstance());
			} else {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String out = "";
		if (getInstance() != null) {
			out = getInstance() + "[" + getStateAsString() + "]";
		} else {
			out = "null";
		}
		return out;
	}



}
