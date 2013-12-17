 
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
import org.eclipse.swt.widgets.Button;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeListener;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeServiceListener;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ElementPart implements ArchetypeServiceListener , ArchetypeListener  {
	private Text txtNS;
	private Text txtName;
	private Text txtId;
	private Button btnShowProperties;
	@Inject
	IArchetypeService arch;
	
	@Inject
	public ElementPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		Label lblNs = new Label(parent, SWT.NONE);
		lblNs.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNs.setText("NS:");
		
		txtNS = new Text(parent, SWT.BORDER);
		txtNS.setEnabled(false);
		txtNS.setEditable(false);
		txtNS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		txtName = new Text(parent, SWT.BORDER);
		txtName.setEditable(false);
		txtName.setEnabled(false);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblId = new Label(parent, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setText("Id:");
		
		txtId = new Text(parent, SWT.BORDER);
		txtId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null && arch.getCurrentArchetype().getSelectedObject() != null
						&& arch.getCurrentArchetype().getSelectedObject() instanceof ElementDescription) {
					if (!((ElementDescription)arch.getCurrentArchetype().getSelectedObject()).getId().equalsIgnoreCase(txtId.getText())) {
						((ElementDescription)arch.getCurrentArchetype().getSelectedObject()).setId(txtId.getText());	
					}					
				}
			}
		});
		txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDetail = new Label(parent, SWT.NONE);
		lblDetail.setText("Detail:");
		
		btnShowProperties = new Button(parent, SWT.CHECK);
		btnShowProperties.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (arch.getCurrentArchetype() != null && arch.getCurrentArchetype().getSelectedObject() != null
						&& arch.getCurrentArchetype().getSelectedObject() instanceof ElementDescription) {
					if (((ElementDescription)arch.getCurrentArchetype().getSelectedObject()).isShowProperties() != btnShowProperties.getSelection()) {
						((ElementDescription)arch.getCurrentArchetype().getSelectedObject()).setShowProperties(btnShowProperties.getSelection());	
					}					
				}
			}
		});
		btnShowProperties.setSelection(true);
		btnShowProperties.setText("show properties");
		//TODO Your code here
		
		this.arch.addListener(this);
	}
	
	@Focus
	public void getFocus() {
		txtId.setFocus();
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
			
			if (newObj != null && newObj instanceof ElementDescription) {
				txtNS.setText(((ElementDescription)newObj).getNamespace());
				txtName.setText(((ElementDescription)newObj).getName());
				txtId.setText(((ElementDescription)newObj).getId());
				btnShowProperties.setSelection(((ElementDescription)newObj).isShowProperties());
				if (((ElementDescription)newObj).getProperties().size() == 0) {
					btnShowProperties.setEnabled(false);
				} else {
					btnShowProperties.setEnabled(true);
				}
			}
		}
	}
}