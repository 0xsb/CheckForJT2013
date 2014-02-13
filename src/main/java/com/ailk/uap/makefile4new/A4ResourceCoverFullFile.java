package com.ailk.uap.makefile4new;

import com.ailk.uap.dbconn.ConnectionManager;
import com.ailk.uap.makefile4new.sql.A4ResourceCoverFullFileSQL;
import com.ailk.uap.util.CommonUtil;
import com.ailk.uap.util.FileProUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.fileupload.util.Streams;

public class A4ResourceCoverFullFile extends AbstractMakeMonthFile implements A4ResourceCoverFullFileSQL
{

  public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new A4ResourceCoverFullFile();
		abstractMakeFile.makeFile();
  }

  public static void generateA4ResourceCoverFullFile() throws Exception {
    StringBuffer a4ResourceCoverFileBuffer = new StringBuffer();
    a4ResourceCoverFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
    a4ResourceCoverFileBuffer.append("<smp>\r\n");
    a4ResourceCoverFileBuffer.append("<type>SMCRF</type>\r\n");
    a4ResourceCoverFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
    a4ResourceCoverFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

    String truncatea4crfsmp = "truncate table a4_crf_smp";
    log.info("truncatea4crfsmp======" + truncatea4crfsmp);
    PreparedStatement prepStmttruncate = conn.prepareStatement(truncatea4crfsmp);
    ResultSet truncaters = prepStmttruncate.executeQuery();
    ConnectionManager.closeResultSet(truncaters);
    ConnectionManager.closePrepStmt(prepStmttruncate);

    StringBuilder insertA4RcFullFileSql = new StringBuilder();
    insertA4RcFullFileSql.append("insert into a4_crf_smp (");
    insertA4RcFullFileSql.append(A4_RESOURCE_COVER_FULL_SQL);
    insertA4RcFullFileSql.append("WHERE EFFECTTIME <");
    insertA4RcFullFileSql.append("TO_DATE('");
    insertA4RcFullFileSql.append(endTime);
    insertA4RcFullFileSql.append("', 'yyyy-MM-dd HH24:mi:ss')");
    insertA4RcFullFileSql.append(")");

    log.info("insertA4RcFullFileSql===" + insertA4RcFullFileSql.toString());

    PreparedStatement insertStmttruncate = conn.prepareStatement(insertA4RcFullFileSql.toString());
    ResultSet insertRs = insertStmttruncate.executeQuery();
    ConnectionManager.closeResultSet(insertRs);
    ConnectionManager.closePrepStmt(insertStmttruncate);

    String A4RcFullFileSql = "select *   from a4_crf_smp t  where (t.restype = '04' and        t.resaddress in (select v.RESADDRESS from a4_firewall_inner_smp_view v))     or t.restype <> '04'";

    log.info("A4RcFullFileSql=====" + A4RcFullFileSql);

    uapLoadTempFile = new File(uap_file_uapload_temp + File.separator + uploadFileName);
    FileProUtil.createDir(uap_file_uapload_temp + File.separator);
    FileProUtil.createFile(uapLoadTempFile.getPath());
    fos = new FileOutputStream(uapLoadTempFile, true);
    output = new OutputStreamWriter(fos, "UTF-8");
    bw = new BufferedWriter(output);
    log.info("getA4ResorceCoverFullInfo*******************Start******");
    getA4ResorceCoverFullInfo(A4RcFullFileSql, a4ResourceCoverFileBuffer, conn);
    log.info("getA4ResorceCoverFullInfo*******************End*********");
    a4ResourceCoverFileBuffer.append("</data>\r\n");
    a4ResourceCoverFileBuffer.append("</smp>");

    writeFileBufferToTempFile(a4ResourceCoverFileBuffer);

    BufferedInputStream in = new BufferedInputStream(new FileInputStream(uapLoadTempFile));
    File uapLoadFile = new File(uap_file_uapload + File.separator + uploadFileName);
    if (!(uapLoadFile.exists()))
      uapLoadFile.createNewFile();

    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uapLoadFile));
    Streams.copy(in, out, true);
    in.close();
    out.close();

    if (bw != null)
      bw.close();

    if (output != null)
      output.close();

    if (fos != null) {
      fos.close();
    }

    uapLoadTempFile.delete();
  }


  private static void getA4ResorceCoverFullInfo(String A4RcFullFileSql, StringBuffer a4ResourceCoverFileBuffer, Connection conn) throws Exception
  {
    PreparedStatement prepStmt = conn.prepareStatement(A4RcFullFileSql, 1004, 1007);

    ResultSet rs = prepStmt.executeQuery();
    rs.last();
    sum = rs.getRow();
    log.info("sum===" + sum);
    a4ResourceCoverFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
    a4ResourceCoverFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
    a4ResourceCoverFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
    a4ResourceCoverFileBuffer.append("<data>");
    rs.beforeFirst();
    int i = 0;
    while (rs.next()) {
      ++i;
      if (i % 1000 == 0) {
        log.info("writeA4ResourceCoverFileBufferToTempFile******start***");
        writeFileBufferToTempFile(a4ResourceCoverFileBuffer);
        log.info("writeA4ResourceCoverFileBufferToTempFile******end***");
        a4ResourceCoverFileBuffer.setLength(0);
      }

      a4ResourceCoverFileBuffer.append("<rcd>\r\n");
      a4ResourceCoverFileBuffer.append("<seq>" + String.valueOf(i) + "</seq>" + "\r\n");
      a4ResourceCoverFileBuffer.append("<restype>" + String.valueOf(rs.getString("restype")) + "</restype>" + "\r\n");

      a4ResourceCoverFileBuffer.append("<resname>" + CommonUtil.getStr(rs.getString("resname")) + "</resname>" + "\r\n");

      a4ResourceCoverFileBuffer.append("<resaddress>" + CommonUtil.getStr(rs.getString("resaddress")) + "</resaddress>" + "\r\n");

      a4ResourceCoverFileBuffer.append("<effecttime>" + CommonUtil.getTime(rs.getTimestamp("effecttime")) + "</effecttime>" + "\r\n");

      a4ResourceCoverFileBuffer.append("</rcd>\r\n");
    }
    log.info("==========" + i);
    ConnectionManager.closeResultSet(rs);
    ConnectionManager.closePrepStmt(prepStmt);
  }

@Override
public void generateMakeFile() throws Exception {
	generateA4ResourceCoverFullFile();
}

@Override
public String getFileType() {
	return super.TYPE_SMCRF;
}
  
}