package fr.liglab.adele.cube.archetype.designer.parser;


/**
 * An attribute is a key-value pair. It represents the attribute
 * of XML elements.
 * @author 
 */

public class XMLAttribute {
    /**
     * The name of the attribute.
     */
    private String name;

    /**
     * The value of the attribute.
     */
    private String value;

    /**
     * The namespace of the attribute.
     */
    private String nameSpace;

    /**
     * Creates an attribute.
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    public XMLAttribute(String name, String value) {
        this.name = name.toLowerCase();
        this.value = value;
    }

    /**
     * Creates an attribute.
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     * @param ns the namespace of the attribute.
     */
    public XMLAttribute(String name, String ns, String value) {
        this.name = name.toLowerCase();
        this.value = value;
        if (ns != null && ns.length() > 0) {
            this.nameSpace = ns;
        }
    }

    /**
     * Gets the attribute name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets attribute value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets attribute namespace.
     * @return the namespace
     */
    public String getNameSpace() {
        return nameSpace;
    }

}
