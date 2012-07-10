package fr.liglab.adele.cube.extensions.core.constraints;

import java.util.List;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.resolver.RValue;
import fr.liglab.adele.cube.agent.defaults.resolver.RVariable;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.Type;
import fr.liglab.adele.cube.extensions.IConstraintResolver;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.NodeInstance;
import fr.liglab.adele.cube.util.id.CInstanceID;

public class ComponentsPerNodeResolver implements IConstraintResolver {

	public String getConstraintName() {
		return ComponentsPerNode.NAME;
	}

	public String getConstraintNamespace() {
		return CoreExtensionFactory.ID;
	}

	public RValue find(List<RVariable> params, int which, Constraint c) {
		// not supported
		return null;
	}

	public void perform(List<RVariable> params, Constraint c) {
		// not supported		
	}

	public void cancel(List<RVariable> params, Constraint c) {
		// not supported		
	}

	public boolean check(List<RVariable> params, Constraint c) {
		RVariable v12 = params.get(0);		
		if (v12 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
			int valuee = 0;				
			ComponentsPerNode cpn = (ComponentsPerNode)c;
			int max = cpn.getMax();
			String type = cpn.getComponentsType();
			
			if (type == null) {
				CInstance instance = cpn.getArchtype().getCubeAgent().getRuntimeModel().getCInstance( v12.getRValue().getInstance() );							
				if (instance != null && instance instanceof NodeInstance) {
					for (CInstanceID inComp : ((NodeInstance)instance).getComponentInstances()) {					
						valuee = valuee + 1;					
					}
				} else {
					
				}
				if (valuee <= max) {					
					return true;
				} else {
					
					return false;
				}	
			} else {			
				Type constrainedType = cpn.getArchtype().getType(type);				
				if (constrainedType != null) {
					CInstance instance = cpn.getArchtype().getCubeAgent().getRuntimeModel().getCInstance( v12.getRValue().getInstance() );							
					if (instance != null && instance instanceof NodeInstance) {
						for (CInstanceID inComp : ((NodeInstance)instance).getComponentInstances(constrainedType)) {					
							valuee = valuee + 1;					
						}
					} else {
						
					}
					if (valuee <= max) {					
						return true;
					} else {
						
						return false;
					}	
				} else {
					return false;
				}
			}			
		}
		return false;
	}

}
