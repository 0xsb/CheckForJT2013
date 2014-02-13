--模拟集团表
create table A4_MAIN_ACCT_JT
(
  main_acct_id NUMBER(10) not null,
  login_name   VARCHAR2(64) not null,
  user_name    VARCHAR2(32),
  valid        CHAR(1),
  lock_status  CHAR(1) not null,
  acct_type    VARCHAR2(4) not null,
  rolelist     varchar2(500),
  effect_time  TIMESTAMP(6) not null,
  expire_time  TIMESTAMP(6) not null,
  create_time  TIMESTAMP(6),
  area_id      NUMBER(10),
  org_id       NUMBER(10),
  orgname      varchar2(1000),
  update_time  TIMESTAMP(6),
  superuser    CHAR(1),
  opendays     NUMBER,
  logondays    NUMBER,
  modify_mode CHAR(3),
  acct_usage  NUMBER(7,4)
)
--快照表
create table A4_MAIN_ACCT_snap
(
  main_acct_id NUMBER(10) not null,
  login_name   VARCHAR2(64) not null,
  user_name    VARCHAR2(32),
  valid        CHAR(1),
  lock_status  CHAR(1) not null,
  acct_type    VARCHAR2(4) not null,
  rolelist     varchar2(500),
  effect_time  TIMESTAMP(6) not null,
  expire_time  TIMESTAMP(6) not null,
  create_time  TIMESTAMP(6),
  area_id      NUMBER(10),
  org_id       NUMBER(10),
  orgname      varchar2(1000),
  update_time  TIMESTAMP(6),
  superuser    CHAR(1),
  opendays     NUMBER,
  logondays    NUMBER,
  modify_mode CHAR(3),
  acct_usage  NUMBER(7,4)
)



---主账号可用天数和使用率情况表
create table A4_MAIN_ACCT_USABLEDAY_USAGE
(
  main_acct_id   NUMBER(10) not null,
  login_name     VARCHAR2(64),
  acct_type      VARCHAR2(2),
  logon_days     NUMBER(2) default 0,
  aviliable_days NUMBER(2) default 0,
  effect_time  TIMESTAMP(6) not null,
  expire_time  TIMESTAMP(6) not null,
  create_time  TIMESTAMP(6),  
  update_time  TIMESTAMP(6),
  calculate_time TIMESTAMP(6),
  acct_usage     NUMBER(7,4) default 0.0000,
  valid          CHAR(1),
  modify_mode    CHAR(3)
)
-- Add comments to the columns 
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.main_acct_id
  is '主账号ID';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.login_name
  is '主账号登陆名称';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.acct_type
  is '账号类型';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.logon_days
  is '登录天数';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.aviliable_days
  is '可用天数';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.calculate_time
  is '计算时间';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.acct_usage
  is '账号使用率';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.valid
  is '主账号是否有效,0无效，1有效';
comment on column A4_MAIN_ACCT_USABLEDAY_USAGE.modify_mode
  is '主账号更新类型add  upd del';
  
--主账号使用率
-- Create table
create table A4_MAIN_ACCT_USAGE
(
  caculate_time    TIMESTAMP(6),
  over_five_count  NUMBER(8),
  over_forty_count NUMBER(8),
  acct_type        VARCHAR2(4),
  acct_usage       NUMBER(7,4),
  all_count        NUMBER(8)
)
-- Add comments to the columns 
comment on column A4_MAIN_ACCT_USAGE.caculate_time
  is '计算时间';
comment on column A4_MAIN_ACCT_USAGE.over_five_count
  is '可用天数超过5天的账号总数';
comment on column A4_MAIN_ACCT_USAGE.over_forty_count
  is '单个账号使用率超过40%的总人数';
comment on column A4_MAIN_ACCT_USAGE.acct_type
  is '账号类型：1营业员，2客服坐席，5合作伙伴';
comment on column A4_MAIN_ACCT_USAGE.acct_usage
  is '账号使用率';
comment on column A4_MAIN_ACCT_USAGE.all_count
  is '该账号在用总人数';
  
---合作伙伴账号良好管理率
 create table A4_partner_manage
(
  caculate_time    TIMESTAMP(6),
  login_days  NUMBER(2),
  all_acct NUMBER(8),
  acct_manage       NUMBER(7,4) 
) 
 comment on column A4_PARTNER_MANAGE.caculate_time
  is '计算时间';
comment on column A4_PARTNER_MANAGE.login_days
  is '当前登录天数少于4天的伙伴主账号的状态为无效或被锁定的总数';
comment on column A4_PARTNER_MANAGE.all_acct
  is '当月所有登录天数少于4天的伙伴主账号总数';
comment on column A4_PARTNER_MANAGE.acct_manage
  is '账号良好管理率'; 

create table a4_organization
(
      organization_id varchar2(100),
      organization_name varchar2(1000),
      parent_id varchar2(100)
);
declare
    str varchar2(1000);
begin 
  for rec2 in (select * from uap_organization a) loop
    str:='';
    for rec in (select * from uap_organization a
    connect by a.org_id= prior a.parent_id 
    start with a.org_id=rec2.org_id
    order by level desc) loop 
          str:=str||rec.org_name||'-';
    end loop;
    str:=substr(STR,1,length(str)-1);
   
    insert into a4_organization values(rec2.org_id,str,rec2.parent_id);
  end loop;
end;
---------
create table A4_MAIN_ACCT_LOGONS
(
  main_acct_id NUMBER(10) not null,
  login_name   VARCHAR2(64),
  login_days   NUMBER
)
create table A4_MAIN_ACCT_LOGONS_TEMP
(
  login_day      DATE,
  main_acct_id   NUMBER(10),
  main_acct_name VARCHAR2(250)
)
  