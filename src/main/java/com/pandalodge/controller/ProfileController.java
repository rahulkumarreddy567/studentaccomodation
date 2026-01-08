package com.pandalodge.controller;

import com.pandalodge.dao.AccommodationDAO;
import com.pandalodge.dao.BookingDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.pandalodge.model.Accommodation;
import com.pandalodge.model.Booking;
import com.pandalodge.model.Student;
import com.pandalodge.util.UserSession;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label avatarLabel;
    @FXML
    private Label memberSinceLabel;
    @FXML
    private Label totalBookingsLabel;
    @FXML
    private Label activeBookingsLabel;
    @FXML
    private Label completedBookingsLabel;
    @FXML
    private Label bookingCountLabel;
    @FXML
    private VBox bookingsContainer;
    @FXML
    private VBox emptyState;
    @FXML
    private Button homeBtn;

    @FXML
    public void initialize() {
        loadProfile();
        loadBookings();
    }

    private void loadProfile() {
        Student student = UserSession.getCurrentStudent();
        if (student != null) {
            nameLabel.setText(student.getName());
            emailLabel.setText(student.getEmail());

            // Generate avatar from first letter of name
            String initial = student.getName().substring(0, 1).toUpperCase();
            avatarLabel.setText(initial);

            memberSinceLabel.setText("📅 Member since 2026");
        }
    }

    private void loadBookings() {
        Student student = UserSession.getCurrentStudent();
        if (student == null)
            return;

        List<Booking> bookings = BookingDAO.findByStudent(student.getId());

        // Update stats
        int total = bookings.size();
        int active = (int) bookings.stream()
                .filter(b -> "PENDING".equalsIgnoreCase(b.getStatus()) || "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .count();
        int completed = (int) bookings.stream().filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus())).count();

        totalBookingsLabel.setText(String.valueOf(total));
        activeBookingsLabel.setText(String.valueOf(active));
        completedBookingsLabel.setText(String.valueOf(completed));
        bookingCountLabel.setText(total + " booking" + (total != 1 ? "s" : ""));

        bookingsContainer.getChildren().clear();

        if (bookings.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);

            for (Booking booking : bookings) {
                VBox card = createBookingCard(booking);
                bookingsContainer.getChildren().add(card);
            }
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(12);
        card.getStyleClass().add("booking-card");
        card.setPadding(new Insets(16));

        // Find accommodation details
        Accommodation accommodation = AccommodationDAO.findById(booking.getAccommodationId());

        // Header with status
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🏠");
        icon.setStyle("-fx-font-size: 24px;");

        VBox titleBox = new VBox(4);
        Label title = new Label(
                accommodation != null ? accommodation.getType() + " in " + getShortAddress(accommodation.getAddress())
                        : "Accommodation #" + booking.getAccommodationId());
        title.getStyleClass().add("booking-title");

        Label address = new Label(accommodation != null ? accommodation.getAddress() : "Unknown location");
        address.getStyleClass().add("booking-address");

        titleBox.getChildren().addAll(title, address);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        // Status badge
        Label statusBadge = createStatusBadge(booking.getStatus());

        header.getChildren().addAll(icon, titleBox, statusBadge);

        // Details row
        HBox details = new HBox(20);
        details.setAlignment(Pos.CENTER_LEFT);
        details.getStyleClass().add("booking-details");

        VBox dateBox = new VBox(2);
        Label dateLabel = new Label("📅 Check-in/out");
        dateLabel.getStyleClass().add("detail-label");
        String formattedDates = formatBookingDates(booking.getStartDate(), booking.getEndDate());
        Label dateValue = new Label(formattedDates);
        dateValue.getStyleClass().add("detail-value");
        dateBox.getChildren().addAll(dateLabel, dateValue);

        VBox durationBox = new VBox(2);
        Label durationLabel = new Label("⏱️ Duration");
        durationLabel.getStyleClass().add("detail-label");
        Label durationValue = new Label(booking.getDurationFormatted());
        durationValue.getStyleClass().add("detail-value-highlight");
        durationBox.getChildren().addAll(durationLabel, durationValue);

        VBox priceBox = new VBox(2);
        Label priceLabel = new Label("💰 Price");
        priceLabel.getStyleClass().add("detail-label");
        Label priceValue = new Label(
                accommodation != null ? "€" + String.format("%.0f", accommodation.getPrice()) + "/month" : "N/A");
        priceValue.getStyleClass().add("detail-value");
        priceBox.getChildren().addAll(priceLabel, priceValue);

        VBox bookedOnBox = new VBox(2);
        Label bookedOnLabel = new Label("🕐 Booked on");
        bookedOnLabel.getStyleClass().add("detail-label");
        Label bookedOnValue = new Label(booking.getCreatedAtFormatted());
        bookedOnValue.getStyleClass().add("detail-value-muted");
        bookedOnBox.getChildren().addAll(bookedOnLabel, bookedOnValue);

        VBox idBox = new VBox(2);
        Label idLabel = new Label("🔖 ID");
        idLabel.getStyleClass().add("detail-label");
        Label idValue = new Label("#" + booking.getId());
        idValue.getStyleClass().add("detail-value");
        idBox.getChildren().addAll(idLabel, idValue);

        details.getChildren().addAll(dateBox, durationBox, priceBox, bookedOnBox, idBox);

        // Footer with actions
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);

        if ("PENDING".equalsIgnoreCase(booking.getStatus())) {
            Button cancelBtn = new Button("Cancel Booking");
            cancelBtn.getStyleClass().add("btn-small-danger");
            cancelBtn.setOnAction(e -> {
                BookingDAO.updateStatus(booking.getId(), "CANCELLED");
                loadBookings(); // Refresh
            });
            footer.getChildren().add(cancelBtn);
        }

        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("btn-small");
        viewBtn.setOnAction(e -> viewRoomDetails(accommodation));
        footer.getChildren().add(viewBtn);

        card.getChildren().addAll(header, details, footer);
        return card;
    }

    private String getShortAddress(String address) {
        if (address == null)
            return "Unknown";
        String[] parts = address.split(",");
        return parts.length > 1 ? parts[1].trim() : parts[0].trim();
    }

    private Label createStatusBadge(String status) {
        Label badge = new Label();
        badge.getStyleClass().add("status-badge");

        switch (status.toUpperCase()) {
            case "PENDING":
                badge.setText("⏳ Pending");
                badge.getStyleClass().add("status-pending");
                break;
            case "CONFIRMED":
                badge.setText("✅ Confirmed");
                badge.getStyleClass().add("status-confirmed");
                break;
            case "CANCELLED":
                badge.setText("❌ Cancelled");
                badge.getStyleClass().add("status-cancelled");
                break;
            case "COMPLETED":
                badge.setText("🎉 Completed");
                badge.getStyleClass().add("status-completed");
                break;
            default:
                badge.setText(status);
        }
        return badge;
    }

    private void viewRoomDetails(Accommodation accommodation) {
        if (accommodation == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/details.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            DetailController controller = loader.getController();
            controller.setData(accommodation);

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - " + accommodation.getType());
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onHome() {
        navigateTo("/com/pandalodge/view/home.fxml", "Home");
    }

    @FXML
    public void onLogout() {
        UserSession.logout();
        navigateTo("/com/pandalodge/view/login.fxml", "Login");
    }

    @FXML
    public void onBrowse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            DashboardController dashboard = loader.getController();
            dashboard.showAccommodations(null, null);

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Browse Accommodations");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onViewListings() {
        onBrowse();
    }

    private String formatBookingDates(String startDate, String endDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy");
            return start.format(fmt) + " → " + end.format(fmt);
        } catch (Exception e) {
            return startDate + " → " + endDate;
        }
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - " + title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}










