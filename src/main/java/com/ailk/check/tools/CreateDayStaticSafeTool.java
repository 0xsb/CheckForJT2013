package com.ailk.check.tools;

import com.ailk.check.safeguard.safe.DaySafeFileCreator;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import com.ailk.check.utils.CalendarUtil;
import com.ailk.check.utils.FileUtil;
import com.ailk.jt.util.PropertiesUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-18
 * Time: 下午2:16
 */
public class CreateDayStaticSafeTool {
    private static Logger logger = LoggerFactory.getLogger(CreateDayStaticSafeTool.class);

    public static void main(String[] args) {
        logger.info("create day static safe file ... ");

        String uploadPath = PropertiesUtil.getValue("uap_file_uapload");
        String osFlag = PropertiesUtil.getValue("os_flag");
        String daySafeFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");
        String createPath = PropertiesUtil.getValue("uap_file_uapload_for_init_static");
        String rightDateBegin = PropertiesUtil.getValue("right_Datebegin").trim();

        // 设置日期为8月份 生成8月份所有的静态安全文件 金库的注释掉暂时不要
        int year = 2013;
        int month = 7; // 月份是从0开始的

        for (int i = 1; i < 32; i++) {
            DaySafeFileCreator daySafeFileCreator = new DaySafeFileCreator(year, month, i);

            String nameDate = CalendarUtil.getAtStringDateFromNumber(year, month, i);

            try {
                logger.info("create SMMAI ...");
                String createPathSMMAI = createPath + osFlag + "SMMAI_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMMAI = daySafeFilePath + osFlag + "SMMAI_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMMAI = new XmlReader(safePathSMMAI);
                daySafeFileCreator.createStaticSafeFile(createPathSMMAI, xmlReaderSMMAI, rightDateBegin, daySafeFileCreator.getDate(),
                        "/smp/data/rcd/updatetime");
                logger.info("create SMMAI result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMMAI, uploadPath);
                }
                logger.info("create SMMAI end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMBHR ...");
                String createPathSMBHR = createPath + osFlag + "SMBHR_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMBHR = daySafeFilePath + osFlag + "SMBHR_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMBHR = new XmlReader(safePathSMBHR);
                daySafeFileCreator.createStaticSafeFile(createPathSMBHR, xmlReaderSMBHR, "", "");
                logger.info("create SMBHR result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMBHR, uploadPath);
                }
                logger.info("create SMBHR end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMDAR ...");
                String createPathSMDAR = createPath + osFlag + "SMDAR_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMDAR = daySafeFilePath + osFlag + "SMDAR_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMDAR = new XmlReader(safePathSMDAR);
                daySafeFileCreator.createStaticSafeFile(createPathSMDAR, xmlReaderSMDAR, "", "");
                logger.info("create SMDAR result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMDAR, uploadPath);
                }
                logger.info("create SMDAR end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SM4AI ...");
                String createPathSM4AI = createPath + osFlag + "SM4AI_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSM4AI = daySafeFilePath + osFlag + "SM4AI_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSM4AI = new XmlReader(safePathSM4AI);
                daySafeFileCreator.createStaticSafeFile(createPathSM4AI, xmlReaderSM4AI, rightDateBegin, daySafeFileCreator.getDate(),
                        "/smp/data/rcd/updatetime");
                logger.info("create SM4AI result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSM4AI, uploadPath);
                }
                logger.info("create SM4AI end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMAAI ...");
                String createPathSMAAI = createPath + osFlag + "SMAAI_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMAAI = daySafeFilePath + osFlag + "SMAAI_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMAAI = new XmlReader(safePathSMAAI);
                daySafeFileCreator.createStaticSafeFile(createPathSMAAI, xmlReaderSMAAI, rightDateBegin, daySafeFileCreator.getDate(),
                        "/smp/data/rcd/updatetime");
                logger.info("create SMAAI result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMAAI, uploadPath);
                }
                logger.info("create SMAAI end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SM4AR ...");
                String createPathSM4AR = createPath + osFlag + "SM4AR_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSM4AR = daySafeFilePath + osFlag + "SM4AR_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSM4AR = new XmlReader(safePathSM4AR);
                daySafeFileCreator.createStaticSafeFile(createPathSM4AR, xmlReaderSM4AR, "", "");
                logger.info("create SM4AR result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSM4AR, uploadPath);
                }
                logger.info("create SM4AR end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMAAR ...");
                String createPathSMAAR = createPath + osFlag + "SMAAR_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMAAR = daySafeFilePath + osFlag + "SM4AR_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMAAR = new XmlReader(safePathSMAAR);
                daySafeFileCreator.createStaticSafeFile(createPathSMAAR, xmlReaderSMAAR, "SM4AR", "SMAAR", "/smp/type");
                logger.info("create SMAAR result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMAAR, uploadPath);
                }
                logger.info("create SMAAR end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMFWL ...");
                String createPathSMFWL = createPath + osFlag + "SMFWL_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMFWL = daySafeFilePath + osFlag + "SMFWL_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMFWL = new XmlReader(safePathSMFWL);
                daySafeFileCreator.createStaticSafeFile(createPathSMFWL, xmlReaderSMFWL, "toReplace", "SMFWL", "/smp/type");
                logger.info("create SMFWL result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMFWL, uploadPath);
                }
                logger.info("create SMFWL end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMFLO ...");
                String createPathSMFLO = createPath + osFlag + "SMFLO_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMFLO = daySafeFilePath + osFlag + "SMFWL_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMFLO = new XmlReader(safePathSMFLO);
                daySafeFileCreator.createStaticSafeFile(createPathSMFLO, xmlReaderSMFLO, "toReplace", "SMFLO", "/smp/type");
                logger.info("create SMFLO result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMFLO, uploadPath);
                }
                logger.info("create SMFLO end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            /*try {
                logger.info("create SMJKR ...");
                String createPathSMJKR = createPath + osFlag + "SMJKR_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMJKR = daySafeFilePath + osFlag + "SMJKR_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMJKR = new XmlReader(safePathSMJKR);
                daySafeFileCreator.createStaticSafeFile(createPathSMJKR, xmlReaderSMJKR, rightDateBegin, daySafeFileCreator.getDate(),
                        "/smp/data/rcd/requesttime",
                        "/smp/data/rcd/begintime",
                        "/smp/data/rcd/endtime");
                logger.info("create SMJKR result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMJKR, uploadPath);
                }
                logger.info("create SMJKR end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }


            try {
                logger.info("create SMJKA ...");
                String createPathSMJKA = createPath + osFlag + "SMJKA_371_01DY_" + nameDate + "_000_000.xml";
                String safePathSMJKA = daySafeFilePath + osFlag + "SMJKA_371_01DY_" + rightDateBegin.replace("-", "") + "_000_000.xml";
                XmlReader xmlReaderSMJKA = new XmlReader(safePathSMJKA);
                daySafeFileCreator.createStaticSafeFile(createPathSMJKA, xmlReaderSMJKA, rightDateBegin, daySafeFileCreator.getDate(),
                        "/smp/data/rcd/approvetime");
                logger.info("create SMJKA result : " + daySafeFileCreator.isCreateSuccess());
                if (daySafeFileCreator.isCreateSuccess()) {
                    FileUtil.moveToDir(createPathSMJKA, uploadPath);
                }
                logger.info("create SMJKA end.");
            } catch (DocumentException e) {
                logger.error("createDayStaticSafeMain have DocumentException!", e);
            } catch (IOException e) {
                logger.error("createDayStaticSafeMain have IOException!", e);
            }*/
        }
    }
}
