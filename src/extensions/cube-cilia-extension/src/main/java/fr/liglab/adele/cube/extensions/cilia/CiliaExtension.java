package fr.liglab.adele.cube.extensions.cilia;

import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.ComponentInstance;
import fr.liglab.adele.cube.util.id.CInstanceUID;

public class CiliaExtension extends AbstractExtension {


	public CiliaExtension(CubeAgent agent, IExtensionFactory factory,
			AgentExtensionConfig config) {
		super(agent, factory, config);
		
		
	}

	@Override
	public void start() {
		System.out.println("\n\n\nCILIA EXTENSION started.\n\n");
		
		// 1. create cilia chain
		
		// 2. read the runtime model, and see if there exist already cube component instances created!
		for (CInstance i : getCubeAgent().getRuntimeModel().getCInstances(CoreExtensionFactory.ID, Component.NAME, CInstance.VALID)) {
			createMediator(i);
		}
	}
	
	public void validatedInstance(CInstance coi) {
		if (coi != null) {			
			if (coi.getCType().getNamespace().equalsIgnoreCase(CoreExtensionFactory.ID) && coi.getCType().getName().equalsIgnoreCase(Component.NAME)) {
				createMediator(coi);
			}
		}
	}
	
	/**
	 * Create Cilia Mediator and add it to the cilia local chain
	 * @param i
	 */
	private void createMediator(CInstance i) {		
		String componentType = i.getCType().getId();
		// TODO create mediator of componentType type and add it to the chain
		
		for (CInstanceUID instanceUID : ((ComponentInstance)i).getOutComponents()) {
			// TODO you can get the object instance of this UID from the runtime model
			CInstance inst = getCubeAgent().getRuntimeModel().getCInstance(instanceUID);
			// this is the next mediator where the actual one is connected.
			// TODO check if this next mediators is already created, if so, create binding between the two
						
		}
		
	}

	public void stop() {
		// remove chain
	}

	public void stateChanged(CInstance coi, int oldState, int newState) {
		// not yet implemented in cube!!
	}

	@Override
	public String toString() {
		String out = "";
		out += "+ CiliaExtension\n";
		
		// TODO print what you want :)
		// when we call the "ext" command, this toString method is called to show 
		// some information about this extension internal work.
		
		return out;
	}


}
