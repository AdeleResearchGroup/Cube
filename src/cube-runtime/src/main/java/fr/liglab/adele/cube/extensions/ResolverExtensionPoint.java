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


package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.resolver.Variable;
import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:02 AM
 */
public interface ResolverExtensionPoint extends ExtensionPoint {

    boolean check(ManagedElement me, String value);

    boolean perform(ManagedElement me, String value);

    List<String> find(ManagedElement me, ManagedElement description);
}
