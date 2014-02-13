package com.ailk.jt.task.dao;

import java.util.HashMap;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ailk.jt.task.entity.A4MainAcctLogonDays;

public class A4MainAcctLogonDaysDao extends SqlMapClientDaoSupport {

	public A4MainAcctLogonDaysDao() {
	}

	public java.util.List<A4MainAcctLogonDays> getLogOnDays(HashMap<String, String> parMap) {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctlogonDays.getLogOnDays", parMap);
	}
}
