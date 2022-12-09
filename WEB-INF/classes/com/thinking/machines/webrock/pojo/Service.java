package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
import java.util.*;
public class Service
{
private Class serviceClass;
private String path;
private Method method;
private Object[] arguments;
private List<AutoWiredField> autowiredFields;
private List<RequestParameterField> requestParameterFields;
private Boolean allowBothPostAndGet;
private Boolean allowPost;
private Integer priority;
private Boolean allowGet;
private Boolean isForwarding;
private String forwardTo;
private Class methodReturnType;
private Parameter[] parameters;
private Boolean injectSessionScope;
private Boolean injectApplicationScope;
private Boolean injectRequestScope;
private Boolean injectApplicationDirectory;
public Service()
{
this.requestParameterFields=null;
this.arguments=null;
this.autowiredFields=null;
this.injectSessionScope=false;
this.injectApplicationScope=false;
this.injectRequestScope=false;
this.injectApplicationDirectory=false;
this.serviceClass=null;
this.path=null;
this.method=null;
this.allowPost=false;
this.allowGet=false;
this.isForwarding=false;
this.forwardTo=null;
this.allowBothPostAndGet=false;
this.methodReturnType=null;
this.parameters=null;
this.priority=0;
}
public void setRequestParameterFields(List<RequestParameterField> requestParameterFields)
{
this.requestParameterFields=requestParameterFields;
}
public List<RequestParameterField> getRequestParameterFields()
{
return this.requestParameterFields;
}
public void setArguments(Object[] arguments)
{
this.arguments=arguments;
}
public Object[] getArguments()
{
return this.arguments;
}
public void setAutowiredFields(List<AutoWiredField> autowiredFields)
{
this.autowiredFields=autowiredFields;
}
public List<AutoWiredField> getAutowiredFields()
{
return this.autowiredFields;
}
public void setInjectApplicationScope(Boolean injectApplicationScope)
{
this.injectApplicationScope=injectApplicationScope;
}
public Boolean getInjectApplicationScope()
{
return this.injectApplicationScope;
}
public void setInjectSessionScope(Boolean injectSessionScope)
{
this.injectSessionScope=injectSessionScope;
}
public Boolean getInjectSessionScope()
{
return this.injectSessionScope;
}
public void setInjectRequestScope(Boolean injectRequestScope)
{
this.injectRequestScope=injectRequestScope;
}
public Boolean getInjectRequestScope()
{
return this.injectRequestScope;
}
public void setInjectApplicationDirectory(Boolean injectApplicationDirectory)
{
this.injectApplicationDirectory=injectApplicationDirectory;
}
public Boolean getInjectApplicationDirectory()
{
return this.injectApplicationDirectory;
}
public void setPriority(Integer priority)
{
this.priority=priority;
}
public Integer getPriority()
{
return this.priority;
}
public void setMethodReturnType(Class methodReturnType)
{
this.methodReturnType=methodReturnType;
}
public Class getMethodReturnType()
{
return this.methodReturnType;
}
public void setParameters(Parameter[] parameters)
{
this.parameters=parameters;
}
public Parameter[] getParameters()
{
return this.parameters;
}
public void setAllowBothPostAndGet(Boolean allowBothPostAndGet)
{
this.allowBothPostAndGet=allowBothPostAndGet;
}
public Boolean getAllowBothPostAndGet()
{
return this.allowBothPostAndGet;
}
public void setIsForwarding(Boolean isForwarding)
{
this.isForwarding=isForwarding;
}
public Boolean getIsForwarding()
{
return this.isForwarding;
}
public void setForwardTo(String forwardTo)
{
this.forwardTo=forwardTo;
}
public String getForwardTo()
{
return this.forwardTo;
}
public void setServiceClass(Class serviceClass)
{
this.serviceClass=serviceClass;
}
public Class getServiceClass()
{
return this.serviceClass;
}
public void setAllowPost(Boolean allowPost)
{
this.allowPost=allowPost;
}
public Boolean hasAllowPost()
{
return this.allowPost;
}
public void setAllowGet(Boolean allowGet)
{
this.allowGet=allowGet;
}
public Boolean hasAllowGet()
{
return this.allowGet;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setMethod(Method method)
{
this.method=method;
}
public Method getMethod()
{
return this.method;
}
} 