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

import java.util.List;
import java.util.ArrayList;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:02 AM
 */
public class Element {

    private Archetype archetype;

    private String namespace;
    private String name;
    private String id;

    private String description = "";

    private List<Characteristic> characteristics = new ArrayList<Characteristic>();
    private Iterable<? extends Objective> unaryObjectives;

    public Element(Archetype archetype, String namespace, String name, String id) {
        this.archetype = archetype;
        this.namespace = namespace;
        this.name = name;
        this.id = id;
    }

    public Element(Archetype archetype, String namespace, String name, String id, String description) {
        this(archetype, namespace, name, id);
        this.description = description;
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

    public void addCharacteristic(Characteristic characteristic) {
        this.characteristics.add(characteristic);
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public List<Characteristic> getUnaryCharacteristics() {
        List<Characteristic> result = new ArrayList<Characteristic>();
        for (Characteristic c : getCharacteristics()) {
            if (c.getObject() != null && !(c.getObject() instanceof Element))
                result.add(c);
        }
        return result;
    }

    public List<Characteristic> getBinaryCharacteristics() {
        List<Characteristic> result = new ArrayList<Characteristic>();
        for (Characteristic c : getCharacteristics()) {
            if (c.getObject() != null && c.getObject() instanceof Element)
                result.add(c);
        }
        return result;
    }

    public List<Objective> getUnaryObjectives() {
        List<Objective> result = new ArrayList<Objective>();
        for (Objective o : this.archetype.getUnaryObjectives()) {
            if (o.getSubject() == this) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Objective> getBinaryObjectives() {
        List<Objective> result = new ArrayList<Objective>();
        for (Objective o : this.archetype.getBinaryObjectives()) {
            if (o.getSubject() == this) {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Element) {
            Element e = ((Element)obj);
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


}
