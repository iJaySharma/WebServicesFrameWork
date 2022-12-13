package bobby.student;
import com.thinking.machines.webrock.annotations.*;
@Path("/studentService")
@Get
public class StudentService
{
@Path("/addStudent")
@Forward("/index.jsp")
public void addMember()
{
System.out.println("Add Student Invoked");
}
@Path("/updateStudent")
public String updateStudent()
{
System.out.println("Update Student Invoked");
return "Student";
}
}