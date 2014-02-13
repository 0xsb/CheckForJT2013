
----
create table A4_AAR
(
  resname VARCHAR2(64),
  restype VARCHAR2(4),
  num     NUMBER,
  dlvalue NUMBER,
  czvalue NUMBER
)
comment on column A4_AAR.restype
  is '11 BOSS,12 CRM,13 经分,14 BOMC,15 VGOP,16 使用经分账号库的VGOP账号,17 ESOP,18 使用CRM账号库的ESOP账号,99 其他';
comment on column A4_AAR.num
  is '从0点到24点，每半小时计数加1，即从1~48；1表示0点到0点30分时间段。';
comment on column A4_AAR.dlvalue
  is '半小时登陆总数';
comment on column A4_AAR.czvalue
  is '半小时操作总数';
--------
create table A4_4AR
(
  resname VARCHAR2(64),
  restype VARCHAR2(4),
  num     NUMBER,
  dlvalue NUMBER,
  czvalue NUMBER
)  
 comment on column A4_4AR.restype
  is '11 BOSS,12 CRM,13 经分,14 BOMC,15 VGOP,16 使用经分账号库的VGOP账号,17 ESOP,18 使用CRM账号库的ESOP账号,99 其他';
comment on column A4_4AR.num
  is '从0点到24点，每半小时计数加1，即从1~48；1表示0点到0点30分时间段。';
comment on column A4_4AR.dlvalue
  is '半小时登陆总数';
comment on column A4_4AR.czvalue
  is '半小时操作总数';
-----------
create table a4_aar_temp_dl as (
select a, count(b) dlvalue
  from (SELECT (CASE
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '00:31' THEN
                  '1'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '01:01' THEN
                  '2'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '01:31' THEN
                  '3'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '02:01' THEN
                  '4'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '02:31' THEN
                  '5'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '03:01' THEN
                  '6'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '03:31' THEN
                  '7'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '04:01' THEN
                  '8'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '04:31' THEN
                  '9'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '05:01' THEN
                  '10'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '05:31' THEN
                  '11'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '06:01' THEN
                  '12'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '06:31' THEN
                  '13'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '07:01' THEN
                  '14'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '07:31' THEN
                  '15'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '08:01' THEN
                  '16'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '08:31' THEN
                  '17'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '09:01' THEN
                  '18'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '09:31' THEN
                  '19'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '10:01' THEN
                  '20'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '10:31' THEN
                  '21'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '11:01' THEN
                  '22'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '11:31' THEN
                  '23'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '12:01' THEN
                  '24'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '12:31' THEN
                  '25'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '13:01' THEN
                  '26'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '13:31' THEN
                  '27'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '14:01' THEN
                  '28'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '14:31' THEN
                  '29'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '15:01' THEN
                  '30'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '15:31' THEN
                  '31'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '16:01' THEN
                  '32'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '16:31' THEN
                  '33'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '17:01' THEN
                  '34'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '17:31' THEN
                  '35'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '18:01' THEN
                  '36'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '18:31' THEN
                  '37'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '19:01' THEN
                  '38'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '19:31' THEN
                  '39'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '20:01' THEN
                  '40'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '20:31' THEN
                  '41'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '21:01' THEN
                  '42'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '21:31' THEN
                  '43'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '22:01' THEN
                  '44'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '22:31' THEN
                  '45'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '23:01' THEN
                  '46'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '23:31' THEN
                  '47'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '24:01' THEN
                  '48'
                 ELSE
                  '0'
               END) as a,
               t.log_id as b
          from audit11.iap_app_log partition(PART_APP_LOG_201108) t
         where t.operate_content like '%BOSS%'
           and t.operate_type_id = '1-AIUAP-10012'
           and t.operate_time >
               to_date('2011-08-01 00:00:00', 'yyyy-MM-dd HH24:mi:ss')
           and t.operate_time <=
               to_date('2011-08-31 00:30:00', 'yyyy-MM-dd HH24:mi:ss'))
 group by a)
-------------------------------------
create table a4_aar_temp_cz as (
 select a, count(b) czvalue
  from (SELECT (CASE
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '00:31' THEN
                  '1'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '01:01' THEN
                  '2'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '01:31' THEN
                  '3'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '02:01' THEN
                  '4'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '02:31' THEN
                  '5'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '03:01' THEN
                  '6'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '03:31' THEN
                  '7'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '04:01' THEN
                  '8'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '04:31' THEN
                  '9'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '05:01' THEN
                  '10'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '05:31' THEN
                  '11'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '06:01' THEN
                  '12'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '06:31' THEN
                  '13'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '07:01' THEN
                  '14'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '07:31' THEN
                  '15'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '08:01' THEN
                  '16'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '08:31' THEN
                  '17'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '09:01' THEN
                  '18'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '09:31' THEN
                  '19'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '10:01' THEN
                  '20'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '10:31' THEN
                  '21'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '11:01' THEN
                  '22'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '11:31' THEN
                  '23'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '12:01' THEN
                  '24'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '12:31' THEN
                  '25'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '13:01' THEN
                  '26'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '13:31' THEN
                  '27'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '14:01' THEN
                  '28'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '14:31' THEN
                  '29'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '15:01' THEN
                  '30'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '15:31' THEN
                  '31'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '16:01' THEN
                  '32'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '16:31' THEN
                  '33'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '17:01' THEN
                  '34'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '17:31' THEN
                  '35'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '18:01' THEN
                  '36'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '18:31' THEN
                  '37'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '19:01' THEN
                  '38'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '19:31' THEN
                  '39'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '20:01' THEN
                  '40'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '20:31' THEN
                  '41'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '21:01' THEN
                  '42'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '21:31' THEN
                  '43'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '22:01' THEN
                  '44'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '22:31' THEN
                  '45'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '23:01' THEN
                  '46'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '23:31' THEN
                  '47'
                 WHEN TO_CHAR(t.operate_time, 'hh24:mi') < '24:01' THEN
                  '48'
                 ELSE
                  '0'
               END) as a,
               t.log_id as b
          from audit11.iap_app_log partition(PART_APP_LOG_201108) t
         where t.domain_id = 'UAP'
           and t.operate_type_id != '1-AIUAP-10012'
           and t.operate_time >
               to_date('2011-08-01 00:00:00', 'yyyy-MM-dd HH24:mi:ss')
           and t.operate_time <=
               to_date('2011-08-31 00:30:00', 'yyyy-MM-dd HH24:mi:ss'))
 group by a);
 
 
 
 
 
 
-----------计算平均值供BOMC监控使用
create table a4_4ar_for_average
(
 calculate_time timestamp,
 operator_type char(1),
 operator_count1  number ,
operator_count2  number ,
operator_count3  number ,
operator_count4  number ,
operator_count5  number ,
operator_count6  number ,
operator_count7  number ,
operator_count8  number ,
operator_count9  number ,
operator_count10  number ,
operator_count11  number ,
operator_count12  number ,
operator_count13  number ,
operator_count14  number ,
operator_count15  number ,
operator_count16  number ,
operator_count17  number ,
operator_count18  number ,
operator_count19  number ,
operator_count20  number ,
operator_count21  number ,
operator_count22  number ,
operator_count23  number ,
operator_count24  number ,
operator_count25  number ,
operator_count26  number ,
operator_count27  number ,
operator_count28  number ,
operator_count29  number ,
operator_count30  number ,
operator_count31  number ,
operator_count32  number ,
operator_count33  number ,
operator_count34  number ,
operator_count35  number ,
operator_count36  number ,
operator_count37  number ,
operator_count38  number ,
operator_count39  number ,
operator_count40  number ,
operator_count41  number ,
operator_count42  number ,
operator_count43  number ,
operator_count44  number ,
operator_count45  number ,
operator_count46	number ,
operator_count47	number ,
operator_count48	number
)
comment on column A4_4AR_FOR_AVERAGE.calculate_time
  is '计算时间';
comment on column A4_4AR_FOR_AVERAGE.operator_type
  is '0代表登录，1代表操作';
comment on column A4_4AR_FOR_AVERAGE.operator_count1
  is '第一个时间区间'; 
