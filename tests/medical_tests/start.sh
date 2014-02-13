
rm -rf am/felix-cache 2>/dev/null
rm -rf felix-cache 2>/dev/null
rm -f  am/*.csv 2>/dev/null
rm -rf am/bundle 2>/dev/null
rm -rf am/load 2>/dev/null 
rm -rf am/*.arch 2>/dev/null 

mkdir am/load
cp -rf 1/amAggr.cube am/load/
cp -rf 1/amMed1.cube am/load/
#cp -rf $1/am*.cube am/load/

mkdir am/bundle





cp ../../lib/felix/*.jar am/bundle
cp ../../lib/ipojo/*.jar am/bundle

#cp ../../lib/commons/*.jar am/bundle
#cp ../../lib/cilia/*.jar am/bundle

cp ../../bin/cube-runtime-2.0.jar am/bundle
cp ../../bin/cube-console-2.0.jar am/bundle
cp ../../bin/cube-script-extension-2.0.jar am/bundle
cp ../../bin/cube-rm-monitoring-extension-2.0.jar am/bundle
#cp ../../bin/cube-cilia-extension-2.0.jar am/bundle

#cp ../../lib/commons/*.jar am/bundle
#cp ../../lib/felix/*.jar am/bundle
#cp ../../lib/ipojo/*.jar am/bundle
#cp ../../lib/cilia/*.jar am/bundle

cd am
java -jar bin/felix.jar 

cd ..