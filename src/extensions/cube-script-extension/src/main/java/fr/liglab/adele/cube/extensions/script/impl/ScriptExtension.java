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
import fr.liglab.adele.cube.extensions.script.monitorsExecutors.Scripter;
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

    Scripter s;

    public ScriptExtension(AutonomicManager am, ExtensionFactoryService factory, Properties properties) {
        super(am, factory, properties);
        s = new Scripter(this);
    }

    @Override
    public List<ExtensionPoint> getExtensionPoints() {
        List<ExtensionPoint> extensionPointsList = new ArrayList<ExtensionPoint>();

        extensionPointsList.add(s);

        return extensionPointsList;
    }

    public void start() {
        s.start();
    }


    public void stop() {
        System.out.println("[INFO] Stopping script extension..");
    }

    public void destroy() {

    }

}
