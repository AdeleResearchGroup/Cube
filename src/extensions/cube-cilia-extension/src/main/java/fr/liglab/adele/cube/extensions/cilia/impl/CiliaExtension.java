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


package fr.liglab.adele.cube.extensions.cilia.impl;


import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.ExtensionFactoryService;
import fr.liglab.adele.cube.extensions.ExtensionPoint;
import fr.liglab.adele.cube.extensions.cilia.monitorsExecutors.CiliaMonitorExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 7:23 PM
 */
public class CiliaExtension extends AbstractExtension {

    CiliaMonitorExecutor ce;
    CiliaContext cc;
    public CiliaExtension(AutonomicManager am, ExtensionFactoryService factory, Properties properties, CiliaContext cc) {
        super(am, factory, properties);
        this.cc = cc;
    }

    @Override
    public List<ExtensionPoint> getExtensionPoints() {
        List<ExtensionPoint> extensionPointsList = new ArrayList<ExtensionPoint>();

	// poll periodic -> pas besoin de s'enregistrer
//        extensionPointsList.add(ce);

        return extensionPointsList;
    }

    public void starting() {
        System.out.println("[INFO] Starting cilia extension..");
        ce = new CiliaMonitorExecutor(this, cc);
        if (ce != null) {
            String connectorType =  (String) getProperties().get("connectorType");
            if (connectorType!=null){
                this.ce.setConnectorType(connectorType);
            }else{
                this.ce.setConnectorType("socket");
            }
            ce.start();
        }
    }

    public void stopping() {
        System.out.println("[INFO] Stopping cilia extension..");
        if (ce != null) ce.stop();
    }

    public void destroying() {
        if (ce != null) ce.destroy();
    }

}
