package com.ailk.check.safeguard.validate.method;

import com.ailk.jt.validate.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-3
 * Time: 上午8:52
 *
 * 验证XML文件是否符合Schema（xsd）
 * 如果有一个不符合就返回false
 */
public class XmlIsValidated {
    private static Logger logger = LoggerFactory.getLogger(XmlIsValidated.class);

    private Map<String, String> xmlPathMaps;
    private boolean validated = true;
    private String notValidatedFileName;
    private String notValidatedFilePath;

    public XmlIsValidated(Map<String, String> xmlPathMaps) {
        this.xmlPathMaps = xmlPathMaps;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getNotValidatedFileName() {
        return notValidatedFileName;
    }

    public String getNotValidatedFilePath() {
        return notValidatedFilePath;
    }

    public XmlIsValidated invoke() {
        for (Map.Entry<String, String> xmlPathMap : xmlPathMaps.entrySet()) {
            String xmlPath = xmlPathMap.getKey();
            String dirPath = xmlPathMap.getValue();
            boolean result = FileValidator.validate(xmlPath, dirPath);
            if (!result) {
                logger.error("xml validate error, xml is : " + xmlPath);
                this.validated = false;
                this.notValidatedFileName = new File(xmlPath).getName();
                this.notValidatedFilePath = xmlPath;
                break;
            }
        }
        return this;
    }
}
