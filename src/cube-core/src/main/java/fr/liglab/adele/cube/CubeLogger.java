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

package fr.liglab.adele.cube;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Cube logger
 * 
 */
public class CubeLogger {

    /**
     * The Log Level ERROR.
     */
    public static final int ERROR = 1;

    /**
     * The Log Level WARNING.
     */
    public static final int WARNING = 2;

    /**
     * The Log Level INFO.
     */
    public static final int INFO = 3;

    /**
     * The Log Level DEBUG.
     */
    public static final int DEBUG = 4;
    
    public static final String CUBE_LOG_LEVEL = "cube.log.level";

    private BundleContext m_context;
    private String m_name;
    private int m_level;

    public CubeLogger(String name, int level) {
        this(null, name, level);
    }
        
    public CubeLogger(BundleContext context, String name, int level) {
        
        m_name = name;
        m_context = context;
        m_level = level;
    }
    
    public CubeLogger(BundleContext context, String name) {
    	this(context, name, getDefaultLevel(context));
    }

    public void setName(String name) {
    	this.m_name = name;
    }
    
    /**
     * Logs a message.
     * @param level the level of the message
     * @param msg the the message to log
     */
    public void log(int level, String msg) {
        if (m_level >= level) {
            dispatch(level, msg);
        }
    }

    /**
     * Logs a message with an exception.
     * @param level the level of the message
     * @param msg the message to log
     * @param exception the exception attached to the message
     */
    public void log(int level, String msg, Throwable exception) {
        if (m_level >= level) {
            dispatch(level, msg, exception);
        }
    }
    
    public void debug(String msg) {
    	dispatch(this.DEBUG, msg);
    }
    
    public void error(String msg) {
    	dispatch(this.ERROR, msg);
    }
    
    public void info(String msg) {
    	dispatch(this.INFO, msg);
    }
    
    public void warning(String msg) {
    	dispatch(this.WARNING, msg);
    }
    
    /**
     * Internal log method. 
     * @param level the level of the message.
     * @param msg the message to log
     */
    private void dispatch(int level, String msg) {
        LogService log = null;
        ServiceReference ref = null;
        if (m_context != null) {
            try {
                ref = m_context.getServiceReference(LogService.class.getName());
                if (ref != null) {
                    log = (LogService) m_context.getService(ref);
                }
            } catch (IllegalStateException e) {
                // Handle the case where the iPOJO bundle is stopping
            }
        }
        String message = null;
        switch (level) {
            case DEBUG:
                message = "[DEBUG] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_DEBUG, message);
                } else {
                    System.err.println(message);
                }
                break;
            case ERROR:
                message = "[ERROR] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_ERROR, message);
                } else {
                    System.err.println(message);
                }
                break;
            case INFO:
                message = "[INFO] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_INFO, message);
                } else {
                    System.err.println(message);
                }
                break;
            case WARNING:
                message = "[WARNING] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_WARNING, message);
                } else {
                    System.err.println(message);
                }
                break;
            default:
                message = "[UNKNOWN] " + m_name + " : " + msg;
                System.err.println(message);
                break;
        }

        if (log != null) {
            m_context.ungetService(ref);
        }
    }

    /**
     * Internal log method.
     * @param level the level of the message.
     * @param msg the message to log
     * @param exception the exception attached to the message
     */
    private void dispatch(int level, String msg, Throwable exception) {
        LogService log = null;
        ServiceReference ref = null;
        if (m_context != null) {
            try {
                ref = m_context.getServiceReference(LogService.class.getName());
                if (ref != null) {
                    log = (LogService) m_context.getService(ref);
                }
            } catch (IllegalStateException e) {
                // Handle the case where the iPOJO bundle is stopping
            }
        }

        String message = null;
        switch (level) {
            case DEBUG:
                message = "[DEBUG] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_DEBUG, message, exception);
                } else {
                    System.err.println(message);
                    exception.printStackTrace();
                }
                break;
            case ERROR:
                message = "[ERROR] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_ERROR, message, exception);
                } else {
                    System.err.println(message);
                    exception.printStackTrace();
                }
                break;
            case INFO:
                message = "[INFO] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_INFO, message, exception);
                } else {
                    System.err.println(message);
                    exception.printStackTrace();
                }
                break;
            case WARNING:
                message = "[WARNING] " + m_name + " : " + msg;
                if (log != null) {
                    log.log(LogService.LOG_WARNING, message, exception);
                } else {
                    System.err.println(message);
                    exception.printStackTrace();
                }
                break;
            default:
                message = "[UNKNOWN] " + m_name + " : " + msg;
                System.err.println(message);
                exception.printStackTrace();
                break;
        }

        if (log != null) {
            m_context.ungetService(ref);
        }
    }
    /**
     * Gets the default logger level.
     * The property is searched inside the framework properties, 
     * the system properties, and in the manifest from the given 
     * bundle context. By default, set the level to {@link Logger#WARNING}. 
     * @param context the bundle context.
     * @return the default log level.
     */
    private static int getDefaultLevel(BundleContext context) {
    	String level = null;
        // First check in the framework and in the system properties
    	if (context != null) {
    		level = context.getProperty(CubeLogger.CUBE_LOG_LEVEL);
    	}
        if (level != null) {
            if (level.equalsIgnoreCase("info")) {
                return INFO;
            } else if (level.equalsIgnoreCase("debug")) {
                return DEBUG;
            } else if (level.equalsIgnoreCase("warning")) {
                return WARNING;
            } else if (level.equalsIgnoreCase("error")) {
                return ERROR;
            }
        }
        
        // Either l is null, either the specified log level was unknown
        // Set the default to WARNING
        return WARNING;
        
    }

}