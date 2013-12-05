rm -rf felix-cache 2>/dev/null
rm -rf felix-cache 2>/dev/null
rm -f  *.csv 2>/dev/null
rm -rf bundle 2>/dev/null

mkdir bundle

cp ../../../lib/felix/*.jar bundle
cp ../../../lib/commons/*.jar bundle
cp ../../../lib/ipojo/*.jar bundle
cp ../../../lib/cilia/*.jar bundle

cp ../../../bin/*.jar bundle

#cp ../../lib/commons/*.jar bundle
#cp ../../lib/felix/*.jar bundle
#cp ../../lib/ipojo/*.jar bundle
#cp ../../lib/cilia/*.jar bundle

java -jar bin/felix.jar

