-- 模拟集团表
create table A4_MAIN_ACCT_JT
(
  main_acct_id     NUMBER(10) not null,
  login_name       VARCHAR2(64),
  lock_status      CHAR(1),
  effect_time      TIMESTAMP(6),
  expire_time      TIMESTAMP(6),
  create_time      TIMESTAMP(6),
  last_login_time  TIMESTAMP(6),
  last_update_time TIMESTAMP(6),
  default_org      NUMBER(10),
  acct_type        CHAR(2),
  valid            CHAR(1),
  modify_mode      VARCHAR2(32),
  area_id          NUMBER(10)
)
-- Create/Recreate primary, unique and foreign key constraints 
alter table A4_MAIN_ACCT_JT
  add constraint MAINID primary key (MAIN_ACCT_ID)

  
-----------主账号快照表
create table A4_MAIN_ACCT_SNAP
(
  main_acct_id         NUMBER(10) not null,
  login_name           VARCHAR2(64) not null,
  login_pwd            VARCHAR2(256) not null,
  pwd_question         VARCHAR2(64),
  pwd_answer           VARCHAR2(64),
  authen_rule_id       NUMBER(10),
  lock_status          CHAR(1) not null,
  lock_type            CHAR(1),
  lock_desc            VARCHAR2(128),
  lock_main_acct_id    NUMBER(10),
  lock_time            TIMESTAMP(6),
  pwd_type             CHAR(1) not null,
  pwd_wrong_times      INTEGER,
  pwd_update_time      TIMESTAMP(6),
  effect_time          TIMESTAMP(6) not null,
  expire_time          TIMESTAMP(6) not null,
  last_login_time      TIMESTAMP(6),
  create_time          TIMESTAMP(6),
  last_update_time     TIMESTAMP(6),
  work_no              VARCHAR2(32),
  area_id              NUMBER(10),
  pwd_rule_id          NUMBER(10),
  name                 VARCHAR2(32),
  gender               CHAR(1),
  birth_date           DATE,
  id_card_no           VARCHAR2(32),
  education            CHAR(1),
  mobile               VARCHAR2(16),
  email                VARCHAR2(256),
  oa_email             VARCHAR2(256),
  tel                  VARCHAR2(32),
  staff_no             VARCHAR2(32),
  person_status        CHAR(1),
  staff_type           CHAR(1),
  remark               VARCHAR2(512),
  join_date            DATE,
  contract_expire_date DATE,
  default_org          NUMBER(10),
  acct_type            VARCHAR2(4) not null,
  acct_position        CHAR(1) not null,
  canloginway          CHAR(1),
  is_approve           VARCHAR2(1),
  valid                CHAR(1),
  modify_mode          VARCHAR2(32)
)
-- Create/Recreate primary, unique and foreign key constraints 
alter table A4_MAIN_ACCT_SNAP
  add constraint ID_MAIN primary key (MAIN_ACCT_ID);
--创建主账号变更表
create table A4_MAIN_ACCT_CHANGE_DO
(
  calculatedate     TIMESTAMP(6),
  jtchange          NUMBER(7,4),
  fullmainacctjt    NUMBER(7),
  dayacctchangejt   NUMBER(7),
  fullmainacctlocal NUMBER(7),
  inanotinb         NUMBER(7),
  inbnotina         NUMBER(7),
  down              NUMBER(7)
)
-- Add comments to the columns 
comment on column A4_MAIN_ACCT_CHANGE_DO.calculatedate
  is '计算时间';
comment on column A4_MAIN_ACCT_CHANGE_DO.jtchange
  is '集团主账号全量';
comment on column A4_MAIN_ACCT_CHANGE_DO.fullmainacctjt
  is '集团主账号全量';
comment on column A4_MAIN_ACCT_CHANGE_DO.dayacctchangejt
  is '集团主账号增量';
comment on column A4_MAIN_ACCT_CHANGE_DO.fullmainacctlocal
  is '本地主账号全量';
comment on column A4_MAIN_ACCT_CHANGE_DO.inanotinb
  is '在集团表中但不在本地表中';
comment on column A4_MAIN_ACCT_CHANGE_DO.inbnotina
  is '在本地表中但不在集团表中';
comment on column A4_MAIN_ACCT_CHANGE_DO.down
  is '变动率公式中分母';
-----主账号差异账号表
create table A4_MAIN_ACCT_JT_DIFFER
(
  login_name VARCHAR2(64),
  valid      CHAR(1),
  acct_type  VARCHAR2(2)
)
comment on column A4_MAIN_ACCT_JT_DIFFER.login_name
  is '登陆名称';
comment on column A4_MAIN_ACCT_JT_DIFFER.valid
  is '是否有效';
comment on column A4_MAIN_ACCT_JT_DIFFER.acct_type
  is '账号类型';
  -------------------------
  create table A4_MAIN_ACCT_HIS_TODAY
(
  main_acct_id         NUMBER(10) not null,
  login_name           VARCHAR2(64) not null,
  login_pwd            VARCHAR2(256) not null,
  pwd_question         VARCHAR2(64),
  pwd_answer           VARCHAR2(64),
  authen_rule_id       NUMBER(10),
  lock_status          CHAR(1) not null,
  lock_type            CHAR(1),
  lock_desc            VARCHAR2(128),
  lock_main_acct_id    NUMBER(10),
  lock_time            TIMESTAMP(6),
  pwd_type             CHAR(1) not null,
  pwd_wrong_times      INTEGER,
  pwd_update_time      TIMESTAMP(6),
  effect_time          TIMESTAMP(6) not null,
  expire_time          TIMESTAMP(6) not null,
  last_login_time      TIMESTAMP(6),
  create_time          TIMESTAMP(6),
  last_update_time     TIMESTAMP(6),
  work_no              VARCHAR2(32),
  area_id              NUMBER(10),
  pwd_rule_id          NUMBER(10),
  name                 VARCHAR2(32),
  gender               CHAR(1),
  birth_date           DATE,
  id_card_no           VARCHAR2(32),
  education            CHAR(1),
  mobile               VARCHAR2(16),
  email                VARCHAR2(256),
  oa_email             VARCHAR2(256),
  tel                  VARCHAR2(32),
  staff_no             VARCHAR2(32),
  person_status        CHAR(1),
  staff_type           CHAR(1),
  remark               VARCHAR2(512),
  join_date            DATE,
  contract_expire_date DATE,
  default_org          NUMBER(10),
  acct_type            VARCHAR2(4) not null,
  acct_position        CHAR(1) not null,
  is_approve           CHAR(1)
)
-------------------------------
create table A4_MAIN_ACCT_HIS_YESTODAY
(
  main_acct_id         NUMBER(10) not null,
  login_name           VARCHAR2(64) not null,
  login_pwd            VARCHAR2(256) not null,
  pwd_question         VARCHAR2(64),
  pwd_answer           VARCHAR2(64),
  authen_rule_id       NUMBER(10),
  lock_status          CHAR(1) not null,
  lock_type            CHAR(1),
  lock_desc            VARCHAR2(128),
  lock_main_acct_id    NUMBER(10),
  lock_time            TIMESTAMP(6),
  pwd_type             CHAR(1) not null,
  pwd_wrong_times      INTEGER,
  pwd_update_time      TIMESTAMP(6),
  effect_time          TIMESTAMP(6) not null,
  expire_time          TIMESTAMP(6) not null,
  last_login_time      TIMESTAMP(6),
  create_time          TIMESTAMP(6),
  last_update_time     TIMESTAMP(6),
  work_no              VARCHAR2(32),
  area_id              NUMBER(10),
  pwd_rule_id          NUMBER(10),
  name                 VARCHAR2(32),
  gender               CHAR(1),
  birth_date           DATE,
  id_card_no           VARCHAR2(32),
  education            CHAR(1),
  mobile               VARCHAR2(16),
  email                VARCHAR2(256),
  oa_email             VARCHAR2(256),
  tel                  VARCHAR2(32),
  staff_no             VARCHAR2(32),
  person_status        CHAR(1),
  staff_type           CHAR(1),
  remark               VARCHAR2(512),
  join_date            DATE,
  contract_expire_date DATE,
  default_org          NUMBER(10),
  acct_type            VARCHAR2(4) not null,
  acct_position        CHAR(1) not null,
  is_approve           CHAR(1)
)