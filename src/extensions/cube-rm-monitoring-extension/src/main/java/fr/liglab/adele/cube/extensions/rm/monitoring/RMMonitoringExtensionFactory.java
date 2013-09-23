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


package fr.liglab.adele.cube.extensions.rm.monitoring;


import fr.liglab.adele.cube.extensions.ExtensionFactoryService;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 7:21 PM
 */
public interface RMMonitoringExtensionFactory extends ExtensionFactoryService {
    String NAME = "rm";
    String PREFIX = "rm";
    String NAMESPACE = "fr.liglab.adele.cube.rm.monitoring";
}
