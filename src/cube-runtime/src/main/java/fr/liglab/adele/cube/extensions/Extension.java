package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.AutonomicManager;

import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:56 PM
 */
public interface Extension {

    public String getUri();
    public AutonomicManager getAutonomicManager();
    public ExtensionFactoryService getExtensionFactory();
    public Properties getProperties();

    public List<ExtensionPoint> getExtensionPoints();

    public void start();
    public void stop();
    public void destroy();
    public void starting();
    public void stopping();
    public void destroying();


}
