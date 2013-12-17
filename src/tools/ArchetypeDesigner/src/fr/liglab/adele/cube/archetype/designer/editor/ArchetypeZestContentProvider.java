package fr.liglab.adele.cube.archetype.designer.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.Property;

public class ArchetypeZestContentProvider extends ArrayContentProvider implements IGraphEntityRelationshipContentProvider {

	@Override
	public Object[] getElements(Object input) {
		ArrayList<Element> results = new ArrayList<Element>();
		if (input instanceof Archetype) {
			results.addAll(((Archetype) input).getElements());
		}
		return results.toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		super.inputChanged(viewer, oldInput, newInput);
	}
	
	
	
	@Override
	public Object[] getRelationships(Object source, Object dest) {
		
		Collection<Property> results = new ArrayList<Property>();
		if (source instanceof ElementDescription) {			
			for (Property p :  ((ElementDescription) source).getProperties()) {
				if (p.getObject()==dest) {
					results.add(p);
				}
			}			
		}
		return results.toArray();
	}

}
