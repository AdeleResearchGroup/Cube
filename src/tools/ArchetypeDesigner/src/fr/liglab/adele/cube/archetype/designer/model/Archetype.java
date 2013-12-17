package fr.liglab.adele.cube.archetype.designer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Archetype  {
	
	private static final String DEFAULT_ARCHETYPE_VERSION = "1.0";
	private static final String CUBE_VERSION = "2.0";

	private String filepath = "";
	
    private String id = "net.debbabi.myarchetype";
    private String archetypeDescription = "My Archetype";
    private String version = DEFAULT_ARCHETYPE_VERSION;
    private String cubeVersion = CUBE_VERSION;
        
	private List<Element> elements;  // element_id, element
    private List<Property> properties;
    
    private static int index = 1;
    
    private AObject selectedObject = null;
    
    private List<ArchetypeListener> listeners = new ArrayList<ArchetypeListener>();
    
    private boolean addingGoal = false;
    private boolean addingProperty = false;
    
    private boolean hideDetail = false;
	
    
    public Archetype(String id, String version, String cubeVersion,
			String archetypeDescription) {
		super();
		this.id = id;
		this.version = version;
		this.cubeVersion = cubeVersion;
		this.archetypeDescription = archetypeDescription;
	}

    public Archetype(String filepath) {
    	super();
    	this.filepath = filepath;
    }
    
	public Archetype() {
		this.filepath = null; 
		index++;
    	properties = new ArrayList<Property>();
        elements = new ArrayList<Element>();         
    }
    

	public void hideDetail() {
		if (hideDetail == false) { 
			hideDetail = true;
			for (ElementDescription ed :getElementsDescriptions()) {
				ed.setShowProperties(false);
			}
		}
		else { 
			hideDetail = false;
			for (ElementDescription ed :getElementsDescriptions()) {
				ed.setShowProperties(true);
			}
		}
		
		
	}
	
	public boolean isAddingGoal() {
		return addingGoal;
	}

	public void setAddingGoal(boolean addingGoal) {
		
		this.addingGoal = addingGoal;
	}
	
	public boolean isAddingProperty() {
		return addingProperty;
	}

	public void setAddingProperty(boolean addingProperty) {
		this.addingProperty = addingProperty;
	}

	
	
	public AObject getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(AObject selectedObject) {
		AObject oldObj = this.selectedObject;
		this.selectedObject = selectedObject;
		if (selectedObject == null) {
				
		} else {
			if (selectedObject instanceof Element) {
				if (isAddingGoal()) {				
					if (oldObj != null && oldObj instanceof ElementDescription) {
						GoalProperty gp = new GoalProperty(this, "fr.liglab.adele.cube.core", "", (Element)oldObj, (Element)selectedObject, "F", "1", "");						
						setAddingGoal(false);
						setAddingProperty(false);	
						setSelectedObject(gp);
						return;
					}
				} else if (isAddingProperty()) {
					if (oldObj != null && oldObj instanceof ElementDescription) {
						DescriptionProperty dp = new DescriptionProperty(this, "fr.liglab.adele.cube.core", "", (Element)oldObj, (Element)selectedObject, "");						
						setAddingGoal(false);
						setAddingProperty(false);	
						setSelectedObject(dp);
						return;
					}
				}
			} else {
				
			}
		}	
		setAddingGoal(false);
		setAddingProperty(false);	
		notifyListeners(ArchetypeListener.SELECTED_CHANGED, oldObj, selectedObject);
	}

	public synchronized void addListener(ArchetypeListener listener) {
		if (!this.listeners.contains(listener)) {			
			this.listeners.add(listener);
		}
	}
	
	public synchronized void removeListener(ArchetypeListener listener) {
		this.listeners.remove(listener);
	}
	
	synchronized void notifyListeners(int event, Object oldObj, Object newObj) {
		for (ArchetypeListener l : this.listeners) {
			l.notify(event, oldObj, newObj);
		}
	}
	
    public String getFilepath() {
		return filepath;
	}    	

	public void setFilepath(String filepath) {
		String old = this.filepath;
		this.filepath = filepath;
		notifyListeners(ArchetypeListener.FILEPATH_CHANGED, old, filepath);
	}

	public String getId() {
		return id;
	}
    
	public void setId(String id) {
		this.id = id;
	}
	
	public String getArchetypeDescription() {
		return archetypeDescription;
	}
	
	public void setArchetypeDescription(String archetypeDescription) {
		String old = this.archetypeDescription;
		this.archetypeDescription = archetypeDescription;
		notifyListeners(ArchetypeListener.ARCHETYPE_DESCRIPTION_CHANGED, old, archetypeDescription);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		String old = this.version;
		this.version = version;
		notifyListeners(ArchetypeListener.ARCHETYPE_VERSION_CHANGED, old, version);
	}

	public String getCubeVersion() {
		return cubeVersion;
	}

	public void setCubeVersion(String cubeVersion) {
		String old = this.cubeVersion;
		this.cubeVersion = cubeVersion;
		notifyListeners(ArchetypeListener.CUBE_VERSION_CHANGED, old, cubeVersion);
	}

	public synchronized List<Element> getElements() {        
        return this.elements;
    }
    
    public synchronized List<Property> getProperties() {        
        return this.properties;
    }
    
    public synchronized List<DescriptionProperty> getDescriptionProperties() {
        List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
        for (Property e : this.properties) {            
            if (e instanceof DescriptionProperty) {
                result.add((DescriptionProperty)(e));
            }
        }
        return result;
    }
    
    public synchronized List<GoalProperty> getGoalProperties() {
        List<GoalProperty> result = new ArrayList<GoalProperty>();
        for (Property e : this.properties) {            
            if (e instanceof GoalProperty) {
                result.add((GoalProperty)(e));
            }
        }
        return result;
    }
    
    public synchronized Element getElement(String id) {
    	if (id != null) {
	    	for (Element e : this.elements) {
	    		if (e.getId() != null && e.getId().equalsIgnoreCase(id)) {
	    			return e;
	    		}
	    	}
    	}
    	return null;
    }
    
    public synchronized ElementDescription getElementDescription(String id) {
    	if (id != null) {
	    	for (Element e : this.elements) {
	    		if (e instanceof ElementDescription) {
		    		if (e.getId() != null && e.getId().equalsIgnoreCase(id)) {
		    			return (ElementDescription) e;
		    		}
	    		}
	    	}
    	}
    	return null;
    }
    
    
    public synchronized boolean addElement(Element element) {
        if (element != null) {        	        	
            if( getElement(element.getId()) == null) {
		        this.elements.add(element);
		        notifyListeners(ArchetypeListener.NEW_OBJECT, null, element);
		        return true;
            }
        }
        return false;
    }
    
    public synchronized boolean addProperty(Property property) {
        if (property != null) {        	
            this.properties.add(property);
            notifyListeners(ArchetypeListener.NEW_OBJECT, null, property);
            return true;
        }
        return false;
    }

	@Override
	public String toString() {
		return "Archetype [id=" + id + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Archetype other = (Archetype) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
    
	public Archetype copy() {
		return new Archetype(this.id, this.version, this.cubeVersion, this.archetypeDescription);
	}

	public void removeObject(AObject a) {
		if (a != null) {			
			boolean removed = false;
			
			if (a instanceof Element) {		
				List<Property> toBeRemoved = new ArrayList<Property>();
				for (Property p : ((Element) a).getProperties()) {
					toBeRemoved.add(p);
				}
				for (Property p : ((Element) a).getInputProperties()) {
					toBeRemoved.add(p);
				}
				for (Property p : toBeRemoved) {
					removeObject(p);
				}
				/*
				toBeRemoved = new ArrayList<Property>();
				synchronized (this.properties) {
					for (Property p : this.properties) {
						if (p.getObject() == a) {
							toBeRemoved.add(p);
						}						
					}
				}*/	
				for (Property p : toBeRemoved) {
					removeObject(p);
				}
				synchronized (this.elements) {
					removed = this.elements.remove(a);
					notifyListeners(ArchetypeListener.DELETED_OBJECT, a, null);
				}
			}
			else 
			if (a instanceof Property) {				
				synchronized (this.properties) {
					removed = this.properties.remove(a);
				}
				((Property) a).getSubject().removeProperty(((Property) a));				
				((Property) a).getObject().removeInputProperty(((Property) a));
				if (removed == false) {System.out.println("[ARCH] not found property: " +a);}
				notifyListeners(ArchetypeListener.DELETED_OBJECT, a, null);
				
			}	
			
			/*
			if (removed == true) {				
				// remove dependencies
				
				notifyListeners(ArchetypeListener.DELETED_OBJECT, a, null);
			}
			*/
		}
	}
/*
	public synchronized void removeProperty(Property selectedProperty2) {
		if (selectedProperty2 != null) {
			Set set = this.properties.keySet();
	        Iterator itr = set.iterator();
	        boolean removed = false;
	        while (itr.hasNext())
	        {
	            Object o = itr.next();
	            if (o.toString().equalsIgnoreCase(selectedProperty2.getId())) {
	                itr.remove(); 
	                removed=true;
	                break;
	            }
	        }
	        if (removed == true) {
	        	System.out.println("DELETED!");
	        } else {
	        	System.out.println("NOT DELETED!");
	        }
		}
	}
	*/

	public synchronized List<ElementDescription> getElementsDescriptions() {
		List<ElementDescription> result = new ArrayList<ElementDescription>();
		for (Element e : this.elements) {
			if (e instanceof ElementDescription) {
				result.add((ElementDescription)e);
			}
		}
		return result;
	}
}

