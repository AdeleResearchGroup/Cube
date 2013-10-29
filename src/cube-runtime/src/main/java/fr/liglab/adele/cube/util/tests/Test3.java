package fr.liglab.adele.cube.util.tests;

import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.Configuration;
import fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException;
import fr.liglab.adele.cube.autonomicmanager.NotFoundManagedElementException;
import fr.liglab.adele.cube.extensions.ExtensionConfig;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.PropertyExistException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: debbabi
 * Date: 9/28/13
 * Time: 11:19 AM
 */
public class Test3 {

    private static final int NBR_GRENOBLE_SERVERS = 3;
    private static final int NBR_GRENOBLE_GATWAYS = 15;
    private static final int NBR_PARIS_SERVERS = 5;
    private static final int NBR_PARIS_GATWAYS = 25;


    public static void test(AdministrationService administrationService, int param) {
        System.out.println("\n||||||||||||| TEST 3 UC1 ||||||||||||||||\n");
        Map<String, String> ams = new HashMap<String, String>();
        try {
            // MASTER
            {
                Configuration config = new Configuration();
                config.setArchetypeUrl("file:test.arch");
                config.setHost("localhost");
                config.setPort(19000);
                ExtensionConfig ext = new ExtensionConfig();
                ext.setId("fr.liglab.adele.cube.core");
                ext.addProperty("master", "true");
                config.addExtension(ext);
                ams.put("m", administrationService.createAutonomicManager(config));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n[INFO] Initializing National datacentre ...\n");
            // NATIONAL DATACENTER
            {
                Configuration config = new Configuration();
                config.setArchetypeUrl("file:test.arch");
                config.setHost("localhost");
                config.setPort(19001);
                ExtensionConfig ext = new ExtensionConfig();
                ext.setId("fr.liglab.adele.cube.core");
                config.addExtension(ext);
                ams.put("dc", administrationService.createAutonomicManager(config));
                String amUri = ams.get("dc");
                AutonomicManager am = administrationService.getAutonomicManager(amUri);
                if (am != null) {
                    Properties p = new Properties();
                    p.put(Node.CORE_NODE_TYPE, "Datacentre");
                    try {
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Node.NAME, p);
                    } catch (NotFoundManagedElementException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    } catch (PropertyExistException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n[INFO] Initializing Grenoble Servers...\n");
            // GRENOBLE SERVER
            for (int i=0; i<NBR_GRENOBLE_SERVERS; i++) {
                Configuration config = new Configuration();
                config.setArchetypeUrl("file:test.arch");
                config.setHost("localhost");
                config.setPort(19100+i);
                config.addProperty("city","Grenoble");
                config.addProperty("node.type","Server");
                ExtensionConfig ext1 = new ExtensionConfig();
                ext1.setId("fr.liglab.adele.cube.core");
                config.addExtension(ext1);
                ams.put("sg" + i, administrationService.createAutonomicManager(config));
                Properties p = new Properties();
                p.put(Node.CORE_NODE_TYPE, "Server");
                try {
                    String amUri = ams.get("sg"+i);
                    AutonomicManager am = administrationService.getAutonomicManager(amUri);
                    System.out.println("[INFO] Initializing Grenoble Server: "+amUri+"...");
                    am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Node.NAME, p);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (NotFoundManagedElementException e) {
                    e.printStackTrace();
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                }
            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n[INFO] Initializing Paris Servers...\n");
            // GRENOBLE SERVER
            for (int i=0; i<NBR_PARIS_SERVERS; i++) {
                Configuration config = new Configuration();
                config.setArchetypeUrl("file:test.arch");
                config.setHost("localhost");
                config.setPort(19200+i);
                config.addProperty("city","Paris");
                config.addProperty("node.type","Server");
                ExtensionConfig ext1 = new ExtensionConfig();
                ext1.setId("fr.liglab.adele.cube.core");
                config.addExtension(ext1);
                ams.put("sp" + i, administrationService.createAutonomicManager(config));
                Properties p = new Properties();
                p.put(Node.CORE_NODE_TYPE, "Server");
                try {
                    String amUri = ams.get("sp"+i);
                    AutonomicManager am = administrationService.getAutonomicManager(amUri);
                    System.out.println("[INFO] Initializing Paris Server: "+amUri+"...");
                    am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Node.NAME, p);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (NotFoundManagedElementException e) {
                    e.printStackTrace();
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                }
            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int index = param;
            int gg = NBR_GRENOBLE_GATWAYS>=param?param:NBR_GRENOBLE_GATWAYS;
            int pg = (param-NBR_GRENOBLE_GATWAYS)<0?0:param-NBR_GRENOBLE_GATWAYS;

            System.out.println("\n[INFO] Initializing Grenoble Gateways ...\n");
            // GRENOBLE GATEWAYS
            for (int i=0; i<gg; i++) {
                Configuration config = new Configuration();
                config.setArchetypeUrl("file:test.arch");
                config.setHost("localhost");
                config.setPort(19300 + i);
                //config.setDebug(true);
                config.addProperty("city","Grenoble");
                config.addProperty("node.type","Gateway");
                ExtensionConfig ext1 = new ExtensionConfig();
                ext1.setId("fr.liglab.adele.cube.core");
                config.addExtension(ext1);
                ams.put("gg" + i, administrationService.createAutonomicManager(config));
                String amUri = ams.get("gg"+i);
                AutonomicManager am = administrationService.getAutonomicManager(amUri);
                if (am != null) {
                    Properties p = new Properties();
                    p.put(Node.CORE_NODE_TYPE, "Gateway");
                    try {
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Node.NAME, p);
                    } catch (NotFoundManagedElementException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    } catch (PropertyExistException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n[INFO] Initializing Paris Gateways ...\n");
            // GRENOBLE GATEWAYS
            for (int i=0; i<pg; i++) {
                Configuration config = new Configuration();
                config.setArchetypeUrl("file:test.arch");
                config.setHost("localhost");
                config.setPort(19400 + i);
                //config.setDebug(true);
                config.addProperty("city","Paris");
                config.addProperty("node.type","Gateway");
                ExtensionConfig ext1 = new ExtensionConfig();
                ext1.setId("fr.liglab.adele.cube.core");
                config.addExtension(ext1);
                ams.put("gp" + i, administrationService.createAutonomicManager(config));
                String amUri = ams.get("gp"+i);
                AutonomicManager am = administrationService.getAutonomicManager(amUri);
                if (am != null) {
                    Properties p = new Properties();
                    p.put(Node.CORE_NODE_TYPE, "Gateway");
                    try {
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Node.NAME, p);
                    } catch (NotFoundManagedElementException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    } catch (PropertyExistException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n[INFO] Initializing Grenoble Gateway Mediators...\n");

            // create mediators of grenoble gateways
            for (int i=0; i<gg; i++) {
                String amUri = ams.get("gg"+i);
                AutonomicManager am = administrationService.getAutonomicManager(amUri);
                if (am != null) {
                    try {
                        Properties p1 = new Properties();
                        p1.put(Component.CORE_COMPONENT_TYPE, "GP");
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Component.NAME, p1);
                        Properties p2 = new Properties();
                        p2.put(Component.CORE_COMPONENT_TYPE, "EP");
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Component.NAME, p2);
                        Properties p3 = new Properties();
                        p3.put(Component.CORE_COMPONENT_TYPE, "WP");
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Component.NAME, p3);
                    } catch (NotFoundManagedElementException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    } catch (PropertyExistException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n[INFO] Initializing Paris Gateway Mediators...\n");

            // create mediators of grenoble gateways
            for (int i=0; i<pg; i++) {
                String amUri = ams.get("gp"+i);
                AutonomicManager am = administrationService.getAutonomicManager(amUri);
                if (am != null) {
                    try {
                        Properties p1 = new Properties();
                        p1.put(Component.CORE_COMPONENT_TYPE, "GP");
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Component.NAME, p1);
                        Properties p2 = new Properties();
                        p2.put(Component.CORE_COMPONENT_TYPE, "EP");
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Component.NAME, p2);
                        Properties p3 = new Properties();
                        p3.put(Component.CORE_COMPONENT_TYPE, "WP");
                        am.getRuntimeModelController().newManagedElement(CoreExtensionFactory.NAMESPACE, Component.NAME, p3);
                    } catch (NotFoundManagedElementException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    } catch (PropertyExistException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        } catch (AutonomicManagerException e) {
            e.printStackTrace();
        }

    }
}
