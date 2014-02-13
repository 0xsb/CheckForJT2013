#!/bin/bash
export JAVA_HOME=/opt/jdk1.5.0_21
PATH=$JAVA_HOME/bin:$PATH:$HOME/bin
export PATH
export CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar

TEMP_CLASSPATH=
for file in $(ls ../lib);do
TEMP_CLASSPATH=$TEMP_CLASSPATH:../lib/$file
done
CLASSPATH=.:$TEMP_CLASSPATH:$CLASSPATH
export CLASSPATH
nohup java -Xms32m -Xmx256m -XX:PermSize=32M -XX:MaxPermSize=64m  -Djava.awt.headless=true -classpath $CLASSPATH com.ailk.check.main.UploadDayToBomc &