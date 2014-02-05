package fr.liglab.adele.cube.autonomicmanager.resolver;

import fr.liglab.adele.cube.autonomicmanager.ArchetypeResolver;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Master;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.extensions.core.model.Scope;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.util.perf.ResolutionMeasure;

/**
 * Created by debbabi on 03/02/14.
 */
public class Resolution implements Runnable {

    private ArchetypeResolver resolver;
    private ManagedElement instance;
    private ResolutionGraph rg;
    private Thread t;
    private boolean working = false;

    public Resolution(ArchetypeResolver resolver, ManagedElement instance) {
        this.resolver = resolver;
        this.instance = instance;
        rg = new ResolutionGraph(resolver);
        MultiValueVariable root = new MultiValueVariable(rg);
        rg.setRoot(root);
        try {
            ManagedElement desc = (ManagedElement) instance.clone();
            root.setDescription(desc);
            root.addValue(instance.getUUID());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        t = new Thread(this);
        t.start();
    }

    public void resolve() {
        this.working = true;
    }

    public void run() {
        while(working == false) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ResolutionMeasure m = new ResolutionMeasure(resolver.getAutonomicManager().getUri(), instance.getName());
        if (instance.getName().equalsIgnoreCase(Component.NAME)) {
            m.setComment(instance.getAttribute(Component.CORE_COMPONENT_TYPE));
        } else if (instance.getName().equalsIgnoreCase(Node.NAME)) {
            m.setComment(instance.getAttribute(Node.CORE_NODE_TYPE));
        } else if (instance.getName().equalsIgnoreCase(Scope.NAME)) {
            m.setComment(instance.getAttribute(Scope.CORE_SCOPE_ID));
        } else if (instance.getName().equalsIgnoreCase(Master.NAME)) {
            m.setComment(instance.getAttribute(Master.NAME));
        }
        m.start();
        if (rg.resolve()) {
            m.end();
            m.setResolved(true);
            info(instance.getName() + " '" + instance.getUUID() + "' is resolved!");
            if (validateSolution(rg)) {
                resolver.getAutonomicManager().getRuntimeModelController().getRuntimeModel().refresh();
            }
        } else {
            m.end();
            m.setResolved(false);
            info("no solution found for "+instance.getName()+": " + instance.getUUID());
        }
        synchronized (resolver) {
            instance.setInResolution(false);
        }
        m.calculate();
        resolver.getAutonomicManager().getAdministrationService().getPerformanceChecker().addResolutionMeasure(m);
    }

    private boolean validateSolution(ResolutionGraph rg) {
        // TODO should check "am" attribute to check if it should added here or in another runtime model part
        Variable root = rg.getRoot();
        boolean changed = false;
        for (Constraint c : root.getConstraints()) {
            if (c instanceof GoalConstraint) {
                String related = ((GoalConstraint) c).getCurrentSolution();
                if (resolver.getAutonomicManager().getRuntimeModelController().isLocalInstance(related)) {

                    ManagedElement me = resolver.getAutonomicManager().getRuntimeModelController().getRuntimeModel().getManagedElement(related);
                    if (me != null) {
                        if (me.getAutonomicManager() != null && !me.getAutonomicManager().equalsIgnoreCase(resolver.getAutonomicManager().getUri())) {
                            // should be in another am!
                            // 1. add to remote hash map


                            resolver.getAutonomicManager().getExternalInstancesHandler().addExternalInstance(related,
                                    me.getAutonomicManager());
                            // 2. prepare table of references/ams - to be sent with the ME

                            // 3. move the ME
                            resolver.moveManagedElement(me, me.getAutonomicManager());
                            // 4. remove local instance
                            resolver.getAutonomicManager().getRuntimeModelController().removeManagedElement(related);

                            //System.out.println("#### this instance "+related+" should be moved to and validated at a remote am!");
                            //am.getRuntimeModelController().getRuntimeModel().removeUnmanagedElements();

                        } else {
                            int state = resolver.getAutonomicManager().getRuntimeModelController().getState(related);
                            if (state == ManagedElement.UNMANAGED) {
                                resolver.getAutonomicManager().getRuntimeModelController().getRuntimeModel().manage(related);
                                changed = true;
                            }
                        }
                    }
                } else {
                    //TODO validate the instance of the remote am!
                    String am_uri = resolver.getAutonomicManager().getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(related);
                    if (am_uri != null) {
                        resolver.refreshRemoteAM(am_uri);
                    } else {
                        System.out.println("WARNING: remote instance referenced but no am uri is found in the local AM!");
                    }
                }
            }
        }
        String uuid = ((MultiValueVariable)root).getDescription().getUUID();
        ManagedElement me = resolver.getAutonomicManager().getRuntimeModelController().getRuntimeModel().getManagedElement(uuid);
        if (me != null) {
            if (me.getState() == ManagedElement.INVALID) {
                me.setState(ManagedElement.VALID);
                changed = true;
            }
        }
        return changed;
    }

    private void info(String msg) {
        if (resolver.getAutonomicManager().getConfiguration().isDebug() == true) {
            System.out.println("[RESOLVER:"+resolver.getAutonomicManager().getUri()+":"+this.hashCode()+"] " + msg);
        }
}
    
    public MultiValueVariable getRoot() {
        return (MultiValueVariable) rg.getRoot();
    }
}
