package fr.liglab.adele.cube.archetype.designer.parts;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import fr.liglab.adele.cube.archetype.designer.editor.ArchetypeZestContentProvider;
import fr.liglab.adele.cube.archetype.designer.editor.ArchetypeZestLabelProvider;
import fr.liglab.adele.cube.archetype.designer.editor.ElementFilter;
import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeListener;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeServiceListener;
import fr.liglab.adele.cube.archetype.designer.model.DescriptionProperty;
import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.GoalProperty;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import fr.liglab.adele.cube.archetype.designer.model.Property;
import fr.liglab.adele.cube.archetype.designer.parser.ArchetypeParser;

public class DiagramPart implements ArchetypeServiceListener, ArchetypeListener {

	private GraphViewer viewer;
	
	@Inject	
	private IArchetypeService arch;
	
	@Inject
	private MDirtyable dirty;
	
	@Inject
	EPartService partService;
	
	private int layout = 1;
		
	MPart mparch ;
	MPart mpvalue ;
	MPart mpelement ;
	MPart mpproperty ;
	MPart mpgoal ;
	
	@PostConstruct
	public void postConstruct(Composite parent) {	
	
		this.arch.addListener(this);
		
		/*
		if (this.arch.getCurrentArchetype() == null) {
			this.archetype = this.arch.newArchetype();		
			this.arch.setCurrentArchetype(this.archetype);
		}
		*/
		
		this.viewer = new GraphViewer(parent, SWT.NONE);		
		this.viewer.setContentProvider(new ArchetypeZestContentProvider());		
		Color blue = viewer.getGraphControl().LIGHT_BLUE_CYAN;
		Color grey = viewer.getGraphControl().GREY_BLUE;		
		this.viewer.setLabelProvider(new ArchetypeZestLabelProvider(blue, grey));				
		//this.viewer.setInput(this.archetype); 						
		LayoutAlgorithm layout = setLayout();
	    viewer.setLayoutAlgorithm(layout, true);
	    viewer.applyLayout();
	    
	    ElementFilter filter = new ElementFilter();
	    ViewerFilter[] filters = new ViewerFilter[1];
	    filters[0]= filter; 
	    viewer.setFilters(filters);
	    
	    viewer.getGraphControl().addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(SelectionEvent e) {	    	  	    	  	
	    	  if (arch.getCurrentArchetype() != null) {
		    	  if (viewer.getGraphControl().getSelection().size()>0) {	    		  
		    		  if (viewer.getGraphControl().getSelection().get(0) instanceof GraphNode) {	    			  
		    			  GraphNode node = (GraphNode)viewer.getGraphControl().getSelection().get(0);
		    			  arch.getCurrentArchetype().setSelectedObject((Element)node.getData());
		    		  } else if (viewer.getGraphControl().getSelection().get(0) instanceof GraphConnection) {	    			  
		    			  GraphConnection connection = (GraphConnection)viewer.getGraphControl().getSelection().get(0);	    			  
		    			  arch.getCurrentArchetype().setSelectedObject((Property)connection.getData());
		    		  } else {
		    			  
		    		  }
		    	  } else {
		    		  arch.getCurrentArchetype().setSelectedObject(null);	    		  
		    	  }
	    	  }
	    	  
	      }

	    });
	
		mparch = partService.findPart("archetypedesigner.part.archetype");
		partService.activate(mparch);
		mparch.setVisible(false);
		mpvalue = partService.findPart("archetypedesigner.part.value");
		partService.activate(mpvalue);
		mpvalue.setVisible(false);
		mpelement = partService.findPart("archetypedesigner.part.element");
		partService.activate(mpelement);
		mpelement.setVisible(false);
		mpproperty = partService.findPart("archetypedesigner.part.property");
		partService.activate(mpproperty);
		mpproperty.setVisible(false);
		mpgoal = partService.findPart("archetypedesigner.part.goal");
		partService.activate(mpgoal);
		mpgoal.setVisible(false);
		
	}



	@Override
	public synchronized void notify(int event, Object oldObj, Object newObj) {
		if (event == ArchetypeServiceListener.CURRENT_ARCHETYPE_CHANGED) {
			
			if (oldObj != null && oldObj instanceof Archetype) {
				((Archetype)oldObj).removeListener(this);
			} 
			if (newObj != null && newObj instanceof Archetype) {
				
				((Archetype)newObj).addListener(this);
				if (viewer != null) {
					viewer.setInput(((Archetype)newObj));
					refresh();
				}
				mparch.setVisible(true);
				mpvalue.setVisible(false);
				mpelement.setVisible(false);
				mpproperty.setVisible(false);
				mpgoal.setVisible(false);
				partService.activate(mparch, true);
				
			} else {
				/*
				mparch.setVisible(false);
				mpvalue.setVisible(false);
				mpelement.setVisible(false);
				mpproperty.setVisible(false);
				mpgoal.setVisible(false);
				*/
			}
		}
		if (event == ArchetypeListener.NEW_OBJECT)  {		
			dirty.setDirty(true);		
			refresh();
		}
		if (event == ArchetypeListener.DELETED_OBJECT) {
			dirty.setDirty(true);		
			refresh();
		}
		if (event == ArchetypeListener.OBJECT_UPDATED) {
			dirty.setDirty(true);		
			refresh();
		}
		if (event == ArchetypeListener.SELECTED_CHANGED) {
			
			if (this.viewer != null) {		
				if (newObj != null) {
					GraphItem[] items = new GraphItem[1];
					if (newObj instanceof Element) {
						for (Object gn : this.viewer.getGraphControl().getNodes()) {
							if  ((((GraphNode)gn).getData()) == newObj) {
								items[0] = ((GraphNode)gn);
								this.viewer.getGraphControl().setSelection(items);
							}					
						}		
					} else if (newObj instanceof Property) {
						for (Object gn : this.viewer.getGraphControl().getConnections()) {							
							if  ((((GraphConnection)gn).getData()) == newObj) {								
								items[0] = (GraphConnection)gn;
								this.viewer.getGraphControl().setSelection(items);
							}					
						}	
					}
				}						
			}
			
			if (newObj == null) {
				mparch.setVisible(true);				
				mpvalue.setVisible(false);
				mpelement.setVisible(false);
				mpproperty.setVisible(false);
				mpgoal.setVisible(false);
				partService.showPart(mparch, PartState.ACTIVATE);
				
			} else {
				if (newObj instanceof ElementValue) {					
					mparch.setVisible(false);				
					mpvalue.setVisible(true);
					mpelement.setVisible(false);
					mpproperty.setVisible(false);
					mpgoal.setVisible(false);
					partService.showPart(mpvalue, PartState.ACTIVATE);				
				} else if (newObj instanceof ElementDescription) {
					mparch.setVisible(false);				
					mpvalue.setVisible(false);
					mpelement.setVisible(true);
					mpproperty.setVisible(false);
					mpgoal.setVisible(false);
					partService.showPart(mpelement, PartState.ACTIVATE);
				} else if (newObj instanceof DescriptionProperty) {
					mparch.setVisible(false);				
					mpvalue.setVisible(false);
					mpelement.setVisible(false);
					mpproperty.setVisible(true);
					mpgoal.setVisible(false);
					partService.showPart(mpproperty, PartState.ACTIVATE);
				} else if (newObj instanceof GoalProperty) {
					mparch.setVisible(false);				
					mpvalue.setVisible(false);
					mpelement.setVisible(false);
					mpproperty.setVisible(false);
					mpgoal.setVisible(true);
					partService.showPart(mpgoal, PartState.ACTIVATE);
				}
			}
			
		}
		
		
	}
	
	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		return layout;
	}
	
	
	public void setLayoutManager() {
	    switch (layout) {
	    case 1:
	      this.viewer.getGraphControl().setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	      layout++;
	      break;
	    case 2:
	    	this.viewer.getGraphControl().setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	      layout = 1;
	      break;
	    }
	    refresh();
	  }
	
	@Focus
	public void setFocus() {
		//this.arch.setCurrentArchetype(this.archetype);		
		//this.viewer.refresh();
	}
	
	static int index = 1;
	@Persist
	public void save(Shell shell) {
		if (this.arch.getCurrentArchetype() != null) {
			String filename = this.arch.getCurrentArchetype().getFilepath();
			if (filename == null) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);				
			    dialog.setFilterExtensions(new String[] { "*.arch", "*.xml" }); 
			    dialog.setFileName("file"+ index++ +".arch");
			    filename = dialog.open();			    
			} else {
				
			}
			if (filename != null) {
		    	try {
		    		String archText = ArchetypeParser.toXmlString(this.arch.getCurrentArchetype());
		    		//System.out.println(archText);
					PrintWriter out = new PrintWriter(filename);
					out.println(archText);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		    	this.arch.getCurrentArchetype().setFilepath(filename);	
		    	
		    }
		}
		dirty.setDirty(false);
	}

	public void refresh() {		
		if (this.viewer != null) {				
			this.viewer.refresh();
		}
		for (Property p :this.arch.getCurrentArchetype().getProperties()) {
			
		}
	}

}
