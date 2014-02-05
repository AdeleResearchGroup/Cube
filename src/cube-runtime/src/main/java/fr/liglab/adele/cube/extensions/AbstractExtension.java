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
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.util.Utils;

import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:57 PM
 */
public abstract class AbstractExtension implements Extension, Runnable {

    Thread t;
    private boolean working = false;
    private boolean destroyRequested = false;

    private String uri = null;

    private AutonomicManager am = null;
    private ExtensionFactoryService factory = null;
    private Properties properties = new Properties();

    public AbstractExtension(AutonomicManager agent, ExtensionFactoryService factory, Properties properties) {
        this.uri = agent.getUri() + "/extension/" + factory.getName();
        this.am = agent;
        this.factory = factory;
        if (properties != null) {
            for (Object key: properties.keySet()) {
                this.properties.put(key, properties.get(key));
            }
        }
        t = new Thread(this);
    }

    public String getUri() {
        return this.uri;
    }

    public AutonomicManager getAutonomicManager() {
        return this.am;
    }

    public ExtensionFactoryService getExtensionFactory() {
        return this.factory;
    }

    public Properties getProperties(){
        Properties p = new Properties();
        for (Object k : this.properties.keySet()) {
            String pvalue = this.properties.getProperty(k.toString());
            p.put(k.toString(), Utils.evaluateValue(getAutonomicManager(), pvalue));
        }
        //return this.properties;
        return p;
    }

    public void run() {
        while(working == false) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        starting();
    }

    public void start() {
        //System.out.println("starting extension "+this.getExtensionFactory().getFullName()+ " ...");
        this.working = true;
        this.destroyRequested = false;
        t.start();
    }

    public void stop() {
        this.working = false;
        this.destroyRequested = false;
    }

    public void destroy() {
        this.working = false;
        this.destroyRequested = true;
    }

    public abstract List<ExtensionPoint> getExtensionPoints();

    /**
     * Track the given managed element.
     * If its state, properties, or references change, the extension is notified.
     * @param me The Managed ElementDescription to track
     */
    protected void trackManagedElement(ManagedElement me) {

    }

}
