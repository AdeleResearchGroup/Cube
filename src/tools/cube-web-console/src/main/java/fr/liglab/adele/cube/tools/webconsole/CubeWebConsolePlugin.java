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


package fr.liglab.adele.cube.tools.webconsole;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.WebConsoleUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: debbabi
 * Date: 5/1/13
 * Time: 8:32 PM
 */
@SuppressWarnings("serial")
@Component(immediate=true)
@Provides
@Instantiate
public class CubeWebConsolePlugin extends AbstractWebConsolePlugin {

    /**
     * Label used by the web console.
     */
    @ServiceProperty(name = "felix.webconsole.label")
    private String m_label = "cube";

    /**
     * Title used by the web console.
     */
    @ServiceProperty(name = "felix.webconsole.title")
    private String m_title = "Agents";

    // templates
    private final String CUBE_AGENTS_TEMPLATE;


    public CubeWebConsolePlugin() {
        /*
        INSTANCES = readTemplate("/res/instances.html" );
        FACTORIES = readTemplate("/res/factories.html" );
        HANDLERS = readTemplate("/res/handlers.html" );
        FACTORY_DETAILS = readTemplate("/res/factory.html" );
        INSTANCE_DETAILS = readTemplate("/res/instance.html" );
        */
        CUBE_AGENTS_TEMPLATE = readTemplateFile( "/templates/agents.html" );;
    }

    @Override
    protected void renderContent(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws ServletException, IOException {
        // get request info from request attribute

        final RequestInfo reqInfo = new RequestInfo(request);
        // prepare variables

        DefaultVariableResolver vars = ( (DefaultVariableResolver) WebConsoleUtil.getVariableResolver(request) );
        /*
        if (reqInfo.instances) { // Instance
            if (reqInfo.name == null) { // All
                response.getWriter().print( INSTANCES );
            } else { // Specific
                vars.put("name", reqInfo.name); // Inject the name variable.
                response.getWriter().print( INSTANCE_DETAILS );
            }
        } else if (reqInfo.factories) { // Factory
            if (reqInfo.name == null) { // All
                response.getWriter().print( FACTORIES );
            } else { // Specific
                vars.put("name", reqInfo.name); // Inject the name variable.
                response.getWriter().print( FACTORY_DETAILS );
            }
        } else if (reqInfo.handlers) { // Handlers
            response.getWriter().print( HANDLERS );
            // No detailed view for handlers.
        } else {
            // Default
            response.getWriter().print( INSTANCES );
        }
        */
    }

    /**
     * Helper method loading a template file.
     * @param templateFile the template file name
     * @return the template
     */
    private final String readTemplate(final String templateFile) {
        InputStream templateStream = getClass().getResourceAsStream(
                templateFile);
        if (templateStream != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            try {
                int len = 0;
                while ((len = templateStream.read(data)) > 0) {
                    baos.write(data, 0, len);
                }
                return baos.toString("UTF-8");
            } catch (IOException e) {
                // don't use new Exception(message, cause) because cause is 1.4+
                throw new RuntimeException("readTemplateFile: Error loading "
                        + templateFile + ": " + e);
            } finally {
                try {
                    templateStream.close();
                } catch (IOException e) {
                    /* ignore */
                }

            }
        }

        // template file does not exist, return an empty string
        log("readTemplateFile: File '" + templateFile
                + "' not found through class " + this.getClass());
        return "";
    }

    @Override
    public String getLabel() {
        return m_label;
    }

    @Override
    public String getTitle() {
        return m_title;
    }

    /**
     * Parse request to extract the query.
     */
    private final class RequestInfo {
        /**
         * The extension.
         */
        public final String extension;
        /**
         * The path.
         */
        public final String path;
        /**
         * The instances.
         */
        public final boolean instances;
        /**
         * The factories.
         */
        public final boolean factories;
        /**
         * The handlers.
         */
        public final boolean handlers;

        /**
         * The specific factory or instance name.
         */
        public final String name;


        /**
         * Creates a RequestInfo.
         * @param request the request
         */
        protected RequestInfo( final HttpServletRequest request ) {
            String info = request.getPathInfo();
            // remove label and starting slash
            info = info.substring(getLabel().length() + 1);

            // get extension
            if (info.endsWith(".json")) {
                extension = "json";
                info = info.substring(0, info.length() - 5);
            } else {
                extension = "html";
            }

            if (info.startsWith("/")) {
                path = info.substring(1);

                instances = path.startsWith("instances");
                factories = path.startsWith("factories");
                handlers = path.startsWith("handlers");

                if (instances  && path.startsWith("instances/")) {
                    name = path.substring("instances".length() + 1);
                } else if (factories  && path.startsWith("factories/")) {
                    name = path.substring("factories".length() + 1);
                } else {
                    name = null;
                }
            } else {
                path = null;
                name = null;
                instances = false;
                factories = false;
                handlers = false;
            }

            request.setAttribute(CubeWebConsolePlugin.class.getName(), this);
        }

    }
}
