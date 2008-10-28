#!/bin/sh

if [ ! $JAVA_HOME ]; then
echo "Please set the JAVA_HOME environment variable to point to a valid Java install location."
exit 1
fi

if [ ! -f $JAVA_HOME/bin/java ]; then
echo "Please set the JAVA_HOME environment variable to point to a valid Java install location."
exit 1
fi

OPENFAST_HOME=$( cd -P -- "$(dirname -- "$0")" && pwd -P)/..
JAVA="$JAVA_HOME/bin/java"

CLASSPATH=$OPENFAST/lib/commons-cli-1.1.jar:$OPENFAST/${project.artifactId}-${project.version}.jar
MAIN=org.openfast.examples.tmplexch.DecodeMain
$JAVA $JAVA_OPTS -classpath $CLASSPATH $MAIN $*