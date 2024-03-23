## Task manager
Api for task manager built on Spring Framework for leaning purposes.

## Technologies:
* Java 17
* Spring framework (Spring boot, Spring Security, String Data JPA/Hibernate, Spring Web, Spring boot starter mail)
* Postgresql
* PGAdmin
* Lombok
* JUnit
* JWT
* Testcontainers
* Springdoc OpenApi

### Functionality/features:
* User authentication with JWT generation and storing it in cookies
* User registration with checking existing ones
* Refresh JWT token if refresh-token exists and has not expired
* Logout by retrieving the current authenticated user and removing refresh tokens associated with his ID
* Account verification after registration
* Password reset by mail
* Changing the current user's password
* Dashboard with projects and 50 tasks by deadline and creation date
* Retrieving all projects or a project by query and receiving tasks of all or by request or tag
* All CRUD operations with projects and tasks
* Changing project/task status
* Unit and integration tests

## JWT
Auth was performed using JWT with Refresh and Access tokens and HttpOnly Cookie.

## Swagger Docs
The project has been configured with a Swagger that exposes the APIs with the schema

Accessible at http://localhost:8080/swagger-ui/index.html

## Getting started

<h4> Clone the application </h4>

```bash
$ git clone https://github.com/AlKl1M/TaskManager.git
$ cd TaskManager
```

<h4>Create a Postgres database</h4>

```bash
$ create database taskmanager;
$ create user taskmanager with password 'taskmanager';
$ grant all privileges on database taskmanager to taskmanager;
```

<h4>Run the app</h4>
Now you can run the app locally or deploy it using docker

```bash
$ docker compose up
```

* It deploys locally on port 8080, and in Docker on 8081.
* Every run of the app will reset your state. To not do that, modify spring.jpa.hibernate.ddl-auto: update
* Also don't forget to create gmail account for Spring mail functionality. This account must have two-factor authentication.
