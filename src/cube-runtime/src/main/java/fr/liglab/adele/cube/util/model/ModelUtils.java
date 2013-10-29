package fr.liglab.adele.cube.util.model;

import fr.liglab.adele.cube.metamodel.Attribute;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Reference;

import java.util.List;

/**
 * User: debbabi
 * Date: 9/22/13
 * Time: 7:03 PM
 */
public class ModelUtils {

    /**
     * Compare two ManagedElement instances.
     *
     * @param me1
     * @param me2
     * @return If returns -1, the two compared ManagedElement instances are of different natures.
     *         If returns 0. the two ManagedElements are perfectly equivalent
     *         Other else, the returned positive value is the number of attributes and references elements that
     *                      are different
     */
    public static int compareTwoManagedElements(ManagedElement me1, ManagedElement me2) {
        //System.out.println("[RESOLVER] comparing two managed elements:");
        //System.out.println("[RESOLVER] me1: " + me1.getUUID()+"\n"+me1.getDocumentation());
        //System.out.println("[RESOLVER] me2: " + me2.getUUID()+"\n"+me2.getDocumentation());
        int result = -1;
        if (me1 == null || me1.getNamespace() == null || me1.getName() == null ||
                me2 == null || me2.getNamespace() == null || me2.getName() == null) {
            return -1;
        } else {
            int ok = 0;
            int notok = 0;
            int totalComparedAttributes = 0;
            int totalComparedReferences = 0;
            List<Attribute> me1as = me1.getAttributes();
            List<Reference> me1rs = me1.getReferences();
            for (Attribute a : me1as) {
                if (a.getValue() != null) {
                    totalComparedAttributes++;
                    String tmp = me2.getAttribute(a.getName());
                    if (tmp != null) {
                        if (tmp.equalsIgnoreCase(a.getValue())) {
                            ok++;
                        } else { notok++; }
                    } else {
                        notok++;
                    }
                }
            }
            for (Reference r : me1rs) {
                if (r.getReferencedElements().size() > 0) {
                    Reference me2reftmp = me2.getReference(r.getName());
                    if (me2reftmp == null) {
                        totalComparedReferences++;
                        notok++;
                        continue;
                    } else {
                        for (String tmp : r.getReferencedElements()) {
                            totalComparedReferences++;
                            if (me2reftmp.getReferencedElements().contains(tmp) == true) {
                                ok++;
                            } else { notok++; }
                        }
                    }
                }
            }
            if (ok == (totalComparedAttributes+totalComparedReferences)) return 0;
            else return notok;
        }
    }
    public static int compareAttributesOfTwoManagedElements(ManagedElement me1, ManagedElement me2) {
        //System.out.println("[RESOLVER] comparing two managed elements:");
        //System.out.println("[RESOLVER] me1: " + me1.getUUID()+"\n"+me1.getDocumentation());
        //System.out.println("[RESOLVER] me2: " + me2.getUUID()+"\n"+me2.getDocumentation());
        int result = -1;
        if (me1 == null || me1.getNamespace() == null || me1.getName() == null ||
                me2 == null || me2.getNamespace() == null || me2.getName() == null) {
            return -1;
        } else {
            int ok = 0;
            int notok = 0;
            int totalComparedAttributes = 0;
            List<Attribute> me1as = me1.getAttributes();
            List<Reference> me1rs = me1.getReferences();
            for (Attribute a : me1as) {
                if (a.getValue() != null) {
                    totalComparedAttributes++;
                    String tmp = me2.getAttribute(a.getName());
                    if (tmp != null) {
                        if (tmp.equalsIgnoreCase(a.getValue())) {
                            ok++;
                        } else { notok++; }
                    } else {
                        notok++;
                    }
                }
            }
            if (ok == (totalComparedAttributes)) return 0;
            else return notok;
        }
    }

}
