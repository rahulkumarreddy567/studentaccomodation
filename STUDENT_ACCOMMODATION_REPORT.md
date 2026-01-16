# PROJECT REPORT ON
# STUDENT ACCOMMODATION MANAGEMENT SYSTEM
# (PANDA LODGE)

---

**SUBMITTED BY:**
- **RAHUL KUMAR REDDY** (Backend & Architecture)
- **MEGHANATH9121** (UI/UX Design)

---

## 1. INTRODUCTION
The **Student Accommodation Management System**, branded as **Panda Lodge**, is a comprehensive desktop application designed to bridge the gap between students looking for housing and property owners (renters) who provide accommodation. 

Finding safe, affordable, and convenient housing is one of the biggest challenges for students, especially in large cities. Panda Lodge simplifies this process by providing a verified platform where students can search, view, and book accommodations like studios, shared rooms, and full apartments.

## 2. OBJECTIVES
The primary objectives of this project are:
- To provide a user-friendly interface for students to browse and search for housing based on city, price, and type.
- To enable property owners (renters) to list their properties with detailed descriptions, images, and contact information.
- To implement a secure booking system that tracks the status of student housing requests.
- To develop an administrative dashboard for managing the overall platform, including FAQs and reviews.
- To ensure data persistence using a local SQLite database for portability and speed.

## 3. SYSTEM REQUIREMENTS

### 3.1 Hardware Requirements
- **Processor**: Intel Core i3 or higher (Recommended: i5/i7).
- **RAM**: Minimum 4GB (Recommended: 8GB or higher).
- **Storage**: At least 500MB of free space for the application and database.
- **Display**: 1366x768 resolution or higher.

### 3.2 Software Requirements
- **Operating System**: Windows 10/11, macOS, or Linux.
- **Java Development Kit (JDK)**: JDK 21 or higher.
- **Database**: SQLite (built-in JDBC).
- **Build Tool**: Apache Maven.
- **User Interface Framework**: JavaFX.

## 4. TECHNOLOGY STACK
- **Java**: The core programming language used for the backend logic and data handling.
- **JavaFX**: A powerful framework for creating modern, responsive transitions and elegant user interfaces.
- **SQLite**: A lightweight, serverless, and self-contained database engine used for storing all application data.
- **Maven**: Dependency management and build automation.
- **BCrypt**: Used for secure password hashing and user authentication.
- **CSS**: Custom styling for JavaFX components to achieve a premium look and feel.

## 5. SYSTEM DESIGN & ARCHITECTURE

### 5.1 Architecture: Model-View-Controller (MVC)
The project follows the standard **MVC Architecture**:
- **Model**: Represents the data structures (e.g., `Student`, `Accommodation`, `Booking`).
- **View**: The front-end layouts designed in FXML and styled with CSS.
- **Controller**: The bridge between the UI and the logic, handling user actions and database calls.

### 5.2 Database Schema
The system utilizes several tables to store information:
1. **accommodations**: Stores details like type, price, address, owner info, and status.
2. **students**: Stores student profiles, emails, and hashed passwords.
3. **bookings**: Tracks booking requests including start/end dates and status (PENDING, APPROVED, etc.).
4. **renters**: Stores property owner details.
5. **faqs**: Manages platform help and support information.
6. **reviews**: Stores user feedback on accommodations.

## 6. KEY MODULES

### 6.1 Student Module
- **Search & Filter**: Find housing by city or type (Studio, Room, Apartment).
- **Detail View**: View HD images, interactive map locations (Google Maps), and owner contact info.
- **Booking Request**: Submit housing requests for specific dates.
- **Profile Management**: Update personal details and track booking status.

### 6.2 Admin Module
- **Overview Dashboard**: View system statistics (Total Students, Rooms, Bookings).
- **FAQ Management**: Create and edit platform help content.
- **Database Management**: Oversee all listings and user accounts.

### 6.3 Renter Module
- **Property Listing**: Post new ads with detailed descriptions and images.
- **Management**: View and update the status of listed properties.

## 7. DATABASE DESIGN (ER MODEL)
The database design ensures referential integrity between students and their bookings.
- **One-to-Many Relationship**: One student can have multiple bookings.
- **Many-to-One Relationship**: Multiple bookings can refer to the same accommodation.
- **Identity Tracking**: Uses `id` as the primary key for all tables with `AUTOINCREMENT` for uniqueness.

## 8. IMPLEMENTATION DETAILS
- **Navigation Flow**: Seamless transition between Home, Login, and Detailed views using a central stage management.
- **Security**: All user passwords are encrypted using **BCrypt** before being stored in the database.
- **Map Integration**: Google Maps URLs are dynamically generated based on the property's latitude and longitude.
- **Responsive Design**: The UI adapts to different screen sizes while maintaining a premium aesthetic.

## 9. CONCLUSION
The **Panda Lodge Student Accommodation System** is a robust and scalable solution for modern student housing needs. By leveraging JavaFX for a beautiful UI and SQLite for reliable data storage, it provides a professional-grade platform that is both efficient and easy to use. The clear separation of concerns through the MVC pattern ensures the code is maintainable and ready for future enhancements.

## 10. FUTURE SCOPE
- **Real-time Messaging**: Implementing a chat system between students and owners.
- **Cloud Database**: Migrating to PostgreSQL or MySQL for remote access.
- **Payment Gateway**: Integration of Stripe or PayPal for secure online rent payments.
- **Multi-language Support**: Expanding the platform for international students in multiple languages.

---
**END OF REPORT**
