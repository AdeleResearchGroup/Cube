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
cp ../../../../bin/console-extension-2.0.jar bundle
cp ../../../../bin/osgi-extension-2.0.jar bundle
# cp ../../../../bin/cube-internal-monitoring-extension-2.0.jar bundle

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
