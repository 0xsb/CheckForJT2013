package com.ailk.jt.task.service;

import com.ailk.jt.task.dao.A4CBOSSCXDao;
import com.ailk.jt.task.entity.A4CBOSSCX;
import java.util.List;


public class A4CBOSSCXService
{
  private A4CBOSSCXDao a4CBOSSCXDao;

  public void saveOrUpdateAcctList(List<A4CBOSSCX> list)
  {
    this.a4CBOSSCXDao.saveOrUpdateAcctList(list);
  }

  public A4CBOSSCXDao getA4CBOSSCXDao() {
    return this.a4CBOSSCXDao;
  }

  public void setA4CBOSSCXDao(A4CBOSSCXDao dao) {
    this.a4CBOSSCXDao = dao;
  }
}