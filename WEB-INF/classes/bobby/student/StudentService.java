package bobby.Student;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;
@Path("/studentService")
public class StudentService
{

@Path("/addStudent")
@Post
public Student addStudent(Student student,ApplicationScope as)
{
System.out.println(student.getRollNumber()+","+student.getName());
return student;
}
@Path("/updateStudent")
public String updateStudent()
{
System.out.println("Update Student Invoked");
return "Student";
}
}