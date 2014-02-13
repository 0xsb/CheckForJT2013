package com.ailk.uap.entity;

import java.util.Date;

public class DrUploadFileInfo
{
  private String fileName;
  private String prov;
  private String type;
  private String intval;
  private String fileSeq;
  private String reloadFlag;
  private Date fileCreate;
  private Long total;
  private Date statisticBeginTime;
  private Date statisticEndTime;
  private String uploadStatus;

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getProv() {
    return prov;
  }

  public void setProv(String prov) {
    this.prov = prov;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getIntval() {
    return intval;
  }

  public void setIntval(String intval) {
    this.intval = intval;
  }

  public String getFileSeq() {
    return fileSeq;
  }

  public void setFileSeq(String fileSeq) {
    this.fileSeq = fileSeq;
  }

  public String getReloadFlag() {
    return reloadFlag;
  }

  public void setReloadFlag(String reloadFlag) {
    this.reloadFlag = reloadFlag;
  }

  public Date getFileCreate() {
    return fileCreate;
  }

  public void setFileCreate(Date fileCreate) {
    this.fileCreate = fileCreate;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Date getStatisticBeginTime() {
    return statisticBeginTime;
  }

  public void setStatisticBeginTime(Date statisticBeginTime) {
    this.statisticBeginTime = statisticBeginTime;
  }

  public Date getStatisticEndTime() {
    return statisticEndTime;
  }

  public void setStatisticEndTime(Date statisticEndTime) {
    this.statisticEndTime = statisticEndTime;
  }

  public String getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(String uploadStatus) {
    this.uploadStatus = uploadStatus;
  }
}