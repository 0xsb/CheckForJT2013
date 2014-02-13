#!/bin/bash

PATH=/usr/bin:/etc:/usr/sbin:/usr/ucb:$HOME/bin:/usr/bin/X11:/sbin:.
export PATH
export PATH=/usr/java5_64/bin:$PATH
export CLASSPATH=/usr/java5_64/lib

#hour file upload to bomc
#*/5 * * * * sh /home/aiuap/CheckForJT2013/bin/hourUploadToBomc.sh
HOUR_UPLOAD=/home/aiuap/CheckForJT2013/hour_upload
HOUR_UPLOAD_TEMP=/home/aiuap/CheckForJT2013/temp
HOUR_UPLOAD_BAK=/home/aiuap/CheckForJT2013/bak/hour
HOUR_UPLOAD_LOG=/home/aiuap/CheckForJT2013/log
BOMC_UPLOAD=SafeM
APPLY_TIME=`date`
SMMAL_NUMBER=`ls -lrt /home/aiuap/CheckForJT2013/hour_upload/SMMAL*.xml | wc -l`
SMSAL_NUMBER=`ls -lrt /home/aiuap/CheckForJT2013/hour_upload/SMSAL*.xml | wc -l`

cd $HOUR_UPLOAD
echo ===============$APPLY_TIME================= >> $HOUR_UPLOAD_LOG/hour_file.log
echo upload SMMAL number : $SMMAL_NUMBER  >>$HOUR_UPLOAD_LOG/hour_file.log
ls -lrt SMMAL*.xml >> $HOUR_UPLOAD_LOG/hour_file.log
echo upload SMSAL number : $SMSAL_NUMBER >> $HOUR_UPLOAD_LOG/hour_file.log
ls -lrt SMSAL*.xml >> $HOUR_UPLOAD_LOG/hour_file.log
mv SMMAL*.xml $HOUR_UPLOAD_TEMP
mv SMSAL*.xml $HOUR_UPLOAD_TEMP
#put all file to BOMC 10.87.21.64
echo 'upload file to 10.87.21.64' >>$HOUR_UPLOAD_LOG/hour_file.log
lftp -u capesup,1qaz!QAZ sftp://10.87.21.64 <<UP
cd $BOMC_UPLOAD
lcd $HOUR_UPLOAD_TEMP
mput SM*.xml
bye
UP
echo 'upload end' >>$HOUR_UPLOAD_LOG/hour_file.log
cd $HOUR_UPLOAD_TEMP
echo 'mv SM*.xml to bak' >>$HOUR_UPLOAD_LOG/hour_file.log
mv SM*.xml $HOUR_UPLOAD_BAK
echo 'mv okay' >> $HOUR_UPLOAD_LOG/hour_file.log
echo ===============$APPLY_TIME=================  >> $HOUR_UPLOAD_LOG/hour_file.log
exit
