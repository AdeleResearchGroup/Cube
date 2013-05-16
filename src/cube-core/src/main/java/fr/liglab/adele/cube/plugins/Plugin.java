package fr.liglab.adele.cube.plugins;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:56 PM
 */
public interface Plugin {

    public String getUri();
    public CubeAgent getCubeAgent();
    public PluginFactory getPluginFactory();
    public Properties getProperties();

    public void run();
    public void stop();
    public void destroy();

    public ConstraintResolver getConstraintResolver(String name);

    /**
     * Creates a new Managed Element Instance of the given name.
     * @param element_name
     * @return
     */
    ManagedElement newManagedElement(String element_name);

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException;
}
