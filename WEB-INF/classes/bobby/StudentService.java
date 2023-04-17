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