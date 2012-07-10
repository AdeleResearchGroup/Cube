package fr.liglab.adele.cube.util.id;

public class ExtensionID extends CubeAgentID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */	
	
	private String name = null;
	
	public ExtensionID(CubeID ciid) throws InvalidIDException {
		super(ciid);
		String hierar = ciid.getHierarchicalPart();
		if (hierar.contains("/ctr/")) {
			this.name = hierar.substring(hierar.lastIndexOf("/"), hierar.length());
		}
	}
		
	public ExtensionID(CubeAgentID ciid, String name) throws InvalidIDException  {
		super(ciid);				
		String hierar = ciid.getHierarchicalPart();				
		if (hierar.contains("/ctr/")) {
			throw new InvalidIDException("Invalid ControllerID!");
		}
		if (name != null) {
			this.name = name;
		} else {
			throw new InvalidIDException("No controller name was given!");
		}
	}
	
	public String getLocalId() {
		return this.name;
	}
		
	public static boolean check(CubeID id) {	
		if (id != null) {
			if (id.getHierarchicalPart().contains("/ctr/")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return super.getURI() + "/ctr/" + name;
	}
}
