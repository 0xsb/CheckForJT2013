package com.ailk.jt.task.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ailk.jt.task.entity.A4MainAcctJt;

public class A4MainAcctJtDao extends SqlMapClientDaoSupport {

	public A4MainAcctJtDao() {
	}

	public List<A4MainAcctJt> getMainAcctJTByName(HashMap<String, Object> parMap) {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctJt.ByLoginName", parMap);
	}

	public void delMainAcctJT(A4MainAcctJt copy) {
		this.getSqlMapClientTemplate().update("A4MainAcctJt.deleteAcctFromJT", copy);
	}

	public void updateMainAcctJT(A4MainAcctJt copy) {
		this.getSqlMapClientTemplate().update("A4MainAcctJt.updateAcctFroJT", copy);
	}

	public void insertMainAcctJT(A4MainAcctJt copy) {
		this.getSqlMapClientTemplate().update("A4MainAcctJt.insertAcctToJT", copy);

	}

	public int getLogOnDays(HashMap<String, String> parMap) {
		Object loginDays = this.getSqlMapClientTemplate().queryForObject("A4MainAcctJt.getLogOnDays", parMap);
		System.out.println(Integer.valueOf(loginDays.toString()));
		return Integer.valueOf(loginDays.toString());
	}

	public List<A4MainAcctJt> getAllAcct() {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctJt.getAllAcct");
	}

	public int getInvalidCount() {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctJt.getInvalidCount").size();
	}

	public int getHZAllCount() {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctJt.getHZAllCount").size();
	}

}
