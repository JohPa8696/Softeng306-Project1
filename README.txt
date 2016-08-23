About
-------------
This project solves an NP hard task scheduling problem using an Iterative Deepening A* (IDA*) algorithm. 
Pseudocode can be found on the Github Wiki (https://github.com/JohPa8696/Softeng306-Project1/wiki/IDA*).

It can be run with visualisation and parallel processing.


Installation instructions
-------------
Import the project into Eclipse and export it as a jar. Invoke the jar from the command line with the following parameters:

java −jar scheduler.jar INPUT.dot P [OPTION] 

INPUT.dot	a task graph with integer weights in dot format 
P 			number of processors to schedule the INPUT graph on

Optional: 
−p N		use N cores for execution in parallel (default is sequential) 
−v 			visualise the search 
−o OUPUT	output file is named OUTPUT(default is INPUT−output.dot)


Authors
-------------
Kelvin Lau
Xiaohui Lin
Vincent Nio
Johnny Pham
Jack Wong