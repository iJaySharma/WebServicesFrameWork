package bobby.Thinking;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;

@Path("/employee")
@InjectApplicationScope
public class EmployeeService
{
private ApplicationScope applicationScope;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
System.out.println("Application Scope invoked for employee service "+applicationScope);
}

@AutoWired(name="xyz")
private Employee firstEmployee;
@AutoWired(name="pqr")
private Employee secondEmployee;

@Path("/addEmployee")
@Get
public void addEmployee()
{
System.out.println("Add Employee Invoked");
}

@Path("/getFirstEmployee")
@Forward("/employee/printFirstEmployee")
@Get
public void getFirstEmployee()
{
Employee emp=new Employee();
emp.setCode(101);
emp.setName("JAY");
this.applicationScope.setAttribute("xyz",emp);
if(this.applicationScope.getAttribute("xyz")!=null)
{
Employee e=(Employee)this.applicationScope.getAttribute("xyz");
System.out.println(e.getCode());
System.out.println(e.getName());
}
System.out.println("Get First Employee Called");
}

@Path("/printFirstEmployee")
@Get
//@Forward("/index.jsp")
public void printFirstEmployee()
{
if(this.applicationScope.getAttribute("xyz")!=null)
{
Employee e=(Employee)this.applicationScope.getAttribute("xyz");
//System.out.println(e.getCode());
System.out.println(e.getName());
}
System.out.println("print First Employee Called");
}

@Path("/getSecondEmployee")
@Get
public void getThirdEmployee()
{
Employee emp=new Employee();
emp.setCode(102);
emp.setName("Rajul");
this.applicationScope.setAttribute("pqr",emp);
if(this.applicationScope.getAttribute("pqr")!=null)
{
Employee e=(Employee)this.applicationScope.getAttribute("pqr");
System.out.println(e.getName());
}
System.out.println("Get Second Employee Called ");
}

@Path("/printSecondEmployee")
@Get
@Forward("/index.jsp")
public void printSecondEmployee()
{
System.out.println("Print Second Employee Invoked");
if(this.firstEmployee==null)
{
System.out.println("First employee is null");
}
if(this.secondEmployee!=null) System.out.println(this.secondEmployee.getCode()+","+this.secondEmployee.getName());
}
}
