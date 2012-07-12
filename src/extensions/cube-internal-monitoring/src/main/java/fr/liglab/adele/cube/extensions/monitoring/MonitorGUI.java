/*
 * Copyright 2011 Adele Team LIG (http://www-adele.imag.fr/)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.liglab.adele.cube.extensions.monitoring;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import fr.liglab.adele.cube.agent.CInstance;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.model.ComponentInstance;
import fr.liglab.adele.cube.extensions.core.model.ComponentType;
import fr.liglab.adele.cube.extensions.core.model.NodeInstance;
import fr.liglab.adele.cube.extensions.core.model.NodeType;
import fr.liglab.adele.cube.extensions.core.model.ScopeInstance;
import fr.liglab.adele.cube.extensions.core.model.ScopeType;
import fr.liglab.adele.cube.util.id.CInstanceUID;

public class MonitorGUI extends JFrame {
	
	private static final String graphNodesAndEdges = "graph";
	private static final String graphNodes = "graph.nodes";
	private static final String graphEdges = "graph.edges";
	private static final String RUBBER_BAND = "rubberband";
	private static final String NODES = graphNodes;

	private Graph graph;
	private Display display;
	private Dictionary nodes = new Hashtable();
	private String selectedMediator = "";

	private LabelRenderer m_nodeRenderer;
	private EdgeRenderer m_edgeRenderer;
	private JPanel panel;
	
	CubeAgent cubeInstance;
	
	public MonitorGUI(CubeAgent ci) {
		this.cubeInstance = ci;
		display = buildGraphDisplay();
		display.addControlListener(new ToolTipControl("uri"));
		initGui();
		
		
	}
	
	synchronized void addComponent(final CInstance ci) {
		// System.out.println("[CubeGui] addComponent: ci=" + ci);
				
		if (ci != null) {
			if (this.nodes.get(ci.getId().toString()) == null) {
				final Node N = graph.addNode();			
				String name = ci.getId().getURI();
				name = name.substring(name.lastIndexOf("/")+1);
				N.set("name", ci.getCType().getId() + "(" + name + ")");
				N.set("uri", ci.getId().toString());
				for (CInstanceUID id : ((ComponentInstance)ci).getOutComponents()) {
					if (this.nodes.get(id.getURI()) != null) {
						Node n2 = (Node) this.nodes.get(id.getURI());
						graph.addEdge(N, n2);
					} else {
						if (id.isLocal(cubeInstance.getId()) == false) {
							final Node RN = graph.addNode();																	
							RN.set("name", ".");
							RN.set("uri", id.toString());
							nodes.put(id.toString(), RN);
							graph.addEdge(N, RN);
						}
						
					}
				}
				for (CInstanceUID id : ((ComponentInstance)ci).getInComponents()) {
					if (this.nodes.get(id.getURI()) != null) {
						Node n1 = (Node) this.nodes.get(id.getURI());
						graph.addEdge(n1, N);
					} else {
						if (id.isLocal(cubeInstance.getId()) == false) {
							final Node RN = graph.addNode();																	
							RN.set("name", ".");
							RN.set("uri", id.toString());
							nodes.put(id.toString(), RN);
							graph.addEdge(RN, N);
						}
					}
				}
				/*
				ci.addComponentListener(new ComponentListener() {
					public void onUpdatedProperty(PropertyEvent event) {
						if (event.getProperty().equalsIgnoreCase("id")) {
							Object target = event.getTarget();
							if (target instanceof Component) {
								Component c = (Component) target;
								if (c.isLocal()) {
									N.set("name", event.getNewValue() + "\n["
											+ c.getComponentType().getName() + "]");
								} else {
									N.set("name",
											c.getNode()
													+ "."
													+ event.getNewValue()
													+ "\n["
													+ c.getComponentType()
															.getName() + "]");
								}
							}
						}
					}
				});*/
				
				nodes.put(ci.getId().toString(), N);
				//for (Association asso : ci.getAssociations()) {
				//	addAssociation(asso);
				//}
			}
		} else {
			// System.out
			// .println("[ERROR] CubeLocalMonitorGui: addComponent, ci null!");
		}
	}
	
	private Display buildGraphDisplay() {
		graph = new Graph(true);
		graph.addColumn("name", String.class);
		graph.addColumn("uri", String.class);

		// initData(null);

		Visualization vis = new Visualization();
		vis.add("graph", graph);

		m_nodeRenderer = new LabelRenderer("name");
		m_nodeRenderer.setRoundedCorner(8, 8); // round the corners

		m_edgeRenderer = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_LINE,
				prefuse.Constants.EDGE_ARROW_FORWARD);

		DefaultRendererFactory drf = new DefaultRendererFactory(m_nodeRenderer);
		drf.add(new InGroupPredicate(graphEdges), m_edgeRenderer);

		vis.setRendererFactory(drf);

		ColorAction nodeTextColor = new ColorAction(graphNodes,
				VisualItem.TEXTCOLOR);
		ColorAction nodeFillColor = new ColorAction(graphNodes,
				VisualItem.FILLCOLOR, ColorLib.color(Color.YELLOW));
		nodeFillColor.add("_hover", ColorLib.rgb(220, 200, 200));
		nodeFillColor.add(VisualItem.HIGHLIGHT, ColorLib.rgb(220, 220, 0));
		ColorAction nodeStrokeColor = new ColorAction("graph.nodes",
				VisualItem.STROKECOLOR, ColorLib.color(Color.RED));

		ColorAction edgeLineColor = new ColorAction(graphEdges,
				VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));
		edgeLineColor.add("_hover", ColorLib.rgb(220, 100, 100));
		ColorAction edgeArrowColor = new ColorAction(graphEdges,
				VisualItem.FILLCOLOR, ColorLib.rgb(100, 100, 100));
		edgeArrowColor.add("_hover", ColorLib.rgb(220, 100, 100));

		// recolor
		ActionList recolor = new ActionList();
		recolor.add(nodeTextColor);
		recolor.add(nodeFillColor);
		recolor.add(nodeStrokeColor);
		recolor.add(edgeLineColor);
		recolor.add(edgeArrowColor);
		vis.putAction("recolor", recolor);

		ActionList layout = new ActionList(Activity.INFINITY);
		layout.add(new ForceDirectedLayout("graph", true));
		layout.add(new RepaintAction());
		layout.add(recolor);
		vis.putAction("layout", layout);

		vis.run("layout");

		Display d = new Display(vis);

		d.pan(400, 300);
		d.zoom(new Point2D.Double(400, 300), 1.25);

		d.addControlListener(new ZoomToFitControl(
				prefuse.controls.Control.MIDDLE_MOUSE_BUTTON));
		d.addControlListener(new ZoomControl());
		d.setSize(720, 500);
		d.addControlListener(new DragControl());
		d.addControlListener(new PanControl());
		d.addControlListener(new WheelZoomControl());				
		
		return d;
	}

	protected void updateGraph() {
		System.out.println("udpdating graph..");
		nodes = new Hashtable();
		graph.clear();

		for (CInstance i : this.cubeInstance.getRuntimeModel().getCInstances(CoreExtensionFactory.ID, ComponentType.NAME, CInstance.VALID)) {
			this.addComponent(i);
		}
		
		if (this.cubeInstance != null) {
			setTitle(this.cubeInstance.getId().toString());
			String id="";
			String type="";
			String sid="";
			String stype="";
			List<CInstance> nodes = this.cubeInstance.getRuntimeModel().getCInstances(CoreExtensionFactory.ID, NodeType.NAME, CInstance.VALID);
			if (nodes != null && nodes.size()>0 && nodes.get(0) instanceof NodeInstance) {
				id = ((NodeInstance)nodes.get(0)).getLocalId();
				type = ((NodeInstance)nodes.get(0)).getCType().getId();				
			}
			List<CInstance> scopes = this.cubeInstance.getRuntimeModel().getCInstances(CoreExtensionFactory.ID, ScopeType.NAME, CInstance.VALID);
			if (scopes != null && scopes.size()>0 && scopes.get(0) instanceof ScopeInstance) {
				sid = ((ScopeInstance)scopes.get(0)).getLocalId();
				stype = ((ScopeInstance)scopes.get(0)).getCType().getId();				
			}
			label.setText(id + ":" + type + " (" + sid + ":" + stype+ ") - " + this.cubeInstance.getId().toString() + ")");
		}
	}
	
	private void initGui() {
		label = new JLabel();
		if (this.cubeInstance != null) {
			setTitle(this.cubeInstance.getId().toString());
			String id="";
			String type="";
			String sid="";
			String stype="";
			List<CInstance> nodes = this.cubeInstance.getRuntimeModel().getCInstances(CoreExtensionFactory.ID, NodeType.NAME, CInstance.VALID);
			if (nodes != null && nodes.size()>0 && nodes.get(0) instanceof NodeInstance) {
				id = ((NodeInstance)nodes.get(0)).getLocalId();
				type = ((NodeInstance)nodes.get(0)).getCType().getId();				
			}
			List<CInstance> scopes = this.cubeInstance.getRuntimeModel().getCInstances(CoreExtensionFactory.ID, ScopeType.NAME, CInstance.VALID);
			if (scopes != null && scopes.size()>0 && scopes.get(0) instanceof ScopeInstance) {
				sid = ((ScopeInstance)scopes.get(0)).getLocalId();
				stype = ((ScopeInstance)scopes.get(0)).getCType().getId();				
			}
			label.setText(id + ":" + type + " (" + sid + ":" + stype+ ") - " + this.cubeInstance.getId().toString() + ")");
		} else {
			setTitle("Runtime Model Monitor");
			label.setText("Runtime Model Monitor");	
		}
		//label = new JLabel("Hello!");	
		label.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
				updateGraph();
			}
		});
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(500, 300));
		getContentPane().add(panel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//setPreferredSize(new Dimension(400, 300));
		setSize(496, 410);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(label, BorderLayout.NORTH);
		getContentPane().add(this.display, BorderLayout.CENTER);
	}
	JLabel label = null;
	
}
