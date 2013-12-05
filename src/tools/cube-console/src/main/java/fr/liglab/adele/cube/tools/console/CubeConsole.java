/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
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


package fr.liglab.adele.cube.tools.console;


import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.NotFoundManagedElementException;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.metamodel.PropertyNotExistException;
import fr.liglab.adele.cube.util.tests.Test;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.service.command.Descriptor;

import java.util.List;
import java.util.Properties;


/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:56 PM
 */
@Component(public_factory = true, immediate = true)
@Provides(specifications = {CubeConsole.class})
@Instantiate
public class CubeConsole {

    @Requires
    AdministrationService cps;

    @ServiceProperty(name = "osgi.command.scope", value = "cube")
    String m_scope;

    @ServiceProperty(name = "osgi.command.function", value = "{}")
    String[] m_function = new String[]{"version", "ams", "arch", "rm" , "newi" , "extensions", "update", "rmi", "delete", "links","test" /*, "extension"*/};


    @Descriptor("Test Cube")
    public void test(@Descriptor("Test id") String testNumber) {
        if (testNumber == null) {
            System.out.println("You should provide the test1 number!");
            return;
        }

        Test t1 = new Test(cps, new Integer(testNumber));

    }

    @Descriptor("Test Cube")
    public void test(@Descriptor("Test id") String testNumber, @Descriptor("Test param") String param) {
        if (testNumber == null) {
            System.out.println("You should provide the test1 number!");
            return;
        }

        Test t1 = new Test(cps, new Integer(testNumber), new Integer(param));

    }

    @Descriptor("Show Cube Platform Version")
    public void version() {
        String msg = "--------------------------------------------------------------------------";
        msg += "\nCube Runtime - version: " + this.cps.getVersion();
        msg += "\n--------------------------------------------------------------------------";
        System.out.println(msg);

    }

    @Descriptor("Show created Cube Agents")
    public void ams() {
        String msg = "--------------------------------------------------------------------------";
        for (String uri : this.cps.getAutonomicManagers()) {
            AutonomicManager ci = cps.getAutonomicManager(uri);
            msg += "\n[" + ci.getLocalId() + "] " + ci.getUri();

        }
        msg += "\n--------------------------------------------------------------------------";
        System.out.println(msg);

    }

    @Descriptor("Show archtype")
    public void arch(@Descriptor("Agent local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Autonomic Manager you want to see its archetype.\nType 'cube:ams' to see the list of existing Cube Agents in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube AM in this runtime.");
        } else {
            String msg = "--------------------------------------------------------------------------";
            if (agent.getArchetype() != null) {
                msg += "\n" + ArchetypeParser.toXmlString(agent.getArchetype());
            } else {
                msg += "\n... No archetype!";
            }
            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Shows the internal model at runtime of the given Cube Agent")
    public void rm(@Descriptor("Agent local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Autonomic Manager you want to see its Runtime Model.\nType 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Manager in this runtime.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            msg += "\n------ UNMANAGED ----";
            for (ManagedElement e : agent.getRuntimeModelController().getRuntimeModel().getManagedElements(ManagedElement.UNMANAGED)) {
                msg += e.getDocumentation();
            }
            msg += "\n" + "------ INVALID ----";
            for (ManagedElement e : agent.getRuntimeModelController().getRuntimeModel().getManagedElements(ManagedElement.INVALID)) {
                msg += e.getDocumentation();
            }
            msg += "\n" + "------ VALID --------";
            for (ManagedElement e : agent.getRuntimeModelController().getRuntimeModel().getManagedElements(ManagedElement.VALID)) {
                msg += e.getDocumentation();
            }

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Show archtype")
    public void newi(@Descriptor("Agent local id") String aid,
                     @Descriptor("ElementDescription type") String type) {
        if (aid == null) {
            System.out.println("You should provide which Cube Agent you want to see its archetype.\nType 'cube:agents' to see the list of existing Cube Agents in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            if (type != null) {
                String typens = CoreExtensionFactory.NAMESPACE;
                String typename = type;
                if (type.contains(":")) {
                    String[] tmp = type.split(":");
                    if (tmp != null && tmp.length == 2) {
                        typens = tmp[0];
                        typename = tmp[1];
                    }
                }
                ManagedElement me = null;
                try {
                    me = agent.getRuntimeModelController().newManagedElement(typens, typename, null);
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                } catch (NotFoundManagedElementException e) {
                    System.out.println(e.getMessage());
                }
                if (me != null) {
                    //agent.getRuntimeModelController().getRuntimeModel().add(me);
                    msg += "\n... instance created: " + me.getUri();
                    agent.getRuntimeModelController().getRuntimeModel().refresh();
                }
            } else {
                msg += "\n... error!";
            }

            msg += "\n--------------------------------------------------------------------------";
            //System.out.println(msg);
        }
    }

    @Descriptor("New instance")
    public void newi(@Descriptor("Agent local id") String aid,
                     @Descriptor("ElementDescription type") String type,
                     @Descriptor("ElementDescription properties") String properties) {
        if (aid == null) {
            System.out.println("You should provide which Cube Agent you want to see its archetype.\nType 'cube:agents' to see the list of existing Cube Agents in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            if (type != null) {
                String typens = CoreExtensionFactory.NAMESPACE;
                String typename = type;
                if (type.contains(":")) {
                    String[] tmp = type.split(":");
                    if (tmp != null && tmp.length == 2) {
                        typens = tmp[0];
                        typename = tmp[1];
                    }
                }
                Properties p = new Properties();

                String[] tmp = properties.split(",");
                if (tmp != null && tmp.length > 0) {
                    for (int i =0; i<tmp.length; i++) {
                        String[] prop = tmp[i].split("=");
                        if (prop != null && prop.length == 2) {
                            p.put(prop[0], prop[1]);
                        }
                    }
                }

                ManagedElement me = null;
                try {
                    me = agent.getRuntimeModelController().newManagedElement(typens, typename, p);
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                } catch (NotFoundManagedElementException e) {
                    System.out.println(e.getMessage());
                }

                if (me != null) {
                    //agent.getRuntimeModelController().getRuntimeModel().add(me);
                    msg += "\n... instance created: " + me.getUri();
                    agent.getRuntimeModelController().getRuntimeModel().refresh();
                }
            } else {
                msg += "\n... error!";
            }

            msg += "\n--------------------------------------------------------------------------";
            //System.out.println(msg);
        }
    }

    @Descriptor("Show archtype")
    public void rmi(@Descriptor("Agent local id") String aid, @Descriptor("instance uuid") String uuid) {
        if (aid == null) {
            System.out.println("You should specify in which Cube Autonomic Manager you want to execute your command!");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
        } else {

            String msg = "--------------------------------------------------------------------------";

            boolean result = agent.getRuntimeModelController().destroyElement(uuid);
            if (result == true)
                msg += "\n... Done!";
            else
                msg += "\n... No instance was found to be removed!";

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Shows the internal extensions of the given Cube Agent")
    public void extensions(@Descriptor("Agent local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Autonomic Manager you want to see its Runtime Model.\nType 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
        } else {

            String msg = "--------------------------------------------------------------------------";

            for (Extension p : agent.getExtensions()) {
                String ns = p.getExtensionFactory().getNamespace();
                String n = p.getExtensionFactory().getName();
                msg += ("\n" +ns + ":" + n);
            }

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Update instance property")
    public void update(@Descriptor("Agent local id") String aid,
                     @Descriptor("ElementDescription instance") String instance_uuid,
                     @Descriptor("ElementDescription properties") String properties) {
        if (aid == null) {
            System.out.println("You should provide which Cube Autonomic Manager you want to see its archetype.\nType 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            if (instance_uuid != null) {

                String[] tmp = properties.split("=");
                String pname = null;
                String pvalue=null;
                if (tmp != null && tmp.length > 1) {
                    pname = tmp[0];
                    pvalue = tmp[1];
                }
                if (pname == null || pvalue == null) {
                    System.out.println("Properties error!");
                    return;
                }

                ManagedElement me = agent.getRuntimeModelController().getLocalElement(instance_uuid);
                if (me == null) {
                    System.out.println("The instance identified by '"+instance_uuid+"' does not exist in the autonomicmanager '"+aid+"'!");
                    return;
                }

                try {
                    String old = agent.getRuntimeModelController().updateAttribute(instance_uuid, pname, pvalue);
                    if (old != null && !old.equalsIgnoreCase(pvalue)) {
                        (agent.getRuntimeModelController().getRuntimeModel()).refresh();
                    }
                } catch (PropertyNotExistException e) {
                    e.printStackTrace();
                }

            } else {
                msg += "\n... error!";
            }

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("delete Autonomic Manager")
    public void delete(@Descriptor("AM local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Autonomic Managed you want to remove.\nType 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
        } else {
            String msg = "--------------------------------------------------------------------------";
            cps.destroyAutonomicManager(agent.getUri());
            msg +="\n  Deleted!";
            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Show Autonomic Manager external links")
    public void links(@Descriptor("AM local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Autonomic Manager you want to see its external links with other Autonomic Managers.\nType 'cube:ams' to see the list of existing Cube Autonomic Managers in this runtime.");
            return;
        }
        AutonomicManager agent = cps.getAutonomicManagerByLocalId(aid);
        if (agent == null) {
            System.out.println("Autonomic Manager '"+aid+"' does not exist! Type 'cube:ams' to see the list of existing Cube AM in this runtime.");
        } else {

            String msg = "External Links of AM: "+agent.getUri()+"\n--------------------------------------------------------------------------";

            List<String> ams = agent.getExternalInstancesHandler().getExternalAutonomicManagers();
            for (String a : ams) {
                msg += "\n-- " + a;
            }
            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }
}
