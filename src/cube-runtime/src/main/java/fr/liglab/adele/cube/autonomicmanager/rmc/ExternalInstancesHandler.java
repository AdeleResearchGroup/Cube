package fr.liglab.adele.cube.autonomicmanager.rmc;

import fr.liglab.adele.cube.autonomicmanager.RuntimeModelController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: debbabi
 * Date: 9/22/13
 * Time: 6:31 PM
 */
public class ExternalInstancesHandler {

    RuntimeModelController rmc;

    /**
     * key: instance uuid
     * value: autonomic manager uri
     */
    Map<String , String> externalInstances;

    public ExternalInstancesHandler(RuntimeModelController rmc) {
        this.rmc = rmc;
        externalInstances = new HashMap<String, String>();
    }

    public synchronized void addExternalInstance(String uuid, String autonomicManagerUri) {
        if (uuid != null && autonomicManagerUri != null) {
            ((RuntimeModelControllerImpl)rmc).info("adding external instance: " + uuid + " of AM: " + autonomicManagerUri);
            this.externalInstances.put(uuid, autonomicManagerUri);
        }
    }

    public String getAutonomicManagerOfExternalInstance(String uuid) {
        return this.externalInstances.get(uuid);
    }

    public synchronized void removeExternalAgentInstances(String agent_uri) {
        List<String> toBeRemoved = new ArrayList<String>();
        if (this.externalInstances.containsValue(agent_uri)) {
            for (String uuid : this.externalInstances.keySet()) {
                String agent = this.externalInstances.get(uuid);
                if (agent != null && agent.equalsIgnoreCase(agent_uri)) {
                    toBeRemoved.add(uuid);
                }
            }
        }
        ((RuntimeModelImpl)rmc.getRuntimeModel()).removeReferencedElements(toBeRemoved);
    }
}
