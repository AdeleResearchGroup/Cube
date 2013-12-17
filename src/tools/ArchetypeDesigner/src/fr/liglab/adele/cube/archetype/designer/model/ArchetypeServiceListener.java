package fr.liglab.adele.cube.archetype.designer.model;

public interface ArchetypeServiceListener {
	
	final int CURRENT_ARCHETYPE_CHANGED = 100;
	
	void notify(int event, Object oldObj, Object newObj);
}
