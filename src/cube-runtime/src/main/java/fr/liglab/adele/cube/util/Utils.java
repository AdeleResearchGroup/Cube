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

package fr.liglab.adele.cube.util;

import fr.liglab.adele.cube.AutonomicManager;

import java.util.UUID;

/**
 * General Purpose Util methods.
 * 
 * @author debbabi
 *
 */
public class Utils {
	
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}

    public static String hostFromURI(String uri) {
        if (uri != null && uri.length() > 0) {
            if (uri.startsWith("cube://")) {
                String tmp = uri.replaceFirst("cube://", "");
                if (tmp != null) {
                    return tmp.substring(0, tmp.indexOf(":"));
                }
            }
        }
        return "";
    }

    public static long portFromURI(String uri) {
        if (uri != null && uri.length() > 0) {
            if (uri.startsWith("cube://")) {
                String tmp = uri.replaceFirst("cube://", "");
                if (tmp != null) {
                    tmp = tmp.substring(tmp.indexOf(":") + 1, tmp.length());
                    if (tmp != null) {
                        if (tmp.contains("/")) {
                            return new Long(tmp.substring(0, tmp.indexOf("/"))).longValue();
                        } else {
                            return new Long(tmp).longValue();
                        }

                    }

                }
            }
        }
        return -1;
    }

    public static String GenerateUUID() {
        UUID uuid = UUID.randomUUID();
        return String.valueOf(uuid);
    }

    public static String toString(CharSequence charSequence) {
        final StringBuilder sb = new StringBuilder(charSequence.length());
        sb.append(charSequence);
        return sb.toString();
    }

    public static String evaluateValue(AutonomicManager am, String value) {
        String result = value;
        if (am != null && result != null && result.contains("${")) {
            int ps1 = result.indexOf("$");
            int ps2 = result.indexOf("}");
            CharSequence toModify = result.subSequence(ps1, ps2+1);
            if (toModify != null) {
                CharSequence pname = toModify.subSequence(2, toModify.length()-1);
                if (am != null) {
                    String pvalue = am.getArchetype().getAutonomicManager().getProperty(Utils.toString(pname));
                    if (pvalue != null) {
                        result = result.replace(toModify, pvalue);
                    } else {
                        System.out.println("[WARNING] value of Archetype Element '"+value+"' cannot be found among Autonomic Manager's properties!");
                    }
                }
            }
        }
        return result;
    }
}
