VectorClockZeroMQ
=================

Vector clock implementation using ZeroMQ.
* [VectorClock](http://en.wikipedia.org/wiki/Vector_clock)
* [ZeroMQ](http://zeromq.org/) 

To execute a server
-------

	java -jar VectorClock.jar server numberOfProcesses
	
Example:
	java -jar VectorClock.jar server 3
	
	
To execute a client
-------

	java -jar VectorClock.jar client serverIP
	
Example:
	java -jar VectorClock.jar client 192.168.0.1
