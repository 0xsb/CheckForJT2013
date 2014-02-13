package com.ailk.check.safeguard.safe.random.safe;

import com.ailk.check.FileType;
import com.ailk.check.safeguard.safe.random.shuffle.ShuffleList;
import com.ailk.check.safeguard.safe.random.shuffle.ShuffleTime;
import com.ailk.check.xsd.JAXBUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-23
 * Time: 上午11:25
 * <p/>
 * 金库申请审批日文件随机安全文件创建者
 */
public class JKDayRandomSafeFileCreator {
    private static Logger logger = Logger.getLogger(JKDayRandomSafeFileCreator.class);

    private static final int APPROVE_INFO_FLAG = 4;

    private int year; // 年
    private int month; // 月
    private int day; // 日
    private boolean generateJKSuccess; // 是否生成金库成功

    /**
     * 传入的时间是文件中数据所对应的时间
     * 其中月从零开始不用加一
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public JKDayRandomSafeFileCreator(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.generateJKSuccess = false;
    }

    // 是否生成成功
    public boolean isGenerateJKSuccess() {
        return this.generateJKSuccess;
    }

    /**
     * 创建金库安全文件
     */
    public void createJKSafeFile() {
        logger.info("create JK safe file " + year + month + day + " ...");
        try {
            // 获取安全模板文件
            File jkrTemplateFile = new File(FileType.SMJKR.randomSafeTemplatePath(day));
            File jkaTemplateFile = new File(FileType.SMJKA.randomSafeTemplatePath(day));

            // 从安全模板XML获取对象
            com.ailk.check.xsd.smjkr.Smp jkrTemplateObject = JAXBUtils.unMarshal(com.ailk.check.xsd.smjkr.Smp.class, FileUtils.readFileToString(jkrTemplateFile, "UTF-8"));
            com.ailk.check.xsd.smjka.Smp jkaTemplateObject
                    = JAXBUtils.unMarshal(com.ailk.check.xsd.smjka.Smp.class, FileUtils.readFileToString(jkaTemplateFile, "UTF-8"));
            // 洗牌并获取对应关系
            List<RcdMapping> rcdMappingList = shuffleAndGetMappingList(jkrTemplateObject, jkaTemplateObject);
            // 洗时间、ID、seq
            shuffleTimeAndIdSortSeq(jkrTemplateObject, jkaTemplateObject, rcdMappingList);

            // 生成安全文件
            String jkrName = FileType.SMJKR.getFileName(year, month, day, 0);
            generateJKSafeFile(FileType.SMJKR.generateXmlPath(jkrName), jkrTemplateObject, com.ailk.check.xsd.smjkr.Smp.class);
            String jkaName = FileType.SMJKA.getFileName(year, month, day, 0);
            generateJKSafeFile(FileType.SMJKA.generateXmlPath(jkaName), jkaTemplateObject, com.ailk.check.xsd.smjka.Smp.class);
            generateJKSuccess = true;
        } catch (Exception e) {
            logger.error("JKDayRandomSafeFileCreator.createJKSafeFile have Exception!", e);
            generateJKSuccess = false;
        }
    }

    /**
     * 生成安全文件
     *
     * @param path   生成安全文件路径
     * @param object 序列化对象
     * @return 安全文件
     * @throws IOException
     * @throws JAXBException
     */
    private File generateJKSafeFile(String path, Object object, Class clazz) throws IOException, JAXBException {
        // 创建生成文件
        File safeFile = new File(path);
        FileUtils.touch(safeFile);
        // 将洗过的对象序列化为XML
        String jkXml = JAXBUtils.marshal(clazz, object);
        FileUtils.writeStringToFile(safeFile, jkXml, "UTF-8");
        return safeFile;
    }

    // 洗时间、ID、seq
    private void shuffleTimeAndIdSortSeq(com.ailk.check.xsd.smjkr.Smp jkrTemplateObject, com.ailk.check.xsd.smjka.Smp jkaTemplateObject, List<RcdMapping> rcdMappingList) throws DatatypeConfigurationException {
        // 新建Data对象
        com.ailk.check.xsd.smjkr.ObjectFactory jkrObjectFactory = new com.ailk.check.xsd.smjkr.ObjectFactory();
        com.ailk.check.xsd.smjkr.Smp.Data jkrData = jkrObjectFactory.createSmpData();
        com.ailk.check.xsd.smjka.ObjectFactory jkaObjectFactory = new com.ailk.check.xsd.smjka.ObjectFactory();
        com.ailk.check.xsd.smjka.Smp.Data jkaData = jkaObjectFactory.createSmpData();

        // 首先，找出重复的requestId
        Set<String> requestSet = new HashSet<String>();
        Set<String> sameSet = new HashSet<String>();
        for (RcdMapping rcdMapping : rcdMappingList) {
            if (requestSet.contains(rcdMapping.getRequestId())) {
                sameSet.add(rcdMapping.getRequestId());
                continue;
            }
            requestSet.add(rcdMapping.getRequestId());
        }

        // 把不重复的rcdMapping取出来
        List<RcdMapping> noSameList = new ArrayList<RcdMapping>();
        for (RcdMapping rcdMapping : rcdMappingList) {
            if (!sameSet.contains(rcdMapping.getRequestId())) {
                noSameList.add(rcdMapping);
            }
        }

        // 把requestId重复的rcdMapping分组后取出来
        List<List<RcdMapping>> sameGroupList = new ArrayList<List<RcdMapping>>();
        for (String requestId : sameSet) {
            List<RcdMapping> sameList = new ArrayList<RcdMapping>();
            for (RcdMapping rcdMapping : rcdMappingList) {
                if (requestId.equals(rcdMapping.getRequestId())) {
                    sameList.add(rcdMapping);
                }
            }
            sameGroupList.add(sameList);
        }

        // 处理非重复RcdMapping
        for (RcdMapping rcdMapping : noSameList) {
            // 时间对等变化
            long changeValue = ShuffleTime.getChangeValue();
            XMLGregorianCalendar requestTime = ShuffleTime.shuffle(rcdMapping.getJkrRcd().getRequesttime(), changeValue);
            requestTime = ShuffleTime.resetYearMonthDay(requestTime, year, month, day);
            rcdMapping.getJkrRcd().setRequesttime(requestTime);
            XMLGregorianCalendar beginTime = ShuffleTime.shuffle(rcdMapping.getJkrRcd().getBegintime(), changeValue);
            beginTime = ShuffleTime.resetYearMonthDay(beginTime, year, month, day);
            rcdMapping.getJkrRcd().setBegintime(beginTime);
            XMLGregorianCalendar endTime = ShuffleTime.shuffle(rcdMapping.getJkrRcd().getEndtime(), changeValue);
            endTime = ShuffleTime.resetYearMonthDay(endTime, year, month, day);
            rcdMapping.getJkrRcd().setEndtime(endTime);
            // 结束时间一般秒都是0
            //rcdMapping.getJkrRcd().getEndtime().setSecond(0);

            XMLGregorianCalendar approveTime = ShuffleTime.shuffle(rcdMapping.getJkaRcd().getApprovetime(), changeValue);
            approveTime = ShuffleTime.resetYearMonthDay(approveTime, year, month, day);
            rcdMapping.getJkaRcd().setApprovetime(approveTime);

            // 替换申请ID
            String requestId = generateRequestId(rcdMapping.getRequestId());
            rcdMapping.getJkrRcd().setRequestid(requestId);
            rcdMapping.getJkaRcd().setRequestid(requestId);

            // 处理approveInfo
            if (rcdMapping.getJkaRcd().getApproveinfo().length() > APPROVE_INFO_FLAG) {
                String sheetId = generateSheetId(rcdMapping.getJkaRcd().getApproveinfo());
                rcdMapping.getJkaRcd().setApproveinfo(sheetId);
            }
        }

        // 处理重复RcdMapping
        for (List<RcdMapping> sameList : sameGroupList) {
            // 时间对等变化
            long changeValue = ShuffleTime.getChangeValue();
            for (RcdMapping rcdMapping : sameList) {
                XMLGregorianCalendar requestTime = ShuffleTime.shuffle(rcdMapping.getJkrRcd().getRequesttime(), changeValue);
                requestTime = ShuffleTime.resetYearMonthDay(requestTime, year, month, day);
                rcdMapping.getJkrRcd().setRequesttime(requestTime);
                XMLGregorianCalendar beginTime = ShuffleTime.shuffle(rcdMapping.getJkrRcd().getBegintime(), changeValue);
                beginTime = ShuffleTime.resetYearMonthDay(beginTime, year, month, day);
                rcdMapping.getJkrRcd().setBegintime(beginTime);
                XMLGregorianCalendar endTime = ShuffleTime.shuffle(rcdMapping.getJkrRcd().getEndtime(), changeValue);
                endTime = ShuffleTime.resetYearMonthDay(endTime, year, month, day);
                rcdMapping.getJkrRcd().setEndtime(endTime);
                // 结束时间一般秒都是0
                //rcdMapping.getJkrRcd().getEndtime().setSecond(0);

                XMLGregorianCalendar approveTime = ShuffleTime.shuffle(rcdMapping.getJkaRcd().getApprovetime(), changeValue);
                approveTime = ShuffleTime.resetYearMonthDay(approveTime, year, month, day);
                rcdMapping.getJkaRcd().setApprovetime(approveTime);
            }

            // 替换申请ID
            String requestId = generateRequestId(sameList.get(0).getRequestId());
            for (RcdMapping rcdMapping : sameList) {
                rcdMapping.getJkrRcd().setRequestid(requestId);
                rcdMapping.getJkaRcd().setRequestid(requestId);
            }

            // 处理approveInfo 该处需要注意，主要是相同的会有工单号
            String oldApproveInfo = sameList.get(0).getJkaRcd().getApproveinfo();
            if (oldApproveInfo.length() > APPROVE_INFO_FLAG) {
                String sheetId = generateSheetId(oldApproveInfo);
                for (RcdMapping rcdMapping : sameList) {
                    rcdMapping.getJkaRcd().setApproveinfo(sheetId);
                }
            }
        }

        int index = 1;
        for (RcdMapping rcdMapping : rcdMappingList) {
            // 重排序列
            rcdMapping.getJkrRcd().setSeq(BigInteger.valueOf(index));
            rcdMapping.getJkaRcd().setSeq(BigInteger.valueOf(index));
            index++;

            jkrData.getRcd().add(rcdMapping.getJkrRcd());
            jkaData.getRcd().add(rcdMapping.getJkaRcd());
        }

        // 修改金库XML头部时间
        changeHeadTimeForJK(jkrTemplateObject, jkaTemplateObject);

        jkrTemplateObject.setData(jkrData);
        jkaTemplateObject.setData(jkaData);

        // 调整sum值为洗牌后的
        jkrTemplateObject.setSum(BigInteger.valueOf(jkrData.getRcd().size()));
        jkaTemplateObject.setSum(BigInteger.valueOf(jkaData.getRcd().size()));
    }

    // 修改金库XML头部时间
    private void changeHeadTimeForJK(com.ailk.check.xsd.smjkr.Smp jkrTemplateObject, com.ailk.check.xsd.smjka.Smp jkaTemplateObject) throws DatatypeConfigurationException {
        jkrTemplateObject.setCreatetime(ShuffleTime.resetYearMonthDay(jkrTemplateObject.getCreatetime(), year, month, day + 1));
        jkrTemplateObject.setBegintime(ShuffleTime.resetYearMonthDay(jkrTemplateObject.getBegintime(), year, month, day));
        jkrTemplateObject.setEndtime(ShuffleTime.resetYearMonthDay(jkrTemplateObject.getEndtime(), year, month, day + 1));

        jkaTemplateObject.setCreatetime(ShuffleTime.resetYearMonthDay(jkrTemplateObject.getCreatetime(), year, month, day + 1));
        jkaTemplateObject.setBegintime(ShuffleTime.resetYearMonthDay(jkrTemplateObject.getBegintime(), year, month, day));
        jkaTemplateObject.setEndtime(ShuffleTime.resetYearMonthDay(jkrTemplateObject.getEndtime(), year, month, day + 1));
    }

    /**
     * 洗牌并获取对应关系
     *
     * @param jkrTemplateObject 金库申请对象
     * @param jkaTemplateObject 金库审核对象
     * @return 对应关系
     */
    private List<RcdMapping> shuffleAndGetMappingList(com.ailk.check.xsd.smjkr.Smp jkrTemplateObject, com.ailk.check.xsd.smjka.Smp jkaTemplateObject) {
        // 首先，找出重复的requestId
        Set<String> requestSet = new HashSet<String>();
        Set<String> sameSet = new HashSet<String>();
        for (com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd : jkrTemplateObject.getData().getRcd()) {
            if (requestSet.contains(jkrRcd.getRequestid())) {
                sameSet.add(jkrRcd.getRequestid());
                continue;
            }
            requestSet.add(jkrRcd.getRequestid());
        }

        // 把不重复的rcd取出来
        List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd> noSameList = new ArrayList<com.ailk.check.xsd.smjkr.Smp.Data.Rcd>();
        for (com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd : jkrTemplateObject.getData().getRcd()) {
            if (!sameSet.contains(jkrRcd.getRequestid())) {
                noSameList.add(jkrRcd);
            }
        }

        // 把requestId重复的rcd分组后取出来
        List<List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd>> groupList = new ArrayList<List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd>>();
        for (String requestId : sameSet) {
            List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd> sameList = new ArrayList<com.ailk.check.xsd.smjkr.Smp.Data.Rcd>();
            for (com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd : jkrTemplateObject.getData().getRcd()) {
                if (requestId.equals(jkrRcd.getRequestid())) {
                    sameList.add(jkrRcd);
                }
            }
            groupList.add(sameList);
        }

        // 随机减少
        ShuffleList.reduce(noSameList, ShuffleList.Per.TWO, ShuffleList.Base.THOUSAND);

        // 分组后洗牌
        for (com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd : noSameList) {
            List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd> list = new ArrayList<com.ailk.check.xsd.smjkr.Smp.Data.Rcd>();
            list.add(jkrRcd);
            groupList.add(list);
        }
        ShuffleList.shuffle(groupList);

        // 洗牌后把分组去掉
        List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd> jkrRcdList = new ArrayList<com.ailk.check.xsd.smjkr.Smp.Data.Rcd>();
        for (List<com.ailk.check.xsd.smjkr.Smp.Data.Rcd> rcdList : groupList) {
            for (com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd : rcdList) {
                jkrRcdList.add(jkrRcd);
            }
        }

        // 找洗牌后的对应关系
        List<RcdMapping> rcdMappingList = new ArrayList<RcdMapping>();
        for (com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd : jkrRcdList) {
            for (com.ailk.check.xsd.smjka.Smp.Data.Rcd jkaRcd : jkaTemplateObject.getData().getRcd()) {
                if (jkaRcd.getRequestid().equals(jkrRcd.getRequestid()) && jkaRcd.getSeq().equals(jkrRcd.getSeq())) {
                    RcdMapping rcdMapping = new RcdMapping();
                    rcdMapping.setJkaRcd(jkaRcd);
                    rcdMapping.setJkrRcd(jkrRcd);
                    rcdMapping.setRequestId(jkrRcd.getRequestid());
                    rcdMappingList.add(rcdMapping);
                }
            }
        }
        return rcdMappingList;
    }

    // requestId生成规则：在原有基础上加上 550,0000，但该日期的模板只能使用一次
    private String generateRequestId(String requestId) {
        return String.valueOf(Integer.valueOf(requestId) + 5500000);
    }

    // todo 工单ID的规则需要确认 增加值为5500,0000
    private String generateSheetId(String approveInfo) {
        String result = approveInfo;
        if (approveInfo.length() > APPROVE_INFO_FLAG) {
            String sheetNum = approveInfo.substring(2, approveInfo.length());
            sheetNum = String.valueOf(Integer.valueOf(sheetNum) + 55000000);
            result = approveInfo.substring(0, 2) + sheetNum;
        }
        return result;
    }

    class RcdMapping {
        private com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd;
        private com.ailk.check.xsd.smjka.Smp.Data.Rcd jkaRcd;

        private String requestId;

        com.ailk.check.xsd.smjkr.Smp.Data.Rcd getJkrRcd() {
            return jkrRcd;
        }

        void setJkrRcd(com.ailk.check.xsd.smjkr.Smp.Data.Rcd jkrRcd) {
            this.jkrRcd = jkrRcd;
        }

        com.ailk.check.xsd.smjka.Smp.Data.Rcd getJkaRcd() {
            return jkaRcd;
        }

        void setJkaRcd(com.ailk.check.xsd.smjka.Smp.Data.Rcd jkaRcd) {
            this.jkaRcd = jkaRcd;
        }

        String getRequestId() {
            return requestId;
        }

        void setRequestId(String requestId) {
            this.requestId = requestId;
        }
    }
}
