package fr.liglab.adele.cube.extensions;

import java.util.List;

import fr.liglab.adele.cube.agent.defaults.resolver.RValue;
import fr.liglab.adele.cube.agent.defaults.resolver.RVariable;
import fr.liglab.adele.cube.archetype.Constraint;

public interface IConstraintResolver {
	
	public String getConstraintName();
	public String getConstraintNamespace();
	
	/**
	 * Find 
	 * @param params
	 * @param which which param do you want to find value for it? 
	 * @return
	 */
	public RValue find(List<RVariable> params, int which, Constraint c);
	public void perform(List<RVariable> params, Constraint c);
	public void cancel(List<RVariable> params, Constraint c);
	public boolean check(List<RVariable> params, Constraint c);
}
