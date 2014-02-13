package com.ailk.jt.task.entity;

public class A4MainAcctLogonDays {
    private String mainAcctName;
	private java.lang.Long loginDay;
	public String getMainAcctName() {
		return mainAcctName;
	}
	public void setMainAcctName(String mainAcctName) {
		this.mainAcctName = mainAcctName;
	}
	public java.lang.Long getLoginDay() {
		return loginDay;
	}
	public void setLoginDay(java.lang.Long loginDay) {
		this.loginDay = loginDay;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loginDay == null) ? 0 : loginDay.hashCode());
		result = prime * result + ((mainAcctName == null) ? 0 : mainAcctName.hashCode());
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
		final A4MainAcctLogonDays other = (A4MainAcctLogonDays) obj;
		if (loginDay == null) {
			if (other.loginDay != null)
				return false;
		} else if (!loginDay.equals(other.loginDay))
			return false;
		if (mainAcctName == null) {
			if (other.mainAcctName != null)
				return false;
		} else if (!mainAcctName.equals(other.mainAcctName))
			return false;
		return true;
	}
	public A4MainAcctLogonDays() {
		super();
	}
}
