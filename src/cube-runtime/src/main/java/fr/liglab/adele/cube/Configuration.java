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

import fr.liglab.adele.cube.extensions.ExtensionConfig;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 6:19 PM
 */
public class Configuration {

    public static final String DEFAULT_HOST = "localhost";
    public static final long DEFAULT_PORT = 38000;
    public static final long DEFAULT_PULSE = 3000;
    private static final long DEFAULT_KAINTERVAL = 5000;
    private static final long DEFAULT_KARETRY = 1;
    public static final String DEFAULT_RESOLVER = "default-resolver";
    public static final String DEFAULT_COMMUNICATOR = "socket-communicator";


    private String host = DEFAULT_HOST;
    private long port = DEFAULT_PORT;
    private long pulse = DEFAULT_PULSE;

    private String archetypeUrl;
    private String resolverName = DEFAULT_RESOLVER;
    private String communicatorName = DEFAULT_COMMUNICATOR;
    private boolean debug = false;
    private boolean perf = false;
    private boolean persist = false;

    private Properties properties = new Properties();

    private List<ExtensionConfig> extensions = new ArrayList<ExtensionConfig>();
    private long keepAliveInterval = DEFAULT_KAINTERVAL;
    private long keepAliveRetry = DEFAULT_KARETRY;

    public Configuration() {

    }

    public String getHost() {
        return host;
    }

    public long getPort() {
        return port;
    }

    public long getPulse() {
        return pulse;
    }


    public String getCommunicatorName() {
        return communicatorName;
    }

    public void setCommunicatorName(String communicatorName) {
        this.communicatorName = communicatorName;
    }

    public boolean isDebug() {
        return debug;
    }
    public boolean isPersist() {
        return persist;
    }

    public boolean isPerf() {
        return perf;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(long port) {
        this.port = port;
    }

    public void setRmCheckInterval(long pulse) {
        this.pulse = pulse;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public String getArchetypeUrl() {
        return archetypeUrl;
    }

    public void setArchetypeUrl(String archetypeUrl) {
        this.archetypeUrl = archetypeUrl;
    }

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

    public List<ExtensionConfig> getExtensionConfigs() {
        return extensions;
    }

    public void addExtension(ExtensionConfig aec) {
        this.extensions.add(aec);
    }

    public void setExtensions(List<ExtensionConfig> extensions) {
        this.extensions = extensions;
    }

    public void setPerf(boolean booleanValue) {
        this.perf = booleanValue;
    }

    public void setKeepAliveInterval(long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public void setKeepAliveRetry(long keepAliveRetry) {
        this.keepAliveRetry = keepAliveRetry;
    }

    public long getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public long getKeepAliveRetry() {
        return keepAliveRetry;
    }
}
