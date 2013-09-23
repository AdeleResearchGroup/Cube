package fr.liglab.adele.cube.extensions;

/**
 * User: debbabi
 * Date: 9/17/13
 * Time: 8:40 PM
 */
public abstract class AbstractResolver implements ResolverExtensionPoint {

    Extension extension;

    public AbstractResolver(Extension extension) {
        this.extension = extension;
    }

    public Extension getExtension() {
        return this.extension;
    }
}
