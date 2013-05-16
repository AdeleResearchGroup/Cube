Connecting two local components 
-------------------------------

All A instances are connected to B instances that have at max 2 inputs. If no B instance has at max 2 inputs, Cube creates a new one. 
All B instances are connected to C instances. Each C instance is connected to only one B instance.


1. go to server1/
2. run sh start.sh 
3. show the created Cube agents:

	g! agents

	--------------------------------------------------------------------------
	[1] cube://localhost:38001
	--------------------------------------------------------------------------

4. type the following command several (3) times to create nstances of A component in the agent [1]:

	g! newi 1 component type=A

4. type the following command to show the [1]'s Cube Runtime Model :

	g! rm 1

