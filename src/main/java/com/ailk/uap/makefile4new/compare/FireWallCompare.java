package com.ailk.uap.makefile4new.compare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-25
 * Time: 上午9:56
 * <p/>
 * 防火墙对比类
 */
public class FireWallCompare {
    private static Logger logger = LoggerFactory.getLogger(FireWallCompare.class);

    private String loginname = ""; // 登录主帐号
    private String sheetno = ""; // 工单号
    private String resaddress = ""; // 防火墙地址（IP:端口）
    private String opertime = ""; // 操作时间
    private String opercontent = ""; // 操作内容

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getResaddress() {
        return resaddress;
    }

    public void setResaddress(String resaddress) {
        this.resaddress = resaddress;
    }

    public String getOpertime() {
        return opertime;
    }

    public void setOpertime(String opertime) {
        this.opertime = opertime;
    }

    public String getOpercontent() {
        return opercontent;
    }

    public void setOpercontent(String opercontent) {
        this.opercontent = opercontent;
    }

    public String getSheetno() {
        return sheetno;
    }

    public void setSheetno(String sheetno) {
        this.sheetno = sheetno;
    }

    public boolean compare(FireWallCompare compareObject) {
        try {
            // 先比较 loginname
            if (!this.loginname.equals(compareObject.getLoginname())) {
                return false;
            }
            // 再比较 sheetno
            if (!this.sheetno.equals(compareObject.getSheetno())) {
                return false;
            }
            // 再比较 opertime
            if (!this.opertime.equals(compareObject.getOpertime())) {
                return false;
            }

            // 再比较 resaddress
            String resaddressThis;
            if (this.resaddress.indexOf(":") > 0) {
                resaddressThis = this.resaddress.substring(0, this.resaddress.indexOf(":"));
            } else {
                resaddressThis = this.resaddress;
            }
            String resaddressCompare;
            if (compareObject.getResaddress().indexOf(":") > 0) {
                resaddressCompare = compareObject.getResaddress().substring(0, compareObject.getResaddress().indexOf(":"));
            } else {
                resaddressCompare = compareObject.getResaddress();
            }
            if (!resaddressThis.equals(resaddressCompare)) {
                return false;
            }

            // 最后对比最复杂的opercontent
            return compareContent(this.opercontent, compareObject.getOpercontent());
        } catch (Exception e) {
            logger.error("FireWallCompare method compare have Exception!", e);
            return false;
        }
    }

    // 按照全匹配，思科匹配，华为匹配三种，有一种匹配上，就算是通过
    private boolean compareContent(String target, String source) {
        return target.equals(source)
                || getStringByCisco(target).equals(getStringByCisco(source))
                || getStringByHuawei(target).equals(getStringByHuawei(source));

    }

    // 思科日志获取要匹配的字符串
    private String getStringByCisco(String source) {
        String result;
        if (source.lastIndexOf(" host ") != -1) {
            String first = source.substring(0, source.lastIndexOf(" host ") + 6);

            String source_last1 = source.substring(source.lastIndexOf(" host ") + 6).trim();
            String source_get1 = source_last1.substring(0, source_last1.indexOf(" "));

            String source_last2 = source_last1.substring(source_last1.indexOf(" ")).trim();
            String source_get2 = source_last2.substring(0, source_last2.indexOf(" "));

            result = (first + source_get1 + source_get2).replaceAll(" ", "");
        } else {
            result = source;
        }
        return result;
    }

    // 华为日志获取要匹配的字符串
    private String getStringByHuawei(String source) {
        String result;
        if (source.lastIndexOf(" destination-port ") != -1) {
            String first = source.substring(0, source.lastIndexOf(" destination-port ") + 18);

            String source_last1 = source.substring(source.lastIndexOf("destination-port") + 18).trim();
            String source_get1 = source_last1.substring(0, source_last1.indexOf(" "));

            result = (first + source_get1).replaceAll(" ", "");
        } else {
            result = source;
        }
        return result;
    }
}
