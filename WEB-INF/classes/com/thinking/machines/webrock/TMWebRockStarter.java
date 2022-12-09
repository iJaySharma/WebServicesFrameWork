package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.exceptions.*;
import com.thinking.machines.webrock.annotations.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.stream.*;
public class TMWebRockStarter extends HttpServlet
{
private WebRockModel webRockModel=new WebRockModel();
private String servicePackagePrefix;
private List<Class> serviceClasses=new ArrayList<>(); 
private List<Service> services=new ArrayList<>();
private List<Service> servicesToInvokeOnStartup=new ArrayList<>();
public void invokeServicesOnStartup()
{
try
{
for(Service s : this.servicesToInvokeOnStartup)
{
s.getMethod().invoke(s.getServiceClass().newInstance());
}
}catch(Exception exception)
{
exception.printStackTrace();
}
}
public void validateServicePackagePrefix(String servicePackagePrefix) throws ServiceException
{
//validating if service package prefix is correctly specified of not
boolean isPkgSpecifiedProperly=true;
for(int i=0;i<servicePackagePrefix.length();i++)
{
if(((int)servicePackagePrefix.charAt(i)>=97 && (int)servicePackagePrefix.charAt(i)<=122) ||
((int)servicePackagePrefix.charAt(i)>=65 && (int)servicePackagePrefix.charAt(i)<=90) ||
((int)servicePackagePrefix.charAt(i)>=48 && (int)servicePackagePrefix.charAt(i)<=57)
)
{
continue;
}
else
{
isPkgSpecifiedProperly=false;
break;
}
}
if(!isPkgSpecifiedProperly)
{
throw new ServiceException("Invalid Package Prefix");
}
}
public void scanClassAndLoadInDS(String className)
{
if(className.charAt(0)=='.') className=className.substring(1);
//extracting word prior to .class
for(int i=className.length()-1;i>=0;i--)
{
if(className.charAt(i)=='.') 
{
className=className.substring(0,i);
break;
}
}
className=servicePackagePrefix+"."+className;
try
{
Class c=Class.forName(className);
if(c.isAnnotationPresent(Path.class)) this.serviceClasses.add(c);
}catch(Throwable t)
{
t.printStackTrace();
}
}
public void recursiveDirectoriesScan(File[] folders,int level,String packageName)
{
for(File f : folders)
{
if(f.isFile() && f.getName().endsWith(".class"))
{
if(packageName.length()==0) scanClassAndLoadInDS(f.getName());
else scanClassAndLoadInDS(packageName+"."+f.getName());
}
else if(f.isDirectory())
{
recursiveDirectoriesScan(f.listFiles(),level+1,packageName+"."+f.getName());
}
}
}
public void scanClasses(String packagePrefix) throws ServiceException
{
//code to scan all the Folders inside Folders and find .class File
ServletContext sc=getServletContext();
String classesFolder=sc.getRealPath("/")+"WEB-INF"+File.separator+"classes";
File folder=new File(classesFolder+File.separator+packagePrefix);
if(!folder.exists())
{
throw new ServiceException("Invalid Package name");
}
File[] folders=folder.listFiles();
recursiveDirectoriesScan(folders,0,"");
}
public void scanAllMethodsInClass(Class c)
{
Method[] methods=c.getMethods();
Field[] fields=c.getDeclaredFields();
List<AutoWiredField> autowiredFields=new ArrayList<>();
List<RequestParameterField> requestParameterFields=new ArrayList<>();
if(fields!=null)
{
for(Field field : fields)
{
if(field.isAnnotationPresent(AutoWired.class))
{
AutoWired autowiredAnnotation=(AutoWired)field.getAnnotation(AutoWired.class);
String name="";
if(autowiredAnnotation!=null && autowiredAnnotation.name()!=null)
{
name=autowiredAnnotation.name();
} 
AutoWiredField autowiredField=new AutoWiredField();
autowiredField.setField(field);
autowiredField.setName(name);
autowiredFields.add(autowiredField);
}
if(field.isAnnotationPresent(InjectRequestParameter.class))
{
InjectRequestParameter injectRequestParameterAnnotation=(InjectRequestParameter)field.getAnnotation(InjectRequestParameter.class);
String value="";
if(injectRequestParameterAnnotation!=null && injectRequestParameterAnnotation.value()!=null)
{
value=injectRequestParameterAnnotation.value();
}
RequestParameterField requestParameterField=new RequestParameterField();
requestParameterField.setField(field);
requestParameterField.setValue(value);
requestParameterFields.add(requestParameterField);
}
}
}
for(Method m : methods)
{
if(m.isAnnotationPresent(Path.class))
{
Service s=new Service();
s.setServiceClass(c);
Path pathAnnotation=(Path)c.getAnnotation(Path.class);
Path pathAnnotationForMethod=(Path)m.getAnnotation(Path.class);
if(pathAnnotation.value().startsWith("/")==true && pathAnnotation.value().startsWith("/")==true)
{
s.setPath(pathAnnotation.value()+pathAnnotationForMethod.value());
}
if(c.isAnnotationPresent(Get.class) || m.isAnnotationPresent(Get.class))
{
s.setAllowGet(true);
}
if(c.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Post.class))
{
s.setAllowPost(true);
}
if(c.isAnnotationPresent(Get.class)==false && m.isAnnotationPresent(Get.class)==false 
&& c.isAnnotationPresent(Post.class)==false && m.isAnnotationPresent(Post.class)==false)
{
s.setAllowGet(false);
s.setAllowPost(false);
s.setAllowBothPostAndGet(true);
}
if(autowiredFields.size()>0) 
{
s.setAutowiredFields(autowiredFields);
}
if(requestParameterFields.size()>0)
{
s.setRequestParameterFields(requestParameterFields);
}
if(m.isAnnotationPresent(Forward.class))
{
s.setIsForwarding(true);
Forward forwardAnnotation=(Forward)m.getAnnotation(Forward.class);
s.setForwardTo(forwardAnnotation.value());
}
s.setMethod(m);
if(m.getParameters()!=null && m.getParameters().length>0) s.setParameters(m.getParameters());
s.setMethodReturnType(m.getReturnType());
if(m.isAnnotationPresent(OnStartup.class) && m.getReturnType().toString().equals("void") && (m.getParameters()==null || m.getParameters().length==0))
{
OnStartup onStartupAnnotation=(OnStartup)s.getMethod().getAnnotation(OnStartup.class);
int priority=onStartupAnnotation.priority();
s.setPriority(priority);
}
if(c.isAnnotationPresent(InjectSessionScope.class))
{
s.setInjectSessionScope(true);
}
if(c.isAnnotationPresent(InjectRequestScope.class))
{
s.setInjectRequestScope(true);
}
if(c.isAnnotationPresent(InjectApplicationScope.class))
{
s.setInjectApplicationScope(true);
}
if(c.isAnnotationPresent(InjectApplicationDirectory.class))
{
s.setInjectApplicationDirectory(true);
}
this.services.add(s);
}
}
//code to process methods annotated with On Startup
}
public void init()
{
try
{
System.out.println("*****************");
System.out.println("****TM Web Rock********");
System.out.println("*****************");
ServletConfig servletConfig=getServletConfig();
servicePackagePrefix=servletConfig.getInitParameter("SERVICE_PACKAGE_PREFIX");
validateServicePackagePrefix(servicePackagePrefix);
scanClasses(servicePackagePrefix);
Service service;
for(Class c : this.serviceClasses)
{
scanAllMethodsInClass(c);
}
if(this.services!=null && this.services.size()>0)
{
for(Service s : this.services)
{
String path=s.getPath();
if(path!=null) webRockModel.putService(path,s);
}
getServletContext().setAttribute("webRockModel",webRockModel);
}
else getServletContext().setAttribute("webRockModel",null);
for(Map.Entry<String,Service> entry : webRockModel.getServicesMap().entrySet())
{
System.out.println("Key : "+entry.getKey());
System.out.println("Value : "+entry.getValue());
}
for(Service s : this.services)
{
//invoke only when return type is void having 0 params and annotated with OnStartup
if((s.getMethodReturnType().toString().equals("void") && (s.getParameters()==null || s.getParameters().length==0) && s.getMethod().isAnnotationPresent(OnStartup.class) && s.getPriority()!=0))
{
servicesToInvokeOnStartup.add(s);
}
}
//sort servicesToInvoke priority wise
servicesToInvokeOnStartup=servicesToInvokeOnStartup.
stream().sorted((s1,s2)-> s1.getPriority().compareTo(s2.getPriority())).
collect(Collectors.toList());
invokeServicesOnStartup();
}catch(Exception e)
{
e.printStackTrace();
}
}
}