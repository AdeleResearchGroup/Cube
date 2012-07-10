package fr.liglab.adele.cube.extensions.core.constraints;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

public class ComponentsPerNode extends Constraint {
		
	String max = "10";
	String type = null;
	
	public ComponentsPerNode(String v, String type, String max, String id, String description, int priority,
			Archetype archtype) {
		super(id, description, priority, archtype);
		addParameter(v);
		this.type = type;
		this.max = max;
	}

	public static final String NAME = "components-per-node";

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
				 
		out += "<"+xmlns+":"+getName()+" "+tmp+" type=\""+getComponentsType()+"\" max=\""+this.max+"\" p=\""+getPriority()+"\"/>\n";		
		return out;
	}

	public String getComponentsType() {
		return this.type;
	}
	
	public int getMax() {
		return new Integer(this.max).intValue();
	}
}
