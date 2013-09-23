package fr.liglab.adele.cube.extensions;

/**
 * User: debbabi
 * Date: 9/17/13
 * Time: 9:56 PM
 */
public abstract class AbstractMonitorExecutor implements MonitorExecutorExtensionPoint {

    Extension extension;

    public AbstractMonitorExecutor(Extension extension) {
        this.extension = extension;
        this.extension.getAutonomicManager().getRuntimeModelController().getRuntimeModel().addListener(this);
    }

    public Extension getExtension() {
        return this.extension;
    }

}
