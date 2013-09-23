package fr.liglab.adele.cube.extensions.core.model;

import fr.liglab.adele.cube.extensions.AbstractManagedElement;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.PropertyExistException;

import java.util.Properties;

/**
 * User: debbabi
 * Date: 9/18/13
 * Time: 12:04 AM
 */
public class NodeFactory extends AbstractManagedElement {

    public NodeFactory(Extension extension) {
        super(extension);
    }

    public ManagedElement newInstance(Properties properties) {
        Node node = null;
        try {
            node = new Node(getExtension().getAutonomicManager().getUri(), properties);
        } catch (PropertyExistException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidNameException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return node;
    }

    public String getName() {
        return Node.NAME;
    }
}
