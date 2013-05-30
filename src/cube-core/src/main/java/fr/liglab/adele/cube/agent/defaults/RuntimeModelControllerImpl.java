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
import fr.liglab.adele.cube.metamodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:49 PM
 */
public class RuntimeModelControllerImpl implements RuntimeModelController {

    CubeAgent agent;

    public RuntimeModelControllerImpl(CubeAgent agent) {
        this.agent = agent;
    }

    public String getAgentOfElement(String managed_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.getCubeAgent();
        } else {
            String s = this.agent.getExternalAgentUri(managed_element_uuid);
            return s;
        }
    }

    public boolean setAgentOfElement(String managed_element_uuid, String agentUri) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            ((AbstractManagedElement)me1).setCubeAgent(agentUri);
            return true;
        } else {
            try {
                throw new Exception("RuntimeModelController.setAgentOfElement (for remote elements) is not yet implemented!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getPropertyValue(String managed_element_uuid, String name) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            //System.out.println(".... get property value (local) of element: " + managed_element_uuid);
            return me1.getProperty(name);
        } else {
            //System.out.println(".... get property value (remote) of element: " + managed_element_uuid);
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getPropertyValue");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", name);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            return resultmsg.getBody().toString();
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(".... get property value: impossible to find agent uri of instance " + managed_element_uuid);
            }
        }
        return null;
    }

    public boolean addProperty(String managed_element_uuid, String name, String value) throws PropertyExistException, InvalidNameException {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.addProperty(name, value);
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("addProperty");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", name);
                msg.addHeader("value", value);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String updateProperty(String managed_element_uuid, String name, String newValue) throws PropertyNotExistException {
        System.out.println("updateProperty:");
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            System.out.println("updating instance property! "+name+"="+newValue);
            String old = me1.updateProperty(name, newValue);
            if (old != null && !old.equalsIgnoreCase(newValue)) {
                // notify changes!
                ((AbstractManagedElement)me1).updateState(ManagedElement.UNCHECKED);
            }
            return old;
        } else {
            // remote
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("updateProperty");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", name);
                msg.addHeader("value", newValue);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            return resultmsg.getBody().toString();
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public List<String> getReferencedElements(String managed_element_uuid, String reference_name) {

        List<String> result = new ArrayList<String>();
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.getReferencedElements();
            } else {
                //System.out.println("CONTROLLER: no reference with name: " + reference_name);
            }
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getReferencedElements");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            String[] tmp = resultmsg.getBody().toString().split(",");
                            for (int i=0; i<tmp.length; i++) {
                                if (tmp[i] != null && tmp[i].length()>0) {
                                    String[] tmp2 = tmp[i].split("###");
                                    String agenturi = tmp2[0];
                                    String elementuuid = tmp2[1];
                                    if (!agenturi.equalsIgnoreCase(getCubeAgent().getUri())) {
                                        this.agent.addExternalElement(elementuuid, agenturi);
                                    }
                                    result.add(elementuuid);
                                }
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;



    }

    public boolean addReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuid) throws InvalidNameException {
        return addReferencedElement(managed_element_uuid, reference_name, false, referenced_element_uuid);
    }

    public boolean addReferencedElement(String managed_element_uuid, String reference_name, boolean onlyone, String referenced_element_uuid) throws InvalidNameException {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            //System.out.println("add Local Reference Element ...");
            Reference r = me1.getReference(reference_name);
            if (r == null) {
               // System.out.println("Reference '"+reference_name+"' does not exists! we will create it!");
                r = me1.addReference(reference_name, onlyone);
            }
            if (r != null) {
                r.addReferencedElement(referenced_element_uuid);
                return true;
            }
        } else {
            //System.out.println("add Remote Reference Element ...");
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("addReferencedElement");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                msg.addHeader("onlyone", onlyone==true?"true":"false");
                msg.addHeader("refuuid", referenced_element_uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("External agent of element '"+managed_element_uuid+"' not found!");
            }
        }
        return false;
    }

    public boolean removeReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.removeReferencedElement(referenced_element_uuid);
            }
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("removeReferencedElement");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                msg.addHeader("refuuid", referenced_element_uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean hasReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuri) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.hasReferencedElement(referenced_element_uuri);
            }
        }  else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("hasReferencedElement");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                msg.addHeader("refuuid", referenced_element_uuri);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public boolean destroyElement(String managed_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            // get its references
            List<Reference> outRefs = me1.getReferences();
            ManagedElement result = null;
            result = ((RuntimeModelImpl)agent.getRuntimeModel()).remove(me1);
            if (result != null)
            {
                //System.out.println("\n\n\nremoving instance: "+result.getUUID()+"\n\n\n");
                List<String> toBeRemovedLocally = new ArrayList<String>();
                List<String> toBeRemovedRemotely = new ArrayList<String>();
                //List<String> toBeRemovedRemotely = new ArrayList<String>();
                toBeRemovedLocally.add(managed_element_uuid);
                for (Reference r : outRefs) {
                    for (String ss : r.getReferencedElements()) {
                        // check if it is a remote reference
                        ManagedElement me = getLocalElement(ss);
                        if (me == null) {
                            String agent = getAgentOfElement(ss);
                            if (agent != null)
                                toBeRemovedRemotely.add(agent);
                        }
                    }
                }
                // remove references from the local Runtime Model
                for (String s : toBeRemovedLocally) {
                    System.out.println(">> " + s);
                }
                ((RuntimeModelImpl)agent.getRuntimeModel()).removeReferences(toBeRemovedLocally);

                // remove remote references
                //System.out.println("\n\n\nremove remote references not yet implemented!\n\n\n");

                for (String agent : toBeRemovedRemotely) {
                    CMessage msg = new CMessage();
                    msg.setTo(agent);
                    msg.setObject("runtimemodel");
                    msg.setBody("removeReferences");
                    msg.addHeader("uuid", managed_element_uuid);

                    try {
                        send(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            return result != null;
        }  else {

            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("destroyElement");
                msg.addHeader("uuid", managed_element_uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean areSimilar(String instance_uuid1, String instance_uuid2) {
        ManagedElement m1 = getLocalElement(instance_uuid1);
        if (m1 == null) {
            m1 = getRemoteElement(instance_uuid1);
        }
        ManagedElement m2 = getLocalElement(instance_uuid2);
        if (m2 == null) {
            m2 = getRemoteElement(instance_uuid2);
        }
        if (m1 != null && m2 != null) {
            m1.removeEmptyReferences();
            m1.removeEmptyProperties();
            m2.removeEmptyReferences();
            m2.removeEmptyReferences();
            return m1.isSimilar(m2);
        }
        return false;
    }

    public ManagedElement getLocalElement(String managed_element_uuid) {
        ManagedElement me = agent.getRuntimeModel().getManagedElement(managed_element_uuid);
        if (me != null)
            return me;
        else {
            for (ManagedElement unme : agent.getUnmanagedElements()) {
                if (unme.getUUID().equalsIgnoreCase(managed_element_uuid)) {
                    return unme;
                }
            }
        }
        return null;
    }

    public ManagedElement getRemoteElement(String managed_element_uuid) {
        String auri = agent.getExternalAgentUri(managed_element_uuid);
        if (auri != null) {
            CMessage msg = new CMessage();
            msg.setTo(auri);
            msg.setObject("runtimemodel");
            msg.setBody("getRemoteElement");
            msg.addHeader("uuid", managed_element_uuid);
            try {
                CMessage resultmsg = sendAndWait(msg);
                if (resultmsg != null) {
                    return resultmsg.getAttachement();
                }
            } catch (TimeOutException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ManagedElement getElement(String managed_element_uuid) {
        String auri = agent.getExternalAgentUri(managed_element_uuid);
        if (auri != null) {
            return getRemoteElement(managed_element_uuid);
        } else {
            return getLocalElement(managed_element_uuid);
        }
        //return null;
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
            if (msg.getBody() != null) {
                if (msg.getBody().toString().equalsIgnoreCase("getPropertyValue")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");

                    String p = null;
                    if (uuid != null && name != null) {
                        p = getPropertyValue(uuid.toString(), name.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(p);
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (msg.getBody().toString().equalsIgnoreCase("addProperty")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object value = msg.getHeader("value");

                    boolean p = false;
                    if (uuid != null && name != null && value != null) {
                        p = addProperty(uuid.toString(), name.toString(), value.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (msg.getBody().toString().equalsIgnoreCase("getReferencedElements")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");

                    String resultat = "";
                    if (uuid != null && name != null) {
                        List<String> res = getReferencedElements(uuid.toString(), name.toString());
                        for (String r : res) {
                            resultat += this.agent.getRuntimeModelController().getAgentOfElement(r) + "###" + r + ",";
                        }
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(resultat);

                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   else if (msg.getBody().toString().equalsIgnoreCase("addReferencedElement")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object onlyone = msg.getHeader("onlyone");
                    Object refuuid = msg.getHeader("refuuid");

                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        this.agent.addExternalElement(refuuid.toString(), msg.getFrom().toString());
                        p = addReferencedElement(uuid.toString(), name.toString(),
                                onlyone.toString().equalsIgnoreCase("true")?true:false , refuuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   else if (msg.getBody().toString().equalsIgnoreCase("removeReferencedElement")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object refuuid = msg.getHeader("refuuid");

                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        p = removeReferencedElement(uuid.toString(), name.toString(), refuuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   else if (msg.getBody().toString().equalsIgnoreCase("hasReferencedElement")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object refuuid = msg.getHeader("refuuid");

                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        p = hasReferencedElement(uuid.toString(), name.toString(), refuuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   else if (msg.getBody().toString().equalsIgnoreCase("destroyElement")) {

                    // destroyElement
                    Object uuid = msg.getHeader("uuid");
                    if (uuid == null) return;
                    boolean p = false;
                    if (uuid != null) {
                        p = destroyElement(uuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }   else if (msg.getBody().toString().equalsIgnoreCase("getRemoteElement")) {
                    Object uuid = msg.getHeader("uuid");
                    ManagedElement me = null;
                    if (uuid != null) {
                        me = getLocalElement(uuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setAttachement(me);
                    resmsg.setObject(msg.getObject());
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   else if (msg.getBody().toString().equalsIgnoreCase("updateProperty")) {
                    Object uuid = msg.getHeader("uuid");
                    Object pname = msg.getHeader("name");
                    Object pvalue = msg.getHeader("value");

                    String oldvalue = null;
                    if (uuid != null && pname != null && pvalue != null) {
                        oldvalue = updateProperty(uuid.toString(), pname.toString(), pvalue.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(oldvalue);

                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }  else if (msg.getBody().toString().equalsIgnoreCase("removeReferences")) {
                    Object uuid = msg.getHeader("uuid");
                    if (uuid != null) {
                        List<String> toBeRemoved = new ArrayList<String>();
                        toBeRemoved.add(uuid.toString());
                        ((RuntimeModelImpl)agent.getRuntimeModel()).removeReferences(toBeRemoved);
                    }
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

    CubeAgent getCubeAgent() {
        return this.agent;
    }

    private CMessage waitingMessage = null;
    private long TIMEOUT = 3000;
    private Object csplock = new Object();
    private static long correlation = 1;
    private long waitingCorrelation = -1;
}
