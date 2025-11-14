This Spring Boot application integrates both MVC architectural pattern and REST controllers. 
Admin and Doctor dashboards are rendered using Thymeleaf, while the remaining modules rely on RESTful APIs.
The system connects to two databases: MySQL for handling patient, doctor, appointment, and admin information, and MongoDB for managing prescription documents.
All controllers—whether template-based or REST—delegate requests to a shared service layer that executes business logic and communicates with the appropriate repositories.
MySQL interactions use JPA entities, while MongoDB relies on document models, enabling the application to efficiently manage data across relational and NoSQL storage systems.


 Each step of the data flow :

1. User sends an HTTP request via browser or API client.
2. Controller (MVC or REST) receives the request and calls the service layer.
3. Service layer executes business logic and determines which repository to use.
4. Repository layer interacts with MySQL (JPA) or MongoDB (documents).
5. Database executes queries and returns data to the repository.
6. Service layer processes and transforms the data as needed.
7. Controller returns the response as a Thymeleaf-rendered page or RESTful JSON.
