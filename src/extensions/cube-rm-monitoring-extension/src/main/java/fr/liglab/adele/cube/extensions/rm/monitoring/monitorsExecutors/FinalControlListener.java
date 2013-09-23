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


package fr.liglab.adele.cube.extensions.rm.monitoring.monitorsExecutors;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import prefuse.controls.ControlAdapter;
import prefuse.controls.Control;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class FinalControlListener extends ControlAdapter implements Control {

    public void itemClicked(VisualItem item, MouseEvent e)
    {
        if(item instanceof NodeItem)
        {
            String occupation = ((String) item.get("descr"));
            //int age = (Integer) item.get("age");
            //String occupation = "hello";
            JPopupMenu jpub = new JPopupMenu();
            jpub.add(occupation);
            //jpub.add("Age: " + age);
            jpub.show(e.getComponent(),((int) item.getX()) + 300, ((int) item.getY()) + 300);

        }
    }

}
