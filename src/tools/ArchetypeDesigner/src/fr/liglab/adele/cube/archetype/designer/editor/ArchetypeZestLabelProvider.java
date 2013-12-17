package fr.liglab.adele.cube.archetype.designer.editor;


import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.liglab.adele.cube.archetype.designer.model.DescriptionProperty;
import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.GoalProperty;
import fr.liglab.adele.cube.archetype.designer.model.Property;

public class ArchetypeZestLabelProvider extends LabelProvider implements IConnectionStyleProvider, IEntityStyleProvider, IColorProvider {

	Color elementback;
	Color valueback;
	Image componentIcon = createImage("icons/component.gif");
	Image nodeIcon = createImage("icons/node.gif");
	Image scopeIcon = createImage("icons/scope.gif");
	Image masterIcon = createImage("icons/master.gif");
	
	public ArchetypeZestLabelProvider(Color elementback, Color valueback) {
		this.elementback = elementback;
		this.valueback = valueback;
	}
	
	private Image createImage(String path) {
	    Bundle bundle = FrameworkUtil.getBundle(ArchetypeZestLabelProvider.class);
	    URL url = FileLocator.find(bundle, new org.eclipse.core.runtime.Path(path), null);
	    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
	    return imageDcr.createImage();	
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof Element) {
			if (element instanceof ElementDescription) {
				return ((ElementDescription) element).getId();  
			} else if (element instanceof ElementValue) {
				return ((ElementValue) element).getValue();
			}
		}
		if (element instanceof Property) {
			if (element instanceof GoalProperty) {
				return ((Property) element).getName() + " [" + ((GoalProperty) element).getResolutionStrategy() + "]";
			}
			return ((Property) element).getName();
		}
		return null;
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof ElementDescription) {
			if (((ElementDescription) element).getName().toLowerCase().equalsIgnoreCase("component")) {
				return componentIcon;
			} else if (((ElementDescription) element).getName().toLowerCase().equalsIgnoreCase("node")) {
				return nodeIcon;
			} else if (((ElementDescription) element).getName().toLowerCase().equalsIgnoreCase("scope")) {
				return scopeIcon;
			} else if (((ElementDescription) element).getName().toLowerCase().equalsIgnoreCase("master")) {
				return masterIcon;
			}
		}
		return super.getImage(element);
	}
	
	@Override
	public void dispose() {		
		super.dispose();
		if (componentIcon != null) {
			componentIcon.dispose();
			componentIcon = null;
		}
	}
	
	@Override
	public Color getForeground(Object element) {
		if (element instanceof ElementValue) {
			return ColorConstants.white;
		}
		if (element instanceof ElementDescription) {
			return ColorConstants.black;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof ElementValue) {
			return valueback;
		}
		if (element instanceof ElementDescription) {
			return elementback;
		}
		return null;
	}

	@Override
	public int getConnectionStyle(Object rel) {
		if (rel instanceof DescriptionProperty) {
			return ZestStyles.CONNECTIONS_DIRECTED;
		}
		if (rel instanceof GoalProperty) {
			return ZestStyles.CONNECTIONS_DASH | ZestStyles.CONNECTIONS_DIRECTED;
		}
		return 0;
	}

	@Override
	public Color getColor(Object rel) {
		
		if (rel instanceof GoalProperty) {
			return ColorConstants.red;
		}
		return null;
	}

	@Override
	public Color getHighlightColor(Object rel) {
		return null;
	}

	@Override
	public int getLineWidth(Object rel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getNodeHighlightColor(Object entity) {
		if (entity instanceof ElementValue) {
			
				return ColorConstants.black;
			 
		}
		return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
		if (entity instanceof ElementDescription) {
			if (((ElementDescription) entity).isShowProperties() == false && ((ElementDescription) entity).getProperties().size() > 0) {
				return ColorConstants.black;
			} 
		}
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
		if (entity instanceof ElementDescription) {
			if (((ElementDescription) entity).isShowProperties() == false && ((ElementDescription) entity).getProperties().size() > 0) {
				return 2;
			} 
		}
		return 0;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
		
		return ColorConstants.black;
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}


	
}
