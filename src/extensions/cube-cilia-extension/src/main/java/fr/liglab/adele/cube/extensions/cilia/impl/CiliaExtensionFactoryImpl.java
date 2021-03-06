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
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.cilia.CiliaExtensionFactory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 7:22 PM
 */
@Component
@Provides
@Instantiate
public class CiliaExtensionFactoryImpl implements CiliaExtensionFactory {

    @Requires
    private CiliaContext ccontext ;

    public String getName() {
        return NAME;
    }

    public String getPrefix() {
        return PREFIX;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String getFullName() {
        return getNamespace()+":"+getName();
    }

    public Extension newExtensionInstance(AutonomicManager am, Properties properties) {
        System.out.println(">>> new cilia extension ...");
        return new CiliaExtension(am, this, properties, ccontext);
    }
}
