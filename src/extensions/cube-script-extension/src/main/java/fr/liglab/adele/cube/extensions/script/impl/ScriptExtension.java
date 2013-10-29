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


package fr.liglab.adele.cube.extensions.script.impl;


import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.NotFoundManagedElementException;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.ExtensionFactoryService;
import fr.liglab.adele.cube.extensions.ExtensionPoint;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.Master;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 7:23 PM
 */
public class ScriptExtension extends AbstractExtension {

    public ScriptExtension(AutonomicManager am, ExtensionFactoryService factory, Properties properties) {
        super(am, factory, properties);

    }

    @Override
    public List<ExtensionPoint> getExtensionPoints() {
        List<ExtensionPoint> extensionPointsList = new ArrayList<ExtensionPoint>();

        //extensionPointsList.add(guimonitor);

        return extensionPointsList;
    }

    public void start() {
        System.out.println("[INFO] Starting script extension..");
        Properties p = getProperties();
        for (Object s : p.keySet()) {
            //System.out.println(">>>>> "+p.getProperty(s.toString()));
            String script = p.getProperty(s.toString());
            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  */
            execute(script);
        }
    }

    private void execute(String command) {
        if (command != null) {
            String[] splitted = command.split(" ");
            if (splitted != null) {
                System.out.println("[INFO] script extension: executing command "+command+"..." );
                if (splitted.length == 1) {
                    System.out.println("[WARNING] script extension: executing command not yet implemented!");
                } else if (splitted.length == 2) {
                    String com = splitted[0];
                    String type = splitted[1];
                    String properties = splitted[2];
                    String typens = CoreExtensionFactory.NAMESPACE;
                    String typename = type;
                    if (type.contains(":")) {
                        String[] tmp = type.split(":");
                        if (tmp != null && tmp.length == 2) {
                            typens = tmp[0];
                            typename = tmp[1];
                        }
                    }
                    if (com.equalsIgnoreCase("newi")) {
                        ManagedElement me = null;
                        try {
                            me = getAutonomicManager().getRuntimeModelController().newManagedElement(typens, typename, null);
                        } catch (InvalidNameException e) {
                            e.printStackTrace();
                        } catch (PropertyExistException e) {
                            e.printStackTrace();
                        } catch (NotFoundManagedElementException e) {
                            System.out.println(e.getMessage());
                        }
                        if (me != null) {
                            getAutonomicManager().getRuntimeModelController().getRuntimeModel().refresh();
                        }
                    }  else {
                        System.out.println("[INFO] script extension: unknown command " + com+"!");
                    }
                } else if (splitted.length == 3) {
                    String com = splitted[0];
                    String type = splitted[1];
                    String properties = splitted[2];
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
                        if (com.equalsIgnoreCase("newi")) {
                            ManagedElement me = null;
                            try {
                                me = getAutonomicManager().getRuntimeModelController().newManagedElement(typens, typename, p);
                            } catch (InvalidNameException e) {
                                e.printStackTrace();
                            } catch (PropertyExistException e) {
                                e.printStackTrace();
                            } catch (NotFoundManagedElementException e) {
                                System.out.println(e.getMessage());
                            }

                            if (me != null) {
                                getAutonomicManager().getRuntimeModelController().getRuntimeModel().refresh();
                            }
                        }   else {
                            System.out.println("[INFO] script extension: unknown command " + com+"!");
                        }
                    }
                } else {
                    System.out.println("[WARNING] script extension: executing command not yet implemented!");
                }
            }
        }


    }

    public void stop() {
        System.out.println("[INFO] Stopping script extension..");
    }

    public void destroy() {

    }

}
