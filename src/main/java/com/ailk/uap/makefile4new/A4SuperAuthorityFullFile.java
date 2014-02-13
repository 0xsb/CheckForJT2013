package com.ailk.uap.makefile4new;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.fileupload.util.Streams;

import com.ailk.uap.makefile4new.sql.A4ResourceCoverFullFileSQL;
import com.ailk.uap.util.FileProUtil;
/**
 * 前台具备超级权限人员统计月全量文件
 * type=SMSMF
 *
 */
public class A4SuperAuthorityFullFile  extends AbstractMakeMonthFile implements A4ResourceCoverFullFileSQL {

	public static void main(String[] args) {
		AbstractMakeFile abstractMakeFile = new A4SuperAuthorityFullFile();
		abstractMakeFile.makeFile();
	}

	public static void generateA4ResourceCoverFullFile() throws Exception {
		StringBuffer a4ResourceCoverFileBuffer = new StringBuffer();
		a4ResourceCoverFileBuffer.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");

		a4ResourceCoverFileBuffer.append("<smp>\r\n");
		a4ResourceCoverFileBuffer.append("<type>SMSMF</type>\r\n");
		a4ResourceCoverFileBuffer.append("<province>" + prov_code + "</province>" + "\r\n");
		a4ResourceCoverFileBuffer.append("<createtime>" + createTime + "</createtime>" + "\r\n");

		sum = 0L;
		log.info("total sum ===" + sum);
		a4ResourceCoverFileBuffer.append("<sum>" + sum + "</sum>" + "\r\n");
		a4ResourceCoverFileBuffer.append("<begintime>" + beginTimeWithT + "</begintime>" + "\r\n");
		a4ResourceCoverFileBuffer.append("<endtime>" + endTimeWithT + "</endtime>" + "\r\n");
		a4ResourceCoverFileBuffer.append("<data>");
		 uapLoadTempFile = new File(uap_file_uapload_temp + File.separator + uploadFileName);
		FileProUtil.createDir(uap_file_uapload_temp + File.separator);
		FileProUtil.createFile(uapLoadTempFile.getPath());
		fos = new FileOutputStream(uapLoadTempFile, true);
		output = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(output);
		log.info("getA4ResorceCoverFullInfo*******************Start******");
		log.info("getA4ResorceCoverFullInfo*******************End*********");
		a4ResourceCoverFileBuffer.append("</data>\r\n");
		a4ResourceCoverFileBuffer.append("</smp>");
		log.info("a4ResourceCoverFileBuffer ==== " + a4ResourceCoverFileBuffer.toString());

		writeFileBufferToTempFile(a4ResourceCoverFileBuffer);

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(uapLoadTempFile));
		File uapLoadFile = new File(AbstractMakeMonthFile.uap_file_uapload + File.separator + uploadFileName);
		if (!uapLoadFile.exists()) {
			uapLoadFile.createNewFile();
		}
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uapLoadFile));
		Streams.copy(in, out, true);
		in.close();
		out.close();

		if (bw != null) {
			bw.close();
		}
		if (output != null) {
			output.close();
		}
		if (fos != null) {
			fos.close();
		}

		uapLoadTempFile.delete();
	}
	
	public  String getFileType(){
		return super.TYPE_SMSMF;
	}
	
	@Override
	public void generateMakeFile() throws Exception {
		generateA4ResourceCoverFullFile();
	}
	
}