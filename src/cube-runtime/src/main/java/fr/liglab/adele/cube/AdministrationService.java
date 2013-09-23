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


package fr.liglab.adele.cube;

import fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException;
import fr.liglab.adele.cube.extensions.ExtensionFactoryService;
import org.osgi.framework.BundleContext;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:42 PM
 */
public interface AdministrationService {

    public static final String CUBE_VERSION = "2.0";

    /**
     *
     * @param config
     * @return AM Uri
     * @throws fr.liglab.adele.cube.autonomicmanager.AutonomicManagerException
     */
    public String createAutonomicManager(Configuration config)  throws AutonomicManagerException;

    /**
     * Destroy Cube AM.
     * @param amUri
     */
    public void destroyAutonomicManager(String amUri);

    /**
     * Start Cube AM
     * @param amUri
     */
    public void runAutonomicManager(String amUri);

    /**
     * Stop Cube Agent
     * @param amUri
     */
    public void stopAutonomicManager(String amUri);

    /**
     * Gets Cube Autonomic Managers in this local OSGi Platform.
     * @return List of Cube Agent' URIs
     */
    public List<String> getAutonomicManagers();

    /**
     * Gets Cube AMs in this local OSGi Platform.
     * @return List of Cube AM's local Ids
     */
    public List<String> getAutonomicManagersLocalIds();

    /**
     * Gets the Cube Autonomic Manager of the given URI.
     * @param uri
     * @return
     */
    public AutonomicManager getAutonomicManager(String uri);

    /**
     * Gets the Cube AM of the given local Id.
     * @param localId
     * @return
     */
    public AutonomicManager getAutonomicManagerByLocalId(String localId);

    /**
     * Gets the Extension Bundle has the given id.
     * Its OSGi bundle should be already deployed.
     * @param namespace
     * @return
     */
    public ExtensionFactoryService getExtensionFactory(String namespace);

    /**
     * Gets the CommunicatorExtensionPoint having the given id.
     * @param id
     * @return
     */
    //public CommunicatorExtensionPoint getCommunicator(String id);

    /**
     * Get OSGi Bundle Context.
     * @return
     */
    public BundleContext getBundleContext();
    /**
     * Get Cube Platform Version.
     * @return Version number
     */
    public String getVersion();
}
