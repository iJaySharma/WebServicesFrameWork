class Student
{
constructor(rollNumber,name,age)
{
this.rollNumber=rollNumber;
this.name=name;
this.age=age;
}
}

class StudentService
{
addStudent(student)
{
var prm=new Promise(function(resolve,reject){
$.ajax({
"url" : "/WebRock/schoolService/studentService/addStudent",
"type" : "POST",
"data" : JSON.stringify(student),
"contentType" : "application/json",
"success" : function(response){
resolve(response);
},
"error" : function(response){
reject(response);
}
});
});
return prm;
}

getAllStudent()
{
var prm=new Promise(function(resolve,reject){
$.ajax({
"url" : "/WebRock/schoolService/studentService/getAllStudent",
"type" : "GET",
"contentType" : "application/json",
"success" : function(responseData){
resolve(responseData);
},
"error" : function(){
reject("response failed");
}
});
});
return prm;
}

updateStudent(student)
{
var prm=new Promise(function(resolve,reject){
$.ajax({
"url" : "/WebRock/schoolService/studentService/updateStudent",
"type" : "POST",
"data" : JSON.stringify(student),
"contentType" : "application/json",
"success" : function(response){
resolve(response);
},
"error" : function(response){
reject(response);
}
});
});
return prm;
}

removeStudent(rollNumber)
{
var prm=new Promise(function(resolve,reject){
$.ajax({
"url" : "/WebRock/schoolService/studentService/removeStudent?rollNumber="+rollNumber,
"type" : "GET",
"contentType" : "application/json",
"success" : function(response){
resolve(response);
},
"error" : function(response){
reject(response);
}
});
});
return prm;
}
}