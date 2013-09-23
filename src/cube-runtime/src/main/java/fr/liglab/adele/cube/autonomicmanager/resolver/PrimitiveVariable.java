package fr.liglab.adele.cube.autonomicmanager.resolver;

/**
 * User: debbabi
 * Date: 9/20/13
 * Time: 5:23 PM
 */
public class PrimitiveVariable extends Variable {

    String value;

    public PrimitiveVariable(ResolutionGraph resolutionGraph) {
        super(resolutionGraph);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
