# Panda Lodge - Student Accommodation Management System

A premium, Studapart-inspired JavaFX application designed to simplify the student housing search across Europe. This system provides a unified platform for students, property owners, and administrators with a focus on modern aesthetics and user-friendly navigation.

![Java](https://img.shields.io/badge/Java-21+-orange?style=for-the-badge&logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-21+-blue?style=for-the-badge&logo=java)
![SQLite](https://img.shields.io/badge/SQLite-Database-green?style=for-the-badge&logo=sqlite)

## ✨ Modern UI & Aesthetics
The application features a complete design overhaul incorporating:
- **Glassmorphism & Gradients**: Premium visual cards and background depths.
- **Responsive Sidebar**: Dynamic navigation hub with role-based access control.
- **Unified Transitions**: Smooth fade-in effects for content switching.
- **Tailored Components**: Custom-styled buttons, tags, and booking cards.

## 🏠 Core Features

### 🎓 For Students
- **Smart Browse**: Filter accommodations by type, location, and price.
- **Visual Listings**: Rich accommodation cards with pricing and status indicators.
- **Seamless Booking**: Simple one-click booking process for verified rooms.
- **Minimalist Profile**: streamlined identity view showing essential account details.
- **Booking History**: Track the status of all current and past rental requests.

### 🏠 For Property Owners (Renters)
- **Performance Tracking**: Overview of listing engagement and booking stats.
- **Property Management**: Simplified forms for adding and managing listings.
- **Rental Requests**: Direct access to incoming student booking applications.

### 📊 For Administrators
- **System Overview**: High-level statistics on platform activity.
- **Student & Owner Management**: Full CRUD operations for platform users.
- **Global Moderation**: Manage all listings, reviews, and bookings.
- **Content Management**: Update FAQs and support documentation.

## 🏗️ Technical Architecture

```
src/main/java/com/pandalodge/
├── application/     # Application Entry Point & Global Config
├── controller/      # MVC Controllers (Logic Layer)
├── dao/            # Data Access Objects (SQLite Integration)
├── model/          # Entity Data Models
└── util/           # Session Management & Security Helpers
```

## 🚀 Getting Started

### Prerequisites
- **JDK 21+**
- **JavaFX SDK 21+**
- **Maven**

### Installation & Run
1. **Clone the repository**
2. **Configure path**: Update `pom.xml` with your local `javafx.sdk.path`.
3. **Execute**:
   ```powershell
   .\mvnw.cmd clean javafx:run
   ```

## 🔐 Credentials (Demo)

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@panda.com` | `admin123` |
| **Owner** | `marie.dubois@gmail.com` | `password123` |
| **Student** | *Create via Signup* | *Any* |

## 🗃️ Database & Persistence
The system uses **SQLite** for zero-configuration persistence. The database is automatically initialized at `data/sa.db` with migration-ready schemas for accommodations, bookings, renters, and reviews.

---
*Created with ❤️ for the Student Accommodation Community.*
