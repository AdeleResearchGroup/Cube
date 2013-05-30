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
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.plugins.core.CorePluginFactory;
import fr.liglab.adele.cube.plugins.core.model.Component;
import fr.liglab.adele.cube.plugins.core.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 7:34 PM
 */
public class HasNoComponents implements ConstraintResolver {

    private static ConstraintResolver instance = new HasNoComponents();

    public static ConstraintResolver instance() {
        return instance;
    }

    public void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {

    }

    public boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance1_uuid = subjectVariable.getValue();
        Object agentUri = objectVariable.getValue();

        if (instance1_uuid != null && agentUri != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                List<String> comps = rmController.getReferencedElements(instance1_uuid.toString(), Node.CORE_NODE_COMPONENTS);
                if (comps == null || comps.size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean applyDescription(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
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
        System.out.println("\n\n performing: hasNoComponents...\n\n");
        Object instance1_uuid = subjectVariable.getValue();
        Object whichCompts = objectVariable.getValue();

        if (instance1_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {

                ArrayList<String> comps = new ArrayList<String>();
                synchronized (this) {
                    List<ManagedElement> tmp = agent.getRuntimeModel().getManagedElements(CorePluginFactory.NAMESPACE, Component.NAME, ManagedElement.VALID);
                    for (ManagedElement m : tmp) {
                        comps.add(m.getUUID());
                    }
                }
                //List<String> comps = rmController.getReferencedElements(instance1_uuid.toString(), Node.CORE_NODE_COMPONENTS);
                for (String c : comps) {
                    System.out.println("- "+c);
                    rmController.destroyElement(c);
                    //rmController.removeReferencedElement(c, Component.CORE_COMPONENT_NODE ,instance1_uuid.toString());

                }
                return true;
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
