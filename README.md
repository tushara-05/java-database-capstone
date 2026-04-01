# Smart Clinic Management System

## Project Description
The Smart Clinic Management System is an easy-to-use web application designed to help healthcare clinics run smoothly. It solves the problem of messy and confusing appointment scheduling by providing a single, organized platform where patients can easily find doctors and book their visits online. The system also gives doctors a clear view of their daily schedules and helps clinic administrators manage staff records and generate detailed performance reports. By replacing manual paperwork with a secure online portal, this tool saves time, prevents booking errors, and makes the healthcare experience better for everyone involved.

## Tech Stack

### **Architecture & Backend Development**
* **Architecture:** MVC (Model-View-Controller) handling RESTful API requests via Spring Web
* **Language:** Java 17
* **Framework:** Spring Boot
* **Data Access:** Spring Data JPA, Spring Data MongoDB
* **Security:** JSON Web Tokens (JWT) for secure authentication
* **Build Tool:** Maven

### **Frontend & User Interface**
* **Template Engine:** Thymeleaf
* **Languages:** Vanilla HTML5, CSS3, JavaScript

### **Databases**
* **Relational Database (MySQL):** Handles structured core data (tables: `admin`, `appointment`, `doctor`, `doctor_available_times`, `patient`).
  * **Stored Procedures:** Used for data analytics: `GetDailyAppointmentReportByDoctor`, `GetDoctorWithMostPatientsByMonth`, and `GetDoctorWithMostPatientsByYear`.
* **NoSQL Database (MongoDB):** Handles flexible medical records specifically for `prescriptions`.

### **Containerization & CI/CD**
* **Containerization:** Docker (Multi-stage builds)
* **Automation (CI/CD):** GitHub Actions (Automated code checks via HTMLHint, StyleLint, and ESLint)
