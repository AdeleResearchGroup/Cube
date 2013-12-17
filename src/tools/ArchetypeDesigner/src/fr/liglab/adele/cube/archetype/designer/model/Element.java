package fr.liglab.adele.cube.archetype.designer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Element extends AObject {
	
	protected Archetype archetype;
    protected String id;    
    private static int index = 0;	
	
    private List<Property> properties = new ArrayList<Property>();
    
    private List<Property> inputproperties = new ArrayList<Property>();
    
	public Element(Archetype archetype) {
		this.archetype = archetype;
        this.id = "e"+index++;        
	}
	
	public Element(Archetype archetype, String id) {
		this.archetype = archetype;
        if (id == null) this.id = "e"+index++; else this.id = id;        
	}

	public synchronized void addProperty(Property p) {
		if (p != null && !this.properties.contains(p)) {
			this.properties.add(p);
		}
	}
	
	public synchronized void removeProperty(Property p) {
		this.properties.remove(p);
	}
	
	public synchronized List<Property> getProperties() {
		return properties;
	}
	
	public synchronized void addInputProperty(Property p) {
		if (p != null && !this.inputproperties.contains(p)) {
			this.inputproperties.add(p);
		}
	}
	
	public synchronized void removeInputProperty(Property p) {
		this.inputproperties.remove(p);
	}
	
	public synchronized List<Property> getInputProperties() {
		return inputproperties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Archetype getArchetype() {
		return archetype;
	}

	public void setArchetype(Archetype archetype) {
		this.archetype = archetype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		String old = this.id;
		this.id = id;
		getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, id);
	}
		
	@Override
	public String toString() {
		
		return this.id;
	}
		
}
