package fr.liglab.adele.cube.autonomicmanager;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.extensions.MonitorExecutorExtensionPoint;
import fr.liglab.adele.cube.extensions.ResolverExtensionPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * User: debbabi
 * Date: 9/17/13
 * Time: 11:36 PM
 */
public interface MonitorExecutor {

    public void addSpecificMonitorExecutor(MonitorExecutorExtensionPoint sme);

}
