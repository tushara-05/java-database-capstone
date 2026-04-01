## MySQL Database Design
The relational database handles core operational entities using MySQL. 
### Table: patient
Stores personal information for clinic patients.
- **id**: `BIGINT`, Primary Key, Auto Increment
- **name**: `VARCHAR(100)`, Not Null
- **email**: `VARCHAR(150)`, Not Null
- **password**: `VARCHAR(255)`, Not Null  
- **phone**: `VARCHAR(10)`, Not Null 
- **address**: `VARCHAR(255)`, Not Null
### Table: doctor
Stores information about medical professionals.
- **id**: `BIGINT`, Primary Key, Auto Increment
- **name**: `VARCHAR(100)`, Not Null
- **speciality**: `VARCHAR(50)`, Not Null
- **email**: `VARCHAR(150)`, Not Null
- **password**: `VARCHAR(255)`, Not Null
- **phone**: `VARCHAR(10)`, Not Null
### Table: doctor_available_times
Join table generated for the `@ElementCollection` in the `Doctor` model.
- **doctor_id**: `BIGINT`, Foreign Key → `doctor(id)`, On Delete Cascade
- **available_times**: `VARCHAR(255)`
### Table: appointment
Manages the scheduling of consultations.
- **id**: `BIGINT`, Primary Key, Auto Increment
- **doctor_id**: `BIGINT`, Foreign Key → `doctor(id)`
- **patient_id**: `BIGINT`, Foreign Key → `patient(id)`
- **appointment_time**: `DATETIME`, Not Null
- **status**: `INT` (0 = Scheduled, 1 = Completed)
### Table: admin
System administrators for managing system settings.
- **id**: `BIGINT`, Primary Key, Auto Increment
- **username**: `VARCHAR(255)`, Not Null
- **password**: `VARCHAR(255)`, Not Null
---
## MongoDB Collection Design
MongoDB is used for medical records requiring flexibility. 
### Collection: prescriptions
Captures medical details from clinic visits.
**Example Document:**
```json
{
  "_id": "64abc1234567890abcdef123",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours."
}
```
