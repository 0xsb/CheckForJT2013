package com.ailk.jt.task.service;

import java.util.List;

import com.ailk.jt.task.dao.A4CBOSSDao;
import com.ailk.jt.task.entity.A4CBOSS;

public class A4CBOSSService {
	private A4CBOSSDao a4CBOSSDao;

	public void saveOrUpdateAcctList(List<A4CBOSS> list) {
		a4CBOSSDao.saveOrUpdateAcctList(list);
	}
}
