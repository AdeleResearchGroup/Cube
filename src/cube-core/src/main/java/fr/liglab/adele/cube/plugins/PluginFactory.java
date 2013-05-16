package fr.liglab.adele.cube.plugins;


import fr.liglab.adele.cube.agent.CubeAgent;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:06 PM
 */
public interface PluginFactory {

    public String getName();
    public String getPrefix();
    public String getNamespace();

    public Plugin getPluginInstance(CubeAgent agent, Properties properties);

}
