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



}