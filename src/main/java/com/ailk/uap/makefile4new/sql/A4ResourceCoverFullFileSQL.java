package com.ailk.uap.makefile4new.sql;

import com.ailk.uap.config.PropertiesUtil;
import com.ailk.uap.makefile4new.AbstractMakeMonthFile;
import com.ailk.uap.makefile4new.constant.BusiCode;

public abstract interface A4ResourceCoverFullFileSQL extends BusiCode
{
  public static final String A4_RESOURCE_COVER_FULL_SQL = "SELECT RESTYPE, RESNAME, RESADDRESS, EFFECTTIME FROM (                     SELECT                                                                    " 
	  + APP_SMCRF_SQL_OF_RESTYPE 
	  + "RESTYPE,                                                                  " 
	  + "       APP.APP_NAME RESNAME,                                              " 
	  + "       '无' RESADDRESS,                                                    " 
	  + "       APP.CREATE_TIME EFFECTTIME                                         " 
	  + "  FROM UAP_APP APP                                                        " 
	  + " WHERE app.lock_status='1' "
	  + "UNION ALL                                                                 " 
	  + "SELECT '01' RESTYPE,                                                      " 
	  + "       HOST.ENTITY_NAME RESNAME,                                          " 
	  + "       CASE                                                               " 
	  + "         WHEN HOST.LOGIN_TYPE = '1' THEN                                  " 
	  + "          IP.IP || ':' || HOST.TELNET_PORT                                " 
	  + "         ELSE                                                             " 
	  + "          IP.IP || ':' || HOST.SSH_PORT                                   " 
	  + "       END RESADDRESS,                                                    "
      + "       HOST.CREATE_TIME EFFECTTIME                                        "
      + "  FROM UAP_HOST HOST                                                      " 
      + "  LEFT JOIN UAP_HOST_IP IP                                                " 
      + "    ON HOST.HOST_ID = IP.HOST_ID                                          " 
      + "   AND IP.IS_DEFAULT = '1'                                                " 
      + " where host.lock_status='1' "
      + "UNION ALL                                                                 " 
      /*+ "SELECT '04' RESTYPE,                                                      "
      + "       SD.ENTITY_NAME RESNAME,                                            " 
      + "       CASE                                                               " 
      + "         WHEN SD.LOGIN_TYPE = '1' THEN                                    " 
      + "          SD.IP || ':' || SD.TELNET_PORT                                  " 
      + "         ELSE                                                             " 
      + "          SD.IP || ':' || SD.SSH2_PORT                                    " 
      + "       END RESADDRESS,                                                    " 
      + "       SD.CREATE_TIME EFFECTTIME                                          " 
      + "  FROM UAP_SECURITY_DEVICE SD where sd.ip not in ('10.96.161.228','10.96.161.196','10.96.161.236','10.96.164.136') " // todo 按照申诉涛要求，2013年7月4日该处过滤了四个IP*/
          + " SELECT '04' RESTYPE, " +
          "       SD.ENTITY_NAME RESNAME, " +
          "       CASE " +
          "         WHEN SD.LOGIN_TYPE = '1' THEN " +
          "          SD.IP || ':' || SD.TELNET_PORT " +
          "         ELSE " +
          "          SD.IP || ':' || SD.SSH2_PORT " +
          "       END RESADDRESS, " +
          "       SD.CREATE_TIME EFFECTTIME " +
          "  FROM UAP_SECURITY_DEVICE SD " +
          "  WHERE sd.IP IS NOT NULL " +
          "  AND  sd.lock_status='1' " + 
          " UNION ALL " +
          " SELECT '04' RESTYPE, " +
          "       SD.ENTITY_NAME RESNAME, " +
          "       CASE " +
          "         WHEN SD.LOGIN_TYPE = '1' THEN " +
          "          SD.IP_BAK || ':' || SD.TELNET_PORT " +
          "         ELSE " +
          "          SD.IP_BAK || ':' || SD.SSH2_PORT " +
          "       END RESADDRESS, " +
          "       SD.CREATE_TIME EFFECTTIME " +
          "  FROM UAP_SECURITY_DEVICE SD " +
          "  WHERE sd.IP_BAK IS NOT NULL  " +
          " AND  sd.lock_status='1' " +
          " UNION ALL " +
          " SELECT '04' RESTYPE, " +
          "       SD.ENTITY_NAME RESNAME, " +
          "       CASE " +
          "         WHEN SD.LOGIN_TYPE = '1' THEN " +
          "          SD.STANDBY_IP || ':' || SD.TELNET_PORT " +
          "         ELSE " +
          "          SD.STANDBY_IP || ':' || SD.SSH2_PORT " +
          "       END RESADDRESS, " +
          "       SD.CREATE_TIME EFFECTTIME " +
          "  FROM UAP_SECURITY_DEVICE SD " +
          "  WHERE sd.STANDBY_IP IS NOT NULL  " +
          "  AND  sd.lock_status='1'  " +
          " UNION ALL " +
          " SELECT '04' RESTYPE, " +
          "       SD.ENTITY_NAME RESNAME, " +
          "       CASE " +
          "         WHEN SD.LOGIN_TYPE = '1' THEN " +
          "          SD.STANDBY_IP_BAK || ':' || SD.TELNET_PORT " +
          "         ELSE " +
          "          SD.STANDBY_IP_BAK || ':' || SD.SSH2_PORT " +
          "       END RESADDRESS, " +
          "       SD.CREATE_TIME EFFECTTIME " +
          "  FROM UAP_SECURITY_DEVICE SD " +
          "  WHERE sd.standby_ip_bak IS NOT NULL  " +
          " AND  sd.lock_status=1  "
          + "UNION ALL                                                                 "
      + "SELECT '02' RESTYPE,                                                      " 
      + "CASE\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" 
      + "WHEN DHOST.INSTANCE_NAME IS NULL THEN\t\t\t\t\t\t\t\t\t\t" 
      + "DHOST.DB_NAME\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" 
      + "ELSE\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" 
      + "DHOST.INSTANCE_NAME\t\t\t\t\t\t\t\t\t\t\t\t\t\t" 
      + "END RESNAME,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" 
      + "CASE DHOST.DEPLOY_MODE                                                    "
      + "WHEN '1' THEN                                                             "
      + "DHOST.IP_ADDR || ':' || DHOST.PORT                                        "
      + "WHEN '0' THEN                                                             "
      + "\t\t\t\t(SELECT IP.IP                                               "
      + "\t\t\t        FROM UAP_HOST HOST, UAP_HOST_IP IP                   \t" 
      + "\t\t\t         WHERE DHOST.HOST_ID = HOST.HOST_ID                  \t" 
      + "\t\t\t           AND HOST.HOST_ID = IP.HOST_ID                     \t"
      + "\t\t\t           AND IP.IS_DEFAULT = '1'                           \t"
      + "\t\t\t    ) || ':' || DHOST.PORT                                   \t" 
      + "ELSE                                                                      " 
      + " '无'                                                                      "
      + "END RESADDRESS,                                                           " 
      + "       DB.CREATE_TIME EFFECTTIME                                          " 
      + "  FROM UAP_DB DB, UAP_DB_HOST DHOST                                       " 
      + " WHERE DB.DB_ID = DHOST.DB_ID                                             " 
      + " AND  db.lock_status='1' "
      + "UNION ALL                                                                 " 
      + "SELECT '03' RESTYPE,                                                      " 
      + "       ND.ENTITY_NAME RESNAME,                                            " 
      + "       CASE                                                               " 
      + "         WHEN ND.LOGIN_TYPE = '1' THEN                                    " 
      + "          ND.IP || ':' || ND.TELNET_PORT                                  " 
      + "         ELSE                                                             " 
      + "          ND.IP || ':' || ND.SSH2_PORT                                    " 
      + "       END RESADDRESS,                                                    " 
      + "       ND.CREATE_TIME EFFECTTIME                                          " 
      + "  FROM UAP_NET_DEVICE ND  WHERE nd.lock_status='1' )                                              ";

}