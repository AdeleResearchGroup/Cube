 
package fr.liglab.adele.cube.archetype.designer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;

public class HideDetailHandler {
	
	@Inject
	IArchetypeService arch;
	
	@Execute
	public void execute() {
		this.arch.getCurrentArchetype().hideDetail();
	}
	
	@CanExecute
	public boolean canExecute() {
		return this.arch.getCurrentArchetype() != null;
	}
		
}