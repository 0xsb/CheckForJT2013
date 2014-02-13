package com.ailk.check.safeguard;

import com.ailk.check.safeguard.validate.Validator;
import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SaveErrorFileUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-7
 * Time: 上午11:15
 * <p/>
 * 保障抽象类
 * 内含短信公用方法、错误入库等
 */
public abstract class Safeguard {

    // 短信信息
    private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

    protected Validator validator; // 文件验证者

    public Validator getValidator() {
        return validator;
    }

    /**
     * 插入数据库发送短信
     *
     * @param errorFilePath 错误文件路径
     * @param errorCode     错误信息代码
     */
    protected void insertDBNotice(String errorFilePath, String errorCode) {
        DBUtil.notice(tran.getProperty("a4File") + errorFilePath + tran.getProperty(errorCode));
    }

    /**
     * 错误信息插入数据库
     *
     * @param fileName  错误文件名
     * @param total     XML文件中的sum值
     * @param errorCode 错误信息代码
     */
    protected void errorInsertDB(String fileName, int total, String errorCode) {
        //将错误信息插入表中
        HashMap<String, String> dateMap = new HashMap<String, String>();
        dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
        dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
        dateMap.put("file_name", fileName);
        dateMap.put("file_sum", String.valueOf(total));
        dateMap.put("file_error_reason", tran.getProperty(errorCode).trim().split("，")[0]); // todo 这个逗号是做什么的？
        dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
        SaveErrorFileUtil.saveErrorFile(dateMap);
    }

    /**
     * 验证文件，如果有错误，使用安全文件替换，如果安全文件失败，返回fasle并告警
     *
     * @return 是否上传
     */
    public abstract boolean safeguard();
}
