 
package fr.liglab.adele.cube.archetype.designer.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeListener;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeServiceListener;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class ValuePart implements ArchetypeServiceListener , ArchetypeListener {
	
	@Inject
	IArchetypeService arch;
	
	private Text txtValue;
	
	@Inject
	public ValuePart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		Label lblValue = new Label(parent, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblValue.setText("Value:");
		
		txtValue = new Text(parent, SWT.BORDER);
		txtValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null && arch.getCurrentArchetype().getSelectedObject() != null
						&& arch.getCurrentArchetype().getSelectedObject() instanceof ElementValue) {
					if (!((ElementValue)arch.getCurrentArchetype().getSelectedObject()).getValue().equalsIgnoreCase(txtValue.getText())){
						((ElementValue)arch.getCurrentArchetype().getSelectedObject()).setValue(txtValue.getText());	
					}					
				}
					
			}
		});
		txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));	
		
		this.arch.addListener(this);
	}
	
	@Focus
	public void getFocus() {
		txtValue.setFocus();
	}

	@Override
	public synchronized void notify(int event, Object oldObj, Object newObj) {
		if (event == ArchetypeServiceListener.CURRENT_ARCHETYPE_CHANGED) {
			if (oldObj != null && oldObj instanceof Archetype) {
				((Archetype)oldObj).removeListener(this);
			}
			if (newObj != null && newObj instanceof Archetype) {				
				((Archetype)newObj).addListener(this);							
			} else {	
				
			}
		}
		if (event == ArchetypeListener.SELECTED_CHANGED) {
			
			if (newObj != null && newObj instanceof ElementValue) {
				txtValue.setText(((ElementValue)newObj).getValue());
			}
		}
	}
	
	
}