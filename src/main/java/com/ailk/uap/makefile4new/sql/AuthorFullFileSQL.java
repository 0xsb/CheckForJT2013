package com.ailk.uap.makefile4new.sql;

public abstract interface AuthorFullFileSQL
{
  public static final String AUTHOR_SQL = "SELECT M.LOGIN_NAME MAINACC,"     +
          "       M.MAIN_ACCT_ID MAINID,"     +
          "       CASE"     +
          "         WHEN T.RES_KIND = '1' THEN"     +
          "          (SELECT APP.LOGIN_ACCT"     +
          "             FROM UAP_APP_ACCT APP"     +
          "            WHERE APP.ACCT_SEQ = T.RES_ACCT_ID)"     +
          "         WHEN T.RES_KIND = '2' THEN"     +
          "          (SELECT HOST.ACCT_NAME"     +
          "             FROM UAP_HOST_ACCT HOST"     +
          "            WHERE HOST.HOST_ACCT_ID = T.RES_ACCT_ID)"     +
          "         WHEN T.RES_KIND = '3' THEN"     +
          "          (SELECT DB.ACCT_NAME"     +
          "             FROM UAP_DB_ACCT DB"     +
          "            WHERE DB.DB_ACCT_ID = T.RES_ACCT_ID)"     +
          "         WHEN T.RES_KIND = '4' THEN"     +
          "          (SELECT ND.ACCT_NAME"     +
          "             FROM UAP_ND_ACCT ND"     +
          "            WHERE ND.ND_ACCT_ID = T.RES_ACCT_ID)"     +
          "         WHEN T.RES_KIND = '5' THEN"     +
          "          (SELECT SD.ACCT_NAME"     +
          "             FROM UAP_SD_ACCT SD"     +
          "            WHERE SD.SD_ACCT_ID = T.RES_ACCT_ID)"     +
          "         ELSE"     +
          "          ''"     +
          "       END SUBACC,"     +
          "       T.RES_ACCT_ID SUBID,"     +
          "       CASE"     +
          "         WHEN T.RES_KIND = '1' THEN"     +
          "          '2'"     +
          "         ELSE"     +
          "          '1'"     +
          "       END SUBACCTYPE,"     +
          "       T.AUTHOR_EFFECT_TIME EFFECTTIME,"     +
          "       T.AUTHOR_EXPIRE_TIME EXPIRETIME"     +
          "  FROM UAP_ACCT_AUTHOR T, UAP_MAIN_ACCT M"     +
          " WHERE T.USE_MAIN_ACCT_ID = M.MAIN_ACCT_ID ";
}