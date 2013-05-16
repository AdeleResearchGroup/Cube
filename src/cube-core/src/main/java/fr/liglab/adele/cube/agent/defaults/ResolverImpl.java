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


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.*;
import fr.liglab.adele.cube.agent.defaults.resolver.Constraint;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Notification;
import fr.liglab.adele.cube.agent.defaults.resolver.ResolutionGraph;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;

import java.io.IOException;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:18 PM
 */
public class ResolverImpl implements Resolver, RuntimeModelListener {

    private CubeAgent agent;

    //private Thread thread;
    //private RuntimeModelChecker checker;

    public ResolverImpl(CubeAgent agent) {
        this.agent = agent;
        if (agent == null)
            throw new NullPointerException();
        if (agent != null) {
            agent.getRuntimeModel().addListener(this);
        }
        //checker = new RuntimeModelChecker(this);
    }

    public void update(RuntimeModel rm, Notification notification) {
        switch (notification.getNotificationType()) {
            /*
            case RuntimeModelListener.NEW_UNCHECKED_INSTANCE: {
                Object instance = notification.getNewValue();
                if (instance != null && instance instanceof ManagedElement) {
                    resolveUncheckedInstance((ManagedElement) instance);
                }
            } break;
            */
            case RuntimeModelListener.UPDATED_RUNTIMEMODEL: {
                for (ManagedElement me : agent.getRuntimeModel().getManagedElements(ManagedElement.UNCHECKED)) {
                    resolveUncheckedInstance(me);
                }
            } break;
        }
    }

    void resolveUncheckedInstance(ManagedElement instance) {
        info("resolving UNCHECKED element '"+instance.getUri()+"'...");
        boolean check = false;
        if (instance != null) {
            synchronized (instance) {
                if (instance.getState() == ManagedElement.UNCHECKED && ((AbstractManagedElement)instance).isInResolution() == false) {
                    ((AbstractManagedElement)instance).setInResolution(true);
                    check = true;
                } else {
                    return;
                }
            }
        } else {
            return;
        }
        if (check == true) {
            info("resolving UNCHECKED element '"+instance.getUri()+"' STARTS...");
            /*
             * Create the root variable that contains the newly created instance (to be resolved).
             */
            Variable var = new Variable(agent, instance.getNamespace(), instance.getName());
            var.setValue(instance.getUUID());

            /*
             * Create a Resolution Graph (Constraints Graph).
             */
            ResolutionGraph constraintsGraph = new ResolutionGraph(this);
            /*
             * Set the root variable.
             */
            constraintsGraph.setRoot(var);

            /*
             * Start the resolution processs.
             */
            info("resolving new instance: " + instance.getUri() + " ...");
            info("");
            if (constraintsGraph.resolve()) {

                if (validateSolution(constraintsGraph)) {
                    agent.getRuntimeModel().refresh();
                }
                // notify others about changes on the runtime model!
                //((RuntimeModelImpl)agent.getRuntimeModel()).refresh();

            } else {
                System.out.println("[RESOLVER] element '"+instance.getName()+"' not resolved! ");
                getCubeAgent().removeUnmanagedElements();
            }
            ((AbstractManagedElement)instance).setInResolution(false);

        }
    }

    /**
     * Validate the found solution.
     * @param graph
     */
    boolean validateSolution(ResolutionGraph graph) {
        info("");
        info("validating solution..");
        info("\n");
        if (graph != null) {
            if (graph.getRoot() != null) {
                return validateVariable(graph.getRoot());
            }
        }
        return false;
    }

    boolean validateVariable(Variable v) {
        boolean changed = false;
        if (v.getValue() != null) {
            //
            if (!v.isPrimitive()) {
                for (Constraint c : v.getBinaryConstraints()) {
                    validateVariable(c.getObjectVariable());
                }
            }
            //
            Object uuid =v.getValue();
            if (uuid != null) {
                String agenturi = getCubeAgent().getRuntimeModelController().getAgentOfElement(uuid.toString());
                if (getCubeAgent().getUri().equalsIgnoreCase(agenturi)) {
                    ManagedElement me = agent.getRuntimeModelController().getLocalElement(uuid.toString());
                    if (me != null) {
                        if (me.getState() == ManagedElement.UNCHECKED) {
                            ((AbstractManagedElement)me).validate();
                            ((AbstractManagedElement)me).setInResolution(false);
                        } else if (me.getState() == ManagedElement.UNMANAGED) {
                            agent.getRuntimeModel().add(me);
                            ((AbstractManagedElement)me).setInResolution(false);
                        }
                        changed = true;
                    }
                }
                else {
                    //info("... remote createValue");

                    CMessage msg = new CMessage();
                    msg.setTo(agenturi);
                    msg.setFrom(agent.getUri());
                    msg.setReplyTo(agent.getUri());
                    msg.setObject("resolution");
                    msg.setBody("validateVariable");
                    msg.setAttachement(v);
                    try {
                        //System.out.println("sending..." + msg.toString());
                        //System.out.println(v.getTextualDescription());
                        send(msg);
                    } catch (TimeOutException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        return changed;
    }

    /**
     * FIND
     * @param v
     * @return
     */
    String find (Variable v) {

        return null;

    }

    private void info(String msg) {
        if (this.agent.getConfig().isDebug() == true) {
            System.out.println("[RESOLVER] " + msg);
        }
    }

    public void receiveMessage(CMessage msg) {
        if (msg.getCorrelation() == waitingCorrelation) {
            this.waitingMessage = msg;
            if (csplock != null) {
                synchronized (csplock) {
                    csplock.notify();
                }
            }
            waitingCorrelation = -1;
        }
        try {
            handleMessage(msg);
        }  catch(Exception ex) {
            //getCubeAgent().getLogger().error("[AbstractPlugin.receiveMessage] " + ex.getMessage());
        }
    }

    protected void handleMessage(CMessage msg) throws Exception {

        if (msg != null) {
            //System.out.println("\n\n received message to resolve! \n\n "+msg.toString()+" \n\n");
            if (msg.getBody() != null && msg.getBody().toString().equalsIgnoreCase("findUsingCharacteristics")) {
                if (msg.getAttachement() != null && msg.getAttachement() instanceof Variable) {
                    ResolutionGraph constraintsGraph = new ResolutionGraph(this);
                    Variable v = (Variable)msg.getAttachement();
                    constraintsGraph.setRoot(v);

                    String result = constraintsGraph.find();
                    //String result = constraintsGraph.findUsingCharacteristics(v);

                    //System.out.println("found:" + result);
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setFrom(getCubeAgent().getUri());
                    resmsg.setReplyTo(getCubeAgent().getUri());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(result);
                    try {
                        //System.out.println("in msg:" + msg.toString());
                        //System.out.println("out msg:" + resmsg.toString());
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else if (msg.getBody() != null && msg.getBody().toString().equalsIgnoreCase("createUsingCharacteristics")) {
                if (msg.getAttachement() != null && msg.getAttachement() instanceof Variable) {
                    ResolutionGraph constraintsGraph = new ResolutionGraph(this);
                    Variable v = (Variable)msg.getAttachement();
                    constraintsGraph.setRoot(v);

                    String result = constraintsGraph.create();
                    if (result == null) {
                        //getCubeAgent().removeUnmanagedElements();
                    } else {
                        //
                    }

                    //String result = constraintsGraph.findUsingCharacteristics(v);

                    //System.out.println("found:" + result);
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setFrom(getCubeAgent().getUri());
                    resmsg.setReplyTo(getCubeAgent().getUri());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(result);
                    try {
                        //System.out.println("in msg:" + msg.toString());
                        //System.out.println("out msg:" + resmsg.toString());
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }  else if (msg.getBody() != null && msg.getBody().toString().equalsIgnoreCase("validateVariable")) {
                if (msg.getAttachement() != null && msg.getAttachement() instanceof Variable) {

                    Variable v = (Variable)msg.getAttachement();
                    validateVariable(v);
                    agent.getRuntimeModel().refresh();
                }
            }
        }
    }

    public void send(CMessage msg) throws Exception {
        if (msg != null) {
            msg.setFrom(getCubeAgent().getUri());
            msg.setReplyTo(getCubeAgent().getUri());
            /*if (msg.getTo() != null && !msg.getTo().contains("/ext/"))
                msg.setTo(msg.getTo() + "/ext/" + getExtensionFactory().getExtensionId());*/
            getCubeAgent().getCommunicator().sendMessage(msg);
        }
    }

    public CMessage sendAndWait(CMessage msg) throws TimeOutException {
        if (msg != null) {
            msg.setFrom(getCubeAgent().getUri());
            msg.setReplyTo(getCubeAgent().getUri());
            /*if (msg.getTo() != null && !msg.getTo().contains("/ext/"))
                msg.setTo(msg.getTo() + "/ext/" + getExtensionFactory().getExtensionId());*/
            //String to = msg.getTo();
            msg.setCorrelation(++correlation);
            waitingCorrelation = msg.getCorrelation();
            //System.out.println(msg.toString());
            try {
                this.waitingMessage = null;

                this.getCubeAgent().getCommunicator().sendMessage(msg);
            } catch (Exception e) {
                //this.getCubeAgent().getLogger().warning("The Plugin could not send a message to " + to + "!");
            }
            try {
                long initialTime = System.currentTimeMillis();
                long currentTime = initialTime;
                long waitingTime = TIMEOUT;
                synchronized (csplock) {
                    while (((currentTime < (initialTime + TIMEOUT)) && waitingTime > 1)
                            && (this.waitingMessage == null)) {
                        csplock.wait(waitingTime);
                        currentTime = System.currentTimeMillis();
                        waitingTime = waitingTime - (currentTime - initialTime);
                    }
                }
            } catch (InterruptedException e) {
                //this.getCubeAgent().getLogger().warning("The Plugin waits for a response message from " + to + " but no answer! timeout excedded!");
            }
            return this.waitingMessage;
        } else {
            return null;
        }
    }

    private CMessage waitingMessage = null;
    private long TIMEOUT = 3000;
    private Object csplock = new Object();
    private static long correlation = 1;
    private long waitingCorrelation = -1;



    /**
     * Recursive backtracking search algo.
     * Returns a solution, or failure.
     *
     * uses domain-specific heuristic functions derived from the knowledge of the problem.
     *
     * propagating information through constraints:
     *   Whenever a variable X is assigned, the forxard checking process looks at each unassigned
     *   variable Y that is connected to X by a constraint and deletes from Y's domain any value
     *   that is in consistent with the value chosen for X.
     *
     * Chronological backtracking:
     *   When a branch of the search fails! back up to the preceding variable and try a different value for it.
     *
     *
     *
     *
     * @param csp
     */
    void backtrackingSearch(ResolutionGraph csp) {
        /**
         * if assignement is complete then return assignement
         * var = select_unassigned_variable(variables[constraintsGraph], assignement, csp) do
         * for each 'value' in Order_Domain_Values(var, assignement, csp) do
         *   if 'value' is consistent with assignment according to Constraints[csp] then
         *      add {var = value} to assignement
         *      result = backtrackingSearch(assignement, csp)
         *      if (result != failure then return result;
         *      remove {var = value} from assignement
         * return failure
         */
    }

    void BT() {
        /**
         * Foreach Val in D[i]
         *      Assignments[i] = val.
         *      Consistent = true
         *      for h=1 To i-1 While Consistent
         *             Consistent = Test(i, h)
         *      if Consistent
         *          if i = n
         *                 Show ( Solution() )
         *          Else
         *              BT(i+1)
         *      return false;
         */
    }

    public CubeAgent getCubeAgent() {
        return this.agent;
    }

}
