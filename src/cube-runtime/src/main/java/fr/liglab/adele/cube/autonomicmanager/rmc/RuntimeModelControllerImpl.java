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


package fr.liglab.adele.cube.autonomicmanager.rmc;

import fr.liglab.adele.cube.autonomicmanager.*;
import fr.liglab.adele.cube.autonomicmanager.comm.TimeOutException;
import fr.liglab.adele.cube.autonomicmanager.life.ExternalInstancesHandlerImpl;
import fr.liglab.adele.cube.extensions.ManagedElementExtensionPoint;
import fr.liglab.adele.cube.metamodel.*;
import fr.liglab.adele.cube.AutonomicManager;

import java.util.*;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:49 PM
 */
public class RuntimeModelControllerImpl implements RuntimeModelController {

    private AutonomicManager am;

    private RuntimeModel rm;

    private Map<String, ManagedElementExtensionPoint> factories;



    private RuntimeModelControllerMessageHandler msgHandler;

    public RuntimeModelControllerImpl(AutonomicManager am) {
        this.am = am;
        this.rm = new RuntimeModelImpl(am);
        this.factories = new HashMap<String, ManagedElementExtensionPoint>();

        msgHandler = new RuntimeModelControllerMessageHandler(this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// ATTRIBUTES /////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int getState(String managed_element_uuid) {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                return me1.getState();
            }
        } if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getState");
                msg.addHeader("uuid", managed_element_uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            return new Integer(resultmsg.getBody().toString());
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }else {
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return -1;
    }

    public String getAttributeValue(String managed_element_uuid, String name) {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                return me1.getAttribute(name);
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getAttributeValue");
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
                System.out.println(".... get property value: impossible to find autonomic manager uri of instance " + managed_element_uuid);
            }
        } else {
            info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
        }

        return null;
    }

    public boolean addAttribute(String managed_element_uuid, String name, String value) throws PropertyExistException, InvalidNameException {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = am.getRuntimeModelController().getRuntimeModel().getManagedElement(managed_element_uuid);
            if (me1 != null) {
                return me1.addAttribute(name, value);
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("addAttribute");
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
            } else {
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String updateAttribute(String managed_element_uuid, String name, String newValue) throws PropertyNotExistException {
        if (isLocalInstance(managed_element_uuid)){
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                //System.out.println("updating instance property! "+name+"="+newValue);
                String old = me1.updateAttribute(name, newValue);
                if (old != null && !old.equalsIgnoreCase(newValue)) {
                    me1.updateState(ManagedElement.INVALID);
                }
                return old;
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("updateAttribute");
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
            } else {
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return null;
    }

    public String getAutonomicManagerOf(String uuid) {
        if (isLocalInstance(uuid)) {
            return this.getAutonomicManager().getUri();
        } else if (isRemoteInstance(uuid)) {
            return this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(uuid);
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// REFERENCES /////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<String> getReferencedElements(String managed_element_uuid, String reference_name) {
        List<String> result = new ArrayList<String>();
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                Reference r = me1.getReference(reference_name);
                if (r != null) {
                    return r.getReferencedElements();
                } else {
                }
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
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
                                    String amUri = tmp2[0];
                                    String elementuuid = tmp2[1];
                                    if (!amUri.equalsIgnoreCase(getAutonomicManager().getUri())) {
                                        this.am.getExternalInstancesHandler().addExternalInstance(elementuuid, amUri);
                                    }
                                    result.add(elementuuid);
                                }
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            } else {
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return result;
    }

    public boolean hasReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuri) {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                Reference r = me1.getReference(reference_name);
                if (r != null) {
                    return r.hasReferencedElement(referenced_element_uuri);
                }
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
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
            } else {
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return false;
    }

    public boolean addReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuid) throws InvalidNameException {
        return addReferencedElement(managed_element_uuid, reference_name, false, referenced_element_uuid);
    }

    public boolean addReferencedElement(String managed_element_uuid, String reference_name, boolean onlyone, String referenced_element_uuid) throws InvalidNameException {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                Reference r = me1.getReference(reference_name);
                if (r == null) {
                    r = me1.addReference(reference_name, onlyone);
                }
                if (r != null) {
                    return r.addReferencedElement(referenced_element_uuid);
                }
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
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
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return false;
    }

    public boolean removeReferencedElement(String managed_element_uuid, String reference_name, String referenced_element_uuid) {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                Reference r = me1.getReference(reference_name);
                if (r != null) {
                    return r.removeReferencedElement(referenced_element_uuid);
                }
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
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
            } else {
                info("External autonomic manager of element '"+managed_element_uuid+"' not found!");
            }
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// RUNTIME MODEL INSTANCES ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RuntimeModel getRuntimeModel() {
        return this.rm;
    }

    public ManagedElement newManagedElement(String namespace, String name, Properties properties)
            throws NotFoundManagedElementException, InvalidNameException, PropertyExistException {
        ManagedElementExtensionPoint mexp = this.factories.get(namespace.toLowerCase()+":"+name.toLowerCase());
        if (mexp != null) {
            ManagedElement me = mexp.newInstance(properties);
            if (me != null) {
                ((RuntimeModelImpl)this.rm).add(me, ManagedElement.INVALID);
                return me;
            }
        } else {
            throw new NotFoundManagedElementException("No managed element with this name '"+namespace+":"+name+"'");
        }
        return null;
    }

    public ManagedElement newManagedElement(String namespace, String name, Properties properties, boolean isUnmanaged)
            throws NotFoundManagedElementException, InvalidNameException, PropertyExistException {
        ManagedElementExtensionPoint mexp = this.factories.get(namespace.toLowerCase()+":"+name.toLowerCase());
        if (mexp != null) {
            ManagedElement me = mexp.newInstance(properties);
            if (me != null) {
                if (isUnmanaged == true) {
                    ((RuntimeModelImpl)this.rm).add(me, ManagedElement.UNMANAGED);
                } else {
                    ((RuntimeModelImpl)this.rm).add(me, ManagedElement.INVALID);
                }
                return me;
            }
        } else {
            throw new NotFoundManagedElementException("No managed element with this name '"+namespace+":"+name+"'");
        }
        return null;
    }

    public boolean isLocalInstance(String uuid) {
        return getRuntimeModel().getManagedElement(uuid) != null;
    }

    public boolean isRemoteInstance(String uuid) {
        return this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(uuid) != null;
    }

    public ManagedElement getCopyOfManagedElement(String uuid) {
        if (isLocalInstance(uuid)) {
            ManagedElement me = this.getRuntimeModel().getManagedElement(uuid);
            if (me != null) {
                try {
                    return (ManagedElement) me.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        } else if (isRemoteInstance(uuid)) {
            //System.out.println("\n[RMC] getCopyOfManagedElement method is not implemented yet for remote instances!\n");
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getCopyOfManagedElement");
                msg.addHeader("uuid", uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        //System.out.println("getCopyOfManagedElement...return:\n"+msg.toString());
                        return resultmsg.getAttachment();
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            } else {
                info("External autonomic manager of element '"+uuid+"' not found!");
            }
        }
        return null;
    }

    public void addManagedElement(ManagedElement managedElement) {
        if (managedElement != null) {
            ((RuntimeModelImpl)this.rm).add(managedElement, ManagedElement.INVALID);
        }
    }


    public boolean removeManagedElement(String uuid) {
        if (isLocalInstance(uuid)) {
            ManagedElement me1 = getLocalElement(uuid);
            if (me1 != null) {
                ((RuntimeModelImpl) am.getRuntimeModelController().getRuntimeModel()).remove(me1);
            }
        }
        return true;
    }

    /**
     * remove from local runtime model, and remove all references to it
     * @param managed_element_uuid
     * @return
     */
    public boolean destroyElement(String managed_element_uuid) {
        if (isLocalInstance(managed_element_uuid)) {
            ManagedElement me1 = getLocalElement(managed_element_uuid);
            if (me1 != null) {
                // get its references
                List<Reference> outRefs = me1.getReferences();
                ManagedElement result = me1;
                ((RuntimeModelImpl) am.getRuntimeModelController().getRuntimeModel()).remove(me1);
                if (result != null)
                {
                    List<String> toBeRemovedLocally = new ArrayList<String>();
                    List<String> toBeRemovedRemotely = new ArrayList<String>();
                    toBeRemovedLocally.add(managed_element_uuid);
                    for (Reference r : outRefs) {
                        for (String ss : r.getReferencedElements()) {
                            // check if it is a remote reference
                            ManagedElement me = getLocalElement(ss);
                            if (me == null) {
                                String agent = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(ss);
                                if (agent != null)
                                    toBeRemovedRemotely.add(agent);
                            }
                        }
                    }
                    // remove references from the local Runtime Model
                    for (String agent : toBeRemovedRemotely) {
                        CMessage msg = new CMessage();
                        msg.setTo(agent);
                        msg.setObject("runtimemodel");
                        msg.setBody("removeReferencedElements");
                        msg.addHeader("uuid", managed_element_uuid);

                        try {
                            send(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    /*
                    for (String s : toBeRemovedLocally) {
                        //System.out.println(">> " + s);
                    }*/
                    ((RuntimeModelImpl) am.getRuntimeModelController().getRuntimeModel()).removeReferencedElements(toBeRemovedLocally);


                }
                return result != null;
            }
        } else if (isRemoteInstance(managed_element_uuid)) {
            String auri = this.am.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(managed_element_uuid);
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
    /*
    public boolean move(ManagedElement me, String amUri) {
        if (me != null) {
            if (isLocalInstance(me.getUUID())) {
                me.setAutonomicManager(amUri);
                CMessage msg = new CMessage();
                msg.setTo(amUri);
                msg.setAttachment(me);
                msg.setFrom(getAutonomicManager().getUri());
                msg.setReplyTo(getAutonomicManager().getUri());
                msg.setObject("runtimemodel");
                msg.setBody("move");
                try {
                    getAutonomicManager().getCommunicator().sendMessage(msg);
                    // remove from the local runtime model
                    getExternalInstancesHandler().addExternalInstance(me.getUUID(), amUri);
                    ((RuntimeModelImpl)getRuntimeModel()).remove(me);

                } catch (CommunicationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    } */

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// UTILS //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addManagedElementFactory(ManagedElementExtensionPoint exp) {
        this.factories.put(exp.getExtension().getExtensionFactory().getNamespace().toLowerCase()+":"+exp.getName().toLowerCase(), exp);
    }

    public ManagedElement getLocalElement(String managed_element_uuid) {
        ManagedElement me = am.getRuntimeModelController().getRuntimeModel().getManagedElement(managed_element_uuid);
        if (me != null)
            return me;
        return null;
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
            this.msgHandler.handleMessage(msg);
        }  catch(Exception ex) {
        }
    }

    public void send(CMessage msg) throws Exception {
        if (msg != null) {
            msg.setFrom(getAutonomicManager().getUri());
            msg.setReplyTo(getAutonomicManager().getUri());
            getAutonomicManager().getCommunicator().sendMessage(msg);
        }
    }

    public CMessage sendAndWait(CMessage msg) throws TimeOutException {
        if (msg != null) {
            msg.setFrom(getAutonomicManager().getUri());
            msg.setReplyTo(getAutonomicManager().getUri());
            msg.setCorrelation(++correlation);
            waitingCorrelation = msg.getCorrelation();
            try {
                this.waitingMessage = null;

                this.getAutonomicManager().getCommunicator().sendMessage(msg);
            } catch (Exception e) {
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
            }
            return this.waitingMessage;
        } else {
            return null;
        }
    }

    public AutonomicManager getAutonomicManager() {
        return this.am;
    }

    void info(String msg) {
        if (this.am.getConfiguration().isDebug() == true) {
            System.out.println("[RMC:"+this.am.getUri()+":"+this.hashCode()+"] " + msg);
        }
    }

    private CMessage waitingMessage = null;
    private long TIMEOUT = 3000;
    private Object csplock = new Object();
    private static long correlation = 1;
    private long waitingCorrelation = -1;
}
