package fr.liglab.adele.cube.archetype.designer.parser;

/**
 * Exceptions thrown by parsers.
 * 
 * @author debbabi
 */
public class ParseException extends Exception {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Parsing error.
     * @param msg : the error message.
     */
    public ParseException(String msg) {
        super(msg);
    }

}
