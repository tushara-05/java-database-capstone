## Section 1: Architecture Summary
This Spring Boot application employs a hybrid architecture, combining MVC (Model-View-Controller) and REST (Representational State Transfer) patterns. The project utilizes a `DashboardController` (`com.project.back_end.mvc`) to serve Thymeleaf templates for the Admin and Doctor dashboards. Meanwhile, core functional modules—including Patient management, Appointments, and Prescriptions—are implemented via `@RestController`s that provide JSON-based RESTful APIs.

Data is persisted in a dual-database environment: MySQL (via Spring Data JPA) handles structured, relational data such as patients, doctors, and appointments, whereas MongoDB (via Spring Data MongoDB) manages document-oriented prescription records. All secured requests are routed through a centralized service layer that performs business logic and validates JWT tokens via a dedicated `TokenService` before accessing the repository tier.

## Section 2: Numbered Flow of Data and Control
1. The user initiates a request by accessing a dashboard URL or a specific REST API endpoint.
2. The request is routed to either a Thymeleaf MVC controller for page rendering or a REST controller for data exchange.
3. For protected routes, the controller validates the user's JWT token using the `TokenService` component to ensure authorization.
4. The controller invokes the appropriate service layer methods to execute the required business logic.
5. The service layer interacts with the Repository Layer, which delegates commands to either MySQL (using Spring Data JPA repositories) or MongoDB (using Spring Data MongoDB repositories). Advanced data aggregation, such as identifying top-performing medical staff, is handled via MySQL Stored Procedures (e.g., `GetDoctorWithMostPatientsByMonth`) for optimal performance.
6. The repository layer returns the persistence-tier object (Entity or Document) to the service layer for processing and DTO mapping.
7. The service layer returns the processed data to the controller.
8. The controller finally either renders a Thymeleaf template (for dashboards) or returns a JSON response (for API calls) to the user.
