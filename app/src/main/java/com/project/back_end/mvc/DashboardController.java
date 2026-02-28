Dashboard controller
The dashboard controller handles view rendering for Thymeleaf templates after validating a token for either admin or doctor users.

Purpose: This controller serves as a gatekeeper to the Thymeleaf dashboard views (adminDashboard and doctorDashboard) by verifying access tokens for authenticated users.

Open the DashboardController.java file
Open DashboardController.java in IDE

Set up the Controller class
Annotate the class with @Controller to indicate it returns views, not JSON responses.
This class maps requests to Thymeleaf templates based on user roles and tokens.
Autowired dependencies
Autowire the required service:

Service for handling the token validation logic.
Define the adminDashboard method
Annotate with @GetMapping("/adminDashboard/{token}") to handle requests to the admin dashboard.

Accept the token as a @PathVariable.

Call validateToken(token, "admin") using the service and check if the returned map is empty:

If empty: Token is valid → return the admin/adminDashboard view.
If not empty: Redirect to login page.
Define the doctorDashboard method
Annotate with @GetMapping("/doctorDashboard/{token}") to handle doctor dashboard access.
Accept the token as a @PathVariable.
Call validateToken(token, "doctor") and apply the same logic as in the adminDashboard.
Response
Each method returns a view name (String) for Thymeleaf to resolve:

If token is valid: Returns the respective dashboard template.
If invalid: Redirects the user to the login page at http://localhost:8080.
package com.project.back_end.mvc;

public class DashboardController {

// 1. Set Up the MVC Controller Class:
//    - Annotate the class with `@Controller` to indicate that it serves as an MVC controller returning view names (not JSON).
//    - This class handles routing to admin and doctor dashboard pages based on token validation.


// 2. Autowire the Shared Service:
//    - Inject the common `Service` class, which provides the token validation logic used to authorize access to dashboards.


// 3. Define the `adminDashboard` Method:
//    - Handles HTTP GET requests to `/adminDashboard/{token}`.
//    - Accepts an admin's token as a path variable.
//    - Validates the token using the shared service for the `"admin"` role.
//    - If the token is valid (i.e., no errors returned), forwards the user to the `"admin/adminDashboard"` view.
//    - If invalid, redirects to the root URL, likely the login or home page.


// 4. Define the `doctorDashboard` Method:
//    - Handles HTTP GET requests to `/doctorDashboard/{token}`.
//    - Accepts a doctor's token as a path variable.
//    - Validates the token using the shared service for the `"doctor"` role.
//    - If the token is valid, forwards the user to the `"doctor/doctorDashboard"` view.
//    - If the token is invalid, redirects to the root URL.


}
