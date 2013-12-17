package fr.liglab.adele.cube.archetype.designer.model;

import java.util.ArrayList;
import java.util.List;

public class ElementDescription extends Element {
	private String namespace;
    private String name;
    private String documentation = "";
    //protected String label = "";
    
    private static int indexC = 1;
    private static int indexN = 1;
    private static int indexS = 1;
    private static int indexM = 1;
    
    //private List<DescriptionProperty> descriptionProperties;

    private boolean showProperties = true;
    
    public ElementDescription(Archetype archetype, String namespace, String name, String id) {
        super(archetype, id);
        this.namespace = namespace;
        this.name = name;        
        //descriptionProperties = new ArrayList<DescriptionProperty>();     
        if (id == null || id.equalsIgnoreCase("")) {
        	
	        if (getName() != null && getName().equalsIgnoreCase("component"))	        	
	        	setId(getName()+archetype.getElements().size());
	        else if (getName() != null && getName().equalsIgnoreCase("node"))
	        	setId(getName()+archetype.getElements().size());
	        if (getName() != null && getName().equalsIgnoreCase("scope"))
	        	setId(getName()+archetype.getElements().size());
	        if (getName() != null && getName().equalsIgnoreCase("master"))
	        	setId(getName()+archetype.getElements().size());
        }
        this.archetype.addElement(this);
    }

    public ElementDescription(Archetype archetype, String namespace, String name, String id, String documentation) {
        this(archetype, namespace, name, id);
        this.documentation = documentation;        
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
	public boolean isShowProperties() {
		return showProperties;
	}

	public void setShowProperties(boolean showProperties) {
		boolean old = this.showProperties;
		this.showProperties = showProperties;
		getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, showProperties);
	}

	public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    public synchronized List<DescriptionProperty> getDescriptionProperties() {
    	List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
    	for (Property p : getProperties()) {
    		if (p instanceof DescriptionProperty) {
    			result.add((DescriptionProperty) p);
    		}
    	}
        return result;
    }
/*
    public synchronized void addDescriptionProperty(DescriptionProperty property) {
        property.setSubject(this);
        this.descriptionProperties.add(property);
    }

    

    public List<DescriptionProperty> getUnaryDescriptionProperties() {
        List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
        for (DescriptionProperty c : getDescriptionProperties()) {
            if (c.getObject() != null && c.getObject() instanceof ElementValue)
                result.add(c);
        }
        return result;
    }

    public List<DescriptionProperty> getBinaryDescriptionProperties() {
        List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
        for (DescriptionProperty c : getDescriptionProperties()) {
            if (c.getObject() != null && c.getObject() instanceof ElementDescription)
                result.add(c);
        }
        return result;
    }
*/
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ElementDescription) {
            ElementDescription e = ((ElementDescription)obj);
            if (e.getName() != null && e.getName().equalsIgnoreCase(getName()) &&
                    e.getNamespace() != null && e.getNamespace().equalsIgnoreCase(e.getNamespace()) &&
                    e.getId() != null && e.getId().equalsIgnoreCase(e.getId())) {
                return true;
            } else {
                return false;
            }
        }
        return super.equals(obj);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String getFullname()  {
        return getNamespace()+":"+getName();
    }
}
