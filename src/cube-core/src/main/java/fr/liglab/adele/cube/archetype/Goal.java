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

import java.util.List;
import java.util.ArrayList;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:09 AM
 */
public class Goal {

    private Archetype archetype;

    private String id;
    private String description;

    private static int index = 0;

    List<Objective> objectives = new ArrayList<Objective>();

    public Goal(Archetype archetype, String id, String description) {
        this.archetype = archetype;
        if (id != null) this.id = id; else this.id = "__g"+index++;
        if (description != null) this.description = description; else this.description = "";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addObjective(Objective o) {
        this.objectives.add(o);
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Goal) {
            Goal g = (Goal)obj;
            if (g.getId() != null && g.getId().equalsIgnoreCase(getId())) {
                return true;
            }  else {
                return false;
            }
        }
        return super.equals(obj);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
