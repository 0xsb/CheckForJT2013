package com.ailk.check.safeguard.validate.method;

import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-3
 * Time: 上午10:01
 *
 * 检查XML中sum与seq是否相等
 * 如果有一个XML有问题就返回false
 */
public class SumIsEqualsSeq {
    private static Logger logger = LoggerFactory.getLogger(SumIsEqualsSeq.class);

    private XmlReader[] xmlReaders;
    private boolean equals = true;
    private String notEqualsFilePath;
    private String notEqualsFileName;
    private int notEqualsFileSum;

    public SumIsEqualsSeq(XmlReader... xmlReaders) {
        this.xmlReaders = xmlReaders;
    }

    public boolean isEquals() {
        return equals;
    }

    public String getNotEqualsFileName() {
        return notEqualsFileName;
    }

    public String getNotEqualsFilePath() {
        return notEqualsFilePath;
    }

    public int getNotEqualsFileSum() {
        return notEqualsFileSum;
    }

    public SumIsEqualsSeq invoke() {
        for (XmlReader xmlReader : xmlReaders) {
            if (xmlReader.getSeqCountVal() != xmlReader.getSumVal()) {
                logger.error("xml sum not equals seq error, xml is : " + xmlReader.getXmlPath());
                this.equals = false;
                this.notEqualsFilePath = xmlReader.getXmlPath();
                this.notEqualsFileName = xmlReader.getXmlName();
                this.notEqualsFileSum = xmlReader.getSumVal();
                break;
            }
        }
        return this;
    }
}
