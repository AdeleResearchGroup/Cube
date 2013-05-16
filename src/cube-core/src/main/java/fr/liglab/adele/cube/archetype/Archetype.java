/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
 * LIG Laboratory (http://www.liglab.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package fr.liglab.adele.cube.archetype;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * See Concept Maps: http://kremer.cpsc.ucalgary.ca/dissertation/Ch1.html
 *
 * Author: debbabi
 * Date: 4/11/13
 * Time: 3:12 PM
 */
public class Archetype {

    private String id = "";
    private String description = "";
    private String version = "1.0";
    private String cubeVersion = "2.0";

    private Map<String, Element> elements = new HashMap<String, Element>();
    private Map<String, Goal> goals = new HashMap<String, Goal>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCubeVersion() {
        return cubeVersion;
    }

    public void setCubeVersion(String cubeVersion) {
        this.cubeVersion = cubeVersion;
    }

    public List<Element> getElements() {
        List<Element> result = new ArrayList<Element>();
        for (String e : this.elements.keySet()) {
            result.add(this.elements.get(e));
        }
        return result;
    }

    public Element getElement(String id) {
        return this.elements.get(id);
    }

    public boolean addElement(Element element) throws ArchetypeException {
        if (element != null) {
            for (Element e : getElements()) {
                if (e.getId() != null && e.getId().equalsIgnoreCase(element.getId())) {
                    throw new ArchetypeException("Another element with the same id '"+element.getId()+"' already exists in the Archetype! It cannot be added!");
                }
            }
            this.elements.put(element.getId(), element);
            return true;
        }
        return false;
    }

    public List<Goal> getGoals() {
        List<Goal> result = new ArrayList<Goal>();
        for (String e : this.goals.keySet()) {
            result.add(this.goals.get(e));
        }
        return result;
    }

    public List<Objective> getObjectives() {
        List<Objective> result = new ArrayList<Objective>();
        for (Goal g : getGoals()) {
            for (Objective o : g.getObjectives()) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Objective> getUnaryObjectives() {
        List<Objective> result = new ArrayList<Objective>();
        for (Goal g : getGoals()) {
            for (Objective o : g.getObjectives()) {
                if (o.getObject() != null && !(o.getObject() instanceof Element))
                    result.add(o);
            }
        }
        return result;
    }

    public List<Objective> getBinaryObjectives() {
        List<Objective> result = new ArrayList<Objective>();
        for (Goal g : getGoals()) {
            for (Objective o : g.getObjectives()) {
                if (o.getObject() != null && o.getObject() instanceof Element)
                    result.add(o);
            }
        }
        return result;
    }

    public Goal getGoal(String id) {
        return this.goals.get(id);
    }

    public void addGoal(Goal goal) throws ArchetypeException {
        if (goal != null) {
            for (Goal g : getGoals()) {
                if (g.getId() != null && g.getId().equalsIgnoreCase(goal.getId())) {
                    throw new ArchetypeException("Another goal with the same id '"+goal.getId()+"' already exists in the Archetype! It cannot be added!");
                }
            }
            this.goals.put(goal.getId(), goal);
        }
    }
}
