## Overview
---
This is WebServices Framework(Replica of SpringBoot Framework) designed to exemplify the functionality of spring boot.
What it does is it gives java programmer a control by which one can get rid of writing/handling bulk of java servlet classes, mappings and services.
In a simple word user just wants to write java classes with multiple methods by specifying the annotations on top of it in accordance to the functionality and feasibility of task deliver by this operations.
Basically the idea is developed around Java Reflection Api Annotations and the framework uses Tomcat9 Http web-server.
## Benefits of Using FrameWork
* No requirements of writing servlets and managing their xml mappings because this is actually the part framework handles itself. 
* User keep the advantage over writing complex request response driven http protocols based services for any kind of request framework will manage it Whole.
* User ultimately consumes the parameters like Get/Post Requests Inversion of Control(IOC) Servlet life cycle Dependency Injection etc in a transparent manner.
* Therefore programmer/user bring up the their way of writing java classes to make services for a Web-Application independently.
## Let's get Started
* Download [Latest JDK](https://www.oracle.com/in/java/technologies/javase-downloads.html)
* Download [Apache tomcat9](https://tomcat.apache.org/download-90.cgi) open source web application Java Server 
* Set Java class path if not added and for running tomcat9 server just make sure cmd runs startup.bat file in tomcat9/bin/startup.bat (Path of server starter inside tomcat9 folder) successfully to see logs and activities.
* clone this repository inside tomcat9/webapps folder and change the repository folder name (lets say WebServicesFrameWork -> "ProjectName") and Copy web.xml to tomcat9/Webapps/"ProjectName"/WEB-INF/
* User have to create package structure inside classes folder (for example i have created it as bobby/student bobby/Engineer etc) 
```
    <init-param>
    <param-name>SERVICE_PACKAGE_PREFIX</param-name>
    <param-value>bobby</param-value>
    </init-param>
```
Note: the folder-name mention here should exists inside tomcat9/Webapps/"project name"/WEB-INF/classes/ and inside it you can write java service classes(java programs to create web services) which is going to be scanned by reflection Api
* URL servicePattern /schoolServices/* is the application entity name which can be changed if needed.
    ```
    <servlet>
    <servlet-name>TMWebRock</servlet-name>
    <servlet-class>com.thinking.machines.webrock.TMWebRock</servlet-class>
    </servlet>
    <servlet-mapping>
    <servlet-name>TMWebRock</servlet-name>
    <url-pattern>/schoolService/*</url-pattern>
    </servlet-mapping>
    ```
    
* Now Paste WebRock.jar and gson jar inside libs folder of WEB-INF (TMWebRock.jar is the most important file to use this framework)
* start tomcat server by running startup batch file inside tomcat's bin folder and send request from either browser or REST Client
* for compilation of java classes inside service-package-prefix (i.e bobby folder) write  javac -classpath c:\tomcatFolder\lib*;c:\tomcatFolder\webapps\ProjectName\WEB-INF\lib*; c:\tomcatFolder\webapps\ProjectName\WEB-INF\classes;. ClassName.java<br>
Note : for testing resources open browser or RESTclient give it as (eg localhost:portNumber/ProjectName/servicePattern/bobby/student) in address bar 
* for demostrating the fuctionality of the framework i take you through the usecase examples of what each annotations does in below points/image








