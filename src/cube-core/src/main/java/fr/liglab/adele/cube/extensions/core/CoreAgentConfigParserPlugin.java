package fr.liglab.adele.cube.extensions.core;

import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.ExtensionConfiguration;
import fr.liglab.adele.cube.util.parser.AgentConfigParserPlugin;
import fr.liglab.adele.cube.util.parser.ParseException;
import fr.liglab.adele.cube.util.xml.XMLElement;

public class CoreAgentConfigParserPlugin implements AgentConfigParserPlugin {

	private static final String SET_SCOPE = "set-scope";	
	private static final String SET_NODE = "set-node";
	private static final String ID = "id";
	private static final String TYPE = "type";

	public ExtensionConfiguration parseExtensionConfiguration(
			XMLElement element, AgentExtensionConfig extensionConfig)
			throws ParseException {
		if (element != null) {
			/*
			 * Initializer
			 */
			InitializerConfiguration initializerConf = new InitializerConfiguration();
			if (element.getName() == initializerConf.getName()) {
				XMLElement[] childs = element.getElements();
				if (childs != null && childs.length>0) {
					for (int i=0; i<childs.length; i++) {
						XMLElement child = childs[i];
						if (child.getName().equalsIgnoreCase(SET_SCOPE)) {
							initializerConf.setScopeId(child.getAttribute(ID));
							initializerConf.setScopeType(child.getAttribute(TYPE));
						} else if (child.getName().equalsIgnoreCase(SET_NODE)) {
							initializerConf.setNodeId(child.getAttribute(ID));
							initializerConf.setNodeType(child.getAttribute(TYPE));
						}
					}
				}
				return initializerConf;
			}
		}
		return null;
	}

}
