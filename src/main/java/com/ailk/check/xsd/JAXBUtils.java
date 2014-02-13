package com.ailk.check.xsd;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: WangPu
 * Date: 13-4-12
 * Time: 下午4:21
 */
public class JAXBUtils {
    private static Logger logger = LoggerFactory.getLogger(JAXBUtils.class);

    private JAXBUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 将XML反序列化为对象
     *
     * @param clazz 包路径（必须含有ObjectFactory）
     * @param xml   XML字符串
     * @return Object 对象
     * @throws javax.xml.bind.JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T unMarshal(Class<T> clazz, String xml) throws JAXBException {
        logger.debug("jaxb unMarshal xml to java object ...");
        StringReader reader = new StringReader(xml);
        return (T) createUnMarshaller(clazz).unmarshal(reader);
    }

    /**
     * 将对象序列化为XML文件（编码为UTF-8）
     *
     * @param clazz    包路径（必须含有ObjectFactory）
     * @param obj      对象
     * @return String XML
     * @throws javax.xml.bind.JAXBException
     */
    public static String marshal(Class clazz, Object obj) throws JAXBException {
        logger.debug("jaxb marshal java object to xml ...");
        StringWriter writer = new StringWriter();
        createMarshaller(clazz, "UTF-8").marshal(obj, writer);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + writer.toString();
    }

    protected static Marshaller createMarshaller(Class clazz, String encoding) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);//是否格式化生成的xml串
        if (StringUtils.isNotBlank(encoding)) {//判断encoding是否为空串
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);//设置编码
        }
        // 为了去掉jaxb自动生成的头部“standalone”，自定义了头部
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        return marshaller;
    }

    protected static Unmarshaller createUnMarshaller(Class clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        return jaxbContext.createUnmarshaller();
    }
}
