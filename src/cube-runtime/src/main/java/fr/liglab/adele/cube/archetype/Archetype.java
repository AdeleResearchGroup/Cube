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

import fr.liglab.adele.cube.AdministrationService;

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

    private static final String DEFAULT_ARCHETYPE_VERSION = "1.0";

    private String id = "";
    private String archetypeDescription = "";
    private String version = DEFAULT_ARCHETYPE_VERSION;
    private String cubeVersion = AdministrationService.CUBE_VERSION;

    private Map<String, Element> elements;  // element_id, element
    private Map<String, Property> properties;
    private Map<String, GoalSet> goalGroups;

    public Archetype() {
        properties = new HashMap<String, Property>();
        elements = new HashMap<String, Element>();
        goalGroups = new HashMap<String, GoalSet>();
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
        this.archetypeDescription = archetypeDescription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public synchronized Element getElement(String id) {
        return this.elements.get(id);
    }

    public synchronized ElementDescription getElementDescription(String id) {

        Element e =  this.elements.get(id);
        if (e != null && e instanceof ElementDescription) {
            return (ElementDescription)e;
        }
        return null;
    }

    public synchronized List<Element> getElements() {
        List<Element> result = new ArrayList<Element>();
        for (String e : this.elements.keySet()) {
            Element element = this.elements.get(e);
            result.add(element);
        }
        return result;
    }

    public synchronized List<ElementDescription> getElementsDescriptions() {
        List<ElementDescription> result = new ArrayList<ElementDescription>();
        for (String e : this.elements.keySet()) {
            Element element = this.elements.get(e);
            if (element instanceof ElementDescription) {
                result.add((ElementDescription)(element));
            }
        }
        return result;
    }

    public synchronized Property getProperty(String id) {
        return this.properties.get(id);
    }

    public synchronized List<Property> getProperties() {
        List<Property> result = new ArrayList<Property>();
        for (String e : this.properties.keySet()) {
            Property element = this.properties.get(e);
            if (element instanceof Property) {
                result.add(element);
            }
        }
        return result;
    }

    public synchronized List<DescriptionProperty> getDescriptionProperties() {
        List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
        for (String e : this.properties.keySet()) {
            Property element = this.properties.get(e);
            if (element instanceof DescriptionProperty) {
                result.add((DescriptionProperty)(element));
            }
        }
        return result;
    }

    public synchronized List<GoalProperty> getGoalProperties() {
        List<GoalProperty> result = new ArrayList<GoalProperty>();
        for (String e : this.properties.keySet()) {
            Property element = this.properties.get(e);
            if (element instanceof GoalProperty) {
                result.add((GoalProperty)(element));
            }
        }
        return result;
    }


    public synchronized boolean addElement(Element element) throws ArchetypeException {
        if (element != null) {
            for (Element e : getElements()) {
                if (e.getId() != null && e.getId().equalsIgnoreCase(element.getId())) {
                    //throw new ArchetypeException("Another element with the same id '"+ element.getId()+"' already exists in the Archetype! It cannot be added!");
                    return false;
                }
            }
            this.elements.put(element.getId(), element);
            return true;
        }
        return false;
    }

    public synchronized boolean addProperty(Property property) throws ArchetypeException {
        if (property != null) {
            for (Property g : getProperties()) {
                if (g.getId() != null && g.getId().equalsIgnoreCase(property.getId())) {
                    //throw new ArchetypeException("Another property with the same id '"+ property.getId()+"' already exists in the Archetype! It cannot be added!");
                    return false;
                }
            }
            this.properties.put(property.getId(), property);
            return true;
        }
        return false;
    }

    public synchronized List<GoalSet> getGoalSets() {
        List<GoalSet> result = new ArrayList<GoalSet>();
        for (String e : this.goalGroups.keySet()) {
            GoalSet goalSet = this.goalGroups.get(e);
            result.add(goalSet);
        }
        return result;
    }

    public synchronized boolean addGoalSet(GoalSet goalSet) throws ArchetypeException {
        if (goalSet != null) {
            for (GoalSet g : getGoalSets()) {
                if (g.getId() != null && g.getId().equalsIgnoreCase(goalSet.getId())) {
                    //throw new ArchetypeException("Another GoalSet with the same id '"+ goalSet.getId()+"' already exists in the Archetype! It cannot be added!");
                    return false;
                }
            }
            this.goalGroups.put(goalSet.getId(), goalSet);
        }
        return false;
    }
}
