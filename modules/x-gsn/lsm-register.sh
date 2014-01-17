#!/bin/bash

for jarFile in $( ls targnnet/*jar ); do
     cp=$cp:./$jarFile
done
for jarFile in $( ls target/**/*jar ); do
     cp=$cp:./$jarFile
done

$JAVA_HOME/bin/java -classpath target/classes:$cp:conf org.openiot.gsn.metadata.LSM.utils "$@" &
