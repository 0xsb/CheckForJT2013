package com.ailk.jt.task.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ailk.jt.task.entity.A4CBOSSJF;

public class A4CBOSSJFDao extends SqlMapClientDaoSupport {

	private static final Logger log = Logger.getLogger(A4CBOSSDao.class);// 获取日志打印对象

	public void saveOrUpdateAcctList(List<A4CBOSSJF> list) {
		for (int i = 0; i < list.size(); i++) {
			A4CBOSSJF temp = list.get(i);
			this.getSqlMapClientTemplate().insert("A4CBOSSJF.insertAcct", temp);
		}
	}

}
