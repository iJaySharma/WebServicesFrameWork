package bobby.Student;
public class Student
{
private Integer rollNumber;
private String name;
Student()
{
this.rollNumber=0;
this.name="";
}

public void setRollNumber(Integer rollNumber)
{
this.rollNumber = rollNumber;
}
public void setName(String name)
{
this.name=name;
}
public Integer getRollNumber()
{
return this.rollNumber;
}
public String getName()
{
return this.name;
}
}