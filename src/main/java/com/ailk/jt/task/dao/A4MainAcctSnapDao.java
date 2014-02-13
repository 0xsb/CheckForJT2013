package com.ailk.jt.task.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ailk.jt.task.entity.A4MainAcctSnap;

public class A4MainAcctSnapDao extends SqlMapClientDaoSupport {

	public List<A4MainAcctSnap> getAllAcct() {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctSnap.getAllAcct");
	}

	public List<A4MainAcctSnap> getEqualAcctList() {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctSnap.getEqualAcctList");
	}

	public List<A4MainAcctSnap> getNotEqualAcctList() {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctSnap.getNotEqualAcctList");
	}

	public void insertSnap(HashMap<String, String> betweenHashMap) {
		this.getSqlMapClientTemplate().insert("A4MainAcctSnap.insertSnap", betweenHashMap);
	}

	public List<A4MainAcctSnap> selectMainAcctByTime(HashMap<String, String> timeMap) {
		return this.getSqlMapClientTemplate().queryForList("A4MainAcctSnap.ByTwoCreateTime", timeMap);
	}

	public A4MainAcctSnap selectMainAcctByName(HashMap<String, Object> parMap) {
		return (A4MainAcctSnap) this.getSqlMapClientTemplate().queryForObject("A4MainAcctSnap.selectMainAcctByName", parMap);
	}

	public void updateSnapAcct(A4MainAcctSnap snapAcct) {
		this.getSqlMapClientTemplate().update("A4MainAcctSnap.updateSnapAcct", snapAcct);
	}

}
