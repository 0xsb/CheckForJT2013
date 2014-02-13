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
nohup java -Xms256m -Xmx1024m -XX:PermSize=128M -XX:MaxPermSize=256m  -Djava.awt.headless=true -classpath $CLASSPATH com.ailk.uap.makefile4new.DeviceSubAcctLoginFile &