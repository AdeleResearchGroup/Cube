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

import java.util.ArrayList;
import java.util.List;

/**
 * Cube Agent Configuration.
 * 
 * @author debbabi
 *
 */
public class AgentConfig {
	
	public static final String DEFAULT_HOST = "localhost";
	public static final long DEFAULT_PORT = 38000;
	public static final String DEFAULT_RESOLVER = "default-resolver";
	public static final String DEFAULT_COMMUNICATOR = "socket-communicator";
	
	private String host = DEFAULT_HOST;
	private long port = DEFAULT_PORT;
	private String archetypeUrl;
	private String resolverName = DEFAULT_RESOLVER;
	private String communicatorName = DEFAULT_COMMUNICATOR;
	private boolean debug = false;
	private boolean perf = false;
	private boolean persist = false;
		
	private List<AgentExtensionConfig> extensions = new ArrayList<AgentExtensionConfig>();
	
	public AgentConfig() {
		
	}
	
	public AgentConfig(String host, long port, String archetypeUrl) {
		super();
		this.host = host;
		this.port = port;
		this.archetypeUrl = archetypeUrl;
	}
	
	public AgentConfig(String host, long port, String archetypeUrl, boolean debug,
			boolean persist, boolean perf) {
		super();
		this.host = host;
		this.port = port;
		this.debug = debug;
		this.persist = persist;
		this.perf = perf;
		this.archetypeUrl = archetypeUrl;
	}
		
	public String getHost() {
		return host;
	}
	public long getPort() {
		return port;
	}

	public String getResolverName() {
		return resolverName;
	}

	public void setResolverName(String resolverName) {
		this.resolverName = resolverName;
	}

	public String getCommunicatorName() {
		return communicatorName;
	}

	public void setCommunicatorName(String communicatorName) {
		this.communicatorName = communicatorName;
	}

	public boolean isDebug() {
		return debug;
	}
	public boolean isPersist() {
		return persist;
	}

	public boolean isPerf() {
		return perf;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(long port) {
		this.port = port;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public String getArchetypeUrl() {
		return archetypeUrl;
	}

	public void setArchetypeUrl(String archetypeUrl) {
		this.archetypeUrl = archetypeUrl;
	}

	public List<AgentExtensionConfig> getExtensions() {
		return extensions;
	}

	public void addExtension(AgentExtensionConfig aec) {
		this.extensions.add(aec);
	}
	
	public void setExtensions(List<AgentExtensionConfig> extensions) {
		this.extensions = extensions;
	}

	public void setPerf(boolean booleanValue) {
		this.perf = booleanValue;
	}
	
	
}
