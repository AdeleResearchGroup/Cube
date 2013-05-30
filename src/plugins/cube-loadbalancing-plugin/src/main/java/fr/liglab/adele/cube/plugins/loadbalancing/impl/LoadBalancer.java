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


package fr.liglab.adele.cube.plugins.loadbalancing.impl;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.metamodel.PropertyNotExistException;
import fr.liglab.adele.cube.plugins.core.CorePluginFactory;
import fr.liglab.adele.cube.plugins.core.model.Node;
import fr.liglab.adele.cube.plugins.core.model.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Author: debbabi
 * Date: 5/30/13
 * Time: 2:24 PM
 */
public class LoadBalancer implements Runnable {

    private static final String CPU_PROPERTY = "cpu";
    private static final String LOADBALANCING_PROPERTY = "loadbalancing";

    private boolean working = false;
    private boolean destroyRequested = false;
    private long interval = 5000;

    Thread t;

    CubeAgent agent = null;

    private int max_limite = 70;

    public LoadBalancer(CubeAgent agent, int interval) {
        this.agent = agent;
        this.interval = interval;
        t = new Thread(this);
        t.start();
    }

    private void work() {

        // update cpu property if a Node instance is present in the runtime model
        updateNodeCPU();

        // calculate the number of needed nodes to equilibrate the load.
        loadbalancing();

        // refresh the Runtime Model?
    }

    private void updateNodeCPU() {
        Random rand = new Random();
        int min = 10, max = 90;

        // simulated value
        int cpu = rand.nextInt(max - min + 1) + min;;

        List<ManagedElement> mes = this.agent.getRuntimeModel().getManagedElements(CorePluginFactory.NAMESPACE,
                Node.NAME, ManagedElement.VALID);

        if (mes != null && mes.size() > 0) {
            for (ManagedElement me : mes) {
                try {
                    String loadbalanced = me.getProperty(LOADBALANCING_PROPERTY);
                    if (loadbalanced != null) {
                        if (loadbalanced.equalsIgnoreCase("true")) {
                            if (me.hasProperty(CPU_PROPERTY)) {
                                agent.getRuntimeModelController().updateProperty(me.getUUID(), CPU_PROPERTY, new Integer(cpu).toString());
                            } else {
                                agent.getRuntimeModelController().addProperty(me.getUUID(), CPU_PROPERTY, new Integer(cpu).toString());
                            }
                        }
                    }
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                } catch (PropertyNotExistException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void loadbalancing() {

        ArrayList<ManagedElement> mes = null;

        synchronized (this) {
            List<ManagedElement> tmp = this.agent.getRuntimeModel().getManagedElements(CorePluginFactory.NAMESPACE,
                    Scope.NAME, ManagedElement.VALID);
            mes = (ArrayList)((ArrayList)tmp).clone();
        }

        int total = 0;
        List<String> active_nodes = new ArrayList<String>();
        List<String> inactive_nodes = new ArrayList<String>();

        String msg = "";

        // get active nodes that participate in the loadbalancing...
        // and add their cpu load to the total value
        if (mes != null && mes.size() > 0) {
            for (ManagedElement me : mes) {
                if (me instanceof Scope) {

                    ArrayList<String> nodes = null;
                    synchronized (this) {
                        List<String> tmp2 = ((Scope)me).getNodes();
                        nodes = (ArrayList)((ArrayList)tmp2).clone();
                    }

                    if (nodes != null && nodes.size() > 0) {
                        for (String node : nodes) {
                            // check that the node has the "loadbalancing" property
                            String loadbalanced = agent.getRuntimeModelController().getPropertyValue(node, LOADBALANCING_PROPERTY);
                            if (loadbalanced != null) {
                                if (loadbalanced.equalsIgnoreCase("true")) {
                                    // check that the node is "active"
                                    String active = agent.getRuntimeModelController().getPropertyValue(node, "active");
                                    if (active != null && active.equalsIgnoreCase("true")) {
                                        //
                                        active_nodes.add(node);
                                        // get the cpu value of the node
                                        String cpu = agent.getRuntimeModelController().getPropertyValue(node, CPU_PROPERTY);
                                        if (cpu != null) {
                                            msg += "**** cpu of '"+node+"' is: "+cpu+"\n";
                                            total += new Integer(cpu).intValue();
                                        }
                                    } else {
                                        inactive_nodes.add(node);
                                    }
                                }  else {
                                    // nothing to do
                                }
                            }
                        }
                    }

                }
            }

            if (active_nodes.size() > 0) {
                // formula
                // calculate how many nodes should be active to support the load
                int should_be_activated = (total / max_limite) + 1;

                if (should_be_activated < active_nodes.size()) {
                    int to_deactivate = active_nodes.size() - should_be_activated;
                    for (int i=0; i<to_deactivate; i++) {
                        try {
                            agent.getRuntimeModelController().updateProperty(active_nodes.get(i), "active", "false");
                        } catch (PropertyNotExistException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    int to_activate = should_be_activated - active_nodes.size();
                    for (int i=0; i<to_activate; i++) {
                        try {
                            if ( inactive_nodes.size() >= to_activate) {
                                agent.getRuntimeModelController().updateProperty(inactive_nodes.get(i), "active", "true");
                            } else {
                                System.out.println("[WARNING] LoadBalancer : Load is high and we cannot find other Nodes to activate!");
                            }
                        } catch (PropertyNotExistException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                msg += "to activate: " + should_be_activated;
                System.out.println(msg);
            }

        }



    }

    public void start() {
        this.working = true;
    }

    public void stop() {
        this.working = false;
    }

    public void destroy() {
        this.destroyRequested = true;
    }

    public void run() {
        while (true) {
            try {
                //System.out.println("..... ping .....");
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
