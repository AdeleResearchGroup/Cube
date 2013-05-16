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

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.PluginConfig;
import org.osgi.framework.BundleContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.liglab.adele.cube.CubeLogger;
import fr.liglab.adele.cube.agent.AgentConfig;
import fr.liglab.adele.cube.util.xml.XMLElement;
import fr.liglab.adele.cube.util.xml.XMLParser;

/**
 * Cube Agent XML Configuration file Parser.
 * 
 * @author debbabi
 *
 */
public class AgentConfigParser {

	public static final String CUBE_AGENT_CONFIG_EXTENSION = ".cube";
	
	private static final String AGENT = "agent";
	private static final String HOST = "host";
	private static final String PORT = "port";
    private static final String RMCHECKINTERVAL = "rmcheck-interval";
    private static final String KAINTERVAL = "keepalive-interval";
    private static final String KARETRY = "keepalive-retry";
	private static final String DEBUG = "debug";
	private static final String PERSIST = "persist";
	private static final String PERF = "perf";
	private static final String RESOLVER = "resolver";
	private static final String COMMUNICATOR = "communicator";
	private static final String ARCHETYPEURL = "archetypeUrl";
	private static final String PLUGINS = "plugins";
	private static final String PLUGIN = "plugin";
    private static final String PROPERTY = "property";
    private static final String NAME = "name";
	private static final String VALUE = "value";
	private static final String ID = "id";
	private static final String VERSION = "version";
	
	static BundleContext bundleContext;
	static CubeLogger log = null;
	
	/**
	 * Parse Cube Agent Configuration.
	 * 
	 * @param cp
	 * @param configContent
	 * @return
	 * @throws fr.liglab.adele.cube.util.parser.ParseException
	 */
	public static AgentConfig parse(CubePlatform cp, String configContent) throws ParseException {
		return parse(cp, ParseUtils.stringToInputStream(configContent));
	}
	
	public static AgentConfig parse(CubePlatform cp, URL url) throws ParseException {
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
	
	/**
	 * Parse Cube Agent Configuration.
	 * 
	 * @param cp
	 * @param stream
	 * @return
	 * @throws fr.liglab.adele.cube.util.parser.ParseException
	 */
	public static AgentConfig parse(CubePlatform cp, InputStream stream) throws ParseException {
		init(cp);		
		AgentConfig cac = null;
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
            log.error("Cannot open the agent cube config input stream: " + e.getMessage());
        } catch (ParseException e) {
        	log.error("Parsing error when parsing the XML file: " + e.getMessage());
        } catch (SAXParseException e) {
        	log.error("Error during agent cube config parsing at line " + e.getLineNumber() + " : " + e.getMessage());
        } catch (SAXException e) {
        	log.error("Parsing error when parsing (Sax Error) the XML file: " + e.getMessage());
        }

        if (meta == null || meta.length == 0) {
        	log.warning("The parsed Cube Agent Config file is empty!");
        }                   
        cac = buildConfig(cp, meta);
        return cac;
	}
	
	/**
	 * Build CubeAgentConfig object from the parsed XMLElements.
	 * 
	 * @param cp
	 * @param meta
	 * @return
	 * @throws fr.liglab.adele.cube.util.parser.ParseException
	 */
	private static AgentConfig buildConfig(CubePlatform cp, XMLElement[] meta) throws ParseException {
		init(cp);		
		AgentConfig cac = null;
		
		if (meta != null && meta.length > 0) {
			XMLElement cicelement = meta[0];
			/*
			 * get the cube-instance-config element
			 */			
			if (cicelement.getName()!= null && cicelement.getName().equalsIgnoreCase(AGENT)) {
				cac = new AgentConfig();				
				
				XMLElement[] configs = cicelement.getElements();
				if (configs != null && configs.length > 0) {										
					for (int i = 0; i<configs.length; i++) {
						XMLElement cconfig = configs[i];
						if (cconfig != null) {
							if (cconfig.getName().equalsIgnoreCase(HOST)) {
								cac.setHost(cconfig.getAttribute(VALUE));								
							} else if (cconfig.getName().equalsIgnoreCase(PORT)) {
								cac.setPort(new Long(cconfig.getAttribute(VALUE)).longValue());

							} else if (cconfig.getName().equalsIgnoreCase(RMCHECKINTERVAL)) {
                                cac.setRmCheckInterval(new Long(cconfig.getAttribute(VALUE)).longValue());
                            } else if (cconfig.getName().equalsIgnoreCase(KAINTERVAL)) {
                                cac.setKeepAliveInterval(new Long(cconfig.getAttribute(VALUE)).longValue());
                            } else if (cconfig.getName().equalsIgnoreCase(KARETRY)) {
                                cac.setKeepAliveRetry(new Long(cconfig.getAttribute(VALUE)).longValue());
                            } else if (cconfig.getName().equalsIgnoreCase(RESOLVER)) {
								cac.setResolverName(cconfig.getAttribute(VALUE));
							} else if (cconfig.getName().equalsIgnoreCase(COMMUNICATOR)) {
								cac.setCommunicatorName(cconfig.getAttribute(VALUE));
							} else if (cconfig.getName().equalsIgnoreCase(DEBUG)) {
								cac.setDebug(new Boolean(cconfig.getAttribute(VALUE)).booleanValue());
							} else if (cconfig.getName().equalsIgnoreCase(PERSIST)) {
								cac.setPersist(new Boolean(cconfig.getAttribute(VALUE)).booleanValue());
							} else if (cconfig.getName().equalsIgnoreCase(PERF)) {
								cac.setPerf(new Boolean(cconfig.getAttribute(VALUE)).booleanValue());
							} else if (cconfig.getName().equalsIgnoreCase(ARCHETYPEURL)) {
								cac.setArchetypeUrl(cconfig.getAttribute(VALUE));
							} else if (cconfig.getName().equalsIgnoreCase(PLUGINS)) {
								XMLElement[] extensions = cconfig.getElements(PLUGIN);
								if (extensions != null && extensions.length>0) {
									for (int j=0; j<extensions.length; j++) {
										XMLElement extension = extensions[j];										
										PluginConfig aec = new PluginConfig();

										if (extension.getAttribute(ID) != null) {
											aec.setId(extension.getAttribute(ID));
										} else {
											throw new ParseException("Plugin should have an ID!");
										}
                                        /*
										if (extension.getAttribute(VERSION) != null) {
											aec.setVersion(extension.getAttribute(VERSION));
										}*/
										XMLElement[] extensionContents = extension.getElements(PROPERTY);
										if (extensionContents != null && extensionContents.length >0) {
											for (int k=0; k<extensionContents.length; k++) {
												XMLElement extensionConfigurations = extensionContents[k];
                                                aec.addProperty(extensionConfigurations.getAttribute(NAME).toString(), extensionConfigurations.getAttribute(VALUE).toString());
											}
										}
										cac.addExtension(aec);
									}
								}
							}
						} else {							
						}
					}
				}									
			} else {
				log.error("Cube Agent Configuration XML file should starts with '"+AGENT+"' element!");
			}						
		}		
		return cac;
	}
	
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
			log = new CubeLogger(bundleContext, AgentConfigParser.class.getName());
		}
	}
}
