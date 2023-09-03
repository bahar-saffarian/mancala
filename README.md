# Mancala Game REST API 

This is a Rest service provider application in java 17 and SpringBoot. </br>

## Security
Rest APi resources are secured via API KEY authentication. The rest request must contain a header named "X-API-KEY" and the 
value needs to be "bol-mancala-game". The api-key value is placed in application.properties file with the name "app.apikey.value"
this property could be overridden when you run the project.

## Api Document by OpenApi
The project is documented by open api. After running the project swagger V3 would be available on below address, 
you can access the APIs specification and also test the Apis via swagger interface.
```
http://localhost:8080/swagger-ui/index.html#/
```

## Run project

Do this steps to run the server on port 8080 of your machine:
To create a jar file, in project directory run:
```
./mvnw clean compile package
```
Then in target package you can find “mancala-0.0.1-SNAPSHOT.jar” and run by:
```
java -jar mancala-0.0.1-SNAPSHOT.jar
```
