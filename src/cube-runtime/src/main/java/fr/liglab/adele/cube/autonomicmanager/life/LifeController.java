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


package fr.liglab.adele.cube.autonomicmanager.life;

import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModel;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelListener;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicationException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Notification;
import fr.liglab.adele.cube.metamodel.Reference;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.impl.AutonomicManagerImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 1:32 AM
 */
public class LifeController implements Runnable {

    private AutonomicManager agent;

    private long maxRetry = 1;

    private long interval = 5000;

    private boolean working = false;

    private boolean destroyRequested = false;

    Thread t;

    /**
     * key: __autonomicmanager
     * value: tentatives
     */
    private Map<String, Integer> monitoredAgents = new HashMap<String, Integer>();

    private RuntimeModelListener listener;

    public LifeController(AutonomicManager agent) {
        this.agent = agent;
        this.maxRetry = agent.getConfiguration().getKeepAliveRetry();
        this.interval = agent.getConfiguration().getKeepAliveInterval();

        listener = new RuntimeModelListener() {
            public void update(RuntimeModel rm, Notification notification) {
                if (notification != null && notification.getNotificationType() == RuntimeModelListener.UPDATED_RUNTIMEMODEL) {
                    manage();
                }
            }
        };
        t = new Thread(this);
        t.start();
    }

    private void manage() {
        List<String> tmp = new ArrayList<String>();
         for (ManagedElement me : agent.getRuntimeModelController().getRuntimeModel().getManagedElements(ManagedElement.VALID)) {
             for (Reference r : me.getReferences()) {
                 for (String reg : r.getReferencedElements()) {
                     String agenturi = agent.getRuntimeModelController().getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(reg);
                     if (agenturi != null) {
                         tmp.add(agenturi);
                     }
                 }
             }
         }
         for (String a : tmp) {
             addAgentToMonitor(a);
         }
        // print
        /*
        System.out.println("AGENTS TO MONITOR:");
        for (String a : this.monitoredAgents.keySet()) {
            System.out.println("- " + a);
        } */
    }

    private void addAgentToMonitor(String a) {
        synchronized (monitoredAgents) {
            if (!monitoredAgents.containsKey(a)) {
                monitoredAgents.put(a, 0);
            }
        }
    }


    public void start() {
        manage();
        this.agent.getRuntimeModelController().getRuntimeModel().addListener(listener);
        this.working = true;
    }

    public void stop() {
        this.working = false;
        this.agent.getRuntimeModelController().getRuntimeModel().deleteListener(listener);
    }

    public void destroy() {
        this.destroyRequested = true;
    }

    private void work() {
        List<String> toBeRemoved = new ArrayList<String>();
        List<String> clone = new ArrayList<String>();
        String tmp = "";
        synchronized (monitoredAgents) {
            for (String a : this.monitoredAgents.keySet()) {
                tmp += ".*.*. monitoring: "+ a +"\n";
                clone.add(a);
            }
        }
        if (!tmp.equalsIgnoreCase("")) System.out.println(tmp);
            for (String a : clone) {
                Integer counter = 0;
                synchronized (monitoredAgents) {
                    counter = this.monitoredAgents.get(a);
                }
                if (counter != null && counter >= this.maxRetry) {
                    synchronized (monitoredAgents) {
                        this.monitoredAgents.remove(a);
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
                    synchronized (monitoredAgents) {
                        if (this.monitoredAgents.containsKey(a))
                            this.monitoredAgents.put(a, counter);
                    }
                }
            }

        for (String a : toBeRemoved) {
            ((AutonomicManagerImpl)agent).getRuntimeModelController().getExternalInstancesHandler().removeExternalAgentInstances(a);
        }
    }



    public void keepAliveReceived(String agent) {
        synchronized (monitoredAgents) {
            if (this.monitoredAgents.containsKey(agent)) {
                this.monitoredAgents.put(agent,0);
            }
        }
    }

    public void run() {
        while (true) {
            try {
                if (this.working) {

                    // TODO
                    //System.out.println("================== life controller working... ");

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
