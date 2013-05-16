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
import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.archetype.*;
import fr.liglab.adele.cube.plugins.core.CorePluginFactory;
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
    public static final String SUBJECT = "s";
    public static final String OBJECT = "o";
    public static final String RESOLUTION = "r";

    static BundleContext bundleContext;
    static CubeLogger log = null;

    /**
     * Initialize the bundleContet object and the Logger.
     *
     * @param cp CubePlatform
     */
    private static void init(CubePlatform cp) {
        if (bundleContext == null) {
            bundleContext = cp.getBundleContext();
        }
        if (log == null) {
            log = new CubeLogger(bundleContext, ArchetypeParser.class.getName());
        }
    }

    public static Archetype parse(CubePlatform cp, String archetypeContent) throws ParseException, ArchetypeException {
        init(cp);
        return parse(cp, ParseUtils.stringToInputStream(archetypeContent));
    }

    public static Archetype parse(CubePlatform cp, URL url) throws ParseException, ArchtypeParsingException, ArchetypeException {
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

    public static Archetype parse(CubePlatform cp, InputStream stream) throws ParseException, ArchetypeException {
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

    private static Archetype buildArchtype(CubePlatform cp, XMLElement[] meta) throws ParseException, ArchetypeException {
        init(cp);
        Archetype archtype = null;
        if (meta != null && meta.length > 0) {
            XMLElement archtypeE = meta[0];
            if (archtypeE.getName()!= null && archtypeE.getName().equalsIgnoreCase(ARCHETYPE)) {
                archtype = new Archetype();
                archtype.setId(archtypeE.getAttribute(ID));
                archtype.setDescription(archtypeE.getAttribute(DESCRIPTION));
                archtype.setVersion(archtypeE.getAttribute(VERSION));


                // parse elements
                XMLElement[] elementTypess = archtypeE.getElements(ELEMENTS);
                if (elementTypess != null && elementTypess.length > 0) {
                    XMLElement elementTypes = elementTypess[0];
                    if (elementTypes != null) {
                        XMLElement[] cobjects = elementTypes.getElements();
                        // pass 1 : elements only (without their predicates)
                        for (int i = 0; i<cobjects.length; i++) {
                            XMLElement typeElement = cobjects[i];
                            if (typeElement != null) {
                                String namespace = typeElement.getNameSpace();
                                if (namespace == null) namespace = CorePluginFactory.NAMESPACE;
                                String name = typeElement.getName();
                                String id = typeElement.getAttribute(ID);

                                Element ee = new Element(archtype, namespace, name, id);
                                archtype.addElement(ee);
                            }
                        }
                        // pass 2 : add predicates

                        for (int i = 0; i<cobjects.length; i++) {
                            XMLElement typeElement = cobjects[i];
                            if (typeElement != null) {
                                String id = typeElement.getAttribute(ID);

                                Element e = archtype.getElement(id);
                                //System.out.println(".............. "+ e.getId());

                                XMLElement[] caracts = typeElement.getElements();
                                if (caracts != null) {
                                    for (int j = 0; j<caracts.length; j++) {
                                        XMLElement caract = caracts[j];
                                        if (caract != null) {
                                            String cnamespace = caract.getNameSpace();
                                            if (cnamespace == null) cnamespace = CorePluginFactory.NAMESPACE;
                                            String cname = caract.getName();
                                            String cdescription = caract.getAttribute(DESCRIPTION);
                                            String cobjectAttr = caract.getAttribute(OBJECT);
                                            Object object = cobjectAttr;
                                            if (cobjectAttr != null) {
                                                if (cobjectAttr.startsWith("@")) {
                                                    String tmp = cobjectAttr.substring(1);
                                                    object = archtype.getElement(tmp);
                                                }
                                            }
                                            Characteristic characteristic = new Characteristic(cnamespace, cname, e, object);
                                            e.addCharacteristic(characteristic);
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

                                    Goal g =new Goal(archtype, id, description);
                                    //System.out.println("GOAL:"+g.getId() + "/"+ g.getDescription());
                                    archtype.addGoal(g);
                                    // parse objectives
                                    XMLElement[] xmlobjectives =xmlgoal.getElements();
                                    if (xmlobjectives != null) {
                                        for (int j=0; j<xmlobjectives.length; j++) {
                                            XMLElement xmlobjective = xmlobjectives[j];
                                            if (xmlobjective != null) {
                                                String onamespace = xmlobjective.getNameSpace();
                                                if (onamespace == null) onamespace = CorePluginFactory.NAMESPACE;
                                                String oname = xmlobjective.getName();
                                                String odescription = xmlobjective.getAttribute(DESCRIPTION);
                                                String osubject = xmlobjective.getAttribute(SUBJECT);
                                                if (osubject == null || !osubject.startsWith("@")) throw new ParseException("No subject was specified for the Objective '"+oname+"'!");
                                                String oobject = xmlobjective.getAttribute(OBJECT);
                                                if (oobject == null) throw new ParseException("No object was specified for the Objective '"+oname+"'!");
                                                String resolution = xmlobjective.getAttribute(RESOLUTION);
                                                String opriority = xmlobjective.getAttribute(PRIORITY);
                                                int priority = Objective.DEFAULT_PRIORITY;
                                                if (opriority != null) priority = Integer.valueOf(opriority).intValue();

                                                Objective o = null;
                                                Element subject = archtype.getElement(osubject.substring(1));
                                                if (subject == null)
                                                    throw new ParseException("The Objective '"+oname+"' has unkown subject '"+osubject+"'");
                                                Object object = null;
                                                if (oobject.startsWith("@")) {
                                                    object = archtype.getElement(oobject.substring(1));
                                                } else {
                                                    object = oobject;
                                                }
                                                //log.info("Objective '"+oname+"' has the object: "+ oobject);
                                                o = new Objective(g, onamespace, oname, subject, object, resolution, priority, odescription);
                                                g.addObjective(o);

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
        if (archetype.getDescription() != null) out += "" + DESCRIPTION + "=\"" + archetype.getDescription() + "\" ";
        if (archetype.getVersion() != null) out += "" + VERSION + "=\"" + archetype.getVersion() + "\" ";

        out += ">\n";

        out += "<"+GOALS+">\n";

        for (Goal g: archetype.getGoals()) {
            out += "<"+GOAL+" id=\""+g.getId()+"\" description=\""+g.getDescription()+"\">\n";
            for (Objective o : g.getObjectives()) {
                if (o.getObject() instanceof Element)  {
                    if (o.getNamespace() == CorePluginFactory.NAMESPACE)
                        out += "<"+o.getName()+" s=\"@"+o.getSubject().getId()+"\" o=\"@"+((Element)o.getObject()).getId()+"\" r=\""+o.getResolutionStrategy()+"\" />\n";
                    else
                        out += "<"+o.getNamespace()+":"+o.getName()+" s=\"@"+o.getSubject().getId()+"\" o=\"@"+((Element)o.getObject()).getId()+"\" r=\""+o.getResolutionStrategy()+"\"/>\n";
                }
                else {
                    if (o.getObject() != null)
                        out += "<"+o.getNamespace()+":"+o.getName()+" s=\"@"+o.getSubject().getId()+"\" o=\""+o.getObject().toString()+"\"/>\n";
                    else
                        log.warning(o.getName() + " object value is null");
                }
            }
            out += "</"+GOAL+">\n";
        }
        out += "</"+GOALS+">\n";

        out += "<"+ELEMENTS+">\n";
        for (Element e : archetype.getElements()) {
            if (e.getCharacteristics().size() == 0)
                if (e.getNamespace() == CorePluginFactory.NAMESPACE)
                    out += "<"+e.getName()+" id=\""+e.getId()+"\"/>\n";
                else
                    out += "<"+e.getNamespace()+":"+e.getName()+" id=\""+e.getId()+"\"/>\n";
            else {
                if (e.getNamespace() == CorePluginFactory.NAMESPACE)
                    out += "<"+e.getName()+" id=\""+e.getId()+"\">\n";
                else
                    out += "<"+e.getNamespace()+":"+e.getName()+" id=\""+e.getId()+"\">\n";
                for (Characteristic c : e.getCharacteristics()) {
                    if (c.getObject() instanceof Element) {
                        if (e.getNamespace() == CorePluginFactory.NAMESPACE)
                           out += "<"+c.getName()+" o=\"@"+((Element)c.getObject()).getId()+"\"/>\n";
                        else
                            out += "<"+c.getNamespace()+":"+c.getName()+" o=\"@"+((Element)c.getObject()).getId()+"\"/>\n";
                    }
                    else {
                        if (e.getNamespace() == CorePluginFactory.NAMESPACE)
                            out += "<"+c.getName()+" o=\""+c.getObject()+"\"/>\n";
                        else
                            out += "<"+c.getNamespace()+":"+c.getName()+" o=\""+c.getObject()+"\"/>\n";
                    }
                }
                if (e.getNamespace() == CorePluginFactory.NAMESPACE)
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
