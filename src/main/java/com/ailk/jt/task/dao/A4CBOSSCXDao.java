package com.ailk.jt.task.dao;

import com.ailk.jt.task.entity.A4CBOSSCX;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class A4CBOSSCXDao extends SqlMapClientDaoSupport
{
  private static final Logger log = Logger.getLogger(A4CBOSSCXDao.class);

  public void saveOrUpdateAcctList(List<A4CBOSSCX> list)
  {
    for (int i = 0; i < list.size(); i++) {
      A4CBOSSCX temp = (A4CBOSSCX)list.get(i);
      getSqlMapClientTemplate().insert("A4CBOSSCX.insertAcct", temp);
    }
  }
}