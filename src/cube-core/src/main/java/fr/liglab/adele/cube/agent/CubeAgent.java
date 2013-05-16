package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.plugins.Plugin;

import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:39 PM
 */
public interface CubeAgent {

    /**
     * Gets the URI
     * @return
     */
    public String getUri();

    /**
     * Gets the local id of the current Cube Agent
     * @return
     */
    public String getLocalId();

    /**
     * Gets the platform
     * @return
     */
    public CubePlatform getPlatform();

    /**
     * Gets the initial Agent Configuration
     * @return
     */
    public AgentConfig getConfig();

    /**
     * Gets the associated Archetype.
     * @return
     */
    public Archetype getArchetype();

    /**
     * Gets the associated Extensions
     * @return
     */
    public List<Plugin> getPlugins();

    /**
     * Gets the extension having the given id.
     * @param namespace
     */
    public Plugin getPlugin(String namespace);

    /**
     * Gets the Cube Agent's Communicator.
     * @return
     */
    public Communicator getCommunicator();

    /**
     * Gets the Runtime Model
     * @return
     */
    public RuntimeModel getRuntimeModel();

    /**
     * Creates a new Managed Element.
     * @param namespace
     * @param name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String namespace, String name, Properties properties) throws InvalidNameException, PropertyExistException;

    /**
     * Gets the list of unmanaed elements.
     * (which are not associated to the runtime model)
     * @return
     */
    public List<ManagedElement> getUnmanagedElements();

    public void run();
    public void stop();
    public void destroy();

    /**
     * Gets the Runtime Model Controller.
     *
     * @return
     */
    public RuntimeModelController getRuntimeModelController();

    public void addExternalElement(String element_uuid, String agent_uri);

    public String getExternalAgentUri(String managed_element_uuid);

    /**
     * Remove all UNMANAGED Elements.
     */
    void removeUnmanagedElements();
}
