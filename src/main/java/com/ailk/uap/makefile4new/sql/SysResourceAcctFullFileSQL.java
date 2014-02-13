package com.ailk.uap.makefile4new.sql;

public abstract interface SysResourceAcctFullFileSQL
{
  public static final String SYS_RESOURCE_ACCT_FULL_SQL = "SELECT ID,"     +
          "       ACCTNAME,"     +
          "       RESNAME,"     +
          "       RESADDRESS,"     +
          "       RESTYPE,"     +
          "       LOCKSTATUS,"     +
          "       ACCTTYPE,"     +
          "       ESTABLISHTIME,"     +
          "       UPDATETIME"     +
          "  FROM (SELECT T.HOST_ACCT_ID ID,"     +
          "               T.ACCT_NAME ACCTNAME,"     +
          "               HOST.ENTITY_NAME RESNAME,"     +
          "               CASE"     +
          "                 WHEN HOST.LOGIN_TYPE = '1' THEN"     +
          "                  IP.IP || ':' || HOST.TELNET_PORT"     +
          "                 ELSE"     +
          "                  IP.IP || ':' || HOST.SSH_PORT"     +
          "               END RESADDRESS,"     +
          "               '01' RESTYPE,"     +
          "               DECODE(T.ACCT_STATUS, '0', '1', '1', '0', '0') LOCKSTATUS,"     +
          "               T.ACCT_TYPE ACCTTYPE,"     +
          "               T.CREATE_TIME ESTABLISHTIME,"     +
          "               T.LAST_UPDATE_TIME UPDATETIME"     +
          "          FROM UAP_HOST_ACCT T"     +
          "          LEFT JOIN UAP_HOST HOST"     +
          "            ON T.HOST_ID = HOST.HOST_ID"     +
          "          LEFT JOIN UAP_HOST_IP IP"     +
          "            ON HOST.HOST_ID = IP.HOST_ID"     +
          "           AND IP.IS_DEFAULT = '1'"     +
          "        UNION ALL"     +
          "        SELECT SDACCT.SD_ACCT_ID ID,"     +
          "               SDACCT.ACCT_NAME ACCTNAME,"     +
          "               SD.ENTITY_NAME RESNAME,"     +
          "               CASE"     +
          "                 WHEN SD.LOGIN_TYPE = '1' THEN"     +
          "                  SD.IP || ':' || SD.TELNET_PORT"     +
          "                 ELSE"     +
          "                  SD.IP || ':' || SD.SSH2_PORT"     +
          "               END RESADDRESS,"     +
          "               '04' RESTYPE,"     +
          "               DECODE(SDACCT.ACCT_STATUS, '0', '1', '1', '0', '0') LOCKSTATUS,"     +
          "               SDACCT.ACCT_TYPE ACCTTYPE,"     +
          "               SDACCT.CREATE_TIME ESTABLISHTIME,"     +
          "               SDACCT.LAST_UPDATE_TIME UPDATETIME"     +
          "          FROM UAP_SD_ACCT SDACCT"     +
          "          LEFT JOIN UAP_SECURITY_DEVICE SD"     +
          "            ON SDACCT.SD_ID = SD.DEVICE_ID"     +
          "        UNION ALL"     +
          "        SELECT DBACCT.DB_ACCT_ID ID,"     +
          "               DBACCT.ACCT_NAME ACCTNAME,"     +
          "               CASE"     +
          "                 WHEN DHOST.INSTANCE_NAME IS NULL THEN"     +
          "                  DHOST.DB_NAME"     +
          "                 ELSE"     +
          "                  DHOST.INSTANCE_NAME"     +
          "               END RESNAME,"     +
          "               CASE DHOST.DEPLOY_MODE"     +
          "                 WHEN '1' THEN"     +
          "                  DHOST.IP_ADDR || ':' || DHOST.PORT"     +
          "                 WHEN '0' THEN"     +
          "                  (SELECT IP.IP"     +
          "                     FROM UAP_HOST HOST, UAP_HOST_IP IP"     +
          "                    WHERE DHOST.HOST_ID = HOST.HOST_ID"     +
          "                      AND HOST.HOST_ID = IP.HOST_ID"     +
          "                      AND IP.IS_DEFAULT = '1') || ':' || DHOST.PORT"     +
          "                 ELSE"     +
          "                  '无'"     +
          "               END RESADDRESS,"     +
          "               '02' RESTYPE,"     +
          "               DECODE(DBACCT.ACCT_STATUS, '0', '1', '1', '0', '0') LOCKSTATUS,"     +
          "               DBACCT.ACCT_TYPE ACCTTYPE,"     +
          "               DBACCT.CREATE_TIME ESTABLISHTIME,"     +
          "               DBACCT.LAST_UPDATE_TIME UPDATETIME"     +
          "          FROM UAP_DB_ACCT DBACCT, UAP_DB DB, UAP_DB_HOST DHOST"     +
          "         WHERE DBACCT.DB_ID = DB.DB_ID"     +
          "           AND DHOST.DB_ID = DB.DB_ID"     +
          "        UNION ALL"     +
          "        SELECT NDACCT.ND_ACCT_ID ID,"     +
          "               NDACCT.ACCT_NAME ACCTNAME,"     +
          "               ND.ENTITY_NAME RESNAME,"     +
          "               CASE"     +
          "                 WHEN ND.LOGIN_TYPE = '1' THEN"     +
          "                  ND.IP || ':' || ND.TELNET_PORT"     +
          "                 ELSE"     +
          "                  ND.IP || ':' || ND.SSH2_PORT"     +
          "               END RESADDRESS,"     +
          "               '03' RESTYPE,"     +
          "               DECODE(NDACCT.ACCT_STATUS, '0', '1', '1', '0', '0') LOCKSTATUS,"     +
          "               NDACCT.ACCT_TYPE ACCTTYPE,"     +
          "               NDACCT.CREATE_TIME ESTABLISHTIME,"     +
          "               NDACCT.LAST_UPDATE_TIME UPDATETIME"     +
          "          FROM UAP_ND_ACCT NDACCT"     +
          "          LEFT JOIN UAP_NET_DEVICE ND"     +
          "            ON NDACCT.DEVICE_ID = ND.DEVICE_ID)";
}