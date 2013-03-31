# removing cache and perf measure files
rm -rf felix-cache 2>/dev/null
rm -f perf.csv 2>/dev/null

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
cp ../../../../../bin/cube-core-1.2.jar bundle
cp ../../../../../bin/cube-gogo-commands-1.2.jar bundle
cp ../../../../../bin/cube-cilia-extension-1.2.jar bundle
cp ../../../../../bin/cube-internal-monitoring-1.2.jar bundle
cp ../../../../../bin/cilia-usecase-1.2.jar bundle
cp ../../../../../lib/org.apache.felix.bundlerepository-1.6.2.jar bundle
cp ../../../../../lib/org.apache.felix.fileinstall-3.2.0.jar bundle
cp ../../../../../lib/org.apache.felix.gogo.command-0.8.0.jar bundle
cp ../../../../../lib/org.apache.felix.gogo.runtime-0.8.0.jar bundle
cp ../../../../../lib/org.apache.felix.gogo.shell-0.8.0.jar bundle
cp ../../../../../lib/org.apache.felix.ipojo-1.8.0.jar bundle
cp ../../../../../lib/org.apache.felix.ipojo.arch.gogo-1.0.1.jar bundle
cp ../../../../../lib/org.apache.felix.ipojo.handler.jmx-1.4.0.jar bundle
cp ../../../../../lib/org.osgi.service.obr-1.0.2.jar bundle
cp ../../../../../lib/cilia-core-1.2.2-SNAPSHOT.jar bundle
cp ../../../../../lib/cilia-runtime-1.2.2-SNAPSHOT.jar bundle
cp ../../../../../lib/cilia-admin-1.2.2-SNAPSHOT.jar bundle

# launching OSGi felix
java -jar bin/felix.jar
