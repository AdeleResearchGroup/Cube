 
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
import org.eclipse.swt.widgets.Combo;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeListener;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeServiceListener;
import fr.liglab.adele.cube.archetype.designer.model.DescriptionProperty;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class PropertyPart implements ArchetypeServiceListener , ArchetypeListener  {
	
	private Text txtNS;
	private Combo cbName;
	
	@Inject
	IArchetypeService arch;
		
	@Inject
	public PropertyPart() {
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
		
		cbName = new Combo(parent, SWT.NONE);
		cbName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (arch.getCurrentArchetype() != null && arch.getCurrentArchetype().getSelectedObject() != null
						&& arch.getCurrentArchetype().getSelectedObject() instanceof DescriptionProperty) {
					if (!((DescriptionProperty)arch.getCurrentArchetype().getSelectedObject()).getName().equalsIgnoreCase(cbName.getText())) {
						((DescriptionProperty)arch.getCurrentArchetype().getSelectedObject()).setName(cbName.getText());	
					}					
				}
			}
		});		
		cbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//TODO Your code here

		this.arch.addListener(this);
	}
	
	@Focus
	public void getFocus() {
		cbName.setFocus();
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
			
			if (newObj != null && newObj instanceof DescriptionProperty) {
				//new String[] {"Connected", "ControlledBy", "HasComponentId", "HasComponentType","HasMaxInputComponents","HasMaxInstancesPerAM","HasNode"
				//		,"HasNodeId", "HasNodeType", "HasProperty", "HasScopeId", "HasSourceComponent", "HoldComponent", "InScope","IsLocal", "LocatedIn", "OnNode"};
				
				txtNS.setText(((DescriptionProperty)newObj).getNamespace());
				String name = ((DescriptionProperty)newObj).getName();
				if (((ElementDescription)((DescriptionProperty)newObj).getSubject()).getName().equalsIgnoreCase("component")) {
					if (((DescriptionProperty)newObj).getObject() instanceof ElementValue) {
						cbName.setItems(new String[] {"HasProperty", "HasComponentId", "HasComponentType"
								, "HasMaxInputComponents", "HasNode", "IsLocal", "HasMaxInstancesPerAM", "LocatedIn"});
						if (name==null || name.equalsIgnoreCase("")) name="HasProperty";
					} else {
						if (((ElementDescription)((DescriptionProperty)newObj).getObject()).getName().equalsIgnoreCase("component")) {
							cbName.setItems(new String[] {"Connected", "HasSourceComponent"});
							if (name==null || name.equalsIgnoreCase("")) name="Connected";
						} else if (((ElementDescription)((DescriptionProperty)newObj).getObject()).getName().equalsIgnoreCase("node")) {
							cbName.setItems(new String[] {"OnNode"});
							if (name==null || name.equalsIgnoreCase("")) name="OnNode";
						}						
					}					
				} else if (((ElementDescription)((DescriptionProperty)newObj).getSubject()).getName().equalsIgnoreCase("node")) {
					if (((DescriptionProperty)newObj).getObject() instanceof ElementValue) {
						cbName.setItems(new String[] {"HasProperty", "HasNodeId", "HasNodeType"
								,"IsLocal", "HasMaxInstancesPerAM", "LocatedIn"});
						if (name==null || name.equalsIgnoreCase("")) name="HasProperty";
					} else {
						if (((ElementDescription)((DescriptionProperty)newObj).getObject()).getName().equalsIgnoreCase("component")) {
							cbName.setItems(new String[] {"HoldComponent"});	
							if (name==null || name.equalsIgnoreCase("")) name="HoldComponent";
						} else if (((ElementDescription)((DescriptionProperty)newObj).getObject()).getName().equalsIgnoreCase("scope")) {
							cbName.setItems(new String[] {"InScope"});
							if (name==null || name.equalsIgnoreCase("")) name="InScope";
						}						
					}					
				} else if (((ElementDescription)((DescriptionProperty)newObj).getSubject()).getName().equalsIgnoreCase("scope")) {
					if (((DescriptionProperty)newObj).getObject() instanceof ElementValue) {
						cbName.setItems(new String[] {"HasProperty", "HasScopeId"
								,"IsLocal", "HasMaxInstancesPerAM", "LocatedIn"});
						if (name==null || name.equalsIgnoreCase("")) name="HasProperty";
					} else {
						if (((ElementDescription)((DescriptionProperty)newObj).getObject()).getName().equalsIgnoreCase("master")) {
							cbName.setItems(new String[] {"ControlledBy"});	
							if (name==null || name.equalsIgnoreCase("")) name="ControlledBy";
						}						
					}					
				} else if (((ElementDescription)((DescriptionProperty)newObj).getSubject()).getName().equalsIgnoreCase("master")) {
					if (((DescriptionProperty)newObj).getObject() instanceof ElementValue) {
						cbName.setItems(new String[] {"LocatedIn", "IsLocal", "HasMaxInstancesPerAM"});
						if (name==null || name.equalsIgnoreCase("")) name="LocatedIn";
					} 
				}
				cbName.setText(name);
			}
		}
	}
	
	
	
	
}