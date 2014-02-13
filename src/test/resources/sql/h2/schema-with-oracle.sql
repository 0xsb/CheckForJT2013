drop table if exists UAP_NOTICE;

create table UAP_NOTICE
(
  id               NUMBER(10) not null,
  type_id          VARCHAR2(10),
  content          VARCHAR2(4000),
  param_a          VARCHAR2(100),
  param_b          VARCHAR2(100),
  send_begin_date  TIMESTAMP,
  send_end_date    TIMESTAMP,
  create_date_time TIMESTAMP,
  ext_code         NUMBER(10),
  primary key (id)
);

create table A4_MAIN_ACCT_JT
(
  main_acct_id NUMBER(10) not null,
  login_name   VARCHAR2(64) not null,
  user_name    VARCHAR2(32),
  valid        CHAR(1),
  lock_status  CHAR(1) not null,
  acct_type    VARCHAR2(4) not null,
  rolelist     VARCHAR2(500),
  effect_time  TIMESTAMP(6) not null,
  expire_time  TIMESTAMP(6) not null,
  create_time  TIMESTAMP(6),
  area_id      NUMBER(10),
  org_id       NUMBER(10),
  orgname      VARCHAR2(1000),
  update_time  TIMESTAMP(6),
  superuser    CHAR(1),
  opendays     NUMBER,
  logondays    NUMBER,
  modify_mode  CHAR(3),
  acct_usage   NUMBER(7,4),
  primary key (main_acct_id)
);

create sequence uap_notice_seq start with 1 increment by 1;

insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132092, 'fanmengmeng', '4A', '0', '1', '1', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12298, null, parsedatetime('28-12-2011 22:59:58.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132093, 'fanchunsheng', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12277, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132094, 'fanguoming', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12282, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132095, 'fanjianping', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12277, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132096, 'fanjianwei', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12282, null, parsedatetime('05-11-2011 23:00:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132097, 'fanjingjing', '4A', '0', '1', '99', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 10216, null, parsedatetime('28-12-2011 22:59:58.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132098, 'fanxiaozheng', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12277, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132099, 'fanyuwei', '4A', '0', '1', '1', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12272, null, parsedatetime('27-10-2011 17:36:22.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132102, 'fandianhai', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12297, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132103, 'fanenming', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12297, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132104, 'fanfusheng', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12292, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132105, 'fanhaiqing', '4A', '0', '1', '99', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 10815, null, parsedatetime('28-12-2011 22:59:58.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);
insert into A4_MAIN_ACCT_JT (main_acct_id, login_name, user_name, valid, lock_status, acct_type, rolelist, effect_time, expire_time, create_time, area_id, org_id, orgname, update_time, superuser, opendays, logondays, modify_mode, acct_usage)
values (132106, 'fanhua', '4A', '0', '1', '3', null, parsedatetime('19-11-2009 23:13:09.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('01-01-2050 00:00:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), parsedatetime('22-12-2010 20:30:23.000000', 'dd-MM-yyyy hh:mm:ss.SS'), 10009, 12277, null, parsedatetime('26-08-2011 23:05:00.000000', 'dd-MM-yyyy hh:mm:ss.SS'), null, 0, 0, 'upd', 0);