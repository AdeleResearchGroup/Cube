package fr.liglab.adele.cube.archetype.designer.model;

import java.util.List;

public interface IArchetypeService {
	
	Archetype newArchetype();
	Archetype loadArchetype(String filename);
	Archetype getArchetype(String filename);	
	boolean saveArchetype(Archetype arch);
	boolean closeArchetype(Archetype arch);
	List<Archetype> getArchetypes();
	void setCurrentArchetype(Archetype a);
	Archetype getCurrentArchetype();
	void addListener(ArchetypeServiceListener listener);	
	void removeListener(ArchetypeServiceListener listener);
	
}
