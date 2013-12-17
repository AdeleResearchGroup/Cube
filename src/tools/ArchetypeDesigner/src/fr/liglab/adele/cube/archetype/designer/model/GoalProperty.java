package fr.liglab.adele.cube.archetype.designer.model;

public class GoalProperty extends Property {	

	private String resolutionStrategy;
    private String priority;
    private boolean optional=false;
    
    private String group = "";
    
	private int goalSet = 0;
	
	public GoalProperty(Archetype archetype, String namespace, String name, Element subject, Element object,String resolutionStrategy, String priority, String documentation) {
        super(archetype, namespace, name, null, documentation);
        this.resolutionStrategy = resolutionStrategy;
        this.priority = priority;
        optional=false;
        setSubject(subject);
        setObject(object);
        getArchetype().addProperty(this);
    }

	
	
    public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getResolutionStrategy() {
        return resolutionStrategy;
    }

    public void setResolutionStrategy(String resolutionStrategy) {
    	String old = this.resolutionStrategy ;
        this.resolutionStrategy = resolutionStrategy;
        getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, resolutionStrategy);
    }    

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
    	String old=this.priority;
        this.priority = priority;
        //getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, priority);
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
    	boolean old = this.optional;
        this.optional = optional;
        //getArchetype().notifyListeners(ArchetypeListener.OBJECT_UPDATED, old, optional);
    }

	public int getGoalSet() {
		return goalSet;
	}

	public void setGoalSet(int goalSet) {
		this.goalSet = goalSet;
	}
        
}
