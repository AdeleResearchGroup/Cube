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

package fr.liglab.adele.cube.agent;

import java.util.Properties;

/**
 * Agent's Plugin Config.
 * 
 * @author debbabi
 *
 */
public class PluginConfig {
	
	private String id = null;
	//private String version = null;
    private Properties properties = new Properties();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

    /*
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	} */

    public void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public String getProperty(String name) {
        if (name != null) {
            if (this.properties.get(name) != null)
                return this.properties.get(name).toString();
        }
        return null;
    }

    public Properties getProperties() {
        return properties;
    }
}
