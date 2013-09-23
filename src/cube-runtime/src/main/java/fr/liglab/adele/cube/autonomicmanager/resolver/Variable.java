package fr.liglab.adele.cube.autonomicmanager.resolver;

import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * User: debbabi
 * Date: 9/19/13
 * Time: 3:01 PM
 */
public class Variable {

    private String id;

    ResolutionGraph resolutionGraph;
    List<Constraint> constraints = new ArrayList<Constraint>();

    private transient static int index = 1;


    public Variable(ResolutionGraph resolutionGraph) {
        this.id = "__v" + index++;
        this.resolutionGraph = resolutionGraph;
    }

    public ResolutionGraph getResolutionGraph() {
        return resolutionGraph;
    }

    public void setResolutionGraph(ResolutionGraph resolutionGraph) {
        this.resolutionGraph = resolutionGraph;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public synchronized void addConstraint(Constraint c) {
        this.constraints.add(c);
    }

    public String getId() {
        return id;
    }


    public String toString() {
        String out = "";
        if (this instanceof PrimitiveVariable) {
            out += "  v("+id+")\n="+((PrimitiveVariable)this).getValue() + "\n";
            out += "  VALUE["+((PrimitiveVariable)this).getValue()+"]\n";
        } else if (this instanceof MultiValueVariable) {
            out += "  v("+id+")\n="+((MultiValueVariable)this).getDescription().getDocumentation() + "\n";
            out += "  VALUE[";
            for (String s : ((MultiValueVariable)this).getValues()) {
                out += "\n"+ s;
            }
            out += "]\n";
            for (Constraint c : this.constraints) {
                out += "    ----- " + c.getArchetypePropertyName() + "-----" + c.getObject().toString();
            }
        }
        return out;
    }

}
