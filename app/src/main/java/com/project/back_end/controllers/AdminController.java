Admin Controller
The admin controller will handle login operations, validating credentials and issuing tokens to authorized users.

Purpose: This controller handles the login functionality for the admin. It provides an endpoint for admin login validation.

Open the AdminController.java file.
Open AdminController.java in IDE

Set Up the Controller Class
Annotate the class with @RestController to designate it as a REST controller for handling HTTP requests.
Use @RequestMapping("${api.path}" + "admin") to set the base URL path for all methods in this controller.
Autowired Dependencies
Autowire the necessary service.
Service for handling the business logic, including admin validation
Define the adminLogin Method
Annotate this method with @PostMapping.
The method should accept an Admin object in the request body.
It should call validateAdmin method from Service to perform the admin login validation.
Return the response from the validateAdmin method, which provides the result of the admin login validation.
Response
The method returns a ResponseEntity<Map<String, String>>.
If the admin credentials are correct, the response will include a token.
If the credentials are incorrect, the response will contain an error message.
  
package com.project.back_end.controllers;

public class AdminController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that it's a REST controller, used to handle web requests and return JSON responses.
//    - Use `@RequestMapping("${api.path}admin")` to define a base path for all endpoints in this controller.
//    - This allows the use of an external property (`api.path`) for flexible configuration of endpoint paths.


// 2. Autowire Service Dependency:
//    - Use constructor injection to autowire the `Service` class.
//    - The service handles core logic related to admin validation and token checking.
//    - This promotes cleaner code and separation of concerns between the controller and business logic layer.


// 3. Define the `adminLogin` Method:
//    - Handles HTTP POST requests for admin login functionality.
//    - Accepts an `Admin` object in the request body, which contains login credentials.
//    - Delegates authentication logic to the `validateAdmin` method in the service layer.
//    - Returns a `ResponseEntity` with a `Map` containing login status or messages.



}

