package fr.liglab.adele.cube.autonomicmanager.rmc;

import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelController;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicationException;
import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: debbabi
 * Date: 9/22/13
 * Time: 8:33 PM
 */
public class RuntimeModelControllerMessageHandler {

    RuntimeModelController rmc;

    public RuntimeModelControllerMessageHandler(RuntimeModelController rmc) {
        this.rmc = rmc;
    }

    void handleMessage(CMessage msg) throws Exception {

        if (msg != null) {
            if (msg.getBody() != null) {
                if (msg.getBody().toString().equalsIgnoreCase("getAttributeValue")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");

                    String p = null;
                    if (uuid != null && name != null) {
                        p = rmc.getAttributeValue(uuid.toString(), name.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(p);
                    try {
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(msg.getBody().toString().equalsIgnoreCase("getState")) {
                    Object uuid = msg.getHeader("uuid");

                    int p = -1;
                    if (uuid != null) {
                        p = rmc.getState(uuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(p);
                    try {
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (msg.getBody().toString().equalsIgnoreCase("addAttribute")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object value = msg.getHeader("value");

                    boolean p = false;
                    if (uuid != null && name != null && value != null) {
                        p = rmc.addAttribute(uuid.toString(), name.toString(), value.toString());
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
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
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
                        List<String> res = rmc.getReferencedElements(uuid.toString(), name.toString());
                        for (String r : res) {
                            resultat += rmc.getAutonomicManager().getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(r) + "###" + r + ",";
                        }
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(resultat);

                    try {
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
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
                    Object refamuri = msg.getHeader("refamuri");
                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        this.rmc.getAutonomicManager().getExternalInstancesHandler().addExternalInstance(
                                    refuuid.toString(), msg.getFrom().toString());
                        p = rmc.addReferencedElement(uuid.toString(), name.toString(),
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
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
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
                        p = rmc.removeReferencedElement(uuid.toString(), name.toString(), refuuid.toString());
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
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
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
                        p = rmc.hasReferencedElement(uuid.toString(), name.toString(), refuuid.toString());
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
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
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
                        p = rmc.destroyElement(uuid.toString());
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
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }   else if (msg.getBody().toString().equalsIgnoreCase("getRemoteElement")) {
                    Object uuid = msg.getHeader("uuid");
                    ManagedElement me = null;
                    if (uuid != null) {
                        me = rmc.getLocalElement(uuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setAttachment(me);
                    resmsg.setObject(msg.getObject());
                    try {
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   else if (msg.getBody().toString().equalsIgnoreCase("updateAttribute")) {
                    Object uuid = msg.getHeader("uuid");
                    Object pname = msg.getHeader("name");
                    Object pvalue = msg.getHeader("value");

                    String oldvalue = null;
                    if (uuid != null && pname != null && pvalue != null) {
                        oldvalue = rmc.updateAttribute(uuid.toString(), pname.toString(), pvalue.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(oldvalue);

                    try {
                        rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }  else if (msg.getBody().toString().equalsIgnoreCase("removeReferencedElements")) {
                    Object uuid = msg.getHeader("uuid");
                    if (uuid != null) {
                        List<String> toBeRemoved = new ArrayList<String>();
                        toBeRemoved.add(uuid.toString());
                        ((RuntimeModelImpl) rmc.getAutonomicManager().getRuntimeModelController().getRuntimeModel()).removeReferencedElements(toBeRemoved);
                    }
                } else if (msg.getBody().toString().equalsIgnoreCase("getCopyOfManagedElement")) {
                    Object uuid = msg.getHeader("uuid");

                    if (uuid == null) {

                    } else {
                        ManagedElement me = rmc.getRuntimeModel().getManagedElement(uuid.toString());
                        if (me != null) {

                            ManagedElement copy = (ManagedElement) me.clone();

                            CMessage resmsg = new CMessage();
                            resmsg.setTo(msg.getFrom());
                            resmsg.setCorrelation(msg.getCorrelation());
                            resmsg.setObject(msg.getObject());
                            resmsg.setAttachment(copy);

                            try {
                                rmc.getAutonomicManager().getCommunicator().sendMessage(resmsg);
                            } catch (CommunicationException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {

                        }

                    }

                }
            }
        }
    }
}
