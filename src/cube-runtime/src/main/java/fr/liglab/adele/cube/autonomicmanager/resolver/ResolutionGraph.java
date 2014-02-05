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
        print();
        for (Constraint c : root.getConstraints()) {
            resolveConstraint(c, root.getValues().get(0));
        }
        return performConstraints(this.root);
    }

    private void resolveConstraint(Constraint c, String currentProblem) {
        info("resolving constraint "+c.getArchetypePropertyName()+"...");
        c.setCurrentProblem(currentProblem);
        if (c instanceof GoalConstraint) {
            if (c.getObject() instanceof PrimitiveVariable) {
                info("unary constraint '" + c.getArchetypePropertyName() + "' is by default resolved! no need to execute a resolution strategy!");
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
                            info("create...1");
                            createValue(c.getObject());
                        }
                    } break;
                    case Create: {
                        info("executing resolution strategy 'Create' on constraint '"+c.getArchetypePropertyName()+"'...");
                        List<Constraint> cc = c.getObject().getConstraints();
                        for (Constraint cs : cc) {
                            Variable cv = cs.getObject();
                            findValues(cv);
                        }
                        // perform ...
                        if (c.getObject() instanceof MultiValueVariable) {
                            ManagedElement desc = ((MultiValueVariable) c.getObject()).getDescription();
                            if (performConstraints((c.getObject()))== true) {
                                createValue(c.getObject());
                                info("create...1");
                            } else {
                                info("create...0");
                            }
                        }



                    } break;
                }
            }
        }
    }

    /**
     * Create a value for the variable using the provided description
     * @param v
     */
    private void createValue(Variable v) {
        info("Creating value for variable "+v.getId()+"...");
        if (v instanceof PrimitiveVariable) {
            // nothing to do!
        } else if (v instanceof MultiValueVariable) {
            String newInstance = this.resolver.createUsingDescription(((MultiValueVariable) v).getDescription());
            //System.out.println("[RG] .... new instance: " + newInstance);
            /// VERIFY
            if (verifyValue(newInstance, v)) {
                ((MultiValueVariable) v).addValue(newInstance);
                for (Constraint c : v.getConstraints()) {
                    c.setCurrentProblem(newInstance);
                }
                //r.add(res);
            }
        }
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
            // if has only unary constraints! we look on the Runtime Model!
            boolean isFinalVariable = true;
            for (Constraint c : cc) {
                if (c.getObject() instanceof MultiValueVariable) {
                    isFinalVariable = false;
                }
            }
            if (isFinalVariable == true) {
                info("find possible values for variable: "+ ((MultiValueVariable) v).getDescription().getName() + " from RuntimeModel...");
                // has no constraints, find value from Runtime Model
                List<String> result = this.resolver.findFromRuntimeModel(((MultiValueVariable) v).getDescription());
                info("find possible values for variable: "+ ((MultiValueVariable) v).getDescription().getName() + " from RuntimeModel...("+result.size()+")");
                // verify found values
                for (String res : result) {
                    if (verifyValue(res, v)) {
                        ((MultiValueVariable) v).addValue(res);
                        r.add(res);
                        info("--- "+res+ " OK");
                    } else {
                        info("--- "+res+ " X");
                    }
                }
                //((MultiValueVariable) v).addValues(result);
                //r.addAll(result);
            } else {
                info("find possible values for variable: "+ ((MultiValueVariable) v).getDescription().getName() + " ... using its constraints");
                // has constraint, find value using those constraints
                for (Constraint c : cc) {
                    if (c.getObject() instanceof MultiValueVariable) { // avoid finding values for primitive values!
                        Variable cv = c.getObject();
                        List<String> cvs = findValues(cv);
                        for (String uuid : cvs) {
                            List<String> result = this.resolver.findUsingArchetypeProperty(c.getArchetypePropertyName(), uuid, ((MultiValueVariable) v).getDescription());
                            for (String res : result) {
                                if (verifyValue(res, v)) {
                                    ((MultiValueVariable) v).addValue(res);
                                    r.add(res);
                                }
                            }
                            //((MultiValueVariable) v).addValues(result);
                            //r.addAll(result);
                        }
                    }
                }
            }
        }
        return r;
    }


    private boolean performConstraints(Variable v) {
        // try first by checking if there is a constraint with no possible values.
        // If so, return false, Which mean that the instance will remain INVALID
        for (Constraint c : v.getConstraints()) {
            if (c.getObject() instanceof MultiValueVariable) {
                if (((MultiValueVariable) c.getObject()).getValues().size()==0) {
                    if (c instanceof GoalConstraint){
                        if (((GoalConstraint) c).isOptional() == false) {
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
                if (((MultiValueVariable)v).getValues().size()>0) {
                    String me1 = ((MultiValueVariable)v).getValues().get(0);
                    this.resolver.performProperty(c.getArchetypePropertyName(), me1, ((PrimitiveVariable) c.getObject()).getValue());
                    if (c instanceof GoalConstraint) {
                        ((GoalConstraint) c).setCurrentSolution(((PrimitiveVariable) c.getObject()).getValue());
                    }
                }
            } else if (c.getObject() instanceof MultiValueVariable) {
                List<String> possibleValues = ((MultiValueVariable) c.getObject()).getValues();
                info("finding "+possibleValues.size()+" possible solution(s) for goal '"+c.getArchetypePropertyName()+"'");
                for (String pv : possibleValues){
                    info("--- "+pv);
                }
                if (possibleValues.size()>0) {

                    String uuid_subject = c.getCurrentProblem();
                    info("We only perform the first found solution!");
                    String uuid_object = possibleValues.get(0);

                    if (isUnmanaged(uuid_object)) {
                        if (performConstraints(c.getObject()) == false) {
                            return false;
                        }
                    }
                    if (this.resolver.performProperty(c.getArchetypePropertyName(), uuid_subject, uuid_object)==true) {
                        info("performing constraint '"+c.getArchetypePropertyName()+"' between '"+uuid_subject+"' and '"+uuid_object+"'");
                        if (c instanceof GoalConstraint) {
                            ((GoalConstraint) c).setCurrentSolution(uuid_object);
                        }
                    } else {
                        info("there were problem while performing constraint '"+c.getArchetypePropertyName()+"' between '"+uuid_subject+"' and '"+uuid_object+"'");
                        if (c instanceof GoalConstraint) {
                            if (((GoalConstraint) c).isOptional()) {
                                continue;
                            }
                        }
                    }
                    /*
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
                            info("performing constraint '"+c.getArchetypePropertyName()+"' between '"+uuid_subject+"' and '"+uuid_object+"'");
                            if (c instanceof GoalConstraint) {
                                ((GoalConstraint) c).setCurrentSolution(uuid_object);
                            }
                        } else {
                            //this.resolver.cancelProperty(c.getArchetypePropertyName(), uuid_subject, uuid_object);
                            // TODO cancel performed constraints!
                            return false;
                        }
                    } */

                } else {
                    if (c instanceof GoalConstraint) {
                        if (((GoalConstraint)c).isOptional()==true)
                            return true;
                    }
                    return false;
                }
            }
        }
        //info("instance "+((MultiValueVariable)v).getValues().get(0)+" is resolved!");
        return true;
    }
/*
    private boolean performConstraints(Variable v) {
        // try first by checking if there is a constraint with no possible values.
        // If so, return false, Which mean that the instance will remain INVALID
        for (Constraint c : v.getConstraints()) {
            if (c.getObject() instanceof MultiValueVariable) {
                if (((MultiValueVariable) c.getObject()).getValues().size()==0) {
                    if (c instanceof GoalConstraint){
                        if (((GoalConstraint) c).isOptional() == false) {
                            info("constraint "+c.getArchetypePropertyName()+" is not resolved for instance '"+((MultiValueVariable) c.getObject()).getDescription().getName()+"'!");
                            return false;
                        }
                    }
                }
            }
        }
        List<ConstraintDetail> toBePerformed = new ArrayList<ConstraintDetail>();
        // perform constraints
        for (Constraint c : v.getConstraints()) {
            if (c.getObject() instanceof PrimitiveVariable) {
                if (((MultiValueVariable)v).getValues().size()>0) {
                    String me1 = ((MultiValueVariable)v).getValues().get(0);
                    //this.resolver.performProperty(c.getArchetypePropertyName(), me1, ((PrimitiveVariable) c.getObject()).getValue());
                    toBePerformed.add(new ConstraintDetail(c, me1, ((PrimitiveVariable) c.getObject()).getValue()));

                }
            } else if (c.getObject() instanceof MultiValueVariable) {
                List<String> possibleValues = ((MultiValueVariable) c.getObject()).getValues();
                info("finding "+possibleValues.size()+" possible solution(s) for goal '"+c.getArchetypePropertyName()+"'");
                for (String pv : possibleValues){
                    info("--- "+pv);
                }
                if (possibleValues.size()>0) {

                    String uuid_subject = c.getCurrentProblem();
                    info("We only perform the first found solution!");
                    String uuid_object = possibleValues.get(0);

                    toBePerformed.add(new ConstraintDetail(c, uuid_subject, uuid_object));

                } else {
                    if (c instanceof GoalConstraint) {
                        if (((GoalConstraint)c).isOptional()==true)
                            return true;
                    }
                    return false;
                }
            }
        }
        // performing the toBePerformed Constraints
        info("Performing the constraints...");
        for (ConstraintDetail cd : toBePerformed) {
            if (isUnmanaged(cd.getValue())) {
                if (performConstraints(cd.getConstraint().getObject()) == false) {
                    return false;
                }
            }
            if (this.resolver.performProperty(cd.getConstraint().getArchetypePropertyName(), cd.getSubject(), cd.getValue())==true) {
                info("performing constraint '"+cd.getConstraint().getArchetypePropertyName()+"' between '"+cd.getSubject()+"' and '"+cd.getValue()+"'");
                if (cd.getConstraint() instanceof GoalConstraint) {
                    ((GoalConstraint) cd.getConstraint()).setCurrentSolution(cd.getValue());
                }
            } else {
                info("there were problem while performing constraint '"+cd.getConstraint().getArchetypePropertyName()+"' between '"+cd.getSubject()+"' and '"+cd.getValue()+"'");
                if (cd.getConstraint() instanceof GoalConstraint) {
                    if (((GoalConstraint) cd.getConstraint()).isOptional()) {
                        continue;
                    }
                }
            }
        }

        return true;
    }
*/

    private boolean verifyValue(String uuid, Variable v) {
        boolean verified = true;
        for (Constraint c: v.getConstraints()) {
            if (c.getObject() instanceof PrimitiveVariable) {
                if (this.resolver.verifyProperty(c.getArchetypePropertyName(), uuid, ((PrimitiveVariable) c.getObject()).getValue())==false) {
                    info("constraint '"+c.getArchetypePropertyName()+"' is NOT verified!");
                    verified = false;
                    break;
                }
                info("constraint '"+c.getArchetypePropertyName()+"' is verified!");
            }
            info("we do not verify binary constraints!");
        }
        info ("verification result: "+verified);
        return verified;
    }



    private void retrieveGoalsFromArchetype() {
        // get applicable goals
        for (GoalProperty gp : am.getArchetype().getGoalProperties()) {
            Element subject = gp.getSubject();
            if (checkForGoalSubjects((ElementDescription) subject, root) == true) {
                info(">> we will add the goal '"+gp.getName()+"' to the Resolution Graph!");
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
                addConstraint(unaryDP, v2);
            }
            for (DescriptionProperty binaryDP : ((ElementDescription)objectElement).getBinaryDescriptionProperties()) {
                addConstraint(binaryDP, v2);
            }
        }
        return v2;
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


    ////////// getters/setters /////////////////////////////////////////////////

    public Variable getRoot() {
        return root;
    }

    public void setRoot(MultiValueVariable root) {
        this.root = root;
    }

    public void print() {
        String out = "-------------------------------------------------------------------------------------\n";
        out += root.getDescription().getName();
        for (Constraint c : root.getConstraints()) {
            out += "\n" + c.print("    ");
        }
        out += "\n-------------------------------------------------------------------------------------";
        info(out);
    }


    void info(String msg) {
        if (this.am.getConfiguration().isDebug() == true) {
            System.out.println("[RG:"+this.am.getUri()+":"+this.hashCode()+"] " + msg);
        }
    }
}
