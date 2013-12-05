package fr.liglab.adele.cube.autonomicmanager;

import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 7:08 PM
 */
public interface RuntimeModel {

    public List<ManagedElement> getElements();

    public List<ManagedElement> getElements(String namespace, String name);

    public List<ManagedElement> getElements(int state);

    public List<ManagedElement> getElements(String namespace, String name, int state);

    public List<ManagedElement> getManagedElements();

    public List<ManagedElement> getUnmanagedElements();

    public void addListener(RuntimeModelListener listener);

    public void deleteListener(RuntimeModelListener listener);

    public void deleteListeners();

    public void refresh();

    public boolean hasChanged();

    public ManagedElement getManagedElement(String uuid);

    void manage(String uuid);

    public void removeReferencedElements(List<String> refs);

    public void removeReferencedElement(String ref);

    void removeUnmanagedElements();
}
