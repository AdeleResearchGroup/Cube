/*
 * Copyright 2011-2012 Adele Research Group (http://adele.imag.fr/) 
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

package fr.liglab.adele.cube.tools.gogocommands;

import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Descriptor;

import fr.liglab.adele.cube.ICubePlatform;
import fr.liglab.adele.cube.TypeNotDeclaredException;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.extensions.IExtension;
import fr.liglab.adele.cube.util.id.CubeAgentID;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;

/**
 * OSGi Gogo commands.
 * This help to manipulate the Cube Platform using the OSGi gogo commands.
 * 
 * @author debbabi
 *
 */
@Component(public_factory = true, immediate = true)
@Instantiate
@Provides(specifications = PlatformGogoCommands.class)
public class PlatformGogoCommands {

	//TODO just for test!
	CubeAgentID currentCid;
	
	@Requires
	ICubePlatform cps;
	
	@ServiceProperty(name = "osgi.command.scope", value = "cube")
	String m_scope;

	@ServiceProperty(name = "osgi.command.function", value = "{}")
	String[] m_function = new String[] { "version", "arch", "rm", "newi", "extensions", "extension" /*, "cubes", "ctr", "sn","si","srti", "sm", "load", "stop", "destroy", "ex"*/};

	@Descriptor("Show Cube Platform Version")
	public void version() {	
		
		System.out.println("\nCube Platform version: " + this.cps.getVersion() +"\n");
		
	}
	
	@Descriptor("Show archtype")
	public void arch() {	
		for (String id : this.cps.getCubeAgents()) {
			CubeAgent ci = cps.getCubeAgent(id);
			if (ci != null) {
				System.out.println("--------------------------------------------------------------------------");
				System.out.println(ArchetypeParser.toXmlString(ci.getArchetype()));
				System.out.println("--------------------------------------------------------------------------");
			}
		}
	}
	
	@Descriptor("Shows the internal model at runtime of the given Cube Instance")
	public void rm() {
		for (String id : this.cps.getCubeAgents()) {
			CubeAgent ci = cps.getCubeAgent(id);
			if (ci != null) {
				System.out.println("--------------------------------------------------------------------------");
				
				Map<String, CInstance> coinstances = ci.getRuntimeModel().getCInstances();
				String out = "";
				for (String key : coinstances.keySet()) {
					out += "\n- " + key + " " + "(" + coinstances.get(key).getCType().getNamespace() + "." + coinstances.get(key).getCType().getName() + ":" + coinstances.get(key).getCType().getId() + ") [" + coinstances.get(key).getStateAsString() + "]\n";
					out += coinstances.get(key);
				}
				System.out.println(out);				
				
				System.out.println("--------------------------------------------------------------------------");
			}
		}	
	}
	
	@Descriptor("Create a new CObjectInstance")
	public void newi(@Descriptor("Type id") String tid) {	
		for (String id : this.cps.getCubeAgents()) {
			CubeAgent ci = cps.getCubeAgent(id);
			if (ci != null) {								
				ManagedElement t = ci.getArchetype().getType(tid);
				if (t != null) {
					CInstance instance;
					try {
						instance = t.newInstance(ci);
						ci.getRuntimeModel().addAndValidate(instance);
					} catch (TypeNotDeclaredException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}			
				} else {
					System.out.println("Type " + tid + " not specified on the Archetype!");
				}
			}
		}			
	}
	
	@Descriptor("Show the list of Extensions")
	public void extensions() {
		for (String id : this.cps.getCubeAgents()) {
			CubeAgent ci = cps.getCubeAgent(id);
			if (ci != null) {
				System.out.println("--------------------------------------------------------------------------");				
				for (IExtension ex : ci.getExtensions()) {											
					System.out.println("["+ex.getLocalId()+"] " + ex.getExtensionFactory().getExtensionId() + ":" + ex.getExtensionFactory().getExtensionVersion());					
				}
				System.out.println("--------------------------------------------------------------------------");
			}
		}
	}
	
	@Descriptor("Show Extension description")
	public void extension(@Descriptor("extension local id") String extension) {		
		for (String id : this.cps.getCubeAgents()) {
			CubeAgent ci = cps.getCubeAgent(id);
			if (ci != null) {
				System.out.println("--------------------------------------------------------------------------");				
				IExtension ex = ci.getExtensionByLocalID(extension);
				System.out.println(ex.toString());								
				System.out.println("--------------------------------------------------------------------------");
			}
		}
		
	}
	
	/*
	@Descriptor("Shows the internal model at runtime of the given Cube Instance")
	public void rm(@Descriptor("Cube instance ID") String cubeID) {		
		CubeAgent ci = this.cps.getCubeAgent(cubeID);
		if (ci != null) {
			System.out.println("........... M@R of " + ci.getId());
			if (ci != null) {
				Map<String, CInstance> coinstances = ci.getRuntimeModel().getCInstances();
				String out = "";
				for (String key : coinstances.keySet()) {
					out += "\n- " + key + " " + "(" + coinstances.get(key).getCType().getNamespace() + "." + coinstances.get(key).getCType().getName() + ":" + coinstances.get(key).getCType().getId() + ") [" + coinstances.get(key).getStateAsString() + "]\n";
					out += coinstances.get(key);
				}
				System.out.println(out);
			}
		} else {
			System.out.println("Unkown Cube Instance " + cubeID);
		}	
	}
		
	@Descriptor("Display running Cube Instances")
	public void cubes() {
		System.out.println("... CUBE INSTANCES ....");
		if (cps != null) {
			List<CubeAgentID> cids = cps.getCubeAgents();
			if (cids != null) {
				for (CubeAgentID id : cids) {
					System.out.println("* " + id);		
				}
			}		
		}
	}

	@Descriptor("Display the controllers")
	public void ctr() {
		System.out.println("**************** CONTROLLERS ****************");
		if (cps != null) {
			if (this.cps.getCubeAgents().size()>0) {
				CubeAgent ci = this.cps.getCubeAgent(this.cps.getCubeAgents().get(0).toString());
			if (ci != null) {
					System.out.println("/// " + ci.getId().toString() + "");		
					List<IExtension> controllers = ci.getControllers();
					System.out.println(".............................................");
					for (IExtension c : controllers) {
						System.out.println(c.toString());
						System.out.println(".............................................");
					}
					
				}
			}		
		}
		//System.out.println("*********************************************");
	}
	
	@Descriptor("Display scope nodes")
	public void sn(@Descriptor("type") String type, @Descriptor("localid") String localid) {
		System.out.println("**************** SCOPE NODES ****************");
		if (cps != null) {
			if (this.cps.getCubeAgents().size()>0) {
				CubeAgent ci = this.cps.getCubeAgent(this.cps.getCubeAgents().get(0).toString());
			if (ci != null) {
					System.out.println("/// " + localid + ":" + type);
					
					IExtension c = ci.getController(CoreController.NAMESPACE);
					if (c != null) {
						List<String> nodes = ((CoreController)c).getScopeNodes(type, localid);
						if (nodes != null && nodes.size()>0) {
							for (String n : nodes) {
								System.out.println("    + " + n);
							}
						}
					}								
				}
			}
		}
		//System.out.println("*********************************************");
	}
	
	@Descriptor("Display scope instances")
	public void si(@Descriptor("type") String type) {
		System.out.println("**************** SCOPE INSTANCES ****************");
		if (cps != null) {
			if (this.cps.getCubeAgents().size()>0) {
				CubeAgent ci = this.cps.getCubeAgent(this.cps.getCubeAgents().get(0).toString());
			if (ci != null) {
					System.out.println("/// " + type);
					
					IExtension c = ci.getController(CoreController.NAMESPACE);
					if (c != null) {
						List<String> sinsts = ((CoreController)c).getScopeInstances(type);
						if (sinsts != null && sinsts.size()>0) {
							for (String n : sinsts) {
								System.out.println("    + " + n);
							}
						}
					}								
				}
			}
		}
		//System.out.println("*********************************************");
	}
	
	@Descriptor("Display scope runtime instances")
	public void srti(@Descriptor("type") String type, @Descriptor("localid") String localid) {
		System.out.println("**************** SCOPE RUNTIME INSTANCES ****************");
		if (cps != null) {
			if (this.cps.getCubeAgents().size()>0) {
				CubeAgent ci = this.cps.getCubeAgent(this.cps.getCubeAgents().get(0).toString());
			if (ci != null) {
					System.out.println("/// " + type);
					
					IExtension c = ci.getController(CoreController.NAMESPACE);
					if (c != null) {
						List<String> srtinsts = ((CoreController)c).getScopeRuntimeInstances(type, localid);
						if (srtinsts != null && srtinsts.size()>0) {
							for (String n : srtinsts) {
								System.out.println("    + " + n);
							}
						}
					}								
				}
			}
		}
		//System.out.println("*********************************************");
	}
	
	
	
	@Descriptor("Load and create a new Cube Instance for the given archtype.")
	public void load(@Descriptor("archtype url") String archtypeUrl, @Descriptor("config url") String configUrl) {
		System.out.println("........... Create new Cube Instance ....");
		if (cps != null) {
			currentCid = cps.createCubeAgent(archtypeUrl, configUrl);
		}
	}
	
	@Descriptor("Load and create a new Cube Instance for the given archtype.")
	public void load(@Descriptor("archtype url") String archtypeUrl, @Descriptor("host") String host, @Descriptor("port")  String port) {
		System.out.println("........... Create new Cube Instance ....");
		if (cps != null) {
			currentCid = cps.createCubeAgent(archtypeUrl, host, new Integer(port).intValue());
		}
	}

	@Descriptor("Load and create a new Cube Instance for the given archtype.")
	public void load() {
		System.out.println("........... Create new Cube Instance ....");
		if (cps != null) {
			currentCid = cps.createCubeAgent("file:../demo.cube");
		}
	}

	@Descriptor("Load and create a new Cube Instance for the given archtype.")
	public void load(@Descriptor("archtype url") String archtypeUrl) {
		System.out.println("........... Create new Cube Instance ....");
		if (cps != null) {
			currentCid = cps.createCubeAgent(archtypeUrl);
		}
	}
	
	@Descriptor("Stop a Cube Instance")
	public void stop(@Descriptor("Cube Instance ID") String cubeid) {
		System.out.println("........... stopping " + cubeid);		
	}
	
	@Descriptor("Destroying a Cube Instance")
	public void destroy(@Descriptor("Cube Instance ID") String cubeid) {
		System.out.println("........... destroying " + cubeid);
	}

	@Descriptor("Create a new CObjectInstance")
	public void newi(@Descriptor("Cube instance ID") String cubeID, @Descriptor("Type id") String tid) {	
		System.out.println("..................................................................");
		if (cubeID.equalsIgnoreCase("*")) {			
			for (CubeAgentID id : this.cps.getCubeAgents()) {
				createCObjectInstance(id.getURI().toString(), tid, null);	
			}
		} else {
			createCObjectInstance(cubeID, tid, null);
		}
		System.out.println("..................................................................");
	}
	
	@Descriptor("Create a new CObjectInstance")
	public void newi(@Descriptor("Cube instance ID") String cubeID, @Descriptor("Type id") String tid, @Descriptor("Properties") String props) {				
		System.out.println("..................................................................");
		if (cubeID.equalsIgnoreCase("*")) {			
			for (CubeAgentID id : this.cps.getCubeAgents()) {
				createCObjectInstance(id.getURI().toString(), tid, props);	
			}
		} else {
			createCObjectInstance(cubeID, tid, props);
		}
		System.out.println("..................................................................");
	}
	
	@Descriptor("Show scope instances")
	public void si(@Descriptor("Cube Instance ID") String cubeID, @Descriptor("Type Name") String type) {	
		if (cubeID.equalsIgnoreCase("*")) {			
			for (CubeAgentID id : this.cps.getCubeAgents()) {
				showScopeInstances(id.toString(), type);	
			}
		} else {
			showScopeInstances(cubeID, type);
		}
	}
	
	private void showScopeInstances(String cubeID, String type) {

	}
	
	@Descriptor("Show scope members")
	public void sm(@Descriptor("Cube Instance ID") String cubeID, @Descriptor("Type Name") String type, @Descriptor("Local Id") String localID) {	
		if (cubeID.equalsIgnoreCase("*")) {			
			for (CubeAgentID id : this.cps.getCubeAgents()) {
				showScopeMemebers(id.toString(), type, localID);	
			}
		} else {
			showScopeMemebers(cubeID, type, localID);
		}
	}
	
			
	private void showScopeMemebers(String cubeID, String type, String localID) {

	}
	
	@Descriptor("Show scope nodes")
	public void sn(@Descriptor("Cube Instance ID") String cubeID, @Descriptor("Type Name") String type, @Descriptor("Local Id") String localID) {	
		if (cubeID.equalsIgnoreCase("*")) {			
			for (CubeAgentID id : this.cps.getCubeAgents()) {
				showScopeNodes(id.toString(), type, localID);	
			}
		} else {
			showScopeNodes(cubeID, type, localID);
		}
	}
	
	private void showScopeNodes(String cubeID, String type, String localID) {

	}
	
	@Descriptor("Show archtype")
	public void arch(@Descriptor("Cube Instance ID") String cubeID) {	
		if (cubeID.equalsIgnoreCase("*")) {			
			for (CubeAgentID id : this.cps.getCubeAgents()) {
				CubeAgent ci = cps.getCubeAgent(id.toString());
				if (ci != null) {
					System.out.println("\nArchtype of: " + cubeID + "\n");
					if (ci.getArchtype() != null) {
						System.out.println("......................................................");
						System.out.println(ci.getArchtype().toXMLString());
						System.out.println("......................................................");
					}
				}
			}
		} else {
			CubeAgent ci = cps.getCubeAgent(cubeID);
			if (ci != null) {
				System.out.println("\nArchtype of: " + cubeID + "\n");
				//System.out.println(ci.getArchtype().toXMLString());
			}
		}
	}
	
	 */

	
	
}
