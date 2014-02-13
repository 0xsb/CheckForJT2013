package com.ailk.check.safeguard;

import com.ailk.check.ConfigReader;
import com.ailk.check.FileType;
import com.ailk.check.safeguard.safe.JKDaySafeFileCreator;
import com.ailk.check.safeguard.validate.JKDayValidator;
import com.ailk.check.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-6
 * Time: 上午8:43
 * <p/>
 * 金库申请审批日文件保障
 */
public class JKDaySafeguard extends Safeguard {
    private static Logger logger = LoggerFactory.getLogger(JKDaySafeguard.class);

    private String pathSMJKR; // SMJKR文件路径
    private String pathSMJKA; // SMJKA文件路径
    private String dirUpload; // 上传目录

    private int year, month, day;

    /**
     * 传入的时间是文件中数据所对应的时间
     * 其中月从零开始不用加一
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public JKDaySafeguard(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        init();

        // 金库申请、金库审批文件验证
        this.validator = new JKDayValidator(year, month, day, pathSMJKR, pathSMJKA);
    }

    private void init() {
        this.pathSMJKR = FileType.SMJKR.generateXmlPath(FileType.SMJKR.getFileName(year, month, day, 0));
        this.pathSMJKA = FileType.SMJKA.generateXmlPath(FileType.SMJKA.getFileName(year, month, day, 0));
        this.dirUpload = ConfigReader.getUploadDir();
    }

    /**
     * 验证文件，如果有错误，使用安全文件替换，如果安全文件失败，返回fasle并告警
     *
     * @return 是否上传
     */
    public boolean safeguard() {
        try {
            // 如果验证失败
            if (!getValidator().validate()) {
                // 短信、错误入库
                logger.error("smjkr smjka validate error! path:" + getValidator().getErrorFilePath()
                        + " reason：" + getValidator().getErrorCode());
                // 发送错误短信插入数据库
                insertDBNotice(getValidator().getErrorFilePath(), getValidator().getErrorCode());
                // 错误信息插入数据库
                errorInsertDB(getValidator().getErrorFileName(), getValidator().getTotal(), getValidator().getErrorCode());

                try {
                    // 写在try中为发生异常时不影响安全文件生成执行（对于本程序的异常处理，需要进一步讨论修正）
                    // 自检验证失败的文件存放到指定目录
                    FileUtil.moveToDir(pathSMJKR, ConfigReader.getValidateErrorDir());
                    FileUtil.moveToDir(pathSMJKA, ConfigReader.getValidateErrorDir());
                } catch (IOException e) {
                    logger.error("JKDaySafeguard.safeguard() moveToDir have IOException ...", e);
                }

                // 进行随机安全文件替换
                JKDaySafeFileCreator jkDaySafeFileCreator = new JKDaySafeFileCreator(year, month, day);
                jkDaySafeFileCreator.createJKDaySafeFile();
                if (!jkDaySafeFileCreator.isCreateSuccess()) {
                    logger.error("金库申请、金库审批日增量文件生成随机安全文件失败！");
                    // 短信
                    insertDBNotice(pathSMJKR, "jkDaySafeFileCreateError");
                    return false;
                }
                if (!getValidator().validate()) {
                    logger.error("金库申请、金库审批日增量文件随机安全文件验证失败！");
                    // 短信
                    insertDBNotice(pathSMJKR, "jkDaySafeFileValidateError");
                    return false;
                }
                // 短信：替换成功
                insertDBNotice(pathSMJKR, "replaceSuccess");
                insertDBNotice(pathSMJKA, "replaceSuccess");
            }

            // 搬移金库日文件到上传目录
            uploadJKDayFile();
        } catch (Exception e) {
            // 短信
            insertDBNotice(pathSMJKR, "programeError");
            logger.error("JKDaySafeguard.safeguard() have Exception ...", e);
        }
        return true;
    }

    // 搬移金库日文件到上传目录
    private void uploadJKDayFile() throws IOException {
        FileUtil.moveToDir(pathSMJKR, dirUpload);
        FileUtil.moveToDir(pathSMJKA, dirUpload);
    }
}
