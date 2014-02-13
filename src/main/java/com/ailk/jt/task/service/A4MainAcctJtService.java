package com.ailk.jt.task.service;

import java.util.HashMap;
import java.util.List;

import com.ailk.jt.task.dao.A4MainAcctJtDao;
import com.ailk.jt.task.entity.A4MainAcctJt;

public class A4MainAcctJtService
{
  private A4MainAcctJtDao a4MainAcctJtDao;

  public List<A4MainAcctJt> getMainAcctJTByName(HashMap<String, Object> parMap)
  {
    return this.a4MainAcctJtDao.getMainAcctJTByName(parMap);
  }
  public List<A4MainAcctJt> getAllAcct() {
    return this.a4MainAcctJtDao.getAllAcct();
  }

  public void delMainAcctJT(A4MainAcctJt copy) {
    this.a4MainAcctJtDao.delMainAcctJT(copy);
  }

  public void updateMainAcctJT(A4MainAcctJt copy) {
    this.a4MainAcctJtDao.updateMainAcctJT(copy);
  }

  public void insertMainAcctJT(A4MainAcctJt copy)
  {
    this.a4MainAcctJtDao.insertMainAcctJT(copy);
  }

  public int getLogOnDays(HashMap<String, String> parMap) {
    return this.a4MainAcctJtDao.getLogOnDays(parMap);
  }

  public A4MainAcctJtDao getA4MainAcctJtDao() {
    return this.a4MainAcctJtDao;
  }

  public void setA4MainAcctJtDao(A4MainAcctJtDao mainAcctJtDao) {
    this.a4MainAcctJtDao = mainAcctJtDao;
  }
  public int getInvalidCount() {
    return this.a4MainAcctJtDao.getInvalidCount();
  }
  public int getHZAllCount() {
    return this.a4MainAcctJtDao.getHZAllCount();
  }
}
