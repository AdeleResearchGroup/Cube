package fr.liglab.adele.cube.extensions.core.constraints;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

public class InScope extends Constraint {

	public static final String NAME = "in-scope";
	
	public InScope(String v1, String v2, String id, String description,
			int priority, Archetype archtype) {
		super(id, description, priority, archtype);
		addParameter(v1);
		addParameter(v2);
	}	
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getNamespace() {
		return CoreExtensionFactory.ID;
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public boolean isFindingConstraint() {
		return false;
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
