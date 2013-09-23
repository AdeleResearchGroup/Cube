package fr.liglab.adele.cube.autonomicmanager.resolver;

import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.ArrayList;
import java.util.List;

/**
 * User: debbabi
 * Date: 9/20/13
 * Time: 5:23 PM
 */
public class MultiValueVariable extends Variable {

    ManagedElement description = null;  // (expected) ManagedElement instance, or primitive description
    List<String> values = new ArrayList<String>();  // tested values

    public MultiValueVariable(ResolutionGraph resolutionGraph) {
        super(resolutionGraph);
        description = new ManagedElement();
    }

    public ManagedElement getDescription() {
        return description;
    }

    public void setDescription(ManagedElement managedElement) {
        this.description = managedElement;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValue(String value) {
        if (!this.values.contains(value))
            this.values.add(value);
    }

    public void addValues(List<String> r) {
        for (String v : r){
            addValue(v);
        }
    }
}
