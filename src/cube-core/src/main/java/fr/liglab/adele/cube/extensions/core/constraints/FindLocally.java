package fr.liglab.adele.cube.extensions.core.constraints;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

public class FindLocally extends Constraint {

	public static final String NAME = "find-locally";
	
	
	public FindLocally(String v, String id, String description, int priority,
			Archetype archtype) {
		super(id, description, priority, archtype);
		addParameter(v);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String getNamespace() {
		return CoreExtensionFactory.ID;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public boolean isFindingConstraint() {
		return true;
	}

	@Override
	public boolean isCheckingConstraint() {
		return false;
	}

	@Override
	public boolean isPerformingConstraint() {
		return false;
	}

	

}
