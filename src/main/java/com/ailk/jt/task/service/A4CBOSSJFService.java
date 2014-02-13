package com.ailk.jt.task.service;

import java.util.List;

import com.ailk.jt.task.dao.A4CBOSSJFDao;
import com.ailk.jt.task.entity.A4CBOSSJF;


public class A4CBOSSJFService
{
  private A4CBOSSJFDao a4CBOSSJFDao;

  public void saveOrUpdateAcctList(List<A4CBOSSJF> list)
  {
    this.a4CBOSSJFDao.saveOrUpdateAcctList(list);
  }

  public A4CBOSSJFDao getA4CBOSSJFDao()
  {
    return this.a4CBOSSJFDao;
  }

  public void setA4CBOSSJFDao(A4CBOSSJFDao dao) {
    this.a4CBOSSJFDao = dao;
  }
}