package com.ailk.jt.task.service;

import java.util.HashMap;
import java.util.List;

import com.ailk.jt.task.dao.A4MainAcctSnapDao;
import com.ailk.jt.task.entity.A4MainAcctSnap;

public class A4MainAcctSnapService
{
  private A4MainAcctSnapDao a4MainAcctSnapDao;

  public List<A4MainAcctSnap> getAllAcct()
  {
    return this.a4MainAcctSnapDao.getAllAcct();
  }

  public List<A4MainAcctSnap> getEqualAcctList() {
    return this.a4MainAcctSnapDao.getEqualAcctList();
  }

  public List<A4MainAcctSnap> getNotEqualAcctList() {
    return this.a4MainAcctSnapDao.getNotEqualAcctList();
  }

  public void insertSnap(HashMap<String, String> betweenHashMap) {
    this.a4MainAcctSnapDao.insertSnap(betweenHashMap);
  }

  public List<A4MainAcctSnap> selectMainAcctByTime(HashMap<String, String> timeMap) {
    return this.a4MainAcctSnapDao.selectMainAcctByTime(timeMap);
  }

  public A4MainAcctSnapDao getA4MainAcctSnapDao() {
    return this.a4MainAcctSnapDao;
  }

  public void setA4MainAcctSnapDao(A4MainAcctSnapDao mainAcctSnapDao) {
    this.a4MainAcctSnapDao = mainAcctSnapDao;
  }

  public A4MainAcctSnap selectMainAcctByName(HashMap<String, Object> parMap)
  {
    return this.a4MainAcctSnapDao.selectMainAcctByName(parMap);
  }

  public void updateSnapAcct(A4MainAcctSnap snapAcct) {
    this.a4MainAcctSnapDao.updateSnapAcct(snapAcct);
  }
}