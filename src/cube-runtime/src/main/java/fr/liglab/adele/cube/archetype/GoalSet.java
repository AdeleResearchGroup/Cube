package fr.liglab.adele.cube.archetype;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of GoalSet Properties
 *
 * User: debbabi
 * Date: 9/1/13
 * Time: 8:09 PM
 */
public class GoalSet {
    Archetype archetype;
    String id;
    String documentation;
    private static int index = 0;

    List<GoalProperty> goals = new ArrayList<GoalProperty>();

    public GoalSet(Archetype archetype, String id, String documentation) throws ArchetypeException {
        this.archetype = archetype;
        if (id != null) this.id = id; else this.id = "__g"+index++;
        this.documentation = documentation;
        //this.archetype.addGoalSet(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }

    public List<GoalProperty> getGoals() {
        return goals;
    }

    public void addGoal(GoalProperty goal) {
        this.goals.add(goal);
    }
}
