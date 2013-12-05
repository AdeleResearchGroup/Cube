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

package fr.liglab.adele.cube.extensions.rm.monitoring.monitorsExecutors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.*;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Reference;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Scope;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
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

import java.util.Map;

public class GuiMonitorPrefuse extends JFrame {


    private static final String GRAPH = "graph";
    private static final String NODES = "graph.nodes";
    private static final String EDGES = "graph.edges";

    private static final String ID = "id";
    private static final String LABEL = "name";
    private static final String DESCRIPTION = "descr";
    private static final String STATE = "state";

	private static final String RUBBER_BAND = "rubberband";
	//private static final String NODES = NODES;

	private Graph graph;
	private Display display;
    private Visualization vis;

	private Map<String, Node> nodes = new HashMap<String, Node>();



	private JPanel panel;
	
	AutonomicManager cubeInstance;
	
	public GuiMonitorPrefuse(AutonomicManager ci) {
		this.cubeInstance = ci;

        // visualization
        vis = new Visualization();

        // graph data structure
        initGraph();
        vis.add(GRAPH, graph);

        // renderers
        initRenderers();

        // colors
        initActions();

        display = buildGraphDisplay();
		display.addControlListener(new ToolTipControl(ID));
        display.addControlListener(new FinalControlListener());

        initGui();
		
	}
	
	synchronized void addNode(final ManagedElement ci) {
        try {
            if (ci != null && ci.getState() != ManagedElement.UNMANAGED) {
                if (this.nodes.get(ci.getUri().toString()) == null) {
                    final Node N = graph.addNode();

                    String name = ci.getName();
                    N.set(ID, ci.getUUID());
                    if (ci.getName().equalsIgnoreCase(Component.NAME))
                        N.set(LABEL, "Comp ("+((Component)ci).getComponentType() + ")");
                    else if (ci.getName().equalsIgnoreCase(fr.liglab.adele.cube.extensions.core.model.Node.NAME))
                        N.set(LABEL, "Node ("+((fr.liglab.adele.cube.extensions.core.model.Node)ci).getNodeType() + ")");
                    else if (ci.getName().equalsIgnoreCase(Scope.NAME))
                        N.set(LABEL, "Scope ("+((Scope)ci).getScopeId() + ")");
                    else
                        N.set(LABEL, ci.getName());
                    N.set(DESCRIPTION, ci.getHTMLDocumentation());
                    N.set(STATE, ci.getState());
                    //synchronized (nodes){
                        nodes.put(ci.getUUID(), N);
                    //}
                    for (Reference r : ci.getReferences()) {
                        for (String ref : r.getReferencedElements()) {
                            //synchronized (nodes){
                                Node n2 = this.nodes.get(ref);
                                if (n2 != null) {
                                    graph.addEdge(N, n2);
                                }
                            //}
                        }
                    }
                }
            } else {

            }
        } catch(Exception ex) {
            System.out.println("..... pb .....");
        }
	}

    private void initGraph() {
        try {
            graph = new Graph(true);
            graph.addColumn(ID, String.class);
            graph.addColumn(LABEL, String.class);
            graph.addColumn(DESCRIPTION, String.class);
            graph.addColumn(STATE, int.class);
        } catch(Exception ex) {
            System.out.println("..... pb .....");
        }
    }

    private void initRenderers() {
        try {
            // standard labelRenderer for the given label
            LabelRenderer nodeRenderer = new LabelRenderer(LABEL);
            nodeRenderer.setRoundedCorner(8, 8); // round the corners
            nodeRenderer.setHorizontalPadding(8);
            // rendererFactory for the visualization items.
            DefaultRendererFactory drf = new DefaultRendererFactory();
            // set the labelRenderer
            drf.setDefaultRenderer(nodeRenderer);

            EdgeRenderer edgeRenderer = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_LINE,
                    Constants.EDGE_ARROW_NONE);

            drf.add(new InGroupPredicate(EDGES), edgeRenderer);

            vis.setRendererFactory(drf);
        } catch(Exception ex) {
            System.out.println("..... pb .....");
        }
    }

    private void initActions() {

        try {
            // Color Actions for nodes
            ColorAction nodeText = new ColorAction(NODES, VisualItem.TEXTCOLOR);


            //nodeText.setDefaultColor(ColorLib.gray(255));
            ColorAction nodeStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
            nodeStroke.setDefaultColor(ColorLib.rgb(60,63,65));

            /*
            ColorAction nodeFillColor = new ColorAction(NODES, VisualItem.FILLCOLOR, ColorLib.color(Color.YELLOW));
            nodeFillColor.add("_hover", ColorLib.rgb(220, 200, 200));
            nodeFillColor.add(VisualItem.HIGHLIGHT, ColorLib.rgb(220, 220, 0));
              */

            // Color Actions for edges
            ColorAction edgeLineColor = new ColorAction(EDGES, VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));
            edgeLineColor.add("_hover", ColorLib.rgb(220, 100, 100));
            ColorAction edgeArrowColor = new ColorAction(EDGES, VisualItem.FILLCOLOR, ColorLib.rgb(100, 100, 100));
            edgeArrowColor.add("_hover", ColorLib.rgb(220, 100, 100));


            // create our nominal color palette
            // pink for females, baby blue for males
            int[] palette = new int[] {
                    ColorLib.rgb(209,103,90), ColorLib.rgb(129,172,119)
                    /*ColorLib.rgb(255,200,125), ColorLib.rgb(200,200,255),
                    ColorLib.rgb(255,100,100), ColorLib.rgb(209,103,90)*/
            };
            // map nominal data values to colors using our provided palette
            DataColorAction fill = new DataColorAction(NODES, STATE,
                    Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
            //fill.add("_hover", ColorLib.rgb(220, 200, 200));
            //fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(220, 220, 0));

            // draw
            ActionList draw = new ActionList();
            draw.add(new RepaintAction());
            //draw.add(fill);
            draw.add(nodeText);
            //draw.add(nodeFillColor);
            draw.add(fill);
            draw.add(nodeStroke);
            draw.add(edgeLineColor);
            draw.add(edgeArrowColor);


            // -------- DataSizeAction
            //DataColorAction nodeDataColorAction = new DataColorAction(NODES, SIZE, Constants.NUMERICAL, VisualItem.FILLCOLOR, ColorLib.getInterpolatedPalette(ColorLib.rgb(200,0, 0), ColorLib.rgb(0,0, 200)));
            //draw.add(nodeDataColorAction);

            //vis.putAction("draw", draw);

            ActionList layout = new ActionList(Activity.INFINITY);
            layout.add(new ForceDirectedLayout(GRAPH, true));
            layout.add(new RepaintAction());
            layout.add(draw);



            //vis.putAction("draw", draw);
            vis.putAction("layout", layout);
            //vis.start("draw");

            vis.run("layout");
        } catch(Exception ex) {
            System.out.println("..... pb .....");
        }
    }


	private Display buildGraphDisplay() {
        Display d = new Display(vis);
        try {
            d.pan(400, 300);
            d.zoom(new Point2D.Double(400, 300), 1.25);

            d.addControlListener(new ZoomToFitControl(
                    prefuse.controls.Control.MIDDLE_MOUSE_BUTTON));
            d.addControlListener(new ZoomControl());
            d.setSize(720, 500);
            d.addControlListener(new DragControl());
            d.addControlListener(new PanControl());
            d.addControlListener(new WheelZoomControl());
        } catch(Exception ex) {
            System.out.println("..... pb .....");
        }
		return d;
	}

	public synchronized void updateGraph() {
		//System.out.println("udpdating graph..");
        try {
            nodes.clear();
            nodes = new Hashtable();
            graph.clear();


            for (ManagedElement i : this.cubeInstance.getRuntimeModelController().getRuntimeModel().getElements()) {
                this.addNode(i);
            }

            if (this.cubeInstance != null) {
                setTitle(this.cubeInstance.getUri().toString());

            }
        } catch(Exception ex) {
            System.out.println("..... pb .....");
        }
	}
	
	private void initGui() {
		label = new JButton("Refresh View");
		if (this.cubeInstance != null) {
			setTitle(this.cubeInstance.getUri().toString());

		} else {
			setTitle("Runtime Model Monitor");
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
	JButton label = null;
	
}
