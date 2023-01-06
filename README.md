# Todo-app
This is web application I made when learning Java and Spring Framework.

## How to run

To make it run you just download or clone repo and compile it on your device.  
By default app should start without any problems on Java 17 and path: `localhost:8080`

## How to run with Keycloak

Application have implemented Keycloak security module but it's disabled by default to make it easier to run and test.  
If you want to work with authentication and user feutures.  
You need to download keycloak distribution by Quarkus from https://www.keycloak.org/downloads  
Then import realms from this repository or run Keycloak server and create your own realms.  
By default keycloak server adress in application has been set to `localhost:8180` so keep that in mind.  
After that change keycloak.enabled property to true in application.yml and now you can run application with users and authentication funtionality.  

Test users logins and passwords are:  
Login: admin  
Password admin  

Login: user  
Password: user  
