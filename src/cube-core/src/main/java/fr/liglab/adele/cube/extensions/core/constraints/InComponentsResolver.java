package fr.liglab.adele.cube.extensions.core.constraints;

import java.util.List;

import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.resolver.RValue;
import fr.liglab.adele.cube.agent.defaults.resolver.RVariable;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.extensions.IConstraintResolver;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.ComponentInstance;
import fr.liglab.adele.cube.util.id.CInstanceID;

public class InComponentsResolver implements IConstraintResolver {

	public String getConstraintName() {
		// TODO Auto-generated method stub
		return InComponents.NAME;
	}

	public String getConstraintNamespace() {
		// TODO Auto-generated method stub
		return CoreExtensionFactory.ID;
	}

	public RValue find(List<RVariable> params, int which, Constraint c) {
		// TODO Auto-generated method stub
		return null;
	}

	public void perform(List<RVariable> params, Constraint c) {
		// TODO Auto-generated method stub
		
	}

	public void cancel(List<RVariable> params, Constraint c) {
		// TODO Auto-generated method stub
		
	}

	public boolean check(List<RVariable> params, Constraint c) {
		RVariable v12 = params.get(0);		
		if (v12 != null) {
			CubeAgent agent = v12.getResolutionGraph().getCubeAgent();
			int valuee = 0;			
			CInstance instance = agent.getRuntimeModel().getCInstance(v12.getRValue().getInstance());		
			if (instance != null && instance instanceof ComponentInstance) {
				for (CInstanceID inComp : ((ComponentInstance)instance).getInComponents()) {
					//TODO verify remote components type!!
					//CInstance inCompInst = getCubeInstance().getRuntimeModel().getCObjectInstance(inComp);
					//if (inCompInst != null && inCompInst.getCObjectType().equals(inComponentsType)) {
						valuee = valuee + 1;
					//}
				}
			}			
			if (valuee <= ((InComponents)c).getMax()) {
				return true;
			} else {
				//System.out.println("[InComponent] returns false!");
				return false;
			}	
		}
		return false;
	}

}
