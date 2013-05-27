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


package fr.liglab.adele.cube.agent.defaults.resolver;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 5:15 PM
 *
 *
 */
public class Variable extends AbstractManagedElement {

    //private Object value = null;

    private String id;

    private String name;
    private String namespace;

    boolean primitive = false;

    private transient CubeAgent agent;

    public int findStep = 0;

    List<Constraint> constraints = new ArrayList<Constraint>();

    public Stack<Object> values = new Stack<Object>();

    /**
     * used when pre-processing the variable when building the resolution graph
     */
    private transient boolean processed = false;
     private transient static int index = 1;
    /**
     * Primitive.
     *
     * @param agent
     * @param value
     */
    public Variable(CubeAgent agent, Object value) {
        super(agent);
        this.id = "__" + index++;
        this.primitive = true;
        this.agent = agent;
        if (value != null)
            this.values.push(value.toString());
    }

    /**
     * Not primitive variable.
     * @param agent
     * @param namespace
     * @param name
     */
    public Variable(CubeAgent agent, String id, String namespace, String name) {
        super(agent);
        this.id = id;
        this.namespace = namespace;
        this.name = name;
        this.primitive = false;
        this.agent = agent;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getId() {
        return id;
    }

    public boolean isPrimitive() {
        return primitive;
    }



    public CubeAgent getAgent() {
        return this.agent;
    }

    public void setValue(String uuid) {
        if (uuid != null) {
            this.values.push(uuid);
        }
    }

    public Object getValue() {
        if (this.values.size() > 0)
            return this.values.peek();
        else
            return null;
    }

    public boolean hasValue(Object value) {
        if (value != null) {
            return this.values.contains(value);
        }
        return false;
    }

    public boolean hasValue() {
        return this.values.size() > 0;
    }

    public Object removeValue() {
        if (this.values.size() > 0)
            return this.values.pop();
        return null;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    public List<Constraint> getConstraints() {
        return this.constraints;
    }

    public List<Constraint> getUnaryConstraints() {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Constraint c : getConstraints()) {
            if (c.isUnaryConstraint() == true)
                result.add(c);
        }
        return result;
    }

    public List<Constraint> getBinaryConstraints() {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Constraint c : getConstraints()) {
            if (c.isBinaryConstraint() == true)
                result.add(c);
        }
        return result;
    }

    public List<Constraint> getObjectiveConstraints() {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Constraint c : getConstraints()) {
            if (c.isObjectiveConstraint()==true) {
                result.add(c);
            }
        }
        return result;
    }

    public void addConstraint(Constraint c) {
        this.constraints.add(c);
    }

    public void removeConstraint(Constraint c) {
        if (c != null) {
            if (c.getSubjectVariable() == this)
                this.constraints.remove(c);
        }
    }



}
