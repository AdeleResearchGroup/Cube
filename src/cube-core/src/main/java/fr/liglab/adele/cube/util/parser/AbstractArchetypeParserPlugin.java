package fr.liglab.adele.cube.util.parser;

import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.Type;
import fr.liglab.adele.cube.archetype.GlobalConfig;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.constraints.Connect;
import fr.liglab.adele.cube.extensions.core.constraints.FindLocally;
import fr.liglab.adele.cube.extensions.core.constraints.OnNode;
import fr.liglab.adele.cube.extensions.core.constraints.SelfCreateLocally;
import fr.liglab.adele.cube.util.xml.XMLElement;

public class AbstractArchetypeParserPlugin implements ArchetypeParserPlugin {

	public Type parseType(XMLElement element, Archetype archtype)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public Constraint parseConstraint(XMLElement e, Archetype archtype)
			throws ParseException {
		/*if (e != null) {
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
					String c1 = e.getAttribute(C1);
					String c2 = e.getAttribute(C2);
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
					String c = e.getAttribute(C);
					String n = e.getAttribute(N);
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					OnNode onNode = new OnNode(c, n, id, description,  new Integer(priority).intValue(), archtype);					
					return onNode;
				} else if (name.equalsIgnoreCase(FindLocally.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String i = e.getAttribute(I);					
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					FindLocally findLocaly = new FindLocally(i, id, description,  new Integer(priority).intValue(), archtype);									
					return findLocaly;
				}	else if (name.equalsIgnoreCase(SelfCreateLocally.NAME)) {
					String id = null;
					if (e.getAttribute(ID) != null) {id = e.getAttribute(ID);}
					String description = null;
					if (e.getAttribute(DESCRIPTION) != null) {description = e.getAttribute(DESCRIPTION);}
					String i = e.getAttribute(I);					
					String priority = null;
					if (e.getAttribute(PRIORITY) != null) { priority = e.getAttribute(PRIORITY); }
					if (e.getAttribute(P) != null) { priority = e.getAttribute(P); }
					if (priority == null) {
						priority="0";
					} 
					SelfCreateLocally selfCreateLocally = new SelfCreateLocally(i, id, description,  new Integer(priority).intValue(), archtype);									
					return selfCreateLocally;
				}				
			}
		}*/
		return null;
	}

	public GlobalConfig parseGlobalConfig(XMLElement element, Archetype archtype)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
