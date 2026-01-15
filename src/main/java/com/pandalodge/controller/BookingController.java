package com.pandalodge.controller;

import com.pandalodge.dao.BookingDAO;
import com.pandalodge.dao.AccommodationDAO;
import com.pandalodge.dao.StudentDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
    @FXML
    private Label statusLabel;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;

    }

    @FXML
    public void onBack() {

        if (dashboardController != null) {
            if (UserSession.isAdmin()) {
                dashboardController.showAdminOverview();
            } else {
                dashboardController.showAccommodations();
            }
        } else {
            // Fallback: navigate to home page
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/pandalodge/view/home.fxml"));
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

        // Status Column with color and background
        TableColumn<Booking, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(120);
        statusColumn.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();
            {
                label.setStyle(
                        "-fx-padding: 4 12; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    switch (item) {
                        case "PENDING" -> label.setStyle(label.getStyle() + " -fx-background-color: #f59e0b;");
                        case "APPROVED" -> label.setStyle(label.getStyle() + " -fx-background-color: #22c55e;");
                        case "REJECTED" -> label.setStyle(label.getStyle() + " -fx-background-color: #ef4444;");
                        default -> label.setStyle(label.getStyle() + " -fx-background-color: #64748b;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Actions Column
        TableColumn<Booking, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final Button deleteBtn = new Button("Delete");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox adminBox = new HBox(8, approveBtn, rejectBtn, deleteBtn);
            private final HBox studentBox = new HBox(8, cancelBtn);

            {
                adminBox.setAlignment(Pos.CENTER);
                studentBox.setAlignment(Pos.CENTER);

                approveBtn.getStyleClass().add("btn-small");
                approveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white;");
                rejectBtn.getStyleClass().add("btn-small-danger");
                deleteBtn.getStyleClass().add("btn-small-danger");
                deleteBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white;");

                cancelBtn.getStyleClass().add("btn-small-danger");

                approveBtn.setOnAction(e -> updateBookingStatus(getTableView().getItems().get(getIndex()), "APPROVED"));
                rejectBtn.setOnAction(e -> updateBookingStatus(getTableView().getItems().get(getIndex()), "REJECTED"));
                deleteBtn.setOnAction(e -> deleteBookingConfirm(getTableView().getItems().get(getIndex())));
                cancelBtn.setOnAction(e -> deleteBookingConfirm(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking b = getTableView().getItems().get(getIndex());
                    if (isAdmin) {
                        if ("PENDING".equals(b.getStatus())) {
                            approveBtn.setVisible(true);
                            rejectBtn.setVisible(true);
                        } else {
                            approveBtn.setVisible(false);
                            rejectBtn.setVisible(false);
                        }
                        setGraphic(adminBox);
                    } else {
                        if ("PENDING".equals(b.getStatus())) {
                            setGraphic(studentBox);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            }
        });
        actionsCol.setPrefWidth(isAdmin ? 240 : 120);

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
        bookingTable.getColumns().add(actionsCol);

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
                if (statusLabel != null) {
                    statusLabel.setText("✅ Booking request " + newStatus.toLowerCase() + " successfully.");
                    statusLabel
                            .setStyle("-fx-text-fill: " + ("APPROVED".equals(newStatus) ? "#22c55e" : "#ef4444") + ";");
                }
                // If approved, mark accommodation as BOOKED
                if ("APPROVED".equals(newStatus)) {
                    AccommodationDAO.updateStatus(booking.getAccommodationId(), "BOOKED");
                }
                // If rejected, mark accommodation as AVAILABLE (in case it was somehow marked
                // booked before)
                else if ("REJECTED".equals(newStatus)) {
                    AccommodationDAO.updateStatus(booking.getAccommodationId(), "AVAILABLE");
                }
                loadData(); // Refresh the table
            }
        }
    }

    private void deleteBookingConfirm(Booking b) {
        String msg = UserSession.isAdmin() ? "Delete this booking record permanently?" : "Cancel your booking request?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = BookingDAO.delete(b.getId());
            if (success) {
                if (statusLabel != null) {
                    statusLabel.setText(
                            "✅ Booking " + (UserSession.isAdmin() ? "deleted" : "cancelled") + " successfully.");
                    statusLabel.setStyle("-fx-text-fill: #64748b;");
                }
                loadData();
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
