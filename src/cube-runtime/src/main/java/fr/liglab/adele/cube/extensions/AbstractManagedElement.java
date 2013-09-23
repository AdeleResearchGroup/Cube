package fr.liglab.adele.cube.extensions;

/**
 * User: debbabi
 * Date: 9/18/13
 * Time: 12:13 AM
 */
public abstract class AbstractManagedElement implements ManagedElementExtensionPoint {

    Extension extension;

    public AbstractManagedElement(Extension extension) {
        this.extension = extension;
    }

    public Extension getExtension() {
        return extension;
    }

}
