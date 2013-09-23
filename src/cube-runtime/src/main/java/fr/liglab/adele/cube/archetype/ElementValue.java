package fr.liglab.adele.cube.archetype;

/**
 * User: debbabi
 * Date: 9/1/13
 * Time: 6:08 PM
 */
public class ElementValue extends Element {

    private String value;

    public ElementValue(Archetype archetype, String value) {
        super(archetype, null);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
