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


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.*;
import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.ArchetypeException;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;
import fr.liglab.adele.cube.plugins.Plugin;
import fr.liglab.adele.cube.plugins.PluginFactory;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;
import fr.liglab.adele.cube.util.parser.ArchtypeParsingException;
import fr.liglab.adele.cube.util.parser.ParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 6:16 PM
 */
public class CubeAgentImpl implements CubeAgent {


    /**
     * Cube Agent URI.
     */
    private String uri = null;

    /**
     * Associated Cube Platform.
     */
    private CubePlatform platform;

    /**
     * Configuration.
     */
    private AgentConfig config;

    /**
     * Archetype.
     */
    private Archetype archetype;

    /**
     * Cube Agent Extensions.
     */
    private List<Plugin> plugins;

    /**
     * Communicator
     */
    private Communicator communicator;

    /**
     * Runtime Model.
     */
    private RuntimeModel runtimeModel;

    /**
     * Runtime Model Controller.
     */
    private RuntimeModelController rmController;

    /**
     * Runtime Model Resolver.
     */
    private Resolver resolver;

    /**
     * Life Controller.
     */
    private LifeController lifeController;

    /**
     * Runtime Model Checker.
     */
    private RuntimeModelChecker checker;

    /**
     * Local Id
     */
    private String localId = "0";

    /**
     * key: uuid
     * value: agent_uri
     */
    private Map<String , String> externalElements = new HashMap<String, String>();

    private static int index = 1;
    private Map<String, ManagedElement> unmanagedElements;


    /**
     * Constructor
     * @param cp
     * @param config
     * @throws CubeAgentException
     */
    public CubeAgentImpl(CubePlatform cp, AgentConfig config) throws CubeAgentException {

        this.platform = cp;

        this.config = config;

        this.localId = "" + index++;

        if (config == null)
            throw new CubeAgentException("Cube Agent's Configuration error!");

        String host = config.getHost();
        long port = config.getPort();
        this.uri = "cube://" + host + ":" + port;

        // archetype
        try {
            this.archetype = ArchetypeParser.parse(cp, new URL(this.config.getArchetypeUrl()));
        } catch (ParseException e) {
            throw new CubeAgentException(e.getMessage());
        } catch (ArchtypeParsingException e) {
            throw new CubeAgentException(e.getMessage());
        } catch (ArchetypeException e) {
            throw new CubeAgentException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new CubeAgentException(e.getMessage());
        }

        // communicator
        this.communicator = cp.getCommunicator(this.config.getCommunicatorName());
        if (this.communicator != null) {

            try {
                this.communicator.addMessagesListener(this.getUri(), new MessagesListener() {
                    public void receiveMessage(CMessage msg) {
                        if (msg != null) {

                            //System.out.println("received: " + msg.toString());

                            if (msg.getObject() != null) {
                                if (msg.getObject().equalsIgnoreCase("resolution")) {



                                    CubeAgentImpl.this.resolver.receiveMessage(msg);
                                } else if (msg.getObject().equalsIgnoreCase("runtimemodel")) {
                                    CubeAgentImpl.this.rmController.receiveMessage(msg);
                                } else if (msg.getObject().equalsIgnoreCase("keepalive")) {
                                    CubeAgentImpl.this.lifeController.keepAliveReceived(msg.getFrom());
                                }
                            }
                        } else {
                            System.out.println("received NULL msg!");
                        }
                    }
                });

            } catch (Exception e) {
                    e.printStackTrace();
            }
        }


        // Unmanaged elements
        this.unmanagedElements = new HashMap<String, ManagedElement>();

        // Runtime Model
        this.runtimeModel = new RuntimeModelImpl(this);

        // Runtime Model Controller
        this.rmController = new RuntimeModelControllerImpl(this);

        // Life Controller
        this.lifeController = new LifeControllerImpl(this);

        // resolver
        this.resolver = new ResolverImpl(this);

        // runtime model checker
        this.checker = new RuntimeModelChecker(this);

        // plugins
        this.plugins = new ArrayList<Plugin>();
        for (PluginConfig ex : this.config.getPlugins()) {
            String id = ex.getId();
            if (id != null && id.length() > 0) {
                PluginFactory eb = getPlatform().getPluginFactory(id);
                if (eb != null) {
                    Plugin plugin = eb.getPluginInstance(this, ex.getProperties());
                    if (plugin != null) {
                        plugins.add(plugin);
                    }
                }   else {
                    System.out.println("[WARNING] the plugin '"+id+"' was not found in this OSGi Platform. Check that you have already deployed the adequate bundle!");
                }
            }
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

    public CubePlatform getPlatform() {
        return this.platform;
    }

    public AgentConfig getConfig() {
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

    public List<Plugin> getPlugins() {
        return this.plugins;
    }

    /**
     * Gets the plugin having the given id.
     *
     * @param namespace
     */
    public Plugin getPlugin(String namespace) {
        for (Plugin e : getPlugins()) {
            if (e.getPluginFactory().getNamespace().equalsIgnoreCase(namespace))
                return e;
        }
        return null;
    }

    /**
     * Gets the Cube Agent's Communicator.
     *
     * @return
     */
    public Communicator getCommunicator() {
        return this.communicator;
    }

    /**
     * Gets the Runtime Model
     *
     * @return
     */
    public RuntimeModel getRuntimeModel() {
        return this.runtimeModel;
    }

    /**
     * Creates a new Managed Element.
     *
     * @param namespace
     * @param name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String namespace, String name, Properties properties) throws InvalidNameException, PropertyExistException {
        Plugin e = getPlugin(namespace);
        if (e != null) {
            ManagedElement me = e.newManagedElement(name, properties);
            if (me != null) {
                ((AbstractManagedElement)me).updateState(ManagedElement.UNMANAGED);
                unmanagedElements.put(me.getUUID(), me);
                return me;
            }
        }
        return null;
    }

    void deleteUnmanagedElement(String uuid) {
        this.unmanagedElements.remove(uuid);
    }

    /**
     * Gets the list of unmanaed elements.
     * (which are not associated to the runtime model)
     *
     * @return
     */
    public List<ManagedElement> getUnmanagedElements() {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        for (Object key : this.unmanagedElements.keySet()) {
            result.add(this.unmanagedElements.get(key));
        }
        return result;
    }

    /**
     * Gets the Runtime Model Controller.
     *
     * @return
     */
    public RuntimeModelController getRuntimeModelController() {
        return this.rmController;
    }

    public synchronized void addExternalElement(String element_uuid, String agent_uri) {
        this.externalElements.put(element_uuid, agent_uri);
    }

    public synchronized String getExternalAgentUri(String managed_element_uuid) {
        return this.externalElements.get(managed_element_uuid);
    }

    synchronized void removeExternalAgentElements(String agent_uri) {
        List<String> toBeRemoved = new ArrayList<String>();
        if (this.externalElements.containsValue(agent_uri)) {
            for (String uuid : this.externalElements.keySet()) {
                String agent = this.externalElements.get(uuid);
                if (agent != null && agent.equalsIgnoreCase(agent_uri)) {
                    toBeRemoved.add(uuid);
                }
            }
        }
        ((RuntimeModelImpl)this.getRuntimeModel()).removeReferences(toBeRemoved);
    }

    /**
     * Remove all UNMANAGED Elements.
     */
    public synchronized void removeUnmanagedElements() {
        this.unmanagedElements.clear();
    }

    public void run() {
        System.out.println("[INFO] >>>>>>>>> starting agent: " + uri.toString());
        if (this.communicator != null) {
            try {
                this.communicator.run(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.lifeController != null)
            this.lifeController.start();
        for (Plugin ex: this.getPlugins()) {
            ex.run();
        }
        if (this.checker != null) {
            this.checker.start();
        }
    }

    public void stop() {
        System.out.println("[INFO] >>>>>>>>> stopping CubeAgent: " + uri.toString());
        for (Plugin ex: this.getPlugins()) {
            ex.stop();
        }
        if (this.lifeController != null)
            this.lifeController.stop();
        if (this.communicator != null)
            this.communicator.stop();
        if (this.checker != null) {
            this.checker.stop();
        }
    }

    public void destroy() {
        System.out.println("[INFO] >>>>>>>>> destroying CubeAgent: " + uri.toString());
        for (Plugin ex: this.getPlugins()) {
            ex.destroy();
        }
        if (this.lifeController != null)
            this.lifeController.destroy();
        if (this.communicator != null)
            this.communicator.destroy();
        if (this.checker != null) {
            this.checker.destroy();
        }
    }

    Resolver getResolver() {
        return this.resolver;
    }
}
