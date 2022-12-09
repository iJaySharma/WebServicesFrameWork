package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import com.google.gson.*;
public class TMWebRock extends HttpServlet
{
private WebRockModel webRockModel;
public Boolean isResourceInHashMap(String resource)
{
Service service=webRockModel.getService(resource);
if(service==null) return false;
return true;
}
public void process(Service service,String methodType,HttpServletRequest request,HttpServletResponse response)
{
String[] requestInputs=request.getRequestURI().substring(1).split("/");
String[] pathVariables=null;
if(requestInputs.length>4)
{
pathVariables=new String[requestInputs.length-4];
int x=0;
for(int j=4;j<requestInputs.length;j++)
{
pathVariables[x]=requestInputs[j];
x++;
}
}
int index=0;
ApplicationScope applicationScope=null;
SessionScope sessionScope=null;
RequestScope requestScope=null;
ApplicationDirectory applicationDirectory=null;
ServletContext sc=getServletContext();
Method setSessionScopeMethod=null;
Method setApplicationScopeMethod=null;
Method setRequestScopeMethod=null;
Method setApplicationDirectoryMethod=null;
for(Method m1 : service.getServiceClass().getDeclaredMethods())
{
if(m1.getName().toString().equals("setSessionScope")) setSessionScopeMethod=m1;
if(m1.getName().toString().equals("setRequestScope")) setRequestScopeMethod=m1;
if(m1.getName().toString().equals("setApplicationScope")) setApplicationScopeMethod=m1;
if(m1.getName().toString().equals("setApplicationDirectory")) setApplicationDirectoryMethod=m1;
}
try
{
Object obj=service.getServiceClass().newInstance();
if(methodType.equals("GET"))
{
if(service.hasAllowPost()==true || service.hasAllowGet()==false)
{
//can't allow both at the same time
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
}
if(methodType.equals("GET"))
{
if(service.hasAllowGet()==true || service.getAllowBothPostAndGet()==true)
{
if(service.getInjectApplicationScope()==true)
{
if(setApplicationScopeMethod!=null) 
{
applicationScope=new ApplicationScope();
applicationScope.setServletContext(sc);
setApplicationScopeMethod.invoke(obj,applicationScope);
}
}
if(service.getInjectSessionScope()==true)
{
if(setSessionScopeMethod!=null)
{
sessionScope=new SessionScope();
sessionScope.setSession(request.getSession());
setSessionScopeMethod.invoke(obj,sessionScope);
}
}
if(service.getInjectRequestScope()==true)
{
if(setRequestScopeMethod!=null)
{
requestScope=new RequestScope();
requestScope.setRequest(request);
setRequestScopeMethod.invoke(obj,requestScope);
}
}
if(service.getInjectApplicationDirectory())
{
if(setApplicationDirectoryMethod!=null)
{
applicationDirectory=new ApplicationDirectory(new File(sc.getRealPath("/")));
setApplicationDirectoryMethod.invoke(obj,applicationDirectory);
}
}
//setting autowired fields before invoking methods
if(service.getAutowiredFields()!=null)
{
for(AutoWiredField af : service.getAutowiredFields())
{
String className=af.getField().getType().toString().substring(6);
Class classToInject=Class.forName(className);
//here we have to inject that autowired property
//order -> request, session ,application
if(request.getAttribute(af.getName())!=null)
{
//check if it is compatible
if(classToInject.isInstance(request.getAttribute(af.getName())))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,Class.forName(className).cast(request.getAttribute(af.getName())));
break;
}
}
}
}// if ends
else if(request.getSession().getAttribute(af.getName())!=null)
{
if(classToInject.isInstance(request.getSession().getAttribute(af.getName())))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,Class.forName(className).cast(request.getSession().getAttribute(af.getName())));
break;
}
}
}
}//2nd if for session
else if(sc.getAttribute(af.getName())!=null)
{
if(classToInject.isInstance(sc.getAttribute(af.getName())))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,Class.forName(className).cast(sc.getAttribute(af.getName())));
break;
}
}
}
}//3rd if for application
else
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,null);
break;
}
}
}//4th else

}
}
Method method=service.getMethod();
//extracting parameters and checking it are yet to be implemented it is only for void type right now having zero params
//extracting all parameter's
Enumeration<String> parameterNames=request.getParameterNames();
HashMap<String,Object> parametersMap=new HashMap<>();
if(parameterNames!=null)
{
while(parameterNames.hasMoreElements())
{
String key=(String)parameterNames.nextElement();
Object val=request.getParameter(key);
parametersMap.put(key,val);
}
}
//setting inject request param algo
if(service.getRequestParameterFields()!=null)
{
for(RequestParameterField rpf : service.getRequestParameterFields())
{
if(parametersMap.get(rpf.getValue())!=null && parametersMap.containsKey(rpf.getValue()))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(rpf.getField().getName().toString()))
{
f.setAccessible(true);
if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("int")) 
{
f.set(obj,Integer.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.String"))
{
f.set(obj,parametersMap.get(rpf.getValue()).toString());
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("boolean"))
{
f.set(obj,Boolean.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("byte"))
{
f.set(obj,Byte.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("short"))
{
f.set(obj,Short.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("long"))
{
f.set(obj,Long.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("float"))
{
f.set(obj,Float.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("double"))
{
f.set(obj,Double.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class")==false && f.getType().toString().equals("char"))
{
String a=parametersMap.get(rpf.getValue()).toString();
f.set(obj,a.charAt(0));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Integer"))
{
f.set(obj,Integer.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Boolean"))
{
f.set(obj,Boolean.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Byte"))
{
f.set(obj,Byte.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Short"))
{
f.set(obj,Short.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Long"))
{
f.set(obj,Long.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Float"))
{
f.set(obj,Float.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Double"))
{
f.set(obj,Double.valueOf(parametersMap.get(rpf.getValue()).toString()));
}
else if(f.getType().toString().startsWith("class") && f.getType().toString().substring(6).equals("java.lang.Character"))
{
String a=parametersMap.get(rpf.getValue()).toString();
f.set(obj,a.charAt(0));
}
else
{
f.set(obj,null);
}
break;
}
}

}
}
}
//get all parameters annotated with @RequestParameter annotation
Object[] arguments=null;
if(service.getParameters()!=null && service.getParameters().length>0) arguments=new Object[service.getParameters().length];
int i=0;
if(service.getParameters()!=null && service.getParameters().length>0)
{
for(Parameter p : service.getParameters())
{
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.ApplicationDirectory"))
{
arguments[i]=applicationDirectory;
i++;
continue;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.ApplicationScope"))
{
arguments[i]=applicationScope;
i++;
continue;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.RequestScope"))
{
arguments[i]=requestScope;
i++;
continue;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.SessionScope"))
{
arguments[i]=sessionScope;
i++;
continue;
}
if(p.isAnnotationPresent(PathVariable.class))
{
if(pathVariables!=null)
{
if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("int")) 
{
arguments[i]=Integer.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.String"))
{
arguments[i]=pathVariables[index];
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("boolean"))
{
arguments[i]=Boolean.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("byte"))
{
arguments[i]=Byte.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("short"))
{
arguments[i]=Short.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("long"))
{
arguments[i]=Long.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("float"))
{
arguments[i]=Float.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("double"))
{
arguments[i]=Double.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("char"))
{
String a=pathVariables[index];
arguments[i]=a.charAt(0);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Integer"))
{
arguments[i]=Integer.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Boolean"))
{
arguments[i]=Boolean.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Byte"))
{
arguments[i]=Byte.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Short"))
{
arguments[i]=Short.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Long"))
{
arguments[i]=Long.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Float"))
{
arguments[i]=Float.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Double"))
{
arguments[i]=Double.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Character"))
{
String a=pathVariables[index];
arguments[i]=a.charAt(0);
}
else
{
arguments[i]=null;
}
index++;
i++;
continue;
}
}//if pathvariable anno exists
if(p.isAnnotationPresent(RequestParameter.class))
{
RequestParameter requestParameterAnnotation=p.getAnnotation(RequestParameter.class);
String key=requestParameterAnnotation.value();
//if key is not present then set default value
if(parametersMap.get(key)==null || parametersMap.containsKey(key)==false)
{
if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("int")) 
{
arguments[i]=0;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.String"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("boolean"))
{
arguments[i]=false;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("byte"))
{
arguments[i]=0;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("short"))
{
arguments[i]=0;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("long"))
{
arguments[i]=0L;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("float"))
{
arguments[i]=0.0f;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("double"))
{
arguments[i]=0.0;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("char"))
{
String a="A";
arguments[i]=a.charAt(0);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Integer"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Boolean"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Byte"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Short"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Long"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Float"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Double"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Character"))
{
arguments[i]=null;
}
else
{
arguments[i]=null;
}
}
//---- here comes the gap
else
{
if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("int")) 
{
Object val=parametersMap.get(key);
arguments[i]=Integer.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.String"))
{
Object val=parametersMap.get(key);
arguments[i]=val.toString();
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("boolean"))
{
Object val=parametersMap.get(key);
arguments[i]=Boolean.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("byte"))
{
Object val=parametersMap.get(key);
arguments[i]=Byte.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("short"))
{
Object val=parametersMap.get(key);
arguments[i]=Short.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("long"))
{
Object val=parametersMap.get(key);
arguments[i]=Long.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("float"))
{
Object val=parametersMap.get(key);
arguments[i]=Float.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("double"))
{
Object val=parametersMap.get(key);
arguments[i]=Double.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("char"))
{
Object val=parametersMap.get(key);
arguments[i]=val.toString().charAt(0);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Integer"))
{
Object val=parametersMap.get(key);
arguments[i]=Integer.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Boolean"))
{
Object val=parametersMap.get(key);
arguments[i]=Boolean.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Byte"))
{
Object val=parametersMap.get(key);
arguments[i]=Byte.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Short"))
{
Object val=parametersMap.get(key);
arguments[i]=Short.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Long"))
{
Object val=parametersMap.get(key);
arguments[i]=Long.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Float"))
{
Object val=parametersMap.get(key);
arguments[i]=Float.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Double"))
{
Object val=parametersMap.get(key);
arguments[i]=Double.valueOf(val.toString());
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Character"))
{
Object val=parametersMap.get(key);
arguments[i]=val.toString().charAt(0);
}
else
{
Object val=parametersMap.get(key);
arguments[i]=null;
}
}//inner else ends
}//parameter annotation if present ends
else // last else
{
//parameter annotation contains no request param
if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("int")) 
{
arguments[i]=0;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.String"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("boolean"))
{
arguments[i]=false;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("byte"))
{
arguments[i]=0;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("short"))
{
arguments[i]=0;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("long"))
{
arguments[i]=0L;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("float"))
{
arguments[i]=0.0f;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("double"))
{
arguments[i]=0.0;
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("char"))
{
String a="A";
arguments[i]=a.charAt(0);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Integer"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Boolean"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Byte"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Short"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Long"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Float"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Double"))
{
arguments[i]=null;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Character"))
{
arguments[i]=null;
}
else
{
arguments[i]=null;
}
}
i++;
}//loop ends
}//if for checking service parameters are not null
if(arguments!=null && arguments.length>0)
{
if(!(service.getMethodReturnType().getName().toString().equals("void")))
{
response.setContentType("application/json");
PrintWriter writer=response.getWriter();
Object result=method.invoke(obj,arguments);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setSuccess(true);
serviceResponse.setIsException(false);
serviceResponse.setMessage(result);
writer.println(new Gson().toJson(serviceResponse));
}
else method.invoke(obj,arguments);
}
else 
{
if(!(service.getMethodReturnType().getName().toString().equals("void")))
{
response.setContentType("application/json");
PrintWriter writer=response.getWriter();
Object result=method.invoke(obj,arguments);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setSuccess(true);
serviceResponse.setIsException(false);
serviceResponse.setMessage(result);
writer.println(new Gson().toJson(serviceResponse));
}
else method.invoke(obj);
}
//check if there any autowired fields
//if forward annotation is present then process it recursively
//invoke sessionscope applicationscope if any
if(service.getIsForwarding())
{
if(service.getForwardTo()!=null && service.getForwardTo().length()>0 && service.getForwardTo().startsWith("/"))
{
//code to check if that path exists or not is yet to be implemented
File file=new File(sc.getRealPath("/")+service.getForwardTo().substring(1));
if(isResourceInHashMap(service.getForwardTo()))
{
Service serviceForForwarding=webRockModel.getService(service.getForwardTo());
//recursively process a service url
if(serviceForForwarding.hasAllowPost()==true || serviceForForwarding.hasAllowGet()==false)
{
//can't allow both at the same time
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
if(serviceForForwarding.hasAllowGet()==true || serviceForForwarding.getAllowBothPostAndGet()==true)
{
//recursive call
this.process(serviceForForwarding,"GET",request,response);
return;
}
}
else if(!file.exists()) //check if static content path exists
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(service.getForwardTo());
requestDispatcher.forward(request,response);
}
}
else
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
}
}
}//if methodType GET ends
if(methodType.equals("POST"))
{
if(service.hasAllowPost()==false || service.hasAllowGet()==true)
{
//can't allow both at the same time
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
}
if(methodType.equals("POST"))
{
if(service.hasAllowPost()==true || service.getAllowBothPostAndGet()==true)
{
if(service.getInjectApplicationScope()==true)
{
if(setApplicationScopeMethod!=null) 
{
applicationScope=new ApplicationScope();
applicationScope.setServletContext(sc);
setApplicationScopeMethod.invoke(obj,applicationScope);
}
}
if(service.getInjectSessionScope()==true)
{
if(setSessionScopeMethod!=null)
{
sessionScope=new SessionScope();
sessionScope.setSession(request.getSession());
setSessionScopeMethod.invoke(obj,sessionScope);
}
}
if(service.getInjectRequestScope()==true)
{
if(setRequestScopeMethod!=null)
{
requestScope=new RequestScope();
requestScope.setRequest(request);
setRequestScopeMethod.invoke(obj,requestScope);
}
}
if(service.getInjectApplicationDirectory())
{
if(setApplicationDirectoryMethod!=null)
{
applicationDirectory=new ApplicationDirectory(new File(sc.getRealPath("/")));
setApplicationDirectoryMethod.invoke(obj,applicationDirectory);
}
}
//setting autowired fields before invoking methods
if(service.getAutowiredFields()!=null)
{
for(AutoWiredField af : service.getAutowiredFields())
{
String className=af.getField().getType().toString().substring(6);
Class classToInject=Class.forName(className);
//here we have to inject that autowired property
//order -> request, session ,application
if(request.getAttribute(af.getName())!=null)
{
//check if it is compatible
if(classToInject.isInstance(request.getAttribute(af.getName())))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,Class.forName(className).cast(request.getAttribute(af.getName())));
break;
}
}
}
}// if ends
else if(request.getSession().getAttribute(af.getName())!=null)
{
if(classToInject.isInstance(request.getSession().getAttribute(af.getName())))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,Class.forName(className).cast(request.getSession().getAttribute(af.getName())));
break;
}
}
}
}//2nd if for session
else if(sc.getAttribute(af.getName())!=null)
{
if(classToInject.isInstance(sc.getAttribute(af.getName())))
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,Class.forName(className).cast(sc.getAttribute(af.getName())));
break;
}
}
}
}//3rd if for application
else
{
for(Field f : service.getServiceClass().getDeclaredFields())
{
if(f.getName().toString().equals(af.getField().getName().toString()))
{
f.setAccessible(true);
f.set(obj,null);
break;
}
}
}//4th else

}
}
//printing post data in request
StringBuffer sb=new StringBuffer();
String line=null;
BufferedReader reader=request.getReader();
while((line=reader.readLine())!=null)
{
sb.append(line);
}
int countForParameter=0;
if(service.getParameters()!=null && service.getParameters().length>0)
{
for(Parameter p : service.getParameters())
{
if(p.isAnnotationPresent(RequestParameter.class))
{
countForParameter=3;
break;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.ApplicationDirectory"))
{
continue;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.ApplicationScope"))
{
continue;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.SessionScope"))
{
continue;
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.RequestScope"))
{
continue;
}
else if(p.isAnnotationPresent(PathVariable.class))
{
continue;
}
else
{
countForParameter++;
}
}
}
if(countForParameter>1)
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
Method method=service.getMethod();
Object[] arguments=null;
if(service.getParameters()!=null && service.getParameters().length>0) arguments=new Object[service.getParameters().length];
int i=0;
if(service.getParameters()!=null && service.getParameters().length>0)
{
for(Parameter p : service.getParameters())
{
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.ApplicationDirectory"))
{
arguments[i]=applicationDirectory;
i++;
continue;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.ApplicationScope"))
{
arguments[i]=applicationScope;
i++;
continue;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.RequestScope"))
{
arguments[i]=requestScope;
i++;
continue;
}
if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("com.thinking.machines.webrock.injections.SessionScope"))
{
arguments[i]=sessionScope;
i++;
continue;
}
if(p.isAnnotationPresent(PathVariable.class))
{
if(pathVariables!=null)
{
if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("int")) 
{
arguments[i]=Integer.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.String"))
{
arguments[i]=pathVariables[index];
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("boolean"))
{
arguments[i]=Boolean.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("byte"))
{
arguments[i]=Byte.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("short"))
{
arguments[i]=Short.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("long"))
{
arguments[i]=Long.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("float"))
{
arguments[i]=Float.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("double"))
{
arguments[i]=Double.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class")==false && p.getType().toString().equals("char"))
{
String a=pathVariables[index];
arguments[i]=a.charAt(0);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Integer"))
{
arguments[i]=Integer.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Boolean"))
{
arguments[i]=Boolean.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Byte"))
{
arguments[i]=Byte.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Short"))
{
arguments[i]=Short.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Long"))
{
arguments[i]=Long.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Float"))
{
arguments[i]=Float.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Double"))
{
arguments[i]=Double.valueOf(pathVariables[index]);
}
else if(p.getType().toString().startsWith("class") && p.getType().toString().substring(6).equals("java.lang.Character"))
{
String a=pathVariables[index];
arguments[i]=a.charAt(0);
}
else
{
arguments[i]=null;
}
index++;
i++;
continue;
}
}//if pathvariable anno exists
if(p.getType().toString().startsWith("class"))
{
if(sb.length()>0)
{
Class classToInject=Class.forName(p.getType().toString().substring(6));
System.out.println(sb.toString()+" "+p.getType());
arguments[i]=new Gson().fromJson(sb.toString(),classToInject);
}
i++;
continue;
}
if(p.getType().toString().startsWith("class")==false)
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
i++;
}//loop ends
}//if for checking null parameters ends
if(arguments!=null && arguments.length>0)
{
if(!(service.getMethodReturnType().getName().toString().equals("void")))
{
response.setContentType("application/json");
PrintWriter writer=response.getWriter();
Object result=method.invoke(obj,arguments);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setSuccess(true);
serviceResponse.setIsException(false);
serviceResponse.setMessage(result);
writer.println(new Gson().toJson(serviceResponse));
}
else method.invoke(obj,arguments);
}
else 
{
if(!(service.getMethodReturnType().getName().toString().equals("void")))
{
response.setContentType("application/json");
PrintWriter writer=response.getWriter();
Object result=method.invoke(obj,arguments);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setSuccess(true);
serviceResponse.setIsException(false);
serviceResponse.setMessage(result);
writer.println(new Gson().toJson(serviceResponse));
}
else method.invoke(obj);
}
//here ends method invokation in post
if(service.getIsForwarding())
{
if(service.getForwardTo()!=null && service.getForwardTo().length()>0 && service.getForwardTo().startsWith("/"))
{
File file=new File(sc.getRealPath("/")+service.getForwardTo().substring(1));
if(isResourceInHashMap(service.getForwardTo()))
{
Service serviceForForwarding=webRockModel.getService(service.getForwardTo());
//recursively handle this thing
if(serviceForForwarding.hasAllowGet()==true || serviceForForwarding.hasAllowPost()==false)
{
//can't allow both at the same time
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
if(serviceForForwarding.hasAllowPost()==true || serviceForForwarding.getAllowBothPostAndGet()==true)
{
//recursive call
this.process(serviceForForwarding,"POST",request,response);
return;
}
}
else if(!file.exists())
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(service.getForwardTo());
requestDispatcher.forward(request,response);
}
}
else
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
}
}
}
}catch(Exception exception)
{
exception.printStackTrace();
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e){}
}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.setContentType("application/json");
PrintWriter pw=response.getWriter();
ServletContext sc=getServletContext();
webRockModel=(WebRockModel)sc.getAttribute("webRockModel");
if(webRockModel==null) 
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setSuccess(false);
serviceResponse.setIsException(false);
serviceResponse.setMessage("No Services has been created");
String jsonObject=new Gson().toJson(serviceResponse);
pw.print(jsonObject);
pw.close();
return;
}
String requestURI=request.getRequestURI();
requestURI=requestURI.substring(1);
if(requestURI.split("/").length<4)
{
response.sendError(HttpServletResponse.SC_BAD_REQUEST);
return;
}
String pathOfClass=requestURI.split("/")[2];
String pathOfMethod=requestURI.split("/")[3];
String actualURI="/"+pathOfClass+"/"+pathOfMethod;
Service service=webRockModel.getService(actualURI);
if(webRockModel.getService(actualURI)==null)
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
//invoke method attached to service
process(service,"GET",request,response);
}catch(Exception exception)
{
exception.printStackTrace();
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.setContentType("application/json");
PrintWriter pw=response.getWriter();
ServletContext sc=getServletContext();
webRockModel=(WebRockModel)sc.getAttribute("webRockModel");
if(webRockModel==null) 
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setSuccess(false);
serviceResponse.setIsException(false);
serviceResponse.setMessage("No Services has been created");
String jsonObject=new Gson().toJson(serviceResponse);
pw.print(jsonObject);
pw.close();
return;
}
String requestURI=request.getRequestURI();
requestURI=requestURI.substring(1);
if(requestURI.split("/").length<4)
{
response.sendError(HttpServletResponse.SC_BAD_REQUEST);
return;
}
String pathOfClass=requestURI.split("/")[2];
String pathOfMethod=requestURI.split("/")[3];
String actualURI="/"+pathOfClass+"/"+pathOfMethod;
Service service=webRockModel.getService(actualURI);
if(webRockModel.getService(actualURI)==null)
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
process(service,"POST",request,response);
}catch(Exception exception)
{
exception.printStackTrace();
}
}
}