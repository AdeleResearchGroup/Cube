package fr.liglab.adele.cube.extensions.cilia;

import fr.liglab.adele.cilia.builder.Architecture;
import fr.liglab.adele.cilia.builder.Builder;
import fr.liglab.adele.cilia.exceptions.BuilderConfigurationException;
import fr.liglab.adele.cilia.exceptions.BuilderException;
import fr.liglab.adele.cilia.exceptions.CiliaException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalStateException;
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

	private final String chainId;
	private final CiliaExtensionFactory cfactory;



	public CiliaExtension(CubeAgent agent, IExtensionFactory factory,
			AgentExtensionConfig config) {
		super(agent, factory, config);
		chainId = agent.getArchetype().getId();
		this.cfactory =(CiliaExtensionFactory) getExtensionFactory();  
	}

	@Override
	public void start() {
		System.out.println("\n\n\nCILIA EXTENSION started.\n\n");

		// 1. create cilia chain
		Builder ciliaBuilder = cfactory.getCiliaContext().getBuilder();
		try {
			Architecture chain = ciliaBuilder.create(chainId);
			// 2. read the runtime model, and see if there exist already cube component instances created!
			for (CInstance i : getCubeAgent().getRuntimeModel().getCInstances(CoreExtensionFactory.ID, Component.NAME, CInstance.VALID)) {
				createMediator(i, chain);
			}
			ciliaBuilder.done();
		} catch (CiliaException e) {
			e.printStackTrace();
		}
		//3. Initialize the chain
		try {
			cfactory.getCiliaContext().getApplicationRuntime().startChain(chainId);
		} catch (CiliaIllegalParameterException e) {
			e.printStackTrace();
		} catch (CiliaIllegalStateException e) {
			e.printStackTrace();
		}
	}

	public void validatedInstance(CInstance coi) {
		if (coi != null) {			
			if (coi.getCType().getNamespace().equalsIgnoreCase(CoreExtensionFactory.ID) && coi.getCType().getName().equalsIgnoreCase(Component.NAME)) {
				Builder ciliaBuilder = cfactory.getCiliaContext().getBuilder();
				Architecture chain;
				try {
					chain = ciliaBuilder.get(chainId);
					createMediator(coi, chain);
					ciliaBuilder.done();
				} catch (CiliaException e) {
					e.printStackTrace();
				} 
			}
		}
	}

	/**
	 * Create Cilia Mediator and add it to the cilia local chain
	 * @param i
	 * @throws BuilderException 
	 * @throws BuilderConfigurationException 
	 */
	private void createMediator(CInstance i, Architecture chain) throws BuilderConfigurationException, BuilderException {		
		String componentType = i.getCType().getId();
		// create mediator of componentType type and add it to the chain
		chain.create().mediator().type(componentType).id(i.getLocalId());
		//were are the properties??
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
