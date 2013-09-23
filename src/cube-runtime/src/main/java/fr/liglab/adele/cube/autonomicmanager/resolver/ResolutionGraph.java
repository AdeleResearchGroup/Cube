package fr.liglab.adele.cube.autonomicmanager.resolver;

import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.archetype.*;
import fr.liglab.adele.cube.autonomicmanager.ArchetypeResolver;
import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.ArrayList;
import java.util.List;

/**
 * User: debbabi
 * Date: 9/19/13
 * Time: 2:54 PM
 */
public class ResolutionGraph {

    AutonomicManager am;
    ArchetypeResolver resolver;
    MultiValueVariable root;

    public ResolutionGraph(ArchetypeResolver resolver) {
        this.resolver = resolver;
        this.am = this.resolver.getAutonomicManager();

    }

    public ResolutionGraph(ArchetypeResolver resolver, MultiValueVariable root) {
        this(resolver);
        this.root = root;
    }

    public boolean resolve() {
        retrieveGoalsFromArchetype();
        for (Constraint c : root.getConstraints()) {
            resolveConstraint(c);
        }
        return performConstraints(this.root);
    }

    private void resolveConstraint(Constraint c) {
        info("resolving constraint "+c.getArchetypePropertyName()+"...");
        if (c instanceof GoalConstraint) {
            if (c.getObject() instanceof PrimitiveVariable) {
                info("unary constraint '"+c.getArchetypePropertyName()+"' is by default resolved! no need to execute a resolution strategy!");
            } else {
                switch (((GoalConstraint) c).getResolutionStrategy()) {
                    case Find: {
                        info("executing resolution strategy 'Find' on constraint '"+c.getArchetypePropertyName()+"'...");
                        List<String> result = findValues(c.getObject());
                        info("find..."+result.size());
                    } break;
                    case FindOrCreate: {
                        info("executing resolution strategy 'FindOrCreate' on constraint '"+c.getArchetypePropertyName()+"'...");
                        List<String> result = findValues(c.getObject());
                        info("find..."+result.size());
                        if (result.size() == 0) {
                            createValue(c.getObject());
                            info("create...1");
                        }
                    } break;
                    case Create: {
                        info("executing resolution strategy 'Create' on constraint '"+c.getArchetypePropertyName()+"'...");
                        List<Constraint> cc = c.getObject().getConstraints();
                        for (Constraint cs : cc) {
                            Variable cv = cs.getObject();
                            findValues(cv);
                        }
                        createValue(c.getObject());
                        info("create...1");
                    } break;
                    case FindOrNothing: {
                        info("executing resolution strategy 'FindOrNothing' on constraint '"+c.getArchetypePropertyName()+"'...");
                        List<String> result=findValues(c.getObject());
                        info("find..."+result.size());
                    } break;
                }
            }
        }
    }

    private boolean performConstraints(Variable v) {
        // try first by checking if there is a constraint with no possible values.
        // If so, return false, Which mean that the instance will remain INVALID
        for (Constraint c : v.getConstraints()) {
            if (c.getObject() instanceof MultiValueVariable) {
                if (((MultiValueVariable) c.getObject()).getValues().size()==0) {
                    if (c instanceof GoalConstraint){
                        if (((GoalConstraint) c).getResolutionStrategy()!=ResolutionStrategy.FindOrNothing) {
                            info("constraint "+c.getArchetypePropertyName()+" is not resolved for instance '"+((MultiValueVariable) c.getObject()).getDescription().getName()+"'!");
                            return false;
                        }
                    }
                }
            }
        }
        // perform constraints
        for (Constraint c : v.getConstraints()) {
            if (c.getObject() instanceof PrimitiveVariable) {
                String me1 = ((MultiValueVariable)v).getValues().get(0);
                this.resolver.performProperty(c.getArchetypePropertyName(), me1, ((PrimitiveVariable) c.getObject()).getValue());
                if (c instanceof GoalConstraint) {
                    ((GoalConstraint) c).setCurrentSolution(((PrimitiveVariable) c.getObject()).getValue());
                }
            } else if (c.getObject() instanceof MultiValueVariable) {
                List<String> possibleValues = ((MultiValueVariable) c.getObject()).getValues();
                info("finding "+possibleValues.size()+" possible solution(s) for goal '"+c.getArchetypePropertyName()+"'");
                for (String pv : possibleValues){
                    info("--- "+pv);
                }
                if (possibleValues.size()>0) {
                    String uuid_object = possibleValues.get(0);
                    if (isUnmanaged(uuid_object)) {
                        if (performConstraints(c.getObject()) == false) {
                            return false;
                        } else {
                            String uuid_subject = ((MultiValueVariable)v).getValues().get(0);
                            if (this.resolver.performProperty(c.getArchetypePropertyName(), uuid_subject, uuid_object)==true) {
                                if (c instanceof GoalConstraint) {
                                    ((GoalConstraint) c).setCurrentSolution(uuid_object);
                                }
                            } else {
                                //this.resolver.cancelProperty(c.getArchetypePropertyName(), uuid_subject, uuid_object);
                                // TODO cancel performed constraints!
                                return false;
                            }
                        }
                    } else {
                        String uuid_subject = ((MultiValueVariable)v).getValues().get(0);
                        if (this.resolver.performProperty(c.getArchetypePropertyName(), uuid_subject, uuid_object)==true) {
                            if (c instanceof GoalConstraint) {
                                ((GoalConstraint) c).setCurrentSolution(uuid_object);
                            }
                        } else {
                            //this.resolver.cancelProperty(c.getArchetypePropertyName(), uuid_subject, uuid_object);
                            // TODO cancel performed constraints!
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        //info("instance "+((MultiValueVariable)v).getValues().get(0)+" is resolved!");
        return true;
    }

    private boolean isUnmanaged(String uuid) {
        if (this.am.getRuntimeModelController().isLocalInstance(uuid)) {
            ManagedElement me = this.am.getRuntimeModelController().getRuntimeModel().getManagedElement(uuid);
            if (me != null && me.getState() == ManagedElement.UNMANAGED) {
                return true;
            }
        }
        return false;
    }


    /**
     * It updates the values of the variable, as well as returning the find values
     * @param v
     * @return
     */
    private List<String> findValues(Variable v) {
        //System.out.println("[RESOLVER] find possible values for variable: "+ v.getId());
        List<String> r = new ArrayList<String>();
        if (v instanceof PrimitiveVariable) {
            //System.out.println("[RESOLVER] find possible values for variable: "+ v.getId() + " ... is primitive variable!");
            //((MultiValueVariable)v).addValues(r);
        } else if (v instanceof MultiValueVariable) {
            List<Constraint> cc = v.getConstraints();
            if (cc.size() == 0) {
                info("find possible values for variable: "+ ((MultiValueVariable) v).getDescription().getName() + " ... from RuntimeModel");
                // has no constraints, find value from Runtime Model
                List<String> result = this.resolver.findFromRuntimeModel(((MultiValueVariable) v).getDescription());
                ((MultiValueVariable) v).addValues(result);
                r.addAll(result);
            } else {
                info("find possible values for variable: "+ ((MultiValueVariable) v).getDescription().getName() + " ... using its constraints");
                // has constraint, find value using those constraints
                for (Constraint c : cc) {
                    Variable cv = c.getObject();
                    List<String> cvs = findValues(cv);
                    for (String uuid : cvs) {
                        List<String> result = this.resolver.findUsingArchetypeProperty(c.getArchetypePropertyName(), uuid, ((MultiValueVariable) v).getDescription());
                        ((MultiValueVariable) v).addValues(result);
                        r.addAll(result);
                    }
                    // TODO verify found values!!
                }
            }

        }
        return r;
    }

    private void createValue(Variable v) {
        if (v instanceof PrimitiveVariable) {
            // nothing to do!
        } else if (v instanceof MultiValueVariable) {
            List<Constraint> cc = v.getConstraints();
            if (cc.size() == 0) {

            } else {
                // find values for related elements to this "te be created" element.
            }
            String newInstance = this.resolver.createUsingDescription(((MultiValueVariable) v).getDescription());
            System.out.println("[RG] .... new instance: " + newInstance);
            ((MultiValueVariable) v).addValue(newInstance);
        }
    }

    private void retrieveGoalsFromArchetype() {
        // get applicable goals
        for (GoalProperty gp : am.getArchetype().getGoalProperties()) {
            Element subject = gp.getSubject();
            if (checkForGoalSubjects((ElementDescription) subject, root) == true) {
                addGoal(gp);
                //System.out.println("[RESOLVER] adding goal: "+gp.getFullname());
            } else {
                ///System.out.println("[resolver] goal '"+gp.getFullname()+"' in not suited!");
            }

        }
    }

    private boolean checkForGoalSubjects(ElementDescription e, MultiValueVariable v) {
        String namespace = v.getDescription().getNamespace();
        String name = v.getDescription().getName();
        if (namespace != null && namespace.equalsIgnoreCase(e.getNamespace())
                && name != null && name.equalsIgnoreCase(e.getName())) {

            for (DescriptionProperty dp : e.getUnaryDescriptionProperties()) {
                String ov = ((ElementValue)dp.getObject()).getValue().toString();
                if (resolver.checkProperty(dp.getFullname(), v.getDescription(), ov) == false) {
                    return false;
                }
            }
            for (DescriptionProperty dp : e.getBinaryDescriptionProperties()) {
                // TODO
            }
            return true;
        } else {
            //System.out.println("[resolver] not application element description '"+e.getId()+"'!");
        }
        return false;
    }

    private void addGoal(GoalProperty gp) {
        addConstraint(gp, root);
    }

    private Variable addConstraint(Property p, Variable v1) {
        //System.out.println("[RESOVLER] addConstraint: ");
        Variable v2 = null;
        Constraint c = null;
        if (p instanceof GoalProperty) {
            c = new GoalConstraint(this, p.getFullname(), ((GoalProperty)p).getResolutionStrategyValue(), v1, v2);
        } else {
            c = new Constraint(this, p.getFullname(), v1, v2);
        }
        v1.addConstraint(c);
        // update v2 from Archetype
        Element objectElement = p.getObject();
        if (p.isUnaryProperty()) {
            v2 = new PrimitiveVariable(this);
            c.setObject(v2);
            String v = ((ElementValue)objectElement).getValue();
            ((PrimitiveVariable)v2).setValue(v);
        } else {
            v2 = new MultiValueVariable(this);
            c.setObject(v2);
            ((MultiValueVariable)v2).getDescription().setName(((ElementDescription) objectElement).getName());
            ((MultiValueVariable)v2).getDescription().setNamespace(((ElementDescription) objectElement).getNamespace());
            for (DescriptionProperty unaryDP : ((ElementDescription) objectElement).getUnaryDescriptionProperties()) {
                String ov = ((ElementValue)unaryDP.getObject()).getValue().toString();
                this.resolver.performProperty(unaryDP.getFullname(), ((MultiValueVariable)v2).getDescription(), ov);
            }
            for (DescriptionProperty binaryDP : ((ElementDescription)objectElement).getBinaryDescriptionProperties()) {
                addConstraint(binaryDP, v2);
            }
        }
        return v2;
    }

    private boolean resolveGoal(Constraint c) {
       return true;
    }




    ////////// getters/setters /////////////////////////////////////////////////

    public Variable getRoot() {
        return root;
    }

    public void setRoot(MultiValueVariable root) {
        this.root = root;
    }

    public String print() {
        String out = "";
        out += "root::\n";
        out += "      " + root.getDescription().getDocumentation();
        for (Constraint c : root.getConstraints()) {
            out += "\n------------goal:::"+c.getArchetypePropertyName()+"------- "+ c.getObject();
        }

        return out;
    }


    void info(String msg) {
        if (this.am.getConfiguration().isDebug() == true) {
            System.out.println("[RG:"+this.am.getUri()+":"+this.hashCode()+"] " + msg);
        }
    }
}
