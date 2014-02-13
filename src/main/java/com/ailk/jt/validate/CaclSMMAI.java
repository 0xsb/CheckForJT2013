package com.ailk.jt.validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.thoughtworks.xstream.XStream;

//
//create table a4_smmai_for_jk 
//( 
//beginTime timestamp(6) , 
//endTime   timestamp(6) ,
//acctmode  varchar(3),   
//mainacctid   varchar2(20),   
//loginname   varchar2(64),   
//valid   varchar2(20),   
//lockstatus   varchar2(20),   
//accttype   varchar2(20) ,
//effecttime   timestamp(6) ,
//expiretime   timestamp(6) ,
//establishtime    timestamp(6) ,
//updatetime   timestamp(6) 
//)
/**
 * @ClassName: CaclSMMAI
 * @Description:将SMMAI.xml文件中的数据导入到数据库表中
 * @author huangpumm@asiainfo-linkage.com
 * @date Feb 25, 2013 3:18:02 PM
 */
public class CaclSMMAI {
	private final static Logger log = Logger.getLogger(CaclSMMAI.class);
	private static String fileFoder = PropertiesUtil.getValue("uap_file_uapload_for_smmai_db_now");
	// 1、获取当前时间
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * @Title: main
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param args
	 *            设定文件
	 * @return void 返回类型
	 */

	public static void main(String[] args) {
		Long beginTime = System.currentTimeMillis();
		try {
			// log.debug(" run CalcSMJKR......,current time is:" + beginTime);
			CaclSMMAI cs = new CaclSMMAI();

			// 1、从文件保存目录里面查找文件,并保存到文件集合中
			Collection<File> fileCollection = FileUtils.listFiles(new File(fileFoder), new String[] { "xml" }, false);
			// log.debug("fileCollection.size()=" + fileCollection.size());

			if (fileCollection.size() <= 0) {// 在目录中未找到任何xml文件
				log.debug(" not found  SMMAI file !!");
			} else {
				for (File file : fileCollection) {// 循环遍历文件集合中的文件
					if (file.getName().contains("SMMAI")) {// 目前只计算SMMAI文件数值
						log.debug(" SMMAI file path is:" + file.getAbsolutePath());
						SMMAIMateData smmaiMataData = cs.genarateBean(file);
						List<SMMAIBean> smmaiBeanList = smmaiMataData.getData();// 已经将xml文件数据拿到
						saveResult(smmaiBeanList, smmaiMataData);// 将xml文件中的数据导入到数据库中
					}
				}
			}
			Long endTime = System.currentTimeMillis();
			log.debug("end program:" + endTime + " ,program cost time:" + (endTime - beginTime));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void saveResult(List<SMMAIBean> smmaiBeanList, SMMAIMateData smmaiMataData) {
		Connection connection = DBUtil.getAiuap20Connection();
		try {
			for (SMMAIBean bean : smmaiBeanList) {
				StringBuffer sBuffer = new StringBuffer(300);// (calcateTime,contextId,qqvalue,yxvalue,jjvalue,csvalue,czvalue)
				sBuffer.append("insert into a4_smmai_for_jk values");
				sBuffer.append("(").append(" to_date('" + smmaiMataData.getBegintime().replace("T", " ") + "','yyyy-MM-dd hh24:Mi:ss'),")
						.append(" to_date('" + smmaiMataData.getEndtime().replace("T", " ") + "','yyyy-MM-dd hh24:Mi:ss'),").append(
								 "'"+bean.getMode()).append("','").append( bean.getMainacctid()).append("','").append(
								 bean.getLoginname()).append("','").append(bean.getValid()).append("','").append(
								 bean.getLockstatus()).append("','").append(bean.getAccttype()).append("',").append(
								" to_date('" + bean.getEffecttime().replace("T", " ") + "','yyyy-MM-dd hh24:Mi:ss'),").append(
								" to_date('" + bean.getExpiretime().replace("T", " ") + "','yyyy-MM-dd hh24:Mi:ss'),").append(
								" to_date('" + bean.getEstablishtime().replace("T", " ") + "','yyyy-MM-dd hh24:Mi:ss'),").append(
								" to_date('" + bean.getUpdatetime().replace("T", " ") + "','yyyy-MM-dd hh24:Mi:ss'))");

				System.out.println("======="+sBuffer);
				DBUtil.executeSQL(connection, sBuffer.toString());
			}
		} catch (Exception e) {
			DBUtil.closeConnection(connection);
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(connection);
		}
	}

	private SMMAIMateData genarateBean(File file) {
		try {
			XStream xstream = new XStream();
			xstream.alias("bomc", SMMAIMateData.class);
			xstream.alias("rcd", SMMAIBean.class);
			SMMAIMateData acct = (SMMAIMateData) xstream.fromXML(new InputStreamReader(new FileInputStream(file),
					"UTF-8"));
			return acct;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

class SMMAIMateData {
	private String type;
	private String province;
	private String createtime;
	private String sum;
	private String begintime;
	private String endtime;
	private List<SMMAIBean> data = new ArrayList<SMMAIBean>();

	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public List<SMMAIBean> getData() {
		return data;
	}

	public void setData(List<SMMAIBean> data) {
		this.data = data;
	}

	public SMMAIMateData() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

}

class SMMAIBean {
	private String seq;
	private String mode;
	private String mainacctid;
	private String loginname;
	private String username;
	private String valid;
	private String lockstatus;
	private String accttype;
	private String rolelist;
	private String effecttime;
	private String expiretime;
	private String establishtime;
	private String areaid;
	private String orgid;
	private String orgname;
	private String updatetime;

	public SMMAIBean() {
		super();
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMainacctid() {
		return mainacctid;
	}

	public void setMainacctid(String mainacctid) {
		this.mainacctid = mainacctid;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getLockstatus() {
		return lockstatus;
	}

	public void setLockstatus(String lockstatus) {
		this.lockstatus = lockstatus;
	}

	public String getAccttype() {
		return accttype;
	}

	public void setAccttype(String accttype) {
		this.accttype = accttype;
	}

	public String getEffecttime() {
		return effecttime;
	}

	public void setEffecttime(String effecttime) {
		this.effecttime = effecttime;
	}

	public String getExpiretime() {
		return expiretime;
	}

	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
	}

	public String getEstablishtime() {
		return establishtime;
	}

	public void setEstablishtime(String establishtime) {
		this.establishtime = establishtime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getRolelist() {
		return rolelist;
	}

	public void setRolelist(String rolelist) {
		this.rolelist = rolelist;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.seq + "->" + this.accttype + "->" + this.areaid + "->" + this.loginname;
	}

}
