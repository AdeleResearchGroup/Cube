package fr.liglab.adele.cube.archetype.designer.model;

public class DescriptionProperty extends Property {
	
	public DescriptionProperty(Archetype archetype, String namespace, String name, Element subject, Element object, String documentation) {
        super(archetype, namespace, name, null, documentation);
        setSubject(subject);
        setObject(object);
        getArchetype().addProperty(this);
    }
}
