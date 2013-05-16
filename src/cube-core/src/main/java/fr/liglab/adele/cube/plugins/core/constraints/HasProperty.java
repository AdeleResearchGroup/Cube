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


package fr.liglab.adele.cube.plugins.core.constraints;

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.RuntimeModelController;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.metamodel.PropertyNotExistException;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:07 AM
 */
public class HasProperty implements ConstraintResolver {

    private static ConstraintResolver instance = new HasProperty();

    public static ConstraintResolver instance() {
        return instance;
    }

    public void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        // no initialization for Unary constraints
    }

    public boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance1_uuid = subjectVariable.getValue();
        Object property = objectVariable.getValue();
        if (instance1_uuid != null) {
            if (property != null) {
                String pname = null;
                String pvalue = null;
                if (property.toString().contains(":")) {
                    String[] tmp = property.toString().split(":");
                    if (tmp != null && tmp.length==2) {
                        pname = tmp[0];
                        pvalue = tmp[1];
                    }
                } else {
                    pname = property.toString();
                }
                RuntimeModelController rmController = agent.getRuntimeModelController();
                if (rmController != null) {
                    String value = rmController.getPropertyValue(instance1_uuid.toString(), pname);
                    if (value != null) {
                        if (pvalue == null) {
                            return true;
                        }
                        if (value.equalsIgnoreCase(pvalue)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean applyDescription(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        if (subjectVariable != null && objectVariable != null && objectVariable.getValue() != null) {

            Object property = objectVariable.getValue();
            String pname = null;
            String pvalue = null;
            if (property.toString().contains(":")) {
                String[] tmp = property.toString().split(":");
                if (tmp != null && tmp.length==2) {
                    pname = tmp[0];
                    pvalue = tmp[1];
                }
            } else {
                pname = property.toString();
            }

            if (subjectVariable.getProperty(pname) == null) {
                try {
                    subjectVariable.addProperty(pname, pvalue);
                    return true;
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    subjectVariable.updateProperty(pname, pvalue);
                } catch (PropertyNotExistException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Apply the objective constraint.
     * This should modify the two elements in relation!
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public boolean performObjective(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Cancel the applied objective constraint.
     * This should remove properties or references added by the equivalent 'apply' function.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public boolean cancelObjective(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Find value from object variable.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public String find(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
