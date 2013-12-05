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


package fr.liglab.adele.cube.extensions.core.impl;

import fr.liglab.adele.cube.extensions.ExtensionPoint;
import fr.liglab.adele.cube.extensions.core.communicator.SocketCommunicator;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.extensions.ExtensionFactoryService;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.core.model.*;
import fr.liglab.adele.cube.extensions.core.resolvers.*;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:58 PM
 */
public class CoreExtension extends AbstractExtension {

    SocketCommunicator comm = new SocketCommunicator(this);

    public CoreExtension(AutonomicManager am, ExtensionFactoryService factory, Properties properties) {
        super(am, factory, properties);
    }

    public List<ExtensionPoint> getExtensionPoints() {
        List<ExtensionPoint> extensionPointsList = new ArrayList<ExtensionPoint>();

        // communicator
        extensionPointsList.add(comm);

        // managed elements
        extensionPointsList.add(new ComponentFactory(this));
        extensionPointsList.add(new NodeFactory(this));
        extensionPointsList.add(new ScopeFactory(this));
        extensionPointsList.add(new MasterFactory(this));


        // specific resolvers
        extensionPointsList.add(new HasProperty(this));
        extensionPointsList.add(new IsLocal(this));
        extensionPointsList.add(new HasMaxInstancesPerAM(this));
        extensionPointsList.add(new LocatedIn(this));

        extensionPointsList.add(new HasComponentId(this));
        extensionPointsList.add(new HasComponentType(this));
        extensionPointsList.add(new Connected(this));
        extensionPointsList.add(new HasSourceComponent(this));
        extensionPointsList.add(new HasMaxInputComponents(this));
        extensionPointsList.add(new HasNode(this));

        extensionPointsList.add(new HasNodeId(this));
        extensionPointsList.add(new HasNodeType(this));
        extensionPointsList.add(new OnNode(this));
        extensionPointsList.add(new HoldComponent(this));
        extensionPointsList.add(new InScope(this));

        extensionPointsList.add(new HasScopeId(this));
        extensionPointsList.add(new ControlledBy(this));

        return extensionPointsList;
    }

    public void start() {
        if (comm != null) comm.start();
        Object master = getProperties().get("master");
        if (master != null) {
            if (master.toString().equalsIgnoreCase("true")) {
                Master m = new Master(getAutonomicManager().getUri());
                getAutonomicManager().getRuntimeModelController().addManagedElement(m);
                getAutonomicManager().getRuntimeModelController().getRuntimeModel().refresh();
            }
        }
    }

    public void stop() {
        if (comm != null) comm.stop();
    }

    public void destroy() {
        if (comm != null) comm.destroy();
        comm = null;
    }

}
