package fr.liglab.adele.cube.util.id;

public class CPredicateID extends CubeID {
	
	
	public CPredicateID(CubeAgentID cubeinstance, String path) {
		super();		
		setHierarchicalPart(cubeinstance.getHost() + ":" + cubeinstance.getPort() + "/archtype/" + path);
		setFilter(cubeinstance.getFilter());		
	}
	
	public CPredicateID(String predicateID) {
		super();
		if (predicateID != null) {
			String uri = predicateID.replace("cube://", "");
			setHierarchicalPart(uri);
			setFilter("");
		}		
	}

	public CubeAgentID getCubeInstanceID() {
		String uri = getURI();
		String[] tmp1 = uri.split("://");
		String[] tmp2 = tmp1[1].split(":");
		String host = tmp2[0];
		String port = tmp2[1];
		if (port.contains("/")) {
			port = port.substring(0, port.indexOf("/"));
		}
		CubeAgentID id;
		try {
			id = new CubeAgentID(host, new Integer(port).intValue(), "");
			return id;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getPath() {
		String uri = getURI();
		String[] tmp1 = uri.split("/archtype/");		
		return tmp1[1];
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CPredicateID) {
			return ((CPredicateID)obj).getURI().equalsIgnoreCase(this.getURI());
		}
		return false;
	}
}
