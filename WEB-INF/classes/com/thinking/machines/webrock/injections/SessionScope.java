package com.thinking.machines.webrock.injections;
import javax.servlet.*;
import javax.servlet.http.*;
public class SessionScope
{
private HttpSession session;
public void setSession(HttpSession session)
{
this.session=session;
}
public void setAttribute(String key,Object value)
{
this.session.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.session.getAttribute(key);
}
}