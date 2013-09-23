package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicationException;
import fr.liglab.adele.cube.autonomicmanager.MessagesListener;

import java.io.IOException;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 12:04 AM
 */
public interface CommunicatorExtensionPoint extends ExtensionPoint {

    public void sendMessage(CMessage msg) throws CommunicationException, IOException;
    public void addMessagesListener(MessagesListener callback);
    public void start();
    public void stop();
    public void destroy();
}
