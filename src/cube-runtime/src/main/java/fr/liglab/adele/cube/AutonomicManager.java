package fr.liglab.adele.cube;

import fr.liglab.adele.cube.autonomicmanager.ArchetypeResolver;
import fr.liglab.adele.cube.autonomicmanager.Communicator;
import fr.liglab.adele.cube.autonomicmanager.ExternalInstancesHandler;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelController;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.util.perf.PerformanceChecker;

import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:39 PM
 */
public interface AutonomicManager {

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
     * Gets the runtime
     * @return
     */
    public AdministrationService getAdministrationService();

    /**
     * Gets the initial Agent Configuration
     * @return
     */
    public Configuration getConfiguration();

    /**
     * Gets the associated Archetype.
     * @return
     */
    public Archetype getArchetype();

    /**
     * Gets the Archetype Resolver
     * @return
     */
    public ArchetypeResolver getArchetypeResolver();

    public ExternalInstancesHandler getExternalInstancesHandler();

    public void addProperty(String name, String value);

    public String getProperty(String name);

    public Properties getProperties();

    /**
     * Gets the associated Extensions
     * @return
     */
    public List<Extension> getExtensions();

    /**
     * Gets the extension having the given id.
     * @param namespace
     */
    public Extension getExtension(String namespace);

    /**
     * Gets the Cube Agent's CommunicatorExtensionPoint.
     * @return
     */
    public Communicator getCommunicator();


    /**
     * Creates a new Managed ElementDescription.
     * @param namespace
     * @param name
     * @param properties
     * @return
     */

    /**
     * Gets the Runtime Model Controller.
     *
     * @return
     */
    public RuntimeModelController getRuntimeModelController();


    public void start();
    public void stop();
    public void destroy();

    //public void addExternalElement(String element_uuid, String agent_uri);

    //public String getExternalAutonomicManagerUri(String managed_element_uuid);


}
