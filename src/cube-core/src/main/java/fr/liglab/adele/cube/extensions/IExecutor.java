package fr.liglab.adele.cube.extensions;

public interface IExecutor {
	
	public void run(IExtension c);
	public void stop();
	public IExtension getExtension();
	
}
