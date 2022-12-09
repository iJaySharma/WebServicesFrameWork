package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class AutoWiredField
{
private Field field;
private String name;
public AutoWiredField()
{
this.field=null;
this.name=null;
}
public void setField(Field field)
{
this.field=field;
}
public Field getField()
{
return this.field;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
}