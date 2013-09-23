package fr.liglab.adele.cube.autonomicmanager;

import fr.liglab.adele.cube.extensions.CommunicatorExtensionPoint;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicationException;

import java.io.IOException;

/**
 * User: debbabi
 * Date: 9/17/13
 * Time: 11:11 PM
 */
public interface Communicator extends MessagesListener {

    public void addSpecificCommunicator(CommunicatorExtensionPoint scomm);

    public void sendMessage(CMessage msg) throws CommunicationException, IOException;
    public void addMessagesListener(String id, MessagesListener callback) throws Exception;

}
