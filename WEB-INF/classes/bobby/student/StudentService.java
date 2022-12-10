package bobby.student;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.injections.*;
import java.util.*;

@Path("/studentService")
public class StudentService
{

    private ArrayList<String> list = new ArrayList<>();

    @Path("/test")
    @Get
    @Forward("/index.jsp")
    public String testStudent()
    {
        System.out.println("test student got invoked ");
        return "jay";
    }

    @Path("/test11")
    @Get
    public void test11()
    {
        System.out.println("test11 invoked");
    }

    @Path("/test22")
    @Get
    @Forward("/studentService/test11")
    public void test22()
    {
        System.out.println("test22 invoked");
    }

    @Path("/startup")
    @Get
    @OnStartup(priority=1)
    public void startup()
    {
list.add("mohan");
list.add("suresh");
System.out.println("startup got invoked");
    }

    @Path("/startup2")
    @Get
    @OnStartup(priority=2)
    public void startup2()
    {
for(String i : this.list)
{
System.out.println(i);
}
System.out.println("startup2 got invoked");
    }

    @Path("/startup3")
    @Get
    @OnStartup(priority=3)
    public void startup3()
    {

System.out.println("startup3 got invoked "+this.list);
}
}