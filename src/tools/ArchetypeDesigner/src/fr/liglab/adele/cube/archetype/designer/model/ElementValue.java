package fr.liglab.adele.cube.archetype.designer.model;

public class ElementValue extends Element {
	
	private String value;

    public ElementValue(Archetype archetype, String value) {
        super(archetype);
        this.value = value;
        this.archetype.addElement(this);
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
    	String old = this.value;
        this.value = value;
        getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, value);
        
    }

}
