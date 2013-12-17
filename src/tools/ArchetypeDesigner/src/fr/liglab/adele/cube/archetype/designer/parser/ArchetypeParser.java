package fr.liglab.adele.cube.archetype.designer.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.DescriptionProperty;
import fr.liglab.adele.cube.archetype.designer.model.Element;
import fr.liglab.adele.cube.archetype.designer.model.ElementDescription;
import fr.liglab.adele.cube.archetype.designer.model.ElementValue;
import fr.liglab.adele.cube.archetype.designer.model.GoalProperty;

public class ArchetypeParser {
	
	public static final String NAMESPACE = "fr.liglab.adele.cube.core";
	
	public static final String ARCHETYPE_EXTENSION = ".arch";

    public static final String CUBE = "cube";
    public static final String ARCHETYPE = "archetype";
    public static final String ID = "id";
    public static final String VERSION = "version";
    public static final String DESCRIPTION = "description";
    public static final String ELEMENTS = "elements";
    public static final String GOALS = "goals";
    public static final String GOAL = "goal";
    public static final String PRIORITY = "priority";
    public static final String OPTIONAL = "optional";
    public static final String SUBJECT = "s";
    public static final String OBJECT = "o";
    public static final String RESOLUTION = "r";
    
        
    public static Archetype parse(URL url) throws ParseException, ParseException{
        
        InputStream fis;
        try {
            fis = url.openStream();
            return parse(fis);
        } catch (IOException e) {
            //log.error("Error when trying to open File.");
        }
        return null;
    }

    public static Archetype parse(InputStream stream) throws ParseException {        
        Archetype archtype = null;
        XMLElement[] meta = null;
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            XMLParser handler = new XMLParser();
            parser.setContentHandler(handler);

            parser.setErrorHandler(handler);

            InputSource is = new InputSource(stream);
            parser.parse(is);
            meta = handler.getMetadata();
            stream.close();

        } catch (IOException e) {
            //log.error("Cannot open the archtype input stream: " + e.getMessage());
        } catch (ParseException e) {
            //log.error("Parsing error when parsing the XML file: " + e.getMessage());
        } catch (SAXParseException e) {
            //log.error("Error during archtype parsing at line " + e.getLineNumber() + " : " + e.getMessage());
        } catch (SAXException e) {
            //log.error("Parsing error when parsing (Sax Error) the XML file: " + e.getMessage());
        }

        if (meta == null || meta.length == 0) {
            //log.warning("The parsed archtype is empty!");
        }
        /* build the archtype object from the xml elements */
        archtype = buildArchtype(meta);
        return archtype;
    }
    
    private static Archetype buildArchtype(XMLElement[] meta) throws ParseException {
        
        Archetype archtype = new Archetype();
        
        if (meta != null && meta.length > 0) {
            XMLElement archtypeE = meta[0];
            if (archtypeE.getName()!= null && archtypeE.getName().equalsIgnoreCase(ARCHETYPE)) {
                archtype = new Archetype();
                archtype.setId(archtypeE.getAttribute(ID));
                archtype.setArchetypeDescription(archtypeE.getAttribute(DESCRIPTION));
                archtype.setVersion(archtypeE.getAttribute(VERSION));

                // parse elements
                XMLElement[] elementTypess = archtypeE.getElements(ELEMENTS);
                if (elementTypess != null && elementTypess.length > 0) {
                    XMLElement elementTypes = elementTypess[0];
                    if (elementTypes != null) {
                        XMLElement[] cobjects = elementTypes.getElements();
                        // pass 1 : elements only (without their description properties)
                        for (int i = 0; i<cobjects.length; i++) {
                            XMLElement typeElement = cobjects[i];
                            if (typeElement != null) {
                                String namespace = typeElement.getNameSpace();
                                if (namespace == null) namespace = NAMESPACE;
                                String name = typeElement.getName();
                                String id = typeElement.getAttribute(ID);

                                ElementDescription ee = new ElementDescription(archtype, namespace, name, id);
                                //archtype.addElement(ee);
                            }
                        }
                        // pass 2 : add description properties

                        for (int i = 0; i<cobjects.length; i++) {
                            XMLElement typeElement = cobjects[i];
                            if (typeElement != null) {
                                String id = typeElement.getAttribute(ID);

                                ElementDescription e = archtype.getElementDescription(id);

                                XMLElement[] caracts = typeElement.getElements();
                                if (caracts != null) {
                                    for (int j = 0; j<caracts.length; j++) {
                                        XMLElement caract = caracts[j];
                                        if (caract != null) {
                                            String cnamespace = caract.getNameSpace();
                                            if (cnamespace == null) cnamespace = NAMESPACE;
                                            String cname = caract.getName();
                                            String cdescription = caract.getAttribute(DESCRIPTION);
                                            String cobjectAttr = caract.getAttribute(OBJECT);

                                            DescriptionProperty property = null;
                                            Element objectElement = null;
                                            if (cobjectAttr != null) {
                                                if (cobjectAttr.startsWith("@")) {
                                                    String tmp = cobjectAttr.substring(1);
                                                    objectElement = archtype.getElement(tmp);
                                                } else if(cobjectAttr.startsWith("$")) {
                                                    objectElement = new ElementValue(archtype, cobjectAttr);
                                                    //System.out.println("ARCHETYPE PROPERTY: " + propval);
                                                    //objectElement = archtype.getElement();
                                                }
                                                else {
                                                    objectElement = new ElementValue(archtype, cobjectAttr);
                                                }
                                            }
                                            property = new DescriptionProperty(archtype, cnamespace, cname, e, objectElement, cdescription);                                            

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // parse goals
                XMLElement[] goalssss = archtypeE.getElements(GOALS);
                if (goalssss != null && goalssss.length > 0) {
                    XMLElement xmlgoalstag = goalssss[0];
                    if (xmlgoalstag != null) {
                        XMLElement[] xmlgoals = xmlgoalstag.getElements(GOAL);
                        if (xmlgoals != null) {
                            for (int i = 0; i<xmlgoals.length; i++) {
                                XMLElement xmlgoal = xmlgoals[i];
                                if (xmlgoal != null) {
                                    String id = xmlgoal.getAttribute(ID);
                                    String description = xmlgoal.getAttribute(DESCRIPTION);

                                    Object g = new Object();
                                    if (g != null) {
                                        //archtype.addGoalSet(g);
                                        // parse objectives
                                        XMLElement[] xmlobjectives =xmlgoal.getElements();
                                        if (xmlobjectives != null) {
                                            for (int j=0; j<xmlobjectives.length; j++) {
                                                XMLElement xmlobjective = xmlobjectives[j];
                                                if (xmlobjective != null) {
                                                    String onamespace = xmlobjective.getNameSpace();
                                                    if (onamespace == null) onamespace = NAMESPACE;
                                                    String oname = xmlobjective.getName();
                                                    String odescription = xmlobjective.getAttribute(DESCRIPTION);
                                                    String osubject = xmlobjective.getAttribute(SUBJECT);
                                                    if (osubject == null || !osubject.startsWith("@")) throw new ParseException("No subject was specified for the Objective '"+oname+"'!");
                                                    String oobject = xmlobjective.getAttribute(OBJECT);
                                                    if (oobject == null) throw new ParseException("No object was specified for the Objective '"+oname+"'!");
                                                    String resolution = xmlobjective.getAttribute(RESOLUTION);
                                                    String opriority = xmlobjective.getAttribute(PRIORITY);
                                                    String ooptional = xmlobjective.getAttribute(OPTIONAL);

                                                    

                                                    ElementDescription subject = archtype.getElementDescription(osubject.substring(1));
                                                    if (subject == null)
                                                        throw new ParseException("The goal '"+oname+"' has unkown subject '"+osubject+"'");

                                                    GoalProperty goalProperty = null;

                                                    Element objectElement = null;
                                                    if (oobject != null) {
                                                        if (oobject.startsWith("@")) {
                                                            String tmp = oobject.substring(1);
                                                            objectElement = archtype.getElement(tmp);
                                                        } else {
                                                            objectElement = new ElementValue(archtype, oobject);
                                                        }
                                                    }

                                                    Element subjectElement = null;
                                                    if (osubject != null) {
                                                        if (osubject.startsWith("@")) {
                                                            String tmp = osubject.substring(1);
                                                            subjectElement = archtype.getElement(tmp);
                                                        } else {
                                                            throw new ParseException("Goal should have an Element Description as object!");
                                                        }
                                                    }

                                                    goalProperty = new GoalProperty(archtype, onamespace, oname, subjectElement, objectElement, resolution, opriority, odescription);                                                
                                                    if (oobject != null && oobject.equalsIgnoreCase("true")) {
                                                        goalProperty.setOptional(true);
                                                    }
                                                    goalProperty.setGroup(id);                                                    
                                                }
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }
                }


            } else {
               // log.error("Archtype XML file should starts with '"+ARCHETYPE+"' element!");
            }
        }
        return archtype;
    }
    
    public static String toXmlString(Archetype archetype) {
        String out = "";
        out += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        out += "<cube>\n";
        out += "<"+ARCHETYPE+" ";

        if (archetype.getId() != null) out += "" + ID + "=\"" + archetype.getId() + "\" ";
        if (archetype.getArchetypeDescription() != null) out += "" + DESCRIPTION + "=\"" + archetype.getArchetypeDescription() + "\" ";
        if (archetype.getVersion() != null) out += "" + VERSION + "=\"" + archetype.getVersion() + "\" ";

        out += ">\n";

        out += "<"+GOALS+">\n";

        
            out += "<"+GOAL+" id=\"mygoal\" description=\"generated from Archetype Designer\">\n";
            for (GoalProperty o : archetype.getGoalProperties()) {
                if (o.isBinaryProperty())  {
                    if (o.getNamespace() == NAMESPACE) {

                        out += "<"+o.getName()
                                +" s=\"@"+o.getSubject().getId()+"\" o=\"@"
                                +o.getObject().getId()
                                +"\" r=\""+o.getResolutionStrategy()+"\" />\n";
                    }
                    else
                        out += "<"+o.getNamespace()+":"+o.getName()+" s=\"@"+o.getSubject().getId()+"\" o=\"@"+((ElementDescription)o.getObject()).getId()+"\" r=\""+o.getResolutionStrategy()+"\"/>\n";
                }
                else {
                    if (o.getObject() != null)
                        out += "<"+o.getNamespace()+":"+o.getName()+" s=\"@"+o.getSubject().getId()+"\" o=\""+((ElementValue)o.getObject()).getValue().toString()+"\"/>\n";
                    //else
                        //log.warning(o.getName() + " object value is null");
                }
            }
            out += "</"+GOAL+">\n";
        
        out += "</"+GOALS+">\n";

        out += "<"+ELEMENTS+">\n";
        for (ElementDescription e : archetype.getElementsDescriptions()) {
            if (e.getDescriptionProperties().size() == 0)
                if (e.getNamespace().equalsIgnoreCase(NAMESPACE))
                    out += "<"+e.getName()+" id=\""+e.getId()+"\"/>\n";
                else
                    out += "<"+e.getNamespace()+":"+e.getName()+" id=\""+e.getId()+"\"/>\n";
            else {
                if (e.getNamespace().equalsIgnoreCase(NAMESPACE))
                    out += "<"+e.getName()+" id=\""+e.getId()+"\">\n";
                else
                    out += "<"+e.getNamespace()+":"+e.getName()+" id=\""+e.getId()+"\">\n";
                for (DescriptionProperty c : e.getDescriptionProperties()) {
                    if (c.isBinaryProperty()) {
                        if (e.getNamespace().equalsIgnoreCase(NAMESPACE))
                           out += "<"+c.getName()+" o=\"@"+((ElementDescription)c.getObject()).getId()+"\"/>\n";
                        else
                            out += "<"+c.getNamespace()+":"+c.getName()+" o=\"@"+((ElementDescription)c.getObject()).getId()+"\"/>\n";
                    }
                    else {
                        if (e.getNamespace().equalsIgnoreCase(NAMESPACE))
                            if (((ElementValue)c.getObject()) != null) {
                                out += "<"+c.getName()+" o=\""+((ElementValue)c.getObject()).getValue()+"\"/>\n";
                            } else {
                                out += "<"+c.getName()+" o=\"NULL\"/>\n";
                            }
                        else {
                            if (((ElementValue)c.getObject()) != null) {
                                out += "<"+c.getNamespace()+":"+c.getName()+" o=\""+((ElementValue)c.getObject()).getValue()+"\"/>\n";
                            } else {
                                out += "<"+c.getNamespace()+":"+c.getName()+" o=\"NULL\"/>\n";
                            }
                        }
                    }
                }
                if (e.getNamespace().equalsIgnoreCase(NAMESPACE))
                    out += "</"+e.getName()+">\n";
                else
                    out += "</"+e.getNamespace()+":"+e.getName()+">\n";
            }
        }
        out += "</"+ELEMENTS+">\n";



        out += "</"+ARCHETYPE+">\n";
        out += "</cube>\n";
        return out;
    }

}
