package fr.liglab.adele.cube.agent;

public interface IResolverFactory {
	
	public String getResolverId();
	public String getResolverVersion();
	
	public IResolver newResolver(CubeAgent agent);
	
}
