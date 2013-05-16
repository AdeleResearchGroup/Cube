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


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.metamodel.ManagedElement;

import static java.lang.Thread.sleep;

/**
 * Author: debbabi
 * Date: 5/6/13
 * Time: 6:08 PM
 */
public class RuntimeModelChecker implements Runnable {

    private CubeAgent agent;

    private boolean working = false;
    private boolean destroyRequested = false;

    Thread t;

    long pulse = 3000;

    public RuntimeModelChecker(CubeAgent agent) {
        this.agent = agent;
        this.pulse = agent.getConfig().getPulse();
        t = new Thread(this);
        t.start();
    }

    public void stop() {
        this.working = false;
    }

    public void start() {
        this.working = true;
    }

    public void destroy() {
        this.destroyRequested = true;
    }

    public void run() {
        while (true) {
            try {
                if (this.working) {
                    //System.out.println("[RM.CHECKER] checking runtime model...");
                    agent.removeUnmanagedElements();


                    for (ManagedElement me : agent.getRuntimeModel().getManagedElements(ManagedElement.UNCHECKED)) {
                        ((ResolverImpl)((CubeAgentImpl)agent).getResolver()).resolveUncheckedInstance(me);
                    }

                }
                if (this.destroyRequested) {
                    Thread.currentThread().interrupt();
                    break;
                }
                sleep(pulse);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }
    }
}
