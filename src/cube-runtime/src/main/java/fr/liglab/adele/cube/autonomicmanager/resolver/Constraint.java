package fr.liglab.adele.cube.autonomicmanager.resolver;

import fr.liglab.adele.cube.archetype.Property;

import java.io.Serializable;

/**
 * User: debbabi
 * Date: 9/19/13
 * Time: 3:01 PM
 */
public class Constraint implements Serializable {

    ResolutionGraph resolutionGraph;

    String archetypePropertyName;   // e.g., fr.liglab.adele.cube.core:OnNode

    Variable subject;
    Variable object;

    public Constraint(ResolutionGraph resolutionGraph, String archetypePropertyName, Variable subject, Variable object) {
        this.resolutionGraph = resolutionGraph;
        this.archetypePropertyName = archetypePropertyName;
        this.subject = subject;
        this.object = object;
    }

    public Variable getObject() {
        return object;
    }

    public void setObject(Variable object) {
        this.object = object;
    }

    public Variable getSubject() {
        return subject;
    }

    public void setSubject(Variable subject) {
        this.subject = subject;
    }

    public String getArchetypePropertyName() {
        return archetypePropertyName;
    }

    public void setArchetypePropertyName(String archetypePropertyName) {
        this.archetypePropertyName = archetypePropertyName;
    }

    public ResolutionGraph getResolutionGraph() {
        return resolutionGraph;
    }

    public void setResolutionGraph(ResolutionGraph resolutionGraph) {
        this.resolutionGraph = resolutionGraph;
    }
}
