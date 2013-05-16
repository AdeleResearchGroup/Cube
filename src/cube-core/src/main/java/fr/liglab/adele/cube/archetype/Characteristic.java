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

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:06 AM
 */
public class Characteristic {

    private String namespace;
    private String name;
    private String description = "";

    private Element subject;

    private Object object;

    public Characteristic(String namespace, String name, Element subject, Object object) {
        this.namespace = namespace;
        this.name = name;
        this.subject = subject;
        this.object = object;
    }

    public Characteristic(String namespace, String name, Element subject, Object object, String description) {
        this(namespace, name, subject, object);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Element getSubject() {
        return subject;
    }

    public void setSubject(Element subject) {
        this.subject = subject;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
