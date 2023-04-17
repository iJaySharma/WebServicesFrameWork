package bobby;
public class Student implements java.io.Serializable
{
private int rollNumber;
private String name;
private int age;

public Student()
{
this.rollNumber=0;
this.name="";
this.age=0;
}


public Student(int rollNumber,String name,int age)
{
this.rollNumber=rollNumber;
this.name=name;
this.age=age;
}

public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}

public int getRollNumber()
{
return this.rollNumber;
}

public void setName(String name)
{
this.name=name;
}

public String getName()
{
return this.name;
}

public void setAge(int age)
{
this.age=age;
}

public int getAge()
{
return this.age;
}

}