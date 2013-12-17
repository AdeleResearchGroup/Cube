 
package fr.liglab.adele.cube.archetype.designer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import fr.liglab.adele.cube.archetype.designer.parts.DiagramPart;

public class NewHandler {
	
	@Inject
	IArchetypeService arch;
		
	@Inject
	EPartService partService;
	
	@Execute
	public void execute() {
		//if (this.arch.getCurrentArchetype() == null) {
			Archetype archetype = this.arch.newArchetype();		
			this.arch.setCurrentArchetype(archetype);
		//} else {
			
		//}
	}

	
	
			
}