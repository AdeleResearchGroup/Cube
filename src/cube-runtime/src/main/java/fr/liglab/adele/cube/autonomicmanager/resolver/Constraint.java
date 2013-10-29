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
    private String currentSolution = null;
    private String currentProblem = null;

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
    public String getCurrentSolution() {
        return currentSolution;
    }

    public void setCurrentSolution(String currentSolution) {
        this.currentSolution = currentSolution;
    }

    public String getCurrentProblem() {
        return currentProblem;
    }

    public void setCurrentProblem(String currentProblem) {
        this.currentProblem = currentProblem;
    }

    public ResolutionGraph getResolutionGraph() {
        return resolutionGraph;
    }

    public void setResolutionGraph(ResolutionGraph resolutionGraph) {
        this.resolutionGraph = resolutionGraph;
    }

    public String print(String indation) {
        String out = indation;
        //out += (subject instanceof PrimitiveVariable?((PrimitiveVariable) subject).getValue():((MultiValueVariable)subject).getDescription().getName());
        out += " ---"+getArchetypePropertyName()+"---> ";
        out += (object instanceof PrimitiveVariable?((PrimitiveVariable) object).getValue():((MultiValueVariable)object).getDescription().getName());
        for (Constraint c : object.getConstraints()) {
            out += "\n" + c.print(indation + "    ");
        }
        return out;
    }
}
