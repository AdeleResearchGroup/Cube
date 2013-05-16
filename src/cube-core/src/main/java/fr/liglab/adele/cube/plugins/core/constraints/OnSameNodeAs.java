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
import fr.liglab.adele.cube.metamodel.Reference;
import fr.liglab.adele.cube.plugins.core.model.Component;
import fr.liglab.adele.cube.plugins.core.model.Node;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 10:24 PM
 */
public class OnSameNodeAs implements ConstraintResolver {

    private static ConstraintResolver instance = new OnSameNodeAs();

    public static ConstraintResolver instance() {
        return instance;
    }

    public void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
    }

    public boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();
        if (instance1_uuid != null && instance2_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                for (String n1 : rmController.getReferencedElements(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE)) {
                    for (String n2 : rmController.getReferencedElements(instance2_uuid.toString(), Component.CORE_COMPONENT_NODE)) {
                        if (n1.equalsIgnoreCase(n2)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Applies the predicate information located on the objectVariable, on the subjectVariable
     * to limit the research domain space.
     * <p/>
     * It should be only implemented for UnaryConstraints.
     *
     * @param subjectVariable
     * @param objectVariable
     */
    public boolean applyDescription(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        if (subjectVariable != null && objectVariable != null && objectVariable.hasValue()) {

            String agent_of_instance2 = null;
            for (String n :  agent.getRuntimeModelController().getReferencedElements(objectVariable.getValue().toString(),
                    Component.CORE_COMPONENT_NODE)) {
                agent_of_instance2 = n;
                break;
            }

            if (agent_of_instance2 != null) {
                Reference ref = subjectVariable.getReference(Component.CORE_COMPONENT_NODE);
                if (ref == null) {
                    try {
                        Reference r = subjectVariable.addReference(Component.CORE_COMPONENT_NODE, true);
                        if (r != null) {
                            r.addReferencedElement(agent_of_instance2);
                            return true;
                        }
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    }
                } else {
                    ref.addReferencedElement(agent_of_instance2);
                    return true;
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
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();

        if (instance1_uuid != null && instance2_uuid != null) {
            String agent_of_instance2 = null;
            for (String n :  agent.getRuntimeModelController().getReferencedElements(objectVariable.getValue().toString(),
                    Component.CORE_COMPONENT_NODE)) {
                agent_of_instance2 = n;
                break;
            }
            if (agent_of_instance2 != null) {
                RuntimeModelController rmController = agent.getRuntimeModelController();
                if (rmController != null) {
                    try {
                        if (rmController.addReferencedElement(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE, agent_of_instance2)) {
                            return true;
                        }
                    } catch (InvalidNameException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return false;
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
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();

        if (instance1_uuid != null && instance2_uuid != null) {
            String agent_of_instance2 = null;
            for (String n :  agent.getRuntimeModelController().getReferencedElements(objectVariable.getValue().toString(),
                    Component.CORE_COMPONENT_NODE)) {
                agent_of_instance2 = n;
                break;
            }
            if (agent_of_instance2 != null) {
                RuntimeModelController rmController = agent.getRuntimeModelController();
                if (rmController != null) {
                    if (rmController.removeReferencedElement(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE, agent_of_instance2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find value from object variable.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public String find(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {

        Object instance2_uuid = objectVariable.getValue();

        if (instance2_uuid != null) {

            String node_of_instance2 = null;
            for (String n :  agent.getRuntimeModelController().getReferencedElements(objectVariable.getValue().toString(),
                    Component.CORE_COMPONENT_NODE)) {
                node_of_instance2 = n;
                break;
            }
            if (node_of_instance2 != null) {
                RuntimeModelController rmController = agent.getRuntimeModelController();
                if (rmController != null) {

                    List<String> nodes = rmController.getReferencedElements(node_of_instance2, Node.CORE_NODE_COMPONENTS);
                    for (String n : nodes) {
                        if (!subjectVariable.hasValue(n)) {
                            return n;
                        }
                    }
                }
            }
        }
        return null;
    }
}
