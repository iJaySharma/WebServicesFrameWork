package bobby.Thinking;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;
import java.util.*;
@Path("/test")
@InjectApplicationScope
@InjectSessionScope
@InjectRequestScope
public class EmployeeService
{
@InjectRequestParameter("code")
private Integer code;
@InjectRequestParameter("name")
private Employee nm;
private ApplicationScope applicationScope;
private SessionScope sessionScope;
private RequestScope requestScope;
private ApplicationDirectory applicationDirectory;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
}
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}
public void setRequestScope(RequestScope requestScope)
{
this.requestScope=requestScope;
}
public void setApplicationDirectory(ApplicationDirectory applicationDirectory)
{
this.applicationDirectory=applicationDirectory;
}
@AutoWired(name="test")
private Employee e;
@Path("/testEmployee")
@Get
public void testEmployee()
{
if(e!=null)
{
System.out.println(e.getCode()+",,,,,"+e.getName());
}
else
{
System.out.println("Problem");
}
}
@Path("/cartoon")
@Get
public void cartoon()
{
System.out.println("Cartoon invoked");
if(code!=null) System.out.println(code);
if(nm!=null) System.out.println(nm);
}
@Path("/testForArguments")
@Get
public Employee testForArguments(int x,String y,Boolean b,Integer z,Character p,@RequestParameter("salary") int salary,@RequestParameter("code") Integer c,@RequestParameter("name") String name)
{
System.out.println("Test for arguments invoked");
Employee e=new Employee();
e.setCode(c);
e.setName(name);
if(code!=null && name!=null)
{
System.out.println("Code "+code+" Name "+name+" Salary "+salary);
}
return e;
}
@Path("/doSomething")
@Get
@Forward("/test.jsp")
public void doSomething(@RequestParameter("xyz") Integer xyz,ApplicationScope as,SessionScope ss,RequestScope rs,ApplicationDirectory ad)
{
System.out.println(xyz);
if(as!=null) System.out.println("Application scope is not null");
if(ss!=null) System.out.println("Session scope is not null");
if(rs!=null) System.out.println("Request Scope is not null");
if(ad!=null) System.out.println("Application directory is not null");
}
@Path("/testForPost")
@Post
public String testForPost(ApplicationScope as,SessionScope ss,RequestScope rs,ApplicationDirectory ad,@RequestParameter("xyz") Employee emp)
{
System.out.println("Test For Post Invoked");
if(emp!=null) System.out.println(emp.getCode()+","+emp.getName());
if(as!=null) System.out.println("Application scope is not null");
if(ss!=null) System.out.println("Session scope is not null");
if(rs!=null) System.out.println("Request Scope is not null");
if(ad!=null) System.out.println("Application directory is not null");
return "Hello from test for Post";
}
@Path("/pathVariable")
@Get
public void pathVariable(@PathVariable Integer xx,@PathVariable String yy,@RequestParameter("pqr") int pqr)
{
if(pqr!=0) System.out.println(pqr);
if(xx!=null) System.out.println(xx);
if(yy!=null) System.out.println(yy);
}
@Path("/xyz")
@Post
public HashMap<Integer,String> xyz(@PathVariable Integer a,@PathVariable String b,Employee emp)
{
System.out.println("Request Arrived For Post");
HashMap<Integer,String> map=new HashMap<>();
map.put(a,b);
map.put(emp.getCode(),emp.getName());
return map;
}
}