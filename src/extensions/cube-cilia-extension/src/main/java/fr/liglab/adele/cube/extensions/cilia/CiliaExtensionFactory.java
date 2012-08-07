package fr.liglab.adele.cube.extensions.cilia;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.IExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.parser.AgentConfigParserPlugin;
import fr.liglab.adele.cube.util.parser.ArchetypeParserPlugin;

@Component
@Provides
@Instantiate
public class CiliaExtensionFactory implements IExtensionFactory {

	public static final String ID = "fr.liglab.adele.cube.cilia";
	public static final String VERSION = CubePlatform.CUBE_VERSION;
	
	public String getExtensionId() {		
		return ID;
	}

	public String getExtensionVersion() {
		return VERSION;
	}

	public ArchetypeParserPlugin getArchetypeParserPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public AgentConfigParserPlugin getAgentConfigParserPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtension newExtension(CubeAgent agent, AgentExtensionConfig config) {		
		return new CiliaExtension(agent, this, config);
	}

}
