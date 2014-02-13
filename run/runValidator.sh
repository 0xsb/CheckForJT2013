#!/bin/bash
TEMP_CLASSPATH=
for file in $(ls ../lib);do
TEMP_CLASSPATH=$TEMP_CLASSPATH:../lib/$file
done
CLASSPATH=.:$TEMP_CLASSPATH:$CLASSPATH
export CLASSPATH
nohup java -Xms256m -Xmx1024m -XX:PermSize=128M -XX:MaxPermSize=256m  -Djava.awt.headless=true -classpath $CLASSPATH com.ailk.jt.validate.FileValidatorOldGood &