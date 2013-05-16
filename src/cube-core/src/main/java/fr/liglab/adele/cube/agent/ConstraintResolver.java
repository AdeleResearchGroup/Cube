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


package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.agent.defaults.resolver.Variable;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:02 AM
 */
public interface ConstraintResolver {

    void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable);

    boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable);

    /**
     * Applies the predicate information located on the objectVariable, on the subjectVariable
     * to limit the research domain space.
     *
     * It should be only implemented for UnaryConstraints.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return TRUE if applied
     */
    boolean applyDescription(CubeAgent agent, Variable subjectVariable, Variable objectVariable);


    /**
     * Apply the objective constraint.
     * This should modify the two elements in relation!
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    boolean performObjective(CubeAgent agent, Variable subjectVariable, Variable objectVariable);

    /**
     * Cancel the applied objective constraint.
     * This should remove properties or references added by the equivalent 'apply' function.
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    boolean cancelObjective(CubeAgent agent, Variable subjectVariable, Variable objectVariable);

    /**
     * Find value from object variable.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    String find(CubeAgent agent, Variable subjectVariable, Variable objectVariable);
}
