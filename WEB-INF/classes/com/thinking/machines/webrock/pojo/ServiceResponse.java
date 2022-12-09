package com.thinking.machines.webrock.pojo;
public class ServiceResponse implements java.io.Serializable
{
private Boolean success;
private Boolean isException;
private Object message;
public ServiceResponse()
{
this.success=false;
this.isException=false;
this.message=null;
}
public void setSuccess(Boolean success)
{
this.success=success;
}
public Boolean getSuccess()
{
return this.success;
}
public void setIsException(Boolean isException)
{
this.isException=isException;
}
public Boolean getIsException()
{
return this.isException;
}
public void setMessage(Object message)
{
this.message=message;
}
public Object getMessage()
{
return this.message;
}
}