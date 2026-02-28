Admin Repository
This repository will support basic CRUD operations for admin users and allow you to find admins by username.

Open the AdminRepository.java file.
Open AdminRepository.java in IDE

Create a repository for the Admin model by extending JpaRepository. This will allow for basic CRUD operations without needing to implement the methods manually.

Add the following method:

findByUsername: Find an admin by their username.

Return type: Admin
Parameter: String username

Hint:

Extend JpaRepository<Admin, Long> to inherit basic CRUD functionality.
Declare custom query methods using Spring Data conventions, such as findByUsername.
package com.project.back_end.repo;

public interface AdminRepository {

    // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Admin, Long>, which gives it basic CRUD functionality.
//    - The methods such as save, delete, update, and find are inherited without the need for explicit implementation.
//    - JpaRepository also includes pagination and sorting features.

// Example: public interface AdminRepository extends JpaRepository<Admin, Long> {}

// 2. Custom Query Method:
//    - **findByUsername**:
//      - This method allows you to find an Admin by their username.
//      - Return type: Admin
//      - Parameter: String username
//      - It will return an Admin entity that matches the provided username.
//      - If no Admin is found with the given username, it returns null.

// Example: public Admin findByUsername(String username);

// 3. Add @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - While it is technically optional (since JpaRepository is a part of Spring Data), it's good practice to include it for clarity.
//    - Spring Data JPA automatically implements the repository, providing the necessary CRUD functionality.

// Example: @Repository
//          public interface AdminRepository extends JpaRepository<Admin, Long> { ... }

}
