package com.ailk.check.main;

import com.ailk.check.safeguard.safe.DaySafeFileCreator;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import com.ailk.check.utils.FileUtil;
import com.ailk.jt.util.PropertiesUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;

/**
 * static safe file to BOMC
 */
public class createDayStaticSafeMain {
    private static Logger logger = LoggerFactory.getLogger(createDayStaticSafeMain.class);

    public static void main(String[] args) {
        logger.info("create day static safe file to BOMC ... ");

        String uploadPath = PropertiesUtil.getValue("uap_file_uapload");
        String osFlag = PropertiesUtil.getValue("os_flag");
        String daySafeFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");
        String createPath = PropertiesUtil.getValue("uap_file_uapload_for_init_static");
        String rightDateBegin = PropertiesUtil.getValue("right_Datebegin").trim();

        // 取当前日期
        Calendar date = Calendar.getInstance();
        // 设置日期为昨天
        date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) - 1);

        DaySafeFileCreator daySafeFileCreator = new DaySafeFileCreator(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

        try {
            logger.info("create SMMAI ...");
            String createPathSMMAI = createPath + osFlag + "SMMAI.xml";
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
            String createPathSMBHR = createPath + osFlag + "SMBHR.xml";
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
            String createPathSMDAR = createPath + osFlag + "SMDAR.xml";
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
            String createPathSM4AI = createPath + osFlag + "SM4AI.xml";
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
            String createPathSMAAI = createPath + osFlag + "SMAAI.xml";
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
            String createPathSM4AR = createPath + osFlag + "SM4AR.xml";
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
            String createPathSMAAR = createPath + osFlag + "SMAAR.xml";
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
            String createPathSMFWL = createPath + osFlag + "SMFWL.xml";
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
            String createPathSMFLO = createPath + osFlag + "SMFLO.xml";
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


        try {
            logger.info("create SMJKR ...");
            String createPathSMJKR = createPath + osFlag + "SMJKR.xml";
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
            String createPathSMJKA = createPath + osFlag + "SMJKA.xml";
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
        }
    }
}
