package fr.liglab.adele.cube.extensions;

/**
 * User: debbabi
 * Date: 9/18/13
 * Time: 12:40 AM
 */
public abstract class AbstractCommunicator implements CommunicatorExtensionPoint {

    Extension extension;

    public AbstractCommunicator(Extension extension) {
        this.extension = extension;
    }

    public Extension getExtension() {
        return extension;
    }
}
