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
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.metamodel.PropertyNotExistException;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:07 AM
 */
public class IsLocal extends AbstractUnaryResolver {

    public IsLocal(Extension extension) {
        super(extension);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public boolean check(ManagedElement me, String value) {
        if (me != null && value != null) {

            if (value != null) {
                String pname = null;
                String pvalue = null;
                if (value.toString().contains("=")) {
                    String[] tmp = value.toString().split("=");
                    if (tmp != null && tmp.length==2) {
                        pname = tmp[0];
                        pvalue = tmp[1];
                    }
                } else {
                    pname = value.toString();
                }
                if (pvalue == null) {
                    return me.hasAttribute(pname);
                } else {
                    if (me.hasAttribute(pname) == false) {
                        return false;
                    } else {
                        String attributeValue = me.getAttribute(pname);
                        return attributeValue.equalsIgnoreCase(pvalue);
                    }
                }
            }
        }
        return false;
    }

    public boolean perform(ManagedElement me, String value) {
        if (me != null && value != null) {
            String pname = null;
            String pvalue = null;
            if (value.contains("=")) {
                String[] tmp = value.split("=");
                if (tmp != null && tmp.length==2) {
                    pname = tmp[0];
                    pvalue = tmp[1];
                }
            } else {
                pname = value;
            }
            if (pvalue == null) {
                return false;
            } else {
                if (me.hasAttribute(pname)) {
                    try {
                        me.updateAttribute(pname, pvalue);
                        return true;
                    } catch (PropertyNotExistException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        me.addAttribute(pname, pvalue);
                    } catch (PropertyExistException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return false;
    }

}
