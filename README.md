# Simple Access Management System using WebFlux
by João Carlos (https://www.linkedin.com/in/joaocarlosvale/)

## Description:
It is a microservice that:
1. Supports assigning and revoking **EDIT** or **VIEW** permission to a specified department for
   a specific user.

2. Supports retrieving the permissions to a specified department for a specific user. For
   John, querying department B permissions will return **VIEW** permission, which is assigned.

## Technologies used:
* Java 11
* Spring Webflux
* Gradle 
* MongoDB

## Commands:

To generate JAR:

    gradlew build

To run:

    java -jar build/libs/task-0.0.1-SNAPSHOT.jar
