About
-------------
This project solves an NP hard task scheduling problem using an Iterative Deepening A* (IDA*) algorithm. 
Pseudocode can be found on the Github Wiki (https://github.com/JohPa8696/Softeng306-Project1/wiki/IDA*).

In addition to finding a solution to scheduling, it can also be run with visualisation and parallel processing.

Build instructions
-------------
Import the project into Eclipse. Graphstream libs need to be added for visualisation. This can be down by libs -> select all jar files -> right click -> build path -> add to build path. Export it as a jar to run. 


Execution Instructions
-------------
Invoke the jar from the command line with the following parameters:

java −jar scheduler.jar INPUT.dot P [OPTION] 

INPUT.dot	a task graph with integer weights in dot format 
P 			number of processors to schedule the INPUT graph on

Optional: 
−p N		use N cores for execution in parallel (default is sequential) 
−v 			visualise the search 
−o OUPUT	output file is named OUTPUT(default is INPUT−output.dot)


Additional information
-------------
Meeting Minutes     https://github.com/JohPa8696/Softeng306-Project1/wiki/Meeting-Minutes
Visualisation       https://github.com/JohPa8696/Softeng306-Project1/wiki/Visualization
Plan                https://github.com/JohPa8696/Softeng306-Project1/wiki/Planning
Issues              https://github.com/JohPa8696/Softeng306-Project1/issues
Final jar file      https://github.com/JohPa8696/Softeng306-Project1/releases
Research            https://www.youtube.com/watch?v=HhDhFsA3aro
                    https://www.youtube.com/watch?v=5LMXQ1NGHwU
                    https://researchspace.auckland.ac.nz/bitstream/handle/2292/5839/02whole.pdf?sequence=6
                    http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=6F2FD645241B4AC587BAE2EB5C5C3E7F?doi=10.1.1.29.4769&rep=rep1&type=pdf
                    https://books.google.co.nz/books?id=r5LfBwAAQBAJ&pg=PA53&lpg=PA53&dq=parallel%20window%20search&source=bl&ots=rInslu-TaK&sig=Ojszg9Hyvh8to2XTG7QbltED32I&hl=en&sa=X&ved=0ahUKEwjkmpPD89bOAhXHspQKHan9AGAQ6AEIMDAE#v=onepage&q=parallel%20window%20search&f=false

Authors
Name            ID        Username  Github ID	
--------------------------------------------------------				
Kelvin Lau      9682466   klau158   klevinlaughs
Xiaohui Lin     8586122   xlin504   xlin504
Vincent Nio     6563584   vnio666   vNeon
Johnny Pham     8563174   dpha010   JohPa8696
Jack Wong       5863577   lwon229   Jack-Wong94
