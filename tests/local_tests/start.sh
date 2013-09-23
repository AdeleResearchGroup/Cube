if [ $# -eq 0 ]; then
	echo "No test number supplied!"
	exit;
fi

rm -rf am/felix-cache 2>/dev/null
rm -rf felix-cache 2>/dev/null
rm -f  am/perf.csv 2>/dev/null
rm -rf am/bundle 2>/dev/null
rm -rf am/load 2>/dev/null 
rm -rf am/test.arch 2>/dev/null 

mkdir am/load
cp -rf $1/am.cube am/load/
cp -rf $1/test.arch am/

mkdir am/bundle

cp ../../bin/cube-runtime-2.0.jar am/bundle
cp ../../bin/cube-console-2.0.jar am/bundle

cp ../../lib/org.apache.felix.bundlerepository-1.6.2.jar am/bundle
cp ../../lib/org.apache.felix.fileinstall-3.2.0.jar am/bundle
cp ../../lib/org.apache.felix.gogo.command-0.8.0.jar am/bundle
cp ../../lib/org.apache.felix.gogo.runtime-0.8.0.jar am/bundle
cp ../../lib/org.apache.felix.gogo.shell-0.8.0.jar am/bundle
cp ../../lib/org.apache.felix.ipojo-1.8.6.jar am/bundle
cp ../../lib/org.apache.felix.ipojo.arch.gogo-1.0.1.jar am/bundle
cp ../../lib/org.apache.felix.ipojo.handler.jmx-1.4.0.jar am/bundle
cp ../../lib/org.osgi.service.obr-1.0.2.jar am/bundle

cd am
java -jar bin/felix.jar

cd ..