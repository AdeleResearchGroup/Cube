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


package fr.liglab.adele.cube.plugins.osgi;

import fr.liglab.adele.cube.plugins.PluginFactory;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 11:50 AM
 */
public interface OSGiPluginFactory extends PluginFactory {
    String NAME = "osgi";
    String PREFIX = "osgi";
    String NAMESPACE = "fr.liglab.adele.cube.osgi";
}
