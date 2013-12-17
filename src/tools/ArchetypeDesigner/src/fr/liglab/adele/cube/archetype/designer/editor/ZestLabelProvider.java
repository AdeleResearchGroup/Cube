package fr.liglab.adele.cube.archetype.designer.editor;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.zest.core.viewers.EntityConnectionData;

import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.Property;

public class ZestLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		if (element instanceof Element) {
			System.out.println("element.getName()...");
			Element e = (Element)element;
			return e.getId();
		} 
		if (element instanceof Property) {
			System.out.println("property.getName()...");
			Property p = (Property)element;
			return p.getName();
		}
		if (element instanceof EntityConnectionData) {
			EntityConnectionData test = (EntityConnectionData) element;
		      return "";
		}
		throw new RuntimeException("Wrong type: "
		        + element.getClass().toString());
	}		

}
