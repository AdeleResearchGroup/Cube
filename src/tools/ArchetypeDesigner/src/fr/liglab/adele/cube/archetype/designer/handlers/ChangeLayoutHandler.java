 
package fr.liglab.adele.cube.archetype.designer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import fr.liglab.adele.cube.archetype.designer.parts.DiagramPart;

public class ChangeLayoutHandler {
	
	@Inject 
	IArchetypeService arch;
	
	@Inject
	MApplication application;
	
	@Inject
	EModelService mservice;
	
	MPart part;
	
	@PostConstruct
	public void configure() {
		MUIElement mm = mservice.find("fr.liglab.adele.cube.archetype.designer.parts.diagrampart", application);
		if (mm instanceof MPart) {
			part = (MPart)mm;
		}
	}
	
	@Execute
	public void execute() {		
		if (part != null) {
			if (part.getObject() != null && part.getObject() instanceof DiagramPart) {
				((DiagramPart)part.getObject()).setLayoutManager();
			}
		}
	}
	
	
	@CanExecute
	public boolean canExecute() {		
		return this.arch.getCurrentArchetype() != null;
	}
		
}