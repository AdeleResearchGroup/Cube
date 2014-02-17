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

import fr.liglab.adele.cube.autonomicmanager.RuntimeModel;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelListener;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Notification;
import fr.liglab.adele.cube.AutonomicManager;

import java.util.*;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 9:32 PM
 */
public class RuntimeModelImpl implements RuntimeModel {

    private AutonomicManager am;

    private boolean changed = false;
    private Vector<RuntimeModelListener> listeners;

    /**
     * key:   uuid
     * value: ManagedElement
     */
    Map<String, ManagedElement> elements = new HashMap<String, ManagedElement>();

    private Map<String, ManagedElement> unmanagedElements = new HashMap<String, ManagedElement>();


    public RuntimeModelImpl(AutonomicManager autonomicManager) {
        this.am = autonomicManager;
        listeners = new Vector<RuntimeModelListener>();
    }

    /**
     * Adds a Managed ElementDescription instance to the Runtime Model.
     * It will hold automatically the UNMANAGED state, until it will be resolved!
     *
     * @param element Managed ElementDescription instance to be added.
     */

     void add(ManagedElement element, int state) {
        if (element != null) {
            synchronized (element) {
                element.updateState(state);
                if (state == ManagedElement.UNMANAGED) {
                    synchronized (this.unmanagedElements) {
                        this.unmanagedElements.put(element.getUUID(), element);
                    }
                } else {
                    synchronized (this.elements) {
                        this.elements.put(element.getUUID(), element);
                    }
                }
            }
        }
    }

    public void manage(String uuid) {
        ManagedElement me = null;
        synchronized (this.unmanagedElements) {
            me = this.unmanagedElements.get(uuid) ;
        }
        if (me != null && me.getState() == ManagedElement.UNMANAGED) {
            add(me, ManagedElement.INVALID);
            removeUnmanaged(me);
        }
    }

    public void refresh() {
        setChanged();
        notifyListeners(new Notification(RuntimeModelListener.UPDATED_RUNTIMEMODEL, this));
    }

    /**
     * TODO: rename to getElements
     * @return
     */
    public List<ManagedElement> getElements() {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        synchronized (this.elements) {
            for (String key : this.elements.keySet()) {
                result.add(this.elements.get(key));
            }
        }
        synchronized (this.unmanagedElements) {
            for (String key : this.unmanagedElements.keySet()) {
                result.add(this.unmanagedElements.get(key));
            }
        }
        return result;
    }

    public List<ManagedElement> getManagedElements() {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        synchronized (this.elements) {
            for (String key : this.elements.keySet()) {
                result.add(this.elements.get(key));
            }
        }
        return result;
    }

    public List<ManagedElement> getUnmanagedElements() {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        synchronized (this.unmanagedElements) {
            for (String key : this.unmanagedElements.keySet()) {
                result.add(this.unmanagedElements.get(key));
            }
        }
        return result;
    }

    /**
     * TODO: rename to getElements
     * @param namespace
     * @param name
     * @return
     */
    public List<ManagedElement> getElements(String namespace, String name) {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        synchronized (this.elements) {
            for (String key : this.elements.keySet()) {
                if (this.elements.get(key).getNamespace().equalsIgnoreCase(namespace)
                        && this.elements.get(key).getName().equalsIgnoreCase(name))
                    result.add(this.elements.get(key));
            }
        }
        synchronized (this.unmanagedElements) {
            for (String key : this.unmanagedElements.keySet()) {
                if (this.unmanagedElements.get(key).getNamespace().equalsIgnoreCase(namespace)
                        && this.unmanagedElements.get(key).getName().equalsIgnoreCase(name))
                    result.add(this.unmanagedElements.get(key));
            }
        }

        return result;
    }

    /**
     * TODO: rename to getElements
     * @param state
     * @return
     */
    public  List<ManagedElement> getElements(int state) {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        if (state == ManagedElement.UNMANAGED) {
            synchronized (this.unmanagedElements) {
                for (String key : this.unmanagedElements.keySet()) {
                    result.add(this.unmanagedElements.get(key));
                }
            }
        } else {
            synchronized (this.elements) {
                for (String key : this.elements.keySet()) {
                    if (this.elements.get(key).getState() == state) {
                        result.add(this.elements.get(key));
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * TODO: rename to getElements
     * @param namespace
     * @param name
     * @param state
     * @return
     */
    public List<ManagedElement> getElements(String namespace, String name, int state) {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        if (state == ManagedElement.UNMANAGED) {
            synchronized (this.unmanagedElements) {
                for (String key : this.unmanagedElements.keySet()) {
                    if (this.unmanagedElements.get(key).getNamespace().equalsIgnoreCase(namespace)
                            && this.unmanagedElements.get(key).getName().equalsIgnoreCase(name)
                            )
                        result.add(this.unmanagedElements.get(key));
                }
            }
        } else {
            synchronized (this.elements) {
                for (String key : this.elements.keySet()) {
                    if (this.elements.get(key).getNamespace().equalsIgnoreCase(namespace)
                            && this.elements.get(key).getName().equalsIgnoreCase(name)
                            && this.elements.get(key).getState() == state)
                        result.add(this.elements.get(key));
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * TODO: rename to getElement
     * @param uuid
     * @return
     */
    public ManagedElement getManagedElement(String uuid) {
        ManagedElement result = null;
        synchronized (this.elements) {
            result = this.elements.get(uuid);
        }
        if (result != null) return result; else {
            synchronized (this.unmanagedElements) {
                return this.unmanagedElements.get(uuid);
            }
        }
    }

    public void removeReferencedElements(List<String> refs) {
        //System.out.println("------------ removeReferencedElements ... ");
        if (refs != null) {
            boolean changed = false;
            synchronized (this.elements) {
                for (String meuuid : this.elements.keySet()) {
                    //System.out.println("------------ analysing : " + meuuid);
                    ManagedElement me = this.elements.get(meuuid);
                    if (me != null) {
                        synchronized (me) {
                            for (String ref : refs) {
                                changed = me.removeReferencedElement(ref);
                            }
                        }
                    }
                    ManagedElement me2 = null;
                    synchronized (this.unmanagedElements) {
                        me2 = this.unmanagedElements.get(meuuid);
                    }
                    if (me2 != null) {
                        synchronized (me2) {
                            for (String ref : refs) {
                                changed = me2.removeReferencedElement(ref);
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeReferencedElement(String ref) {
        if (ref != null) {
            boolean changed = false;
            synchronized (this.elements) {
                for (String meuuid : this.elements.keySet()) {
                    ManagedElement me = this.elements.get(meuuid);
                    if (me != null) {
                        changed = me.removeReferencedElement(ref);
                    }

                    ManagedElement me2 = null;
                    synchronized (this.unmanagedElements) {
                        me2 = this.unmanagedElements.get(meuuid);
                    }
                    if (me2 != null) {
                        changed = me2.removeReferencedElement(ref);
                    }
                }
            }
            if (changed == true) {
                setChanged();
                notifyListeners(new Notification(RuntimeModelListener.UPDATED_RUNTIMEMODEL, this));
            }
        }
    }

    private void removeUnmanaged(ManagedElement me1) {
        synchronized (this.unmanagedElements) {
            Set set2 = this.unmanagedElements.keySet();
            Iterator itr2 = set2.iterator();
            while (itr2.hasNext())
            {
                Object o2 = itr2.next();
                if (o2.toString().equalsIgnoreCase(me1.getUUID())) {
                    itr2.remove(); //remove the pair if key length is less then 3
                    return;
                }
            }
        }
    }

    private void removeManaged(ManagedElement me1) {
        synchronized (this.elements) {
            Set set = this.elements.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext())
            {
                Object o = itr.next();
                if (o.toString().equalsIgnoreCase(me1.getUUID())) {
                    itr.remove(); //remove the pair if key length is less then 3
                    return;
                }
            }
        }
    }

    public void remove(ManagedElement me1) {
        if (me1 == null) return;
        removeManaged(me1);
        removeUnmanaged(me1);
    }

    public void removeUnmanagedElements() {
        List<String> tmp = new ArrayList<String>();
        synchronized (this.unmanagedElements) {
            for (String me : this.unmanagedElements.keySet()) {
                tmp.add(me);
            }
        }
        for (String t : tmp) {
            this.am.getRuntimeModelController().destroyElement(t);
        }
    }

    /**
     * ================================ NOTIFICATIONS ===============================================
     */

    /**
     * Adds a listener to the set of listeners for this object, provided
     * that it is not the same as some listener already in the set.
     * The order in which notifications will be delivered to multiple
     * listeners is not specified.
     *
     * @param  listener   a Runtime Model Listener to be added.
     * @throws NullPointerException  if the parameter o is null.
     */
     public void addListener(RuntimeModelListener listener) {
         if (listener == null)
            throw new NullPointerException();
         synchronized (this.listeners) {
             if (!this.listeners.contains(listener)) {
                listeners.addElement(listener);
             }
         }
     }

    /**
     * Deletes a Listener from the set of listeners of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   listener   the listener to be deleted.
     */
     public void deleteListener(RuntimeModelListener listener) {
        synchronized (this.listeners) {
            this.listeners.removeElement(listener);
        }
     }

    /**
     * Clears the listeners list so that this object no longer has any observers.
     */
     public void deleteListeners() {
         synchronized (this.listeners) {
            this.listeners.removeAllElements();
         }
     }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   notification   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
     private void notifyListeners(Notification notification) {
         /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Listeners.
         */
         Object[] arrLocal;

         synchronized (this.listeners) {
             /* We don't want the Observer doing callbacks into
              * arbitrary code while holding its own Monitor.
              * The code where we extract each Observable from
              * the Vector and store the state of the Observer
              * needs synchronization, but notifying observers
              * does not (should not).  The worst result of any
              * potential race-condition here is that:
              * 1) a newly-added Observer will miss a
              *   notification in progress
              * 2) a recently unregistered Observer will be
              *   wrongly notified when it doesn't care
              */
              if (!changed)
                 return;
              arrLocal = listeners.toArray();
              clearChanged();
          }

          for (int i = arrLocal.length-1; i>=0; i--)
            ((RuntimeModelListener)arrLocal[i]).update(this, notification);
      }

    /**
     * Marks the <tt>RuntimeModelImpl</tt> as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
     private synchronized void setChanged() {
        changed = true;
     }

     /**
      * Indicates that this object has no longer changed, or that it has
      * already notified all of its listeners of its most recent change,
      * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
      * This method is called automatically by the
      * <code>notifyListeners</code> methods.
      *
      */
     protected synchronized void clearChanged() {
        changed = false;
     }

     /**
      * Tests if the Runtime Model has changed.
      *
      * @return  <code>true</code> if and only if the <code>setChanged</code>
      *          method has been called more recently than the
      *          <code>clearChanged</code> method on this object;
      *          <code>false</code> otherwise.
      */
      public synchronized boolean hasChanged() {
        return changed;
      }

}
