-------
create table a4_uap_db   as select * from aiuap20.uap_db;

-------------------------
create table a4_iap_device_session as
select distinct (t1.device_id),
                count(t1.device_id) total,
                CASE TO_CHAR((TRUNC(SYSDATE) +
                         TRUNC((TO_DATE(TO_CHAR(t1.login_time,
                                                 'yyyy-mm-dd hh24:mi:ss'),
                                         'yyyy-mm-dd hh24:mi:ss') -
                                TRUNC(SYSDATE)) * 24 * 60 / 30 - 1) * 30 / 60 / 24),
                         'hh24:mi') || '-' ||
                 TO_CHAR((TRUNC(SYSDATE) +
                         TRUNC((TO_DATE(TO_CHAR(t1.login_time,
                                                 'yyyy-mm-dd hh24:mi:ss'),
                                         'yyyy-mm-dd hh24:mi:ss') -
                                TRUNC(SYSDATE)) * 24 * 60 / 30) * 30 / 60 / 24),
                         'hh24:mi')
         WHEN '00:00-00:30' THEN
          '1'
         WHEN '00:30-01:00' THEN
          '2'
         WHEN '01:00-01:30' THEN
          '3'
         WHEN '01:30-02:00' THEN
          '4'
         WHEN '02:00-02:30' THEN
          '5'
         WHEN '02:30-03:00' THEN
          '6'
         WHEN '03:00-03:30' THEN
          '7'
         WHEN '03:30-04:00' THEN
          '8'
         WHEN '04:00-04:30' THEN
          '9'
         WHEN '04:30-05:00' THEN
          '10'
         WHEN '05:00-05:30' THEN
          '11'
         WHEN '05:30-06:00' THEN
          '12'
         WHEN '06:00-06:30' THEN
          '13'
         WHEN '06:30-07:00' THEN
          '14'
         WHEN '07:00-07:30' THEN
          '15'
         WHEN '07:30-08:00' THEN
          '16'
         WHEN '08:00-08:30' THEN
          '17'
         WHEN '08:30-09:00' THEN
          '18'
         WHEN '09:00-09:30' THEN
          '19'
         WHEN '09:30-10:00' THEN
          '20'
         WHEN '10:00-10:30' THEN
          '21'
         WHEN '10:30-11:00' THEN
          '22'
         WHEN '11:00-11:30' THEN
          '23'
         WHEN '11:30-12:00' THEN
          '24'
         WHEN '12:00-12:30' THEN
          '25'
         WHEN '12:30-13:00' THEN
          '26'
         WHEN '13:00-13:30' THEN
          '27'
         WHEN '13:30-14:00' THEN
          '28'
         WHEN '14:00-14:30' THEN
          '29'
         WHEN '14:30-15:00' THEN
          '30'
         WHEN '15:00-15:30' THEN
          '31'
         WHEN '15:30-16:00' THEN
          '32'
         WHEN '16:00-16:30' THEN
          '33'
         WHEN '16:30-17:00' THEN
          '34'
         WHEN '17:00-17:30' THEN
          '35'
         WHEN '17:30-18:00' THEN
          '36'
         WHEN '18:00-18:30' THEN
          '37'
         WHEN '18:30-19:00' THEN
          '38'
         WHEN '19:00-19:30' THEN
          '39'
         WHEN '19:30-20:00' THEN
          '40'
         WHEN '20:00-20:30' THEN
          '41'
         WHEN '20:30-21:00' THEN
          '42'
         WHEN '21:00-21:30' THEN
          '43'
         WHEN '21:30-22:00' THEN
          '44'
         WHEN '22:00-22:30' THEN
          '45'
         WHEN '22:30-23:00' THEN
          '46'
         WHEN '23:00-23:30' THEN
          '47'
         WHEN '23:30-00:00' THEN
          '48'
         ELSE
          '0'
       END login_hour
  from iap_device_session t1, iap_device_session_cmd t2
 where t1.device_type = 3
   and t1.session_id = t2.session_id
   and t2.op_time > to_date('2012-08-12 00:00:00', 'yyyy-MM-dd HH24:mi:ss')
   and t2.op_time <=
       to_date('2012-08-13 00:00:00', 'yyyy-MM-dd HH24:mi:ss')
 group by t1.device_id,
          TO_CHAR((TRUNC(SYSDATE) +
                  TRUNC((TO_DATE(TO_CHAR(t1.login_time,
                                          'yyyy-mm-dd hh24:mi:ss'),
                                  'yyyy-mm-dd hh24:mi:ss') - TRUNC(SYSDATE)) * 24 * 60 / 30 - 1) * 30 / 60 / 24),
                  'hh24:mi') || '-' ||
          TO_CHAR((TRUNC(SYSDATE) +
                  TRUNC((TO_DATE(TO_CHAR(t1.login_time,
                                          'yyyy-mm-dd hh24:mi:ss'),
                                  'yyyy-mm-dd hh24:mi:ss') - TRUNC(SYSDATE)) * 24 * 60 / 30) * 30 / 60 / 24),
                  'hh24:mi')
------------
create table TEST_DEVICE_SESSION
(
  belong_sys VARCHAR2(2),
  device_id  NUMBER(10),
  total      NUMBER,
  login_hour VARCHAR2(2)
)
--------------------------------
create table SECOND_FINAL_DEVICE_SESSION
(
  belong_sys VARCHAR2(2),
  total      NUMBER,
  login_hour VARCHAR2(2)
)
 -----------------------------------
 create table FINAL_DEVICE_SESSION
(
  belong_sys VARCHAR2(2),
  login_hour VARCHAR2(2),
  total      NUMBER
)