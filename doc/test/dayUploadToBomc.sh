#!/bin/bash

export JAVA_HOME=/opt/jdk1.5.0_21
PATH=$JAVA_HOME/bin:$PATH:$HOME/bin
export PATH
export CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar

#day file upload to bomc
#5,15,25,35,45,55 7-9 * * * sh /home/aiuap/CheckForJT2013/bin/dayUploadToBomc.sh
DAY_UPLOAD=/home/aiuap/CheckForJT2013/upload
DAY_UPLOAD_TEMP=/home/aiuap/CheckForJT2013/temp/day
DAY_UPLOAD_BAK=/home/aiuap/CheckForJT2013/bak/
DAY_UPLOAD_LOG=/home/aiuap/CheckForJT2013/log/
BOMC_UPLOAD=SafeM
APPLY_TIME=`date`
DAY_NUMBER=`ls -lrt /home/aiuap/CheckForJT2013/upload/*.xml | wc -l`
cd $DAY_UPLOAD
echo ===============$APPLY_TIME================= >> $DAY_UPLOAD_LOG/day_file.log

echo upload day_file number : $DAY_NUMBER >> $DAY_UPLOAD_LOG/day_file.log
ls -lrt *.xml >> $DAY_UPLOAD_LOG/day_file.log
mv *.xml $DAY_UPLOAD_TEMP
#put all file to BOMC 10.87.21.64
lftp -u capesup,1qaz!QAZ sftp://10.87.21.64 <<UP
cd $BOMC_UPLOAD
lcd $DAY_UPLOAD_TEMP
mput *.xml
bye
UP
cd $DAY_UPLOAD_TEMP
mv *.xml $DAY_UPLOAD_BAK
echo ===============$APPLY_TIME================= >> $DAY_UPLOAD_LOG/day_file.log
exit
