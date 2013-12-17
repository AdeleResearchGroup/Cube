 
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
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class ArchetypePart implements ArchetypeServiceListener{
	
	@Inject
	IArchetypeService arch;
		
	private Text txtId;
	private Text txtVersion;
	private Text txtDoc;
	private Text txtCubeVersion;
	
	@Inject
	public ArchetypePart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		Label lblId = new Label(parent, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setText("Id:");
		
		txtId = new Text(parent, SWT.BORDER);
		txtId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null) {
					((Archetype)arch.getCurrentArchetype()).setId(txtId.getText());
				}
			}
		});
		txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblVersion = new Label(parent, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVersion.setText("Version:");
		
		txtVersion = new Text(parent, SWT.BORDER);
		txtVersion.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null) {
					((Archetype)arch.getCurrentArchetype()).setVersion(txtVersion.getText());
				}
			}
		});
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDoc = new Label(parent, SWT.NONE);
		lblDoc.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDoc.setText("Doc:");
		
		txtDoc = new Text(parent, SWT.BORDER);
		txtDoc.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null) {
					((Archetype)arch.getCurrentArchetype()).setArchetypeDescription(txtDoc.getText());
				}
			}
		});
		txtDoc.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Label lblCube = new Label(parent, SWT.NONE);
		lblCube.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCube.setText("Cube:");
		
		txtCubeVersion = new Text(parent, SWT.BORDER);
		txtCubeVersion.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null) {
					((Archetype)arch.getCurrentArchetype()).setCubeVersion(txtCubeVersion.getText());
				}
			}
		});
		txtCubeVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
				
			}
			if (newObj != null && newObj instanceof Archetype) {								
				txtId.setText(((Archetype)newObj).getId());
				txtVersion.setText(((Archetype)newObj).getVersion());
				txtCubeVersion.setText(((Archetype)newObj).getCubeVersion());
				if (((Archetype)newObj).getArchetypeDescription() != null) txtDoc.setText(((Archetype)newObj).getArchetypeDescription());
			} else {	
				txtId.setText("");				
			}
		}
	}
	
	
	
	
}