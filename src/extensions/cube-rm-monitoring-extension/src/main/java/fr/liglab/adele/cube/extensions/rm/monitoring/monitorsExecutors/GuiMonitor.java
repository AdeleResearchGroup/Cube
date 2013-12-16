package fr.liglab.adele.cube.extensions.rm.monitoring.monitorsExecutors;

import fr.liglab.adele.cube.autonomicmanager.RuntimeModel;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelListener;
import fr.liglab.adele.cube.extensions.AbstractMonitorExecutor;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.metamodel.Notification;

/**
 * User: debbabi
 * Date: 9/19/13
 * Time: 10:19 AM
 */
public class GuiMonitor extends AbstractMonitorExecutor {

    private static final String NAME = "gui-monitor";

    GuiMonitorPrefuse gui;

    boolean autoRefresh = false;

    public GuiMonitor(Extension extension) {
        super(extension);
        gui = new GuiMonitorPrefuse(getExtension().getAutonomicManager());
        String ars = extension.getProperties().getProperty("auto-refresh");
        if (ars != null) {
            Boolean ar = new Boolean(ars);
            this.autoRefresh = ar.booleanValue();
        }
    }

    public String getName() {
        return "";
    }

    public void start() {
        gui.setVisible(true);
    }

    public void stop() {
        gui.setVisible(false);

    }

    public void destroy() {
        gui = null;
    }


    public void update(RuntimeModel rm, Notification notification) {
        if (notification.getNotificationType() == RuntimeModelListener.UPDATED_RUNTIMEMODEL) {
                    /*
                    Object instance = notification.getNewValue();
                    if (instance != null && instance instanceof ManagedElement) {
                         gui.addNode((ManagedElement)instance);
                    }
                    */
            if (autoRefresh == true) {
                gui.updateGraph();
            }
        }
    }
}
