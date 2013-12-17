package fr.liglab.adele.cube.archetype.designer.model;


public abstract class Property extends AObject {

	private Archetype archetype;
	private String namespace;
    private String name;
    private String id;
    private String documentation;
    
	private Element subject;
	private Element object;
	
	private static int index = 0;
	
	public Property(Archetype archetype, String namespace, String name, String id, String documentation) {
		this.archetype = archetype;
        this.namespace = namespace;
        this.name = name;
        if (id != null) this.id = id; else this.id = "__p"+index++;
        if (documentation != null) this.documentation = documentation; else this.documentation = "";
        
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Element getSubject() {
		return subject;
	}

	public void setSubject(Element subject) {
		this.subject = subject;
		if (subject != null) {
			this.subject.addProperty(this);
		}
	}

	public Element getObject() {
		return object;
	}

	public void setObject(Element object) {		
		this.object = object;
		if (object != null) {
			this.object.addInputProperty(this);
		}
	}

	public String getName() {
		return name;
	}
	
	public Archetype getArchetype() {
		return archetype;
	}

	public void setArchetype(Archetype archetype) {
		this.archetype = archetype;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public void setName(String name) {
		String old = this.name;
		this.name = name;
		getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, name);
	}

	public boolean isUnaryProperty() {
        return this.object != null && this.object instanceof ElementValue;
    }

    public boolean isBinaryProperty() {
        return this.object != null && this.object instanceof ElementDescription;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Property) {
            Property g = (Property)obj;
            if (g.getId() != null && g.getId().equalsIgnoreCase(getId())) {
                return true;
            }  else {
                return false;
            }
        }
        return super.equals(obj);
    }

    public String getFullname() {
        return getNamespace() + ":" + getName();
    }
}
