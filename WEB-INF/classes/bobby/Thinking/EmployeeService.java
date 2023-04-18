package bobby.Thinking;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;
import java.util.*;
@Path("/employee")
@InjectApplicationScope
@InjectSessionScope
@InjectRequestScope
public class EmployeeService
{
@InjectRequestParameter("xyz")
private String xyz;
private ApplicationScope applicationScope;
private SessionScope sessionScope;
private RequestScope requestScope;
private ApplicationDirectory applicationDirectory;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
System.out.println("setApplication Scope method got invoked ");
}
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
System.out.println("setSession scope method got invoked");
}
public void setRequestScope(RequestScope requestScope)
{
this.requestScope=requestScope;
System.out.println("setRequest Scope method got invoked");
}
public void setApplicationDirectory(ApplicationDirectory applicationDirectory)
{
this.applicationDirectory=applicationDirectory;
System.out.println("setApplication Directory method got invoked");
}
@Path("/scope")
@Get
public void scope()
{
if(this.applicationScope!=null)
{
this.applicationScope.setAttribute("name","bobby");
this.applicationScope.setAttribute("code","678");
System.out.println("ApplicationScope object has initialized");
}
}
@Path("/fetchEmployee")
@Get
public void fetchEmployee()
{
if(this.applicationScope!=null)
{
if(this.applicationScope.getAttribute("name")!=null)System.out.println(this.applicationScope.getAttribute("name"));
if(this.applicationScope.getAttribute("code")!=null)System.out.println(this.applicationScope.getAttribute("code"));
}
}
@Path("/parameterRequest")
@Get
public void Parameter(@RequestParameter("salary") Integer salary)
{
System.out.println("@InjectRequestParameter value :"+xyz);
System.out.println("@RequestParameter value :"+salary);
}
@Path("/pathVariable")
@Post
public Employee Employee(Employee ee,@PathVariable int a,@PathVariable String s,ApplicationScope as,SessionScope ss,RequestScope rs)
{
Integer empCode = ee.getCode();
String empName = ee.getName();
as.setAttribute("empCode",empCode);
as.setAttribute("empName",empName);
System.out.println(ee.getName()+" "+ee.getCode());
System.out.println(a+" "+s);
return ee;
}
@AutoWired(name="empName")
private String name;
@AutoWired(name="empCode")
private Integer code;
@Path("/autowired")
@Get
public void autowired()
{
if(code!=null)System.out.println(code);
if(name!=null)System.out.println(name);
}
}