--跨省缴费查询的各省数据量：
create table a4_cboss_buss_cx(
 HOME_SWITCH_NODE varchar2(20),
 total_count varchar2(20)
)
--跨省缴费的各省数据量：
create table a4_cboss_buss_jf(
 HOME_SWITCH_NODE varchar2(20),
 busi_count varchar2(20)
)




create table DEMO_TEST
(
  home_switch_node VARCHAR2(20),
  prvo_name        VARCHAR2(64),
  total_count      VARCHAR2(20),
  busi_count       VARCHAR2(20)
)