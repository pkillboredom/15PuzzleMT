
# The name of the class containing the "main" routine
MAINCLASS = QueueTester

CLASSPATH = .


build:
	javac -cp ${CLASSPATH}  *.java

run: build
	java -ea -cp ${CLASSPATH} ${MAINCLASS}

clean:
	rm -f *.class *~

