## Overview
---
This is WebServices Framework(Replica of SpringBoot Framework) designed to exemplify the functionality of spring boot.
What it does is it gives java programmer a control by which one can get rid of writing/handling bulk of java servlet classes, mappings and services.
In a simple word user just wants to write java classes with multiple methods by specifying the annotations on top of it in accordance to the functionality and feasibility of task deliver by this operations.
Basically the idea is developed around Java Reflection Api Annotations and the framework uses Tomcat9 Http web-server.
## Benefits of Using FrameWork
* No requirements of writing servlets and managing their xml mappings because this is actually the part framework handles itself. 
* User can keep the advantage over writing complex request response driven http protocols based services for any kind of request framework will manage it Whole.
* User ultimately consumes the parameters like Get/Post Requests Inversion of Control(IOC) Servlet life cycle Dependency Injection etc in a transparent manner.
* Therefore programmer/user bring up the their way of writing java classes to make services for a Web-Application independently.
## Let's get Started
* Download [Latest JDK](https://www.oracle.com/in/java/technologies/javase-downloads.html)
* Download [Apache tomcat9](https://tomcat.apache.org/download-90.cgi) open source web application Java Server 
* Set Java class path if not added and for running tomcat9 server just make sure cmd runs startup.bat file in tomcat9/bin/startup.bat (Path of server starter inside tomcat9 folder) successfully to see logs and activities.
* clone this repository inside tomcat9/webapps folder and change the repository folder name (lets say WebServicesFrameWork -> "ProjectName") and Copy web.xml to tomcat9/Webapps/"ProjectName"/WEB-INF/
* User have to create package structure inside classes folder (for example i have created it as bobby/student bobby/Engineer etc) 
```
    <init-param>
    <param-name>SERVICE_PACKAGE_PREFIX</param-name>
    <param-value>bobby</param-value>
    </init-param>
```
Note: the folder-name mention here should exists inside tomcat9/Webapps/"project name"/WEB-INF/classes/ and inside it you can write java service classes(java programs to create web services) which is going to be scanned by reflection Api
* URL servicePattern /schoolServices/* is the application entity name which can be changed if needed.
    ```
    <servlet>
    <servlet-name>TMWebRock</servlet-name>
    <servlet-class>com.thinking.machines.webrock.TMWebRock</servlet-class>
    </servlet>
    <servlet-mapping>
    <servlet-name>TMWebRock</servlet-name>
    <url-pattern>/schoolService/*</url-pattern>
    </servlet-mapping>
    ```
    
* Now Paste WebRock.jar and gson jar inside libs folder of WEB-INF (WebRock.jar is the most important file to use this framework)
* start tomcat server by running startup batch file inside tomcat's bin folder and send request from either browser or REST Client
* for compilation of java classes inside service-package-prefix (i.e bobby folder) write  javac -classpath c:\tomcatFolder\lib*;c:\tomcatFolder\webapps\ProjectName\WEB-INF\lib*; c:\tomcatFolder\webapps\ProjectName\WEB-INF\classes;. ClassName.java<br>
Note : for testing resources open browser or RESTclient give it as (eg localhost:portNumber/ProjectName/servicePattern/"annotatedPath" i.e annotated path as in accordance with classes and methods here) in address bar 
* for demostrating the fuctionality of the framework i take you through the usecase examples of what each annotations does in below points/image

## Features of Framework (Annotations and their descriptions)
- @Path("/pathPattern") applicable for both class as well as method level 
- @AutoWired(name="propname") this annotation will help when user wants to inject data in any property from request or session or servletContext(Property level)
- @Forward(urlPattern) this annotation will forward request to url pattern(Method level)
- @OnStartup(priority) the method annotated with this annotation will invoked according to priority number
- @Get() to specify a service is of get type (class and both method level the method or class which is not annotated with this annotation will be application for both get and post)
- @Post() to specify a service is of post type (class and both method level the method or class which is not annotated with this annotation will be application for both get and post)
- @RequestParameter(name="paramname") this annotation is used to get data coming from query string in get request directly inside a method parameter
- @InjectRequestParameter("paramname") this annotation is used to wrap data coming from query string to a corresponding property specified
- @PathVariable() this annotation will help to get data coming along with request uri inside a method parameter
- @InjectApplicationDirectory this annotation is used to get wrapper of ApplicationDirectory class inside a property specified
- @InjectApplicationScope this annotation is used to inject application scope(ServletContext) wrapper inside a service as a Field
- @InjectSessionScope this annotation is used to inject Session scope(HttpSession) wrapper inside a service as a Field
- @InjectRequestScope this annotation is used to inject Request scope(HttpServletRequest) wrapper inside a service as a Field
## Sample java classes examples (Use Case Functionality of each mentioned Annotations)
```
package bobby.Designation;
import com.thinking.machines.webrock.annotations.*;
import java.util.*;

@Path("/designation")
public class designationService
{
private  ArrayList<String> list = new ArrayList<>();

@OnStartup(priority=1)
@Path("/startup1")
@Get
public void Startup1()
{
    list.add("Backend Engineer");
    list.add("Frontend Engineer");
    list.add("Data Engineer");
    System.out.println("Startup 1 got invoked");
}
@OnStartup(priority=3)
@Path("/startup3")
public void startup3()
{
    System.out.println("Number of Designations are "+this.list.size());
    System.out.println("Starter 3 got invoked ");
}
@Get
@OnStartup(priority=2)
@Path("/startup2")
public void Startup2()
{
    for(String gg : this.list)
    {
        System.out.println(gg);
    }
    System.out.println("Startup 2 got invoked");
}
@Path("/test")
@Forward("/home.jsp")
public void test()
{
System.out.println("Test From designation Service");
}
}
```

```
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
```
## Sample of writing REST End Points for web-application (CRUD Operations) 
```
package bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import java.sql.*;
import java.util.*;

@Path("/studentService")
public class StudentService
{
public static Connection getConnection()
{
Connection connection=null;
try 
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/tmdb","tmdbuser","tmdbuser");
}catch(Exception e)
{
System.out.println(e);
}
return connection;
}//jdbc done

@Path("/addStudent")
@Post
public void addStudent(Student s) throws Exception
{
Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select rollNumber from Student where rollNumber=?");
preparedStatement.setInt(1,s.getRollNumber());
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new Exception("student with roll number "+s.getRollNumber()+" already exits.");
}
preparedStatement=connection.prepareStatement("insert into Student (rollNumber,name,age) values(?,?,?)");
preparedStatement.setInt(1,s.getRollNumber());
preparedStatement.setString(2,s.getName());
preparedStatement.setInt(3,s.getAge());
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}

@Post
@Path("/updateStudent")
public void updateStudent(Student s) throws Exception
{
Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select rollNumber from Student where rollNumber=?");
preparedStatement.setInt(1,s.getRollNumber());
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new Exception("student with roll number "+s.getRollNumber()+" not exists.");
}
preparedStatement=connection.prepareStatement("update Student set name=? , age=? where rollNumber=?");
preparedStatement.setString(1,s.getName());
preparedStatement.setInt(2,s.getAge());
preparedStatement.setInt(3,s.getRollNumber());

preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}

@Get
@Path("/removeStudent")
public void removeStudent(@RequestParameter("rollNumber") int rollNumber) throws Exception
{
Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select rollNumber from Student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new Exception("student with roll number "+rollNumber+" not exists.");
}
preparedStatement=connection.prepareStatement("delete from Student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}

@Path("/getAllStudent")
@Get
public List<Student> getAllStudent() throws Exception
{
List<Student> list=new LinkedList<Student>();
Student s;
Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select * from Student");
resultSet=preparedStatement.executeQuery();
while(resultSet.next())
{
s=new Student();
s.setRollNumber(resultSet.getInt("rollNumber"));
s.setName(resultSet.getString("name"));
s.setAge(resultSet.getInt("age"));
list.add(s);
}
resultSet.close();
preparedStatement.close();
connection.close();
return list;
}
@Path("/getStudentByRollNumber")
@Get
public Student getStudentByRollNumber(@RequestParameter("rollNumber") int rollNumber) throws Exception
{
Student s=new Student();
Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select * from Student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new Exception("student with roll number "+rollNumber+" not exists.");
}
s.setRollNumber(rollNumber);
s.setName(resultSet.getString("name"));
s.setAge(resultSet.getInt("age"));
resultSet.close();
preparedStatement.close();
connection.close();
return s;
}
@Path("/studentAdd")
@Post
public void studentAdd(Student p)
{
System.out.println(p.getRollNumber()+" "+p.getName()+" "+p.getAge());
}
}
```
## Sample Web-Application 
- Refer student.html and student.js file inside my package structure i have write the UI specific scenerios in JQuery and Showed you the effectiveness of the above Demostrated REST End Points which is just the Simple Java Classes with annoations and PreDefined Set Of Methods and JDBC connection.
- When you try running the Application on localhost 8080 you could able to see CRUD Operation on Student (name rollNumber and age) but this is just a basic application which is fully managed and processed by the framework (first setup sql database and include gson and sql-connector jars).






