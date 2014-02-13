#!/bin/bash
PATH=/usr/bin:/etc:/usr/sbin:/usr/ucb:$HOME/bin:/usr/bin/X11:/sbin:.
export PATH
export PATH=/usr/java5_64/bin:$PATH
export CLASSPATH=/usr/java5_64/lib

TEMP_CLASSPATH=
for file in $(ls ../lib);do
TEMP_CLASSPATH=$TEMP_CLASSPATH:../lib/$file
done
CLASSPATH=.:$TEMP_CLASSPATH:$CLASSPATH
export CLASSPATH
nohup java -Xms32m -Xmx256m -XX:PermSize=32M -XX:MaxPermSize=64m  -Djava.awt.headless=true -classpath $CLASSPATH com.ailk.check.main.UploadHourToBomc &