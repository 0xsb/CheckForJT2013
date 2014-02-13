package com.ailk.jt.task.entity;

public class A4CBOSS
{
  private String homeSwitchNode;
  private String totalCount;

  public String getHomeSwitchNode()
  {
    return this.homeSwitchNode;
  }

  public void setHomeSwitchNode(String homeSwitchNode) {
    this.homeSwitchNode = homeSwitchNode;
  }

  public String getTotalCount() {
    return this.totalCount;
  }

  public void setTotalCount(String totalCount) {
    this.totalCount = totalCount;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.homeSwitchNode == null ? 0 : this.homeSwitchNode.hashCode());
    result = 31 * result + (this.totalCount == null ? 0 : this.totalCount.hashCode());
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
    A4CBOSS other = (A4CBOSS)obj;
    if (this.homeSwitchNode == null) {
      if (other.homeSwitchNode != null)
        return false;
    } else if (!this.homeSwitchNode.equals(other.homeSwitchNode))
      return false;
    if (this.totalCount == null) {
      if (other.totalCount != null)
        return false;
    } else if (!this.totalCount.equals(other.totalCount))
      return false;
    return true;
  }
}
