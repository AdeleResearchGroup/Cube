package fr.liglab.adele.cube.archetype.designer.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.liglab.adele.cube.archetype.designer.model.DescriptionProperty;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.Property;

public class ElementFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ElementValue) {		
			if (((ElementValue) element).getInputProperties().size() == 0) {
				return true;
			}
			for (Property p : ((ElementValue) element).getInputProperties()) {
				if (p instanceof DescriptionProperty) {
					if (p.getSubject() instanceof ElementDescription) {
						if (((ElementDescription)p.getSubject()).isShowProperties() == true) {
							return true;
						}
					}
				} else {
					return true;
				}
			}
			return false;
			
		}
		return true;
	}

}
