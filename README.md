# Appointment Scheduling Application

![Java Build](https://github.com/charlesluz1994/agenda-service/actions/workflows/main_agendaservice.yml/badge.svg?branch=main)

This is a Java RESTful application for appointment scheduling. 
The application is hosted on an Azure service (Azure App Service) and uses a PostgreSQL database hosted on Azure. 
Integrated tests are being executed with an H2 database, and unit tests with JUnit + Mockito.
There are 2 Postman collections provided in the root: 1 for the Azure endpoint and one for the Local tests.

By default, the application use the dev profile.

For local tests:
Run the application with the local profile in IntelliJ.
To use 'local', in your run configuration, add the environment variable: spring.profiles.active=local

# Swagger endpoint Local
http://localhost:8085/swagger-ui/#/agenda-controller

# Swagger endpoint Azure
https://agendaservice.azurewebsites.net/swagger-ui/#/agenda-controller

### Technologies
Java 11
Maven
JPA + Hibernate
PostgreSQL
JUnit 5 + Mockito - Unit tests
MockMVC - Integration Tests

### Some functionalities available in the API
- ✅ Java model class with validation
- ✅ JPA repository
- ✅ PostgreSQL database
- ✅ Controller, Service, and Repository layers
- ✅ Hibernate / Jakarta Validation
- ✅ Unit tests for all layers (repository, service, controller)
- ✅ Test coverage for tests
- ✅ Spring Docs - Swagger 
- ✅ Security (Authentication)
- ✅ Flyway
- ✅ Deployed to Azure Cloud

### Some functionalities to be implemented
- Upgrate to Java 17
- Improve Test coverage
- Use Records as DTO
- Caching
- Pagination

## Controllers
The application has two main controllers:

### AgendaController
This controller handles operations related to appointments.

Actions:

Create an appointment
Endpoint: `POST /agenda`
This endpoint creates a new appointment. You need to send a JSON object containing the appointment data in the request body.

List all appointments
Endpoint: GET /agenda
This endpoint returns all registered appointments.

Get an appointment by ID
Endpoint: `GET /agenda/{id}`
This endpoint returns a specific appointment based on the provided ID.

Delete an appointment
Endpoint: `DELETE /agenda/{id}`
This endpoint deletes an appointment based on the provided ID.

### PatientController
This controller handles operations related to patients.

Create a patient
Endpoint: `POST /patient`
This endpoint creates a new patient. You need to send a JSON object containing the patient data in the request body.

List all patients
Endpoint: `GET /patient`
This endpoint returns all registered patients.

Get a patient by ID
Endpoint: `GET /patient/{id}`
This endpoint returns a specific patient based on the provided ID.

Update a patient
Endpoint: `PUT /patient/{id}`
This endpoint updates an existing patient based on the provided ID. You need to send a JSON object containing the updated patient data in the request body.

Delete a patient
Endpoint: `DELETE /patient/{id}`
This endpoint deletes a patient based on the provided ID.


