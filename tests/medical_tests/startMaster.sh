
rm -rf amMaster/felix-cache 2>/dev/null
rm -rf felix-cache 2>/dev/null
rm -f  amMaster/*.csv 2>/dev/null
rm -rf amMaster/bundle 2>/dev/null
rm -rf amMaster/load 2>/dev/null 
rm -rf amMaster/*.arch 2>/dev/null 

mkdir amMaster/load
cp -rf 1/amMaster.cube amMaster/load/
#cp -rf $1/amMaster*.cube amMaster/load/

mkdir amMaster/bundle





cp ../../lib/felix/*.jar amMaster/bundle
cp ../../lib/ipojo/*.jar amMaster/bundle

#cp ../../lib/commons/*.jar amMaster/bundle
#cp ../../lib/cilia/*.jar amMaster/bundle

cp ../../bin/cube-runtime-2.0.jar amMaster/bundle
cp ../../bin/cube-console-2.0.jar amMaster/bundle
cp ../../bin/cube-script-extension-2.0.jar amMaster/bundle
cp ../../bin/cube-rm-monitoring-extension-2.0.jar amMaster/bundle
#cp ../../bin/cube-cilia-extension-2.0.jar am/bundle

#cp ../../lib/commons/*.jar am/bundle
#cp ../../lib/felix/*.jar am/bundle
#cp ../../lib/ipojo/*.jar am/bundle
#cp ../../lib/cilia/*.jar am/bundle

cd amMaster
java -jar bin/felix.jar 

cd ..