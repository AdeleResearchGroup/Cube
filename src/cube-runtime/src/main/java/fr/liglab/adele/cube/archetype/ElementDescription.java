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
public class ElementDescription extends Element {

    private String namespace;
    private String name;
    private String documentation = "";

    private List<DescriptionProperty> descriptionProperties;

    public ElementDescription(Archetype archetype, String namespace, String name, String id) {
        super(archetype, id);
        this.namespace = namespace;
        this.name = name;
        descriptionProperties = new ArrayList<DescriptionProperty>();
    }

    public ElementDescription(Archetype archetype, String namespace, String name, String id, String documentation) {
        this(archetype, namespace, name, id);
        this.documentation = documentation;
        descriptionProperties = new ArrayList<DescriptionProperty>();
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

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public void addDescriptionProperty(DescriptionProperty property) {
        property.setSubject(this);
        this.descriptionProperties.add(property);
    }

    public List<DescriptionProperty> getDescriptionProperties() {
        return descriptionProperties;
    }

    public List<DescriptionProperty> getUnaryDescriptionProperties() {
        List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
        for (DescriptionProperty c : getDescriptionProperties()) {
            if (c.getObject() != null && c.getObject() instanceof ElementValue)
                result.add(c);
        }
        return result;
    }

    public List<DescriptionProperty> getBinaryDescriptionProperties() {
        List<DescriptionProperty> result = new ArrayList<DescriptionProperty>();
        for (DescriptionProperty c : getDescriptionProperties()) {
            if (c.getObject() != null && c.getObject() instanceof ElementDescription)
                result.add(c);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ElementDescription) {
            ElementDescription e = ((ElementDescription)obj);
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

    public String getFullname()  {
        return getNamespace()+":"+getName();
    }

}
