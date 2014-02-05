package fr.liglab.adele.cube.autonomicmanager.comm;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.Communicator;
import fr.liglab.adele.cube.extensions.CommunicatorExtensionPoint;
import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.MessagesListener;
import fr.liglab.adele.cube.util.perf.MessageMeasure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: debbabi
 * Date: 9/17/13
 * Time: 10:58 PM
 */
public class CommunicatorImpl implements Communicator {

    private AutonomicManager am;
    private Map<String, CommunicatorExtensionPoint> communicators;
    private Map<String, MessagesListener> callbacks = new HashMap<String, MessagesListener>();

    private boolean working = false;

    public CommunicatorImpl(AutonomicManager am) {
        this.am = am;
        communicators = new HashMap<String, CommunicatorExtensionPoint>();
    }

    public synchronized void addSpecificCommunicator(CommunicatorExtensionPoint scomm) {
        this.communicators.put(scomm.getName(), scomm);

        scomm.addMessagesListener(this);

    }

    public synchronized void sendMessage(CMessage msg) throws CommunicationException, IOException {
        for (String c : communicators.keySet()) {
            communicators.get(c).sendMessage(msg);
            //System.out.println("[INFO:"+am.getUri()+"] Sending message using '" + communicators.get(c).getName() + "' communicator...\n" + msg.toString());
        }
        /*
        String headers = "";
        if (msg.getHeaders() != null) {
            for (Object key : msg.getHeaders().keySet()) {
                headers += key + ":" + msg.getHeaders().get(key) + ",";
            }
        }
        */

            MessageMeasure m = new MessageMeasure(am.getUri(), msg.getFrom(), msg.getTo(), "", msg.getObject(),
                    msg.getBody()!=null?msg.getBody().toString():"");
            am.getAdministrationService().getPerformanceChecker().addMessageMeasure(m);

    }
    public void addMessagesListener(String id, MessagesListener callback) throws Exception {
        if (this.callbacks.containsKey(id) == false) {
            this.callbacks.put(id, callback);
        }
    }

    public void receiveMessage(CMessage msg) {
        notifyMessageArrival(msg);
        //if (am.getUri().equalsIgnoreCase("cube://localhost:38000"))
        //    System.out.println("[INFO"+am.getUri()+"] Receive message:\n" + msg.toString());
    }

    private void notifyMessageArrival(CMessage msg) {
        if (msg != null) {
            MessagesListener ml = null;
            if (msg != null && msg.getTo() != null && msg.getTo().trim().length() > 0) {
                synchronized (this.callbacks) {
                    ml = this.callbacks.get(msg.getTo());
                }
                if (ml != null) {
                    ml.receiveMessage(msg);
                }
            }
        }
    }

}
