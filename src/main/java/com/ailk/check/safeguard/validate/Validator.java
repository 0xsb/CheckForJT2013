package com.ailk.check.safeguard.validate;

import com.ailk.check.safeguard.validate.method.FilesIsExists;
import com.ailk.check.safeguard.validate.method.SumIsEqualsSeq;
import com.ailk.check.safeguard.validate.method.XmlIsValidated;
import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-31
 * Time: 下午5:14
 * <p/>
 * 文件验证抽象类
 * 将公用的方法定义在该类中
 */
public abstract class Validator {
    private static Logger logger = LoggerFactory.getLogger(Validator.class);

    private String errorFileName = "no error."; // 错误文件名称
    private String errorFilePath = "no error."; // 错误文件路径
    private String errorCode = "no error."; // 错误信息代码
    private int total = 0; // XML文件中的sum值

    public String getErrorFileName() {
        return errorFileName;
    }

    public String getErrorFilePath() {
        return errorFilePath;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getTotal() {
        return total;
    }

    /**
     * 检查文件是否存在
     *
     * @param paths 文件路径
     * @return 如果有一个不存在就返回false
     */
    protected boolean filesIsExists(String... paths) {
        boolean result;
        FilesIsExists filesIsExists = new FilesIsExists(paths).invoke();
        if (filesIsExists.isExists()) {
            result = true;
        } else {
            takeErrorResult(filesIsExists.getNotExistsFileName(), filesIsExists.getNotExistsFilePath(), "nofileError", 0);
            result = false;
        }
        return result;
    }

    /**
     * 验证XML文件是否符合Schema（xsd）
     *
     * @param xmlPathMaps key:xml全路径 value:xml所在目录 （主要是为FileValidator.validate传参数）
     * @return 如果有一个不符合就返回false
     */
    protected boolean xmlIsValidated(Map<String, String> xmlPathMaps) {
        boolean result;
        XmlIsValidated xmlIsValidated = new XmlIsValidated(xmlPathMaps).invoke();
        if (xmlIsValidated.isValidated()) {
            result = true;
        } else {
            takeErrorResult(xmlIsValidated.getNotValidatedFileName(), xmlIsValidated.getNotValidatedFilePath(), "validatorFailed", 0);
            result = false;
        }
        return result;
    }

    /**
     * 验证sum是否与seq总数相等
     *
     * @param xmlReaders xml读取
     * @return 如果有一个不相等就返回false
     */
    protected boolean sumIsEqualsSeq(XmlReader... xmlReaders) {
        boolean result;
        SumIsEqualsSeq sumIsEqualsSeq = new SumIsEqualsSeq(xmlReaders).invoke();
        if (sumIsEqualsSeq.isEquals()) {
            result = true;
        } else {
            takeErrorResult(sumIsEqualsSeq.getNotEqualsFileName(), sumIsEqualsSeq.getNotEqualsFilePath(), "sumNotEqualSeq", sumIsEqualsSeq.getNotEqualsFileSum());
            result = false;
        }
        return result;
    }

    /**
     * 设置要返回错误信息
     *
     * @param errorFileName 错误文件名称
     * @param errorFilePath 错误文件路径
     * @param errorCode     错误信息代码
     * @param total         错误XML中的sum
     */
    protected void takeErrorResult(String errorFileName, String errorFilePath, String errorCode, int total) {
        this.errorFileName = errorFileName;
        this.errorFilePath = errorFilePath;
        this.errorCode = errorCode;
        this.total = total;
    }

    /**
     * 验证文件
     *
     * @return 验证结果
     */
    public abstract boolean validate() throws Exception;
}
