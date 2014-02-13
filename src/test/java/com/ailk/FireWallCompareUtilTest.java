package com.ailk;

import com.ailk.uap.makefile4new.compare.FireWallCompare;
import com.ailk.uap.makefile4new.compare.FireWallCompareUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-25
 * Time: 下午3:22
 */
public class FireWallCompareUtilTest {

    @Test
    public void testIsTwoListMatch() {
        FireWallCompare fireWallCompare1 = new FireWallCompare();
        fireWallCompare1.setLoginname("test1");
        fireWallCompare1.setOpertime("2013-07-25 16:08:43");
        fireWallCompare1.setResaddress("10.87.21.64:22");
        fireWallCompare1.setOpercontent("access-list inside_rule extended permit tcp object-group 12580server host   10.87.21.64    range   ftp-data    ssh");
        fireWallCompare1.setSheetno("00001");

        FireWallCompare fireWallCompare2 = new FireWallCompare();
        fireWallCompare2.setLoginname("test2");
        fireWallCompare2.setOpertime("2013-07-25 15:08:43");
        fireWallCompare2.setResaddress("10.87.21.63:22");
        fireWallCompare2.setOpercontent("access-list inside_rule extended permit tcp object-group 12580server host   10.87.21.63    range   ftp-data    ssh");
        fireWallCompare2.setSheetno("00002");

        FireWallCompare fireWallCompare3 = new FireWallCompare();
        fireWallCompare3.setLoginname("test3");
        fireWallCompare3.setOpertime("2013-07-25 12:08:43");
        fireWallCompare3.setResaddress("10.87.21.61:22");
        fireWallCompare3.setOpercontent("access-list inside_rule extended permit tcp object-group 12580server host   10.87.21.61    range   ftp-data    ssh");
        fireWallCompare3.setSheetno("00003");

        List<FireWallCompare> list1 = new ArrayList<FireWallCompare>();
        list1.add(fireWallCompare1);
        list1.add(fireWallCompare2);
        list1.add(fireWallCompare3);

        FireWallCompare smp1 = new FireWallCompare();
        smp1.setLoginname("test1");
        smp1.setOpertime("2013-07-25 16:08:43");
        smp1.setResaddress("10.87.21.64");
        smp1.setOpercontent("access-list inside_rule  extended permit  tcp  object-group 12580server   host  10.87.21.64  range   20  22");
        smp1.setSheetno("00001");

        FireWallCompare smp2 = new FireWallCompare();
        smp2.setLoginname("test2");
        smp2.setOpertime("2013-07-25 15:08:43");
        smp2.setResaddress("10.87.21.63");
        smp2.setOpercontent("access-list inside_rule  extended permit  tcp  object-group 12580server   host  10.87.21.63  range   20  22");
        smp2.setSheetno("00002");

        FireWallCompare smp3 = new FireWallCompare();
        smp3.setLoginname("test3");
        smp3.setOpertime("2013-07-25 12:08:43");
        smp3.setResaddress("10.87.21.61");
        smp3.setOpercontent("access-list inside_rule  extended permit  tcp  object-group 12580server   host  10.87.21.61  range   20  22");
        smp3.setSheetno("00003");

        List<FireWallCompare> list2 = new ArrayList<FireWallCompare>();
        list2.add(smp1);
        list2.add(smp2);
        list2.add(smp3);

        boolean result = FireWallCompareUtil.isTwoListMatch(list1, list2);
        Assert.assertTrue(result);
    }

    @Test
    public void testIsTwoListMatchError() {
        FireWallCompare fireWallCompare1 = new FireWallCompare();
        fireWallCompare1.setLoginname("test1");
        fireWallCompare1.setOpertime("2013-07-25 16:08:43");
        fireWallCompare1.setResaddress("10.87.21.64:22");
        fireWallCompare1.setOpercontent("access-list inside_rule extended permit tcp object-group 12580server host   10.87.21.64    range   ftp-data    ssh");
        fireWallCompare1.setSheetno("00001");

        FireWallCompare fireWallCompare2 = new FireWallCompare();
        fireWallCompare2.setLoginname("test2");
        fireWallCompare2.setOpertime("2013-07-25 15:08:43");
        fireWallCompare2.setResaddress("10.87.21.63:22");
        fireWallCompare2.setOpercontent("access-list inside_rule extended permit tcp object-group 12580server host   10.87.21.63    range   ftp-data    ssh");
        fireWallCompare2.setSheetno("00002");

        FireWallCompare fireWallCompare3 = new FireWallCompare();
        fireWallCompare3.setLoginname("test3");
        fireWallCompare3.setOpertime("2013-07-25 12:08:43");
        fireWallCompare3.setResaddress("10.87.21.61:22");
        fireWallCompare3.setOpercontent("access-list inside_rule extended permit tcp object-group 12580server host   10.87.21.61    range   ftp-data    ssh");
        fireWallCompare3.setSheetno("00003");

        List<FireWallCompare> list1 = new ArrayList<FireWallCompare>();
        list1.add(fireWallCompare1);
        list1.add(fireWallCompare2);
        list1.add(fireWallCompare3);

        FireWallCompare smp1 = new FireWallCompare();
        smp1.setLoginname("test1");
        smp1.setOpertime("2013-07-25 16:08:43");
        smp1.setResaddress("10.87.21.64");
        smp1.setOpercontent("access-list inside_rule  extended permit  tcp  object-group 12580server1   host  10.87.21.64  range   20  22");
        smp1.setSheetno("00001");

        FireWallCompare smp2 = new FireWallCompare();
        smp2.setLoginname("test2");
        smp2.setOpertime("2013-07-25 15:08:43");
        smp2.setResaddress("10.87.21.63");
        smp2.setOpercontent("access-list inside_rule  extended permit  tcp  object-group 12580server   host  10.87.21.63  range   20  22");
        smp2.setSheetno("00002");

        FireWallCompare smp3 = new FireWallCompare();
        smp3.setLoginname("test3");
        smp3.setOpertime("2013-07-25 12:08:43");
        smp3.setResaddress("10.87.21.61");
        smp3.setOpercontent("access-list inside_rule  extended permit  tcp  object-group 12580server   host  10.87.21.61  range   20  22");
        smp3.setSheetno("00003");

        List<FireWallCompare> list2 = new ArrayList<FireWallCompare>();
        list2.add(smp1);
        list2.add(smp2);
        list2.add(smp3);

        boolean result = FireWallCompareUtil.isTwoListMatch(list1, list2);
        Assert.assertTrue(!result);
    }

    @Test
    public void test() {
        String smpCisco = "access-list inside_rule  extended permit  tcp  object-group 12580server   host  218.206.204.70  range   20  22";
        String a4Cisco = "access-list inside_rule extended permit tcp object-group 12580server host   218.206.204.70    range   ftp-data    ssh";

        System.out.println(getStringByCisco(smpCisco).equals(getStringByCisco(a4Cisco)));


        String smpHuawei = "rule 165   permit tcp source 10.87.21.58 0 destination 10.87.16.0 0.0.0.255 destination-port  eq 22";
        String a4Huawei = "rule 165 permit  tcp source   10.87.21.58  0  destination  10.87.16.0 0.0.0.255   destination-port   eq   ssh";

        System.out.println(getStringByHuawei(smpHuawei).equals(getStringByHuawei(a4Huawei)));

        String ip = "10.87.21.64";
        System.out.println(ip.substring(0, ip.indexOf(":")));
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
