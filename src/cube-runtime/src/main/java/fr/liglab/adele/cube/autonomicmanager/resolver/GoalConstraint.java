package fr.liglab.adele.cube.autonomicmanager.resolver;

import fr.liglab.adele.cube.archetype.ResolutionStrategy;

/**
 * User: debbabi
 * Date: 9/20/13
 * Time: 5:28 PM
 */
public class GoalConstraint extends Constraint {

    ResolutionStrategy resolutionStrategy = ResolutionStrategy.Find;

    private String currentSolution = null;

    public GoalConstraint(ResolutionGraph resolutionGraph, String archetypePropertyName, ResolutionStrategy resolutionStrategy, Variable subject, Variable object) {
        super(resolutionGraph, archetypePropertyName, subject, object);
        this.resolutionStrategy = resolutionStrategy;

    }

    public ResolutionStrategy getResolutionStrategy() {
        return resolutionStrategy;
    }

    public void setResolutionStrategy(ResolutionStrategy resolutionStrategy) {
        this.resolutionStrategy = resolutionStrategy;
    }

    public String getCurrentSolution() {
        return currentSolution;
    }

    public void setCurrentSolution(String currentSolution) {
        this.currentSolution = currentSolution;
    }
}
