/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.liglab.adele.cube.util;

import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.CubeLogger;

/**
 * Not yet used!
 * 
 * @author debbabi
 *
 */
public class Profiler {
	
	private static CubeLogger LOG = null;
	
	public interface Operation<T> {
		public T execute() throws Exception;
	}

	public static <T> T profile(BundleContext btx, Operation<T> op, long timeout, String message)
			throws Exception {
		if (LOG == null) LOG = new CubeLogger(btx, Profiler.class.getName());
		long start = System.currentTimeMillis();
		T res = op.execute();
		long end = System.currentTimeMillis();
		if (end - start > timeout) {
			if (LOG !=null) LOG.info("Elapsed " + (end - start) + " ms: " + message);
		}
		return res;
	}
}
