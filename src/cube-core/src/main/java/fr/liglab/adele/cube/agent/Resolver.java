package fr.liglab.adele.cube.agent;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:17 PM
 */
public interface Resolver {
    public void receiveMessage(CMessage msg);
    public CubeAgent getCubeAgent();
}
