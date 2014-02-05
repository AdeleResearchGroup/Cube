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


package fr.liglab.adele.cube.autonomicmanager.rmc;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelChecker;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import static java.lang.Thread.sleep;
/**
 * User: debbabi
 * Date: 9/21/13
 * Time: 11:48 PM
 */
public class RuntimeModelCheckerImpl implements RuntimeModelChecker, Runnable {

    private AutonomicManager agent;

    private boolean working = false;
    private boolean destroyRequested = false;

    Thread t;

    long pulse = 1000;

    public RuntimeModelCheckerImpl(AutonomicManager agent) {
        this.agent = agent;
        this.pulse = agent.getConfiguration().getPulse();
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
                    agent.getRuntimeModelController().getRuntimeModel().removeUnmanagedElements();


                    for (ManagedElement me : agent.getRuntimeModelController().getRuntimeModel().getElements(ManagedElement.INVALID)) {
                        agent.getArchetypeResolver().resolveUncheckedInstance(me);
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
