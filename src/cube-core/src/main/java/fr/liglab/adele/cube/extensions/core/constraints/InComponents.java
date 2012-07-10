package fr.liglab.adele.cube.extensions.core.constraints;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

public class InComponents extends Constraint {

	String max;
	
	public InComponents(String v, String max, String id, String description, int priority,
			Archetype archtype) {
		super(id, description, priority, archtype);
		addParameter(v);
		this.max = max;
	}

	public static final String NAME = "in-components";
	
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
		return 1;
	}

	@Override
	public boolean isFindingConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCheckingConstraint() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isPerformingConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toXMLString(String xmlns) {
		String out = "";
		String tmp = "";
		for (int i=0; i<getArity(); i++) {
			tmp += "v"+i+"=\""+getParameter(i)+"\" ";
		}
				 
		out += "<"+xmlns+":"+getName()+" "+tmp+" max=\""+this.max+"\" p=\""+getPriority()+"\"/>\n";		
		return out;
	}

	public int getMax() {
		// TODO Auto-generated method stub
		return new Integer(this.max).intValue();
	}
}
