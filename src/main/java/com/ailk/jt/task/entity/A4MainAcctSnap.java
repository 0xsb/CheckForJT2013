package com.ailk.jt.task.entity;

public class A4MainAcctSnap {

	private String acctType;
	private java.lang.Double acctUsage;
	private java.lang.Long areaId;
	private java.util.Date createTime;
	private java.util.Date effectTime;
	private java.util.Date expireTime;
	private String lockStatus;
	private String loginName;
	private Long logondays;
	private java.lang.Long mainAcctId;
	private String modifyMode;
	private Long opendays;
	private java.lang.Long orgId;
	private String orgname;
	private String rolelist;
	private String superuser;
	private java.util.Date updateTime;
	private String userName;
	private String valid;

	public A4MainAcctSnap() {
	}

	public String getAcctType() {
		return this.acctType;
	}

	public Double getAcctUsage() {
		return this.acctUsage;
	}

	public java.lang.Long getAreaId() {
		return this.areaId;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public java.util.Date getEffectTime() {
		return this.effectTime;
	}

	public java.util.Date getExpireTime() {
		return this.expireTime;
	}

	public String getLockStatus() {
		return this.lockStatus;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public Long getLogondays() {
		return this.logondays;
	}

	public java.lang.Long getMainAcctId() {
		return this.mainAcctId;
	}

	public String getModifyMode() {
		return this.modifyMode;
	}

	public Long getOpendays() {
		return this.opendays;
	}

	public java.lang.Long getOrgId() {
		return this.orgId;
	}

	public String getOrgname() {
		return this.orgname;
	}

	public String getRolelist() {
		return this.rolelist;
	}

	public String getSuperuser() {
		return this.superuser;
	}

	public java.util.Date getUpdateTime() {
		return this.updateTime;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getValid() {
		return this.valid;
	}

	public void setAcctType(String value) {
		this.acctType = value;
	}

	public void setAcctUsage(Double value) {
		this.acctUsage = value;
	}

	public void setAreaId(java.lang.Long value) {
		this.areaId = value;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public void setEffectTime(java.util.Date value) {
		this.effectTime = value;
	}

	public void setExpireTime(java.util.Date value) {
		this.expireTime = value;
	}

	public void setLockStatus(String value) {
		this.lockStatus = value;
	}

	public void setLoginName(String value) {
		this.loginName = value;
	}

	public void setLogondays(Long value) {
		this.logondays = value;
	}

	public void setMainAcctId(java.lang.Long value) {
		this.mainAcctId = value;
	}

	public void setModifyMode(String value) {
		this.modifyMode = value;
	}

	public void setOpendays(Long value) {
		this.opendays = value;
	}

	public void setOrgId(java.lang.Long value) {
		this.orgId = value;
	}

	public void setOrgname(String value) {
		this.orgname = value;
	}

	public void setRolelist(String value) {
		this.rolelist = value;
	}

	public void setSuperuser(String value) {
		this.superuser = value;
	}

	public void setUpdateTime(java.util.Date value) {
		this.updateTime = value;
	}

	public void setUserName(String value) {
		this.userName = value;
	}

	public void setValid(String value) {
		this.valid = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acctType == null) ? 0 : acctType.hashCode());
		result = prime * result + ((acctUsage == null) ? 0 : acctUsage.hashCode());
		result = prime * result + ((areaId == null) ? 0 : areaId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((effectTime == null) ? 0 : effectTime.hashCode());
		result = prime * result + ((expireTime == null) ? 0 : expireTime.hashCode());
		result = prime * result + ((lockStatus == null) ? 0 : lockStatus.hashCode());
		result = prime * result + ((loginName == null) ? 0 : loginName.hashCode());
		result = prime * result + ((logondays == null) ? 0 : logondays.hashCode());
		result = prime * result + ((mainAcctId == null) ? 0 : mainAcctId.hashCode());
		result = prime * result + ((modifyMode == null) ? 0 : modifyMode.hashCode());
		result = prime * result + ((opendays == null) ? 0 : opendays.hashCode());
		result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
		result = prime * result + ((orgname == null) ? 0 : orgname.hashCode());
		result = prime * result + ((rolelist == null) ? 0 : rolelist.hashCode());
		result = prime * result + ((superuser == null) ? 0 : superuser.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((valid == null) ? 0 : valid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final A4MainAcctSnap other = (A4MainAcctSnap) obj;
		if (acctType == null) {
			if (other.acctType != null)
				return false;
		} else if (!acctType.equals(other.acctType))
			return false;
		if (acctUsage == null) {
			if (other.acctUsage != null)
				return false;
		} else if (!acctUsage.equals(other.acctUsage))
			return false;
		if (areaId == null) {
			if (other.areaId != null)
				return false;
		} else if (!areaId.equals(other.areaId))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (effectTime == null) {
			if (other.effectTime != null)
				return false;
		} else if (!effectTime.equals(other.effectTime))
			return false;
		if (expireTime == null) {
			if (other.expireTime != null)
				return false;
		} else if (!expireTime.equals(other.expireTime))
			return false;
		if (lockStatus == null) {
			if (other.lockStatus != null)
				return false;
		} else if (!lockStatus.equals(other.lockStatus))
			return false;
		if (loginName == null) {
			if (other.loginName != null)
				return false;
		} else if (!loginName.equals(other.loginName))
			return false;
		if (logondays == null) {
			if (other.logondays != null)
				return false;
		} else if (!logondays.equals(other.logondays))
			return false;
		if (mainAcctId == null) {
			if (other.mainAcctId != null)
				return false;
		} else if (!mainAcctId.equals(other.mainAcctId))
			return false;
		if (modifyMode == null) {
			if (other.modifyMode != null)
				return false;
		} else if (!modifyMode.equals(other.modifyMode))
			return false;
		if (opendays == null) {
			if (other.opendays != null)
				return false;
		} else if (!opendays.equals(other.opendays))
			return false;
		if (orgId == null) {
			if (other.orgId != null)
				return false;
		} else if (!orgId.equals(other.orgId))
			return false;
		if (orgname == null) {
			if (other.orgname != null)
				return false;
		} else if (!orgname.equals(other.orgname))
			return false;
		if (rolelist == null) {
			if (other.rolelist != null)
				return false;
		} else if (!rolelist.equals(other.rolelist))
			return false;
		if (superuser == null) {
			if (other.superuser != null)
				return false;
		} else if (!superuser.equals(other.superuser))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (valid == null) {
			if (other.valid != null)
				return false;
		} else if (!valid.equals(other.valid))
			return false;
		return true;
	}

}
