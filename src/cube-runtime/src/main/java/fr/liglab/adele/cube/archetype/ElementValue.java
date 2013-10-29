package fr.liglab.adele.cube.archetype;

import fr.liglab.adele.cube.util.Utils;

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
        /*
        String result = value;
        if (result != null && result.contains("${")) {
            int ps1 = result.indexOf("$");
            int ps2 = result.indexOf("}");
            CharSequence toModify = result.subSequence(ps1, ps2+1);
            if (toModify != null) {
                CharSequence pname = toModify.subSequence(2, toModify.length()-1);
                if (getArchetype().getAutonomicManager() != null) {
                    String pvalue = getArchetype().getAutonomicManager().getProperty(Utils.toString(pname));
                    if (pvalue != null) {
                        result = result.replace(toModify, pvalue);
                    } else {
                        System.out.println("[WARNING] value of Archetype Element '"+getId()+"' cannot be found among Autonomic Manager's properties!");
                    }
                }
            }
        } */
        return Utils.evaluateValue(getArchetype().getAutonomicManager(), value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
