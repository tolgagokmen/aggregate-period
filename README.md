
IntelliSense.io Exercise
============================

Welcome to the IntelliSense.io Exercise.

This is a Java project for a very simple REST service, which is on an aggregate time period specified in the request.


Start Project 
--------

  Enter the project folder(cd aggregate-period) using the terminal and then run the following commands in the given order.                 

1)mvn clean install    
2)docker build -t aggregate-app .           
3)docker run -p 8090:8080 aggregate-app



Call service with curl
--------

curl -X POST "http://localhost:8090/aggregate" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"period\": 60}"

Call service with Postman
--------

Request Url;
http://localhost:8090/aggregate

Request Body;
{
    "period":60
}