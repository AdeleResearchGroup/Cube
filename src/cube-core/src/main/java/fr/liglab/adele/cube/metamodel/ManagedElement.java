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


package fr.liglab.adele.cube.metamodel;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 7:40 PM
 */
public interface ManagedElement {

    public static final int UNMANAGED = -1;
    public static final int UNCHECKED = 0;
    public static final int VALID= 1;

    /**
     * Gets the ManagedElement concept's name
     * @return
     */
    public String getName();

    /**
     * Gets the ManagedElement concept's namespace
     * @return
     */
    public String getNamespace();

    /**
     * Gets the instance UUID.
     * @return
     */
    public String getUUID();

    /**
     * Gets the actual hosting cube agent.
     * @return
     */
    public String getCubeAgent();

    /**
     * Gets the URI of the current Managed Element.
     * @return
     */
    public String getUri();

    /**
     * Gets the instance current state.
     * @return
     */
    public int getState();

    /**
     * Gets the instance current state as String.
     * @return
     */
    public String getStateAsString();

    /**
     * Get Managed Element Properties
     * @return
     */
    public List<Property> getProperties();

    /**
     * Checks if the Managed Element has the given property.
     * @param name Property name
     * @return TRUE if it has the provided property.
     */
    boolean hasProperty(String name);

    /**
     * Get Property value
     * @param name
     * @return NULL if 'name' is null or no property found with 'name' name; the value of the found property other else
     */
    public String getProperty(String name) ;

    /**
     * Adding property
     * @param name
     * @param value
     * @return TRUE if the property added; FALSE other else.
     * @throws fr.liglab.adele.cube.metamodel.PropertyExistException
     */
    public boolean addProperty(String name, String value) throws PropertyExistException, InvalidNameException;

    /**
     * Update property
     * @param name
     * @param newValue
     * @return NULL is name is null; or Old value if a property exist with 'name' name.
     * @throws fr.liglab.adele.cube.metamodel.PropertyNotExistException
     */
    public String updateProperty(String name, String newValue) throws PropertyNotExistException;

    /**
     * Get Managed Element References
     * @return
     */
    public List<Reference> getReferences();
    /**
     * Get Regerence
     * @param name
     * @return
     */
    public Reference getReference(String name) ;

    /**
     * Checks if the Managed Element has the given reference.
     * @param name Reference name
     * @return TRUE if it has the provided reference name.
     */
    boolean hasReference(String name);

    /**
     * Gets a textual description of the element.
     * @return
     */
    public String getTextualDescription();

    /**
     * Gets an HTML description of the element.
     * @return
     */
    public String getHTMLDescription();

    /**
     *
     * @param name
     * @param onlyOne
     * @return newly, or already exists Reference with the same name.
     * @throws InvalidNameException
     */
    public Reference addReference(String name, boolean onlyOne) throws InvalidNameException;

    public boolean removeEmptyProperties();

    public boolean removeEmptyReferences();

    public boolean removeReferencedElement(String ref);

    public boolean isSimilar(ManagedElement managedElement);


}
