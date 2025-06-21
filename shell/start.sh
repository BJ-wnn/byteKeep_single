#!/bin/bash
# start.sh

APP_JAR="/Users/nanan/Documents/nan/code/byteKeep_single/target/bytekeep-0.0.1-SNAPSHOT.jar"
DEPENDENCIES="/Users/nanan/Documents/nan/code/byteKeep_single/target/libs/*"
MAIN_CLASS="org.wnn.bytekeep.ByteKeepSingleApplication"

java -cp "$APP_JAR:$DEPENDENCIES" $MAIN_CLASS