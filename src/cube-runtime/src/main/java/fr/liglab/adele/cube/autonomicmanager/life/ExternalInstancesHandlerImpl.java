package fr.liglab.adele.cube.autonomicmanager.life;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.ExternalInstancesHandler;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelController;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicationException;
import fr.liglab.adele.cube.autonomicmanager.impl.AutonomicManagerImpl;
import fr.liglab.adele.cube.autonomicmanager.rmc.RuntimeModelImpl;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * User: debbabi
 * Date: 9/22/13
 * Time: 6:31 PM
 */
public class ExternalInstancesHandlerImpl implements ExternalInstancesHandler , Runnable {


    private AutonomicManager agent;

    private long maxRetry = 2;

    private long interval = 5000;

    private boolean working = false;

    private boolean destroyRequested = false;

    /**
     * key: autonomicmanager
     * value: tentatives
     */
    private Map<String, Integer> monitoredAMs;

    Thread t;

    /**
     * key: instance uuid
     * value: autonomic manager uri
     */
    Map<String , String> externalInstances;

    public ExternalInstancesHandlerImpl(AutonomicManager am) {

        // life controller
        this.agent = am;
        this.maxRetry = agent.getConfiguration().getKeepAliveRetry();
        this.interval = agent.getConfiguration().getKeepAliveInterval();
        this.monitoredAMs = new HashMap<String, Integer>();

        // external instances
        this.externalInstances = new HashMap<String, String>();

        t = new Thread(this);
        t.start();
    }

    public synchronized void addExternalInstance(String uuid, String autonomicManagerUri) {
        if (uuid != null && autonomicManagerUri != null) {
            synchronized (this.externalInstances) {
                this.externalInstances.put(uuid, autonomicManagerUri);
            }
            addAutonomicManagerToMonitor(autonomicManagerUri);
        }
    }

    public synchronized String getAutonomicManagerOfExternalInstance(String uuid) {
        return this.externalInstances.get(uuid);
    }

    public synchronized void removeExternalAutonomicManagerInstances(String agent_uri) {
        List<String> toBeRemoved = new ArrayList<String>();
        if (this.externalInstances.containsValue(agent_uri)) {
            for (String uuid : this.externalInstances.keySet()) {
                String agent = this.externalInstances.get(uuid);
                if (agent != null && agent.equalsIgnoreCase(agent_uri)) {
                    toBeRemoved.add(uuid);
                }
            }
        }
        this.agent.getRuntimeModelController().getRuntimeModel().removeReferencedElements(toBeRemoved);
    }

    public synchronized List<String> getExternalAutonomicManagers() {
        List<String> result = new ArrayList<String>();
        for (String a :this.externalInstances.values()) {
            if (!result.contains(a)) {
                result.add(a);
            }
        }
        return result;
    }

    public void start() {
        manage();
        this.working = true;
    }

    private void manage() {
        List<String> tmp = new ArrayList<String>();
        for (ManagedElement me : agent.getRuntimeModelController().getRuntimeModel().getManagedElements(ManagedElement.VALID)) {
            for (Reference r : me.getReferences()) {
                for (String reg : r.getReferencedElements()) {
                    String agenturi = agent.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(reg);
                    if (agenturi != null) {
                        tmp.add(agenturi);
                    }
                }
            }
        }
        for (String a : tmp) {
            addAutonomicManagerToMonitor(a);
        }
    }

    private void addAutonomicManagerToMonitor(String a) {
        synchronized (monitoredAMs) {
            if (!monitoredAMs.containsKey(a)) {
                monitoredAMs.put(a, 0);
            }
        }
    }

    public void stop() {
        this.working = false;
    }

    public void destroy() {
        this.destroyRequested = true;
    }
    private void work() {
        List<String> toBeRemoved = new ArrayList<String>();
        List<String> clone = new ArrayList<String>();
        String tmp = "";
        synchronized (monitoredAMs) {
            for (String a : this.monitoredAMs.keySet()) {
                //tmp += ".*.*. monitoring: "+ a +"\n";
                clone.add(a);
            }
        }
        if (!tmp.equalsIgnoreCase("")) System.out.println(tmp);
        for (String a : clone) {
            Integer counter = 0;
            synchronized (monitoredAMs) {
                counter = this.monitoredAMs.get(a);
            }
            if (counter != null && counter >= this.maxRetry) {
                synchronized (monitoredAMs) {
                    this.monitoredAMs.remove(a);
                }
                toBeRemoved.add(a);
                System.out.println("[WARNING] autonomicmanager '"+a+"' is not connected!");
            } else {
                // send message
                CMessage msg = new CMessage();
                msg.setReplyTo(this.agent.getUri());
                msg.setFrom(this.agent.getUri());
                msg.setTo(a);
                msg.setObject("keepalive");
                try {
                    this.agent.getCommunicator().sendMessage(msg);
                } catch (CommunicationException e) {
                    //e.printStackTrace();
                } catch (IOException e) {
                    //e.printStackTrace();
                }

                counter++;
                synchronized (monitoredAMs) {
                    if (this.monitoredAMs.containsKey(a))
                        this.monitoredAMs.put(a, counter);
                }
            }
        }
        for (String a : toBeRemoved) {
            removeExternalAutonomicManagerInstances(a);
        }
    }

    public void keepAliveReceived(String am) {
        synchronized (monitoredAMs) {
            if (this.monitoredAMs.containsKey(am)) {
                this.monitoredAMs.put(am,0);
            }
        }
    }
    public void run() {
        while (true) {
            try {
                if (this.working) {
                    work();
                }
                if (this.destroyRequested) {
                    Thread.currentThread().interrupt();
                    break;
                }
                sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }
    }
}
