#!/bin/sh

#find a JRE
if [ "$JAVA_HOME" != "" ]; then
	JAVA=$JAVA_HOME/bin/java
elif [ "$JAVA" != "" ]; then
	JAVA=$JAVA
else
	JAVA=`which java`
fi;

# define the entry point
MAIN_CLASS=org.dykman.dexter.Main

#where the script is run
DEXTER_HOME=`dirname $0`
GOSSAMER_HOME=`dirname $DEXTER_HOME`

#locate gossamer lib directory
LIB=$GOSSAMER_HOME/webapp/WEB-INF/lib
DEXTER_JAR=$LIB/dexter.jar
GETOPT_JAR=$DEXTER_HOME/gnu-getopt.jar
XMLAPIS_JAR=$LIB/xml-apis.jar
XERCES_JAR=$LIB/xercesImpl.jar

CLASSPATH=$DEXTER_JAR:$GETOPT_JAR:$XMLAPIS_JAR:$XERCES_JAR

${JAVA} -cp ${CLASSPATH} ${MAIN_CLASS} $@

exit $?
