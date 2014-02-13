package com.ailk.uap.makefile4new.compare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-25
 * Time: 上午9:57
 * <p/>
 * 防火墙对象对比工具
 */
public class FireWallCompareUtil {
    private static Logger logger = LoggerFactory.getLogger(FireWallCompareUtil.class);

    /**
     * 对比两个集合，如果里面的对象能一一匹配上，返回true，否则返回false
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return boolean
     */
    public static boolean isTwoListMatch(List<FireWallCompare> list1, List<FireWallCompare> list2) {
        // 如果数量不相等肯定不匹配
        if (list1.size() != list2.size()) {
            logger.error("list1 list2 size not equal!");
            return false;
        }

        // 先把list1中的对象一个一个在list2中匹配
        for (FireWallCompare compare1 : list1) {
            if (!isMatchInList(compare1, list2)) {
                logger.error("list1 object not find match in list2!");
                logger.error("this object is : loginname-[" + compare1.getLoginname()
                        + "] sheetno-[" + compare1.getSheetno()
                        + "] resaddress-[" + compare1.getResaddress()
                        + "] opertime-[" + compare1.getOpertime()
                        + "] opercontent-[" + compare1.getOpercontent() + "]");
                return false;
            }
        }

        // 再把list2中的对象一个一个在list1中匹配
        for (FireWallCompare compare2 : list1) {
            if (!isMatchInList(compare2, list1)) {
                logger.error("list2 object not find match in list1!");
                logger.error("this object is : loginname-[" + compare2.getLoginname()
                        + "] sheetno-[" + compare2.getSheetno()
                        + "] resaddress-[" + compare2.getResaddress()
                        + "] opertime-[" + compare2.getOpertime()
                        + "] opercontent-[" + compare2.getOpercontent() + "]");
                return false;
            }
        }

        return true;
    }

    /**
     * 在集合中查询有没有匹配上的
     *
     * @param source     要匹配的源对象
     * @param targetList 目标集合
     * @return boolean
     */
    private static boolean isMatchInList(FireWallCompare source, List<FireWallCompare> targetList) {
        boolean result = false;
        for (FireWallCompare target : targetList) {
            if (target.compare(source)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
