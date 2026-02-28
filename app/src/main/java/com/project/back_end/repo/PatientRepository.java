Patient Repository
You'll now implement the repository interface to retrieve patients using their email or phone number for identification and validation.

Open the PatientRepository.java file.
Open PatientRepository.java in IDE

Create a repository for the Patient model by extending JpaRepository. This will allow for basic CRUD operations without needing to implement the methods manually.

Add the following methods:

findByEmail: Find a patient by their email address.

Return type: Patient
Parameter: String email
findByEmailOrPhone: Find a patient using either email or phone number.

Return type: Patient
Parameters: String email, String phone

Hint:

Extend JpaRepository<Patient, Long> to inherit basic CRUD functionality.
Use Spring Data naming conventions to define compound query methods such as findByEmailOrPhone.
    
package com.project.back_end.repo;

public interface PatientRepository {
    // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Patient, Long>, which provides basic CRUD functionality.
//    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
//    - JpaRepository also includes features like pagination and sorting.

// Example: public interface PatientRepository extends JpaRepository<Patient, Long> {}

// 2. Custom Query Methods:

//    - **findByEmail**:
//      - This method retrieves a Patient by their email address.
//      - Return type: Patient
//      - Parameters: String email

//    - **findByEmailOrPhone**:
//      - This method retrieves a Patient by either their email or phone number, allowing flexibility for the search.
//      - Return type: Patient
//      - Parameters: String email, String phone

// 3. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.


}

