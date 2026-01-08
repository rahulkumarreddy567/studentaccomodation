package com.pandalodge.controller;

import com.pandalodge.dao.BookingDAO;
import com.pandalodge.dao.AccommodationDAO;
import com.pandalodge.dao.StudentDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.pandalodge.model.Booking;
import com.pandalodge.model.Accommodation;
import com.pandalodge.util.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Label adminNameLabel;
    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label totalAccommodationsLabel;
    @FXML
    private Label activeBookingsLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private Label availableAccommodationsLabel;
    @FXML
    private Label occupiedAccommodationsLabel;
    @FXML
    private Label maintenanceAccommodationsLabel;
    @FXML
    private TableView<Booking> recentBookingsTable;
    @FXML
    private ComboBox<String> accommodationFilterCombo;
    @FXML
    private VBox contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set admin name
        if (UserSession.isAdmin()) {
            adminNameLabel.setText("Administrator");
        } else if (UserSession.getCurrentStudent() != null) {
            adminNameLabel.setText(UserSession.getCurrentStudent().getName());
        }

        // Initialize room filter combo
        accommodationFilterCombo.setItems(FXCollections.observableArrayList("All Types", "Room", "Studio", "Apartment"));
        accommodationFilterCombo.setValue("All Types");

        // Load dashboard stats
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        try {
            // Count students
            List<?> students = StudentDAO.findAll();
            totalStudentsLabel.setText(String.valueOf(students.size()));

            // Count accommodations
            List<Accommodation> accommodations = AccommodationDAO.findAll();
            totalAccommodationsLabel.setText(String.valueOf(accommodations.size()));

            // Count bookings
            List<Booking> bookings = BookingDAO.findAll();
            activeBookingsLabel.setText(String.valueOf(bookings.size()));

            // Calculate revenue
            double revenue = accommodations.stream().mapToDouble(Accommodation::getPrice).sum();
            revenueLabel.setText("€" + String.format("%.0f", revenue));

            // Accommodation availability (mock data for now)
            int available = (int) (accommodations.size() * 0.6);
            int occupied = (int) (accommodations.size() * 0.35);
            int maintenance = accommodations.size() - available - occupied;

            availableAccommodationsLabel.setText(String.valueOf(Math.max(0, available)));
            occupiedAccommodationsLabel.setText(String.valueOf(Math.max(0, occupied)));
            maintenanceAccommodationsLabel.setText(String.valueOf(Math.max(0, maintenance)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showDashboard() {
        loadDashboardStats();
    }

    @FXML
    public void showStudents() {
        loadView("/com/pandalodge/view/student.fxml", "Students");
    }

    @FXML
    public void showAccommodationManagement() {
        loadView("/com/pandalodge/view/accommodation_management.fxml", "Rooms");
    }

    @FXML
    public void showBookings() {
        loadView("/com/pandalodge/view/bookings.fxml", "Bookings");
    }

    @FXML
    public void showAccommodations() {
        loadView("/com/pandalodge/view/accommodations.fxml", "Accommodations");
    }

    @FXML
    public void showSettings() {
        showAlert("Settings", "Settings panel coming soon!");
    }

    @FXML
    public void showProfile() {
        showAlert("Profile", "Profile management coming soon!");
    }

    @FXML
    public void addStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/student_form.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage dialog = new Stage();
            dialog.setTitle("Add New Student");
            dialog.setScene(scene);
            dialog.setMinWidth(400);
            dialog.setMinHeight(300);
            dialog.showAndWait();

            loadDashboardStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddAccommodation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/accommodation_form.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage dialog = new Stage();
            dialog.setTitle("Add New Accommodation");
            dialog.setScene(scene);
            dialog.setMinWidth(400);
            dialog.setMinHeight(350);
            dialog.showAndWait();

            loadDashboardStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void createBooking() {
        showAlert("Create Booking", "Booking creation wizard coming soon!");
    }

    @FXML
    public void generateReport() {
        showAlert("Generate Report",
                "Report generation feature coming soon!\n\nAvailable reports:\n• Student List\n• Room Occupancy\n• Revenue Summary\n• Booking History");
    }

    @FXML
    public void onLogout() {
        UserSession.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda Stays - Home");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();

            // Set AdminController reference on child controllers for back navigation
            Object controller = loader.getController();
            if (controller instanceof StudentController) {
                ((StudentController) controller).setAdminController(this);
            } else if (controller instanceof AccommodationManagementController) {
                ((AccommodationManagementController) controller).setAdminController(this);
            } else if (controller instanceof BookingController) {
                ((BookingController) controller).setAdminController(this);
            } else if (controller instanceof AccommodationsController) {
                ((AccommodationsController) controller).setAdminController(this);
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda Stays - " + title);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigate back to the admin dashboard. Called from child controllers.
     */
    public void goToAdmin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/admin.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Panda Stays - Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}










