package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.ArrayList;
import java.util.List;

/**
 * User: debbabi
 * Date: 9/17/13
 * Time: 8:40 PM
 */
public abstract class AbstractUnaryResolver implements ResolverExtensionPoint {

    Extension extension;

    public AbstractUnaryResolver(Extension extension) {
        this.extension = extension;
    }

    public Extension getExtension() {
        return this.extension;
    }

    public List<String> find(ManagedElement me, ManagedElement description) {
        List<String> result = new ArrayList<String>();
        return result;
    }
}
