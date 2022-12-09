package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class RequestParameterField
{
private Field field;
private String value;
public RequestParameterField()
{
this.field=null;
this.value=null;
}
public void setField(Field field)
{
this.field=field;
}
public Field getField()
{
return this.field;
}
public void setValue(String value)
{
this.value=value;
}
public String getValue()
{
return this.value;
}
}