 
package fr.liglab.adele.cube.archetype.designer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeListener;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeServiceListener;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;

public class AddPropertyHandler  implements ArchetypeServiceListener, ArchetypeListener{
	
	@Inject
	IArchetypeService arch;
	
	@Inject
	MApplication application;
	
	@Inject
	EModelService mservice;
	
	MToolItem me;
	
	Archetype archetype;
	
	@PostConstruct
	public void configure() {
		MUIElement mm = mservice.find("archetypedesigner.addpropertyhandler", application);
		if (mm instanceof MToolItem) {
			me = (MToolItem)mm;
		}
		if (this.arch != null) {
			this.arch.addListener(this);
		}	else {
			
		}
			

	}
	
	@Execute
	public void execute() {
		
		if (me.isSelected() == true) {			
			this.archetype.setAddingProperty(true);
		} else {
			System.out.println("agg goal is already selected!");
			this.archetype.setAddingProperty(false);
		}
	}
	
	
	@CanExecute
	public boolean canExecute() {		
		return this.arch != null && this.arch.getCurrentArchetype() != null && this.arch.getCurrentArchetype().getSelectedObject() != null
				&& this.arch.getCurrentArchetype().getSelectedObject() instanceof ElementDescription
				&& this.arch.getCurrentArchetype().isAddingGoal() == false
				;			
	}

	@Override
	public void notify(int event, Object oldObj, Object newObj) {
		if (event == ArchetypeServiceListener.CURRENT_ARCHETYPE_CHANGED) {
			if (this.archetype != null) this.archetype.removeListener(this);
			if (newObj != null) {				
				this.archetype = (Archetype)newObj;
				this.archetype.addListener(this);
			}
			else { 
				this.archetype = null; 
			}
		}
		if (event == ArchetypeListener.SELECTED_CHANGED ) {
			
			me.setSelected(false);
		}
	}
		
}