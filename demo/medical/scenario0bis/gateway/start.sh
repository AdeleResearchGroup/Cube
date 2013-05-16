# parameters:
# 1 : cleaning
# 2 : cleaning + preparing bundle directory
# no param : cleaning + preparing + starting cube

# removing cache and perf measure files
rm -rf felix-cache 2>/dev/null
rm -f perf.csv 2>/dev/null
rm -rf bundle 2>/dev/null


if [ $# -eq 1 ]; then
if [ $1 -eq 1 ]; then
	echo ""
	echo "cleaning..."
	echo ""
	exit
fi
fi

# creating the bundle directory if does not exist
bundle="bundle"
a=$(ls $bundle 2>/dev/null)
exist=$?
if [ $exist -eq 0 ]; then
	echo 
else
	echo 
	mkdir $bundle
fi

# copying needed bundles
cp ../../../../bin/cube-core-2.0.jar bundle
cp ../../../../bin/cube-console-2.0.jar bundle
cp ../../../../bin/cube-osgi-plugin-2.0.jar bundle
cp ../../../../bin/cube-rm-monitoring-plugin-2.0.jar bundle
#cp ../../../../bin/web-console-branding-2.0.jar bundle
#cp ../../../../bin/web-console-2.0.jar bundle
# cp ../../../../bin/cube-internal-monitoring-extension-2.0.jar bundle




cp ../../../../lib/org.apache.felix.httplite.complete-0.1.4.jar bundle
cp ../../../../lib/pax-web-jetty-bundle-0.7.0.jar bundle

cp ../../../../lib/commons-io-2.4.jar bundle
cp ../../../../lib/commons-fileupload-1.3.jar bundle
cp ../../../../lib/json-20090211_1.jar bundle

#cp ../../../../lib/org.apache.felix.httplite.core-0.1.4.jar bundle
cp ../../../../lib/org.apache.felix.http.api-2.2.0.jar bundle
#cp ../../../../lib/org.apache.felix.http.base-2.2.0.jar bundle
#cp ../../../../lib/org.apache.felix.webconsole-2.0.2.jar bundle
cp ../../../../lib/org.apache.felix.webconsole-4.0.0.jar bundle

cp ../../../../lib/slf4j-log4j12-1.6.4.jar bundle

cp ../../../../lib/org.apache.felix.ipojo.webconsole-1.6.0.jar bundle

cp ../../../../lib/org.apache.felix.bundlerepository-1.6.2.jar bundle
cp ../../../../lib/org.apache.felix.fileinstall-3.2.0.jar bundle
cp ../../../../lib/org.apache.felix.gogo.command-0.8.0.jar bundle
cp ../../../../lib/org.apache.felix.gogo.runtime-0.8.0.jar bundle
cp ../../../../lib/org.apache.felix.gogo.shell-0.8.0.jar bundle
cp ../../../../lib/org.apache.felix.ipojo-1.8.6.jar bundle
cp ../../../../lib/org.apache.felix.ipojo.arch.gogo-1.0.1.jar bundle
cp ../../../../lib/org.apache.felix.ipojo.handler.jmx-1.4.0.jar bundle
cp ../../../../lib/org.osgi.service.obr-1.0.2.jar bundle

if [ $# -eq 1 ]; then
if [ $1 -eq 2 ];then
	echo ""
	echo "cleaning..."
	echo "preparing bundle directory..."
	echo ""
	exit
fi
fi

# launching OSGi felix
java -jar bin/felix.jar
