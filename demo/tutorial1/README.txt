Connecting two local components
-------------------------------

When an instance of A is created, Cube automatically finds or creates a new instance of B 
and connects the two instances.


1. go to server1/
2. run sh start.sh 
3. show the created Cube agents:

	g! agents

	--------------------------------------------------------------------------
	[1] cube://localhost:38001
	--------------------------------------------------------------------------

4. type the following command to create an instance of A component in the agent [1]:

	g! newi 1 component type=A

4. type the following command to show the [1]'s Cube Runtime Model :

	g! rm 1

	--------------------------------------------------------------------------
	------ UNMANAGED ----
	------ UNCHECKED ----
	------ VALID --------
	 + cube://localhost:38001/component/9dde7821-5aa4-46b7-a1e5-65636556415f
	    | PROPERTIES
	    |   type=B
	    | REFERENCES
	    |   inputs:
		|     18699f05-984d-48a2-97d0-d3b348698e85
	 + cube://localhost:38001/component/18699f05-984d-48a2-97d0-d3b348698e85
		| PROPERTIES
		|   type=A
		| REFERENCES
		|   outputs:
		|     9dde7821-5aa4-46b7-a1e5-65636556415f
	--------------------------------------------------------------------------



