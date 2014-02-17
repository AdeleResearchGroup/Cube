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


package fr.liglab.adele.cube.extensions.core.model;

import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.metamodel.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 9:27 PM
 */
public class Master extends ManagedElement {

    public static final String NAME = "Master";

    public Master(String amUri) {
        super(amUri);
    }

    public Master(String amUri, Properties properties) throws PropertyExistException, InvalidNameException {
        super(amUri, properties);
        setPriority(10);
    }

    public boolean addScopeLeader(String scope_id, String scope_uuid) {
        Reference r = this.getReference(scope_id);
        if (r == null) {
            try {
                r = new Reference(scope_id, true);
                return r.addReferencedElement(scope_uuid);
            } catch (InvalidNameException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getScopeLeader(String scope_id) {
        Reference r = this.getReference(scope_id);
        if (r != null) {
            if (r.getReferencedElements() != null && r.getReferencedElements().size() > 0)
                return r.getReferencedElements().get(0);
        }
        return null;
    }


    public String getNamespace() {
        return CoreExtensionFactory.NAMESPACE;
    }

    public String getName() {
        return NAME;
    }
}
