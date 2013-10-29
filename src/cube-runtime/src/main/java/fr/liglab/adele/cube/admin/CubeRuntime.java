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


package fr.liglab.adele.cube.admin;

import fr.liglab.adele.cube.extensions.CommunicatorExtensionPoint;
import fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.Configuration;
import fr.liglab.adele.cube.extensions.ExtensionFactoryService;
import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.autonomicmanager.impl.AutonomicManagerImpl;
import fr.liglab.adele.cube.util.perf.PerformanceChecker;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;

import java.util.*;

/**
 * Implementation class of the AdministrationService OSGi service.
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:45 PM
 */
@Component
@Provides
@Instantiate
public class CubeRuntime implements AdministrationService {

    private BundleContext bundleContext;

    /**
     * <CubeAgentURI, AutonomicManager>
     */
    private Map<String, AutonomicManager> autonomicManagers;
    {
        autonomicManagers = new HashMap<String, AutonomicManager>();
    }

    private PerformanceChecker performanceChecker;

    /**
     * Extension Factories
     */
    @Requires(specification="fr.liglab.adele.cube.extensions.ExtensionFactoryService",optional = true)
    private List<ExtensionFactoryService> extensionFactories;


    //@Requires(specification = "fr.liglab.adele.cube.extensions.CommunicatorExtensionPoint")
    private List<CommunicatorExtensionPoint> communicators;


    /**
     * Constructor.
     * @param btx Bundle Context injected automatically by iPOJO
     */
    public CubeRuntime(BundleContext btx) {
        this.bundleContext = btx;
        this.performanceChecker = new PerformanceChecker();
        extensionFactories = new ArrayList<ExtensionFactoryService>();
        communicators = new ArrayList<CommunicatorExtensionPoint>();
    }

    /**
     * Called when the Cube Platform starts on the local OSGi Platform.
     */
    @Validate
    public void starting() {
        String msg = "\n";

        msg += "\n    _______              ";
        msg += "\n   /|      |             ";
        msg += "\n  | | CUBE |...Version:  ";
        msg += "\n  | |______|   " + getVersion();
        msg += "\n  |/______/              ";
        msg += "\n";
        System.out.println(msg);
    }

    /**
     * Called when the Cube Platform stops on the local OSGi Platform.
     * All the created Cube Agents will be destroyed.
     */
    @Invalidate
    public synchronized void stopping() {
        System.out.println(" ");
        System.out.println("[INFO] ... Stopping CUBE Runtime");

        // Stopping and destroying all the created cube agents.
        List<String> tmp = new ArrayList<String>();

        for (String a : this.autonomicManagers.keySet()) {
            tmp.add(a);
        }
        for (String a : tmp) {
            stopAutonomicManager(a);
            destroyAutonomicManager(a);
        }
        System.out.println("[INFO] ... writing performance measures to file...");
        if (this.performanceChecker != null) {
            this.performanceChecker.saveToFile();
        }
        System.out.println("[INFO] ... Bye!");
        System.out.println(" ");
    }

    /**
     * Create Cube Agent.
     *
     * @param config AutonomicManager Config
     * @return Cube Agent URI
     */
    public synchronized  String createAutonomicManager(Configuration config) throws AutonomicManagerException {

        if (config == null) {
            throw new AutonomicManagerException("No configuration was given to create the Cube Agent!");
        }
        if (config.getHost() == null || config.getHost().length() == 0) {
            throw new AutonomicManagerException("You should provide a correct 'host' in the Cube Agent's configuration!");
        }
        if (config.getPort() < 0) {
            throw new AutonomicManagerException("You should provide a correct 'port' in the Cube Agent's configuration!");
        }
        // verify that no other existing __autonomicmanager has the same configuration (host+port)
        for (String key: autonomicManagers.keySet()) {
            if (autonomicManagers.get(key).getConfiguration().getHost().equalsIgnoreCase(config.getHost()) &&
                    autonomicManagers.get(key).getConfiguration().getPort() == config.getPort()) {
                throw new AutonomicManagerException("Autonomic Manager with the configuration ("+config.getHost()+","+config.getPort()+") already exists!");
            }
        }
        // all is ok, we create the new __autonomicmanager
        AutonomicManager ci = new AutonomicManagerImpl(this, config);
        autonomicManagers.put(ci.getUri(), ci);
        ci.start();
        return ci.getUri().toString();

    }

    /**
     * Destroy Cube Agent.
     *
     * @param amUri
     */
    public synchronized  void destroyAutonomicManager(String amUri) {
        AutonomicManager am = getAutonomicManager(amUri);
        if (am != null)
            am.destroy();
        Set set2 = this.autonomicManagers.keySet();
        Iterator itr2 = set2.iterator();
        while (itr2.hasNext())
        {
            Object o2 = itr2.next();
            if (o2.toString().equalsIgnoreCase(amUri)) {
                itr2.remove(); //remove the pair if key length is less then 3
                return;
            }
        }
    }

    /**
     * Start Cube Agent
     *
     * @param amUri
     */
    public void runAutonomicManager(String amUri) {
        AutonomicManager agent = getAutonomicManager(amUri);
        if (agent != null)
            agent.start();
    }

    /**
     * Stop Cube Agent
     *
     * @param amUri
     */
    public void stopAutonomicManager(String amUri) {
        AutonomicManager agent = getAutonomicManager(amUri);
        if (agent != null)
            agent.stop();
    }

    /**
     * Get Cube Agents in this local OSGi Platform.
     *
     * @return List of Cube Agent' URIs
     */
    public synchronized  List<String> getAutonomicManagers() {
        List<String> ids = new ArrayList<String>();
        for (String key: autonomicManagers.keySet()) {
            ids.add(key);
        }
        return ids;
    }

    /**
     * Gets Cube Agents in this local OSGi Platform.
     *
     * @return List of Cube Agent' local Ids
     */
    public synchronized  List<String> getAutonomicManagersLocalIds() {
        List<String> ids = new ArrayList<String>();
        for (String key: autonomicManagers.keySet()) {
            ids.add(autonomicManagers.get(key).getLocalId());
        }
        return ids;
    }

    /**
     * Get the Cube Agent of the given URI.
     *
     * @param uri
     * @return
     */
    public synchronized  AutonomicManager getAutonomicManager(String uri) {
        return this.autonomicManagers.get(uri);
    }

    /**
     * Gets the Cube Agent of the given local Id.
     *
     * @param localId
     * @return
     */
    public synchronized AutonomicManager getAutonomicManagerByLocalId(String localId) {
        if (localId != null) {
            for (String key: autonomicManagers.keySet()) {
                if (autonomicManagers.get(key).getLocalId().equalsIgnoreCase(localId)) {
                    return autonomicManagers.get(key);
                }
            }
        }
        return null;
    }

    /**
     * Get the Extension Bundle has the given id.
     * Its OSGi bundle should be already deployed.
     *
     * @param namespace
     * @return
     */
    public ExtensionFactoryService getExtensionFactory(String namespace) {
        if (namespace != null && namespace.length() > 0) {
            for (ExtensionFactoryService eb : this.extensionFactories) {
                if (eb.getNamespace().equalsIgnoreCase(namespace))
                    return eb;
            }
        }
        return null;
    }

    /**
     * Gets the CommunicatorExtensionPoint having the given id.
     *
     * @param id
     * @return
     */
    public CommunicatorExtensionPoint getCommunicator(String id) {
        if (id != null && id.length() > 0) {
            for (CommunicatorExtensionPoint c : this.communicators) {
                if (c.getName().equalsIgnoreCase(id))
                    return c;
            }
        }
        return null;
    }

    /**
     * Get OSGi Bundle Context.
     *
     * @return
     */
    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    public PerformanceChecker getPerformanceChecker() {
        return performanceChecker;
    }

    public void setPerformanceChecker(PerformanceChecker performanceChecker) {
        this.performanceChecker = performanceChecker;
    }
    /**
     * Get Cube Platform Version.
     *
     * @return Version number
     */
    public String getVersion() {
        return CUBE_VERSION;
    }
}
