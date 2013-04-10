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


package fr.liglab.adele.cube.extensions.osgi;

import fr.liglab.adele.cube.CMessage;
import fr.liglab.adele.cube.TypeNotDeclaredException;

import fr.liglab.adele.cube.archetype.ManagedElement;
import org.osgi.framework.BundleContext;
import fr.liglab.adele.cube.agent.AgentExtensionConfig;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.*;


/**
 * Cube Internal Monitoring Extension.
 * 
 * @author debbabi
 *
 */
public class OSGiExtension extends AbstractExtension {


    private static final String CUBE_NODE_TYPE = "cube.node.type";
    private static final String CUBE_NODE_ID = "cube.node.id";
    private static final String CUBE_SCOPE_TYPE = "cube.scope.type";
    private static final String CUBE_SCOPE_ID = "cube.scope.id";

    public OSGiExtension(CubeAgent agent,
			IExtensionFactory factory, AgentExtensionConfig config) throws Exception{
		super(agent, factory, config);		
	}
	
	@Override
	public void start() {
        //System.out.println("Starting OSGiGateway Extension...");
        /*
        Properties pp = System.getProperties();
        for (Object key : pp.keySet()) {
            System.out.println(key.toString() + "=" + System.getProperty(key.toString()));
        }
         */
        BundleContext btx = getCubeAgent().getCubePlatform().getBundleContext();
        String node_type = btx.getProperty(CUBE_NODE_TYPE);
        String node_id = btx.getProperty(CUBE_NODE_ID);
        String scope_type = btx.getProperty(CUBE_SCOPE_TYPE);
        String scope_id = btx.getProperty(CUBE_SCOPE_ID);

        System.out.println("nt:"+node_type+" ni:"+node_id+" st:"+scope_type+" si:"+scope_id);

        CubeAgent ci = getCubeAgent();
        CInstance scope_instance = null;
        CInstance node_instance = null;
        if (ci != null) {
            // creating the scope
            if (scope_type != null && scope_type.trim().length() > 0) {
                ManagedElement t = ci.getArchetype().getType(scope_type);
                if (t != null) {
                    try {
                    scope_instance = t.newInstance();
                        if (scope_id != null) {
                            scope_instance.setLocalId(scope_id);
                        }
                    ci.getRuntimeModel().addAndValidate(scope_instance);
                    } catch (TypeNotDeclaredException ex) {
                           getCubeAgent().getLogger().error(ex.getMessage());
                    } catch (Exception ex2) {
                        getCubeAgent().getLogger().error(ex2.getMessage());
                    }

                }
            }
            if (node_type != null && node_type.trim().length() > 0) {
                ManagedElement t = ci.getArchetype().getType(node_type);
                if (t != null) {
                    try {
                        node_instance = t.newInstance();
                        if (node_id != null) {
                            node_instance.setLocalId(node_id);
                        }
                        ci.getRuntimeModel().addAndValidate(node_instance);
                    } catch (TypeNotDeclaredException ex) {
                        getCubeAgent().getLogger().error(ex.getMessage());
                    } catch (Exception ex2) {
                        getCubeAgent().getLogger().error(ex2.getMessage());
                    }
                    // Note: the node is automatically added to the already available scope in the local runtime model.
                }
            }

        }
	}

    @Override
    protected void handleMessage(CMessage msg) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stateChanged(CInstance coi, int oldState, int newState) {
		// TODO Auto-generated method stub
		
	}

	public void validatedInstance(CInstance coi) {
		if (coi != null) {			
			if (coi.getCType().getNamespace().equalsIgnoreCase(CoreExtensionFactory.ID) && coi.getCType().getName().equalsIgnoreCase(Node.NAME)) {

			}
		}
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
