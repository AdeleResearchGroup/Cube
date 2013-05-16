package fr.liglab.adele.cube.agent;

import java.io.IOException;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 12:04 AM
 */
public interface Communicator {

    public String getName();
    public void sendMessage(CMessage msg) throws CommunicationException, IOException;
    public void addMessagesListener(String id, MessagesListener callback) throws Exception;
    public void run(CubeAgent agent) throws Exception;
    public void stop();
    public void destroy();
}
