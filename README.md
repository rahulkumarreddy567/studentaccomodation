# Panda Lodge - Student Accommodation Management System

A JavaFX-based application for managing student accommodations with features for students, room owners, and administrators.

![Java](https://img.shields.io/badge/Java-21+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21+-blue)
![SQLite](https://img.shields.io/badge/SQLite-Database-green)

## 🏠 Features

### For Students
- **User Registration & Login** - Secure authentication system
- **Browse Accommodations** - Search and filter rooms by type, location, and price
- **Room Details** - View photos, amenities, and location maps
- **Booking System** - Book accommodations with date selection
- **My Profile** - View and manage personal information
- **My Bookings** - Track booking status and history

### For Room Owners
- **Manage Listings** - Add, edit, and remove accommodation listings
- **View Bookings** - See all booking requests for your properties
- **Owner Dashboard** - Overview of all properties and bookings

### For Administrators
- **Admin Dashboard** - Overview of system statistics
- **Student Management** - View, add, edit, and delete students
- **Accommodation Management** - Manage all listings
- **Booking Management** - View and manage all bookings
- **Room Owner Management** - Manage property owners

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/pandalodge/
│   │       ├── application/     # Main application entry point
│   │       ├── controller/      # FXML controllers
│   │       ├── dao/            # Data Access Objects
│   │       ├── model/          # Entity classes
│   │       └── util/           # Utility classes
│   └── resources/
│       ├── com/pandalodge/view/ # FXML files
│       └── styles.css          # CSS styling
```

## 🚀 Prerequisites

- **JDK 21** or higher
- **JavaFX SDK 21** or higher
- **Maven** (or use the included Maven wrapper)

## 📦 Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/accommodation-system.git
   cd accommodation-system
   ```

2. **Download JavaFX SDK:**
   - Download from [Gluon](https://gluonhq.com/products/javafx/)
   - Extract to a folder (e.g., `C:\javafx-sdk-21`)

3. **Set environment variables:**
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
   $env:PATH_TO_FX = "C:\javafx-sdk-21\lib"
   ```

4. **Build and run:**
   ```bash
   # Windows
   .\mvnw.cmd clean javafx:run
   
   # Linux/Mac
   ./mvnw clean javafx:run
   ```

## 🔐 Default Credentials

### Admin Access
- **Email:** admin@panda.com
- **Password:** admin123

### Test Student
- Create a new account via the Sign Up page

## 🗃️ Database

The application uses **SQLite** for data persistence. The database file is created automatically at `data/sa.db` on first run.

### Database Schema
- **students** - Student information
- **admins** - Administrator accounts
- **accommodations** - Room listings
- **bookings** - Booking records
- **renters** - Room owner information
- **reviews** - User reviews
- **photos** - Accommodation photos
- **payments** - Payment records
- **faqs** - Frequently asked questions

## 🎨 Technologies Used

- **JavaFX 21** - UI Framework
- **FXML** - UI Layout
- **CSS** - Styling
- **SQLite** - Database
- **Maven** - Build tool

## 📸 Screenshots

### Login Page
Modern login interface with student and admin access.

### Home Dashboard
Browse available accommodations with search and filters.

### Room Details
Detailed view with photos, amenities, and booking form.

### Admin Panel
Comprehensive admin dashboard for system management.

## 🔧 Configuration

### JavaFX SDK Path
If you have JavaFX SDK installed in a different location, update the path in:
- `pom.xml` - `javafx.sdk.path` property

### Database Location
The SQLite database is stored at:
- `data/sa.db`

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👤 Author

**Student Accommodation Team**

## 🙏 Acknowledgments

- JavaFX community
- SQLite team
- All contributors

