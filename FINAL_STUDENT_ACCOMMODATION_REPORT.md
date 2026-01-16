# Student Accommodation Management System (Panda Lodge)

**Project Members:**
- **Rahul Kumar Reddy** (Backend Development & Database Architecture)
- **Meghanath9121** (UI/UX Design & Frontend FXML Implementation)

---

## 1. Project Overview
The **Student Accommodation Management System (Panda Lodge)** is a specialized desktop application designed to streamline the search and booking process for student housing. Built using **Java** and **JavaFX**, it provides a high-performance, visually stunning interface that connects students with property owners across major cities.

### The Purpose:
Finding affordable and safe housing is a major stress factor for university students. Most existing platforms are either generic or cluttered with non-student listings. Panda Lodge solves this by providing a dedicated ecosystem where every feature—from the search filters to the booking logic—is tailored specifically for the student lifestyle and budget.

### How it Works:
The application serves as a sophisticated bridge between the user (Student/Owner) and a local SQLite database.
1. **Storage:** All data (User profiles, property listings, booking history) is stored permanently in a **SQLite** database, ensuring data integrity without the need for a heavy server setup.
2. **Business Logic:** The system automatically calculates availability, validates booking dates, and handles user authentication using secure hashing.
3. **Visualization:** Raw database entries are transformed into interactive **Dynamic Cards** and high-detail **Property Pages**, with integrated Google Maps for location awareness.

### Core Focus Areas:
1. **Premium UI/UX Design:** Using an "Emerald & Slate" professional theme, the interface prioritizes readability and ease of navigation, designed specifically by Meghanath9121 to feel like a modern web app.
2. **Verification & Trust:** By including owner contact details and property "Status" indicators, the app ensures that students are interacting with verified information.
3. **Efficiency:** Features like city-based quick-search buttons and one-click booking requests minimize the time taken to secure a room.

---

## 2. Feature Specification Table
| Feature Category | Feature Name | Description | Technical Implementation |
|:--- |:--- |:--- |:--- |
| **Data Management** | **Real-time Search** | Users can search by city or university name with instant results. | SQL `LIKE` queries with JavaFX `FilteredList` for real-time UI updates. |
|  | **Secure Auth** | Students and Renters can sign up and login securely. | **BCrypt** password hashing with `UserSession` singleton for state management. |
| **Automation** | **Availability Sync** | Automatically marks rooms as "BOOKED" once a request is approved. | Backend SQL `UPDATE` triggers upon booking state changes. |
| **Financial Tools** | **Price Analytics** | Displays "Starting from" prices and handles monthly rent calculations. | Java Double-precision math logic mapped to formatted UI Labels. |
| **Visualization** | **Dynamic Cards** | Renders property listings as beautiful, interactive cards. | Custom FXML `CardController` loaded dynamically into a `FlowPane`. |
|  | **Map Integration** | One-click navigation to the property location on Google Maps. | `Desktop.browse()` API combined with encoded `Latitude/Longitude` coordinates. |
| **Reporting** | **Admin Stats** | Admins view totals for rooms, bookings, and active students. | SQL `COUNT(*)` aggregations displayed via an Overview Dashboard. |

---

## 3. Use Case Diagram Analysis
The system identifies three primary interactions:

1. **Primary Actor: Student**
    - **Authentication:** Securely access their profile.
    - **Discovery:** Browse and filter properties by type (Studio/Room) and city.
    - **Booking:** Submit housing requests and track their status.
2. **Secondary Actor: Renter (Owner)**
    - **Listing Management:** Create and edit property ads.
    - **Lead Tracking:** View student details who are interested in their property.
3. **System Boundary: Automation**
    - **Map Services:** Generates external links for location verification.
    - **Status Engine:** Manages the lifecycle of a room from 'Available' to 'Occupied'.

---

## 4. Class Diagram Analysis
The system follows a strict **Layered MVC (Model-View-Controller)** architecture to ensure code maintainability.

### 1. Model Layer (Data Objects)
- **Accommodation.java:** The core entity containing price, address, and owner info.
- **Booking.java:** Handles the relationship between a Student and a Room.
- **Student/Renter.java:** Manages user-specific profile data.

### 2. Controller Layer (UI Logic)
- **HomeController:** Manages the "Hero" section and popular city shortcuts.
- **AccommodationsController:** Handles the main listing grid and search bar.
- **DetailController:** The most complex controller, managing map loading, image galleries, and booking submissions.
- **CardController:** Logic for individual property card components.

### 3. DAO Layer (Persistence)
- **AccommodationDAO:** Handles all SQL operations for property CRUD.
- **BookingDAO:** Manages the transaction logic for housing requests.
- **DBConnection:** A utility class providing a centralized SQLite connection pool.

---

## 5. Technology Stack
| Category | Technology | Purpose |
|:--- |:--- |:--- |
| **Language** | **Java 21+** | Core logic and application backend. |
| **GUI Framework** | **JavaFX** | Modern UI rendering and FXML layouts. |
| **Database** | **SQLite** | Portable, serverless relational storage. |
| **Build Tool** | **Maven** | Dependency and lifecycle management. |
| **Security** | **BCrypt** | Industrial-grade password encryption. |
| **Icons** | **Ikonli** | Vector-based Material Design icons. |
| **Styling** | **CSS3** | Premium custom look and feel (styles.css). |

---

## 6. Database Design
The database consists of interconnected tables ensuring valid data relationships:
- **accommodations:** Primary table storing property details (type, price, lat/long).
- **students:** Stores student credentials and registration info.
- **bookings:** A junction table linking `student_id` and `accommodation_id`.
- **faqs:** Stores platform support content.
- **photos:** Stores multiple image URLs for each accommodation.

**Functional Integration:**
- **Relational Integrity:** The `accommodation_id` in the bookings table acts as a Foreign Key, ensuring that a booking cannot exist without a valid room.
- **Migration Ready:** The DAO layer includes "Migration Logic" (e.g., `ALTER TABLE` checks) to automatically update the database schema if new features are added.

---
**END OF REPORT**
