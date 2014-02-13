create table A4_OPERATE_LOGIN
(
  caculate_time           TIMESTAMP(6),
  boss_acct_operate_count NUMBER(2),
  a4_acct_operate_count   NUMBER(8),
  boss_acct_login_count   NUMBER(8),
  a4_acct_login_count     NUMBER(8),
  operate_DIFFER        NUMBER(7,4),
  login_differ            NUMBER(7,4)
)							 
------
create table a4_4ar_for_Bomc as 

select t.operator_type,
       sum(t.operator_count1) / count(t.operator_count1) hour1,
       sum(t.operator_count2) / count(t.operator_count2) hour2,
       sum(t.operator_count3) / count(t.operator_count3) hour3,
       sum(t.operator_count4) / count(t.operator_count4) hour4,
       sum(t.operator_count5) / count(t.operator_count5) hour5,
       sum(t.operator_count6) / count(t.operator_count6) hour6,
       sum(t.operator_count7) / count(t.operator_count7) hour7,
       sum(t.operator_count8) / count(t.operator_count8) hour8,
       sum(t.operator_count9) / count(t.operator_count9) hour9,
       sum(t.operator_count10) / count(t.operator_count10) hour10,
       sum(t.operator_count11) / count(t.operator_count11) hour11,
       sum(t.operator_count12) / count(t.operator_count12) hour12,
       sum(t.operator_count13) / count(t.operator_count13) hour13,
       sum(t.operator_count14) / count(t.operator_count14) hour14,
       sum(t.operator_count15) / count(t.operator_count15) hour15,
       sum(t.operator_count16) / count(t.operator_count16) hour16,
       sum(t.operator_count17) / count(t.operator_count17) hour17,
       sum(t.operator_count18) / count(t.operator_count18) hour18,
       sum(t.operator_count19) / count(t.operator_count19) hour19,
       sum(t.operator_count20) / count(t.operator_count20) hour20,
       sum(t.operator_count21) / count(t.operator_count21) hour21,
       sum(t.operator_count22) / count(t.operator_count22) hour22,
       sum(t.operator_count23) / count(t.operator_count23) hour23,
       sum(t.operator_count24) / count(t.operator_count24) hour24,
       sum(t.operator_count25) / count(t.operator_count25) hour25,
       sum(t.operator_count26) / count(t.operator_count26) hour26,
       sum(t.operator_count27) / count(t.operator_count27) hour27,
       sum(t.operator_count28) / count(t.operator_count28) hour28,
       sum(t.operator_count29) / count(t.operator_count29) hour29,
       sum(t.operator_count30) / count(t.operator_count30) hour30,
       sum(t.operator_count31) / count(t.operator_count31) hour31,
       sum(t.operator_count32) / count(t.operator_count32) hour32,
       sum(t.operator_count33) / count(t.operator_count33) hour33,
       sum(t.operator_count34) / count(t.operator_count34) hour34,
       sum(t.operator_count35) / count(t.operator_count35) hour35,
       sum(t.operator_count36) / count(t.operator_count36) hour36,
       sum(t.operator_count37) / count(t.operator_count37) hour37,
       sum(t.operator_count38) / count(t.operator_count38) hour38,
       sum(t.operator_count39) / count(t.operator_count39) hour39,
       sum(t.operator_count40) / count(t.operator_count40) hour40,
       sum(t.operator_count41) / count(t.operator_count41) hour41,
       sum(t.operator_count42) / count(t.operator_count42) hour42,
       sum(t.operator_count43) / count(t.operator_count43) hour43,
       sum(t.operator_count44) / count(t.operator_count44) hour44,
       sum(t.operator_count45) / count(t.operator_count45) hour45,
       sum(t.operator_count46) / count(t.operator_count46) hour46,
       sum(t.operator_count47) / count(t.operator_count47) hour47,
       sum(t.operator_count48) / count(t.operator_count48) hour48
  from a4_4ar_for_average t
 where t.operator_type=1
 and t.calculate_time >
       to_date((to_char(sysdate, 'yyyy-MM-dd') || '' || '00:00:00'),
               'yyyy-MM-dd hh24:Mi:ss')
 group by t.operator_type;
 --------
alter table A4_4AR_FOR_BOMC
  add constraint BOMC_PK primary key (OPERATOR_TYPE)		