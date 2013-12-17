package fr.liglab.adele.cube.archetype.designer.editor;

import java.util.ArrayList;
import java.util.List;





import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.Property;

public class ZestElementContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider {
	
	@Override
	public Object[] getConnectedTo(Object entity) {
		if (entity instanceof Element) {
			Element e = (Element)entity;
			List<Element> result = new ArrayList<Element>();
			
			for (Property p : e.getProperties()) {
				result.add(p.getObject());
			}
			
			return result.toArray();
		}
		throw new RuntimeException("Type not supported!");
	}

}
