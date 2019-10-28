Including ANTLR4 in the classpath on Linux:
export CLASSPATH="path_to_this_folder/antlr-4.7.1-complete.jar:$CLASSPATH";

On Windows:
SET CLASSPATH=.;path_to_this_folder\antlr-4.7.1-complete.jar;%CLASSPATH%


To compile, execute the command javac *.java in this folder (if Listener.java and/or Main.java fail to compile-take them out
of the directory, along with any Grammar*.java files if necessary, and compile again.

To run, execute the command java DBtest in this folder (do java Main instead if we get Listener.java to compile successfully).

NOTE: We couldn't get the parser and the database engine linked.
However, we have included the files Main.java and Listener.java
for you to read. This was our attempt at linking together the parser and the
database engine.

Development Log, Post-Production, and Test Logs can
all be found at http://github.tamu.edu/ncharchenko/315-P2-Team12/wiki/.
