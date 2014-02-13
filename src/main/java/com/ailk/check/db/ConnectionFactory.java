package com.ailk.check.db;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-4-15
 * Time: 上午11:48
 * <p/>
 * 数据连接工厂类
 */
public class ConnectionFactory {
    // 单例工厂类对象
    private static ConnectionManager aiuap20ConnectionManager = null;
    private static ConnectionManager auditConnectionManager = null;

    /**
     * 私有构造器
     */
    private ConnectionFactory() {
    }

    /**
     * 数据库类型
     */
    public enum Type {
        aiuap20, audit
    }

    /**
     * 获取aiuap连接管理器对象
     * // todo 为复用就代码的折中办法，以后改进
     *
     * @param type 数据库类型
     * @return 连接管理器对象
     */
    public static ConnectionManager getConnectionManagerByType(Type type) {
        switch (type) {
            case aiuap20:
                if (aiuap20ConnectionManager == null) {
                    aiuap20ConnectionManager = new ConnectionManager(Type.aiuap20);
                }
                break;
            case audit:
                if (auditConnectionManager == null) {
                    auditConnectionManager = new ConnectionManager(Type.audit);
                }
                break;
        }
        return aiuap20ConnectionManager;
    }

    /**
     * 为测试初始化连接环境
     */
    public static void initializeForTesting(String driverClassName, String url, String username, String password) {
        if (aiuap20ConnectionManager == null) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            aiuap20ConnectionManager = new ConnectionManager(dataSource);
        }
    }
}
