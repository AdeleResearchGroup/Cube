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

import fr.liglab.adele.cube.autonomicmanager.RuntimeModelController;
import fr.liglab.adele.cube.extensions.AbstractResolver;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.core.model.Scope;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 8:24 PM
 */
public class ControlledBy extends AbstractResolver {


    public ControlledBy(Extension extension) {
        super(extension);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public boolean check(ManagedElement me, String value) {
        //System.out.println(".......... inScoope.check..........");
        if (me != null && value != null) {
            Reference r = me.getReference(Scope.CORE_SCOPE_MASTER);
            if (r!=null) {
                for (String e : r.getReferencedElements()) {
                    if (e.equalsIgnoreCase(value)) return true;
                }
            }
        }
        return false;
    }

    public boolean perform(ManagedElement me, String value) {
        //System.out.println(".......... ControlledBy.perform..........");
        if (me != null && value != null) {
            Object instance1_uuid = me.getUUID();
            Object instance2_uuid = value;

            if (instance1_uuid != null && instance2_uuid != null) {
                RuntimeModelController rmController = getExtension().getAutonomicManager().getRuntimeModelController();
                if (rmController != null) {
                    try {
                        String scope_id=rmController.getAttributeValue(instance1_uuid.toString(), Scope.CORE_SCOPE_ID);
                        if (rmController.addReferencedElement(instance1_uuid.toString(), Scope.CORE_SCOPE_MASTER, instance2_uuid.toString())) {
                            if (rmController.addReferencedElement(instance2_uuid.toString(), scope_id, true, instance1_uuid.toString())) {
                                //System.out.println("++++++++++++++ ControlledBy.perform......"+me.getUUID()+"....TRUE");
                                return true;
                            }
                        }
                    } catch (InvalidNameException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        //System.out.println("++++++++++++++ ControlledBy.perform......"+me.getUUID()+"....FALSE");
        return false;


    }

    /**
     * @param me Master
     * @param description of Scope
     * @return
     */
    public List<String> find(ManagedElement me, ManagedElement description) {
        List<String> result = new ArrayList<String>();
        if (me != null && description != null) {
            //System.out.println(description.getDocumentation());
            Reference r = me.getReference(description.getAttribute(Scope.CORE_SCOPE_ID));
            if (r != null) {
                //System.out.println("CONTROLLED BY . FIND: " + r.getReferencedElements().size());
                return r.getReferencedElements();
            }
            /*
            Object instance2_uuid = me.getUUID();

            if (instance2_uuid != null) {
                RuntimeModelController rmController = getExtension().getAutonomicManager().getRuntimeModelController();
                if (rmController != null) {
                    List<String> sleaders = rmController.getReferencedElements(instance2_uuid.toString(), description.getAttribute(Scope.CORE_SCOPE_ID));
                    for (String s : sleaders) {
                        ManagedElement cmpts = getExtension().getAutonomicManager().getRuntimeModelController().getCopyOfManagedElement(s);
                        if (getExtension().getAutonomicManager().getRuntimeModelController().compareTwoManagedElements(description, cmpts) == 0) {
                            result.add(s);
                        }
                    }
                }
            } */
        }
        //System.out.println("CONTROLLED BY . FIND: RIEN!");
        return result;
    }

}
