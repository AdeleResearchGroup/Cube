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


package fr.liglab.adele.cube.archetype;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:09 AM
 */
public class GoalProperty extends Property {

    private String resolutionStrategy;
    private String priority;

    public GoalProperty(Archetype archetype, String namespace, String name, String resolutionStrategy, String priority, String documentation) {
        super(archetype, namespace, name, null, documentation);
        this.resolutionStrategy = resolutionStrategy;
        this.priority = priority;
    }

    public String getResolutionStrategy() {
        return resolutionStrategy;
    }

    public void setResolutionStrategy(String resolutionStrategy) {
        this.resolutionStrategy = resolutionStrategy;
    }

    public ResolutionStrategy getResolutionStrategyValue() {
        ResolutionStrategy rs=ResolutionStrategy.Find;
        if (resolutionStrategy != null) {
            if (resolutionStrategy.equalsIgnoreCase("F")) rs = ResolutionStrategy.Find;
            else if (resolutionStrategy.equalsIgnoreCase("C")) rs = ResolutionStrategy.Create;
            else if (resolutionStrategy.equalsIgnoreCase("FC")) rs = ResolutionStrategy.FindOrCreate;
            else if (resolutionStrategy.equalsIgnoreCase("FN")) rs = ResolutionStrategy.FindOrNothing;
        }
        return rs;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
