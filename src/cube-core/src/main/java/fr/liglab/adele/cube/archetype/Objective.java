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
 * Time: 3:03 AM
 */
public class Objective {

    public static final int DEFAULT_PRIORITY = 1;

    private String namespace;
    private String name;
    private String description;

    private String resolution;

    int priority = DEFAULT_PRIORITY;

    private Goal goal;

    private Element subject;
    private Object object;

    public Objective(Goal goal, String namespace, String name, Element subject, Object object, String resolution, int priority, String description) {
        this.goal = goal;
        this.namespace = namespace;
        this.name = name;
        this.subject = subject;
        this.resolution = resolution;
        this.object = object;
        this.priority = priority;
        if (description != null) this.description = description; else this.description = "";
    }

    public Objective(Goal goal, String namespace, String name, Element subject, Object object, String resolution, int priority) {
        this(goal, namespace, name, subject, object, resolution, priority, "");
    }

    public Objective(Goal goal, String namespace, String name, Element subject, Object object, String resolution) {
        this(goal, namespace, name, subject, object, resolution, -1, "");
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

    public String getResolutionStrategy() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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
