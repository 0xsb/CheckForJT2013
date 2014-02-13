package com.ailk.check.safeguard.safe;

import com.ailk.check.safeguard.validate.xml.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-4
 * Time: 上午8:54
 * <p/>
 * 安全文件创建者
 * 注意：
 * 1、调用时请使用createSafeFile获取所需安全文件
 * 2、然后使用isCreateSuccess查看是否创建成功，
 * 返回false是创建失败，XML文件不可用，请进行其它处理
 */
public abstract class SafeFileCreator {
    private static Logger logger = LoggerFactory.getLogger(SafeFileCreator.class);

    public final static String XML_REPLACE_KEY = "toReplace"; // 安全文件中需要替换的地方的关键字

    private boolean createSuccess; // 是否创建成功

    public boolean isCreateSuccess() {
        return createSuccess;
    }

    // 设置是否创建成功
    protected void setCreateSuccess(boolean createSuccess) {
        this.createSuccess = createSuccess;
    }

    /**
     * 替换XML指定路径的值
     * 替换关键字为：XML_REPLACE_KEY
     *
     * @param xmlReader XML
     * @param text      替换为的值
     * @param xpaths    xml路径
     */
    protected void replace(XmlReader xmlReader, String text, String... xpaths) {
        for (String xpath : xpaths) {
            xmlReader.replace(XML_REPLACE_KEY, text, xpath);
        }
    }

    /**
     * 替换XML指定路径的值
     * 替换关键字为：传入参数 key
     *
     * @param xmlReader XML
     * @param key       替换关键字
     * @param text      替换为的值
     * @param xpaths    xml路径
     */
    protected void replace(XmlReader xmlReader, String key, String text, String... xpaths) {
        for (String xpath : xpaths) {
            xmlReader.replace(key, text, xpath);
        }
    }

    /**
     * 创建所需安全文件
     *
     * @param createPath 创建文件的路径
     * @param xmlReader  XML
     * @param text       替换为的值
     * @param xpaths     xml路径
     */
    public abstract void createSafeFile(String createPath, XmlReader xmlReader, String text, String... xpaths);
}
