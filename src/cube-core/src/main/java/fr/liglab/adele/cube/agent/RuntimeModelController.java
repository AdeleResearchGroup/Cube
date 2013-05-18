package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.metamodel.*;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 7:15 PM
 */
public interface RuntimeModelController {

    public String getAgentOfElement(String managed_element_uuid);

    public boolean setAgentOfElement(String managed_element_uuid, String agentUri);

    public String getPropertyValue(String managed_element_uuid, String name) ;

    public boolean addProperty(String managed_element_uuid, String name, String value) throws PropertyExistException, InvalidNameException;

    /**
     * Update instance property.
     * @param managed_element_uuid
     * @param name
     * @param newValue
     * @return
     * @throws PropertyNotExistException
     */
    public String updateProperty(String managed_element_uuid, String name, String newValue) throws PropertyNotExistException;

    public List<String> getReferencedElements(String managed_element_uuid, String reference_name);

    /**
     * Add Referenced Element to the given Element.
     * If the Reference identified by 'reference_name' does not exist, a new reference with this name will be created.
     *
     * @param managed_element_uuid
     * @param reference_name
     * @param referenced_element_uuid
     * @return
     * @throws InvalidNameException
     */
    public boolean addReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuid) throws InvalidNameException;

    public boolean addReferencedElement(String managed_element_uuid, String reference_name, boolean onlyone, String referenced_element_uuid) throws InvalidNameException;

    public boolean removeReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuid);

    boolean hasReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuri);

    public void receiveMessage(CMessage msg);

    public boolean areSimilar(String instance_uuid1, String instance_uuid2);

    public ManagedElement getLocalElement(String managed_element_uuid);
}
