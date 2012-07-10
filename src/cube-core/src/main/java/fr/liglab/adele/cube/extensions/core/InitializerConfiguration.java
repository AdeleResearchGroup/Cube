package fr.liglab.adele.cube.extensions.core;

import fr.liglab.adele.cube.agent.ExtensionConfiguration;

public class InitializerConfiguration extends ExtensionConfiguration {
	
	public static final String NAME = "initializer";
	
	private String scopeId;
	private String scopeType;
	private String nodeId;
	private String nodeType;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public String getScopeId() {
		return scopeId;
	}

	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
		
	
}
