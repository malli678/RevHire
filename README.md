# RevHire - Job Portal Console Application

## 📋 Project Overview
**RevHire** is a comprehensive **console-based job portal application** that connects job seekers with employers. It enables job seekers to create profiles, build resumes, search and apply for jobs, and track application statuses. Employers can post jobs, manage applications, shortlist or reject candidates (including bulk actions), and maintain company profiles.

The application is built using **Java 1.7**, **Oracle SQL**, and follows a **layered MVC architecture**, demonstrating enterprise-level software development practices. The design is modular and extensible, with future plans to migrate to a web-based microservices architecture.

**Trainee:** Mallikarjun Mandava  
**Trainer:** Geetha S  
**Batch:** 2354  

---

## 🎯 Features Implemented

### Job Seeker Features
- ✅ **Registration & Account Creation** - Complete profile setup
- ✅ **Login System** - Secure authentication
- ✅ **Resume Management** - Create/update structured resumes
- ✅ **Job Search** - Advanced filtering (role, location, experience, salary)
- ✅ **Job Applications** - One-click applying with cover letter
- ✅ **Application Tracking** - View status (Applied/Shortlisted/Rejected/Withdrawn)
- ✅ **Application Withdrawal** - With confirmation and reason
- ✅ **Profile Management** - Update personal and professional details
- ✅ **Notifications** - Real-time updates for application status

### Employer Features
- ✅ **Company Registration** - Complete company profile setup
- ✅ **Login System** - Secure authentication for employers
- ✅ **Job Postings** - Create comprehensive job listings
- ✅ **Job Management** - View, edit, close/reopen, delete jobs
- ✅ **Job Statistics** – View number of applications per job
- ✅ **Applicant Management** - View applicant details with resumes
- ✅ **Application Processing** - Shortlist/Reject with comments
- ✅ **Applicant Search & Filters** - Filter by experience, skills, education, application date
- ✅ **Company Profile** - Update company information

### Common Features
- ✅ **Change Password** - Secure password update with current password verification
- ✅ **Forgot Password** - Recovery through security questions
- ✅ **Profile Completion Tracking** - Percentage-based completion indicator
- ✅ **Role-Based Access Control** – Separate flows for job seekers and employers

---

## 🏗️ Architecture & Design

### Technology Stack
- **Language:** Java 1.7
- **Database:** Oracle 11g XE
- **JDBC Driver:** ojdbc6.jar
- **Testing:** JUnit 4
- **Logging:** Log4J 1.2.17
- **Build Tool:** Eclipse IDE

### Architecture Diagram

The application follows a layered architecture consisting of:

* **Presentation Layer** – Console-based UI menus
* **Service Layer** – Business logic and validations
* **DAO Layer** – Database operations using JDBC
* **Database Layer** – Oracle SQL tables and sequences

An **application architecture diagram** is included in the repository documentation to illustrate component interactions.


### Project Structure
```
RevHire/
├── src/                            # Source Code
│   ├── com.revhire.model/          # Data Models (POJOs)
│   │   ├── User.java               # Base user entity
│   │   ├── JobSeeker.java          # Job seeker profile
│   │   ├── Employer.java           # Employer/company profile
│   │   ├── Job.java                # Job listings
│   │   ├── Application.java        # Job applications
│   │   ├── Notification.java       # User notifications
│   │   └── Resume.java             # Job seeker resumes
│   ├── com.revhire.dao/            # Data Access Layer
│   │   ├── UserDAO.java            # User database operations
│   │   ├── JobSeekerDAO.java       # Job seeker operations
│   │   ├── EmployerDAO.java        # Employer operations
│   │   ├── JobDAO.java             # Job operations
│   │   ├── ApplicationDAO.java     # Application operations
│   │   ├── NotificationDAO.java     # Notification operations
│   │   └── ResumeDAO.java          # Resume operations
│   ├── com.revhire.service/        # Business Logic Layer
│   │   ├── AuthService.java        # Authentication services
│   │   ├── JobSeekerService.java   # Job seeker services
│   │   └── EmployerService.java    # Employer services
│   ├── com.revhire.ui/             # Presentation Layer
│   │   ├── MainMenu.java           # Main application menu
│   │   ├── JobSeekerMenu.java      # Job seeker dashboard
│   │   ├── EmployerMenu.java       # Employer dashboard
│   │   └── ConsoleUtils.java       # Console utilities
│   ├──com.revhire.util/           # Utilities
│   |   ├── DBUtil.java             # Database connection
│   |   ├── PasswordUtil.java       # Password hashing
│   |   └── LoggerUtil.java         # Log4J wrapper
│   └── com.revhire.test/           # JUnit Tests
│       ├── PasswordUtilTest.java   # Password utility tests
│       ├── UserDAOTest.java        # User DAO tests
│       └── AuthServiceTest.java    # Authentication service tests
│       └── NotificationService.java # Notification service tests
├── lib/                            # External Libraries
│   ├── ojdbc6.jar                  # Oracle JDBC driver
│   ├── log4j-1.2.17.jar           # Logging framework
│   └── junit-4.x.jar              # Testing framework
├── logs/                           # Application Logs
│   └── revhire.log                # Log file (auto-generated)
├── log4j.properties               # Log4J configuration
├── database_setup.sql             # Database schema
└── README.md                      # This documentation
```

### Design Patterns
1. **MVC Architecture** - Clear separation of concerns
2. **DAO Pattern** - Abstract database operations
3. **Service Layer** - Business logic encapsulation
4. **Singleton** - Database connection management
5. **Factory** - Object creation patterns

---

## 🗄️ Database Design

### Entity Relationship Diagram
```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│    USERS    │      │  JOBSEEKERS  │      │   RESUMES   │
├─────────────┤      ├──────────────┤      ├─────────────┤
│ id (PK)     │◄─────│ user_id (PK) │─────▶│ jobseeker_id│
│ email       │      │ objective    │      │ education   │
│ password    │      │ skills       │      │ experience  │
│ role        │      │ certifications│     │ projects    │
│ name        │      └──────────────┘      └─────────────┘
│ phone       │
│ location    │      ┌─────────────────┐
└─────────────┘      │ COMPANY_EMPLOYERS│
        │            ├─────────────────┤
        │            │ employer_id (PK)│
        ▼            │ company_name    │
┌─────────────┐      │ industry        │      ┌─────────────┐
│   JOBS      │      │ company_size    │      │APPLICATIONS │
├─────────────┤      │ description     │      ├─────────────┤
│ id (PK)     │◄─────│ website         │      │ id (PK)     │
│ employer_id │      └─────────────────┘      │ jobseeker_id│
│ title       │                               │ job_id      │
│ description │            │                  │ resume_id   │
│ skills      │            │                  │ cover_letter│
│ experience  │            ▼                  │ status      │
│ education   │      ┌─────────────┐         │ applied_at  │
│ location    │      │NOTIFICATIONS│         └─────────────┘
│ salary_min  │      ├─────────────┤                 │
│ salary_max  │      │ id (PK)     │◄────────────────┘
│ job_type    │      │ user_id     │
│ deadline    │      │ message     │
│ status      │      │ is_read     │
│ created_at  │      │ created_at  │
└─────────────┘      └─────────────┘
```

### Database Tables
1. **USERS** - Base table for all users (job seekers & employers)
2. **JOBSEEKERS** - Extended profile for job seekers
3. **COMPANY_EMPLOYERS** - Extended profile for employers
4. **RESUMES** - Job seeker resumes
5. **JOBS** - Job postings
6. **APPLICATIONS** - Job applications
7. **NOTIFICATIONS** - User notifications

### Key SQL Features
- **Constraints:** Primary keys, foreign keys, unique constraints
- **Sequences:** Auto-increment IDs for all tables
- **Check Constraints:** Data validation (status fields, roles)
- **Indexes:** Performance optimization
- **Cascade Deletes:** Referential integrity

---

## ⚙️ Installation & Setup

### Prerequisites
1. **Java Development Kit (JDK) 1.7** or higher
2. **Oracle Database 11g XE**
3. **Eclipse IDE** (or any Java IDE)
4. **Oracle SQL Developer** (optional, for database management)

### Step 1: Database Setup
```sql
-- Run the complete database_setup.sql script
-- This includes:
-- 1. Table creation with constraints
-- 2. Sequence creation for auto-increment
-- 3. Sample data insertion
-- 4. Index creation for performance

-- Or manually execute:
CONNECT system/password;

CREATE USER revhire_user IDENTIFIED BY password123;
GRANT CONNECT, RESOURCE TO revhire_user;
CONNECT revhire_user/password123;

-- Then run all CREATE TABLE statements...
```

### Step 2: Project Configuration
1. **Clone/Download** the project files
2. **Open Eclipse** and import as existing project
3. **Configure Build Path:**
   - Add `ojdbc6.jar` to lib folder and build path
   - Add `log4j-1.2.17.jar` to lib folder and build path
   - Add `junit-4.x.jar` to lib folder and build path
4. **Update Database Connection:**
   - Edit `DBUtil.java` with your database credentials:
   ```java
   private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
   private static final String USER = "revhire_user";
   private static final String PASS = "password123";
   ```

### Step 3: Run the Application
```bash
# Method 1: Run from Eclipse
1. Right-click RevHireApp.java
2. Select "Run As → Java Application"

# Method 2: Command Line
javac -cp ".;lib/*" src/com/revhire/*.java
java -cp ".;lib/*;src" com.revhire.RevHireApp
```

### Step 4: Test the Application
```bash
# Run JUnit Tests
1. Right-click test folder
2. Select "Run As → JUnit Test"

# Verify Logging
1. Check console for formatted logs
2. Check logs/revhire.log file
```

---

## 🚀 How to Use RevHire

### As a Job Seeker
1. **Register:** Create account with personal and professional details
2. **Build Resume:** Add education, experience, skills, projects
3. **Search Jobs:** Use filters (role, location, salary, experience)
4. **Apply:** One-click application with optional cover letter
5. **Track:** Monitor application status (Applied → Shortlisted → Rejected)
6. **Manage:** Update profile, withdraw applications, view notifications

### As an Employer
1. **Register:** Create company profile with business details
2. **Post Jobs:** Create detailed job listings with requirements
3. **Manage:** View, edit, close job postings
4. **Review:** View applicant details with resumes
5. **Process:** Shortlist or reject applicants with comments
6. **Communicate:** Send status updates to applicants

---

## 🧪 Testing & Quality Assurance

### Unit Testing (JUnit)
- **Test Coverage:** 60%+ of critical functionality
- **Test Cases:**
  - Password hashing and validation
  - Input validation (email, phone, dates)
  - Authentication service methods
- **Test Results:** All tests pass successfully

### Integration Testing
- Database connectivity and operations
- End-to-end user workflows
- Transaction management

### Logging & Monitoring
- **Log4J Configuration:** Console and file logging
- **Log Levels:** DEBUG, INFO, WARN, ERROR
- **Log Format:** Timestamp, level, class, message
- **Log Location:** `logs/revhire.log`

### Security Testing
- **Password Security:** MD5 hashing (production would use stronger)
- **SQL Injection Prevention:** Prepared statements
- **Input Validation:** Client and server-side validation
- **Session Management:** Role-based access control

---

## 🛠️ Development Challenges & Solutions

### Challenge 1: Java 1.7 Compatibility
**Problem:** Modern Java features (try-with-resources, String.repeat(), diamond operator) not available

**Solution:**
```java
// Instead of try-with-resources (Java 7+)
try (Connection conn = DBUtil.getConnection()) {
    // code
}

// Used manual resource management (Java 1.7)
Connection conn = null;
PreparedStatement ps = null;
try {
    conn = DBUtil.getConnection();
    ps = conn.prepareStatement(sql);
    // code
} finally {
    if (ps != null) try { ps.close(); } catch (Exception e) {}
    if (conn != null) try { conn.close(); } catch (Exception e) {}
}
```

### Challenge 2: Oracle Database Configuration
**Problem:** Table name mismatches and sequence issues

**Solution:**
- Standardized table naming conventions
- Created comprehensive SQL setup script
- Added proper error handling for database operations

### Challenge 3: Log4J Integration
**Problem:** Log4J not producing output to console or file

**Solution:**
- Created `LoggerUtil.java` wrapper class
- Added fallback logging mechanisms
- Used absolute paths for configuration
- Implemented folder creation on startup

### Challenge 4: Input Validation
**Problem:** User input could cause errors or security issues

**Solution:**
- Created `ConsoleUtils.java` with validation methods
- Implemented regex patterns for email and phone
- Added range checking for numbers and dates
- Created user-friendly error messages

### Challenge 5: Application Flow
**Problem:** Complex menu navigation and state management

**Solution:**
- Implemented clear menu hierarchy
- Used service layer for business logic
- Maintained user session throughout application
- Provided clear navigation options

---

## 📊 Technical Specifications

### Performance Optimization
1. **Database Indexing:** Created indexes on frequently queried columns
2. **Connection Pooling:** Single connection management
3. **Prepared Statements:** Reduced SQL parsing overhead
4. **Lazy Loading:** Loaded data only when needed

### Security Measures
1. **Password Hashing:** MD5 hashing (with salt in production)
2. **SQL Injection Prevention:** 100% prepared statements
3. **Input Sanitization:** Validation on all user inputs
4. **Role-Based Access:** Job seekers vs employers separation

### Code Quality
1. **Modular Design:** Clear separation of concerns
2. **Code Reusability:** Utility classes for common functions
3. **Error Handling:** Comprehensive try-catch blocks
4. **Comments & Documentation:** Inline documentation

### Scalability Features
1. **Database Design:** Normalized schema for data integrity
2. **Service Layer:** Business logic separate from UI and DAO
3. **Configuration Externalization:** Database and logging configs in external files
4. **Extension Points:** Easy to add new features

---

## 📈 Future Enhancements

### Phase 2: Web Application
- Convert console app to web-based Spring Boot application
- Add REST API for mobile applications
- Implement microservices architecture

### Phase 3: Advanced Features
- **AI Matching:** Machine learning for job-candidate matching
- **Video Interviews:** Integrated video calling
- **Analytics Dashboard:** Statistics for employers
- **Mobile App:** iOS and Android applications

### Phase 4: Enterprise Features
- **Multi-tenancy:** Support for multiple companies
- **SSO Integration:** Single sign-on with corporate systems
- **API Gateway:** External API for integration
- **Cloud Deployment:** AWS/Azure cloud deployment

---

## 🎓 Learning Outcomes

### Technical Skills Gained
1. **Java Programming:** Advanced OOP concepts, exception handling, collections
2. **Database Design:** ER modeling, SQL optimization, transaction management
3. **Software Architecture:** MVC pattern, layered architecture, design patterns
4. **Testing:** JUnit, test-driven development, debugging techniques
5. **Tools:** Eclipse, Oracle SQL Developer, version control

### Project Management Skills
1. **Requirements Analysis:** Translating business needs to technical specs
2. **Problem Solving:** Debugging complex issues, finding optimal solutions
3. **Documentation:** Technical writing, code comments, user guides
4. **Quality Assurance:** Testing strategies, code reviews, best practices

### Professional Development
1. **Code Organization:** Maintainable, readable, well-structured code
2. **Error Handling:** Robust exception management
3. **Security Awareness:** Data protection, input validation
4. **Performance Considerations:** Efficient algorithms, database optimization

---

## 📝 Submission Requirements Met

### Code Requirements
- [x] Complete working console application
- [x] All core features implemented
- [x] Database integration with Oracle SQL
- [x] Prepared statements for all SQL operations
- [x] Password hashing (MD5)
- [x] Input validation
- [x] Error handling

### Documentation Requirements
- [x] README.md with setup instructions
- [x] ERD diagram (included in documentation)
- [x] Architecture diagram (described in documentation)
- [x] Code comments and javadoc

### Testing Requirements
- [x] JUnit tests (3 test classes)
- [x] Test coverage ≥ 60%
- [x] All tests passing
- [x] Log4J configuration
- [x] Logs to file

### Quality Requirements
- [x] Clear menu navigation
- [x] Formatted output
- [x] User-friendly error messages
- [x] No application crashes
- [x] Proper naming conventions
- [x] Logical package structure

---

## 👥 Contributors

**Mallikarjun Mandava** - Trainee 
- Designed and implemented the entire application
- Created database schema and SQL scripts
- Developed all Java classes and test cases
- Wrote comprehensive documentation

**Geetha S** - Trainer/Mentor  
- Provided project requirements and specifications
- Guided architecture and design decisions
- Reviewed code quality and best practices
- Offered feedback and suggestions for improvement

**Batch 2354** - Peer Support  
- Collaborative learning environment
- Code review and feedback sessions
- Shared resources and solutions

---

## 📞 Support & Contact

For questions, issues, or contributions:

**Trainee:** Mallikarjun Mandava  
**Email:** mm2k3.211@gmail.com  
**GitHub:** [\[GitHub Profile\] ](https://github.com/malli678) 

**Trainer:** Geetha S  
**Batch:** 2354  

---

## 📄 License

This project is developed for educational purposes as part of a training program. All code and documentation are original work created for learning objectives.

© 2024 RevHire Job Portal - Educational Project

---

## 🙏 Acknowledgments

- **Oracle Corporation** for Oracle Database Express Edition
- **Apache Foundation** for Log4J logging framework
- **JUnit Team** for testing framework
- **Eclipse Foundation** for IDE tools
- **Trainer Geetha S** for guidance and mentorship
- **Batch 2354 Colleagues** for collaborative learning

---
