package com.ailk.check.xsd.smmaf.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-20
 * Time: 下午3:24
 *
 * SMMAF Handler
 */
public class SmmafHandler {
    private static Logger logger = LoggerFactory.getLogger(SmmafHandler.class);

    public String getMainAcctRoleListInfo(String id) {
        logger.debug("Class[SmmafHandler] Method[getMainAcctRoleListInfo] invoke. param is : " + id);
        return "test1Role,test2Role";
    }
}
