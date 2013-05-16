TUTORIAL 3
==========

Connecting two remote components 
--------------------------------


## the first cube located on pc/ is declared to have also as a master (see pc/load/demo.agent)

	<plugin id="fr.liglab.adele.cube.core">
		<property name="master" value="true"/>
	</plugin>

1. go to pc/  
2. run sh start.sh 


3. go to gateway/
4. run sh start.sh 


5. in gateway, create a B instance:

	g! newi 1 component type=B


