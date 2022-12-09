package bobby.student;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;

@Path("/studentService")
public class StudentService
{
    @Path("/test")
    @Get
    public String testStudent()
    {
        System.out.println("test student got invoked ");
        return "jay";
    }
}