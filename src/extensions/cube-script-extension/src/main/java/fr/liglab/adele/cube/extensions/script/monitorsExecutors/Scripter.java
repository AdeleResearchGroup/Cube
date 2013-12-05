package fr.liglab.adele.cube.extensions.script.monitorsExecutors;

import fr.liglab.adele.cube.autonomicmanager.NotFoundManagedElementException;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModel;
import fr.liglab.adele.cube.extensions.AbstractMonitorExecutor;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Notification;
import fr.liglab.adele.cube.metamodel.PropertyExistException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by debbabi on 12/5/13.
 */
public class Scripter extends AbstractMonitorExecutor {

    private static final String NAME = "scripter";

    class Command implements Comparable<Command> {
        int n;
        String cmd;

        public Command(int n, String cmd) {
            this.n = n;
            this.cmd = cmd;
        }

        public int compareTo(Command command) {
            return n < command.n ? -1 : n > command.n ? 1 : 0;
        }
    }

    List<Command> commands = new ArrayList<Command>();

    public Scripter(Extension extension) {
        super(extension);
    }

    public void start() {
        System.out.println("[INFO] Starting script extension..");
        Properties p = getExtension().getProperties();
        for (Object key : p.keySet()) {
            Command c = new Command(new Integer(key.toString()), p.getProperty(key.toString()));
            if (c != null)
                commands.add(c);
        }
        Collections.sort(commands);
        for (Command c : this.commands) {
            execute(c.cmd);
        }
    }
    private void execute(String command) {
        if (command != null) {
            String[] splitted = command.split(" ");
            if (splitted != null) {
                System.out.println("[INFO] script extension: executing command "+command+"..." );
                if (splitted.length == 1) {
                    System.out.println("[WARNING] script extension: executing command not yet implemented!");
                } else if (splitted.length == 2) {
                    String com = splitted[0];
                    String type = splitted[1];
                    String properties = splitted[2];
                    String typens = CoreExtensionFactory.NAMESPACE;
                    String typename = type;
                    if (type.contains(":")) {
                        String[] tmp = type.split(":");
                        if (tmp != null && tmp.length == 2) {
                            typens = tmp[0];
                            typename = tmp[1];
                        }
                    }
                    if (com.equalsIgnoreCase("newi")) {
                        ManagedElement me = null;
                        try {
                            me = getExtension().getAutonomicManager().getRuntimeModelController().newManagedElement(typens, typename, null);
                        } catch (InvalidNameException e) {
                            e.printStackTrace();
                        } catch (PropertyExistException e) {
                            e.printStackTrace();
                        } catch (NotFoundManagedElementException e) {
                            System.out.println(e.getMessage());
                        }
                        if (me != null) {
                            getExtension().getAutonomicManager().getRuntimeModelController().getRuntimeModel().refresh();
                        }
                    }  else {
                        System.out.println("[INFO] script extension: unknown command " + com+"!");
                    }
                } else if (splitted.length == 3) {
                    String com = splitted[0];
                    String type = splitted[1];
                    String properties = splitted[2];
                    if (type != null) {
                        String typens = CoreExtensionFactory.NAMESPACE;
                        String typename = type;
                        if (type.contains(":")) {
                            String[] tmp = type.split(":");
                            if (tmp != null && tmp.length == 2) {
                                typens = tmp[0];
                                typename = tmp[1];
                            }
                        }
                        Properties p = new Properties();
                        String[] tmp = properties.split(",");
                        if (tmp != null && tmp.length > 0) {
                            for (int i =0; i<tmp.length; i++) {
                                String[] prop = tmp[i].split("=");
                                if (prop != null && prop.length == 2) {
                                    p.put(prop[0], prop[1]);
                                }
                            }
                        }
                        if (com.equalsIgnoreCase("newi")) {
                            ManagedElement me = null;
                            try {
                                me = getExtension().getAutonomicManager().getRuntimeModelController().newManagedElement(typens, typename, p);
                            } catch (InvalidNameException e) {
                                e.printStackTrace();
                            } catch (PropertyExistException e) {
                                e.printStackTrace();
                            } catch (NotFoundManagedElementException e) {
                                System.out.println(e.getMessage());
                            }

                            if (me != null) {
                                getExtension().getAutonomicManager().getRuntimeModelController().getRuntimeModel().refresh();
                            }
                        }   else {
                            System.out.println("[INFO] script extension: unknown command " + com+"!");
                        }
                    }
                } else {
                    System.out.println("[WARNING] script extension: executing command not yet implemented!");
                }
            }
        }


    }
    public void stop() {

    }

    public void destroy() {

    }

    public String getName() {
        return NAME;
    }

    public void update(RuntimeModel rm, Notification notification) {

    }
}
