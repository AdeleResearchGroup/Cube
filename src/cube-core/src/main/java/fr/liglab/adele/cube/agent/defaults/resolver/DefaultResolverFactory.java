package fr.liglab.adele.cube.agent.defaults.resolver;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.IResolver;
import fr.liglab.adele.cube.agent.IResolverFactory;

public class DefaultResolverFactory implements IResolverFactory {

	private static final String ID = "default-resolver";
	private static final String VERSION = "1.2";
	
	public String getResolverId() {		
		return ID;
	}

	public String getResolverVersion() {
		return VERSION;
	}

	public IResolver newResolver(CubeAgent agent) {		
		return new DefaultResolver(agent);
	}

}
