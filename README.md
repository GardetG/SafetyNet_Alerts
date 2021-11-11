# SafetyNet Alerts API
With SafetyNet Alerts, our goal is to to develop an application which will help first aid to be better prepared and handle any situation by providing the information they need to accomplish their mission.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisities

This project is built with:

- Java 11
- Maven 3.8.1
- Spring Boot 2.5.6

Please check that you are using the correct version of Java and Maven

### Running API

After importing this project, check that the data file `data.json` is present and up to date in `src/main/resources`. By default, the API use port 8080, this can be configure in `application.properties` in the same resources directory.

To run the API, go to the root folder of the project containing the pom.xml and execute the command:  
`mvn spring-boot:run`

The API is up and running, ready to receive and respond to requests.

### Testing API

To run the tests from maven, go to the folder that contains the pom.xml file and execute the below command:  
`mvn test`
