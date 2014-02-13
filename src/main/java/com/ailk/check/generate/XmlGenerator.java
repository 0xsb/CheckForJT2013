package com.ailk.check.generate;

import com.ailk.check.*;
import com.ailk.check.db.ConnectionFactory;
import com.ailk.check.db.ConnectionManager;
import com.ailk.check.generate.config.Configuration;
import com.ailk.check.utils.CalendarUtil;
import com.ailk.check.utils.XMLGregorianCalendarUtil;
import com.ailk.check.xsd.JAXBUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-4-15
 * Time: 下午5:18
 */
public class XmlGenerator {
    private static Logger logger = LoggerFactory.getLogger(XmlGenerator.class);

    public static final String HANDLER_PARAM_SEPARATOR = ":";

    public ConnectionManager connectionManager; // 数据库链接
    public int year, month, day, hour; // XML文件中的begintime数字化
    public String generatePath; // 生成XML路径

    private String xmlTypeName; // XML类型名称
    private String namespace; // 命名空间
    private Object smp; // 已知的Smp对象
    private Constructor dataConstructor; // 已知的Data对象构造器
    private Constructor rcdConstructor; // 已知的Rcd对象构造器

    private ConnectionFactory.Type dbType; // 数据库类型
    private String sql; // 要执行的sql

    private Map<String, Configuration.Db.Mapper.Element> elementMap;

    /**
     * XmlGenerator构造方法
     * 传入的时间是文件中的begintime
     * 其中月从零开始不用加一
     *
     * @param configuration XML映射数据库查询列配置
     * @param year          年
     * @param month         月
     * @param day           日
     * @param hour          小时
     */
    public XmlGenerator(Configuration configuration, int year, int month, int day, int hour)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        logger.debug("generate xml type is : " + configuration.getName());
        this.xmlTypeName = configuration.getName();
        logger.debug("generate xml use db is : " + configuration.getDb().getType());
        this.dbType = ConnectionFactory.Type.valueOf(configuration.getDb().getType());
        logger.debug("generate xml use sql is : " + configuration.getDb().getSql());
        this.sql = configuration.getDb().getSql();
        logger.debug("generate xml use namespace is : " + configuration.getDb().getMapper().getNamespace());
        this.namespace = configuration.getDb().getMapper().getNamespace();
        this.elementMap = configuration.getDb().getMapper().getElementMap();

        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;

        // 初始化
        init();
    }

    private void init()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        logger.debug("init XmlGenerator param ...");
        // 反射创建已知的Smp对象
        Class smpClass = Class.forName(namespace);
        this.smp = smpClass.newInstance();

        // 获取构造器
        Class smpClasses[] = smpClass.getDeclaredClasses();
        for (Class c : smpClasses) {
            if (c.getSimpleName().equals("Data")) {
                this.dataConstructor = c.getConstructor();
                Class dataClasses[] = c.getDeclaredClasses();
                for (Class aClass : dataClasses) {
                    if (aClass.getSimpleName().equals("Rcd")) {
                        this.rcdConstructor = aClass.getConstructor();
                    }
                }
            }
        }

        // 根据数据库类型获取连接管理器
        this.connectionManager = ConnectionFactory.getConnectionManagerByType(dbType);

        // 根据开始时间获取文件生成路径
        FileType fileType = FileType.valueOf(xmlTypeName);
        this.generatePath = fileType.generateXmlPath(fileType.getFileName(year, month, day, hour));
        logger.debug("generate xml path is : " + generatePath);
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public File generateXml() throws SQLException, IllegalAccessException, InvocationTargetException,
            InstantiationException, ClassNotFoundException, NoSuchMethodException,
            IOException, JAXBException, DatatypeConfigurationException {
        // 从数据库获取值，设置到对象中
        List<Object> rcdList = new ArrayList<Object>();
        ResultSet rs = null;
        PreparedStatement stm = null;
        int sum;
        logger.debug("query from db ...");
        try {
            stm = connectionManager.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery();
            rs.last();
            // 获取总数
            sum = rs.getRow();
            logger.debug("query result total is : " + sum);
            rs.beforeFirst();
            while (rs.next()) {
                Object f = rcdConstructor.newInstance();
                setDbValueToRcd(rs, f);
                rcdList.add(f);
            }
        } finally {
            logger.debug("release statement, ResultSet and close connection ...");
            connectionManager.release(rs, stm);
            connectionManager.close();
        }

        // 生成Smp对象
        Object data = generateData(rcdList);
        generateSmp(data, sum);

        // 通过反射生成的对象，生成XML
        File xmlFile = new File(generatePath);
        FileUtils.touch(xmlFile);

        Class clazz = Class.forName(namespace);
        String xmlString = JAXBUtils.marshal(clazz, smp);

        // XML写入文件
        FileUtils.writeStringToFile(xmlFile, xmlString, "UTF-8");

        return xmlFile;
    }

    // todo 该方法需要修改，考虑从配置文件获取
    private Map<String, Object> getSmpXmlHeadMap(int sum) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", xmlTypeName);
        map.put("province", ConfigReader.getProvCode());
        map.put("createtime", new Date());
        map.put("begintime", CalendarUtil.getCalendarFromNumber(year, month, day, hour).getTime());
        map.put("endtime", FileType.valueOf(xmlTypeName).getEndTime(year, month, day, hour));
        map.put("sum", String.valueOf(sum));
        return map;
    }

    private void generateSmp(Object data, int sum) throws IllegalAccessException, DatatypeConfigurationException {
        logger.debug("generate smp ...");
        // 获取f对象对应类中的所有属性域
        Field[] fields = smp.getClass().getDeclaredFields();
        Map<String, Object> map = getSmpXmlHeadMap(sum);
        for (Field field : fields) {
            // 对于每个属性，获取属性名
            String varName = field.getName();
            // 获取原来的访问控制权限
            boolean accessFlag = field.isAccessible();
            // 修改访问控制权限
            field.setAccessible(true);
            // 获取属性域类型
            Type fieldType = field.getGenericType();
            if (String.class.equals(fieldType)) {
                Object value = map.get(varName);
                field.set(smp, value);
            } else if (BigInteger.class.equals(fieldType)) {
                String value = (String) map.get(varName);
                field.set(smp, value == null ? null : BigInteger.valueOf(Integer.valueOf(value)));
            } else if (XMLGregorianCalendar.class.equals(fieldType)) {
                Date value = (Date) map.get(varName);
                field.set(smp, XMLGregorianCalendarUtil.dateToXml(value));
            } else if (data.getClass().equals(fieldType)) {
                field.set(smp, data);
            }
            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
    }

    private Object generateData(List<Object> rcdList) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        logger.debug("generate data ...");
        Object data = dataConstructor.newInstance();
        Field dataField = data.getClass().getDeclaredFields()[0];
        // 获取原来的访问控制权限
        boolean accessFlag = dataField.isAccessible();
        // 修改访问控制权限
        dataField.setAccessible(true);
        dataField.set(data, rcdList);
        // 恢复访问控制权限
        dataField.setAccessible(accessFlag);
        return data;
    }

    private void setDbValueToRcd(ResultSet rs, Object f)
            throws IllegalAccessException, InvocationTargetException, InstantiationException,
            SQLException, NoSuchMethodException, ClassNotFoundException, DatatypeConfigurationException {
        logger.debug("set db value to rcd ...");
        // 获取f对象对应类中的所有属性域
        Field[] fields = f.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 对于每个属性，获取属性名
            String varName = field.getName();
            if (!elementMap.containsKey(varName))
                continue;
            if (elementMap.get(varName).getColumn() == null) {
                setValueToRcdByHandler(rs, f, field, varName);
                continue;
            }
            // 获取原来的访问控制权限
            boolean accessFlag = field.isAccessible();
            // 修改访问控制权限
            field.setAccessible(true);
            // 获取对应的数据库列名
            String column = elementMap.get(varName).getColumn();
            // 获取属性域类型
            Type fieldType = field.getGenericType();
            logger.debug("fieldType is : " + fieldType + "; fieldName is : " + varName);
            if (String.class.equals(fieldType)) {
                String value = rs.getString(column);
                field.set(f, value);
            } else if (BigInteger.class.equals(fieldType)) {
                Integer value = rs.getInt(column);
                field.set(f, value == null ? null : BigInteger.valueOf(value));
            } else if (XMLGregorianCalendar.class.equals(fieldType)) {
                Timestamp value = rs.getTimestamp(column);
                field.set(f, XMLGregorianCalendarUtil.dateToXml(value));
            } else if (BigDecimal.class.equals(fieldType)) {
                BigDecimal value = rs.getBigDecimal(column);
                field.set(f, value == null ? null : value);
            }
            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
    }

    private void setValueToRcdByHandler(ResultSet rs, Object f, Field field, String varName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, SQLException, InvocationTargetException {
        logger.debug("set value to rcd by handler, fieldName is : " + varName);
        // 没有定义对应的列名，需要检查是否Handler处理
        String handlerPath = elementMap.get(varName).getHandler();
        String methodName = elementMap.get(varName).getMethod().getName();
        Class handlerClass = Class.forName(handlerPath);
        Object handler = handlerClass.newInstance();

        int size = elementMap.get(varName).getMethod().getParams().size();
        Class[] plusPara = new Class[size];
        for (int i = 0; i < size; i++) {
            plusPara[i] = String.class;
        }
        Method method = handlerClass.getDeclaredMethod(methodName, plusPara);
        Object[] params = new Object[size];
        int i = 0;
        for (Configuration.Db.Mapper.Element.Method.Param param : elementMap.get(varName).getMethod().getParams()) {
            String value = param.getValue().substring(param.getValue().indexOf(HANDLER_PARAM_SEPARATOR) + 1);
            params[i++] = rs.getString(value);
        }
        String result = (String) method.invoke(handler, params);
        // 获取原来的访问控制权限
        boolean accessFlag = field.isAccessible();
        // 修改访问控制权限
        field.setAccessible(true);
        field.set(f, result);
        // 恢复访问控制权限
        field.setAccessible(accessFlag);
    }
}
