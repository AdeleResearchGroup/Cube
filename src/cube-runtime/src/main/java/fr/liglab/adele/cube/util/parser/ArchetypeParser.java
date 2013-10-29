/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
 * LIG Laboratory (http://www.liglab.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package fr.liglab.adele.cube.util.parser;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.AdministrationService;
import fr.liglab.adele.cube.archetype.*;
import fr.liglab.adele.cube.util.Utils;
import fr.liglab.adele.cube.util.xml.XMLElement;
import fr.liglab.adele.cube.util.xml.XMLParser;
import org.osgi.framework.BundleContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 11:49 AM
 */
public class ArchetypeParser {

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

    static BundleContext bundleContext;
    static CubeLogger log = null;

    /**
     * Initialize the bundleContet object and the Logger.
     *
     * @param cp AdministrationService
     */
    private static void init(AdministrationService cp) {
        if (bundleContext == null) {
            bundleContext = cp.getBundleContext();
        }
        if (log == null) {
            log = new CubeLogger(bundleContext, ArchetypeParser.class.getName());
        }
    }

    public static Archetype parse(AdministrationService cp, String archetypeContent) throws ParseException, ArchetypeException {
        init(cp);
        return parse(cp, ParseUtils.stringToInputStream(archetypeContent));
    }

    public static Archetype parse(AdministrationService cp, URL url) throws ParseException, ArchetypeParsingException, ArchetypeException {
        init(cp);
        InputStream fis;
        try {
            fis = url.openStream();
            return parse(cp, fis);
        } catch (IOException e) {
            log.error("Error when trying to open File.");
        }
        return null;
    }

    public static Archetype parse(AdministrationService cp, InputStream stream) throws ParseException, ArchetypeException {
        init(cp);
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
            log.error("Cannot open the archtype input stream: " + e.getMessage());
        } catch (ParseException e) {
            log.error("Parsing error when parsing the XML file: " + e.getMessage());
        } catch (SAXParseException e) {
            log.error("Error during archtype parsing at line " + e.getLineNumber() + " : " + e.getMessage());
        } catch (SAXException e) {
            log.error("Parsing error when parsing (Sax Error) the XML file: " + e.getMessage());
        }

        if (meta == null || meta.length == 0) {
            log.warning("The parsed archtype is empty!");
        }
        /* build the archtype object from the xml elements */
        archtype = buildArchtype(cp, meta);
        return archtype;
    }

    private static Archetype buildArchtype(AdministrationService cp, XMLElement[] meta) throws ParseException, ArchetypeException {
        init(cp);
        Archetype archtype = null;
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
                                if (namespace == null) namespace = CoreExtensionFactory.NAMESPACE;
                                String name = typeElement.getName();
                                String id = typeElement.getAttribute(ID);

                                ElementDescription ee = new ElementDescription(archtype, namespace, name, id);
                                archtype.addElement(ee);
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
                                            if (cnamespace == null) cnamespace = CoreExtensionFactory.NAMESPACE;
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
                                            property = new DescriptionProperty(archtype, cnamespace, cname, cdescription);
                                            property.setObject(objectElement);
                                            boolean result = archtype.addProperty(property);
                                            e.addDescriptionProperty(property);

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

                                    GoalSet g = new GoalSet(archtype, id, description);
                                    if (g != null) {
                                        archtype.addGoalSet(g);
                                        // parse objectives
                                        XMLElement[] xmlobjectives =xmlgoal.getElements();
                                        if (xmlobjectives != null) {
                                            for (int j=0; j<xmlobjectives.length; j++) {
                                                XMLElement xmlobjective = xmlobjectives[j];
                                                if (xmlobjective != null) {
                                                    String onamespace = xmlobjective.getNameSpace();
                                                    if (onamespace == null) onamespace = CoreExtensionFactory.NAMESPACE;
                                                    String oname = xmlobjective.getName();
                                                    String odescription = xmlobjective.getAttribute(DESCRIPTION);
                                                    String osubject = xmlobjective.getAttribute(SUBJECT);
                                                    if (osubject == null || !osubject.startsWith("@")) throw new ParseException("No subject was specified for the Objective '"+oname+"'!");
                                                    String oobject = xmlobjective.getAttribute(OBJECT);
                                                    if (oobject == null) throw new ParseException("No object was specified for the Objective '"+oname+"'!");
                                                    String resolution = xmlobjective.getAttribute(RESOLUTION);
                                                    String opriority = xmlobjective.getAttribute(PRIORITY);
                                                    String ooptional = xmlobjective.getAttribute(OPTIONAL);

                                                    GoalProperty o = null;
                                                    //Objective o = null;

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
                                                            throw new ArchetypeException("Goal '"+o.getName()+"' should have an Element Description as object!");
                                                        }
                                                    }

                                                    goalProperty = new GoalProperty(archtype, onamespace, oname, resolution, opriority, odescription);
                                                    goalProperty.setSubject(subjectElement);
                                                    goalProperty.setObject(objectElement);
                                                    if (oobject != null && oobject.equalsIgnoreCase("true")) {
                                                        goalProperty.setOptional(true);
                                                    }
                                                    g.addGoal(goalProperty);
                                                    archtype.addProperty(goalProperty);

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
                log.error("Archtype XML file should starts with '"+ARCHETYPE+"' element!");
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

        for (GoalSet gs: archetype.getGoalSets()) {
            out += "<"+GOAL+" id=\""+gs.getId()+"\" description=\""+gs.getDocumentation()+"\">\n";
            for (GoalProperty o : gs.getGoals()) {
                if (o.isBinaryProperty())  {
                    if (o.getNamespace() == CoreExtensionFactory.NAMESPACE) {

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
                    else
                        log.warning(o.getName() + " object value is null");
                }
            }
            out += "</"+GOAL+">\n";
        }
        out += "</"+GOALS+">\n";

        out += "<"+ELEMENTS+">\n";
        for (ElementDescription e : archetype.getElementsDescriptions()) {
            if (e.getDescriptionProperties().size() == 0)
                if (e.getNamespace() == CoreExtensionFactory.NAMESPACE)
                    out += "<"+e.getName()+" id=\""+e.getId()+"\"/>\n";
                else
                    out += "<"+e.getNamespace()+":"+e.getName()+" id=\""+e.getId()+"\"/>\n";
            else {
                if (e.getNamespace() == CoreExtensionFactory.NAMESPACE)
                    out += "<"+e.getName()+" id=\""+e.getId()+"\">\n";
                else
                    out += "<"+e.getNamespace()+":"+e.getName()+" id=\""+e.getId()+"\">\n";
                for (DescriptionProperty c : e.getDescriptionProperties()) {
                    if (c.isBinaryProperty()) {
                        if (e.getNamespace() == CoreExtensionFactory.NAMESPACE)
                           out += "<"+c.getName()+" o=\"@"+((ElementDescription)c.getObject()).getId()+"\"/>\n";
                        else
                            out += "<"+c.getNamespace()+":"+c.getName()+" o=\"@"+((ElementDescription)c.getObject()).getId()+"\"/>\n";
                    }
                    else {
                        if (e.getNamespace() == CoreExtensionFactory.NAMESPACE)
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
                if (e.getNamespace() == CoreExtensionFactory.NAMESPACE)
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
