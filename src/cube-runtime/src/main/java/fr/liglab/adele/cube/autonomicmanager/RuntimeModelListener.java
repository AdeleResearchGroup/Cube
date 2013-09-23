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


package fr.liglab.adele.cube.autonomicmanager;

import fr.liglab.adele.cube.autonomicmanager.RuntimeModel;
import fr.liglab.adele.cube.metamodel.Notification;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 1:47 PM
 */
public interface RuntimeModelListener {


    int NEW_UNMANAGED_INSTANCE = 0;
    int NEW_UNCHECKED_INSTANCE = 1;
    int NEW_VALID_INSTANCE = 2;
    int REMOVED_UNCHECKED_INSTANCE = 3;
    int REMOVED_VALID_INSTANCE = 4;
    int UPDATED_RUNTIMEMODEL = 5;

    public void update(RuntimeModel rm, Notification notification);
}
