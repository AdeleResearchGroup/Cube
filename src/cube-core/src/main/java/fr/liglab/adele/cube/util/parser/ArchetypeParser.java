/*
 * Copyright 2011-2012 Adele Research Group (http://adele.imag.fr/) 
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.ICubePlatform;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.ArchtypeParsingException;
import fr.liglab.adele.cube.archetype.Constraint;
import fr.liglab.adele.cube.archetype.ManagedElement;
import fr.liglab.adele.cube.archetype.Variable;
import fr.liglab.adele.cube.archetype.GlobalConfig;
import fr.liglab.adele.cube.extensions.IExtensionFactory;
import fr.liglab.adele.cube.util.xml.XMLElement;
import fr.liglab.adele.cube.util.xml.XMLParser;

/**
 * Cube Archtype SAX parser.
 * 
 * @author debbabi
 */
public class ArchetypeParser {
	
	public static final String ARCHETYPE_EXTENSION = ".arch";
	
	public static final String CUBE = "cube";
	public static final String ARCHETYPE = "archetype";
	public static final String ID = "id";
	public static final String NAME = "name";	
	public static final String VERSION = "version";
	public static final String DESCRIPTION = "description";
	public static final String TYPES = "types";
	public static final String CONSTRAINTS = "constraints";
	public static final String VARIABLES = "variables";
	public static final String VAR = "var";
	public static final String TYPE = "type";
	public static final String GLOBAL_CONFIGS = "global-configs";

	

	static BundleContext bundleContext;
	static CubeLogger log = null;	
		
	
	public static Archetype parse(ICubePlatform cp, String archetypeContent) throws ParseException {
		init(cp);
		return parse(cp, ParseUtils.stringToInputStream(archetypeContent));
	}
	
	public static Archetype parse(ICubePlatform cp, URL url) throws ParseException, ArchtypeParsingException {		 
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
	
	public static Archetype parse(ICubePlatform cp, InputStream stream) throws ParseException {
		init(cp);
		Archetype archtype = null;		
		XMLElement[] meta = null;
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            XMLParser handler = new XMLParser();
            parser.setContentHandler(handler);
            /*
            parser.setFeature("http://xml.org/sax/features/validation",
                    false); //TODO: true
            parser.setFeature("http://apache.org/xml/features/validation/schema",
                    false); //TODO: true
			*/
            parser.setErrorHandler(handler);
            /*
            if (! m_ignoreLocalXSD) {
                parser.setEntityResolver(new SchemaResolver());
            }
			*/            
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
	
	/**
	 * Construct the archtype
	 * @param ca
	 * @param meta
	 * @return
	 * @throws ParseException
	 * @throws ArchtypeParsingException
	 */
	private static Archetype buildArchtype(ICubePlatform cp, XMLElement[] meta) throws ParseException {
		init(cp);
		Archetype archtype = null;		
		if (meta != null && meta.length > 0) {
			XMLElement archtypeE = meta[0];
			if (archtypeE.getName()!= null && archtypeE.getName().equalsIgnoreCase(ARCHETYPE)) {
				archtype = new Archetype();
				archtype.setId(archtypeE.getAttribute(ID));
				archtype.setName(archtypeE.getAttribute(NAME));
				archtype.setVersion(archtypeE.getAttribute(VERSION));
				archtype.setDescription(archtypeE.getAttribute(DESCRIPTION));				
				
				
				XMLElement[] elementTypess = archtypeE.getElements(TYPES);
				if (elementTypess != null && elementTypess.length > 0) {
					XMLElement elementTypes = elementTypess[0];
					if (elementTypes != null) {
						XMLElement[] cobjects = elementTypes.getElements();
						for (int i = 0; i<cobjects.length; i++) {
							XMLElement typeElement = cobjects[i];
							if (typeElement != null) {
								String namespace = typeElement.getNameSpace();								
								IExtensionFactory c = cp.getExtensionFactory(namespace);
								if (c != null) {
									ArchetypeParserPlugin parser = c.getArchetypeParserPlugin();
									if (parser != null) {
										try {
											ManagedElement cmo = parser.parseType(typeElement, archtype);
											archtype.addType(cmo);
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
				
				XMLElement[] constraintss = archtypeE.getElements(CONSTRAINTS);
				if (constraintss != null && constraintss.length > 0) {
					XMLElement constraints = constraintss[0];
					if (constraints != null) {
						XMLElement[] constraintsContent = constraints.getElements();						
						if (constraintsContent != null && constraintsContent.length>0) {
							for (int i=0; i<constraintsContent.length; i++) {
								if (constraintsContent[i].getName().equalsIgnoreCase(VARIABLES)) {
									XMLElement[] vars = constraintsContent[i].getElements();
									if (vars != null && vars.length>0) {
										for (int j=0; j<vars.length; j++) {
											String id = vars[j].getAttribute(ID);
											String type = vars[j].getAttribute(TYPE);
											Variable v = new Variable(id, type);
											archtype.addVariable(v);
										}
										
									}									
								} else {
									XMLElement constr = constraintsContent[i];
									if (constr != null) {
										String namespace = constr.getNameSpace();								
										IExtensionFactory c = cp.getExtensionFactory(namespace);
										if (c != null) {
											ArchetypeParserPlugin parser = c.getArchetypeParserPlugin();
											if (parser != null) {
												try {
													Constraint cmo = parser.parseConstraint(constr, archtype);
													archtype.addConstraint(cmo);
												} catch (ParseException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										}
									}
								}
							}
							
						}
						
						XMLElement[] cconstraints = constraints.getElements();
						for (int i = 0; i<cconstraints.length; i++) {
							XMLElement cconstraint = cconstraints[i];
							if (cconstraint != null) {
								
							}
						}
					}
				}
				/*
				XMLElement[] constraintss = archtypeE.getElements(__Archetype.CONSTRAINTS);
				if (constraintss != null && constraintss.length > 0) {
					XMLElement constraints = constraintss[0];
					if (constraints != null) {
						XMLElement[] cconstraints = constraints.getElements(__Archetype.CONSTRAINT);
						for (int i = 0; i<cconstraints.length; i++) {
							XMLElement cconstraint = cconstraints[i];
							if (cconstraint != null) {				
								
								String id = cconstraint.getAttribute(__Archetype.ID);
								String variables = cconstraint.getAttribute(__Archetype.CONSTRAINT_VARIABLES);
								String priority = cconstraint.getAttribute(__Archetype.CONSTRAINT_PRIORITY);
								CConstraint cc = null;		
								if (priority != null) {
									cc = new CConstraint(id, new Integer(priority).intValue(), archtype);
								} else {
									cc = new CConstraint(id, 0, archtype);
								}								
								
								if (variables != null && variables.length() > 0) {
									String[] tmp = variables.split(" ");
									if (tmp != null && tmp.length > 0) {
										for (int j=0; j<tmp.length; j++) {
											String var = tmp[j];
											String[] tmp2 = var.split(":");
											if (tmp2 != null && tmp2.length == 2) {
												List<CType> cots = archtype.getCTypes(tmp2[1]);
												if (cots.size() > 0 ) {
													//TODO throw exception if there are more that 1 cobjecttype!
													CVariable cv = new CVariable(tmp2[0], cots.get(0));												
													cc.addVariable(cv);
												}
												
											}
										}
									}
								}
							
								XMLElement[] predicatsss = cconstraint.getElements();
								if (predicatsss != null) {
  								    // If there is only one predicate, this will be the topPredicate of the constraint!
 									if (predicatsss.length == 1) {
										XMLElement xmlpredicat = predicatsss[0];
										ConstraintResolver cp = parsePredicate(xmlpredicat, cc,null, ci);
										if (cp != null) {																							
											//cp.setId(archtype.getCubeInstance().getId().toString() + "/archtype/" + cc.getId() + "/0");
											cc.setTopPredicate(cp);
										}
										 // If there is more than one predicate, so create an "And" topPredicate, and add the declared predicates as childs!
									} else if (predicatsss.length > 1) {
										And and = new And(cc, null);
										and.setId(new CPredicateID(archtype.getCubeInstance().getId(), cc.getId() + "/0"));
										for (int j=0; j<predicatsss.length; j++) {
											XMLElement xmlpredicat = predicatsss[j];										
											ConstraintResolver cp = parsePredicate(xmlpredicat, cc, and, ci);											
											if (cp != null) {
												try {
													and.addPredicate(cp);
												} catch (ArchtypeParsingException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}												
											}
										}
										cc.setTopPredicate(and);
									}																		
								}																
								archtype.addCConstraint(cc);
							}
						}
					}
				}
				*/
				
				XMLElement[] configsss = archtypeE.getElements(GLOBAL_CONFIGS);
				if (configsss != null && configsss.length > 0) {
					XMLElement configss = configsss[0];
					if (configss != null) {
						XMLElement[] configs = configss.getElements();
						for (int i = 0; i<configs.length; i++) {
							XMLElement cconfig = configs[i];
							if (cconfig != null) {
								String namespace = cconfig.getNameSpace();								
								IExtensionFactory c = cp.getExtensionFactory(namespace);
								if (c != null) {									
									ArchetypeParserPlugin parser = c.getArchetypeParserPlugin();
									if (parser != null) {										
										try {
											GlobalConfig cc = parser.parseGlobalConfig(cconfig, archtype);
											archtype.addGlobalConfig(cc);											
										} catch (ParseException e) {
											//e.printStackTrace();
											log.error("Global configuration not parsed!");
										}
									}
								} else {
									log.warning("No ParserPlugin found for the namespace " + namespace + "!");
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

	/**
	 * Par predicates
	 * @param xmlpredicat
	 * @param constraint
	 * @param parent
	 * @param ci
	 * @return
	 * @throws ArchtypeParsingException
	 */
	/*
	private static ConstraintResolver parsePredicate(XMLElement xmlpredicat, CConstraint constraint, ConstraintResolver parent, __CubeAgent ci) throws ArchtypeParsingException {		
				
		if (xmlpredicat != null) {			
			if (xmlpredicat.getName().equalsIgnoreCase(And.NAME)) {
				And and = new And(constraint, parent);
				if (parent == null) {
					and.setId(new CPredicateID(constraint.getArchtype().getCubeInstance().getId(), constraint.getId() + "/0"));
				} else {
					and.setId(new CPredicateID(parent.getId().toString() + "/" + parent.getPredicates().size()));
				}
				XMLElement[] childs = xmlpredicat.getElements();
				if (childs != null && childs.length > 0) {
					for (int i=0; i<childs.length; i++) {
						ConstraintResolver pp = parsePredicate(childs[i], constraint, and, ci);			
						if (pp != null) {
							pp.setId(new CPredicateID(and.getId().toString() + "/" + i));
							and.addPredicate(pp);
						} else {							
							throw new ArchtypeParsingException("Where was an error while parsing the predicate " + childs[i].getName());
						}
					}
				}								
				return and;
			} else if (xmlpredicat.getName().equalsIgnoreCase(Or.NAME)){
				Or or = new Or(constraint, parent);
				XMLElement[] childs = xmlpredicat.getElements();
				if (childs != null && childs.length > 0) {
					for (int i=0; i<childs.length; i++) {
						or.addPredicate(parsePredicate(childs[i], constraint, or, ci));
					}
				}
				return or;
			} else if (xmlpredicat.getName().equalsIgnoreCase(Not.NAME)) {
				Not not = new Not(constraint, parent);
				XMLElement[] childs = xmlpredicat.getElements();
				if (childs != null && childs.length > 0) {
					for (int i=0; i<childs.length; i++) {
						not.addPredicate(parsePredicate(childs[i],constraint, not,ci));
					}
				}
				return not;
			} else if (xmlpredicat.getName().equalsIgnoreCase(If.NAME)) {
				throw new ArchtypeParsingException("If predicat parsing not yet implemented!!");
			} else {						
				String namespace = xmlpredicat.getNameSpace();													
				IExtensionFactory c = ci.getCubePlatform().getExtensionFactory(namespace);
				if (c != null) {
					ArchetypeParserPlugin parser = c.getArchetypeParserPlugin();
					if (parser != null) {
						try {	
							ConstraintResolver pp = parser.parseCPredicate(xmlpredicat, constraint, parent);
							if (pp != null) {
								pp.setId(new CPredicateID(parent.getId().toString() + "/" + parent.getPredicates().size()));							
								return pp;
							} else {
								throw new ArchtypeParsingException("Where was an error while parsing the predicate " + xmlpredicat.getName());
							}
							
						} catch (ArchtypeParsingException e) {
							//e.printStackTrace();
							log.error("Predicate not parsed!");
							log.error(e.getMessage());
						}
					}
				}
			}
		}		
		return null;
	}*/
	
	/*
	public static Archetype parse(__CubeAgent ci, URL url) throws ParseException, ArchtypeParsingException {		 
		bundleContext = ci.getBundleContext();
		log = new CubeLogger(bundleContext, ArchtypeParser.class.getName());		
		InputStream fis;
		try {
			fis = url.openStream();
			return parse(ci, fis);
		} catch (IOException e) {
			log.error("Error when trying to open File.");
		}
		return null;
	}
	*/
	/*
	public static Archetype parse(__CubeAgent ci, File archtypeFile) throws ParseException, ArchtypeParsingException {
		bundleContext = ci.getBundleContext();
		log = new CubeLogger(bundleContext, ArchtypeParser.class.getName());		
		Archetype archtype = null;		
		if (archtypeFile != null) {
			if (archtypeFile.isDirectory()) {
	    		// Traverse the directory and parse all files.
	    		log.error(archtypeFile.getName() + " is a directory, not a cube's archtype file!");
	    	} else if (archtypeFile.getName().endsWith(ARCHTYPE_EXTENSION)) { // Detect XML by extension,
	    														  // others are ignored.
		        try {
		            InputStream stream = null;
		            URL url = archtypeFile.toURI().toURL();
		            if (url == null) {
		            	log.warning("Cannot find the archtype file : " + archtypeFile.getAbsolutePath());
		            } else {
		                stream = url.openStream();
		                return parse(ci, stream);
		            }
		        } catch (MalformedURLException e) {
		        	log.error("Cannot open the archtype input stream from " + archtypeFile.getAbsolutePath() + ": " + e.getMessage());
		        } catch (IOException e) {
		        	log.error("Cannot open the archtype input stream: " + archtypeFile.getAbsolutePath() + ": " + e.getMessage());		            
		        }        
	    	}
		} else {
			log.error("Archtype file not found!");
		}
		return archtype;
	}
	*/
	/*
	public static Archetype parse(__CubeAgent ci, InputStream stream) throws ParseException, ArchtypeParsingException {
		bundleContext = ci.getBundleContext();
		log = new CubeLogger(bundleContext, ArchtypeParser.class.getName());		
		Archetype archtype = null;		
		XMLElement[] meta = null;
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            XMLParser handler = new XMLParser();
            parser.setContentHandler(handler);
            
            //parser.setFeature("http://xml.org/sax/features/validation",
            //        false); //TODO: true
            //parser.setFeature("http://apache.org/xml/features/validation/schema",
            //        false); //TODO: true
			
            parser.setErrorHandler(handler);
            
            //if (! m_ignoreLocalXSD) {
            //    parser.setEntityResolver(new SchemaResolver());
            }
			          
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
        archtype = buildArchtype(ci, meta);
        return archtype;
	}*/
	/*
	public static Archetype parse(__CubeAgent ci, String archtype) throws ParseException, Exception {
		bundleContext = ci.getBundleContext();
		log = new CubeLogger(bundleContext, ArchtypeParser.class.getName());		
		return parse(ci, ParseUtils.stringToInputStream(archtype));
	}*/
			
	/**
	 * Initialize the bundleContet object and the Logger.
	 * 
	 * @param cp CubePlatform
	 */
	private static void init(ICubePlatform cp) {
		if (bundleContext == null) {
			bundleContext = cp.getBundleContext();
		}
		if (log == null) {
			log = new CubeLogger(bundleContext, ArchetypeParser.class.getName());
		}
	}
	
	public static String toXmlString(Archetype archetype) {
		String out = "";
		out += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		out += "<cube ";
		Set<String> keys = archetype.getNamespaces().keySet();
		for (String key : keys) {
			out += "xmlns:" + archetype.getNamespaces().get(key) + "=\"" + key + "\" ";
		}
		out += ">\n";				
		out += "<"+ARCHETYPE+" ";

		if (archetype.getId() != null) out += "" + ID + "=\"" + archetype.getId() + "\" ";
		if (archetype.getName() != null) out += "" + NAME + "=\"" + archetype.getName() + "\" ";
		if (archetype.getVersion() != null) out += "" + VERSION + "=\"" + archetype.getVersion() + "\" ";
		if (archetype.getDescription() != null) out += "" + DESCRIPTION + "=\"" + archetype.getDescription() + "\" ";
		out += ">\n";
		
		out += "<"+TYPES+">\n";
		List<ManagedElement> cobjects = archetype.getTypes();
		for (ManagedElement cmo : cobjects) {
			out += cmo.toXMLString(archetype.getNamespaces().get(cmo.getNamespace()));
		}
		out += "</"+TYPES+">\n";
		
		out += "<"+CONSTRAINTS+">\n";
		List<Variable> variables = archetype.getVariables();
		out += "<"+VARIABLES+">\n";
		for (Variable v : variables) {			
			out += "<var id=\""+v.getId()+"\" type=\""+v.getType()+"\"/>\n";			
		}
		out += "</"+VARIABLES+">\n";
		List<Constraint> tmp = archetype.getConstraints();
		for (Constraint tmp2 : tmp) {
			out += tmp2.toXMLString(archetype.getNamespaces().get(tmp2.getNamespace()));
		}
		out += "</"+CONSTRAINTS+">\n";
				
		out += "<"+GLOBAL_CONFIGS+">\n";
		List<GlobalConfig> cconfigs = archetype.getGlobalConfigs();
		for (GlobalConfig cmo : cconfigs) {
			out += cmo.toXMLString(archetype.getNamespaces().get(cmo.getNamespace()));
		}
		out += "</"+GLOBAL_CONFIGS+">\n";
		
		out += "</"+ARCHETYPE+">\n";
		out += "</cube>\n";
		return out;
	}
}
