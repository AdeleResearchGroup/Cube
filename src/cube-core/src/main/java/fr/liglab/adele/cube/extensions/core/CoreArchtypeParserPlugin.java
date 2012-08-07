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

package fr.liglab.adele.cube.extensions.core;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.archetype.GlobalConfig;
import fr.liglab.adele.cube.extensions.core.constraints.ComponentsPerNode;
import fr.liglab.adele.cube.extensions.core.constraints.Connect;
import fr.liglab.adele.cube.extensions.core.constraints.FindLocally;
import fr.liglab.adele.cube.extensions.core.constraints.InComponents;
import fr.liglab.adele.cube.extensions.core.constraints.InScope;
import fr.liglab.adele.cube.extensions.core.constraints.InScopeResolver;
import fr.liglab.adele.cube.extensions.core.constraints.OnNode;
import fr.liglab.adele.cube.extensions.core.constraints.OutComponents;
import fr.liglab.adele.cube.extensions.core.constraints.CreateLocally;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.extensions.core.model.Scope;
import fr.liglab.adele.cube.util.parser.ArchetypeParserPlugin;
import fr.liglab.adele.cube.util.parser.ParseException;
import fr.liglab.adele.cube.util.xml.XMLElement;

public class CoreArchtypeParserPlugin implements ArchetypeParserPlugin {

	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String EXTENDS = "extends";
	private static final String C1 = "c1";
	private static final String C2 = "c2";
	private static final String C = "c";
	private static final String V = "v";
	private static final String V1 = "v1";
	private static final String V2 = "v2";
	private static final String I1 = "i1";
	private static final String I2 = "i2";
	private static final String TYPE = "type";
	private static final String T = "t";
	private static final String N = "n";
	private static final String S = "s";
	private static final String I = "i";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String CARD = "card";
	private static final String URL = "url";
	private static final String PRIORITY = "priority";
	private static final String P = "p";
	private static final String SCOPETYPE = "scopetype";
	private static final String CTYPE = "ctype";
	private static final String TRIGGER = "trigger";
	private static final String MAXINCOMP = "max-incomp";



	public ManagedElement parseType(XMLElement e, Archetype archtype) throws ParseException {	
		if (e != null) {
			String ns = e.getNameSpace();
			if (ns == null || !ns.equalsIgnoreCase(CoreExtensionFactory.ID)) {
				throw new ParseException("namespace problem!");
			}
			String name = e.getName();			
			if (name == null) {
				throw new ParseException("name problem!");
			} else {
				if (name.equalsIgnoreCase(Component.NAME)) {
					/*
					 * Component
					 */
					String id = e.getAttribute(ID);
					String description = e.getAttribute(DESCRIPTION);						
					String parentId = e.getAttribute(EXTENDS);
					Component c = new Component(id, description, parentId, archtype);
					parseProperties(e, c);
					return c;

				} else if (name.equalsIgnoreCase(Node.NAME)) {
					/*
					 * Node
					 */
					String id = e.getAttribute(ID);
					String description = e.getAttribute(DESCRIPTION);			
					Node n = new Node(id, description, archtype);
					parseProperties(e, n);
					return n;
				} else if (name.equalsIgnoreCase(Scope.NAME)) {
					/*
					 * Scope
					 */
					String id = e.getAttribute(ID);
					String description = e.getAttribute(DESCRIPTION);			
					Scope s = new Scope(id, description, archtype);
					parseProperties(e, s);
					return s;
				}
			}
		}
		return null;
	}

	private void parseProperties(XMLElement e, ManagedElement me) throws ParseException {
		XMLElement[] xmlproperties = e.getElements(ManagedElement.PROPERTY);
		if (xmlproperties != null) {
			for (int i=0; i<xmlproperties.length; i++) {
				String pname = xmlproperties[i].getAttribute(ManagedElement.PROPERTY_NAME);
				String pvalue = xmlproperties[i].getAttribute(ManagedElement.PROPERTY_VALUE);
				if (pname != null && pvalue != null) {
					me.addProperty(pname, pvalue);
				} else {
					throw new ParseException("properties problem!");
				}
			}
		}
	}

	public Constraint parseConstraint(XMLElement e, Archetype archtype)
			throws ParseException {
		if (e != null) {
			String ns = e.getNameSpace();
			if (ns == null || !ns.equalsIgnoreCase(CoreExtensionFactory.ID)) {
				throw new ParseException("namespace problem!");
			}
			String name = e.getName();			
			if (name == null) {
				throw new ParseException("name problem!");
			} else {
				if (name.equalsIgnoreCase(Connect.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String c1 = e.getAttribute(V1);
					String c2 = e.getAttribute(V2);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					}   
					Connect connected = new Connect(c1, c2, id, description,  new Integer(priority).intValue(), archtype);
					return connected;
				} else if (name.equalsIgnoreCase(OnNode.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String c = e.getAttribute(V1);
					String n = e.getAttribute(V2);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					OnNode onNode = new OnNode(c, n, id, description,  new Integer(priority).intValue(), archtype);					
					return onNode;
				} else if (name.equalsIgnoreCase(InScope.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String c = e.getAttribute(V1);
					String n = e.getAttribute(V2);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					InScope inScope = new InScope(c, n, id, description,  new Integer(priority).intValue(), archtype);					
					return inScope;
				} else if (name.equalsIgnoreCase(FindLocally.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String i = e.getAttribute(V);					
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					FindLocally findLocaly = new FindLocally(i, id, description,  new Integer(priority).intValue(), archtype);									
					return findLocaly;
				}	else if (name.equalsIgnoreCase(CreateLocally.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String i = e.getAttribute(V);					
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					CreateLocally selfCreateLocally = new CreateLocally(i, id, description,  new Integer(priority).intValue(), archtype);									
					return selfCreateLocally;
				}	else if (name.equalsIgnoreCase(InComponents.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String v = e.getAttribute(V);					
					String max = e.getAttribute(MAX);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					InComponents inComponents = new InComponents(v, max, id, description,  new Integer(priority).intValue(), archtype);									
					return inComponents;
				}	else if (name.equalsIgnoreCase(OutComponents.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String v = e.getAttribute(V);					
					String max = e.getAttribute(MAX);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					OutComponents outComponents = new OutComponents(v, max, id, description,  new Integer(priority).intValue(), archtype);									
					return outComponents;
				}	else if (name.equalsIgnoreCase(ComponentsPerNode.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String v = e.getAttribute(V);					
					String type = e.getAttribute(TYPE);
					String max = e.getAttribute(MAX);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					ComponentsPerNode componentsPerNode = new ComponentsPerNode(v, type, max, id, description,  new Integer(priority).intValue(), archtype);									
					return componentsPerNode;
				}			
			}
		}
		return null;
	}


	/*
	public ConstraintResolver parseCPredicate(XMLElement e, CConstraint constraint, ConstraintResolver parent) throws ArchtypeParsingException {
		if (e != null) {
			String ns = e.getNameSpace();
			if (ns == null || !ns.equalsIgnoreCase(CoreController.NAMESPACE)) {
				throw new ArchtypeParsingException("namespace problem!");
			}
			String name = e.getName();			
			if (name == null) {
				throw new ArchtypeParsingException("name problem!");
			} else {
				if (name.equalsIgnoreCase(Connect.NAME)) {

					String c1 = e.getAttribute(C1);
					String c2 = e.getAttribute(C2);
					String priority = e.getAttribute(PRIORITY);
					if (priority != null) {
						Connect connected = new Connect(c1, c2, constraint, parent,  new Integer(priority).intValue());
						return connected;
					} else {
						Connect connected = new Connect(c1, c2, constraint, parent);
						return connected;
					}

				} else if (name.equalsIgnoreCase(InstancesPerNode.NAME)) {

					String type = e.getAttribute(TYPE);
					String n = e.getAttribute(N);
					String min = e.getAttribute(MIN);
					String max = e.getAttribute(MAX);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						InstancesPerNode ipn = new InstancesPerNode(n, type, new Integer(min), new Integer(max), constraint, parent);					
						return ipn;				
					} else {
						InstancesPerNode ipn = new InstancesPerNode(n, type, new Integer(min), new Integer(max), constraint, parent, new Integer(priority).intValue());					
						return ipn;	
					}
				} else if (name.equalsIgnoreCase(NodeInScope.NAME)) {

					String n = e.getAttribute(N);
					String s = e.getAttribute(S);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						NodeInScope nis = new NodeInScope(n, s, constraint, parent);					
						return nis;				
					} else {

					}
				} else if (name.equalsIgnoreCase(InComponents.NAME)) {

					String c = e.getAttribute(C);					
					String min = e.getAttribute(MIN);
					String max = e.getAttribute(MAX);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						InComponents incomps = new InComponents(c,new Integer(min), new Integer(max), constraint, parent);
						return incomps;
					} else {
						InComponents incomps = new InComponents(c, new Integer(min), new Integer(max), constraint, parent, new Integer(priority).intValue());
						return incomps;
					}

				} else if (name.equalsIgnoreCase(OutComponents.NAME)) {

					String c = e.getAttribute(C);
					String min = e.getAttribute(MIN);
					String max = e.getAttribute(MAX);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						OutComponents outcomps = new OutComponents(c, new Integer(min), new Integer(max), constraint, parent);
						return outcomps;		
					} else {
						OutComponents outcomps = new OutComponents(c, new Integer(min), new Integer(max), constraint, parent, new Integer(priority).intValue());
						return outcomps;	
					}
				} else if (name.equalsIgnoreCase(OnNode.NAME)) {

					String c = e.getAttribute(C);
					String n = e.getAttribute(N);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						OnNode onNode = new OnNode(c, n, constraint, parent);					
						return onNode;
					} else {
						OnNode onNode = new OnNode(c, n, constraint, parent, new Integer(priority).intValue());					
						return onNode;
					}

				} else if (name.equalsIgnoreCase(InScope.NAME)) {

					String n = e.getAttribute(N);
					String s = e.getAttribute(S);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						InScope onScope = new InScope(n, s, constraint, parent);					
						return onScope;
					} else {
						InScope onScope = new InScope(n, s, constraint, parent, new Integer(priority).intValue());					
						return onScope;
					}

				} else if (name.equalsIgnoreCase(InstancesPerCube.NAME)) {

					String i = e.getAttribute(I);					
					String min = e.getAttribute(MIN);
					String max = e.getAttribute(MAX);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						InstancesPerCube ipn = new InstancesPerCube(i, new Integer(min), new Integer(max), constraint, parent);
						return ipn;				
					} else {
						InstancesPerCube ipn = new InstancesPerCube(i, new Integer(min), new Integer(max), constraint, parent, new Integer(priority).intValue());
						return ipn;	
					}
				} else if (name.equalsIgnoreCase(NodesPerScope.NAME)) {

					String s = e.getAttribute(S);
					String n = e.getAttribute(N);					
					String min = e.getAttribute(MIN);
					String max = e.getAttribute(MAX);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						NodesPerScope ipn = new NodesPerScope(s, n, new Integer(min), new Integer(max), constraint, parent);
						return ipn;				
					} else {
						NodesPerScope ipn = new NodesPerScope(s, n, new Integer(min), new Integer(max), constraint, parent, new Integer(priority).intValue());
						return ipn;	
					}
				} else if (name.equalsIgnoreCase(SelfCreateLocaly.NAME)) {

					String i = e.getAttribute(I);					
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						SelfCreateLocaly incomps = new SelfCreateLocaly(i, constraint, parent);
						return incomps;
					} else {
						SelfCreateLocaly incomps = new SelfCreateLocaly(i, constraint, parent, new Integer(priority).intValue());
						return incomps;
					}

				} else if (name.equalsIgnoreCase(FindLocaly.NAME)) {

					String i = e.getAttribute(I);					
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						FindLocaly incomps = new FindLocaly(i, constraint, parent);
						return incomps;
					} else {
						FindLocaly incomps = new FindLocaly(i, constraint, parent, new Integer(priority).intValue());
						return incomps;
					}

				} else if (name.equalsIgnoreCase(Equal.NAME)) {

					String i1 = e.getAttribute(I1);
					String i2 = e.getAttribute(I2);					
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						Equal ipn = new Equal(i1, i2, constraint, parent);
						return ipn;				
					} else {
						Equal ipn = new Equal(i1, i2, constraint, parent, new Integer(priority).intValue());
						return ipn;	
					}
				} else if (name.equalsIgnoreCase(NotEqual.NAME)) {

					String i1 = e.getAttribute(I1);
					String i2 = e.getAttribute(I2);					
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						NotEqual ipn = new NotEqual(i1, i2, constraint, parent);
						return ipn;				
					} else {
						NotEqual ipn = new NotEqual(i1, i2, constraint, parent, new Integer(priority).intValue());
						return ipn;	
					}
				} else if (name.equalsIgnoreCase(ComponentsPerScope.NAME)) {

					String c = e.getAttribute(C);
					String st = e.getAttribute(SCOPETYPE);		
					String min = e.getAttribute(MIN);
					String max = e.getAttribute(MAX);
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						ComponentsPerScope ips = new ComponentsPerScope(c, st, new Integer(min), new Integer(max), constraint, parent);						
						return ips;				
					} else {
						ComponentsPerScope ips = new ComponentsPerScope(c, st, new Integer(min), new Integer(max), constraint, parent, new Integer(priority).intValue());						
						return ips;	
					}
				} else if (name.equalsIgnoreCase(SelfSizing.NAME)) {

					String n = e.getAttribute(N);
					String c = e.getAttribute(CTYPE);		
					String maxincomp = e.getAttribute(MAXINCOMP);					
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						SelfSizing ips = new SelfSizing(n, c, new Integer(maxincomp), constraint, parent);						
						return ips;				
					} else {
						SelfSizing ips = new SelfSizing(n, c, new Integer(maxincomp), constraint, parent, new Integer(priority).intValue());						
						return ips;	
					}
				} else if (name.equalsIgnoreCase(ManagedScope.NAME)) {

					String s = e.getAttribute(S);															
					String priority = e.getAttribute(PRIORITY);
					if (priority == null) {
						ManagedScope ips = new ManagedScope(s, constraint, parent);						
						return ips;				
					} else {
						ManagedScope ips = new ManagedScope(s, constraint, parent, new Integer(priority).intValue());						
						return ips;	
					}
				}
			}
		}
		return null;
	}*/

	public GlobalConfig parseGlobalConfig(XMLElement e, Archetype archtype) throws ParseException {
		if (e != null) {
			String ns = e.getNameSpace();
			if (ns == null || !ns.equalsIgnoreCase(CoreExtensionFactory.ID)) {
				throw new ParseException("namespace problem!");
			}
			String name = e.getName();			
			if (name == null) {
				throw new ParseException("name problem!");
			} else {
				if (name.equalsIgnoreCase(TopScopeLeaderConfig.NAME)) {
					/*
					 * TopScopeLeader Config property
					 */
					String url = e.getAttribute(URL);
					TopScopeLeaderConfig tslc = new TopScopeLeaderConfig(url, archtype);								
					return tslc;
				}
			}
		}
		return null;
	}






}
