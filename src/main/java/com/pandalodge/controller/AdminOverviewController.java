package com.pandalodge.controller;

import com.pandalodge.dao.BookingDAO;
import com.pandalodge.dao.AccommodationDAO;
import com.pandalodge.dao.StudentDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.pandalodge.model.Booking;
import com.pandalodge.model.Accommodation;
import com.pandalodge.util.UserSession;

import java.io.IOException;
import java.util.List;

public class AdminOverviewController {

    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label totalAccommodationsLabel;
    @FXML
    private Label activeBookingsLabel;

    @FXML
    private Label availableLabel;
    @FXML
    private Label occupiedLabel;
    @FXML
    private Label maintenanceLabel;

    @FXML
    private TableView<Booking> recentBookingsTable;
    @FXML
    private TableColumn<Booking, String> colStudent;
    @FXML
    private TableColumn<Booking, String> colRoom;
    @FXML
    private TableColumn<Booking, String> colDate;
    @FXML
    private TableColumn<Booking, String> colStatus;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadStats();
    }

    private void setupTable() {
        colStudent.setCellValueFactory(data -> {
            com.pandalodge.model.Student s = StudentDAO.findById(data.getValue().getStudentId());
            return new SimpleStringProperty(s != null ? s.getName() : "Unknown");
        });
        colRoom.setCellValueFactory(data -> {
            Accommodation a = AccommodationDAO.findById(data.getValue().getAccommodationId());
            return new SimpleStringProperty(a != null ? a.getType() + " - " + a.getAddress() : "Unknown");
        });
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartDate()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    public void loadStats() {
        try {
            totalStudentsLabel.setText(String.valueOf(StudentDAO.findAll().size()));
            List<Accommodation> accs = AccommodationDAO.findAll();
            totalAccommodationsLabel.setText(String.valueOf(accs.size()));

            List<Booking> bookings = BookingDAO.findAll();
            activeBookingsLabel.setText(String.valueOf(bookings.size()));
            recentBookingsTable.setItems(FXCollections.observableArrayList(
                    bookings.subList(0, Math.min(bookings.size(), 10))));

            long available = accs.stream().filter(a -> "AVAILABLE".equalsIgnoreCase(a.getStatus())).count();
            long occupied = accs.stream().filter(a -> "OCCUPIED".equalsIgnoreCase(a.getStatus())).count();

            availableLabel.setText(String.valueOf(available));
            occupiedLabel.setText(String.valueOf(occupied));
            maintenanceLabel.setText(String.valueOf(accs.size() - available - occupied));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddAccommodation() {
        if (dashboardController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/pandalodge/view/accommodation_form.fxml"));
                Stage dialog = new Stage();
                dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                dialog.setTitle("Add New Accommodation");
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                AccommodationFormController c = loader.getController();
                c.setStage(dialog);
                dialog.setScene(scene);
                dialog.showAndWait();
                if (c.isSaved()) {
                    loadStats();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/student_form.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.setTitle("Add New Student");

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            StudentFormController c = loader.getController();
            c.setStage(dialog);

            dialog.setScene(scene);
            dialog.showAndWait();

            if (c.isSaved()) {
                loadStats();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAllBookings() {
        if (dashboardController != null) {
            dashboardController.showAdminBookings();
        }
    }
}
