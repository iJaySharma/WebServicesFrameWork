package com.thinking.machines.webrock.model;
import com.thinking.machines.webrock.pojo.*;
import java.util.*;
public class WebRockModel
{
private HashMap<String,Service> services=new HashMap<>();
public void putService(String path,Service service)
{
this.services.put(path,service);
}
public Service getService(String path)
{
return this.services.get(path);
}
public HashMap<String,Service> getServicesMap()
{
return this.services;
}
}