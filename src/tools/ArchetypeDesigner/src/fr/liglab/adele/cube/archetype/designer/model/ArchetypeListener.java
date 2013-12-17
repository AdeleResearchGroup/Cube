package fr.liglab.adele.cube.archetype.designer.model;

public interface ArchetypeListener {
	final int SELECTED_CHANGED = 1;
	final int FILEPATH_CHANGED = 3;
	final int ARCHETYPE_DESCRIPTION_CHANGED = 4;
	final int ARCHETYPE_VERSION_CHANGED = 5;
	final int CUBE_VERSION_CHANGED = 6;
	final int NEW_OBJECT = 7;	
	final int DELETED_OBJECT = 9;
	final int OBJECT_UPDATED = 10;
	void notify(int event, Object oldObj, Object newObj);
}
