package com.pandalodge.controller;

import com.pandalodge.dao.BookingDAO;
import com.pandalodge.dao.AccommodationDAO;
import com.pandalodge.dao.StudentDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import com.pandalodge.model.Booking;
import com.pandalodge.model.Accommodation;
import com.pandalodge.model.Student;
import com.pandalodge.util.UserSession;

import java.util.List;
import java.util.Optional;

public class BookingController {
    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, Integer> idCol;
    @FXML
    private TableColumn<Booking, String> roomCol;
    @FXML
    private TableColumn<Booking, String> dateCol;
    @FXML
    private TableColumn<Booking, String> statusCol;
    @FXML
    private Label titleLabel;

    private DashboardController dashboardController;
    private AdminController adminController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
        System.out.println("DEBUG BookingController: dashboardController set = " + (dash != null));
    }

    public void setAdminController(AdminController admin) {
        this.adminController = admin;
        System.out.println("DEBUG BookingController: adminController set = " + (admin != null));
    }

    @FXML
    public void onBack() {
        System.out.println("DEBUG BookingController.onBack() called");
        if (dashboardController != null) {
            dashboardController.showAccommodations();
        } else if (adminController != null) {
            // Navigate back to admin dashboard
            javafx.stage.Stage stage = (javafx.stage.Stage) bookingTable.getScene().getWindow();
            adminController.goToAdmin(stage);
        } else {
            // Fallback: navigate to home page if no controller is set
            System.err.println("WARN: No parent controller set in BookingController.onBack(), navigating to home");
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                javafx.stage.Stage stage = (javafx.stage.Stage) bookingTable.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - Home");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize() {
        boolean isAdmin = UserSession.isAdmin();

        // Update title based on role
        if (titleLabel != null) {
            titleLabel.setText(isAdmin ? "All Booking Requests" : "My Bookings");
        }

        // Clear existing columns and rebuild
        bookingTable.getColumns().clear();

        // ID Column
        TableColumn<Booking, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        // Student Column (only for admin)
        TableColumn<Booking, String> studentCol = null;
        if (isAdmin) {
            studentCol = new TableColumn<>("Student");
            studentCol.setCellValueFactory(data -> {
                Student s = StudentDAO.findById(data.getValue().getStudentId());
                return new SimpleStringProperty(s != null ? s.getName() + " (" + s.getEmail() + ")" : "Unknown");
            });
            studentCol.setPrefWidth(200);
        }

        // Accommodation Column
        TableColumn<Booking, String> roomColumn = new TableColumn<>("Accommodation");
        roomColumn.setCellValueFactory(data -> {
            Accommodation a = AccommodationDAO.findById(data.getValue().getAccommodationId());
            return new SimpleStringProperty(a != null ? a.getType() + " - " + a.getAddress() : "Unknown Accommodation");
        });
        roomColumn.setPrefWidth(220);

        // Date Column with formatted dates
        TableColumn<Booking, String> dateColumn = new TableColumn<>("Check-in / Check-out");
        dateColumn.setCellValueFactory(data -> {
            String start = data.getValue().getStartDate();
            String end = data.getValue().getEndDate();
            try {
                java.time.LocalDate startDate = java.time.LocalDate.parse(start);
                java.time.LocalDate endDate = java.time.LocalDate.parse(end);
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy");
                return new SimpleStringProperty(startDate.format(fmt) + " → " + endDate.format(fmt));
            } catch (Exception e) {
                return new SimpleStringProperty(start + " → " + end);
            }
        });
        dateColumn.setPrefWidth(200);

        // Duration Column
        TableColumn<Booking, String> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(data -> {
            return new SimpleStringProperty(data.getValue().getDurationFormatted());
        });
        durationColumn.setPrefWidth(100);
        durationColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("📅 " + item);
                    setStyle("-fx-text-fill: #6366f1; -fx-font-weight: bold;");
                }
            }
        });

        // Booked On Column (when booking was made)
        TableColumn<Booking, String> bookedOnColumn = new TableColumn<>("Booked On");
        bookedOnColumn.setCellValueFactory(data -> {
            return new SimpleStringProperty(data.getValue().getCreatedAtFormatted());
        });
        bookedOnColumn.setPrefWidth(140);
        bookedOnColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                }
            }
        });

        // Status Column with color
        TableColumn<Booking, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "PENDING" -> setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                        case "APPROVED" -> setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                        case "REJECTED" -> setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #64748b;");
                    }
                }
            }
        });

        // Actions Column (only for admin)
        TableColumn<Booking, Void> actionsCol = null;
        if (isAdmin) {
            actionsCol = new TableColumn<>("Actions");
            actionsCol.setCellFactory(col -> new TableCell<>() {
                private final Button approveBtn = new Button("✓ Approve");
                private final Button rejectBtn = new Button("✗ Reject");
                private final HBox box = new HBox(6, approveBtn, rejectBtn);
                {
                    approveBtn.setStyle(
                            "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 4; -fx-cursor: hand;");
                    rejectBtn.setStyle(
                            "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 4; -fx-cursor: hand;");

                    approveBtn.setOnAction(e -> {
                        Booking b = getTableView().getItems().get(getIndex());
                        updateBookingStatus(b, "APPROVED");
                    });
                    rejectBtn.setOnAction(e -> {
                        Booking b = getTableView().getItems().get(getIndex());
                        updateBookingStatus(b, "REJECTED");
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Booking b = getTableView().getItems().get(getIndex());
                        if ("PENDING".equals(b.getStatus())) {
                            setGraphic(box);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });
            actionsCol.setPrefWidth(180);
        }

        // Add columns based on role
        bookingTable.getColumns().add(idColumn);
        if (isAdmin && studentCol != null) {
            bookingTable.getColumns().add(studentCol);
        }
        bookingTable.getColumns().add(roomColumn);
        bookingTable.getColumns().add(dateColumn);
        bookingTable.getColumns().add(durationColumn);
        bookingTable.getColumns().add(bookedOnColumn);
        bookingTable.getColumns().add(statusColumn);
        if (isAdmin && actionsCol != null) {
            bookingTable.getColumns().add(actionsCol);
        }

        loadData();
    }

    private void updateBookingStatus(Booking booking, String newStatus) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to " + newStatus.toLowerCase() + " this booking?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = BookingDAO.updateStatus(booking.getId(), newStatus);
            if (success) {
                loadData(); // Refresh the table
            }
        }
    }

    private void loadData() {
        List<Booking> list;

        if (UserSession.isAdmin()) {
            // Admin sees all bookings
            list = BookingDAO.findAll();
        } else if (UserSession.isLoggedIn() && UserSession.getCurrentStudent() != null) {
            // Student sees only their bookings
            list = BookingDAO.findByStudent(UserSession.getCurrentStudent().getId());
        } else {
            return;
        }

        bookingTable.setItems(FXCollections.observableArrayList(list));
    }
}










