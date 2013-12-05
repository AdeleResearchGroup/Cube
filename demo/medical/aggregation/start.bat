REM parameters:
REM 1 : cleaning
REM 2 : cleaning + preparing bundle directory
REM no param : cleaning + preparing + starting cube
REM NOT YET IMPLEMENTED - see sh version if needed

REM removing cache and perf measure files
rmdir felix-cache /S /Q 
del perf.csv /Q
del bundle\*.jar /Q 

REM: to delete all subdirectories in a directory, but not the directory itself:> for /d %a in ("bundle\*") do rd /s /q "%a" 

REM creating the bundle directory if it does not exist 
if not exist bundle mkdir bundle

REM copying needed bundles

copy ..\..\..\lib\felix\* bundle
copy ..\..\..\lib\commons\* bundle
copy ..\..\..\lib\ipojo\* bundle
copy ..\..\..\lib\cilia\* bundle

copy ..\..\..\bin\*.jar bundle

REM launching OSGi felix
REM java -jar bin/felix.jar
java -jar bin/felix.jar