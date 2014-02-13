---重要说明，所有表结构都要创建在aiuap用户下
--创建boss数据源表
create table a4_boss_acct_change_detail
(
  staff_id       NUMBER(12),
  loginname      VARCHAR2(64),
  chage_mode     NUMBER(1),
  updatetime     TIMESTAMP(6),
  change_content VARCHAR2(256)
)

--boss数据结构和4A数据结构不一样，需要有专门的类将这些数据转换迁移
---创建与4A比对数据的boss账号表(该表暂时没用)
create table a4_boss_acct
(
  staff_id       VARCHAR2(256) not null,
  loginname      VARCHAR2(256),
  chage_mode     CHAR(3),
  updatetime     TIMESTAMP(6), 
)

---创建boss账号和4A账号差异表
create table a4_boss_acct_differ
(
  staff_id   VARCHAR2(256),
  loginname  VARCHAR2(256),
  chage_mode CHAR(3),
  updatetime TIMESTAMP(6)
)

--计算指标监控表
create table A4_BOSS_ACCT_COVERD
(
  caculate_time   TIMESTAMP(6),
  boss_acct_count NUMBER(2),
  a4_acct_count   NUMBER(8),
  coverd          NUMBER(7,4)
)


-------这些表要特别注意创建到aiuap用户下====================================
-- 从日志中查询出BOSS账号变更表
create table A4_BOSS_ACCT_FROM_LOG
(
  seq               NUMBER(10) not null,
  log_id            VARCHAR2(64),
  domain_id         VARCHAR2(50),
  operate_type_id   VARCHAR2(250),
  operate_type_name VARCHAR2(250),
  operate_content   VARCHAR2(4000),
  create_time       TIMESTAMP(6),
  operate_time      TIMESTAMP(6),
  is_batch          CHAR(1),
  acct_change_mode  CHAR(3),
  svn_createtime    TIMESTAMP(6),
  svn_endtetime     TIMESTAMP(6),
  acct_create_time  TIMESTAMP(6)
)
comment on column A4_BOSS_ACCT_FROM_LOG.log_id
  is '日志ID';
comment on column A4_BOSS_ACCT_FROM_LOG.domain_id
  is '归属应用';
comment on column A4_BOSS_ACCT_FROM_LOG.operate_type_id
  is '操作编码ID,审计编码';
comment on column A4_BOSS_ACCT_FROM_LOG.operate_type_name
  is '操作编码名称';
comment on column A4_BOSS_ACCT_FROM_LOG.operate_content
  is '操作内容';
comment on column A4_BOSS_ACCT_FROM_LOG.create_time
  is 'ID创建时间';
comment on column A4_BOSS_ACCT_FROM_LOG.operate_time
  is '操作时间';
comment on column A4_BOSS_ACCT_FROM_LOG.is_batch
  is '是否为批量操作:0代表不是批量操作，1代表批量操作';
comment on column A4_BOSS_ACCT_FROM_LOG.acct_change_mode
  is '账号操作类型：add,del';
comment on column A4_BOSS_ACCT_FROM_LOG.svn_createtime
  is '工单操作开始时间';
comment on column A4_BOSS_ACCT_FROM_LOG.svn_endtetime
  is '工单操作结束时间';
comment on column A4_BOSS_ACCT_FROM_LOG.seq
  is 'ID编号';
comment on column A4_BOSS_ACCT_FROM_LOG.acct_create_time
  is '账号创建时间';
--创建sequence序列
create sequence a4_boss_acct_change_seq
minvalue 1
maxvalue 9999999999
start with 2000055426
increment by 1
cache 20;  
--二次更新数据源用户boss数据与4A账号数据比对
-- 从日志中查询出BOSS账号变更表,该表于a4_boss_acct表中数据比对
create table A4_BOSS_ACCT_FOR_COMPARE
(
  seq               NUMBER(10) not null,
  staff_id       VARCHAR2(256) not null,
  loginname      VARCHAR2(256),
  chage_mode     CHAR(1),
  acct_create_time  TIMESTAMP(6),
  updatetime     TIMESTAMP(6),
  change_content VARCHAR2(256)
)
 comment on column A4_BOSS_ACCT_FOR_COMPARE.seq
  is 'boss账号id，4A侧的主键，由sequence生产';
comment on column A4_BOSS_ACCT_FOR_COMPARE.staff_id
  is 'boss工号';
comment on column A4_BOSS_ACCT_FOR_COMPARE.loginname
  is 'boss登陆名';
comment on column A4_BOSS_ACCT_FOR_COMPARE.chage_mode
  is '账号更新模式，add 或者del';
comment on column A4_BOSS_ACCT_FOR_COMPARE.acct_create_time
  is '账号创建时间';
comment on column A4_BOSS_ACCT_FOR_COMPARE.updatetime
  is '最后跟新时间';
comment on column A4_BOSS_ACCT_FOR_COMPARE.change_content
  is '更新内容'; 
  
--------------------------方案2,
create table A4_boss_acct_coverd
(
  caculate_time    TIMESTAMP(6),
  boss_acct_count  NUMBER(2),
  a4_acct_count NUMBER(8),
  coverd       NUMBER(7,4) 
) 