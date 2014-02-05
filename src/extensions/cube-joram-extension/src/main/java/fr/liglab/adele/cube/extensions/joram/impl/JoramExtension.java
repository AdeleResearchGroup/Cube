package fr.liglab.adele.cube.extensions.joram.impl;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.ExtensionFactoryService;
import fr.liglab.adele.cube.extensions.ExtensionPoint;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;


public class JoramExtension extends AbstractExtension {

	private JoramExtensionExecutor executor ;


    public JoramExtension(AutonomicManager am, ExtensionFactoryService factory, Properties properties) {
        super(am, factory, properties);

    }
    
    @Override
    public List<ExtensionPoint> getExtensionPoints() {
        List<ExtensionPoint> extensionPointsList = new ArrayList<ExtensionPoint>();
        extensionPointsList.add(executor);
        return extensionPointsList;
    }
    
    
    public void starting (){
        System.out.println("---------------- Joram Plugin -----------------");
        this.executor = new JoramExtensionExecutor(this);
        String user = (String) getProperties().get("user");
		if (user !=null){
			this.executor.setUser(user) ;
		}		
		String pass = (String) getProperties().get("pass");
		if ( pass!=null){
			this.executor.setPass(pass);
		}
		String hostname =  (String) getProperties().get("hostname");
		if ( hostname!=null){
			this.executor.setHostname(hostname);
		}
		String portString= (String) getProperties().get("port");
		if (portString !=null){
			this.executor.setJoramPort(Integer.parseInt(portString));
		}
/*		String modeString = (String) getProperties().get("mode");
		if (  modeString!=null){
			this.executor.setMode(Integer.parseInt(modeString));
		}        */
		this.executor.start();
	}

    public void stopping() {
		this.executor.stop();
    }

    public void destroying() {

    }

/*
    public ConstraintResolver getConstraintResolver(String name) {
		return null;
    }
 */
    /**
     * Creates a new Managed Element Instance of the given name.
     *
     * @param element_name
     * @return
     */
/*
    public ManagedElement newManagedElement(String element_name) {
    	return null;
    } */

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
/*    public ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException {
        return null;
    } */
}
