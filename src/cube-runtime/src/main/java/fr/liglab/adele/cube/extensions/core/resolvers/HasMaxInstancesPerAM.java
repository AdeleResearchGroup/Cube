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


package fr.liglab.adele.cube.extensions.core.resolvers;

import fr.liglab.adele.cube.extensions.AbstractUnaryResolver;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.metamodel.*;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:07 AM
 */
public class HasMaxInstancesPerAM extends AbstractUnaryResolver {

    public HasMaxInstancesPerAM(Extension extension) {
        super(extension);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public boolean check(ManagedElement me, String value) {
        if (me != null && value != null) {
            Reference r = me.getReference(Component.CORE_COMPONENT_INPUTS);
            if (r != null) {
                return r.getReferencedElements().size() < new Integer(value);
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean perform(ManagedElement me, String value) {
        return true;
    }

}
