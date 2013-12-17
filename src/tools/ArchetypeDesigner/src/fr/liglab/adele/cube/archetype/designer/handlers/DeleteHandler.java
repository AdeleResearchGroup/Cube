 
package fr.liglab.adele.cube.archetype.designer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;

public class DeleteHandler {
	
	@Inject
	IArchetypeService arch;
	
	@CanExecute
	public boolean canExecute() {
		if (arch != null) {
			return arch.getCurrentArchetype().getSelectedObject()!=null;
		}
		return false;
	}
	
	@Execute
	public void execute() {
		if (this.arch.getCurrentArchetype().getSelectedObject() != null) {
			this.arch.getCurrentArchetype().removeObject(this.arch.getCurrentArchetype().getSelectedObject());		
			System.out.println("DELETING ... " + this.arch.getCurrentArchetype().getSelectedObject());
			this.arch.getCurrentArchetype().setSelectedObject(null);
		}
		else { 		
			System.out.println("No Object Selected to be deleted!");
		}
	}
		
}