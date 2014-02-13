package com.ailk.jt.task.entity;

public class A4CBOSSJF
{
  private String homeSwitchNode;
  private String busiCount;

  public String getHomeSwitchNode()
  {
    return this.homeSwitchNode;
  }

  public void setHomeSwitchNode(String homeSwitchNode) {
    this.homeSwitchNode = homeSwitchNode;
  }

  public String getBusiCount() {
    return this.busiCount;
  }

  public void setBusiCount(String busiCount) {
    this.busiCount = busiCount;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.busiCount == null ? 0 : this.busiCount.hashCode());
    result = 31 * result + (this.homeSwitchNode == null ? 0 : this.homeSwitchNode.hashCode());
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    A4CBOSSJF other = (A4CBOSSJF)obj;
    if (this.busiCount == null) {
      if (other.busiCount != null)
        return false;
    } else if (!this.busiCount.equals(other.busiCount))
      return false;
    if (this.homeSwitchNode == null) {
      if (other.homeSwitchNode != null)
        return false;
    } else if (!this.homeSwitchNode.equals(other.homeSwitchNode))
      return false;
    return true;
  }
}
