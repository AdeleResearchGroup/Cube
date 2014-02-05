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


package fr.liglab.adele.cube.autonomicmanager.impl;

import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.Configuration;
import fr.liglab.adele.cube.autonomicmanager.*;
import fr.liglab.adele.cube.autonomicmanager.comm.CommunicatorImpl;
import fr.liglab.adele.cube.autonomicmanager.life.LifeControllerImpl;
import fr.liglab.adele.cube.autonomicmanager.me.MonitorExecutorImpl;
import fr.liglab.adele.cube.autonomicmanager.resolver.ArchetypeResolverImpl;
import fr.liglab.adele.cube.autonomicmanager.rmc.RuntimeModelCheckerImpl;
import fr.liglab.adele.cube.extensions.*;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.ArchetypeException;
//import fr.liglab.adele.cube.autonomicmanager.life.LifeController;
import fr.liglab.adele.cube.autonomicmanager.rmc.RuntimeModelControllerImpl;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;
import fr.liglab.adele.cube.util.parser.ArchetypeParsingException;
import fr.liglab.adele.cube.util.parser.ParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 6:16 PM
 */
public class AutonomicManagerImpl implements AutonomicManager, Runnable {


    /**
     * Cube AM URI.
     */
    private String uri = null;

    /**
     * Associated Cube Administration Service.
     */
    private AdministrationService adminService;

    /**
     * Configuration.
     */
    private Configuration config;

    /**
     * Archetype.
     */
    private Archetype archetype;

    /**
     * Cube AM Extensions.
     */
    private List<Extension> extensions;

    private Properties properties = new Properties();

    /**
     * Runtime Model Controller.
     */
    private RuntimeModelController rmController;

    /**
     * Runtime Model Resolver.
     */
    private ArchetypeResolver resolver;

    /**
     * Life Controller.
     */
    //private LifeController lifeController;
    private ExternalInstancesHandler externalInstancesHandler;

    private RuntimeModelChecker checker;

    /**
     * Communicators
     */
    private Communicator communicator;

    private MonitorExecutor monitorExecutor;


    /**
     * Local Id
     */
    private String localId = "0";



    /**
     * key: uuid
     * value: agent_uri
     */
    //private Map<String , String> externalElements = new HashMap<String, String>();

    private static int index = 1;

    Thread t;
    private boolean working = false;
    private boolean destroyRequested = false;

    public void run() {

    }

    /**
     * Constructor
     *
     * @param admin
     * @param config
     * @throws fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException
     *
     */
    public AutonomicManagerImpl(AdministrationService admin, Configuration config) throws AutonomicManagerException {

        this.adminService = admin;
        this.config = config;
        this.localId = "" + index++;

        if (config == null)
            throw new AutonomicManagerException("Cube Autonomic Manager Configuration error!");

        String host = config.getHost();
        long port = config.getPort();
        this.uri = "cube://" + host + ":" + port;
        for (Object p : config.getProperties().keySet()) {
            //System.out.println(">>>>>>>> adding property to AM: "+p.toString()+"="+config.getHeaders().getProperty(p.toString()));
            addProperty(p.toString(), config.getProperties().getProperty(p.toString()));
        }

        t = new Thread(this);

        // archetype
        try {
            this.archetype = ArchetypeParser.parse(getAdministrationService(), new URL(this.config.getArchetypeUrl()));
            this.archetype.setAutonomicManager(this);
        } catch (ParseException e) {
            throw new AutonomicManagerException(e.getMessage());
        } catch (ArchetypeParsingException e) {
            throw new AutonomicManagerException(e.getMessage());
        } catch (ArchetypeException e) {
            throw new AutonomicManagerException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new AutonomicManagerException(e.getMessage());
        }

        // communicators
        this.communicator = new CommunicatorImpl(this);

        // Runtime Model Controller
        this.rmController = new RuntimeModelControllerImpl(this);

        // Life Controller
        //this.lifeController = new LifeController(this);
        externalInstancesHandler = new LifeControllerImpl(this);
        // __resolver
        this.resolver = new ArchetypeResolverImpl(this);

        // monitorExecutor
        this.monitorExecutor = new MonitorExecutorImpl(this);

        init();

        // communicator
        if (this.communicator != null) {

            try {
                this.communicator.addMessagesListener(this.getUri(), new MessagesListener() {
                    public void receiveMessage(CMessage msg) {
                        if (msg != null) {

                            if (msg.getObject() != null) {
                                if (msg.getObject().equalsIgnoreCase("resolution")) {
                                    AutonomicManagerImpl.this.resolver.receiveMessage(msg);
                                } else if (msg.getObject().equalsIgnoreCase("runtimemodel")) {
                                    AutonomicManagerImpl.this.rmController.receiveMessage(msg);
                                } else if (msg.getObject().equalsIgnoreCase("keepalive")) {
                                    AutonomicManagerImpl.this.externalInstancesHandler.keepAliveReceived(msg.getFrom());
                                }
                            }
                        } else {
                            //System.out.println("received NULL msg!");
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        checker = new RuntimeModelCheckerImpl(this);

        //lifeController = new LifeController(this);


    }

    private void init() throws AutonomicManagerException {

        // extensions
        this.extensions = new ArrayList<Extension>();
        for (ExtensionConfig ex : this.config.getExtensionConfigs()) {
            String id = ex.getId();
            if (id != null && id.length() > 0) {
                ExtensionFactoryService eb = getAdministrationService().getExtensionFactory(id);
                if (eb != null) {
                    Extension extension = eb.newExtensionInstance(this, ex.getProperties());
                    if (extension != null) {
                        addExtension(extension);
                    }
                }   else {
                    System.out.println("[WARNING] the extension '"+id+"' was not found in this OSGi Platform. Check that you have already deployed the adequate bundle!");
                }
            }
        }



    }

    public ExternalInstancesHandler getExternalInstancesHandler() {
        return externalInstancesHandler;
    }

    private void addExtension(Extension ex) {
        if (ex != null) {
            extensions.add(ex);
            if (ex.getExtensionPoints() == null || ex.getExtensionPoints().size() == 0) {
                System.out.println("[WARNING] Extension '"+ex.getExtensionFactory().getFullName()+"' does not provide any extension point!");
            } else {
                for (ExtensionPoint exp : ex.getExtensionPoints()) {
                    if (exp instanceof CommunicatorExtensionPoint) {
                        this.communicator.addSpecificCommunicator((CommunicatorExtensionPoint)exp);
                    }
                    else if (exp instanceof ResolverExtensionPoint) {
                        this.resolver.addSpecificResolver(((ResolverExtensionPoint) exp));
                    }
                    else if (exp instanceof MonitorExecutorExtensionPoint) {
                        this.monitorExecutor.addSpecificMonitorExecutor(((MonitorExecutorExtensionPoint) exp));
                    }
                    else if (exp instanceof ManagedElementExtensionPoint) {
                        this.rmController.addManagedElementFactory(((ManagedElementExtensionPoint) exp));
                    }
                }
            }
        } else {
            System.out.println("[WARNING] trying to add null exception!");
        }
    }

    public String getUri() {
        return this.uri;
    }

    /**
     * Gets the local id of the current Cube Agent
     *
     * @return
     */
    public String getLocalId() {
        return this.localId;
    }

    public AdministrationService getAdministrationService() {
        return this.adminService;
    }

    public Configuration getConfiguration() {
        return this.config;
    }

    /**
     * Gets the associated Archetype.
     *
     * @return
     */
    public Archetype getArchetype() {
        return this.archetype;
    }

    public void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public String getProperty(String name) {
        if (name != null) {
            if (this.properties.get(name) != null)
                return this.properties.get(name).toString();
        }
        return null;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Extension> getExtensions() {
        return this.extensions;
    }

    /**
     * Gets the plugin having the given id.
     *
     * @param namespace
     */
    public Extension getExtension(String namespace) {
        for (Extension e : getExtensions()) {
            if (e.getExtensionFactory().getNamespace().equalsIgnoreCase(namespace))
                return e;
        }
        return null;
    }

    /**
     * Gets the Cube Agent's CommunicatorExtensionPoint.
     *
     * @return
     */
    public Communicator getCommunicator() {
        return this.communicator;
    }

    /**
     * Gets the Runtime Model Controller.
     *
     * @return
     */
    public RuntimeModelController getRuntimeModelController() {
        return this.rmController;
    }



    public void start() {
        System.out.println("[INFO] >>>>>>>>> starting autonomic manager: " + uri.toString());
        this.working = true;
        for (Extension ex: this.getExtensions()) {
            ex.start();
        }

        if (this.checker != null) {
            this.checker.start();
        }

        if (this.externalInstancesHandler != null) {
            this.externalInstancesHandler.start();
        }

    }

    public void stop() {
        System.out.println("[INFO] >>>>>>>>> stopping autonomic manager: " + uri.toString());
        this.working = false;
        for (Extension ex: this.getExtensions()) {
            ex.stopping();
        }

        if (this.checker != null) {
            this.checker.stop();
        }

        if (this.externalInstancesHandler != null) {
            this.externalInstancesHandler.stop();
        }

    }

    public void destroy() {
        stop();
        System.out.println("[INFO] >>>>>>>>> destroying autonomic manager: " + uri.toString());
        this.working = false;
        this.destroyRequested = true;
        for (Extension ex: this.getExtensions()) {
            ex.destroying();
        }

        if (this.checker != null) {
            this.checker.destroy();
        }


        if (this.externalInstancesHandler != null) {
            this.externalInstancesHandler.destroy();
        }
    }

    public ArchetypeResolver getArchetypeResolver() {
        return this.resolver;
    }


}
