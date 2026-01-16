package com.pandalodge.controller;

import com.pandalodge.dao.BookingDAO;
import com.pandalodge.dao.ReviewDAO;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.pandalodge.model.Review;
import com.pandalodge.model.Accommodation;
import com.pandalodge.model.Student;
import com.pandalodge.util.UserSession;

import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DetailController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private Button bookBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Label statusLabel;
    @FXML
    private Label typeBadge;
    @FXML
    private Label statusBadge;
    @FXML
    private Label typeLabel;
    @FXML
    private Label furnishedLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label availableInfoLabel;
    @FXML
    private StackPane mapContainer;
    @FXML
    private ImageView mapImage;
    @FXML
    private Label mapPlaceholder;
    @FXML
    private Button openMapBtn;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label durationLabel;
    @FXML
    private Label totalCostLabel;
    @FXML
    private VBox successOverlay;
    @FXML
    private Label successTitle;
    @FXML
    private Label successMessage;

    @FXML
    private Label ownerNameLabel;
    @FXML
    private Label ownerEmailLabel;
    @FXML
    private Label ownerPhoneLabel;
    @FXML
    private VBox ownerInfoBox;

    @FXML
    private VBox reviewsContainer;
    @FXML
    private Label avgRatingLabel;
    @FXML
    private Label reviewCountLabel;

    private boolean isSaved = false;

    private Accommodation accommodation;
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
    }

    @FXML
    public void initialize() {
        if (startDatePicker != null && endDatePicker != null) {
            LocalDate today = LocalDate.now();
            startDatePicker.setValue(today.plusDays(1));
            endDatePicker.setValue(today.plusMonths(6));

            startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateDuration());
            endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateDuration());

            updateDuration();
        }
    }

    private void updateDuration() {
        if (startDatePicker != null && endDatePicker != null && durationLabel != null) {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            if (start != null && end != null && end.isAfter(start)) {
                long days = ChronoUnit.DAYS.between(start, end);
                long months = ChronoUnit.MONTHS.between(start, end);

                if (months > 0) {
                    long remainingDays = days - (months * 30);
                    if (remainingDays > 7) {
                        durationLabel.setText(
                                months + " month" + (months > 1 ? "s" : "") + " +" + (remainingDays / 7) + "w");
                    } else {
                        durationLabel.setText(months + " month" + (months > 1 ? "s" : ""));
                    }
                } else {
                    durationLabel.setText(days + " day" + (days != 1 ? "s" : ""));
                }
                durationLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6366f1;");

                updateTotalCost(months, days);
            } else if (start != null && end != null && !end.isAfter(start)) {
                durationLabel.setText("Invalid dates");
                durationLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ef4444;");
                if (totalCostLabel != null) {
                    totalCostLabel.setText("€ --");
                    totalCostLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #94a3b8;");
                }
            } else {
                durationLabel.setText("-- months");
                durationLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");
                if (totalCostLabel != null) {
                    totalCostLabel.setText("€ --");
                    totalCostLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #94a3b8;");
                }
            }
        }
    }

    private void updateTotalCost(long months, long days) {
        if (totalCostLabel != null && accommodation != null) {
            double monthlyRent = accommodation.getPrice();
            double totalCost;

            if (months > 0) {
                double remainingDays = days - (months * 30);
                double dailyRate = monthlyRent / 30;
                totalCost = (months * monthlyRent) + (remainingDays * dailyRate);
            } else {
                double dailyRate = monthlyRent / 30;
                totalCost = days * dailyRate;
            }

            totalCostLabel.setText("€" + String.format("%.0f", totalCost));
            totalCostLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #22c55e; -fx-font-size: 16px;");
        }
    }

    public void setData(Accommodation accommodation) {
        this.accommodation = accommodation;

        String title = accommodation.getType();
        if (accommodation.getAddress() != null && accommodation.getAddress().contains(",")) {
            String[] parts = accommodation.getAddress().split(",");
            if (parts.length >= 2) {
                title = accommodation.getType() + " in " + parts[1].trim();
            }
        }
        titleLabel.setText(title);

        priceLabel.setText("€" + String.format("%.0f", accommodation.getPrice()));

        addressLabel
                .setText(accommodation.getAddress() != null ? accommodation.getAddress() : "Location not specified");

        descriptionLabel.setText(
                accommodation.getDescription() != null ? accommodation.getDescription() : "No description available.");

        if (typeBadge != null) {
            typeBadge.setText(accommodation.getType());
            String badgeColor = switch (accommodation.getType()) {
                case "Room" -> "#22c55e";
                case "Studio" -> "#6366f1";
                case "Apartment" -> "#f59e0b";
                default -> "#64748b";
            };
            typeBadge.setStyle("-fx-background-color: " + badgeColor
                    + "; -fx-text-fill: white; -fx-padding: 6 14; -fx-background-radius: 14; -fx-font-size: 12px; -fx-font-weight: bold;");
        }

        if (statusBadge != null) {
            String status = accommodation.getStatus() != null ? accommodation.getStatus() : "AVAILABLE";
            boolean isAvailable = "AVAILABLE".equalsIgnoreCase(status);
            statusBadge.setText(isAvailable ? "✓ Available" : "Booked");
            statusBadge.setStyle("-fx-background-color: " + (isAvailable ? "#22c55e" : "#ef4444") +
                    "; -fx-text-fill: white; -fx-padding: 6 14; -fx-background-radius: 14; -fx-font-size: 12px; -fx-font-weight: bold;");
        }

        if (typeLabel != null) {
            typeLabel.setText(accommodation.getType());
        }

        if (furnishedLabel != null) {
            furnishedLabel.setText(accommodation.isFurnished() ? "Furnished" : "Unfurnished");
        }

        if (sizeLabel != null) {
            sizeLabel.setText(accommodation.getSize());
        }

        if (availableInfoLabel != null) {
            String status = accommodation.getStatus() != null ? accommodation.getStatus() : "AVAILABLE";
            availableInfoLabel.setText("AVAILABLE".equalsIgnoreCase(status) ? "Now" : "Booked");
        }

        if (accommodation.getImageUrl() != null && !accommodation.getImageUrl().isBlank()) {
            try {
                imageView.setImage(new Image(accommodation.getImageUrl(), 520, 350, false, true, true));
            } catch (Exception e) {
            }
        }

        loadMapImage();

        if (accommodation.getStatus() != null && accommodation.getStatus().equals("BOOKED")) {
            bookBtn.setDisable(true);
            bookBtn.setText("Already Booked");
            bookBtn.setStyle("-fx-background-color: #94a3b8;");
        }

        loadOwnerInfo();

        loadReviews();
    }

    private void loadOwnerInfo() {
        if (accommodation == null)
            return;

        if (ownerNameLabel != null && accommodation.hasOwner()) {
            ownerNameLabel.setText(accommodation.getOwnerName());
        } else if (ownerNameLabel != null) {
            ownerNameLabel.setText("Property Manager");
        }

        if (ownerEmailLabel != null && accommodation.getOwnerEmail() != null) {
            ownerEmailLabel.setText(accommodation.getOwnerEmail());
        } else if (ownerEmailLabel != null) {
            ownerEmailLabel.setText("Contact via platform");
        }

        if (ownerPhoneLabel != null && accommodation.getOwnerPhone() != null) {
            ownerPhoneLabel.setText(accommodation.getOwnerPhone());
        } else if (ownerPhoneLabel != null) {
            ownerPhoneLabel.setText("Not provided");
        }

        if (ownerInfoBox != null) {
            ownerInfoBox.setVisible(true);
        }
    }

    private void loadReviews() {
        if (reviewsContainer == null || accommodation == null)
            return;

        reviewsContainer.getChildren().clear();

        List<Review> reviews = ReviewDAO.findByAccommodation(accommodation.getId());
        double avgRating = ReviewDAO.getAverageRating(accommodation.getId());

        if (avgRatingLabel != null) {
            if (avgRating > 0) {
                avgRatingLabel.setText(String.format("★ %.1f", avgRating));
                avgRatingLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 16px;");
            } else {
                avgRatingLabel.setText("No ratings yet");
                avgRatingLabel.setStyle("-fx-text-fill: #94a3b8;");
            }
        }

        if (reviewCountLabel != null) {
            reviewCountLabel.setText(reviews.size() + " review" + (reviews.size() != 1 ? "s" : ""));
        }

        if (reviews.isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to review!");
            noReviews.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-padding: 20;");
            reviewsContainer.getChildren().add(noReviews);
        } else {
            for (Review review : reviews) {
                VBox reviewBox = createReviewCard(review);
                reviewsContainer.getChildren().add(reviewBox);
            }
        }
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 10;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(review.getStudentName() != null ? review.getStudentName() : "Anonymous");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label dateLabel = new Label(review.getFormattedDate());
        dateLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        header.getChildren().addAll(nameLabel, dateLabel);

        Label starsLabel = new Label(review.getStarRating());
        starsLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 14px;");

        Label textLabel = new Label(review.getReviewData());
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-text-fill: #475569;");

        card.getChildren().addAll(header, starsLabel, textLabel);
        VBox.setMargin(card, new Insets(0, 0, 10, 0));

        return card;
    }

    private void loadMapImage() {
        if (mapImage != null && accommodation != null) {
            try {
                double lat = accommodation.getLatitude();
                double lng = accommodation.getLongitude();

                if (lat != 0 && lng != 0) {
                    int zoom = 15;
                    String mapUrl = String.format(
                            "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=%d&size=600x300&markers=color:red%%7C%f,%f&key=YOUR_API_KEY",
                            lat, lng, zoom, lat, lng);

                    mapImage.setImage(new Image(mapUrl, true));
                    if (mapPlaceholder != null) {
                        mapPlaceholder.setVisible(false);
                    }
                } else {
                    if (mapPlaceholder != null) {
                        mapPlaceholder.setText("📍 " + accommodation.getAddress());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to load map: " + e.getMessage());
                if (mapPlaceholder != null) {
                    mapPlaceholder.setText("📍 Map unavailable");
                }
            }
        }
    }

    @FXML
    public void openGoogleMaps() {
        if (accommodation != null) {
            try {
                String url;
                if (accommodation.getLatitude() != 0 && accommodation.getLongitude() != 0) {
                    url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f",
                            accommodation.getLatitude(), accommodation.getLongitude());
                } else {
                    String encoded = URLEncoder.encode(accommodation.getAddress(), StandardCharsets.UTF_8);
                    url = "https://www.google.com/maps/search/?api=1&query=" + encoded;
                }

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                }
            } catch (Exception e) {
                System.err.println("Failed to open Google Maps: " + e.getMessage());
            }
        }
    }

    @FXML
    public void navigateToBookings() {
        if (dashboardController != null) {
            dashboardController.showMyBookings();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/profile.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                javafx.stage.Stage stage = (javafx.stage.Stage) titleLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - My Bookings");
                stage.centerOnScreen();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void back() {
        if (dashboardController != null) {
            dashboardController.showAccommodations();
        } else {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                DashboardController dashboard = loader.getController();
                dashboard.showAccommodations(null, null);

                javafx.stage.Stage stage = (javafx.stage.Stage) titleLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - Accommodations");
                stage.centerOnScreen();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleSave() {
        isSaved = !isSaved;
        if (saveBtn != null) {
            if (isSaved) {
                saveBtn.setText("♥ Saved");
                saveBtn.setStyle("-fx-background-color: #fecaca; -fx-text-fill: #dc2626; -fx-border-color: #dc2626;");
            } else {
                saveBtn.setText("♥ Save");
                saveBtn.setStyle("");
            }
        }
    }

    @FXML
    public void handleBook() {

        if (!UserSession.isLoggedIn() || UserSession.isAdmin()) {
            statusLabel.setText("Please log in as a student to book.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        LocalDate startDate = startDatePicker != null ? startDatePicker.getValue() : null;
        LocalDate endDate = endDatePicker != null ? endDatePicker.getValue() : null;

        if (startDate == null || endDate == null) {
            statusLabel.setText("⚠️ Please select check-in and check-out dates.");
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            return;
        }

        if (!endDate.isAfter(startDate)) {
            statusLabel.setText("⚠️ Check-out date must be after check-in date.");
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            return;
        }

        if (startDate.isBefore(LocalDate.now())) {
            statusLabel.setText("⚠️ Check-in date cannot be in the past.");
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            return;
        }

        Student student = UserSession.getCurrentStudent();
        if (student == null) {
            statusLabel.setText("Session expired. Please log in again.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        if (accommodation == null) {
            statusLabel.setText("Accommodation data not available. Please go back and try again.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        int studentId = student.getId();
        int accommodationId = accommodation.getId();
        String startStr = startDate.toString();
        String endStr = endDate.toString();

        boolean ok = BookingDAO.create(studentId, accommodationId, startStr, endStr, "PENDING");

        if (ok) {
            long months = ChronoUnit.MONTHS.between(startDate, endDate);
            String durationText = months > 0 ? months + " month" + (months > 1 ? "s" : "")
                    : ChronoUnit.DAYS.between(startDate, endDate) + " days";

            if (successOverlay != null) {
                successMessage.setText("Your request for " + accommodation.getType() + " has been sent to the admin.");
                successOverlay.setVisible(true);
                successOverlay.setOpacity(0);

                javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(500), successOverlay);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();

                successOverlay.setScaleX(0.9);
                successOverlay.setScaleY(0.9);
                javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(500), successOverlay);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            } else {
                statusLabel.setText("🎉 Booking requested for " + durationText + "!");
                statusLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
            }

            bookBtn.setDisable(true);
            bookBtn.setText("✓ Request Sent");
            bookBtn.setStyle("-fx-background-color: #22c55e;");

            if (startDatePicker != null)
                startDatePicker.setDisable(true);
            if (endDatePicker != null)
                endDatePicker.setDisable(true);
        } else {
            statusLabel.setText("Booking failed. Please check the console or try again.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            System.err.println("CRITICAL: BookingDAO.create returned false for student=" + studentId + " accommodation="
                    + accommodationId);
        }
    }

    @FXML
    public void handleAddReview() {
        if (!UserSession.isLoggedIn()) {
            statusLabel.setText("Please log in to add a review.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Add Review");
        dialog.setHeaderText("Share your experience with " + accommodation.getType());
        dialog.setContentText("Write your review (Rating will be 5 stars by default):");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isBlank()) {
            try {
                ReviewDAO.create(UserSession.getCurrentStudent().getId(), accommodation.getId(), result.get(), 5);
                loadReviews();
                statusLabel.setText("✅ Review added successfully!");
                statusLabel.setStyle("-fx-text-fill: #22c55e;");
            } catch (Exception e) {
                statusLabel.setText("Failed to save review.");
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
                e.printStackTrace();
            }
        }
    }
}
