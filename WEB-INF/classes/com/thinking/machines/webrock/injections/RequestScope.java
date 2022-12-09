package com.thinking.machines.webrock.injections;
import javax.servlet.http.*;
import javax.servlet.*;
public class RequestScope
{
private HttpServletRequest request;
public void setRequest(HttpServletRequest request)
{
this.request=request;
}
public void setAttribute(String key,Object value)
{
this.request.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.request.getAttribute(key);
}
}