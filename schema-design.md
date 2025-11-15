## MySQL Database Design

### Table: patients
```sql
CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,      -- Primary Key, Auto Increment for unique patient ID
    first_name VARCHAR(100) NOT NULL,        -- First name of the patient, Cannot be NULL
    last_name VARCHAR(100) NOT NULL,         -- Last name of the patient, Cannot be NULL
    email VARCHAR(255) UNIQUE NOT NULL,      -- Email, unique and must be validated later in the application layer
    phone VARCHAR(15),                       -- Phone number, optional, consider validation via code
    date_of_birth DATE NOT NULL,             -- Patient's date of birth, Cannot be NULL
    gender ENUM('Male', 'Female', 'Other') NOT NULL, -- Gender, Cannot be NULL
    address TEXT,                            -- Optional address field
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Auto set the creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Auto set the update timestamp
);
```

### Table:doctors
```sql
CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- Primary Key, Auto Increment for unique doctor ID
    first_name VARCHAR(100) NOT NULL,            -- First name of the doctor, Cannot be NULL
    last_name VARCHAR(100) NOT NULL,             -- Last name of the doctor, Cannot be NULL
    email VARCHAR(255) UNIQUE NOT NULL,          -- Email, unique and should be validated later
    phone VARCHAR(15),                           -- Phone number, optional, consider validation via code
    specialization VARCHAR(100) NOT NULL,        -- Doctor's specialization, Cannot be NULL
    gender ENUM('Male', 'Female', 'Other') NOT NULL,  -- Gender, Cannot be NULL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto set creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Auto set update timestamp
);
```

### Table:appointments
```sql
CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- Primary Key, Auto Increment for unique appointment ID
    doctor_id INT NOT NULL,                     -- Foreign Key, references doctors(id)
    patient_id INT NOT NULL,                    -- Foreign Key, references patients(id)
    appointment_time DATETIME NOT NULL,         -- DateTime of the appointment, Cannot be NULL
    status INT NOT NULL DEFAULT 0,              -- 0 = Scheduled, 1 = Completed, 2 = Cancelled, Cannot be NULL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Auto set the creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Auto set the update timestamp
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,  -- Deletes appointments if doctor is deleted
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE -- Deletes appointments if patient is deleted
);
```

### Table: admin
```sql
CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,         -- Primary Key, Auto Increment for unique admin ID
    username VARCHAR(50) UNIQUE NOT NULL,       -- Admin username, Unique and Cannot be NULL
    password VARCHAR(255) NOT NULL,             -- Admin password, Cannot be NULL (should be hashed)
    email VARCHAR(255) UNIQUE NOT NULL,         -- Admin email, Unique and Cannot be NULL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto set creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Auto set update timestamp
);
```

